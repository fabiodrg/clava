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

package pt.up.fe.specs.clava.ast.decl;

import java.util.Collection;
import java.util.Optional;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.clava.ClavaNode;
import pt.up.fe.specs.clava.ast.expr.CXXConstructExpr;
import pt.up.fe.specs.clava.ast.expr.Expr;
import pt.up.fe.specs.clava.ast.expr.NullExpr;

/**
 * Represents a member of a struct/union/class.
 * 
 * @author JoaoBispo
 *
 */
public class FieldDecl extends DeclaratorDecl {

    /// DATAKEYS BEGIN

    public final static DataKey<Boolean> IS_MUTABLE = KeyFactory.bool("isMutable");

    /// DATAKEYS END

    public FieldDecl(DataStore data, Collection<? extends ClavaNode> children) {
        super(data, children);
    }

    public Optional<Expr> getBitwidth() {
        if (!hasChildren()) {
            return Optional.empty();
        }

        Expr bitwidth = getChild(Expr.class, 0);
        return bitwidth instanceof NullExpr ? Optional.empty() : Optional.of(bitwidth);
    }

    public Optional<Expr> getInitialization() {
        if (getNumChildren() < 2) {
            return Optional.empty();
        }

        Expr init = getChild(Expr.class, 1);
        return init instanceof NullExpr ? Optional.empty() : Optional.of(init);

    }

    @Override
    public String getCode() {
        StringBuilder code = new StringBuilder();

        if (get(IS_MUTABLE)) {
            code.append("mutable ");
        }

        String name = getDeclName();

        code.append(getType().getCode(this, name));

        getBitwidth().ifPresent(expr -> code.append(": ").append(expr.getCode()));

        // getInitExpr().ifPresent(expr -> code.append(" = ").append(expr.getCode()));
        Optional<Expr> init = getInitialization();
        if (init.isPresent() && init.get() instanceof CXXConstructExpr) {
            CXXConstructExpr cxxConstructor = (CXXConstructExpr) init.get();
            code.append(cxxConstructor.getCode(name));
        } else {
            init.ifPresent(expr -> code.append(" = ").append(expr.getCode()));
        }

        // }

        code.append(";");
        // System.out.println("FIELD DECL:" + code);
        // System.out.println("INIT:" + getInitialization().map(expr -> expr.getCode()));
        // System.out.println("INIT CLASS:" + getInitialization().map(expr -> expr.getClass()));
        return code.toString();
    }

}
