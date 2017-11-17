package de.philippkatz.knime.jsondocgen;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import de.philippkatz.knime.jsondocgen.NodeDoc.InteractiveView;
import de.philippkatz.knime.jsondocgen.NodeDoc.Option;
import de.philippkatz.knime.jsondocgen.NodeDoc.OptionTab;
import de.philippkatz.knime.jsondocgen.NodeDoc.Port;
import de.philippkatz.knime.jsondocgen.NodeDoc.View;
import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.lagarto.dom.LagartoDOMBuilder;

public final class NodeDocJsonParser {

	private NodeDocJsonParser() {
		// only to be instantiated by Chuck Norris
	}

	public static NodeDoc parse(Node domNode, String nodeIdentifier) throws TransformerException {
		Objects.requireNonNull(domNode, "document must not be null");

		String xmlString = documentToString(domNode);

		// explicitly enable XML mode: http://jodd.org/doc/jerry/#configuration
		JerryParser jerryParser = Jerry.jerry();
		LagartoDOMBuilder domBuilder = (LagartoDOMBuilder) jerryParser.getDOMBuilder();
		domBuilder.enableXmlMode();
		Jerry jerry = jerryParser.parse(xmlString);

		NodeDoc nodeDoc = new NodeDoc();

		nodeDoc.name = trim(jerry.$("knimeNode name").text());
		nodeDoc.shortDescription = trim(jerry.$("knimeNode shortDescription").text());
		nodeDoc.intro = trim(jerry.$("knimeNode fullDescription intro").html());
		nodeDoc.identifier = nodeIdentifier;
		nodeDoc.icon = jerry.$("knimeNode").attr("icon");
		nodeDoc.type = jerry.$("knimeNode").attr("type");
		nodeDoc.deprecated = parseOptionalBoolean(jerry.$("knimeNode").attr("deprecated"));

		// options are either children of fullDescription,
		// or nested within tab elements
		Jerry tabs = jerry.$("knimeNode fullDescription tab");
		if (tabs.length() > 0) {
			List<OptionTab> optionTabs = new ArrayList<>();
			for (Jerry tab : tabs) {
				OptionTab optionTab = new OptionTab();
				optionTab.name = tab.attr("name");
				optionTab.options = parseOptions(tab.$("option"));
				optionTabs.add(optionTab);
			}
			nodeDoc.optionTabs = optionTabs;
		} else {
			nodeDoc.options = parseOptions(jerry.$("knimeNode fullDescription option"));
		}

		// ports
		nodeDoc.inPorts = parsePorts(jerry.$("knimeNode ports inPort"), true);
		nodeDoc.outPorts = parsePorts(jerry.$("knimeNode ports outPort"), false);

		// views
		Jerry views = jerry.$("knimeNode views view");
		if (views.length() > 0) {
			List<View> viewObjects = new ArrayList<>();
			for (Jerry view : views) {
				View viewObject = new View();
				viewObject.index = Integer.valueOf(view.attr("index"));
				viewObject.name = view.attr("name");
				viewObject.description = trim(view.html());
				viewObjects.add(viewObject);
			}
			nodeDoc.views = viewObjects;
		}
		
		// interactive view
		Jerry interactiveView = jerry.$("knimeNode interactiveView");
		if (interactiveView.size() > 0) {
			InteractiveView interactiveViewObject = new InteractiveView();
			interactiveViewObject.name = interactiveView.attr("name");
			interactiveViewObject.description = trim(interactiveView.html());
			nodeDoc.interactiveView = interactiveViewObject;
		}
		
		return nodeDoc;

	}

	public static String parseJsonString(Node domNode, String nodeIdentifier) throws TransformerException {
		return parse(domNode, nodeIdentifier).toJson();
	}

	private static List<Option> parseOptions(Jerry options) {
		List<Option> optionsJson = new ArrayList<>();
		for (Jerry option : options) {
			Option optionObject = new Option();
			optionObject.name = option.attr("name");
			optionObject.description = trim(option.html());
			optionObject.optional = parseOptionalBoolean(option.attr("optional"));
			optionsJson.add(optionObject);
		}
		return optionsJson;
	}

	private static String trim(String string) {
		return string != null ? string.trim() : null;
	}

	private static List<Port> parsePorts(Jerry ports, boolean isInPort) {
		List<Port> portsJson = new ArrayList<>();
		for (Jerry port : ports) {
			Port portObject = new Port();
			portObject.index = Integer.valueOf(port.attr("index"));
			portObject.name = port.attr("name");
			portObject.description = trim(port.html());
			if (isInPort) {
				portObject.optional = parseOptionalBoolean(port.attr("optional"));
			}
			portsJson.add(portObject);
		}
		return portsJson;
	}

	private static String documentToString(Node domNode) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(domNode), new StreamResult(writer));
		return writer.getBuffer().toString();
	}
	
	/**
	 * Parse ("true" | "false" | null) to a boolean.
	 * 
	 * @param string
	 *            The string.
	 * @return <code>true</code> in case the string was "true", <code>false</code>
	 *         in case the string was "false" or <code>null</code>.
	 */
	private static boolean parseOptionalBoolean(String string) {
		return Boolean.valueOf(Optional.ofNullable(string).orElse("false"));
	}

}
