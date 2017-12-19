package de.philippkatz.knime.jsondocgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Utils {

	public static String toJson(Object input) {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gson.toJson(input);
	}

	static String trim(String string) {
		return string != null ? string.trim() : null;
	}

	static String stringOrNull(String string) {
		return string == null || string.isEmpty() ? null : string;
	}

	private Utils() {
		// nope!
	}

}
