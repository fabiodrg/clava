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

package pt.up.fe.specs.clava;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.clava.ast.ClavaNodeFactory;
import pt.up.fe.specs.clava.ast.DataStoreToLegacy;
import pt.up.fe.specs.clava.ast.LegacyToDataStore;
import pt.up.fe.specs.clava.ast.comment.InlineComment;
import pt.up.fe.specs.clava.ast.expr.Expr;
import pt.up.fe.specs.clava.ast.extra.App;
import pt.up.fe.specs.clava.ast.extra.TranslationUnit;
import pt.up.fe.specs.clava.ast.stmt.CompoundStmt;
import pt.up.fe.specs.clava.context.ClavaContext;
import pt.up.fe.specs.clava.context.ClavaFactory;
import pt.up.fe.specs.clava.utils.NullNode;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsStrings;
import pt.up.fe.specs.util.treenode.ATreeNode;
import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

public abstract class ClavaNode extends ATreeNode<ClavaNode> {

    /// DATAKEYS BEGIN

    /**
     * Global object with information about the program.
     * 
     * TODO: Should this key be moved to another class?
     */
    public final static DataKey<ClavaContext> CONTEXT = KeyFactory.object("context", ClavaContext.class);

    /**
     * Id of the node.
     */
    public final static DataKey<String> ID = KeyFactory.string("id");

    /**
     * Location of this node. Might not be available (e.g., type nodes).
     */
    public final static DataKey<SourceRange> LOCATION = KeyFactory
            .object("location", SourceRange.class)
            .setDefault(() -> SourceRange.invalidRange());

    /**
     * If this node is part of a macro.
     */
    public final static DataKey<Boolean> IS_MACRO = KeyFactory.bool("isMacro");

    public final static DataKey<SourceRange> SPELLING_LOCATION = KeyFactory
            .object("spellingLocation", SourceRange.class)
            .setDefault(() -> SourceRange.invalidRange());

    public final static DataKey<List<InlineComment>> INLINE_COMMENTS = KeyFactory
            .generic("inlineComments", (List<InlineComment>) new ArrayList<InlineComment>())
            .setDefault(() -> new ArrayList<>())
            .setCopyFunction(inlineComments -> new ArrayList<>(inlineComments));

    /**
     * True if this node was created using legacy parser classes.
     */
    public final static DataKey<Boolean> IS_LEGACY_NODE = KeyFactory.bool("isLegacyNode");

    /// DATAKEYS END

    public static String toTree(Collection<? extends ClavaNode> nodes) {
        return nodes.stream().map(ClavaNode::toTree).collect(Collectors.joining("\n"));
    }

    private DataStore dataI;

    public ClavaNode(ClavaNodeInfo nodeInfo, Collection<? extends ClavaNode> children) {
        this(new LegacyToDataStore().setNodeInfo(nodeInfo).getData(), children);
    }

    public ClavaNode(DataStore dataI, Collection<? extends ClavaNode> children) {
        super(children);

        this.dataI = dataI;

        // Set definition of DataStore
        this.dataI.setDefinition(getClass());

    }

    protected String getTab() {
        return "   ";
    }

    public String indentCode(String code) {
        return ClavaNodes.indentCode(getTab(), code);
    }

    @Override
    public String toContentString() {
        return getData().toInlinedString();
    }

    protected static String toContentString(String previousContentString, String suffix) {

        // Use bridge if there is content and a suffix
        String bridge = !suffix.isEmpty() && !previousContentString.isEmpty() ? ", " : "";

        return previousContentString + bridge + suffix;
    }

    @Override
    public ClavaNode getThis() {
        return this;
    }

    public String getCode() {
        return toUnimplementedCode();
    }

    public SourceRange getLocation() {
        return get(LOCATION);
    }

    /**
     * TODO: Make method private when all accesses are "fixed"
     * 
     * @deprecated use .getExtendedId() instead
     * @return
     */
    @Deprecated
    public Optional<ClavaId> getClavaId() {
        return getInfo().getId();
    }

    public Optional<String> getExtendedId() {
        return Optional.of(getId());
    }

    public String getId() {
        return get(ID);
    }

    /**
     * TODO: Make method protected when all accesses are "fixed"
     * 
     * @deprecated
     * @return
     */
    @Deprecated
    public ClavaNodeInfo getInfo() {
        return DataStoreToLegacy.getNodeInfo(getData());
    }

    @Override
    public ClavaNodeIterator getChildrenIterator() {
        return new ClavaNodeIterator(this);
    }

