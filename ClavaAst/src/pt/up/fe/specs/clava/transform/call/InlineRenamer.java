/**
 * Copyright 2017 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package pt.up.fe.specs.clava.transform.call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import pt.up.fe.specs.clava.ClavaNode;
import pt.up.fe.specs.clava.Types;
import pt.up.fe.specs.clava.ast.decl.FunctionDecl;
import pt.up.fe.specs.clava.ast.decl.ParmVarDecl;
import pt.up.fe.specs.clava.ast.decl.VarDecl;
import pt.up.fe.specs.clava.ast.decl.enums.InitializationStyle;
import pt.up.fe.specs.clava.ast.expr.ArraySubscriptExpr;
import pt.up.fe.specs.clava.ast.expr.CallExpr;
import pt.up.fe.specs.clava.ast.expr.DeclRefExpr;
import pt.up.fe.specs.clava.ast.expr.Expr;
import pt.up.fe.specs.clava.ast.stmt.DeclStmt;
import pt.up.fe.specs.clava.ast.stmt.ReturnStmt;
import pt.up.fe.specs.clava.ast.stmt.Stmt;
import pt.up.fe.specs.clava.ast.type.ArrayType;
import pt.up.fe.specs.clava.ast.type.ConstantArrayType;
import pt.up.fe.specs.clava.ast.type.Type;
import pt.up.fe.specs.clava.context.ClavaFactory;
import pt.up.fe.specs.clava.utils.Typable;
import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.classmap.BiConsumerClassMap;
import pt.up.fe.specs.util.collections.AccumulatorMap;
import pt.up.fe.specs.util.treenode.NodeInsertUtils;

public class InlineRenamer {

    private final CallExpr call;
    private final FunctionDecl functionDecl;
    private final List<Stmt> stmts;
    private final Set<String> usedNames;

    private final BiConsumerClassMap<Expr, ParmVarDecl> argumentsRenamers;

    // Statements that need to be called before the inlined statements
    private final List<Stmt> prefixStmts;

    private final Map<String, Consumer<ClavaNode>> renameActions;
    private final Set<String> newNames;
    private Expr callReplacement;

    private AccumulatorMap<String> newNamesCounter;
    private ClavaFactory factory;

    public InlineRenamer(CallExpr call, FunctionDecl functionDecl, List<Stmt> stmts, Set<String> usedNames) {
        this.call = call;
        this.functionDecl = functionDecl;
        this.stmts = stmts;
        this.usedNames = usedNames;

        this.argumentsRenamers = buildArgumentsRenamers();

        this.prefixStmts = new ArrayList<>();
        this.renameActions = new HashMap<>();
        this.newNames = new HashSet<>();
        this.callReplacement = null;

        this.newNamesCounter = new AccumulatorMap<>();

        // Store reference to factory
        this.factory = call.getFactory();
    }

    private BiConsumerClassMap<Expr, ParmVarDecl> buildArgumentsRenamers() {
        BiConsumerClassMap<Expr, ParmVarDecl> argumentsRenamers = new BiConsumerClassMap<>();

        argumentsRenamers.put(DeclRefExpr.class, this::argumentRename);
        argumentsRenamers.put(ArraySubscriptExpr.class, this::argumentRename);
        argumentsRenamers.put(Expr.class, this::argumentRenameGeneric);

        return argumentsRenamers;
    }

    public List<Stmt> getPrefixStmts() {
        return prefixStmts;
    }

    public List<Stmt> apply() {
        String calleeName = call.getCalleeName();

        List<ParmVarDecl> parameters = functionDecl.getParameters();
        List<Expr> arguments = call.getArgs();

        // Map declaration names
        for (int i = 0; i < parameters.size(); i++) {

            // If no more arguments, just prefix the name of the call
            if (arguments.size() < i) {
                renameParamWithoutArg(calleeName, parameters.get(i));
                continue;
            }

            argumentRename(arguments.get(i), parameters.get(i));
        }

        // Add VarDecls to rename map
        stmts.stream().flatMap(node -> node.getDescendantsAndSelfStream())
                .filter(VarDecl.class::isInstance)
                .map(VarDecl.class::cast)
                .forEach(varDecl -> renameVarDecl(calleeName, varDecl));

        // Apply renames
        return applyRenameActions();

    }

    private String getVarName(ClavaNode node) {
        if (node instanceof VarDecl) {
            return ((VarDecl) node).getDeclName();
        }

        if (node instanceof DeclRefExpr) {
            return ((DeclRefExpr) node).getRefName();
        }

        throw new RuntimeException("Not implemented: " + node.getNodeName());
    }

    private void applyRenameAction(ClavaNode node) {
        String varName = getVarName(node);
        Consumer<ClavaNode> action = renameActions.get(varName);
        if (action == null) {
            return;
        }

        action.accept(node);
    }

    private List<Stmt> applyRenameActions() {
        stmts.stream().flatMap(node -> node.getDescendantsAndSelfStream())
                .filter(node -> node instanceof VarDecl || node instanceof DeclRefExpr)
                .forEach(this::applyRenameAction);

        TypeRenamerFilter typeRenamerFilter = new TypeRenamerFilter();

        // AccumulatorMap<String> acc = new AccumulatorMap<>();
        // Also rename DeclRefExpr found in Types
        // Also types might need renaming
        for (Stmt stmt : stmts) {

            for (ClavaNode node : stmt.getDescendantsAndSelf(ClavaNode.class)) {

                if (!(node instanceof Typable)) {
                    continue;
                }

                Typable typable = (Typable) node;

                Type candidateType = typable.getType().copy();

                List<ClavaNode> nodesToRename = typeRenamerFilter.nodesToRename(candidateType);

                if (nodesToRename.isEmpty()) {
                    continue;
                }

                // Set type copy
                typable.setType(candidateType);

                // Apply rename actions
                for (ClavaNode toRename : nodesToRename) {
                    applyRenameAction(toRename);
                }
            }
        }

        replaceReturn();

        // Prefix new statements
        return SpecsCollections.concat(prefixStmts, stmts);
    }

    private void replaceReturn() {

        // If has a return statement, create temporary name a replace return with assigment to this variable
        Optional<ReturnStmt> returnStmtTry = SpecsCollections.reverseStream(stmts)
                .filter(ReturnStmt.class::isInstance)
                .map(ReturnStmt.class::cast)
                .findFirst();

        if (!returnStmtTry.isPresent()) {
            return;
        }

        ReturnStmt returnStmt = returnStmtTry.get();

        // Remove return from list of statements
        int returnIndex = SpecsCollections.reverseIndexStream(stmts)
                .filter(i -> stmts.get(i) == returnStmt)
                .findFirst()
                .getAsInt();

        Optional<Expr> retValueTry = returnStmt.getRetValue();

        // If no return value, just remove return
        if (!retValueTry.isPresent()) {
            stmts.remove(returnIndex);
            return;
        }

        Expr retValue = retValueTry.get();

        // Create new name
        String returnVarName = getSimpleName(call.getCalleeName(), "return");

        // Create declaration for this new name
        VarDecl varDecl = factory.varDecl(returnVarName, retValue);

        // Replace return with an DeclStmt to the return expression
        DeclStmt declStmt = factory.declStmt(varDecl);
        stmts.set(returnIndex, declStmt);

        // Save new name expression in callReplacement
        callReplacement = factory.declRefExpr(returnVarName, retValue.getType());
    }

    public Optional<Expr> getCallReplacement() {
        return Optional.ofNullable(callReplacement);
    }

    private void argumentRename(Expr expr, ParmVarDecl parmVarDecl) {
        argumentsRenamers.accept(expr, parmVarDecl);
    }

    /**
     * Reference to a variable, rename to the name of the original argument, if different.
     * 
     * @param expr
     * @param varDecl
     */
    private void argumentRename(DeclRefExpr expr, ParmVarDecl varDecl) {

        // If not a pointer or an array, apply generic rename (adds copy before inline)
        Type exprType = expr.getType();

        if (!exprType.isArray() && !exprType.isPointer()) {
            argumentRenameGeneric(expr, varDecl);
            return;
        }

        // Rename parameter to the same name as the argument
        String paramName = varDecl.getDeclName();
        String argName = expr.getRefName();

        // If the name is the same, do nothing
        if (paramName.equals(argName)) {
            return;
        }

        // Rename parameter name to be the same as the argument name
        renameActions.put(paramName, node -> simpleRename(node, argName));
    }

    private void argumentRename(ArraySubscriptExpr expr, ParmVarDecl parmVarDecl) {

        ArrayType arrayType = Types.getElement(parmVarDecl.getType(), ArrayType.class)
                .orElseThrow(() -> new RuntimeException(
                        "Expected ArraySubscriptExpr to have an array type: " + parmVarDecl.getType()));

        // Special case: ConstantArrayType
        if (arrayType instanceof ConstantArrayType) {

            // Parameter name becomes argument name
            // E.g., c[0][0] with argument k[1] becomes k[1][0][0]
            renameActions.put(parmVarDecl.getDeclName(), node -> simpleReplace(node, expr));

            return;
        }

        // Get new name
        String newName = getSimpleName(call.getCalleeName(), parmVarDecl.getDeclName());

        // Add renaming
        addRenameAction(parmVarDecl.getDeclName(), newName);

        // Get arity of parameter
        Type paramType = parmVarDecl.getType();

        int pointerArity = Types.getPointerArity(paramType);
        Type elementType = Types.getElement(paramType);

        Type newType = elementType;
        for (int i = 0; i < pointerArity; i++) {
            newType = factory.pointerType(newType);
        }

        Expr exprWithCast = factory.cStyleCastExpr(newType, expr);

        VarDecl varDecl = parmVarDecl.getFactoryWithNode().varDecl(newName, newType);
        varDecl.set(VarDecl.INIT_STYLE, InitializationStyle.CINIT);
        varDecl.setInit(exprWithCast);

        DeclStmt declStmt = factory.declStmt(varDecl);
        prefixStmts.add(declStmt);
    }

    private void argumentRenameGeneric(Expr expr, VarDecl varDecl) {

        // Get new name
        String newName = getSimpleName(call.getCalleeName(), varDecl.getDeclName());

        // Add renaming
        addRenameAction(varDecl.getDeclName(), newName);

        // Sanitize Vardecl type (e.g., transform arrays to pointers)
        Type varDeclType = expr.getType().copy();

        VarDecl newVarDecl = varDecl.getFactoryWithNode().varDecl(newName, varDeclType);
        newVarDecl.set(VarDecl.INIT_STYLE, InitializationStyle.CINIT);
        newVarDecl.setInit(expr);

        DeclStmt declStmt = factory.declStmt(newVarDecl);
        prefixStmts.add(declStmt);
    }

    /**
     * Renames a parameter that has no corresponding argument.
     * 
     * @param parmVarDecl
     * @param calleeName
     */
    private void renameParamWithoutArg(String calleeName, ParmVarDecl parmVarDecl) {

        // Get new name
        String newName = getSimpleName(calleeName, parmVarDecl.getDeclName());

        // Add renaming
        addRenameAction(parmVarDecl.getDeclName(), newName);

        // Add statement with declaration of variable
        VarDecl varDecl = parmVarDecl.getFactoryWithNode().varDecl(newName, parmVarDecl.getType());
        varDecl.setInit(parmVarDecl.getInit().orElse(null));

        DeclStmt declStmt = factory.declStmt(varDecl);
        prefixStmts.add(declStmt);
    }

    private void renameVarDecl(String calleeName, VarDecl varDecl) {

        // If already renamed (variable shadowing), ignore
        if (renameActions.containsKey(varDecl.getDeclName())) {
            return;
        }

        // Get new name
        String newName = getSimpleName(calleeName, varDecl.getDeclName());

        // Add renaming
        addRenameAction(varDecl.getDeclName(), newName);
    }

    private void addRenameAction(String oldName, String newName) {
        Consumer<ClavaNode> previousAction = renameActions.put(oldName, node -> simpleRename(node, newName));
        if (previousAction != null) {
            throw new RuntimeException("Two variables with the same name inside the function (" + oldName
                    + "), are they in different scopes? Consider checking if name is already mapped and skip in that case.");
        }
        usedNames.add(newName);
    }

    private void simpleRename(ClavaNode node, String newName) {
        if (node instanceof VarDecl) {
            ((VarDecl) node).setDeclName(newName);
            return;
        }

        if (node instanceof DeclRefExpr) {
            ((DeclRefExpr) node).setRefName(newName);
            return;
        }

        throw new RuntimeException("Not implemented: " + node.getNodeName());
    }

    private void simpleReplace(ClavaNode node, Expr newExpr) {
        NodeInsertUtils.replace(node, newExpr.copy());
    }

    private String getSimpleName(String calleeName, String declName) {
        String newName = calleeName + "_" + declName;

        // If new name is valid, just return
        if (isNewNameValid(newName)) {
            return newName;
        }

        // Try creating a new name
        int id = newNamesCounter.getCount(newName);
        String newNameBuilder = newName + "_" + id;

        while (!isNewNameValid(newNameBuilder)) {
            id = newNamesCounter.add(newName);
            newNameBuilder = newName + "_" + id;
        }

        return newNameBuilder;
    }

    private boolean isNewNameValid(String newName) {
        if (newNames.contains(newName)) {
            return false;
        }

        if (usedNames.contains(newName)) {
            return false;
        }

        return true;
    }
}
