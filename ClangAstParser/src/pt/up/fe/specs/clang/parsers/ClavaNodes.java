/**
 * Copyright 2018 SPeCS.
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

package pt.up.fe.specs.clang.parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.suikasoft.jOptions.DataStore.ADataClass;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.clava.ClavaNode;
import pt.up.fe.specs.clava.ClavaNodeInfo;
import pt.up.fe.specs.clava.ast.ClavaNodeFactory;
import pt.up.fe.specs.clava.ast.attr.Attribute;
import pt.up.fe.specs.clava.ast.decl.Decl;
import pt.up.fe.specs.clava.ast.expr.Expr;
import pt.up.fe.specs.clava.ast.type.Type;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.exceptions.CaseNotDefinedException;

public class ClavaNodes {

    // interface TriConsumer<A, B, C> {
    // void accept(A a, B b, C c);
    // }

    // private static final String NULLPRT = "nullptr";
    private static final String NULLPRT_DECL = "nullptr_decl";
    private static final String NULLPRT_STMT = "nullptr_stmt";
    private static final String NULLPRT_EXPR = "nullptr_expr";
    private static final String NULLPRT_TYPE = "nullptr_type";
    private static final String NULLPRT_ATTR = "nullptr_attr";

    private static final Set<String> NULL_IDS = new HashSet<>(
            Arrays.asList(NULLPRT_DECL, NULLPRT_STMT, NULLPRT_EXPR, NULLPRT_TYPE, NULLPRT_ATTR));

    private final DataStore data;
    private final Map<String, ClavaNode> clavaNodes;
    private final List<Runnable> delayedNodesToAdd;

    public ClavaNodes(DataStore data) {
        this.data = data;
        this.clavaNodes = new HashMap<>();
        this.delayedNodesToAdd = new ArrayList<>();
    }

    public Map<String, ClavaNode> getNodes() {
        return clavaNodes;
    }

    public List<Runnable> getDelayedNodesToAdd() {
        return delayedNodesToAdd;
    }

    public ClavaNode get(String nodeId) {
        ClavaNode clavaNode = clavaNodes.get(nodeId);

        Preconditions.checkNotNull(clavaNode, "Could not find ClavaNode with id '" + nodeId
                + "'. Check if node is being visited, or if there is a cycle in the tree.");

        return clavaNode;

    }

    // TODO: Make all static methods, instance methods

    private static ClavaNode getNode(DataStore data, String id) {
        return data.get(ClangParserKeys.CLAVA_NODES).get(id);
        /*
        ClavaNode clavaNode = data.get(ClangParserKeys.CLAVA_NODES).get(id);
        
        Preconditions.checkNotNull(clavaNode, "Could not find ClavaNode with id '" + id
                + "'. Check if node is being visited, or if there is a cycle in the tree.");
        
        return clavaNode;
        */
        // ClavaNode node = dataStore.get(ClangParserKeys.CLAVA_NODES).get(nodeId);
        // Preconditions.checkNotNull(node, "Could not find ClavaNode with id '" + nodeId + "'");
        // return node;
    }

    // public static ClavaNode getNode(DataStore data, String id) {
    // ClavaNode clavaNode = data.get(ClangParserKeys.CLAVA_NODES).get(id);
    // SpecsCheck.checkNotNull(clavaNode, () -> "No ClavaNode found for id '" + id + "'");
    //
    // return clavaNode;
    // }

    public static Type getType(DataStore data, String parsedTypeId) {
        // if (NULLPRT.equals(parsedTypeId)) {
        if (NULLPRT_TYPE.equals(parsedTypeId)) {
            return ClavaNodeFactory.nullType(ClavaNodeInfo.undefinedInfo());
        }

        ClavaNode node = getNode(data, parsedTypeId);

        SpecsCheck.checkArgument(node instanceof Type,
                () -> "Expected id '" + parsedTypeId + "' to be a Type, is a " + node.getClass().getSimpleName());

        return (Type) node;
    }

    public static Attribute getAttr(DataStore data, String parsedAttrId) {
        Preconditions.checkArgument(!NULLPRT_ATTR.equals(parsedAttrId), "Did not expect 'nullptr'");

        ClavaNode node = getNode(data, parsedAttrId);

        SpecsCheck.checkArgument(node instanceof Attribute,
                () -> "Expected id '" + parsedAttrId + "' to be an Attribute, is a " + node.getClass().getSimpleName());
        return (Attribute) node;
    }

    public static Expr getExpr(DataStore data, String parsedExprId) {
        if (NULLPRT_EXPR.equals(parsedExprId)) {
            return ClavaNodeFactory.nullExpr();
        }

        ClavaNode node = getNode(data, parsedExprId);

        SpecsCheck.checkArgument(node instanceof Expr,
                () -> "Expected id '" + parsedExprId + "' to be an Expr, is a " + node.getClass().getSimpleName());

        return (Expr) node;
    }

    public static boolean isNullId(String nullId) {
        return NULL_IDS.contains(nullId);
    }

    public static ClavaNode nullNode(String nullId) {
        switch (nullId) {
        case NULLPRT_DECL:
            return ClavaNodeFactory.nullDecl(ClavaNodeInfo.undefinedInfo());
        case NULLPRT_STMT:
            return ClavaNodeFactory.nullStmt(ClavaNodeInfo.undefinedInfo());
        case NULLPRT_EXPR:
            return ClavaNodeFactory.nullExpr();
        case NULLPRT_TYPE:
            return ClavaNodeFactory.nullType(ClavaNodeInfo.undefinedInfo());
        default:
            throw new CaseNotDefinedException(nullId);
        }
    }

    public static Decl getDecl(DataStore data, String parsedDeclId) {
        if (NULLPRT_DECL.equals(parsedDeclId)) {
            return ClavaNodeFactory.nullDecl(ClavaNodeInfo.undefinedInfo());
        }

        ClavaNode node = getNode(data, parsedDeclId);

        SpecsCheck.checkArgument(node instanceof Decl,
                () -> "Expected id '" + parsedDeclId + "' to be a Decl, is a " + node.getClass().getSimpleName());

        return (Decl) node;
    }

    public void addNodeAtClosing(ADataClass<?> dataClass, DataKey<? extends ClavaNode> key, String nodeIdToAdd) {

        @SuppressWarnings("unchecked") // Check is being done manually
        Runnable nodeToAdd = () -> {
            ClavaNode clavaNode = get(nodeIdToAdd);

            // Check if node is compatible with key
            Preconditions.checkArgument(key.getValueClass().isInstance(clavaNode), "Value of type '"
                    + clavaNode.getClass() + "' not compatible with key accepts values of type '" + key.getValueClass()
                    + "'");

            dataClass.set((DataKey<ClavaNode>) key, clavaNode);
        };

        delayedNodesToAdd.add(nodeToAdd);
    }

    /*
    public static ValueDecl getValueDecl(DataStore data, String parsedDeclId) {
        Decl decl = getDecl(data, parsedDeclId);
    
        if (decl instanceof ValueDecl) {
            return (ValueDecl) decl;
        }
    
        // Create dummy
        return decl.getFactoryWithNode().dummyValueDecl("dummy value decl",
                decl.getFactory().dummyType("type of dummy value decl"));
    }
    */

}
