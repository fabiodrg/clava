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

package pt.up.fe.specs.clava.context;

import java.util.Collections;

import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.clava.ClavaNode;
import pt.up.fe.specs.clava.ast.expr.enums.BuiltinKind;
import pt.up.fe.specs.clava.ast.type.BuiltinType;

public class ClavaFactory {

    private final ClavaContext context;
    private final DataStore baseData;

    public ClavaFactory(ClavaContext context) {
        this(context, null);
    }

    public ClavaFactory(ClavaContext context, DataStore baseData) {
        this.context = context;
        this.baseData = baseData;
    }

    private DataStore newDataStore() {
        DataStore data = DataStore.newInstance("ClavaFactory Node");

        // Add base node, if present
        if (baseData != null) {
            data.addAll(baseData);
        }

        data.set(ClavaNode.CONTEXT, context);

        return data;
    }

    /// TYPES

    public BuiltinType builtinType(BuiltinKind kind) {
        DataStore data = newDataStore().put(BuiltinType.KIND, kind);
        return new BuiltinType(data, Collections.emptyList());
    }

}
