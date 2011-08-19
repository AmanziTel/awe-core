/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.cassidian.loader.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.amanzi.awe.cassidian.constants.LoaderConstants;
import org.amanzi.awe.cassidian.structure.IXmlTag;
import org.amanzi.awe.cassidian.structure.TNSElement;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public abstract class AbstractHandler extends DefaultHandler {
	protected static String currentElement = "";
	protected boolean tagCondition = false;
	protected static Map<String, Class<? extends IXmlTag>> innerElement;
	protected static Map<String, Class<? extends IXmlTag>> mainElement;

	protected static IXmlTag tag;
	protected IXmlTag parent = new TNSElement();
	protected static IXmlTag parentElement;
	protected static SAXParserFactory factory;
	protected static SAXParser saxParser;
	protected static TNSElement tns = new TNSElement();

	StringBuffer charBuilder;

	public abstract IXmlTag parseElement(File file);

	private void resetElementsMap() {
		if (innerElement != null) {
			innerElement.clear();

		} else {
			innerElement = new HashMap<String, Class<? extends IXmlTag>>();
		}
		if (mainElement != null) {
			mainElement.clear();
		} else {
			mainElement = new HashMap<String, Class<? extends IXmlTag>>();
		}
	}

	private void resetSax() {
		if (factory == null) {
			factory = SAXParserFactory.newInstance();
		}
		if (saxParser == null) {
			try {
				saxParser = factory.newSAXParser();
			} catch (ParserConfigurationException e) {
				// TODO Handle ParserConfigurationException
				throw (RuntimeException) new RuntimeException().initCause(e);
			} catch (SAXException e) {
				// TODO Handle SAXException
				throw (RuntimeException) new RuntimeException().initCause(e);
			}
		}
	}

	protected AbstractHandler() {
		// parentElement=element;
		resetElementsMap();
		resetSax();
	}

	protected void parse(File file) {
		try {
			saxParser.parse(file, this);
		} catch (SAXException e) {
			// TODO Handle SAXException
			throw (RuntimeException) new RuntimeException().initCause(e);
		} catch (IOException e) {
			// TODO Handle IOException
			throw (RuntimeException) new RuntimeException().initCause(e);
		}
	}

	public void startElement(String uri, String localName, String tagName,
			Attributes attributes) throws SAXException {
		try {
			currentElement = tagName;

			if (mainElement.containsKey(tagName)) {
				parentElement = mainElement.get(tagName).newInstance();
			} else if (innerElement.containsKey(tagName)) {
				if (tagCondition) {
					parent = tag;
				}
				tag = innerElement.get(tagName).newInstance();
				parent.setValueByTagType(tagName, tag);
				tagCondition = true;
			}
			charBuilder = new StringBuffer();

			System.out.println("Start Element :" + tagName);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		if (ch != null) {
			charBuilder.append(ch, start, length);
		}

	}

	public void endElement(String uri, String localName, String tagName)
			throws SAXException {
		if (!innerElement.containsKey(tagName)
				&& !mainElement.containsKey(tagName)
				&& !tagName.equals(tns.getType())) {
			if (charBuilder.length() == 0) {
				tag.setValueByTagType(currentElement, null);
			} else {
				tag.setValueByTagType(currentElement, charBuilder);
			}
		}

		if ((parent.getClass().isInstance(tns))) {
			parent = tag;
		}

		if (innerElement.containsKey(tagName)) {
			if(tagName.equals("servingData")){
			parentElement.setValueByTagType(tagName, tag);
			}else {
				parentElement.setValueByTagType(tagName, parent);
			}
		} else if (mainElement.containsKey(tagName)) {
			tns.setValueByTagType(tagName, parentElement);
		}
		if (innerElement.containsKey(tagName)) {
			tagCondition = false;
		}

		charBuilder = null;
		System.out.println("End Element :" + tagName);
	}
}