    public App getApp() {
        return getAppTry()
                .orElseThrow(() -> new RuntimeException("Node does not have a parent: " + this));
    }

    public Optional<App> getAppTry() {
        ClavaNode root = this;
        while (root.hasParent()) {
            root = root.getParent();
        }

        if (!(root instanceof App)) {
            return Optional.empty();
        }

        return Optional.of((App) root);
    }

    /**
     * Helper method which generates template code for nodes that have not implemented yet getCode().
     *
     * @param node
     * @return
     */
    private String toUnimplementedCode() {
        return ClavaNodes.toCode("NOT IMPLEMENTED: " + getClass().getSimpleName(), this);
    }

    protected static List<ClavaNode> sanitize(ClavaNodeInfo info, ClavaNode... nodes) {
        return sanitize(info, Arrays.asList(nodes));
    }

    /**
     * Replaces null elements with NullNodes with the given info.
     *
     * @param info
     * @param nodes
     * @return
     */
    protected static List<ClavaNode> sanitize(ClavaNodeInfo info, Collection<? extends ClavaNode> nodes) {
        return nodes.stream()
                .map(node -> node == null ? ClavaNodeFactory.nullNode(info) : node)
                .collect(Collectors.toList());
    }

    /**
     * Helper method that returns a child that can be a NullNode. Ignores WrapperStmt, Pragma and Comment nodes.
     *
     * @param index
     * @param castTo
     * @return
     */
    protected <T extends ClavaNode> Optional<T> getNullable(int index, Class<T> castTo) {
        // Get child
        ClavaNode child = ClavaNodes.getChild(this, index);

        // If NullNode return empty Optional, otherwise cast and return optional
        return child instanceof pt.up.fe.specs.clava.ast.extra.NullNode ? Optional.empty()
                : Optional.of(castTo.cast(child));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     *
     * @return true if the node is a 'wrapper' node. Examples of wrapper nodes: WrapperStmt
     */
    public boolean isWrapper() {
        return false;
    }

    public String toJsonContents() {
        BuilderWithIndentation builder = new BuilderWithIndentation();

        // Always has a name
        builder.addLine("\"name\": \"" + getNodeName() + "\",");

        // Id
        builder.addLine("\"id\": \"" + getExtendedId().orElse("<no id>") + "\",");

        // Content
        builder.addLine("\"content\": \"" + SpecsStrings.escapeJson(toContentString()) + "\",");

        // Location
        builder.addLine("\"location\": \""
                + SpecsStrings.escapeJson(getLocationTry().map(loc -> loc.toString()).orElse("<no_location>"))
                + "\",");

        // Field above has ',' because the last field of the JSON is always the children

        return builder.toString();
    }

    public Optional<SourceRange> getLocationTry() {
        return Optional.of(get(LOCATION));
    }

    /**
     * @deprecated
     * @param expr
     * @return
     */
    @Deprecated
    protected static Expr nullable(Expr expr) {
        return expr == null ? LegacyToDataStore.getFactory().nullExpr() : expr;
    }

    protected void throwNoCodeGeneration() {
        throw new RuntimeException(
                "Code generation not implemented because the code for this case has not been tested yet");
    }

    protected String ln() {
        return ClavaNodes.ln();
    }

    /**
     * Associates a comment to this node.
     *
     * @param inlineComment
     */
    public void associateComment(InlineComment inlineComment) {
        addInlineComment(inlineComment);
    }

    private void addInlineComment(InlineComment inlineComment) {
        Preconditions.checkArgument(!inlineComment.isStmtComment(),
                "InlineComment must not be a statement comment:" + inlineComment);

        getData().get(INLINE_COMMENTS).add(inlineComment);
    }

    @Override
    public ClavaNode copy() {
        return super.copy();
    }

    @Override
    protected ClavaNode copyPrivate() {
        return newInstance(getClass(), Collections.emptyList());
    }

    public boolean hasInlineComments() {
        return getInlineComments().isEmpty();
    }

    public List<InlineComment> getInlineComments() {
        return get(INLINE_COMMENTS);
    }

    /**
     *
     * @return a new list with the removed comments
     */
    public List<InlineComment> removeInlineComments() {

        List<InlineComment> comments = getInlineComments();
        List<InlineComment> copy = new ArrayList<>(comments);
        comments.clear();
        return copy;
    }

    public String getInlineCommentsCode() {
        List<InlineComment> inlineComments = getInlineComments();
        if (inlineComments.isEmpty()) {
            return "";
        }

        String code = getInlineComments().stream()
                .map(InlineComment::getCode)
                .collect(Collectors.joining(" ", " ", ""));

        return code;
    }

    /**
     * Default implementation tests the name of the node, for the prefix "CXX".
     *
     * @return true, if the current node only appears in C++ applications.
     */
    public boolean isCxxNode() {
        return getNodeName().startsWith("CXX");
    }

    public List<ClavaNode> getChildrenNormalized() {
        return getChildren().stream().map(ClavaNodes::normalize).collect(Collectors.toList());
    }

    public ClavaNode setId(String newId) {
        put(ID, newId);
        return this;
    }

    public ClavaNode setLocation(SourceRange location) {
        put(LOCATION, location);
        return this;
    }

    /**
     * 
     * @return the CompoundStmt this node belongs to, or TranslationUnit if the scope is global. A node might not have
     *         scope (e.g., if it is detached from the AST)
     */
    public Optional<ClavaNode> getScope() {
        ClavaNode currentNode = this;
        while (currentNode.hasParent()) {
            ClavaNode parent = currentNode.getParent();

            if (parent instanceof CompoundStmt || parent instanceof TranslationUnit) {
                return Optional.of(parent);
            }

            currentNode = parent;
        }

        return Optional.empty();
    }

    /**
     * Returns the DataStore associated with this node.
     * 
     * <p>
     * Generically, it is not recommended that this method is used outside of ClavaNode classes. <br>
     * Instead, the method .get() / specific setters should be used. <br>
     * However, there might be situations where access this object is needed (e.g., a setter that is not implemented
     * yet).
     * 
     * 
     * @return the underlying DataStore of this node
     */
    public DataStore getData() {
        return dataI;
    }

    public void setData(DataStore data) {
        this.dataI.addAll(data);
    }

    public boolean hasDataI() {
        return dataI != null;
    }

    public Optional<String> getIdSuffix() {
        if (!getExtendedId().isPresent()) {
            return Optional.empty();
        }

        String id = getExtendedId().get();

        if (id == null) {
            return Optional.empty();
        }

        int startIndex = id.lastIndexOf('_');
        if (startIndex == -1) {
            SpecsLogs.msgWarn("Could not find '_' in the id: " + id);
            return Optional.empty();
        }

        return Optional.of(id.substring(startIndex));
    }

    public boolean isNullNode() {
        return this instanceof NullNode;
    }

    public String toTree() {
        return super.toString();
    }

    @Override
    public String toString() {
        return toContentString();
    }

    /**
     * 
     * @return a ClavaFactory where the builders will use the data of this node as the base for new nodes.
     */
    public ClavaFactory getFactoryWithNode() {
        return new ClavaFactory(get(CONTEXT), dataI);
    }

    public ClavaFactory getFactory() {
        return get(CONTEXT).get(ClavaContext.FACTORY);
    }

    /**
     * General getter for ClavaNode fields.
     * 
     * @param key
     * @return
     */
    public <T> T get(DataKey<T> key) {
        return dataI.get(key);
    }

    /**
     * Internal method for setting values.
     * 
     * @param key
     * @param value
     */
    protected <T, E extends T> ClavaNode put(DataKey<T> key, E value) {
        dataI.put(key, value);

        return this;
    }

    public ClavaContext getContext() {
        return get(CONTEXT);
    }

    /**
     * Creates a new node using the same data as this node.
     * 
     * @param nodeClass
     * @param children
     * @return
     */
    public <T extends ClavaNode> T newInstance(Class<T> nodeClass, List<ClavaNode> children) {

        DataStore newDataStore = dataI.copy();

        // Set id
        String newId = get(CONTEXT).get(ClavaContext.ID_GENERATOR).next("from" + getClass().getSimpleName() + "_");
        newDataStore.put(ID, newId);

        try {
            Constructor<? extends ClavaNode> constructorMethod = nodeClass.getConstructor(DataStore.class,
                    Collection.class);

            try {
                return nodeClass.cast(constructorMethod.newInstance(newDataStore, children));
            } catch (Exception e) {
                throw new RuntimeException("Could not call constructor for ClavaNode", e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not create constructor for ClavaNode:" + e.getMessage());
        }
    }

    /**
     * Legacy support.
     * 
     * When all types have DataStore, we can use node.getFactoryWithNode()
     * 
     * @deprecated getFactoryWithNode()
     * @param node
     */
    @Deprecated
    public void setNodeData(ClavaNode node) {
        node.setId(getExtendedId().get());
        node.setLocation(getLocation());
    }

    public ClavaNode setIsLegacyNode(boolean isLegacyNode) {
        getData().set(IS_LEGACY_NODE, isLegacyNode);
        return this;
    }
}
