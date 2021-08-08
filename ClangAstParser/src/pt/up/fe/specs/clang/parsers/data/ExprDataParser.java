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

package pt.up.fe.specs.clang.parsers.data;

import java.math.BigInteger;

import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.streamparser.LineStreamParsers;

import pt.up.fe.specs.clang.codeparser.ClangParserData;
import pt.up.fe.specs.clang.parsers.NodeDataParser;
import pt.up.fe.specs.clava.ast.expr.BinaryOperator;
import pt.up.fe.specs.clava.ast.expr.CXXBoolLiteralExpr;
import pt.up.fe.specs.clava.ast.expr.CXXConstructExpr;
import pt.up.fe.specs.clava.ast.expr.CXXDeleteExpr;
import pt.up.fe.specs.clava.ast.expr.CXXDependentScopeMemberExpr;
import pt.up.fe.specs.clava.ast.expr.CXXMemberCallExpr;
import pt.up.fe.specs.clava.ast.expr.CXXNamedCastExpr;
import pt.up.fe.specs.clava.ast.expr.CXXNewExpr;
import pt.up.fe.specs.clava.ast.expr.CXXNoexceptExpr;
import pt.up.fe.specs.clava.ast.expr.CXXPseudoDestructorExpr;
import pt.up.fe.specs.clava.ast.expr.CXXTypeidExpr;
import pt.up.fe.specs.clava.ast.expr.CallExpr;
import pt.up.fe.specs.clava.ast.expr.CastExpr;
import pt.up.fe.specs.clava.ast.expr.CharacterLiteral;
import pt.up.fe.specs.clava.ast.expr.CompoundLiteralExpr;
import pt.up.fe.specs.clava.ast.expr.DeclRefExpr;
import pt.up.fe.specs.clava.ast.expr.DependentScopeDeclRefExpr;
import pt.up.fe.specs.clava.ast.expr.DesignatedInitExpr;
import pt.up.fe.specs.clava.ast.expr.ExplicitCastExpr;
import pt.up.fe.specs.clava.ast.expr.Expr;
import pt.up.fe.specs.clava.ast.expr.FloatingLiteral;
import pt.up.fe.specs.clava.ast.expr.InitListExpr;
import pt.up.fe.specs.clava.ast.expr.IntegerLiteral;
import pt.up.fe.specs.clava.ast.expr.LambdaExpr;
import pt.up.fe.specs.clava.ast.expr.Literal;
import pt.up.fe.specs.clava.ast.expr.MSPropertyRefExpr;
import pt.up.fe.specs.clava.ast.expr.MaterializeTemporaryExpr;
import pt.up.fe.specs.clava.ast.expr.MemberExpr;
import pt.up.fe.specs.clava.ast.expr.OffsetOfExpr;
import pt.up.fe.specs.clava.ast.expr.OverloadExpr;
import pt.up.fe.specs.clava.ast.expr.PredefinedExpr;
import pt.up.fe.specs.clava.ast.expr.PseudoObjectExpr;
import pt.up.fe.specs.clava.ast.expr.SizeOfPackExpr;
import pt.up.fe.specs.clava.ast.expr.StringLiteral;
import pt.up.fe.specs.clava.ast.expr.UnaryExprOrTypeTraitExpr;
import pt.up.fe.specs.clava.ast.expr.UnaryOperator;
import pt.up.fe.specs.clava.ast.expr.UnresolvedLookupExpr;
import pt.up.fe.specs.clava.ast.expr.enums.BinaryOperatorKind;
import pt.up.fe.specs.clava.ast.expr.enums.CharacterKind;
import pt.up.fe.specs.clava.ast.expr.enums.ConstructionKind;
import pt.up.fe.specs.clava.ast.expr.enums.LambdaCaptureDefault;
import pt.up.fe.specs.clava.ast.expr.enums.LambdaCaptureKind;
import pt.up.fe.specs.clava.ast.expr.enums.NewInitStyle;
import pt.up.fe.specs.clava.ast.expr.enums.ObjectKind;
import pt.up.fe.specs.clava.ast.expr.enums.PredefinedIdType;
import pt.up.fe.specs.clava.ast.expr.enums.StringKind;
import pt.up.fe.specs.clava.ast.expr.enums.UnaryOperatorKind;
import pt.up.fe.specs.clava.ast.expr.enums.UnaryOperatorPosition;
import pt.up.fe.specs.clava.ast.expr.enums.ValueKind;
import pt.up.fe.specs.clava.language.AccessSpecifier;
import pt.up.fe.specs.clava.language.CastKind;
import pt.up.fe.specs.clava.language.UnaryExprOrTypeTrait;
import pt.up.fe.specs.util.SpecsBits;
import pt.up.fe.specs.util.utilities.LineStream;

