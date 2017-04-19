package com.seleniumnodes.util.jsondocgenerator;

import java.util.ArrayList;
import java.util.List;

import org.knime.workbench.repository.model.IRepositoryObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class CategoryDoc {
	final String id;
	final String name;
	List<CategoryDoc> children;
	List<NodeDoc> nodes;

	public CategoryDoc(IRepositoryObject repositoryObject) {
		this.id = repositoryObject.getID();
		this.name = repositoryObject.getName();
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
