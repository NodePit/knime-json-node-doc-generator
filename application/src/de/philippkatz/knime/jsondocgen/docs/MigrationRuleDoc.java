package de.philippkatz.knime.jsondocgen.docs;

public class MigrationRuleDoc {

	public static class MigrationRuleDocBuilder {

		private String originalNodeFactoryClass;
		private String replacementNodeFactoryClass;

		public MigrationRuleDocBuilder setOriginalNodeFactoryClass(String originalNodeFactoryClass) {
			this.originalNodeFactoryClass = originalNodeFactoryClass;
			return this;
		}

		public MigrationRuleDocBuilder setReplacementNodeFactoryClass(String replacementNodeFactoryClass) {
			this.replacementNodeFactoryClass = replacementNodeFactoryClass;
			return this;
		}

		public MigrationRuleDoc build() {
			return new MigrationRuleDoc(this);
		}

	}

	private final String originalNodeFactoryClass;

	private final String replacementNodeFactoryClass;

	MigrationRuleDoc(MigrationRuleDocBuilder migrationRuleInfoBuilder) {
		this.originalNodeFactoryClass = migrationRuleInfoBuilder.originalNodeFactoryClass;
		this.replacementNodeFactoryClass = migrationRuleInfoBuilder.replacementNodeFactoryClass;
	}

	public String getOriginalNodeFactoryClass() {
		return originalNodeFactoryClass;
	}

	public String getReplacementNodeFactoryClass() {
		return replacementNodeFactoryClass;
	}

}
