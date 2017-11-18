package de.philippkatz.knime.jsondocgen;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class CategoryDoc extends AbstractDoc {

	public static final class CategoryDocBuilder extends AbstractDocBuilder {

		private List<CategoryDoc> children;
		private List<NodeDoc> nodes;
		
		public CategoryDocBuilder addChild(CategoryDoc child) {
			if (children == null) {
				children = new ArrayList<>();
			}
			children.add(child);
			return this;
		}

		public CategoryDocBuilder addNode(NodeDoc node) {
			if (nodes == null) {
				nodes = new ArrayList<>();
			}
			nodes.add(node);
			return this;
		}		

		public CategoryDoc build() {
			return new CategoryDoc(this);
		}

	}

	final List<CategoryDoc> children;
	final List<NodeDoc> nodes;

	private CategoryDoc(CategoryDocBuilder builder) {
		super(builder);
		this.children = copyOrNull(builder.children);
		this.nodes = copyOrNull(builder.nodes);
	}
	
	/**
	 * Recursively retrieves all plugin IDs of all contained categories and nodes.
	 * 
	 * @return Set with all plugin IDs.
	 */
	public Set<String> getAllContributingPlugins() {
		Set<String> result = new LinkedHashSet<>();
		result.add(contributingPlugin);
		if (children != null) {
			children.forEach(categoryDoc -> result.addAll(categoryDoc.getAllContributingPlugins()));
		}
		if (nodes != null) {
			nodes.forEach(nodeDoc -> result.add(nodeDoc.contributingPlugin));
		}
		return result;
	}

}
