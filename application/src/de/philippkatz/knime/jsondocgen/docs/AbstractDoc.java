package de.philippkatz.knime.jsondocgen.docs;

import java.util.ArrayList;
import java.util.List;

import de.philippkatz.knime.jsondocgen.Utils;

public abstract class AbstractDoc {
	public static class AbstractDocBuilder {
		private String id;
		private String name;
		private String description;
		private String contributingPlugin;
		private String iconBase64;
		private String afterId;

		public AbstractDocBuilder setId(String id) {
			this.id = id;
			return this;
		}

		public AbstractDocBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public AbstractDocBuilder setDescription(String description) {
			this.description = description;
			return this;
		}

		public AbstractDocBuilder setContributingPlugin(String contributingPlugin) {
			this.contributingPlugin = contributingPlugin;
			return this;
		}

		public AbstractDocBuilder setIconBase64(String iconBase64) {
			this.iconBase64 = iconBase64;
			return this;
		}

		public AbstractDocBuilder setAfterId(String afterID) {
			this.afterId = afterID;
			return this;
		}
	}

	/** @deprecated JSON backwards compatibility; use {@link #id} */
	@Deprecated
	public final String identifier;
	public final String id;
	public final String name;
	/** @deprecated JSON backwards compatibility; use {@link #description} */
	@Deprecated
	public final String shortDescription;
	public final String description;
	public final String contributingPlugin;
	public final String iconBase64;
	public final String afterId;

	protected AbstractDoc(AbstractDocBuilder builder) {
		identifier = builder.id;
		id = builder.id;
		name = builder.name;
		description = builder.description;
		shortDescription = builder.description;
		contributingPlugin = builder.contributingPlugin;
		iconBase64 = builder.iconBase64;
		afterId = builder.afterId;
	}
	
	public String toJson() {
		return Utils.toJson(this);
	}
	
	/**
	 * Create a copy of the given list, return <code>null</code> when argument is
	 * <code>null</code>.
	 * 
	 * @param list
	 *            The list to copy.
	 * @return A copy of the list or <code>null</code>.
	 */
	static <T> List<T> copyOrNull(List<T> list) {
		return list != null ? new ArrayList<>(list) : null;
	}

}
