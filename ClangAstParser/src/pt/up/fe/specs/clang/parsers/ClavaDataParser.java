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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.clang.linestreamparser.LineStreamParser;
import pt.up.fe.specs.clang.linestreamparser.SimpleSnippetParser;
import pt.up.fe.specs.clang.parsers.clavadata.DeclDataParser;
import pt.up.fe.specs.clang.parsers.clavadata.ExprDataParser;
import pt.up.fe.specs.clang.parsers.clavadata.StmtDataParser;
import pt.up.fe.specs.clang.parsers.clavadata.TypeDataParser;
import pt.up.fe.specs.clava.SourceLocation;
import pt.up.fe.specs.clava.SourceRange;
import pt.up.fe.specs.clava.ast.ClavaData;
import pt.up.fe.specs.clava.ast.decl.data2.CXXMethodDeclDataV2;
import pt.up.fe.specs.clava.ast.decl.data2.DeclDataV2;
import pt.up.fe.specs.clava.ast.decl.data2.FunctionDeclDataV2;
import pt.up.fe.specs.clava.ast.decl.data2.NamedDeclData;
import pt.up.fe.specs.clava.ast.decl.data2.ParmVarDeclData;
import pt.up.fe.specs.clava.ast.decl.data2.VarDeclDataV2;
import pt.up.fe.specs.clava.ast.expr.data2.CastExprData;
import pt.up.fe.specs.clava.ast.expr.data2.CharacterLiteralData;
import pt.up.fe.specs.clava.ast.expr.data2.ExprDataV2;
import pt.up.fe.specs.clava.ast.expr.data2.IntegerLiteralData;
import pt.up.fe.specs.clava.ast.stmt.data.StmtData;
import pt.up.fe.specs.clava.ast.type.data2.BuiltinTypeData;
import pt.up.fe.specs.clava.ast.type.data2.TypeDataV2;
import pt.up.fe.specs.util.classmap.ClassMap;
import pt.up.fe.specs.util.stringparser.StringParsers;
import pt.up.fe.specs.util.utilities.LineStream;

/**
 * Utility methods for parsing ClavaData instances from a LineStream.
 * 
 * @author JoaoBispo
 *
 */
// Accepts a LineStream and returns a ClavaData object with the contents read from the stream.
// public interface ClavaDataParser extends Function<LineStream, ClavaData> {
// public class ClavaDataParser extends GenericLineStreamParser {
public class ClavaDataParser implements LineStreamParser {

    private static final ClassMap<ClavaData, Function<LineStream, ClavaData>> DATA_PARSERS;
    static {
        DATA_PARSERS = new ClassMap<>();

        // DECLS
        DATA_PARSERS.put(DeclDataV2.class, DeclDataParser::parseDeclData);
        DATA_PARSERS.put(NamedDeclData.class, DeclDataParser::parseNamedDeclData);
        DATA_PARSERS.put(FunctionDeclDataV2.class, DeclDataParser::parseFunctionDeclData);
        DATA_PARSERS.put(CXXMethodDeclDataV2.class, DeclDataParser::parseCXXMethodDeclData);
        DATA_PARSERS.put(VarDeclDataV2.class, DeclDataParser::parseVarDeclData);
        DATA_PARSERS.put(ParmVarDeclData.class, DeclDataParser::parseParmVarDeclData);

        // STMTS
        DATA_PARSERS.put(StmtData.class, StmtDataParser::parseStmtData);

        // TYPES
        DATA_PARSERS.put(TypeDataV2.class, TypeDataParser::parseTypeData);
        DATA_PARSERS.put(BuiltinTypeData.class, TypeDataParser::parseBuiltinTypeData);

        // EXPRS
        DATA_PARSERS.put(ExprDataV2.class, ExprDataParser::parseExprData);
        DATA_PARSERS.put(CastExprData.class, ExprDataParser::parseCastExprData);
        DATA_PARSERS.put(CharacterLiteralData.class, ExprDataParser::parseCharacterLiteralData);
        DATA_PARSERS.put(IntegerLiteralData.class, ExprDataParser::parseIntegerLiteralData);
    }

    // public static <T extends ClavaData> T getClavaData(DataStore dataStore, Class<T> clavaDataClass, String nodeId) {
    // return getClavaDataTry(dataStore, clavaDataClass, nodeId).orElseThrow(() -> new RuntimeException(
    // "Could not find data for node '" + nodeId + "'"));
    // }

