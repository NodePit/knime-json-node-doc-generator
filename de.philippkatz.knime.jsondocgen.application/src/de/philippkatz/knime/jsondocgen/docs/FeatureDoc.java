package de.philippkatz.knime.jsondocgen.docs;

public class FeatureDoc {
	public static final class FeatureDocBuilder {
		private String id;
		private String name;
		private String version;
		private String description;
		private String descriptionUrl;
		private String provider;
		private String contact;
		private String documentationUrl;
		private String license;
		private String licenseUrl;
		private String copyright;
		private String copyrightUrl;

		public FeatureDocBuilder setId(String id) {
			this.id = id;
			return this;
		}

		public FeatureDocBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public FeatureDocBuilder setVersion(String version) {
			this.version = version;
			return this;
		}

		public FeatureDocBuilder setDescription(String description) {
			this.description = description;
			return this;
		}

		public FeatureDocBuilder setDescriptionUrl(String descriptionUrl) {
			this.descriptionUrl = descriptionUrl;
			return this;
		}

		public FeatureDocBuilder setProvider(String provider) {
			this.provider = provider;
			return this;
		}

		public FeatureDocBuilder setContact(String contact) {
			this.contact = contact;
			return this;
		}

		public FeatureDocBuilder setDocumentationUrl(String documentationUrl) {
			this.documentationUrl = documentationUrl;
			return this;
		}

		public FeatureDocBuilder setLicense(String license) {
			this.license = license;
			return this;
		}

		public FeatureDocBuilder setLicenseUrl(String licenseUrl) {
			this.licenseUrl = licenseUrl;
			return this;
		}

		public FeatureDocBuilder setCopyright(String copyright) {
			this.copyright = copyright;
			return this;
		}

		public FeatureDocBuilder setCopyrightUrl(String copyrightUrl) {
			this.copyrightUrl = copyrightUrl;
			return this;
		}

		public FeatureDoc build() {
			return new FeatureDoc(this);
		}
	}

	final String id;
	final String name;
	final String version;
	final String description;
	final String descriptionUrl;
	final String provider;
	final String contact;
	final String documentationUrl;
	final String license;
	final String licenseUrl;
	final String copyright;
	final String copyrightUrl;

	private FeatureDoc(FeatureDocBuilder builder) {
		id = builder.id;
		name = builder.name;
		version = builder.version;
		description = builder.description;
		descriptionUrl = builder.descriptionUrl;
		provider = builder.provider;
		contact = builder.contact;
		documentationUrl = builder.documentationUrl;
		license = builder.license;
		licenseUrl = builder.licenseUrl;
		copyright = builder.copyright;
		copyrightUrl = builder.copyrightUrl;
	}

}
