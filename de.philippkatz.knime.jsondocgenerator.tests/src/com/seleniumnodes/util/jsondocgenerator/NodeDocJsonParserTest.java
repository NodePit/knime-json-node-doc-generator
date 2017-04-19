package com.seleniumnodes.util.jsondocgenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;

public class NodeDocJsonParserTest {
	@Test
	public void parsing_XML_with_plain_options() throws Exception {
		Document doc = readDoc("/FindElementsNodeFactory.xml");
		NodeDoc description = NodeDocJsonParser.parse(doc, null);

		assertEquals("Find Elements", description.name);
		assertEquals("Find WebElements.", description.shortDescription);
		assertTrue(description.intro.contains("<table class=\"table table-striped table-bordered\">"));

		assertEquals(7, description.options.size());
		assertEquals("Input", description.options.get(0).name);
		assertEquals("The input column providing the starting point where to search.",
				description.options.get(0).description);

		assertEquals(1, description.inPorts.size());
		assertEquals(0, description.inPorts.get(0).index);
		assertEquals("WebDriver or WebElements", description.inPorts.get(0).name);
		assertEquals("Table with a column providing a WebDriver or WebElements in which to search",
				description.inPorts.get(0).description);

		assertEquals(1, description.outPorts.size());

		assertTrue(description.toJson().replaceAll("\\s+", " ").startsWith("{ \"name\": \"Find Elements\""));
	}

	@Test
	public void parsing_XML_with_tab_options() throws Exception {
		Document doc = readDoc("/StartWebDriverNodeFactory.xml");
		NodeDoc description = NodeDocJsonParser.parse(doc, null);
		assertEquals(2, description.optionTabs.size());
		assertEquals("Options", description.optionTabs.get(0).name);
	}

	private static Document readDoc(String resourcePath) throws Exception {
		Objects.requireNonNull(resourcePath, "resourcePath must not be null");
		String filePath = NodeDocJsonParser.class.getResource(resourcePath).getFile();
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		return documentBuilder.parse(filePath);
	}

}
