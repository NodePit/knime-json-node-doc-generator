package de.philippkatz.knime.jsondocgen;

import java.io.InputStream;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * Helper methods for working with the awful DOM API.
 *
 * @author pk
 */
public final class XmlUtils {

	private static final String REMOVE_NAMESPACE_XSLT = "remove-namespace.xslt";

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
	 * Strips namespace information form the given node, to ease further processing.
	 * I took the XSLT transformation from <a href=
	 * "http://clardeur.blogspot.de/2012/11/remove-all-namespaces-from-xml-using.html">here</a>.
	 * 
	 * @param node
	 *            The node.
	 * @return A transformed node without namespace info.
	 */
	public static Node removeNamespaces(Node node) {
		Objects.requireNonNull(node, "node must not be null");
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		InputStream xslt = XmlUtils.class.getResourceAsStream(REMOVE_NAMESPACE_XSLT);
		if (xslt == null) {
			throw new IllegalStateException("Could not load " + REMOVE_NAMESPACE_XSLT + " -- InputStream == null");
		}
		DOMResult result = new DOMResult();
		try {
			Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslt));
			transformer.transform(new DOMSource(node), result);
		} catch (TransformerException e) {
			throw new IllegalStateException(e);
		}
		return result.getNode();
	}

	private XmlUtils() {
		// no instance
	}

}
