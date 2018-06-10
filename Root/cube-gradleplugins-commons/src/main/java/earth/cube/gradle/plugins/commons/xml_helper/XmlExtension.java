package earth.cube.gradle.plugins.commons.xml_helper;

import java.io.File;
import java.io.IOException;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;

import earth.cube.gradle.plugins.commons.utils.XmlUtils;

public class XmlExtension {
	
	public String getString(String file, String select, String... saNamespace) throws IOException {
		Document doc = XmlUtils.parse(new File(file), saNamespace.length > 0);
		return XmlUtils.evaluate(doc.getDocumentElement(), select, XPathConstants.STRING, saNamespace);
	}

	public String getString(File file, String select, String... saNamespace) throws IOException {
		Document doc = XmlUtils.parse(file, saNamespace.length > 0);
		return XmlUtils.evaluate(doc.getDocumentElement(), select, XPathConstants.STRING, saNamespace);
	}
}
