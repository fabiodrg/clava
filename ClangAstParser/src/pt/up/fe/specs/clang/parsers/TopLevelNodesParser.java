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

package pt.up.fe.specs.clang.parsers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.streamparser.LineStreamParsers;
import org.suikasoft.jOptions.streamparser.LineStreamWorker;

import pt.up.fe.specs.clang.dumper.ClangAstData;
import pt.up.fe.specs.util.utilities.LineStream;

public class TopLevelNodesParser {

    private static final String PARSER_ID_DECLS = "<Top Level Nodes>";
    private static final String PARSER_ID_TYPES = "<Top Level Types>";
    private static final String PARSER_ID_ATTR = "<Top Level Attributes>";
    // private static final String PARSER_ID_ALL_DECLS = "<All Decls>";

    public static Collection<LineStreamWorker<ClangAstData>> getWorkers() {
        List<LineStreamWorker<ClangAstData>> workers = new ArrayList<>();

        workers.add(newWorker(PARSER_ID_DECLS, ClangAstData.TOP_LEVEL_DECL_IDS));
        workers.add(newWorker(PARSER_ID_TYPES, ClangAstData.TOP_LEVEL_TYPE_IDS, false));
        workers.add(newWorker(PARSER_ID_ATTR, ClangAstData.TOP_LEVEL_ATTR_IDS));
        // workers.add(newWorker(PARSER_ID_ALL_DECLS, ClangParserKeys.ALL_DECLS_IDS, false));

        return workers;
    }

    public static String getTopLevelNodesHeader() {
        return PARSER_ID_DECLS;
    }

    private static LineStreamWorker<ClangAstData> newWorker(String id, DataKey<Set<String>> key) {
        return newWorker(id, key, true);
    }

    private static LineStreamWorker<ClangAstData> newWorker(String id, DataKey<Set<String>> key,
            boolean checkDuplicate) {

        return LineStreamWorker.newInstance(id, data -> init(key, data),
                (lines, data) -> apply(id, key, lines, data, checkDuplicate));
    }

    private static void init(DataKey<Set<String>> key, ClangAstData data) {
        data.set(key, new LinkedHashSet<>());
    }

    private static void apply(String id, DataKey<Set<String>> key, LineStream lines, ClangAstData data,
            boolean checkDuplicate) {

        LineStreamParsers.stringSet(id, lines, data.get(key), checkDuplicate);
    }

}
