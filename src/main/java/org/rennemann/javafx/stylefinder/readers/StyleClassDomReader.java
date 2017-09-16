/*
 * Copyright 2017 Rennemann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rennemann.javafx.stylefinder.readers;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * DOM reader to get style classes.
 *
 * @author Travis Rennemann
 */
public class StyleClassDomReader {

    private final File file;

    /**
     * Construct with an XML file.
     *
     * @param file The file to search
     */
    public StyleClassDomReader(File file) {
        this.file = file;
    }

    /**
     * Find all of the styleClass values in the given input string.
     *
     * @return
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    public Set<String> find() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        docBuilder = dbFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(file);
        return findStyleInNode(doc.getDocumentElement());
    }

    /**
     * Find styles in the node if it's an element.
     *
     * the XML element will look like this:
     * <styleClass>
     * <String fx:value="green_fill" />
     * <String fx:value="line_item" />
     * <String fx:value="header" />
     * </styleClass>
     *
     * @param node The DOM node to search
     * @return
     */
    private Set<String> findStyleInNode(Node node) {
        Set<String> styleClasses = new HashSet<>();
        // only process Elements in the DOM
        if (node instanceof Element) {
            String style = findStylesInAttributes((Element) node);
            // add the style to the styles list if it hasn't already been added
            addUnique(styleClasses, style);
            // if this element is a styleClass, process its styles
            if (node.getNodeName().equals("styleClass")) {
                NodeList styleNodes = node.getChildNodes();
                for (int n = 0; n < styleNodes.getLength(); n++) {
                    if (styleNodes.item(n).getNodeName().equals("String")) {
                        Node stringNode = styleNodes.item(n).getAttributes().getNamedItemNS("http://javafx.com/fxml/1", "value");
                        if (stringNode != null) {
                            String stringNodeVal = stringNode.getNodeValue();
                            addUnique(styleClasses, stringNodeVal);
                        }
                    }
                }
            }
        }
        // if this node has children, check each child node for styles
        if (node.hasChildNodes()) {
            NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Set<String> childStyles = findStyleInNode(nodeList.item(i));
                // process any styles returned
                if (childStyles != null && childStyles.size() > 0) {
                    for (String childStyle : childStyles) {
                        addUnique(styleClasses, childStyle);
                    }
                }
            }
        }
        return styleClasses;
    }

    /**
     * Only add unique values to the collection.
     *
     * @param list The list to add into
     * @param value The candidate value
     */
    private void addUnique(Set<String> list, String value) {
        if (list != null && value != null) {
            list.add(value);
        }
    }

    /**
     * Return a unique list of styles found in the given element's attributes.
     *
     * the XML element will look something like this:
     * <Button styleClass="run-ico">
     *
     * @param element The XML DOM element to search
     * @return
     */
    private String findStylesInAttributes(Element element) {
        // get a map containing the attributes of this node
        NamedNodeMap attributes = element.getAttributes();
        // get the number of nodes in this map
        int numAttrs = attributes.getLength();

        for (int i = 0; i < numAttrs; i++) {
            Attr attr = (Attr) attributes.item(i);
            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();
            if (attrName.equals("styleClass")) {
                return attrValue;
            }
        }

        return null;
    }
}
