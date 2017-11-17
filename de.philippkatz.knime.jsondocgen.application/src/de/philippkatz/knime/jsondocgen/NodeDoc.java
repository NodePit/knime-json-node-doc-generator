package de.philippkatz.knime.jsondocgen;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** Intermediate object used for constructing the JSON and for testing. */
public final class NodeDoc {
	public static final class OptionTab {
		String name;
		List<Option> options;
		OptionTab() { /* package private */ }
	}
	public static final class Option {
		String name;
		String description;
		boolean optional;
		Option() { /* package private */ }
	}
	public static final class Port {
		int index;
		String name;
		String description;
		/** null in case of an outputPort. */
		Boolean optional;
		Port() { /* package private */ }
	}
	public static final class View {
		int index;
		String name;
		String description;
		View() { /* package private */ }
	}
	public static final class InteractiveView {
		String name;
		String description;
	}
	String identifier;
	String name;
	String shortDescription;
	String intro;
	List<OptionTab> optionTabs;
	List<Option> options;
	List<Port> inPorts;
	List<Port> outPorts;
	List<View> views;
	String icon;
	String type;
	boolean deprecated;
	InteractiveView interactiveView;
	NodeDoc(){ /* package private */ }

	public String toJson() {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gson.toJson(this);
	}

}
