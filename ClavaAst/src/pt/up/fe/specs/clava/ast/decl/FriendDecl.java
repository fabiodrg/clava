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

import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.clava.ClavaLog;
import pt.up.fe.specs.clava.ClavaNode;
import pt.up.fe.specs.clava.ast.type.Type;
import pt.up.fe.specs.clava.utils.NullNode;

/**
 * Represents the declaration of a friend entity.
 * 
 * <p>
 * Friend node can either be a Type or a Decl.
 * 
 * @author JoaoBispo
 *
 */
public class FriendDecl extends Decl {

    public FriendDecl(DataStore data, Collection<? extends ClavaNode> children) {
        super(data, children);
    }

    public ClavaNode getFriendNode() {
        return getChild(0);
    }

    @Override
    public String getCode() {
        var friendNode = getFriendNode();
        if (friendNode instanceof NullNode) {
            ClavaLog.warning(this, "FriendDecl not yet implemented for this case");
            return "friend";
        }

        String friendCode = friendNode.getCode();

        if (friendNode instanceof Type) {
            friendCode = friendCode + ";";
        }

        // Check if it has new lines at the beginning
        if (friendCode.startsWith(ln())) {
            friendCode = friendCode.substring(ln().length());
        }

        if (friendNode instanceof RedeclarableTemplateDecl) {
            return ((RedeclarableTemplateDecl) friendNode).getCode("friend");
        }

        return "friend " + friendCode;
    }

}
