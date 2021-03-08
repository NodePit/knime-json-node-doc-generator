KNIME JSON Node Documentation Generator
=======================================

[![Actions Status](https://github.com/NodePit/knime-json-node-doc-generator/workflows/CI/badge.svg)](https://github.com/NodePit/knime-json-node-doc-generator/actions)

Builds JSON files containing KNIME node, port, and splash icon documentations, based on and similar to
KNIME’s `org.knime.workbench.repository.NodeDocumentationGenerator`, but with a
structured JSON files as a result which can be easily further processed, mined
and integrated in different environments. Originally created for the [Selenium Nodes][1]
website where we display the node documentation on [this][3] page, and now also
used for building [NodePit’s][4] node index.

Installation
------------

The tool is available through the following software site (the version numbers reflect the branch names e.g. `release/4.1` -- in case there’s no explicit branch for a version, the update site will fall back to the most recent previous version):

```
https://download.nodepit.com/jsondocgen/3.6
https://download.nodepit.com/jsondocgen/3.7
https://download.nodepit.com/jsondocgen/4.0
https://download.nodepit.com/jsondocgen/4.1
https://download.nodepit.com/jsondocgen/4.2
```

Beside that, the `master` branch is available through the following software site:

```
https://download.nodepit.com/jsondocgen/master
```

Open KNIME, go to **File → Preferences → Install/Update → Available Software
Sites**, click on **Add…** and paste above’s URL. Save and close the preferences
window, choose **File → Install KNIME Extensions…** and select the **JSON node
documentation generator** in the list. Restart KNIME when promted to do so and
follow the usage instructions below.

Usage
-----

The application has the ID
`de.philippkatz.knime.jsondocgen.application.JsonNodeDocumentationGenerator`.
Execute it without options to see the following instructions:

```
$ ./Knime -nosplash -application de.philippkatz.knime.jsondocgen.application.JsonNodeDocumentationGenerator
Usage: NodeDocuGenerator options
Allowed options are:
	-destination dir : directory where the result should be written to (directory must exist)
	-plugin plugin-id : Only nodes of the specified plugin will be considered (specify multiple plugins by repeating this option). If not all available plugins will be processed.
	-category category-path (e.g. /community) : Only nodes within the specified category path will be considered. If not specified '/' is used.
	-includeDeprecated : Include nodes marked as 'deprecated' in the extension point.
	-includeHidden : Include nodes marked as 'hidden' in the extension point (new since KNIME 4.2).
	-skipNodeDocumentation : Skip generating node documentation
	-skipPortDocumentation : Skip generating port documentation
	-skipSplashIcons : Skip extracting splash screen icons
```

This example creates three JSON file in you home directory
(all images within the JSON files are encoded as [Base64][5] strings):

* `nodeDocumentation.json` with the documentation for all Selenium nodes
* `portDocumentation.json` with a hierarchy of all available ports
* `splashIcons.json`: list of all registered splash screen icons

```
$ ./Knime -nosplash --launcher.suppressErrors -application de.philippkatz.knime.jsondocgen.application.JsonNodeDocumentationGenerator -destination ~ -category /selenium
```

When running within a headless environment (CI, Docker, …) with Xvfb, it’s
highly advisable to add the option `--launcher.suppressErrors`. Otherwise,
execution errors will lead to a seemingly hanging application, as the shown
error dialog remains “invisible”.

The generated `nodeDocumentation.json` JSON file’s structure looks as follows:

```json
{
  "children": [
    {
      "nodes": [
        {
          "intro": "<p>This node allows interacting with JavaScript-based <a href=\"https://developer.mozilla.org/en-US/docs/Web/API/Window/alert\">alert</a>, \n        <a href=\"https://developer.mozilla.org/en-US/docs/Web/API/Window/confirm\">confirm</a>, and \n        <a href=\"https://developer.mozilla.org/en-US/docs/Web/API/Window/prompt\">prompt</a> dialog boxes. \n        When a WebBrowser shows a dialog, these can be dismissed or confirmed. Further, it allows to \n        extract a dialog's text and send keyboard input to the dialog. The node assumes, that a \n        dialog box is currently shown in the browser window.</p><p><b>Important:</b> This node does <b>not</b> work with headless browsers (PhantomJS, jBrowser, \n        HtmlUnit, headless Chrome). In case you want to influence the page's dialog behavior when running\n        headless browsers, you can directly modify the <tt>window.alert</tt>, <tt>window.confirm</tt>, \n        or <tt>window.prompt</tt> functions using an <b>Execute JavaScript</b> node, which needs to be \n        placed at a point in the workflow, before the dialog is actually shown. The following code\n        snippet gives a simple example how to globally override the default behavior:</p><pre>\nwindow.alert = function() {\n   // do nothing\n}\nwindow.confirm = function() {\n  return true; // simulate, that all 'confirm' dialogs are closed via 'OK' button\n}\nwindow.prompt = function() {\n  return 'dummy text'; // simulate, that the given string is entered into all 'prompt' dialogs\n}\n        </pre>",
          "options": [
            {
              "type": "option",
              "name": "WebDriver input",
              "description": "Input column which provides the WebDriver(s)",
              "optional": false
            },
            {
              "type": "option",
              "name": "Dismiss",
              "description": "Close the dialog by clicking the \"Cancel\" button.",
              "optional": false
            },
            {
              "type": "option",
              "name": "Accept",
              "description": "Close the dialog by clicking the \"OK\" button.",
              "optional": false
            },
            {
              "type": "option",
              "name": "Append column with text",
              "description": "Extract the dialog's message and append it as text column.",
              "optional": false
            },
            {
              "type": "option",
              "name": "Send keys",
              "description": "The text to send to the dialog (in case it is a \"prompt\" dialog).",
              "optional": false
            }
          ],
          "inPorts": [
            {
              "index": 0,
              "portObjectClass": "org.knime.core.node.BufferedDataTable",
              "name": "WebDriver",
              "description": "Table with a column providing a WebDriver with an open alert, confirm, or prompt box.",
              "optional": false
            }
          ],
          "outPorts": [
            {
              "index": 0,
              "portObjectClass": "org.knime.core.node.BufferedDataTable",
              "name": "WebDriver",
              "description": "Same as input table, and appended text column in case the \"Save text\" option was selected.",
              "optional": false
            }
          ],
          "inPortObjectClasses": [
            "org.knime.core.node.BufferedDataTable"
          ],
          "outPortObjectClasses": [
            "org.knime.core.node.BufferedDataTable"
          ],
          "type": "Manipulator",
          "deprecated": false,
          "streamable": false,
          "identifier": "ws.palladian.nodes.selenium.alert.AlertNodeFactory",
          "id": "ws.palladian.nodes.selenium.alert.AlertNodeFactory",
          "name": "Alert",
          "shortDescription": "Interact with alert, confirm, and prompt boxes.",
          "description": "Interact with alert, confirm, and prompt boxes.",
          "contributingPlugin": "ws.palladian.nodes.selenium.plugin",
          "iconBase64": "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABtUlEQVR4nKWTO2siYRSGP7DaQrBKsaWNvZWVlY2FoIJFSJ9sQggsC9ukzi+wVEQQRVHEC6hoYWEycYkJUSJGsLJQ8ILiBUXUN/N+MEsCaUwGHmY473mYc75hBADxHYTH4zGqHKucHggdo3C73SeKorx0Oh20269oNpuo159Rq9VQrVahZri7I//U50fc39fx8NBCoXD7Qlc4nc7zUqmEYrGoFgvI5XLIZrNIpVJIJpOIx+OIRqOIRCIIhUIIBoMIBALw+/3Q6XSnXOEMX7zoCpfL9Wu322G73Ur6/T4qlYqc6D2sMdP66NAVDofjYrPZYL1eS8rlMhqNBlqtljwH7q8ovD/JTOujQ1fY7fbL1WqFxWIhSafT4Jnk83lkMhkkEgm5P3f3+Xz/++jQFTab7Wo+n2MymUg47n6/lyO+hzVmWh8dusJqtf6eTqcYDocSTrBcLjGbzT7AGjOtjw5dYbFY/ozHY/R6PUk4HJZvGI1GH2CNmdZHh64wm81/B4MBut2uJBaLwev1fgozrY8OXWEyma45Ej/RIdChK/R6/ZnBYLj5CnSFev1QOVL5eSBH0v3u7/wGzWm83d0+AEIAAAAASUVORK5CYII="
        },

     ],
      "identifier": "selenium",
      "id": "selenium",
      "name": "Selenium",
      "shortDescription": "Selenium",
      "description": "Selenium",
      "contributingPlugin": "ws.palladian.nodes.selenium.plugin",
      "iconBase64": "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAA6UlEQVR4nK2TAQ2EMAxFJ2ESkIAEJCABCUiYg0lAAhKQgAQkVEKPP2hTxuB2yf2kCdnoW9u/OXco7EF7cGXQmaPJHEJgImJRjLEGlCA0DAOXVAFBJY6XZUkJ67py13U8TZNCKqpw+nPf92kRkJ8A0jtaKf3kvdcqIXxjTQHzPKcNgDDMpmkuANuShSigbduLAxCgcorsAY7I2jtOwamYugUBYmeE2RTmc+85d+FJNxdAL7nwN4DsP9qI0u1FwrrdH8cx3ZW8AsLwSpKrLDZbnTbS62MSG+EQrrlo27Zkff4ivz5nJJ2J+pw/KRs8lHJwDIkAAAAASUVORK5CYII="
    }
  ],
  "identifier": "/",
  "id": "/",
  "name": "Root",
  "contributingPlugin": "org.knime.base"
}
```

The generated `portDocumentation.json` JSON file’s structure looks as follows:

```
{
  "name": "PortObject",
  "objectClass": "org.knime.core.node.port.PortObject",
  "specClass": "org.knime.core.node.port.PortObjectSpec",
  "color": "ff9b9b9b",
  "hidden": true,
  "registered": true,
  "children": [
    {
      "name": "WebDriver Factory",
      "objectClass": "ws.palladian.nodes.selenium.ports.AbstractWebDriverFactoryPortObject",
      "specClass": "org.knime.core.node.port.PortObjectSpec",
      "color": "408e2f",
      "hidden": false,
      "registered": true,
      "children": [
        {
          "name": "ws.palladian.nodes.selenium.WebDriverFactory2",
          "objectClass": "ws.palladian.nodes.selenium.ports.WebDriverFactory2PortObject",
          "specClass": "org.knime.core.node.port.PortObjectSpec",
          "color": "408e2f",
          "hidden": true,
          "registered": true
        },
        {
          "name": "ws.palladian.nodes.selenium.WebDriverFactory",
          "objectClass": "ws.palladian.nodes.selenium.ports.WebDriverFactoryPortObject",
          "specClass": "org.knime.core.node.port.PortObjectSpec",
          "color": "408e2f",
          "hidden": true,
          "registered": true
        }
      ]
    }
  ]
}
```

The generated `splashIcons.json` JSON file’s structure looks as follows:

```
[
  {
    "id": "selenium",
    "contributingPlugin": "ws.palladian.nodes.selenium.plugin",
    "tooltip": "Selenium",
    "icon": "iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAADk0lEQVR4nOWZDXHzMAyGDSEQAqEQDCEQDCEQzKAQCqEQAiEQAiEQsr5blCmaZLtxm353n+50u8v8o0eWbNl17rj0D50fulTq8NDLEQOWZakyvtZwrnBEeybAzvNt2y4xxm/13puGhhBSba5nAVz4xJfLZZnneeFyvV53xjVNs4zjuGtzu90kwHQWgOcTS8NIsCrUBl7XpOs6CXEKQOSTWsLDZBgGtQ3APg4wTZNqHEKL2tzvd7VN3/efB0BiSoHHeRushhTkDXLjEwBeTPodyzAairBQDNvaQLAifIXOBoDclMlr9X4mACS4n1OUtOZURv/2bICPyzMAOLywxDkv4zAaKjS6J1ZCAjTup8aJTCE+Y/SrFU7q1rmvK9h9tSdYABene9etnc8EIIjGmHvQACZjoNrkrNExYVfkAL5mIuznOKh47XMS3HEAHFSoJrXTFd9TJfULdQOIiUaD/AZPy/JZE6VQO+rpKgAnv1mF2ZsAmhqAQQIgdLjg4kJ1D/6isEN1ijvCq8KkBOBaCsCrShiq9QNIKqEp6QvzpAhgMBrQHXWLwxIAS1GNyrsDcgmrlgGw8iALENcBtv/LEMKOU7J9avcBLgmIlH0+BxC0AaztE7U+ckILDX5vxiogwfk343JTDeC1ATCRdY3kK0PtEfMkMrn5XdlYBYiVo8cACAJGps4D2kb5lZMOOVKsmGyvAETDvvYwAFfaVWAAXxnA4f/Wk4oFrADcjLmz50BfApAKLwI7CDBl5s8CRM0DuV2Hx7UEoCTXVBl3WOeflHlmDmA91NIAGyA9EcJIDQT/53lBxpGgDJF9EgcanUOmbUumGv0DIF/Y6BkFCu9y4ykHoPw7IGA0f2ZRHrgohNsaAEqiSN61nghzMV2SB8o7kc85lwAsSgLo+Dcq2FIiX6ehfMvkkign6I5u5ufCrpTWvdf0AkIAnsXeTqcwQiGV5PQ7AsII/dDeOIFnGb5CWwnQOH27Si3jO5XyT3NsIKMX5V2IX+Bv67f2AwC0A8nXksiNlQDUeHS/rxHkCW113qkwlEcFldS791MJMLlfr7t1AHxDEvuTAfxq/LjawR0cNQD63UtK7wprohdr6/QH37BC/QHwBoAlOQNe1UfKzk4ZQlieUDjQpwCiY3kgAYL79wGwO/ltwP/p9wEpwdllbniyz5Tok5SjALvayFBpUEmfzj0pRwE0L2pere3zNoDSvby2TxHAF+Odd+SkF1qbAAAAAElFTkSuQmCC",
    "icon24": "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAABbElEQVR4nLWWDbWDMAyFkYAEJCABCUioBCTgAAmVgAQkIAEJSODtg6ULXcOAt+WcnDNYcm9+W7LsJe6h40OXf+r4xNqJ/wJwrF5HHv5ommaZpmnR4r1f8jy/Q7JmEsrSdd1iyTiOd0jA3h6qqjLBdSY3sth+4CxCiSRa/X6e5/sEwzAEoLZtg0FRFLssvkJAubSRRYAdweBL/5xz1zOwVJfuwzC8O1BrorGmhgAuDIM9RRBhXJbljoAotQ2E+l1UypcjUeOQEmqc6kld18lhUH3cp48hYCkiaaIBdI7gKCMGIQaihJQo7sspglRvYgJL3giIViKhTEd7YGWgVWFcXzQj0vObLJNhHRUpAnYGDFE12htQ3/fBiZnGAHC9gNJkfV/gJ0eGFrWk9qLFwmV0dEyIEIAuUbhwjhzJSuyILr71RBhrVZ71wnG6KdRf9wNg0o/PJZ4JSIjkWIkmEOxV/Kd9uKE+i8RlP/hs+QODqrFFftZOSwAAAABJRU5ErkJggg==",
    "icon32": "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAABwUlEQVR4nMVXDdGDMAxFAhKQgIRKQMIkIAEHSKgEJCABCUhAAh+PWz7SkLSMK9u7y91aSvKaP7KiCOE2GTZZNlkzy/LW7QoD/gGjlvhfGj+RcNqB1+u1TtO0cmCN/Ywk9nAMfLMsy5NhCTzHuQwEYDtMuHEco8Y5iQwEYPvYaJrGNKYhUziOhffedLUWGngrK4F5ngMDzrngsOahrARSyuGFnxKgJOUiCSIvEEp6jt+JXDkWy7IEBKqqunyTuq5PIZT5hDNRArIEsb5S71AsyWvAGUXfsWjbVn0JbjTY7zIMg/qeRqrve5sA2MXciGcgKW8hAcIWOegwCZA7U60YSrhHJHj54rdElAAJMjfmDR7PRwhwBbJDEhCOxwmQoCStViyBc9QHtHDeIqDdxiKQwm0CmrGsBGJt9hMCyJmu60wxCXAo9Xr6GKESUkl4QezbyYZzNQduV4Gse7gyNpBQx5Pg7VZr0yYBq94t0E1TnZMDhCSB/6EUt7zyVQN4klqzZIz0W/ahNBjLU991QBvL0bpTUAaTfSx3WrajzWqdj9qv1agQRu5FXAZ7xnAD2zv8pw0pg/hC4JskTsYJrvjy3/M/uJofbKxDOwUAAAAASUVORK5CYII="
  }
]
```

Development
-----------

Run the following Maven command to compile the code, run the tests, and, if
successful, build an update site:

```
$ mvn clean verify
```

To increment the version, update the `MANIFEST.MF` and/or `feature.xml` and
run the following command to automatically update the project’s `pom.xml`
files:

```
$ mvn org.eclipse.tycho:tycho-versions-plugin:update-pom
```

Make sure to follow [Semantic Versioning][6].

Contributing
------------

Pull requests are very welcome. Feel free to discuss bugs or new features by
opening a new [issue][2].

License
-------

[GNU General Public License](http://www.gnu.org/licenses)

- - -

Copyright (c) 2017 – 2021 Philipp Katz

[1]: https://seleniumnodes.com
[2]: https://github.com/NodePit/knime-json-node-doc-generator/issues
[3]: https://seleniumnodes.com/docs
[4]: https://nodepit.com
[5]: https://en.wikipedia.org/wiki/Base64
[6]: http://semver.org
