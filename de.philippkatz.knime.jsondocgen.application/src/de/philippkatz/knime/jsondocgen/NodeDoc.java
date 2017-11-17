package de.philippkatz.knime.jsondocgen;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Intermediate object used for constructing the JSON and for testing. Use the
 * {@link NodeDocBuilder} for construction.
 */
public final class NodeDoc {
	
	public static final class NodeDocBuilder {
		private String identifier;
		private String name;
		private String shortDescription;
		private String intro;
		private List<OptionTab> optionTabs;
		private List<Option> options;
		private List<Port> inPorts;
		private List<Port> outPorts;
		private List<View> views;
		private String icon;
		private String type;
		private boolean deprecated;
		private InteractiveView interactiveView;
		public NodeDocBuilder setIdentifier(String identifier) {
			this.identifier = identifier;
			return this;
		}
		public NodeDocBuilder setName(String name) {
			this.name = name;
			return this;
		}
		public NodeDocBuilder setShortDescription(String shortDescription) {
			this.shortDescription = shortDescription;
			return this;
		}
		public NodeDocBuilder setIntro(String intro) {
			this.intro = intro;
			return this;
		}
		public NodeDocBuilder setOptionTabs(List<OptionTab> optionTabs) {
			this.optionTabs = optionTabs;
			return this;
		}
		public NodeDocBuilder setOptions(List<Option> options) {
			this.options = options;
			return this;
		}
		public NodeDocBuilder setInPorts(List<Port> inPorts) {
			this.inPorts = inPorts;
			return this;
		}
		public NodeDocBuilder setOutPorts(List<Port> outPorts) {
			this.outPorts = outPorts;
			return this;
		}
		public NodeDocBuilder setViews(List<View> views) {
			this.views = views;
			return this;
		}
		public NodeDocBuilder setIcon(String icon) {
			this.icon = icon;
			return this;
		}
		public NodeDocBuilder setType(String type) {
			this.type = type;
			return this;
		}
		public NodeDocBuilder setDeprecated(boolean deprecated) {
			this.deprecated = deprecated;
			return this;
		}
		public NodeDocBuilder setInteractiveView(InteractiveView interactiveView) {
			this.interactiveView = interactiveView;
			return this;
		}
		public NodeDocBuilder addOptionTab(OptionTab optionTab) {
			if (optionTabs == null) {
				optionTabs = new ArrayList<>();
			}
			optionTabs.add(optionTab);
			return this;
		}
		public NodeDocBuilder addView(View view) {
			if (views == null) {
				views = new ArrayList<>();
			}
			views.add(view);
			return this;
		}
		public NodeDoc build() {
			return new NodeDoc(this);
		}
	}
	
	public static final class OptionTab {
		final String name;
		final List<Option> options;
		/* package */ OptionTab(String name, List<Option> options) {
			this.name = name;
			this.options = options;
		}
	}
	public static final class Option {
		final String name;
		final String description;
		final boolean optional;
		/* package */ Option(String name, String description, boolean optional) {
			this.name = name;
			this.description = description;
			this.optional = optional;
		}
	}
	public static final class Port {
		final int index;
		final String name;
		final String description;
		/** null in case of an outputPort. */
		final Boolean optional;
		/* package */ Port(int index, String name, String description, Boolean optional) {
			this.index = index;
			this.name = name;
			this.description = description;
			this.optional = optional;
		}
	}
	public static final class View {
		final int index;
		final String name;
		final String description;
		/* package */ View(int index, String name, String description) {
			this.index = index;
			this.name = name;
			this.description = description;
		}
	}
	public static final class InteractiveView {
		final String name;
		final String description;
		/* package */ InteractiveView(String name, String description) {
			this.name = name;
			this.description = description;
		}
	}

	final String identifier;
	final String name;
	final String shortDescription;
	final String intro;
	final List<OptionTab> optionTabs;
	final List<Option> options;
	final List<Port> inPorts;
	final List<Port> outPorts;
	final List<View> views;
	final String icon;
	final String type;
	final boolean deprecated;
	final InteractiveView interactiveView;

	private NodeDoc(NodeDocBuilder builder) {
		identifier = builder.identifier;
		name = builder.name;
		shortDescription = builder.shortDescription;
		intro = builder.intro;
		optionTabs = copyOrNull(builder.optionTabs);
		options = copyOrNull(builder.options);
		inPorts = copyOrNull(builder.inPorts);
		outPorts = copyOrNull(builder.outPorts);
		views = copyOrNull(builder.views);
		icon = builder.icon;
		type = builder.type;
		deprecated = builder.deprecated;
		interactiveView = builder.interactiveView;
	}

	/**
	 * Create a copy of the given list, return <code>null</code> when argument is
	 * <code>null</code>.
	 * 
	 * @param list
	 *            The list to copy.
	 * @return A copy of the list or <code>null</code>.
	 */
	private static <T> List<T> copyOrNull(List<T> list) {
		return list != null ? new ArrayList<>(list) : null;
	}

	public String toJson() {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gson.toJson(this);
	}

}
