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
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.Option;

public class NodeDocJsonParserTest {
	@Test
	public void parsing_XML_with_plain_options() throws Exception {
		Document doc = readDoc("/FindElementsNodeFactory.xml");
		NodeDoc nodeDoc = NodeDocJsonParser.parse(doc);
		
		assertEquals("Manipulator",nodeDoc.type);
		assertEquals(false, nodeDoc.deprecated);

		assertEquals("Find Elements", nodeDoc.name);
		assertEquals("Find WebElements.", nodeDoc.description);
		
		assertTrue(nodeDoc.intro.startsWith("<p>Extracts WebElements"));
		assertTrue(nodeDoc.intro.endsWith("Timeouts options.</p>"));

		assertEquals(7, nodeDoc.options.size());
		assertEquals("Input", nodeDoc.options.get(0).name);
		assertEquals("The input column providing the starting point where to search.",
				nodeDoc.options.get(0).description);
		assertFalse(nodeDoc.options.get(0).optional);

		assertEquals(1, nodeDoc.inPorts.size());
		assertEquals(0, nodeDoc.inPorts.get(0).index);
		assertEquals("WebDriver or WebElements", nodeDoc.inPorts.get(0).name);
		assertEquals("Table with a column providing a WebDriver or WebElements in which to search",
				nodeDoc.inPorts.get(0).description);

		assertEquals(1, nodeDoc.outPorts.size());
		assertTrue(nodeDoc.toJson().replaceAll("\\s+", " ").contains("\"name\": \"Find Elements\""));
		
		assertNull(nodeDoc.interactiveView);
	}

	@Test
	public void parsing_XML_with_tab_options() throws Exception {
		Document doc = readDoc("/StartWebDriverNodeFactory.xml");
		NodeDoc nodeDoc = NodeDocJsonParser.parse(doc);
		assertEquals(2, nodeDoc.optionTabs.size());
		assertEquals("Options", nodeDoc.optionTabs.get(0).name);
	}
	
	@Test
	public void parsing_XML_dynamicJSNodes() throws Exception {
		Document doc = readDoc("/dynamicJS/node.xml");
		NodeDoc nodeDoc = NodeDocJsonParser.parse(doc);
		assertEquals("Visualizer", nodeDoc.type);
		assertFalse(nodeDoc.deprecated);
		assertEquals("Box Plot (JavaScript)", nodeDoc.name);
		assertEquals("This node provides a view with a Box Plot implemented with D3.js.", nodeDoc.description);
		// FIXME assertTrue(nodeDoc.intro.startsWith("<p>"));
		// FIXME assertTrue(nodeDoc.intro.endsWith("</p>"));
		
		assertEquals(3, nodeDoc.optionTabs.size());
		assertEquals(4, nodeDoc.optionTabs.get(0).options.size());
		Option firstOption = nodeDoc.optionTabs.get(0).options.get(0);
		assertEquals("columnFilterOption", firstOption.type);
		assertEquals("Included columns", firstOption.name);
		assertTrue(firstOption.description.startsWith("Select the columns"));
		assertTrue(firstOption.description.endsWith("warning messages."));
		
		assertEquals("Box Plot", nodeDoc.interactiveView.name);
		assertEquals("A JavaScript implementation of a Box Plot.", nodeDoc.interactiveView.description);
		
		assertEquals(1, nodeDoc.inPorts.size());
		assertEquals(1, nodeDoc.outPorts.size());
	}

	private static Document readDoc(String resourcePath) throws Exception {
		Objects.requireNonNull(resourcePath, "resourcePath must not be null");
		try (InputStream resourceStream = NodeDocJsonParserTest.class.getResourceAsStream(resourcePath)) {
			Objects.requireNonNull(resourcePath, "resource for " + resourcePath + " not found");
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			// org.knime.core.node.NodeFactory.getXMLDescription() is namespaceAware:
			// org.knime.core.node.NodeDescription.initializeDocumentBuilderFactory()
			documentBuilderFactory.setNamespaceAware(true);
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			return documentBuilder.parse(resourceStream);
		}
	}

}
