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

package pt.up.fe.specs.clava;

import java.util.List;

import org.suikasoft.GsonPlus.JsonStringList;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionProvider;

import pt.up.fe.specs.clava.language.Standard;

public interface ClavaOptions extends StoreDefinitionProvider {

    DataKey<Standard> STANDARD = KeyFactory.enumeration("C/C++ Standard", Standard.class)
            .setDefault(() -> Standard.CXX11);

    DataKey<String> FLAGS = KeyFactory.string("Compiler Flags", "");

    DataKey<List<String>> FLAGS_LIST = JsonStringList.newKey("Compiler Flags in list format");

    DataKey<Boolean> CUSTOM_RESOURCES = KeyFactory.bool("Clava Custom Resources")
            .setLabel("Enable custom resource files");

    DataKey<Boolean> DISABLE_REMOTE_DEPENDENCIES = KeyFactory.bool("Disable Remote Dependencies")
            .setLabel("Disable remote dependencies (e.g., git repositories)");

    DataKey<String> CUDA_GPU_ARCH = KeyFactory.string("cudaGpuArch")
            .setLabel("CUDA GPU Arch (default: sm_30)")
            .setDefaultString("sm_30");

    DataKey<String> CUDA_PATH = KeyFactory.string("cudaPath")
            .setLabel("CUDA Path (leave empty if in path)")
            .setDefaultString("");

    // DataKey<Boolean> DISABLE_CLAVA_DATA_NODES = KeyFactory.bool("Disable Clava Data nodes")
    // .setLabel("Disables new method for parsing nodes (only uses 'legacy' nodes)");

    StoreDefinition STORE_DEFINITION = new StoreDefinitionBuilder("Clava")
            // .addKeys(STANDARD, FLAGS, CUSTOM_RESOURCES, DISABLE_REMOTE_DEPENDENCIES, DISABLE_CLAVA_DATA_NODES)
            .addKeys(STANDARD, FLAGS, FLAGS_LIST, CUDA_GPU_ARCH, CUDA_PATH, CUSTOM_RESOURCES,
                    DISABLE_REMOTE_DEPENDENCIES)
            .build();

    @Override
    default StoreDefinition getStoreDefinition() {
        return STORE_DEFINITION;
    }

}
