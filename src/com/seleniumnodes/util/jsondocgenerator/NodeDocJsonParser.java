package com.seleniumnodes.util.jsondocgenerator;

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

import com.seleniumnodes.util.jsondocgenerator.NodeDoc.Option;
import com.seleniumnodes.util.jsondocgenerator.NodeDoc.OptionTab;
import com.seleniumnodes.util.jsondocgenerator.NodeDoc.Port;
import com.seleniumnodes.util.jsondocgenerator.NodeDoc.View;

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
		nodeDoc.intro = trim(transformIntro(jerry.$("knimeNode fullDescription intro")).html());
		nodeDoc.identifier = nodeIdentifier;

		// options are either children of fullDescription,
		// or nested within tab elements
		Jerry tabs = jerry.$("knimeNode fullDescription tab");
		if (tabs.length() > 0) {
			List<OptionTab> optionTabs = new ArrayList<>();
			for (Jerry tab : tabs) {
				OptionTab optionTab = new OptionTab();
				optionTab.name = tab.attr("name");
				Jerry description = tab.$("description");
				if (description.length() > 0) {
					optionTab.description = trim(description.text());
				}
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
			optionsJson.add(optionObject);
		}
		return optionsJson;
	}

	private static Jerry transformIntro(Jerry intro) {
		// add class="table table-striped table-bordered" to tables
		intro.$("table").addClass("table", "table-striped", "table-bordered");

		// TODO convert pseudo-links in comments to actual links
		// e.g. <!-- node-ref="QuitWebDriver" -->Quit WebDriver<!-- /node-ref
		// -->
		// maybe see here for a solution:
		// http://stackoverflow.com/questions/8118054/jquery-change-text-between-two-elements
		/*
		 * var comments = $('*', element).contents().filter(function() { return
		 * this.nodeType === COMMENT_NODE; }).each(function() {
		 * console.log(this.nodeValue.trim()); });
		 */

		return intro;
	}

	private static String trim(String string) {
		return string != null ? string.replaceAll("\\s+", " ").trim() : null;
	}

	private static List<Port> parsePorts(Jerry ports, boolean isInPort) {
		List<Port> portsJson = new ArrayList<>();
		for (Jerry port : ports) {
			Port portObject = new Port();
			portObject.index = Integer.valueOf(port.attr("index"));
			portObject.name = port.attr("name");
			portObject.description = trim(port.html());
			if (isInPort) {
				portObject.optional = Boolean.valueOf(Optional.ofNullable(port.attr("optional")).orElse("false"));
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

}
