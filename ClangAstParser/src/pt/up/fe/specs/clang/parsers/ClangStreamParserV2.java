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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.suikasoft.jOptions.streamparser.LineStreamParser;
import org.suikasoft.jOptions.streamparser.LineStreamWorker;

import pt.up.fe.specs.clang.codeparser.ClangParserData;
import pt.up.fe.specs.clang.version.Clang_3_8;
import pt.up.fe.specs.clava.context.ClavaContext;
import pt.up.fe.specs.util.SpecsCheck;

public class ClangStreamParserV2 {

    private final static String HEADER_WARNING_PREFIX = "error: invalid argument '";
    private final static String HEADER_WARNING_SUFFIX = "' not allowed with 'C'";

    private static final Map<String, LineStreamWorker<ClangParserData>> WORKERS;
    static {
        WORKERS = new HashMap<>();
        addWorker(() -> new ClavaNodeParser(Clang_3_8.getClassesService()));
        addWorker(VisitedChildrenParser::new);
        addWorker(IdToFilenameParser::new);
        addWorker(IncludesParser::new);
        addWorker(PragmasLocationsParser::new);
        addWorker(LanguageParser::new);
        TopLevelNodesParser.getWorkers().forEach(ClangStreamParserV2::addWorker);
        NodeDataParser.getWorkers().forEach(ClangStreamParserV2::addWorker);
        // addWorker(SystemHeadersClangNodes::new);
        // addWorker(SkippedNodesParser::new);
        // addWorker(VisitStartParser::new);
        // addWorker(VisitEndParser::new);
        VisitingChildrenCheck.getWorkers().forEach(ClangStreamParserV2::addWorker);
    }

    private static void addWorker(Supplier<LineStreamWorker<ClangParserData>> workerSupplier) {
        addWorker(workerSupplier.get());
    }

    private static void addWorker(LineStreamWorker<ClangParserData> worker) {
        // Add worker
        WORKERS.put(worker.getId(), worker);
    }

    // public static LineStreamParserV2 newInstance(List<String> arguments) {
    public static LineStreamParser<ClangParserData> newInstance(ClavaContext context) {
        ClangParserData clangParserData = new ClangParserData();
        clangParserData.set(ClangParserData.CONTEXT, context);
        LineStreamParser<ClangParserData> streamParser = LineStreamParser.newInstance(clangParserData, WORKERS);
        streamParser.setLineIgnore(ClangStreamParserV2::ignoreLine);
        // Create ClavaContext
        // streamParser.getData().add(ClavaNode.CONTEXT, new ClavaContext(arguments));
        // streamParser.getData().add(ClavaNode.CONTEXT, context);

        return streamParser;
        // return LineStreamParserV2.newInstance(WORKERS);
        // LineStreamParserV2 lineStreamParser = LineStreamParserV2.newInstance(WORKERS);
        //
        // // Initialize some keys
        // DataStore data = lineStreamParser.getData();
        // data.add(ClangParserKeys.CLAVA_NODES, new HashMap<>());
        // data.add(ClangParserKeys.CLAVA_DATA, new HashMap<>());
        // data.add(ClangParserKeys.VISITED_CHILDREN, new HashMap<>());
        //
        // return lineStreamParser;
    }

    private static boolean ignoreLine(String line) {
        if (line.startsWith(HEADER_WARNING_PREFIX)) {
            SpecsCheck.checkArgument(line.endsWith(HEADER_WARNING_SUFFIX),
                    () -> "Expected line to end with '" + HEADER_WARNING_SUFFIX + "': " + line);

            return true;
        }

        // Rule for #pragma once in header files
        if (line.endsWith("warning: #pragma once in main file [-Wpragma-once-outside-header]")) {
            return true;
        }
        if (line.trim().equals("^")) {
            return true;
        }
        if (line.equals("#pragma once")) {
            return true;
        }

        // System.out.println("LINE:" + line);
        // if (line.equals("error: invalid argument '-std=c++11' not allowed with 'C/ObjC'")) {
        // // System.out.println("IGNORING LINE");
        // return true;
        // }

        return false;
    }
}
