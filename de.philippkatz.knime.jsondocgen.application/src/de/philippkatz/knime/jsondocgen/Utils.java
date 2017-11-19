package de.philippkatz.knime.jsondocgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Utils {

	public static String toJson(Object input) {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gson.toJson(input);
	}
	
	private Utils() {
		// nope!
	}

}
