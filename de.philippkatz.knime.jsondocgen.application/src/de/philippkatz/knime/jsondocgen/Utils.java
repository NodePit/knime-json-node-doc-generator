package de.philippkatz.knime.jsondocgen;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

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

	static String getImageBase64(Image image) {
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { image.getImageData() };
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		imageLoader.save(stream, SWT.IMAGE_PNG);
		return new String(Base64.getEncoder().encode(stream.toByteArray()));
	}

	private Utils() {
		// nope!
	}

}
