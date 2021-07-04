package de.philippkatz.knime.jsondocgen.docs;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PortTypeDoc {

	/** Use {@link PortTypeDoc#builderForObjectClass(String)} for instantiation. */
	public static class PortTypeDocBuilder {
		private final String objectClass;
		private String name;
		private String specClass;
		private String color;
		private boolean hidden;
		private boolean registered;
		private Set<PortTypeDocBuilder> children;
		private String contributingPlugin;

		private PortTypeDocBuilder(String objectClass) {
			this.objectClass = objectClass;
		}

		public PortTypeDocBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public PortTypeDocBuilder setSpecClass(String specClass) {
			this.specClass = specClass;
			return this;
		}

		public PortTypeDocBuilder setColor(String color) {
			this.color = color;
			return this;
		}

		public PortTypeDocBuilder setHidden(boolean hidden) {
			this.hidden = hidden;
			return this;
		}

		/**
		 * Adds a child if it is not already present (determined by 'objectClass').
		 * 
		 * @param child
		 *            The child to add.
		 * @return The builder instance.
		 */
		public PortTypeDocBuilder addChild(PortTypeDocBuilder child) {
			if (children == null) {
				children = new LinkedHashSet<>();
			}
			children.add(child);
			return this;
		}

		public PortTypeDocBuilder setRegistered(boolean registered) {
			this.registered = registered;
			return this;
		}

		public PortTypeDocBuilder setContributingPlugin(String contributingPlugin) {
			this.contributingPlugin = contributingPlugin;
			return this;
		}

		public PortTypeDoc build() {
			return new PortTypeDoc(this);
		}

		// hashCode + equals determined by its objectClass
		// (necessary to avoid inserting duplicate children)

		@Override
		public int hashCode() {
			return objectClass.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof PortTypeDocBuilder)) {
				return false;
			}
			PortTypeDocBuilder other = (PortTypeDocBuilder) obj;
			return objectClass.equals(other.objectClass);
		}

	}

	/**
	 * Return a new builder.
	 * 
	 * @param objectClass
	 *            The objectClass of the PortType.
	 * @return A new builder instance.
	 */
	public static PortTypeDocBuilder builderForObjectClass(String objectClass) {
		return new PortTypeDocBuilder(objectClass);
	}

	public final String name;
	public final String objectClass;
	public final String specClass;
	public final String color;
	public final boolean hidden;
	public final boolean registered;
	public final Set<PortTypeDoc> children;
	/** @since 1.11 */
	public final String contributingPlugin;

	private PortTypeDoc(PortTypeDocBuilder builder) {
		name = builder.name;
		objectClass = builder.objectClass;
		specClass = builder.specClass;
		color = builder.color;
		hidden = builder.hidden;
		registered = builder.registered;
		children = buildChildren(builder);
		contributingPlugin = builder.contributingPlugin;
	}

	private static Set<PortTypeDoc> buildChildren(PortTypeDocBuilder builder) {
		if (builder.children == null) {
			return null;
		}
		return builder.children.stream().map(PortTypeDocBuilder::build).collect(Collectors.toSet());
	}
}
