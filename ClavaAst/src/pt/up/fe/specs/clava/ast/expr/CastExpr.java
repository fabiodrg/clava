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

package pt.up.fe.specs.clava.ast.expr;

import java.util.Collection;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.clava.ClavaNode;
import pt.up.fe.specs.clava.ast.expr.enums.ValueKind;
import pt.up.fe.specs.clava.ast.type.Type;
import pt.up.fe.specs.clava.language.CastKind;

/**
 * Represents a type cast.
 * 
 * @author JoaoBispo
 *
 */
public abstract class CastExpr extends Expr {

    /// DATAKEY BEGIN

    public final static DataKey<CastKind> CAST_KIND = KeyFactory.enumeration("castKind", CastKind.class);

    /// DATAKEY END

    public CastExpr(DataStore data, Collection<? extends ClavaNode> children) {
        super(data, children);
    }

    @Override
    public ValueKind getValueKind() {
        return getSubExpr().getValueKind();
    }

    public CastKind getCastKind() {
        return get(CAST_KIND);
    }

    public Expr getSubExpr() {
        return getChild(Expr.class, 0);
    }

    /**
     * The type this cast will cast to.
     * 
     * @return
     */
    public Type getCastType() {
        return getType();
    }

}
