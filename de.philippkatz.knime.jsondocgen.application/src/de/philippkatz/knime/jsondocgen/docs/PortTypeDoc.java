package de.philippkatz.knime.jsondocgen.docs;

import static de.philippkatz.knime.jsondocgen.docs.AbstractDoc.copyOrNull;

import java.util.ArrayList;
import java.util.List;

public class PortTypeDoc {
	public static class PortTypeDocBuilder {
		private String name;
		private String objectClass;
		private String specClass;
		private String color;
		private boolean hidden;

		private List<String> ancestorObjectClasses;
		private List<String> descendantObjectClasses;

		private List<String> parentObjectClasses;
		private List<String> childrenObjectClasses;

		public PortTypeDocBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public PortTypeDocBuilder setObjectClass(String objectClass) {
			this.objectClass = objectClass;
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

		public PortTypeDocBuilder addAncestorObjectClass(String ancestorObjectClass) {
			if (ancestorObjectClasses == null) {
				ancestorObjectClasses = new ArrayList<>();
			}
			ancestorObjectClasses.add(ancestorObjectClass);
			return this;
		}

		public PortTypeDocBuilder addDescendantObjectClass(String descendantObjectClass) {
			if (descendantObjectClasses == null) {
				descendantObjectClasses = new ArrayList<>();
			}
			descendantObjectClasses.add(descendantObjectClass);
			return this;
		}

		public PortTypeDocBuilder addParentObjectClass(String parentObjectClass) {
			if (parentObjectClasses == null) {
				parentObjectClasses = new ArrayList<>();
			}
			parentObjectClasses.add(parentObjectClass);
			return this;
		}

		public PortTypeDocBuilder addChildrenObjectClass(String childrenObjectClass) {
			if (childrenObjectClasses == null) {
				childrenObjectClasses = new ArrayList<>();
			}
			childrenObjectClasses.add(childrenObjectClass);
			return this;
		}
		
		public PortTypeDoc build() {
			return new PortTypeDoc(this);
		}
	}

	public final String name;
	public final String objectClass;
	public final String specClass;
	public final String color;
	public final boolean hidden;

	public final List<String> ancestorObjectClasses;
	public final List<String> descendantObjectClasses;

	public final List<String> parentObjectClasses;
	public final List<String> childrenObjectClasses;

	private PortTypeDoc(PortTypeDocBuilder builder) {
		name = builder.name;
		objectClass = builder.objectClass;
		specClass = builder.specClass;
		color = builder.color;
		hidden = builder.hidden;
		ancestorObjectClasses = copyOrNull(builder.ancestorObjectClasses);
		descendantObjectClasses = copyOrNull(builder.descendantObjectClasses);
		parentObjectClasses = copyOrNull(builder.parentObjectClasses);
		childrenObjectClasses = copyOrNull(builder.childrenObjectClasses);
	}
}
