/**
 * Copyright 2016 SPeCS.
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

public class DecayedType extends AdjustedType {

    /// DATAKEYS BEGIN

    public final static DataKey<Type> DECAYED_TYPE = KeyFactory.object("decayedType", Type.class);

    public final static DataKey<Type> POINTEE_TYPE = KeyFactory.object("pointeeType", Type.class);

    /// DATAKEYS END

    public DecayedType(DataStore data, Collection<? extends ClavaNode> children) {
        super(data, children);
    }

    // Return the code of the original type
    @Override
    public String getCode(ClavaNode sourceNode, String name) {
        return getOriginalType().getCode(sourceNode, name);
    }

    @Override
    public boolean isArray() {
        return getOriginalType().isArray();
    }

    @Override
    public Type normalize() {
        return getOriginalType();
    }

}
