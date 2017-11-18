package de.philippkatz.knime.jsondocgen;

import java.util.ArrayList;
import java.util.List;

/**
 * Intermediate object used for constructing the JSON and for testing. Use the
 * {@link NodeDocBuilder} for construction.
 */
public final class NodeDoc extends AbstractDoc {
	
	public static final class NodeDocBuilder extends AbstractDocBuilder {
		private String intro;
		private List<OptionTab> optionTabs;
		private List<Option> options;
		private List<Port> inPorts;
		private List<Port> outPorts;
		private List<View> views;
		private String type;
		private boolean deprecated;
		private InteractiveView interactiveView;
		private boolean streamable;
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
		public NodeDocBuilder setStreamable(boolean streamable) {
			this.streamable = streamable;
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

	final String intro;
	final List<OptionTab> optionTabs;
	final List<Option> options;
	final List<Port> inPorts;
	final List<Port> outPorts;
	final List<View> views;
	final String type;
	final boolean deprecated;
	final InteractiveView interactiveView;
	final boolean streamable;

	private NodeDoc(NodeDocBuilder builder) {
		super(builder);
		intro = builder.intro;
		optionTabs = copyOrNull(builder.optionTabs);
		options = copyOrNull(builder.options);
		inPorts = copyOrNull(builder.inPorts);
		outPorts = copyOrNull(builder.outPorts);
		views = copyOrNull(builder.views);
		type = builder.type;
		deprecated = builder.deprecated;
		interactiveView = builder.interactiveView;
		streamable = builder.streamable;
	}
}
