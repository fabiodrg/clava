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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import pt.up.fe.specs.clava.ClavaLog;
import pt.up.fe.specs.clava.ClavaNode;
import pt.up.fe.specs.clava.SourceRange;
import pt.up.fe.specs.clava.ast.comment.MultiLineComment;
import pt.up.fe.specs.clava.context.ClavaContext;
import pt.up.fe.specs.clava.parsing.snippet.TextParserRule;

public class MultiLineCommentRule implements TextParserRule {

    @Override
    public Optional<ClavaNode> apply(String filepath, String line, int lineNumber, Iterator<String> iterator,
            ClavaContext context) {

        // Check if line contains '/*'
        int startIndex = line.indexOf("/*");
        if (startIndex == -1) {
            return Optional.empty();
        }

        // Check if inside a string
        // This will detect false positives, hack until text elements are obtained from Clang
        if (isInsideString(line, startIndex)) {
            return Optional.empty();
        }

        // Found start of a multi-line comment. Try to find the end
        List<String> lines = new ArrayList<>();

        String currentLine = line.substring(startIndex + "/*".length());

        int endIndex = -1;
        while (true) {

            // Check if current line end the multi-line comment
            endIndex = currentLine.indexOf("*/");

            if (endIndex != -1) {

                // Found end of multi-line comment, add line
                lines.add(currentLine.substring(0, endIndex).trim());
                break;

            }

            // If no more line, multi-line comment does not end.
            // Add line, warn user and return
            if (!iterator.hasNext()) {
                lines.add(currentLine.trim());
                ClavaLog.info("Could not find end of multi-line comment start at '" + filepath + "':" + lineNumber);
                break;
            }

            // Did not find end of comment, add current string to list
            lines.add(currentLine.trim());
            currentLine = iterator.next();
        }

        int startCol = startIndex;
        int endCol = endIndex;
        int endLine = lineNumber + lines.size() - 1;

        SourceRange loc = new SourceRange(filepath, lineNumber, startCol, endLine, endCol);

        MultiLineComment comment = context.getFactory().multiLineComment(lines);
        comment.set(ClavaNode.LOCATION, loc);

        return Optional.of(comment);
    }

}
