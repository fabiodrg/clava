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

package pt.up.fe.specs.clava.ast;

import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.clava.ClavaId;
import pt.up.fe.specs.clava.ClavaNode;
import pt.up.fe.specs.clava.ClavaNodeInfo;
import pt.up.fe.specs.clava.ast.attr.Attribute;
import pt.up.fe.specs.clava.ast.attr.legacy.AttrData;
import pt.up.fe.specs.clava.ast.expr.Expr;
import pt.up.fe.specs.clava.ast.expr.data.ExprData;
import pt.up.fe.specs.clava.ast.type.Type;
import pt.up.fe.specs.clava.ast.type.data.TypeData;
import pt.up.fe.specs.clava.ast.type.enums.TypeDependency;

public class DataStoreToLegacy {

    /// EXTRAS

    public static ClavaNodeInfo getNodeInfo(DataStore data) {
        return new ClavaNodeInfo(
                new ClavaId(data.get(ClavaNode.ID)),
                data.get(ClavaNode.LOCATION));
    }

    /// EXPRS

    public static ExprData getExpr(DataStore data) {
        return new ExprData(
                data.get(Expr.TYPE).orElse(null),
                data.get(Expr.VALUE_KIND),
                data.get(Expr.OBJECT_KIND));
    }

    /// TYPE

    public static TypeData getType(DataStore data) {
        return new TypeData(data.get(Type.TYPE_AS_STRING),
                // data.get(Type.HAS_SUGAR),
                data.get(Type.TYPE_DEPENDENCY) != TypeDependency.NONE,
                data.get(Type.TYPE_DEPENDENCY),
                data.get(Type.IS_VARIABLY_MODIFIED),
                data.get(Type.CONTAINS_UNEXPANDED_PARAMETER_PACK),
                data.get(Type.IS_FROM_AST));
    }

    // public static ArrayTypeData getArrayType(DataStore data, Standard standard) {
    //
    // return new ArrayTypeData(
    // ArraySizeType.values()[data.get(ArrayType.ARRAY_SIZE_MODIFIER).ordinal()],
    // data.get(ArrayType.INDEX_TYPE_QUALIFIERS).stream()
    // .map(Qualifier::parse)
    // .collect(Collectors.toList()),
    // standard);
    // }

    /// ATTRS

    public static AttrData getAttribute(DataStore data) {
        return new AttrData(data.get(Attribute.IS_INHERITED), data.get(Attribute.IS_IMPLICIT));
    }

}
