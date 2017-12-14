KNIME JSON Node Documentation Generator
=======================================

![Run status](https://api.shippable.com/projects/590caf4e50a8640700690ab1/badge?branch=master)

Builds a JSON file containing KNIME node documentations, based on and similar to
KNIME’s `org.knime.workbench.repository.NodeDocumentationGenerator`, but with a
structured JSON file as a result which can be easily further processed, mined
and integrated in different environments. Created for the [Selenium Nodes][1]
website where we display the node documentation on [this][3] page.

Installation
------------

The tool is available through the following software site:

```
http://philippkatz.de/knime/repository/jsondocgen
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
```

This example creates a JSON file in you home directory with the documentation for all
Selenium nodes:

```
$ ./Knime -nosplash -application de.philippkatz.knime.jsondocgen.application.JsonNodeDocumentationGenerator -destination ~ -category /selenium
```

The generated JSON file’s structure looks as follows:

```json
{
  "children": [
    {
      "nodes": [
        {
          "intro": "<p>This node allows interacting with JavaScript-based <a href=\"https://developer.mozilla.org/en-US/docs/Web/API/Window/alert\">alert</a>, \n        <a href=\"https://developer.mozilla.org/en-US/docs/Web/API/Window/confirm\">confirm</a>, and \n        <a href=\"https://developer.mozilla.org/en-US/docs/Web/API/Window/prompt\">prompt</a> dialog boxes. \n        When a WebBrowser shows a dialog, these can be dismissed or confirmed. Further, it allows to \n        extract a dialog's text and send keyboard input to the dialog. The node assumes, that a \n        dialog box is currently shown in the browser window.</p><p><b>Important:</b> This node does <b>not</b> work with headless browsers (PhantomJS, jBrowser, \n        HtmlUnit, headless Chrome). In case you want to influence the page's dialog behavior when running\n        headless browsers, you can directly modify the <tt>window.alert</tt>, <tt>window.confirm</tt>, \n        or <tt>window.prompt</tt> functions using an <b>Execute JavaScript</b> node, which needs to be \n        placed at a point in the workflow, before the dialog is actually shown. The following code\n        snippet gives a simple example how to globally override the default behavior:</p><pre>\nwindow.alert = function() {\n   // do nothing\n}\nwindow.confirm = function() {\n  return true; // simulate, that all 'confirm' dialogs are closed via 'OK' button\n}\nwindow.prompt = function() {\n  return 'dummy text'; // simulate, that the given string is entered into all 'prompt' dialogs\n}\n        </pre>",
          "options": [
            {
              "name": "WebDriver input",
              "description": "Input column which provides the WebDriver(s)",
              "optional": false
            },
            {
              "name": "Dismiss",
              "description": "Close the dialog by clicking the \"Cancel\" button.",
              "optional": false
            },
            {
              "name": "Accept",
              "description": "Close the dialog by clicking the \"OK\" button.",
              "optional": false
            },
            {
              "name": "Append column with text",
              "description": "Extract the dialog's message and append it as text column.",
              "optional": false
            },
            {
              "name": "Send keys",
              "description": "The text to send to the dialog (in case it is a \"prompt\" dialog).",
              "optional": false
            }
          ],
          "inPorts": [
            {
              "index": 0,
              "name": "WebDriver",
              "description": "Table with a column providing a WebDriver with an open alert, confirm, or prompt box.",
              "optional": false
            }
          ],
          "outPorts": [
            {
              "index": 0,
              "name": "WebDriver",
              "description": "Same as input table, and appended text column in case the \"Save text\" option was selected."
            }
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


Contributing
------------

Pull requests are very welcome. Feel free to discuss bugs or new features by
opening a new [issue][2].

License
-------

[GNU General Public License](http://www.gnu.org/licenses)

- - -

Copyright (c) 2017 Philipp Katz

[1]: https://seleniumnodes.com
[2]: https://github.com/qqilihq/knime-json-node-doc-generator/issues
[3]: https://seleniumnodes.com/docs
