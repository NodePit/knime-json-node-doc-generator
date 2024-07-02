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
		assertTrue(nodeDoc.intro.startsWith("<p>"));
		assertTrue(nodeDoc.intro.endsWith("</p>"));
		
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

	@Test
	public void parsing_XML_with_data_in_out_ports() throws Exception {
		Document doc = readDoc("/GlideSortNodeFactory.xml");
		NodeDoc nodeDoc = NodeDocJsonParser.parse(doc);

		assertEquals("Source", nodeDoc.type);
		assertEquals(false, nodeDoc.deprecated);

		assertEquals("Glide Sort Results", nodeDoc.name);

		assertTrue(nodeDoc.description.startsWith("Pose-viewer data"));
		assertTrue(nodeDoc.description.endsWith("lowest-scoring structures."));

		assertTrue(nodeDoc.intro.startsWith("Pose-viewer data"));
		assertTrue(nodeDoc.intro.endsWith("implement this node.<br/>"));

		assertEquals(12, nodeDoc.options.size());
		assertEquals("Sorting Options", nodeDoc.options.get(0).name);

		assertEquals(1, nodeDoc.inPorts.size());
		assertEquals(0, nodeDoc.inPorts.get(0).index);
		assertEquals("Molecules in Maestro format", nodeDoc.inPorts.get(0).name);
		assertEquals("Molecules in Maestro format", nodeDoc.inPorts.get(0).description);

		assertEquals(1, nodeDoc.outPorts.size());
		assertEquals(0, nodeDoc.outPorts.get(0).index);
		assertEquals("Molecules in Maestro format", nodeDoc.outPorts.get(0).name);
		assertEquals("Sorted molecules in Maestro format", nodeDoc.outPorts.get(0).description);

		assertEquals(1, nodeDoc.views.size());
		assertEquals(0, nodeDoc.views.get(0).index);
		assertEquals("Std output/error of Sort Results", nodeDoc.views.get(0).name);
		assertEquals("Std output/error of Sort Results", nodeDoc.views.get(0).description);

		assertNull(nodeDoc.interactiveView);
	}

	@Test
	public void parsing_XML_with_description_in_tabs() throws Exception {
		Document doc = readDoc("/JavaSnippetNodeFactory.xml");
		NodeDoc nodeDoc = NodeDocJsonParser.parse(doc);

		assertEquals(3, nodeDoc.optionTabs.size());
		assertEquals("Java Snippet", nodeDoc.optionTabs.get(0).name);
		assertEquals("Contains the Java code editor to edit the snippet.", nodeDoc.optionTabs.get(0).description);
		assertEquals(5, nodeDoc.optionTabs.get(0).options.size());
	}

	@Test
	public void parsing_XML_with_links() throws Exception {
		Document doc = readDoc("/GroupByNodeFactory.xml");
		NodeDoc nodeDoc = NodeDocJsonParser.parse(doc);

		assertEquals(1, nodeDoc.links.size());
		assertEquals(
				"https://www.knime.com/knime-introductory-course/chapter3/section2/classic-aggregations-with-groupby-node",
				nodeDoc.links.get(0).href);
		assertEquals("KNIME E-Learning Course: Classic Aggregations with GroupBy node", nodeDoc.links.get(0).text);
	}

	@Test
	public void parsing_XML_with_dynamic_ports() throws Exception {
		Document doc = readDoc("/ExcelTableWriterNodeFactory.xml");
		NodeDoc nodeDoc = NodeDocJsonParser.parse(doc);

		assertEquals(2, nodeDoc.dynamicInPorts.size());

		assertEquals((Integer) 0, nodeDoc.dynamicInPorts.get(0).insertBefore);
		assertEquals("File system connection", nodeDoc.dynamicInPorts.get(0).name);
		assertEquals("File System Connection", nodeDoc.dynamicInPorts.get(0).groupIdentifier);
		assertEquals("The file system connection.", nodeDoc.dynamicInPorts.get(0).description);

		assertEquals((Integer) 1, nodeDoc.dynamicInPorts.get(1).insertBefore);
		assertEquals("Additional input tables", nodeDoc.dynamicInPorts.get(1).name);
		assertEquals("Sheet Input Ports", nodeDoc.dynamicInPorts.get(1).groupIdentifier);
		assertEquals("Additional data table to write.", nodeDoc.dynamicInPorts.get(1).description);
	}

	private static Document readDoc(String resourcePath) throws Exception {
		Objects.requireNonNull(resourcePath, "resourcePath must not be null");
		try (InputStream resourceStream = NodeDocJsonParserTest.class.getResourceAsStream(resourcePath)) {
			Objects.requireNonNull(resourcePath, "resource for " + resourcePath + " not found");
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			// org.knime.core.node.NodeFactory.getXMLDescription() is namespaceAware:
			// org.knime.core.node.NodeDescription.initializeDocumentBuilderFactory()
			documentBuilderFactory.setNamespaceAware(true);
			// disable validation of external XSD for GlideSortNodeFactory.xml
			documentBuilderFactory.setValidating(false);
			documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			return documentBuilder.parse(resourceStream);
		}
	}

}
