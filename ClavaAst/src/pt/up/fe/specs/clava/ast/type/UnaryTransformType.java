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

package pt.up.fe.specs.clava.ast.type;

import java.util.Collection;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.clava.ClavaNode;
import pt.up.fe.specs.clava.ast.type.enums.UnaryTransformTypeKind;

/**
 * A unary type transform, which is a type constructed from another.
 * 
 * @author JoaoBispo
 *
 */
public class UnaryTransformType extends Type {

    /// DATAKEYS BEGIN

    public final static DataKey<UnaryTransformTypeKind> KIND = KeyFactory
            .enumeration("kind", UnaryTransformTypeKind.class);

    public final static DataKey<Type> UNDERLYING_TYPE = KeyFactory.object("underlyingType", Type.class);

    public final static DataKey<Type> BASE_TYPE = KeyFactory.object("baseType", Type.class);

    /// DATAKEYS END

    public UnaryTransformType(DataStore data, Collection<? extends ClavaNode> children) {
        super(data, children);
    }

    public Type getBaseType() {
        return get(BASE_TYPE);
    }

    public Type getUnderlyingType() {
        return get(UNDERLYING_TYPE);
    }

}
