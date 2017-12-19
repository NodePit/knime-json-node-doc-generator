package de.philippkatz.knime.jsondocgen;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Helper methods for working with the awful DOM API.
 *
 * @author pk
 */
public final class XmlUtils {

	/** Adapts a {@link NodeList} to {@link List} interface. */
	private static final class NodeListAdapter extends AbstractList<Node> {
		private final NodeList nodeList;

		private NodeListAdapter(NodeList nodeList) {
			this.nodeList = nodeList;
		}

		@Override
		public Node get(int index) {
			return nodeList.item(index);
		}

		@Override
		public int size() {
			return nodeList.getLength();
		}
	}

	/**
	 * Get a string value by XPath.
	 *
	 * @param node
	 *            The node.
	 * @param xPath
	 *            The XPath.
	 * @return The string matching the XPath, or <code>null</code>.
	 */
	public static String getString(Node node, String xPath) {
		return (String) evaluateXPath(node, xPath, XPathConstants.STRING);
	}

	/**
	 * Get nodes relative to the given node by XPath.
	 *
	 * @param node
	 *            The node.
	 * @param xPath
	 *            The XPath.
	 * @return A list with nodes, or an empty list.
	 */
	public static List<Node> getNodes(Node node, String xPath) {
		Object nodeList = evaluateXPath(node, xPath, XPathConstants.NODESET);
		return new NodeListAdapter((NodeList) nodeList);
	}

	/**
	 * Get a node relative to the given node by XPath.
	 *
	 * @param node
	 *            The node.
	 * @param xPath
	 *            The XPath.
	 * @return The node, or <code>null</code>.
	 */
	public static Node getNode(Node node, String xPath) {
		return (Node) evaluateXPath(node, xPath, XPathConstants.NODE);
	}

	private static Object evaluateXPath(Node node, String xPath, QName returnType) {
		Objects.requireNonNull(node, "node must not be null");
		Objects.requireNonNull(xPath, "xPath must not be null");
		XPathFactory xPathFactory = XPathFactory.newInstance();
		try {
			XPathExpression xPathExpression = xPathFactory.newXPath().compile(xPath);
			return xPathExpression.evaluate(node, returnType);
		} catch (XPathExpressionException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Get a {@link Node}'s inner text, i.e. including all XML tags.
	 *
	 * @param node
	 *            The node.
	 * @return The inner XML text.
	 */
	public static String getInnerXml(Node node) {
		Objects.requireNonNull(node, "node must not be null");
		Document document = node.getOwnerDocument();
		DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation().getFeature("LS", "3.0");
		LSSerializer lsSerializer = domImplLS.createLSSerializer();
		lsSerializer.getDomConfig().setParameter("xml-declaration", false);
		return new NodeListAdapter(node.getChildNodes()).stream().map(n -> lsSerializer.writeToString(n))
				.collect(Collectors.joining());
	}

	/**
	 * Get an attribute value from the given node.
	 *
	 * @param node
	 *            The node.
	 * @param name
	 *            The name of the attribute.
	 * @return The attribute value, or <code>null</code> in case the attribute does
	 *         not exist.
	 */
	public static String getAttribute(Node node, String name) {
		Objects.requireNonNull(node, "node must not be null");
		Objects.requireNonNull(name, "name must not be null");
		Node attribute = node.getAttributes().getNamedItem(name);
		return attribute != null ? attribute.getNodeValue() : null;
	}

	/**
	 * Re-parses the given node and produces a non-namespace-aware result (this is
	 * easier to process by XPath, but can potentially lead to unexpected results,
	 * because there might be name conflicts).
	 * 
	 * @param node
	 *            The node to re-parse.
	 * @return Same as input, but not namespace-aware.
	 */
	public static Node reParseWithoutNamespace(Node node) {
		Objects.requireNonNull(node, "domNode must not be null");
		try {
			String xmlString = nodeToString(node);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(false);
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			return documentBuilder.parse(new InputSource(new StringReader(xmlString)));
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			throw new IllegalStateException(e);
		}
	}

	private static String nodeToString(Node node) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		return writer.getBuffer().toString();
	}

	private XmlUtils() {
		// no instance
	}

}
