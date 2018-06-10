package earth.cube.gradle.plugins.commons.xml.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.xpath.XPathConstants;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import earth.cube.gradle.plugins.commons.utils.XmlUtils;

public class XmlUtilsTest {
	
	@Test
	public void test_setValue_elem_1() throws IOException {
		Document doc = XmlUtils.parse(new ByteArrayInputStream("<a><b>aa<c>bb</c></b>cc</a>".getBytes()));
		Node node = XmlUtils.evaluate(doc.getDocumentElement(), "/a/b", XPathConstants.NODE);
		XmlUtils.setValue(node, "dd");
		Assert.assertEquals("<a><b>dd</b>cc</a>", XmlUtils.getString(doc).replaceFirst("<\\?xml .*?\\?>", ""));
	}

	@Test
	public void test_setValue_attr_1() throws IOException {
		Document doc = XmlUtils.parse(new ByteArrayInputStream("<a><b d=\"ee\">aa<c>bb</c></b>cc</a>".getBytes()));
		Node node = XmlUtils.evaluate(doc.getDocumentElement(), "/a/b/@d", XPathConstants.NODE);
		XmlUtils.setValue(node, "fff");
		Assert.assertEquals("<a><b d=\"fff\">aa<c>bb</c></b>cc</a>", XmlUtils.getString(doc).replaceFirst("<\\?xml .*?\\?>", ""));
	}
}