    public static <T extends ClavaData> Optional<T> getClavaData(DataStore dataStore, Class<T> clavaDataClass,
            String nodeId) {

        DataKey<Map<String, ClavaData>> key = ClavaDataParser.getDataKey();

        // DataKey<Map<String, T>> key = ClavaDataParser.getDataKey(clavaDataClass);
        // DataKey<Map<String, T>> key = ClangNodeParsing.getNodeDataKey(clavaDataClass);

        if (!dataStore.hasValue(key)) {
            return Optional.empty();
        }

        // T data = getStdErr().get(key).get(node.getExtendedId());
        ClavaData data = dataStore.get(key).get(nodeId);

        if (data == null) {
            return Optional.empty();
        }

        // Check if class is compatible
        if (!clavaDataClass.isInstance(data)) {
            throw new RuntimeException("Node with id '" + nodeId + "' has ClavaData with class '"
                    + data.getClass().getSimpleName() + "' that is not compatible with the requested ClavaData class '"
                    + clavaDataClass.getSimpleName() + "'");
        }

        return Optional.of(clavaDataClass.cast(data));

    }

    public static ClavaDataParser newInstance() {
        // Map where all ClavaData instances will be stored
        Map<String, ClavaData> clavaDataMap = new HashMap<>();

        Map<String, SimpleSnippetParser<Map<String, ClavaData>>> clavaDataParsers = new HashMap<>();

        // Map<DataKey<?>, SnippetParser<?, ?>> parsers = new HashMap<>();

        for (Entry<Class<? extends ClavaData>, Function<LineStream, ClavaData>> entry : DATA_PARSERS.entrySet()) {
            // DataKey<?> dataKey = getDataKey(entry.getKey());
            String id = getNodeDataId(entry.getKey());
            // @SuppressWarnings("unchecked")
            // SnippetParser<?, ?> snippetParser = newSnippetParser((Class<ClavaData>) entry.getKey(),
            // entry.getValue());
            SimpleSnippetParser<Map<String, ClavaData>> snippetParser = newSnippetParser(id, entry.getValue(),
                    clavaDataMap);
            clavaDataParsers.put(id, snippetParser);
            // parsers.put(dataKey, snippetParser);
        }

        // return new ClavaDataParser(parsers);
        return new ClavaDataParser(clavaDataMap, clavaDataParsers);
    }

    /**
     * 
     * @param nodeClass
     * @return the corresponding DataKey for the node data of the given class
     */
    public static <T extends ClavaData> DataKey<Map<String, T>> getDataKey(Class<T> dataClass) {

        // Create key name
        String keyName = "stream_" + dataClass.getSimpleName();

        // Create DataKey
        return KeyFactory.generic(keyName, new HashMap<String, T>());
    }

    public static DataKey<Map<String, ClavaData>> getDataKey() {

        // Create key name
        String keyName = "stream_clava_data";

        // Create DataKey
        return KeyFactory.generic(keyName, new HashMap<String, ClavaData>());
    }

    public static List<DataKey<?>> getDataKeys() {
        // public static List<DataKey<Map<String, ClavaData>>> getDataKeys() {
        return DATA_PARSERS.keySet().stream()
                // .map(dataClass -> (DataKey<Map<String, ClavaData>>) getDataKey(dataClass))
                .map(dataClass -> getDataKey(dataClass))
                .collect(Collectors.toList());
    }

    /**
     * Helper method for building SnipperParser instances.
     * 
     * @param id
     * @param resultInit
     * @param dataParser
     * @return
     */
    // private static <D extends ClavaData> SnippetParser<Map<String, D>, Map<String, D>> newSnippetParser(
    // Class<D> clavaDataClass, Function<LineStream, D> dataParser) {
    private static SimpleSnippetParser<Map<String, ClavaData>> newSnippetParser(
            String id, Function<LineStream, ? extends ClavaData> dataParser, Map<String, ClavaData> clavaDataMap) {

        // String id = getNodeDataId(clavaDataClass);

        // Map where all ClavaData instances will be stored
        // Map<String, D> clavaDataMap = new HashMap<>();

        BiConsumer<LineStream, Map<String, ClavaData>> parser = (lineStream, map) -> ClavaDataParser
                .parseClavaDataTop(
                        dataParser,
                        lineStream, map);

        return SimpleSnippetParser.newInstance(id, clavaDataMap, parser);
    }

