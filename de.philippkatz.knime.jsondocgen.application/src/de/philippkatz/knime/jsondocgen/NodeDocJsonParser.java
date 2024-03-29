package de.philippkatz.knime.jsondocgen;

import static de.philippkatz.knime.jsondocgen.Utils.trim;
import static de.philippkatz.knime.jsondocgen.XmlUtils.getAttribute;
import static de.philippkatz.knime.jsondocgen.XmlUtils.getInnerXml;
import static de.philippkatz.knime.jsondocgen.XmlUtils.getNode;
import static de.philippkatz.knime.jsondocgen.XmlUtils.getNodes;
import static de.philippkatz.knime.jsondocgen.XmlUtils.getString;
import static de.philippkatz.knime.jsondocgen.XmlUtils.removeNamespaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.w3c.dom.Node;

import de.philippkatz.knime.jsondocgen.docs.NodeDoc;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.DynamicPortGroup;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.InteractiveView;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.Link;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.NodeDocBuilder;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.Option;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.OptionTab;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.Port;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.View;

public final class NodeDocJsonParser {

	private NodeDocJsonParser() {
		// only to be instantiated by Chuck Norris
	}
	
	public static NodeDocBuilder parse(Node domNode, NodeDocBuilder builder) {
		Objects.requireNonNull(domNode, "document must not be null");
		Objects.requireNonNull(builder, "builder must not be null");
		
		Node nodeNoNS = removeNamespaces(domNode);

		builder.setName(trim(getString(nodeNoNS, "/knimeNode/name")));
		builder.setDescription(trim(getString(nodeNoNS, "/knimeNode/shortDescription")));
		Node introNode = getNode(nodeNoNS, "/knimeNode/fullDescription/intro");
		if (introNode != null) {
			builder.setIntro(trim(getInnerXml(introNode)));
		}
		builder.setType(getString(nodeNoNS, "/knimeNode/@type"));
		boolean deprecated = Boolean.parseBoolean(getString(nodeNoNS, "/knimeNode/@deprecated"));
		if (deprecated) {
			// there are two locations, where nodes can be set to deprecated:
			// so, do not overwrite with false, if already set to true
			builder.setDeprecated(true);
		}

		// options are either children of fullDescription,
		// or nested within tab elements
		List<Node> tabs = getNodes(nodeNoNS, "/knimeNode/fullDescription/tab");
		if (tabs.size() > 0) {
			for (Node tab : tabs) {
				String name = getAttribute(tab, "name");
				String description = null;
				Node descriptionNode = getNode(tab, "description");
				if (descriptionNode != null) {
					description = trim(getInnerXml(descriptionNode));
				}
				// first, try whether there's a sub-element 'options'; see:
				// https://www.knime.org/node/dynamicNode_v3.0.xsd
				// https://www.knime.org/node/dynamicJSNode_v3.0.xsd
				List<Node> options = getNodes(tab, "./options/*");
				// if not, use options present as children within tab
				if (options.size() == 0) {
					options = getNodes(tab, "option");
				}
				builder.addOptionTab(new OptionTab(name, description, parseOptions(options)));
			}
		} else {
			builder.setOptions(parseOptions(getNodes(nodeNoNS, "/knimeNode/fullDescription/option")));
		}
		
		// links (added in v1.11)
		List<Node> links = getNodes(nodeNoNS, "/knimeNode/fullDescription/link");
		for (Node link : links) {
			String href = getAttribute(link, "href");
			String text = trim(getInnerXml(link));
			builder.addLink(new Link(href, text));
		}

		// in ports
		builder.setInPorts(
				parsePorts(getNodes(nodeNoNS, "/knimeNode/ports/*[name()='inPort' or name()='dataIn']"), true));

		// out ports
		builder.setOutPorts(
				parsePorts(getNodes(nodeNoNS, "/knimeNode/ports/*[name()='outPort' or name()='dataOut']"), false));

		// dynamic in and out ports
		builder.setDynamicInPorts(parseDynamicPorts(getNodes(nodeNoNS, "/knimeNode/ports/dynInPort")));
		builder.setDynamicOutPorts(parseDynamicPorts(getNodes(nodeNoNS, "/knimeNode/ports/dynOutPort")));

		// views
		List<Node> views = getNodes(nodeNoNS, "/knimeNode/views/view");
		for (Node view : views) {
			int index = Integer.valueOf(getAttribute(view, "index"));
			String name = getAttribute(view, "name");
			String description = trim(getInnerXml(view));
			builder.addView(new View(index, name, description));
		}
		
		// interactive view
		Node interactiveView = getNode(nodeNoNS, "/knimeNode/interactiveView");
		if (interactiveView != null) {
			String name = getAttribute(interactiveView, "name");
			String description = trim(getInnerXml(interactiveView));
			builder.setInteractiveView(new InteractiveView(name, description));
		}		
		
		return builder;
	}

	/* package */ static NodeDoc parse(Node domNode) {
		Objects.requireNonNull(domNode, "document must not be null");
		NodeDocBuilder builder = new NodeDocBuilder();
		parse(domNode, builder);
		return builder.build();

	}

	private static List<Option> parseOptions(List<Node> options) {
		List<Option> optionsJson = new ArrayList<>();
		for (Node option : options) {
			String type = option.getNodeName();
			String name = getAttribute(option, "name");
			String description = trim(getInnerXml(option));
			boolean optional = Boolean.parseBoolean(getAttribute(option, "optional"));
			optionsJson.add(new Option(type, name, description, optional));
		}
		return optionsJson;
	}

	private static List<Port> parsePorts(List<Node> ports, boolean isInPort) {
		List<Port> portsJson = new ArrayList<>();
		for (Node port : ports) {
			int index = Integer.valueOf(getAttribute(port, "index"));
			String name = getAttribute(port, "name");
			String description = trim(getInnerXml(port));
			Boolean optional = null;
			if (isInPort) {
				optional = Boolean.parseBoolean(getAttribute(port, "optional"));
			}
			portsJson.add(new Port(index, /* not known at this point. */ null, name, description, optional));
		}
		return portsJson;
	}

	private static List<DynamicPortGroup> parseDynamicPorts(List<Node> ports) {
		List<DynamicPortGroup> portsDocs = new ArrayList<>();
		for (Node port : ports) {
			int insertBefore = Integer.valueOf(getAttribute(port, "insert-before"));
			String name = getAttribute(port, "name");
			String groupIdentifier = getAttribute(port, "group-identifier");
			String description = trim(getInnerXml(port));
			portsDocs.add(new DynamicPortGroup(insertBefore, name, groupIdentifier, description, /* not know at this point */ null));
		}
		return portsDocs;
	}

}
