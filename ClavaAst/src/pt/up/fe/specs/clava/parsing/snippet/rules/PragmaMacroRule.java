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

package pt.up.fe.specs.clava.parsing.snippet.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pt.up.fe.specs.clava.ClavaNode;
import pt.up.fe.specs.clava.SourceRange;
import pt.up.fe.specs.clava.context.ClavaContext;
import pt.up.fe.specs.clava.parsing.pragma.PragmaParsers;
import pt.up.fe.specs.clava.parsing.snippet.TextParserRule;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.stringparser.StringParser;
import pt.up.fe.specs.util.stringparser.StringParsers;

public class PragmaMacroRule implements TextParserRule {

    private static final String PRAGMA = "_Pragma";

    @Override
    public Optional<ClavaNode> apply(String filepath, String line, int lineNumber, Iterator<String> iterator,
            ClavaContext context) {

        // To calculate position of pragma
        String lastLine = line;

        // Check if line starts with '_Pragma'
        String trimmedLine = line.trim();

        // First characters that can contain #pragma
        if (trimmedLine.length() < PRAGMA.length()) {
            return Optional.empty();
        }

        String probe = trimmedLine.substring(0, PRAGMA.length());
        if (!probe.startsWith(PRAGMA)) {
            return Optional.empty();
        }

        // Found start of pragma. Try to find the end
        trimmedLine = trimmedLine.substring(PRAGMA.length()).trim();

        List<String> pragmaContents = new ArrayList<String>();

        while (trimmedLine.endsWith("\\")) {

            // Add line, without the ending '\'
            pragmaContents.add(trimmedLine.substring(0, trimmedLine.length() - 1));

            if (!iterator.hasNext()) {
                SpecsLogs
                        .msgInfo("Could not parse " + PRAGMA + ", there is no more lines after '" + trimmedLine + "'");
                return Optional.empty();
            }

            // Get next line
            lastLine = iterator.next();
            trimmedLine = lastLine.trim();
        }

        // Add last non-broken line
        pragmaContents.add(trimmedLine);

        // Get a single string
        String pragmaContentsSingleLine = pragmaContents.stream()
                .collect(Collectors.joining());

        StringParser parser = new StringParser(pragmaContentsSingleLine);

        parser.apply(StringParsers::parseString, "(");
        String pragmaFullContent = parser.apply(StringParsers::parseDoubleQuotedString);
        parser.apply(StringParsers::parseString, ")");

        int startCol = line.indexOf(PRAGMA) + 1;
        int endCol = -1;
        int endLine = lineNumber + pragmaContents.size() - 1;

        SourceRange loc = new SourceRange(filepath, lineNumber, startCol, endLine, endCol);

        // Try to parse pragma. If pragma not parsable, create generic pragma
        ClavaNode pragmaNode = PragmaParsers.parse(pragmaFullContent, context)
                .orElse(context.getFactory().genericPragma(Arrays.asList(pragmaFullContent)));
        pragmaNode.set(ClavaNode.LOCATION, loc);

        return Optional.of(pragmaNode);
    }

}
