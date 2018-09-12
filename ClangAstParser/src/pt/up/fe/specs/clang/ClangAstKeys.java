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

package pt.up.fe.specs.clang;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;

public interface ClangAstKeys {

    DataKey<String> CLANGAST_VERSION = KeyFactory.string("clangast_version", "");

    /**
     * If true, tries to use the platform system includes (if available), instead of the built-in system includes.
     */
    DataKey<Boolean> USE_PLATFORM_INCLUDES = KeyFactory.bool("platformIncludes")
            .setLabel("Uses the platform system includes headers (if available)");

}
