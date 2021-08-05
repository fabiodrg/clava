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

package pt.up.fe.specs.clava.ast.type.data;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;

import pt.up.fe.specs.clava.ast.decl.Decl;
import pt.up.fe.specs.clava.ast.type.FunctionProtoType;

public class UnevaluatedExceptionSpecification extends ExceptionSpecification {

    /// DATAKEYS BEGIN

    /**
     * The id of the FunctionDecl corresponding to this exception specification.
     * 
     * <p>
     * An id is used instead of a reference to the node because at parsing time, the node might be in halfway built when
     * it is needed.
     */
    // public final static DataKey<String> SOURCE_DECL_ID = KeyFactory.string("sourceDeclId");

    /**
     * The FunctionDecl corresponding to this exception specification.
     * 
     */
    public final static DataKey<Decl> SOURCE_DECL = KeyFactory.object("sourceDecl", Decl.class);

    /// DATAKEYS END

    @Override
    public String getCode(FunctionProtoType type) {

        // Decl sourceDecl = getSourceDecl(type);
        // System.out.println("SOURCE DECL:" + sourceDecl);
        // FunctionType sourceDeclType = sourceDecl.getFunctionType();
        //
        // System.out.println("EXCEPTION TYPE:" + type.getId());
        // System.out.println("SOURCE DECL TYPE:" + sourceDeclType.getId());

        return "noexcept";
        // type.getApp().getNode(id)
        // System.out.println("UNEVAL SOURCE DECL:" + get(SOURCE_DECL_ID));
        // return super.getCode();
    }

    public Decl getSourceDecl(FunctionProtoType type) {
        return get(SOURCE_DECL);
        // ClavaNode clavaNode = type.getContext().get(ClavaContext.APP).getNode(get(SOURCE_DECL_ID));
        //
        // SpecsCheck.checkArgument(clavaNode instanceof FunctionDecl,
        // () -> "Expected SourceDecl to be a FunctionDecl, is '" + clavaNode.getClass() + "'");
        //
        // return (FunctionDecl) clavaNode;
    }
}
