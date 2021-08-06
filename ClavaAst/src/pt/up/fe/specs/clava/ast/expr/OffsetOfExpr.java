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

package pt.up.fe.specs.clava.ast.expr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.clava.ClavaNode;
import pt.up.fe.specs.clava.ast.expr.data.offsetof.OffsetOfComponent;
import pt.up.fe.specs.clava.ast.type.Type;

public class OffsetOfExpr extends Expr {

    /// DATAKEYS BEGIN

    public final static DataKey<Type> SOURCE_TYPE = KeyFactory.object("sourceType", Type.class);

    public final static DataKey<List<OffsetOfComponent>> COMPONENTS = KeyFactory.generic("components",
            new ArrayList<OffsetOfComponent>());

    /// DATAKEYS END

    public OffsetOfExpr(DataStore data, Collection<? extends ClavaNode> children) {
        super(data, children);
    }

    @Override
    public String getCode() {
        String componentsCode = getComponentsCode();
        return "offsetof(" + get(SOURCE_TYPE).getCode(this) + ", " + componentsCode + ")";
    }

    private String getComponentsCode() {

        StringBuilder code = new StringBuilder();

        boolean isFirst = true;
        for (OffsetOfComponent component : get(COMPONENTS)) {

            if (component.isField() && !isFirst) {
                code.append(".");
            }

            code.append(component.getCode());

            if (isFirst) {
                isFirst = false;
            }
        }
        return code.toString();
    }
}
