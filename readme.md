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
	-plugin plugin-id : Only nodes of the specified plugin will be considered. If not all available plugins will be processed.
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
  "id": "selenium",
  "name": "Selenium",
  "nodes": [
    {
      "identifier": "ws.palladian.nodes.selenium.factory.bulk.BulkRemoteWebDriverFactoryNodeFactory",
      "name": "Bulk RemoteWebDriver Factory",
      "shortDescription": "Creates a factory loop for RemoteWebDrivers using different configurations.",
      "intro": "",
      "options": [
        {
          "name": "URL",
          "description": "The remote server URL"
        }
      ],
      "inPorts": [
        {
          "index": 0,
          "name": "Configuration",
          "description": "Table with the configuration rows for the WebDriver.",
          "optional": false
        }
      ],
      "outPorts": [
        {
          "index": 0,
          "name": "WebDriver Factory",
          "description": "A configured WebDriver factory."
        },
        {
          "index": 1,
          "name": "Current configuration",
          "description": "A table with the current row of the configuration input."
        }
      ]
    },

  ]
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
