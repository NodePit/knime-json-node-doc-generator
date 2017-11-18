package de.philippkatz.knime.jsondocgen;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class CategoryDoc {

	public static final class CategoryDocBuilder {

		private String identifier;
		private String name;
		private String description;
		private String contributingPlugin;
		private String iconBase64;
		private List<CategoryDoc> children;
		private List<NodeDoc> nodes;

		public CategoryDocBuilder setIdentifier(String identifier) {
			this.identifier = identifier;
			return this;
		}

		public CategoryDocBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public CategoryDocBuilder setDescription(String description) {
			this.description = description;
			return this;
		}

		public CategoryDocBuilder setContributingPlugin(String contributingPlugin) {
			this.contributingPlugin = contributingPlugin;
			return this;
		}

		public CategoryDocBuilder setIconBase64(String iconBase64) {
			this.iconBase64 = iconBase64;
			return this;
		}
		
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

	final String id;
	final String name;
	final String description;
	final String contributingPlugin;
	final String iconBase64;
	final List<CategoryDoc> children;
	final List<NodeDoc> nodes;

	private CategoryDoc(CategoryDocBuilder builder) {
		this.id = builder.identifier;
		this.name = builder.name;
		this.description = builder.description;
		this.contributingPlugin = builder.contributingPlugin;
		this.iconBase64 = builder.iconBase64;
		this.children = NodeDoc.copyOrNull(builder.children);
		this.nodes = NodeDoc.copyOrNull(builder.nodes);
	}

	public String toJson() {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gson.toJson(this);
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
