/*
 * Copyright 2012 Alessandro Bahgat Shehata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.mezzdev.suffixtree;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMaps;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * Represents a node of the generalized suffix tree graph
 *
 * @see GeneralizedSuffixTree
 */
class Node<T> {

	/**
	 * The payload array used to store the data (indexes) associated with this node.
	 * In this case, it is used to store all property indexes.
	 */
	private Collection<T> data;

	/**
	 * The set of edges starting from this node
	 */
	private Char2ObjectMap<Edge<T>> edges;

	/**
	 * The suffix link as described in Ukkonen's paper.
	 * if str is the string denoted by the path from the root to this, this.suffix
	 * is the node denoted by the path that corresponds to str without the first char.
	 */
	@Nullable
	private Node<T> suffix;

	/**
	 * Creates a new Node
	 */
	Node() {
		edges = Char2ObjectMaps.emptyMap();
		data = List.of();
		suffix = null;
	}

	/**
	 * Gets data from the payload of both this node and its children, the string representation
	 * of the path to this node is a substring of the one of the children nodes.
	 *
	 * @param resultsConsumer  a consumer that accepts the resulting data
	 */
	public void getData(Consumer<Collection<T>> resultsConsumer) {
		resultsConsumer.accept(Collections.unmodifiableCollection(this.data));
		for (Edge<T> edge : edges.values()) {
			Node<T> dest = edge.dest();
			dest.getData(resultsConsumer);
		}
	}

	/**
	 * Adds the given <tt>index</tt> to the set of indexes associated with <tt>this</tt>
	 * returns false if this node already contains the ref
	 */
	void addRef(T value) {
		if (contains(value)) {
			return;
		}

		addValue(value);

		// add this reference to all the suffixes as well
		Node<T> iter = this.suffix;
		while (iter != null) {
			if (!iter.contains(value)) {
				iter.addValue(value);
				iter = iter.suffix;
			} else {
				break;
			}
		}
	}

	/**
	 * Tests whether a node contains a reference to the given index.
	 *
	 * @param value the value to look for
	 * @return true if this contains the value
	 */
	protected boolean contains(T value) {
		return data.contains(value);
	}

	void addEdge(Edge<T> edge) {
		char firstChar = edge.label().charAt(0);

		switch (edges.size()) {
			case 0 -> edges = Char2ObjectMaps.singleton(firstChar, edge);
			case 1 -> {
				edges = new Char2ObjectOpenHashMap<>(edges);
				edges.put(firstChar, edge);
			}
			default -> edges.put(firstChar, edge);
		}
	}

	@Nullable
	Edge<T> getEdge(char ch) {
		return edges.get(ch);
	}

	@Nullable
	Edge<T> getEdge(SubString string) {
		if (string.isEmpty()) {
			return null;
		}
		char ch = string.charAt(0);
		return edges.get(ch);
	}

	@Nullable
	Node<T> getSuffix() {
		return suffix;
	}

	void setSuffix(Node<T> suffix) {
		this.suffix = suffix;
	}

	/**
	 * Add a new value to this {@code Node}'s data.
	 * @param value the value to add
	 */
	protected void addValue(T value) {
		switch (data.size()) {
			case 0 -> data = List.of(value);
			case 1 -> data = List.of(data.iterator().next(), value);
			case 2 -> {
				List<T> newData = new ArrayList<>(4);
				newData.addAll(data);
				newData.add(value);
				data = newData;
			}
			case 16 -> {
				// "upgrade" data to a Set once it's getting bigger,
				// to improve its `contains` performance.
				Collection<T> newData = Collections.newSetFromMap(new IdentityHashMap<>());
				newData.addAll(data);
				newData.add(value);
				data = newData;
			}
			default -> data.add(value);
		}
	}

	@Override
	public String toString() {
		return "Node: size:" + data.size() + " Edges: " + edges;
	}

	/**
	 * @return debug statistics for this node's size and the size of all its descendants.
	 */
	public IntSummaryStatistics nodeSizeStats() {
		return nodeSizes().summaryStatistics();
	}

