package de.philippkatz.knime.jsondocgen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;

import de.philippkatz.knime.jsondocgen.docs.NodeDoc;

public class NodeDocJsonParserTest {
	@Test
	public void parsing_XML_with_plain_options() throws Exception {
		Document doc = readDoc("/FindElementsNodeFactory.xml");
		NodeDoc description = NodeDocJsonParser.parse(doc, (String) null);
		
		assertEquals("Manipulator",description.type);
		assertEquals(false, description.deprecated);

		assertEquals("Find Elements", description.name);
		assertEquals("Find WebElements.", description.description);

		assertEquals(7, description.options.size());
		assertEquals("Input", description.options.get(0).name);
		assertEquals("The input column providing the starting point where to search.",
				description.options.get(0).description);
		assertFalse(description.options.get(0).optional);

		assertEquals(1, description.inPorts.size());
		assertEquals(0, description.inPorts.get(0).index);
		assertEquals("WebDriver or WebElements", description.inPorts.get(0).name);
		assertEquals("Table with a column providing a WebDriver or WebElements in which to search",
				description.inPorts.get(0).description);

		assertEquals(1, description.outPorts.size());
		assertTrue(description.toJson().replaceAll("\\s+", " ").contains("\"name\": \"Find Elements\""));
		
		assertNull(description.interactiveView);
	}

	@Test
	public void parsing_XML_with_tab_options() throws Exception {
		Document doc = readDoc("/StartWebDriverNodeFactory.xml");
		NodeDoc description = NodeDocJsonParser.parse(doc, (String) null);
		assertEquals(2, description.optionTabs.size());
		assertEquals("Options", description.optionTabs.get(0).name);
	}

	private static Document readDoc(String resourcePath) throws Exception {
		Objects.requireNonNull(resourcePath, "resourcePath must not be null");
		try (InputStream resourceStream = NodeDocJsonParserTest.class.getResourceAsStream(resourcePath)) {
			Objects.requireNonNull(resourcePath, "resource for " + resourcePath + " not found");
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			return documentBuilder.parse(resourceStream);
		}
	}

}
