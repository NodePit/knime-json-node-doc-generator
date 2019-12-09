package de.philippkatz.knime.jsondocgen;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import de.philippkatz.knime.jsondocgen.docs.SplashIconDoc;

/**
 * Based on org.knime.product.KNIMESplashHandler
 *
 * @author pk
 */
final class SplashIconReader {

	private static final String SPLASH_EXTENSION_ID = "org.knime.product.splashExtension";

	private static final String ELEMENT_ID = "id";

	private static final String ELEMENT_ICON = "icon";

	private static final String ELEMENT_TOOLTIP = "tooltip";

	public static List<SplashIconDoc> readSplashIcons() {

		IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(SPLASH_EXTENSION_ID)
				.getExtensions();

		List<SplashIconDoc> iconDocs = new ArrayList<>();

		for (IExtension ext : extensions) {
			for (IConfigurationElement elem : ext.getConfigurationElements()) {
				SplashIconDoc.SplashIconDocBuilder builder = new SplashIconDoc.SplashIconDocBuilder();
				builder.setId(elem.getAttribute(ELEMENT_ID));
				builder.setContributingPlugin(elem.getNamespaceIdentifier());
				builder.setIcon(getSplashIconBase64(elem, 48));
				builder.setIcon24(getSplashIconBase64(elem, 24));
				builder.setIcon32(getSplashIconBase64(elem, 32));
				builder.setTooltip(elem.getAttribute(ELEMENT_TOOLTIP));
				iconDocs.add(builder.build());
			}
		}

		return iconDocs;
	}

	private static String getSplashIconBase64(IConfigurationElement splashExtension, int iconSize) {
		String iconImageFilePath = splashExtension.getAttribute(ELEMENT_ICON + iconSize);
		if (iconImageFilePath == null) {
			iconImageFilePath = splashExtension.getAttribute(ELEMENT_ICON);
		}

		// Abort if an icon attribute was not specified - which is weird since it is
		// required
		if (iconImageFilePath == null || iconImageFilePath.length() == 0) {
			return null;
		}

		// Create a corresponding image descriptor
		URL url = FileLocator.find(Platform.getBundle(splashExtension.getNamespaceIdentifier()),
				new Path(iconImageFilePath), null);
		if (url == null) {
			return null;
		}
		ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);

		// Abort if no corresponding image was found
		if (descriptor == null) {
			return null;
		}

		ImageData imageData = descriptor.getImageData(100);
		if (imageData == null) {
			return null;
		}
		Image image = new Image(Display.getDefault(), imageData);
		try {
			return Utils.getImageBase64(image);
		} finally {
			image.dispose();
		}
	}

	private SplashIconReader() {
		// don't even think about it!
	}

}