	private IntStream nodeSizes() {
		return IntStream.concat(
				IntStream.of(data.size()),
				edges.values().stream().flatMapToInt(e -> e.dest().nodeSizes())
		);
	}

	/**
	 * @return debug statistics for this node's edges and the edges of all its descendants.
	 */
	public String nodeEdgeStats() {
		IntSummaryStatistics edgeCounts = nodeEdgeCounts().summaryStatistics();
		IntSummaryStatistics edgeLengths = nodeEdgeLengths().summaryStatistics();
		return "Edge counts: " + edgeCounts +
				"\nEdge lengths: " + edgeLengths;
	}

	private IntStream nodeEdgeCounts() {
		return IntStream.concat(
				IntStream.of(edges.size()),
				edges.values().stream().map(Edge::dest).flatMapToInt(Node::nodeEdgeCounts)
		);
	}

	private IntStream nodeEdgeLengths() {
		return IntStream.concat(
				edges.values().stream().map(Edge::label).mapToInt(SubString::length),
				edges.values().stream().map(Edge::dest).flatMapToInt(Node::nodeEdgeLengths)
		);
	}

	/**
	 * Debug function for printing this {@code Node} in a format usable by <a href="https://graphviz.org/">graphviz</a>.
	 *
	 * @param out  the print writer to output to.
	 * @param includeSuffixLinks  whether to include suffix links in the output graph.
	 * @see GeneralizedSuffixTree#printTree
	 */
	public void printTree(PrintWriter out, boolean includeSuffixLinks) {
		out.println("digraph {");
		out.println("\trankdir = LR;");
		out.println("\tordering = out;");
		out.println("\tedge [arrowsize=0.4,fontsize=10]");
		out.println("\t" + nodeId(this) + " [label=\"\",style=filled,fillcolor=lightgrey,shape=circle,width=.1,height=.1];");
		out.println("//------leaves------");
		printLeaves(out);
		out.println("//------internal nodes------");
		printInternalNodes(this, out);
		out.println("//------edges------");
		printEdges(out);
		if (includeSuffixLinks) {
			out.println("//------suffix links------");
			printSLinks(out);
		}
		out.println("}");
	}

	private void printLeaves(PrintWriter out) {
		if (edges.size() == 0) {
			out.println("\t" + nodeId(this) + " [label=\"" + data + "\",shape=point,style=filled,fillcolor=lightgrey,shape=circle,width=.07,height=.07]");
		} else {
			for (Edge<T> edge : edges.values()) {
				edge.dest().printLeaves(out);
			}
		}
	}

	private void printInternalNodes(Node<T> root, PrintWriter out) {
		if (this != root && edges.size() > 0) {
			out.println("\t" + nodeId(this) + " [label=\"" + data + "\",style=filled,fillcolor=lightgrey,shape=circle,width=.07,height=.07]");
		}

		for (Edge<T> edge : edges.values()) {
			edge.dest().printInternalNodes(root, out);
		}
	}

	private void printEdges(PrintWriter out) {
		for (Edge<T> edge : edges.values()) {
			Node<T> child = edge.dest();
			out.println("\t" + nodeId(this) + " -> " + nodeId(child) + " [label=\"" + edge.label() + "\",weight=10]");
			child.printEdges(out);
		}
	}

	private void printSLinks(PrintWriter out) {
		if (suffix != null) {
			out.println("\t" + nodeId(this) + " -> " + nodeId(suffix) + " [label=\"\",weight=0,style=dotted]");
		}
		for (Edge<T> edge : edges.values()) {
			edge.dest().printSLinks(out);
		}
	}

	public Collection<T> getData() {
		return data;
	}

	public Char2ObjectMap<Edge<T>> getEdges() {
		return edges;
	}

	private static <T> String nodeId(Node<T> node) {
		return "node" + Integer.toHexString(node.hashCode()).toUpperCase();
	}
}
