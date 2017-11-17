package de.philippkatz.knime.jsondocgen;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class CategoryDoc {

	public static final class CategoryDocBuilder {

		private String identifier;
		private String name;
		private String description;
		private String contributingPlugin;
		private String iconBase64;

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

		public CategoryDoc build() {
			return new CategoryDoc(this);
		}

	}

	final String id;
	final String name;
	final String description;
	final String contributingPlugin;
	final String iconBase64;
	List<CategoryDoc> children;
	List<NodeDoc> nodes;

	private CategoryDoc(CategoryDocBuilder builder) {
		this.id = builder.identifier;
		this.name = builder.name;
		this.description = builder.description;
		this.contributingPlugin = builder.contributingPlugin;
		this.iconBase64 = builder.iconBase64;
	}

	public void addChild(CategoryDoc child) {
		if (children == null) {
			children = new ArrayList<>();
		}
		children.add(child);
	}

	public void addNode(NodeDoc node) {
		if (nodes == null) {
			nodes = new ArrayList<>();
		}
		nodes.add(node);
	}

	public String toJson() {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gson.toJson(this);
	}

}
