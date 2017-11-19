package de.philippkatz.knime.jsondocgen;

public class PluginDoc {
	public static final class PluginDocBuilder {
		private String name;
		private String symbolicName;
		private String version;
		private String vendor;

		public PluginDocBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public PluginDocBuilder setSymbolicName(String symbolicName) {
			this.symbolicName = symbolicName;
			return this;
		}

		public PluginDocBuilder setVersion(String version) {
			this.version = version;
			return this;
		}

		public PluginDocBuilder setVendor(String vendor) {
			this.vendor = vendor;
			return this;
		}

		public PluginDoc build() {
			return new PluginDoc(this);
		}
	}

	final String name;
	final String symbolicName;
	final String version;
	final String vendor;

	private PluginDoc(PluginDocBuilder builder) {
		this.name = builder.name;
		this.symbolicName = builder.symbolicName;
		this.version = builder.version;
		this.vendor = builder.vendor;
	}

	public String getName() {
		return name;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public String getVersion() {
		return version;
	}

	public String getVendor() {
		return vendor;
	}

}
