package de.philippkatz.knime.jsondocgen.docs;

public class SplashIconDoc {

	public static class SplashIconDocBuilder {
		private String id;
		private String contributingPlugin;
		private String tooltip;
		private String icon;
		private String icon24;
		private String icon32;

		public SplashIconDocBuilder setId(String id) {
			this.id = id;
			return this;
		}

		public SplashIconDocBuilder setContributingPlugin(String contributingPlugin) {
			this.contributingPlugin = contributingPlugin;
			return this;
		}

		public SplashIconDocBuilder setTooltip(String tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		public SplashIconDocBuilder setIcon(String icon) {
			this.icon = icon;
			return this;
		}

		public SplashIconDocBuilder setIcon24(String icon24) {
			this.icon24 = icon24;
			return this;
		}

		public SplashIconDocBuilder setIcon32(String icon32) {
			this.icon32 = icon32;
			return this;
		}

		public SplashIconDoc build() {
			return new SplashIconDoc(this);
		}

	}

	public final String id;
	public final String contributingPlugin;
	public final String tooltip;
	public final String icon;
	public final String icon24;
	public final String icon32;

	private SplashIconDoc(SplashIconDocBuilder builder) {
		id = builder.id;
		contributingPlugin = builder.contributingPlugin;
		tooltip = builder.tooltip;
		icon = builder.icon;
		icon24 = builder.icon24;
		icon32 = builder.icon32;
	}

}
