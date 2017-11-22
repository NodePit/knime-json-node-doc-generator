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

import de.philippkatz.knime.jsondocgen.docs.NodeDoc;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.InteractiveView;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.NodeDocBuilder;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.Option;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.OptionTab;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.Port;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.View;
import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.lagarto.dom.LagartoDOMBuilder;

public final class NodeDocJsonParser {

	private NodeDocJsonParser() {
		// only to be instantiated by Chuck Norris
	}
	
	public static NodeDocBuilder parse(Node domNode, NodeDocBuilder builder) throws TransformerException {
		Objects.requireNonNull(domNode, "document must not be null");
		Objects.requireNonNull(builder, "builder must not be null");
		
		String xmlString = documentToString(domNode);

		// explicitly enable XML mode: http://jodd.org/doc/jerry/#configuration
		JerryParser jerryParser = Jerry.jerry();
		LagartoDOMBuilder domBuilder = (LagartoDOMBuilder) jerryParser.getDOMBuilder();
		domBuilder.enableXmlMode();
		Jerry jerry = jerryParser.parse(xmlString);

		builder.setName(trim(jerry.$("knimeNode name").text()));
		builder.setDescription(trim(jerry.$("knimeNode shortDescription").text()));
		builder.setIntro(trim(jerry.$("knimeNode fullDescription intro").html()));
		builder.setType(jerry.$("knimeNode").attr("type"));
		builder.setDeprecated(parseOptionalBoolean(jerry.$("knimeNode").attr("deprecated")));

		// options are either children of fullDescription,
		// or nested within tab elements
		Jerry tabs = jerry.$("knimeNode fullDescription tab");
		if (tabs.length() > 0) {
			for (Jerry tab : tabs) {
				String name = tab.attr("name");
				List<Option> options = parseOptions(tab.$("option"));
				builder.addOptionTab(new OptionTab(name, options));
			}
		} else {
			builder.setOptions(parseOptions(jerry.$("knimeNode fullDescription option")));
		}

		// ports
		builder.setInPorts(parsePorts(jerry.$("knimeNode ports inPort"), true));
		builder.setOutPorts(parsePorts(jerry.$("knimeNode ports outPort"), false));

		// views
		Jerry views = jerry.$("knimeNode views view");
		if (views.length() > 0) {
			for (Jerry view : views) {
				int index = Integer.valueOf(view.attr("index"));
				String name = view.attr("name");
				String description = trim(view.html());
				builder.addView(new View(index, name, description));
			}
		}
		
		// interactive view
		Jerry interactiveView = jerry.$("knimeNode interactiveView");
		if (interactiveView.size() > 0) {
			String name = interactiveView.attr("name");
			String description = trim(interactiveView.html());
			builder.setInteractiveView(new InteractiveView(name, description));
		}		
		
		return builder;
	}

	public static NodeDoc parse(Node domNode, String nodeIdentifier) throws TransformerException {
		Objects.requireNonNull(domNode, "document must not be null");
		NodeDocBuilder builder = new NodeDocBuilder();
		parse(domNode, builder);
		builder.setId(nodeIdentifier);
		return builder.build();

	}

	public static String parseJsonString(Node domNode, String nodeIdentifier) throws TransformerException {
		return parse(domNode, nodeIdentifier).toJson();
	}

	private static List<Option> parseOptions(Jerry options) {
		List<Option> optionsJson = new ArrayList<>();
		for (Jerry option : options) {
			String name = option.attr("name");
			String description = trim(option.html());
			boolean optional = parseOptionalBoolean(option.attr("optional"));
			optionsJson.add(new Option(name, description, optional));
		}
		return optionsJson;
	}

	private static String trim(String string) {
		return string != null ? string.trim() : null;
	}

	private static List<Port> parsePorts(Jerry ports, boolean isInPort) {
		List<Port> portsJson = new ArrayList<>();
		for (Jerry port : ports) {
			int index = Integer.valueOf(port.attr("index"));
			String name = port.attr("name");
			String description = trim(port.html());
			Boolean optional = null;
			if (isInPort) {
				optional = parseOptionalBoolean(port.attr("optional"));
			}
			portsJson.add(new Port(index, name, description, optional));
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