/**
 * ClavaData parsers for Expr nodes.
 * 
 * 
 * @author JoaoBispo
 *
 */
public class ExprDataParser {

    public static DataStore parseExprData(LineStream lines, ClangParserData dataStore) {

        DataStore data = NodeDataParser.parseNodeData(lines, dataStore);
        // TODO: ClavaNodes.getType, should be in ClavaContext?
        // data.add(Expr.TYPE, dataStore.getClavaNodes().getType(lines.nextLine()));
        // dataStore.getClavaNodes().queueSetNode(data, Expr.TYPE, lines.nextLine());
        dataStore.getClavaNodes().queueSetOptionalNode(data, Expr.TYPE, lines.nextLine());

        data.add(Expr.VALUE_KIND, LineStreamParsers.enumFromInt(ValueKind.getEnumHelper(), lines));
        data.add(Expr.OBJECT_KIND, LineStreamParsers.enumFromInt(ObjectKind.getEnumHelper(), lines));
        data.add(Expr.IS_DEFAULT_ARGUMENT, LineStreamParsers.oneOrZero(lines));

        return data;
    }

    public static DataStore parseCastExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(CastExpr.CAST_KIND, LineStreamParsers.enumFromName(CastKind.getHelper(), lines));

        return data;
    }

    public static DataStore parseLiteralData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(Literal.SOURCE_LITERAL, ClavaDataParsers.literalSource(lines));

        return data;
    }

    public static DataStore parseCharacterLiteralData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseLiteralData(lines, dataStore);

        data.add(CharacterLiteral.VALUE, LineStreamParsers.longInt(lines));
        data.add(CharacterLiteral.KIND, LineStreamParsers.enumFromInt(CharacterKind.getEnumHelper(), lines));

        return data;
    }

    public static DataStore parseIntegerLiteralData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseLiteralData(lines, dataStore);

        data.add(IntegerLiteral.VALUE, new BigInteger(lines.nextLine()));

        return data;
    }

    public static DataStore parseFloatingLiteralData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseLiteralData(lines, dataStore);

        data.add(FloatingLiteral.VALUE, Double.parseDouble(lines.nextLine()));

        return data;
    }

    public static DataStore parseStringLiteralData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseLiteralData(lines, dataStore);

        data.add(StringLiteral.STRING_KIND, LineStreamParsers.enumFromName(StringKind.class, lines));
        data.add(StringLiteral.LENGTH, LineStreamParsers.longInt(lines));
        data.add(StringLiteral.CHAR_BYTE_WIDTH, LineStreamParsers.integer(lines));

        // Bytes in Java are signed
        data.add(StringLiteral.STRING_BYTES,
                LineStreamParsers.list(lines, lineStream -> SpecsBits.decodeUnsignedByte(lineStream.nextLine())));
        // data.add(StringLiteral.STRING, ClavaDataParsers.literalSource(lines));

        return data;
    }

    public static DataStore parseCXXBoolLiteralExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseLiteralData(lines, dataStore);

        data.add(CXXBoolLiteralExpr.VALUE, LineStreamParsers.oneOrZero(lines.nextLine()));

        return data;
    }

    public static DataStore parseCompoundLiteralExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseLiteralData(lines, dataStore);

        data.add(CompoundLiteralExpr.IS_FILE_SCOPE, LineStreamParsers.oneOrZero(lines.nextLine()));

        return data;
    }

    public static DataStore parseInitListExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        // data.add(InitListExpr.ARRAY_FILLER, dataStore.getClavaNodes().getExpr(lines.nextLine()));
        dataStore.getClavaNodes().queueSetOptionalNode(data, InitListExpr.ARRAY_FILLER, lines.nextLine());
        // data.add(InitListExpr.INITIALIZED_FIELD_IN_UNION, (FieldDecl) ClavaNodes.getDecl(dataStore,
        // lines.nextLine()));
        data.add(InitListExpr.IS_EXPLICIT, LineStreamParsers.oneOrZero(lines));
        data.add(InitListExpr.IS_STRING_LITERAL_INIT, LineStreamParsers.oneOrZero(lines));
        String syntaticForm = lines.nextLine();
        // System.out.println("PARSING - ID: " + data.get(ClavaNode.ID));
        // System.out.println("PARSING - Syntatic form: " + syntaticForm);

        dataStore.getClavaNodes().queueSetOptionalNode(data, InitListExpr.SYNTACTIC_FORM, syntaticForm);
        String semanticForm = lines.nextLine();
        // System.out.println("PARSING - Semantic form: " + semanticForm);
        dataStore.getClavaNodes().queueSetOptionalNode(data, InitListExpr.SEMANTIC_FORM, semanticForm);

        return data;
    }

    public static DataStore parseDeclRefExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(DeclRefExpr.QUALIFIER, lines.nextLine());

        data.add(DeclRefExpr.TEMPLATE_ARGUMENTS, ClavaDataParsers.templateArguments(lines, dataStore));
        // data.add(DeclRefExpr.DECL_NAME, lines.nextLine());
        // data.add(DeclRefExpr.DECL_ID, lines.nextLine());
        // data.add(DeclRefExpr.DECL, ClavaNodes.getValueDecl(dataStore, data.get(DeclRefExpr.DECL_ID)));
        dataStore.getClavaNodes().queueSetNode(data, DeclRefExpr.DECL, lines.nextLine());

        return data;
    }

    public static DataStore parseOverloadExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(OverloadExpr.QUALIFIER, lines.nextLine());
        data.add(OverloadExpr.NAME, lines.nextLine());
        dataStore.getClavaNodes().queueSetNodeList(data, OverloadExpr.UNRESOLVED_DECLS,
                LineStreamParsers.stringList(lines));
        data.add(OverloadExpr.TEMPLATE_ARGUMENTS, ClavaDataParsers.templateArguments(lines, dataStore));
        // data.add(OverloadExpr.TEMPLATE_ARGUMENTS, LineStreamParsers.stringList(lines,
        // ClavaDataParsers::literalSource));

        return data;
    }

    public static DataStore parseCXXConstructExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(CXXConstructExpr.IS_ELIDABLE, LineStreamParsers.oneOrZero(lines));
        // data.add(CXXConstructExpr.IS_DEFAULT_ARGUMENT, LineStreamParsers.oneOrZero(lines));
        data.add(CXXConstructExpr.REQUIRES_ZERO_INITIALIZATION, LineStreamParsers.oneOrZero(lines));
        data.add(CXXConstructExpr.IS_LIST_INITIALIZATION, LineStreamParsers.oneOrZero(lines));
        data.add(CXXConstructExpr.IS_STD_LIST_INITIALIZATION, LineStreamParsers.oneOrZero(lines));
        data.add(CXXConstructExpr.CONSTRUCTION_KIND, LineStreamParsers.enumFromName(ConstructionKind.class, lines));
        data.add(CXXConstructExpr.IS_TEMPORARY_OBJECT, LineStreamParsers.oneOrZero(lines));
        dataStore.getClavaNodes().queueSetNode(data, CXXConstructExpr.CONSTRUCTOR_DECL, lines.nextLine());

        return data;
    }

    public static DataStore parseCXXTemporaryObjectExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseCXXConstructExprData(lines, dataStore);

        return data;
    }

    public static DataStore parseMemberExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(MemberExpr.IS_ARROW, LineStreamParsers.oneOrZero(lines));
        data.add(MemberExpr.MEMBER_NAME, lines.nextLine());
        dataStore.getClavaNodes().queueSetNode(data, MemberExpr.MEMBER_DECL, lines.nextLine());
        dataStore.getClavaNodes().queueSetNode(data, MemberExpr.FOUND_DECL, lines.nextLine());
        data.add(MemberExpr.FOUND_DECL_ACCESS_SPECIFIER, LineStreamParsers.enumFromName(AccessSpecifier.class, lines));

        return data;
    }

    public static DataStore parseMaterializeTemporaryExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        dataStore.getClavaNodes().queueSetOptionalNode(data, MaterializeTemporaryExpr.EXTENDING_DECL, lines.nextLine());

        return data;
    }

    public static DataStore parseBinaryOperatorData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(BinaryOperator.OP, LineStreamParsers.enumFromName(BinaryOperatorKind.class, lines));

        return data;
    }

    public static DataStore parseUnresolvedMemberExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseOverloadExprData(lines, dataStore);

        return data;
    }

    public static DataStore parseUnresolvedLookupExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseOverloadExprData(lines, dataStore);

        data.add(UnresolvedLookupExpr.REQUIRES_ADL, LineStreamParsers.oneOrZero(lines));

        return data;
    }

    public static DataStore parseCallExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        dataStore.getClavaNodes().queueSetOptionalNode(data, CallExpr.DIRECT_CALLEE, lines.nextLine());

        return data;
    }

    public static DataStore parseCXXMemberCallExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseCallExprData(lines, dataStore);

        dataStore.getClavaNodes().queueSetOptionalNode(data, CXXMemberCallExpr.METHOD_DECL, lines.nextLine());

        return data;
    }

    public static DataStore parseCXXOperatorCallExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseCallExprData(lines, dataStore);

        // dataStore.getClavaNodes().queueSetNode(data, CXXMemberCallExpr.METHOD_DECL, lines.nextLine());

        return data;
    }

    public static DataStore parseCXXTypeidExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(CXXTypeidExpr.IS_TYPE_OPERAND, LineStreamParsers.oneOrZero(lines));
        dataStore.getClavaNodes().queueSetNode(data, CXXTypeidExpr.OPERAND, lines.nextLine());

        return data;
    }

    public static DataStore parseExplicitCastExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseCastExprData(lines, dataStore);

        dataStore.getClavaNodes().queueSetNode(data, ExplicitCastExpr.TYPE_AS_WRITTEN, lines.nextLine());

        return data;
    }

    public static DataStore parseCXXNamedCastExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExplicitCastExprData(lines, dataStore);

        data.add(CXXNamedCastExpr.CAST_NAME, lines.nextLine());

        return data;
    }

    public static DataStore parseCXXDependentScopeMemberExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(CXXDependentScopeMemberExpr.IS_ARROW, LineStreamParsers.oneOrZero(lines));
        data.add(CXXDependentScopeMemberExpr.MEMBER_NAME, lines.nextLine());

        data.add(CXXDependentScopeMemberExpr.IS_IMPLICIT_ACCESS, LineStreamParsers.oneOrZero(lines));
        data.add(CXXDependentScopeMemberExpr.QUALIFIER, lines.nextLine());
        data.add(CXXDependentScopeMemberExpr.HAS_TEMPLATE_KEYWORD, LineStreamParsers.oneOrZero(lines));
        data.add(CXXDependentScopeMemberExpr.TEMPLATE_ARGUMENTS, ClavaDataParsers.templateArguments(lines, dataStore));

        return data;
    }

    public static DataStore parseUnaryOperatorData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(UnaryOperator.OP, LineStreamParsers.enumFromName(UnaryOperatorKind.class, lines));
        data.add(UnaryOperator.POSITION, LineStreamParsers.enumFromName(UnaryOperatorPosition.class, lines));

        return data;
    }

    public static DataStore parseUnaryExprOrTypeTraitExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(UnaryExprOrTypeTraitExpr.KIND, LineStreamParsers.enumFromName(UnaryExprOrTypeTrait.class, lines));
        data.add(UnaryExprOrTypeTraitExpr.IS_ARGUMENT_TYPE, LineStreamParsers.oneOrZero(lines));
        dataStore.getClavaNodes().queueSetOptionalNode(data, UnaryExprOrTypeTraitExpr.ARG_TYPE, lines.nextLine());
        data.add(UnaryExprOrTypeTraitExpr.SOURCE_LITERAL, ClavaDataParsers.literalSource(lines));

        return data;
    }

    public static DataStore parseCXXNewExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(CXXNewExpr.IS_GLOBAL, LineStreamParsers.oneOrZero(lines));
        data.add(CXXNewExpr.IS_ARRAY, LineStreamParsers.oneOrZero(lines));
        data.add(CXXNewExpr.HAS_INITIALIZER, LineStreamParsers.oneOrZero(lines));
        data.add(CXXNewExpr.INIT_STYLE, LineStreamParsers.enumFromName(NewInitStyle.class, lines));
        dataStore.getClavaNodes().queueSetOptionalNode(data, CXXNewExpr.INITIALIZER, lines.nextLine());
        dataStore.getClavaNodes().queueSetOptionalNode(data, CXXNewExpr.CONSTRUCT_EXPR, lines.nextLine());
        dataStore.getClavaNodes().queueSetOptionalNode(data, CXXNewExpr.ARRAY_SIZE, lines.nextLine());
        dataStore.getClavaNodes().queueSetOptionalNode(data, CXXNewExpr.OPERATOR_NEW, lines.nextLine());

        return data;
    }

    public static DataStore parseCXXDeleteExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(CXXDeleteExpr.IS_GLOBAL, LineStreamParsers.oneOrZero(lines));
        data.add(CXXDeleteExpr.IS_ARRAY, LineStreamParsers.oneOrZero(lines));
        data.add(CXXDeleteExpr.IS_ARRAY_AS_WRITTEN, LineStreamParsers.oneOrZero(lines));
        // dataStore.getClavaNodes().queueSetNode(data, CXXDeleteExpr.ARGUMENT_EXPR, lines.nextLine());

        return data;
    }

    public static DataStore parseOffsetOfExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        dataStore.getClavaNodes().queueSetNode(data, OffsetOfExpr.SOURCE_TYPE, lines.nextLine());
        data.set(OffsetOfExpr.COMPONENTS,
                ClavaDataParsers.list(lines, dataStore, ClavaDataParsers::offsetOfComponent));

        return data;
    }

    public static DataStore parseLambdaExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.set(LambdaExpr.IS_GENERIC_LAMBDA, LineStreamParsers.oneOrZero(lines));
        data.set(LambdaExpr.IS_MUTABLE, LineStreamParsers.oneOrZero(lines));
        data.set(LambdaExpr.HAS_EXPLICIT_PARAMETERS, LineStreamParsers.oneOrZero(lines));
        data.set(LambdaExpr.HAS_EXPLICIT_RESULT_TYPE, LineStreamParsers.oneOrZero(lines));
        data.set(LambdaExpr.CAPTURE_DEFAULT, LineStreamParsers.enumFromName(LambdaCaptureDefault.class, lines));

        dataStore.getClavaNodes().queueSetNode(data, LambdaExpr.LAMBDA_CLASS, lines.nextLine());

        data.set(LambdaExpr.CAPTURE_KINDS,
                ClavaDataParsers.list(lines, dataStore,
                        (linestream, parserData) -> LineStreamParsers.enumFromName(LambdaCaptureKind.class,
                                linestream)));

        return data;
    }

    public static DataStore parsePredefinedExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.set(PredefinedExpr.PREDEFINED_TYPE, LineStreamParsers.enumFromName(PredefinedIdType.class, lines));
        // data.set(PredefinedExpr.FUNCTION_NAME, lines.nextLine());

        return data;
    }

    public static DataStore parseSizeOfPackExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.set(SizeOfPackExpr.IS_PARTIALLY_SUBSTITUTED, LineStreamParsers.oneOrZero(lines));

        dataStore.getClavaNodes().queueSetNode(data, SizeOfPackExpr.PACK, lines.nextLine());

        data.set(SizeOfPackExpr.PARTIAL_ARGUMENTS,
                ClavaDataParsers.list(lines, dataStore, ClavaDataParsers::templateArgument));

        return data;
    }

    public static DataStore parseArrayInitLoopExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        return data;
    }

    public static DataStore parseDesignatedInitExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.set(DesignatedInitExpr.USES_GNU_SYNTAX, LineStreamParsers.oneOrZero(lines));
        data.set(DesignatedInitExpr.DESIGNATORS, LineStreamParsers.list(lines, ClavaDataParsers::designator));

        return data;
    }

    public static DataStore parseDependentScopeDeclRefExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(DependentScopeDeclRefExpr.DECL_NAME, lines.nextLine());
        data.add(DependentScopeDeclRefExpr.QUALIFIER, lines.nextLine());
        data.add(DependentScopeDeclRefExpr.HAS_TEMPLATE_KEYWORD, LineStreamParsers.oneOrZero(lines));
        data.add(DependentScopeDeclRefExpr.TEMPLATE_ARGUMENTS, ClavaDataParsers.templateArguments(lines, dataStore));

        return data;
    }

    public static DataStore parseCXXNoexceptExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(CXXNoexceptExpr.VALUE, LineStreamParsers.oneOrZero(lines));

        return data;
    }

    public static DataStore parseCXXPseudoDestructorExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(CXXPseudoDestructorExpr.QUALIFIER, LineStreamParsers.optionalString(lines));
        data.add(CXXPseudoDestructorExpr.IS_ARROW, LineStreamParsers.oneOrZero(lines));
        dataStore.getClavaNodes().queueSetNode(data, CXXPseudoDestructorExpr.DESTROYED_TYPE, lines.nextLine());

        return data;
    }
    
    public static DataStore parsePseudoObjectExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        data.add(PseudoObjectExpr.RESULT_EXPR_INDEX, LineStreamParsers.integer(lines));

        return data;
    }

    public static DataStore parseMSPropertyRefExprData(LineStream lines, ClangParserData dataStore) {
        DataStore data = parseExprData(lines, dataStore);

        dataStore.getClavaNodes().queueSetNode(data, MSPropertyRefExpr.BASE_EXPR, lines.nextLine());
        dataStore.getClavaNodes().queueSetNode(data, MSPropertyRefExpr.PROPERTY_DECL, lines.nextLine());
        data.add(MSPropertyRefExpr.IS_IMPLICIT_ACCESS, LineStreamParsers.oneOrZero(lines));
        data.add(MSPropertyRefExpr.IS_ARROW, LineStreamParsers.oneOrZero(lines));

        return data;
    }
    
    // public static DataStore parseFullExprData(LineStream lines, ClangParserData dataStore) {
    // DataStore data = parseExprData(lines, dataStore);
    //
    // dataStore.getClavaNodes().queueSetNode(data, FullExpr.SUB_EXPR, lines.nextLine());
    //
    // return data;
    // }

}