    /**
     * Creates the id for the given ClavaData class that corresponds to the id that will appear in the LineStream.
     * <p>
     * IMPORTANT: This method must return the same id that will appear in the LineStream, both the stream and this
     * method must agree.
     * 
     * @param clavaDataClass
     * @return
     */
    private static String getNodeDataId(Class<? extends ClavaData> clavaDataClass) {
        String simpleName = clavaDataClass.getSimpleName();

        // Simplify name
        simpleName = StringParsers.removeSuffix(simpleName, "V2");

        return "<" + simpleName + ">";
    }

    // Map with parsed ClavaData instances
    private final Map<String, ClavaData> clavaData;
    // ClavaData parsers
    private final Map<String, SimpleSnippetParser<Map<String, ClavaData>>> clavaDataParsers;

    public ClavaDataParser(Map<String, ClavaData> clavaData,
            Map<String, SimpleSnippetParser<Map<String, ClavaData>>> clavaDataParsers) {

        this.clavaData = clavaData;
        this.clavaDataParsers = clavaDataParsers;
    }

    /**
     * Private constructor.
     * 
     * @param parsers
     */
    // private ClavaDataParser(Map<DataKey<?>, SnippetParser<?, ?>> parsers) {
    // super(parsers);
    // }

    public static SourceRange parseLocation(LineStream lines) {
        // Next line will tell if is an invalid location or if to continue parsing
        String firstPart = lines.nextLine();

        if (firstPart.equals("<invalid>")) {
            return SourceRange.invalidRange();
        }

        // Filepaths will be shared between most nodes, intern them

        String startFilepath = firstPart.intern();
        // String startFilepath = firstPart;
        int startLine = Integer.parseInt(lines.nextLine());
        int startColumn = Integer.parseInt(lines.nextLine());

        SourceLocation startLocation = new SourceLocation(startFilepath, startLine, startColumn);

        // Check if start is the same as the end
        String secondPart = lines.nextLine();

        if (startFilepath.equals("<built-in>")) {
            Preconditions.checkArgument(secondPart.equals("<end>"));
            return SourceRange.invalidRange();
        }

        if (secondPart.equals("<end>")) {
            return new SourceRange(startLocation);
        }

        // Parser end location
        String endFilepath = secondPart.intern();
        // String endFilepath = secondPart;

        int endLine = Integer.parseInt(lines.nextLine());
        int endColumn = Integer.parseInt(lines.nextLine());

        SourceLocation endLocation = new SourceLocation(endFilepath, endLine, endColumn);
        return new SourceRange(startLocation, endLocation);
    }

    // public static <D extends ClavaData> void parseClavaDataTop(Function<LineStream, D> dataParser, LineStream lines,
    // Map<String, D> map) {
    public static void parseClavaDataTop(Function<LineStream, ? extends ClavaData> dataParser,
            LineStream lines,
            Map<String, ClavaData> map) {

        // TODO: Let ClavaNode parser read the key/id, and access it from clavaData.getId()
        // String key = lines.nextLine();
        // SourceRange location = ClavaDataParser.parseLocation(lines);

        ClavaData clavaData = dataParser.apply(lines);

        ClavaData previousValue = map.put(clavaData.getId(), clavaData);

        if (previousValue != null) {
            throw new RuntimeException("Duplicated parsing of node '" + clavaData.getId() + "'.\nPrevious value:"
                    + previousValue + "\nCurrent value:" + clavaData);
        }

    }

    public static ClavaData parseClavaData(LineStream lines) {
        return parseClavaData(lines, true);
    }

    public static ClavaData parseClavaData(LineStream lines, boolean hasLocation) {

        String id = lines.nextLine();

        SourceRange location = hasLocation ? parseLocation(lines) : SourceRange.invalidRange();
        boolean isMacro = hasLocation ? GeneralParsers.parseOneOrZero(lines) : false;
        SourceRange spellingLocation = isMacro ? parseLocation(lines) : SourceRange.invalidRange();

        return new ClavaData(id, location, isMacro, spellingLocation);
    }

    @Override
    public DataStore buildData() {
        DataStore data = DataStore.newInstance("ClavaData Parser Data");

        data.set(getDataKey(), clavaData);

        return data;
    }

    @Override
    public boolean parse(String id, LineStream lineStream) {
        // TODO: Replace with Java 10 var
        if (clavaDataParsers.get(id) == null) {
            return false;
        }

        clavaDataParsers.get(id).parse(lineStream);

        return true;
    }

    @Override
    public Collection<String> getIds() {
        return clavaDataParsers.keySet();
    }

}
