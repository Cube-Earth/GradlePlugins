package earth.cube.gradle.plugins.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class XmlUtils {

	public static Document parse(InputStream is, boolean bNamespaceAware) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(bNamespaceAware);
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(is);
		} catch (ParserConfigurationException | SAXException e) {
			throw new IOException(e);
		}
	}

	public static Document parse(InputStream is) throws IOException {
		return parse(is, false);
	}

	public static Document parse(File file, boolean bNamespaceAware) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			return parse(is, bNamespaceAware);
		}
	}

	public static Document parse(File file) throws IOException {
		return parse(file, false);
	}

	@SuppressWarnings("unchecked")
	public static <T> T evaluate(Element elem, String sXPath, QName returnType, String... saNamespace) throws IOException {
		try {
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			if(saNamespace.length != 0)
				xpath.setNamespaceContext(new DefaultXPathNamespaceContext(saNamespace));
			XPathExpression expr = xpath.compile(sXPath);
			return (T) expr.evaluate(elem, returnType);
		} catch (XPathExpressionException e) {
			throw new IOException(e);
		}
	}

	public static <T extends Node> XmlNodeIterator<T> evaluateAsNodeList(Element elem, String sXPath, Class<T> c) throws IOException {
		return new XmlNodeIterator<T>((NodeList) evaluate(elem, sXPath, XPathConstants.NODESET));
	}

	public static XPathExpression compile(String sXPath) throws IOException {
		try {
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			return xpath.compile(sXPath);
		} catch (XPathExpressionException e) {
			throw new IOException(e);
		}
	}

	public static String getString(Document doc) {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static String getString(Element elem) throws IOException {
		return evaluate(elem, "text()", XPathConstants.STRING);
	}


	public static String getPrettyString(Document doc) {
		try {
			doc.normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			for(Node node : new XmlNodeIterator<Node>((NodeList) xPath.evaluate("//text()[normalize-space()='']", doc, XPathConstants.NODESET))) {
				node.getParentNode().removeChild(node);
			}

			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setAttribute("indent-number", 2);
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StringWriter sw = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(sw));
			return sw.toString();
		} catch (XPathExpressionException | DOMException | TransformerException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static void visitDepthElements(Element elem, IElementVisitor visitor) throws IOException {
		if(elem == null)
			return;
		if(!visitor.visit(elem))
			return;
		for(Node node : new XmlNodeIterator<Node>(elem.getChildNodes())) {
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				if(!visitor.visit((Element) node))
					return;
			}
		}
	}

	public static void visitDepthAttributes(Element elem, IAttributeVisitor visitor) {
		if(elem == null)
			return;
		
		for(Node node : new XmlNodeIterator<Node>(elem.getChildNodes())) {
			if(node.getNodeType() == Node.ATTRIBUTE_NODE) {
				if(!visitor.visit((Attr) node))
					return;
			}
		}
	}
	
	public static void removeNodes(Element elem, String sXPath) throws DOMException, IOException {
		for(Node node : XmlUtils.evaluateAsNodeList(elem, sXPath, Node.class)) {
			node.getParentNode().removeChild(node);
		}
	}
	
	public static void removeAllNodes(Element elem) {
		for(Node node : XmlUtils.clone(elem.getChildNodes())) {
			node.getParentNode().removeChild(node);
		}
	}
	
	public static List<Node> clone(NodeList nl) {
		List<Node> nodes = new ArrayList<>();
		for(Node node : new XmlNodeIterator<Node>(nl)) {
			nodes.add(node);
		}
		return nodes;
	}
	
	public static boolean setValue(Node node, String sValue) {
		boolean bModified = false;
		switch(node.getNodeType()) {
			case Node.ATTRIBUTE_NODE:
				Attr attr = (Attr) node;
				if(!sValue.equals(attr.getValue())) {
					attr.setValue(sValue);
					bModified = true;
				}
				break;
				
			case Node.ELEMENT_NODE:
				Element elem = (Element) node;
				if(elem.getChildNodes().getLength() != 1) {
					Node child = elem.getChildNodes().item(0);
					String s = child.getNodeType() == Node.TEXT_NODE ? ((Text) child).getTextContent() : null;
					if(s == null || !s.equals(sValue)) {
						removeAllNodes(elem);
						Text text = elem.getOwnerDocument().createTextNode(sValue);
						elem.appendChild(text);
						bModified = true;
					}
				}
				break;
				
			default:
				throw new IllegalArgumentException("Node type " + node.getNodeType() + " is not supported!");
		}
		return bModified;
	}
	
	public static String getPath(Element parent, Element elem) {
		if(elem == null)
			return null;
		List<Integer> positions = new ArrayList<>();
		if(parent == null)
			parent = elem.getOwnerDocument().getDocumentElement();
		Node curr = elem;
		while(curr != null && curr != parent) {
			int i = 0;
			while(curr.getPreviousSibling() != null) {
				curr = curr.getPreviousSibling();
				if(curr.getNodeType() == Node.ELEMENT_NODE)
					i++;
			}
			positions.add(0, i);
			curr = curr.getParentNode();
		}
		if(curr != parent)
			throw new IllegalArgumentException("Specified parent element '" + parent.getTagName() + "' is not parent of specified element '" + elem.getTagName() + "'!");
		return StringUtils.join(positions, ".");
	}

	public static String getPath(Element elem) {
		return getPath(null, elem);
	}
	
	
	public static Element getElementByPath(Element root, String sPosition) {
		if(sPosition == null || sPosition.length() == 0)
			return root;
		
		List<Integer> positions = StringUtils.split(sPosition, "\\.", new IConverter<String, Integer>() {

			@Override
			public Integer convert(String sValue) {
				return Integer.parseInt(sValue);
			}
		});
		
		Node curr = root;
		while(positions.size() > 0) {
			int i = positions.remove(0);
			curr = curr.getFirstChild();
			while(i == 0 && curr.getNodeType() != Node.ELEMENT_NODE) {
				if(curr.getNodeType() == Node.ELEMENT_NODE)
					i--;
				curr = curr.getNextSibling();
				if(curr == null)
					throw new IllegalArgumentException("Position '" + sPosition + "' is invalid!");
			}
		}
		return (Element) curr;
	}

	public static void saveTo(Document doc, File file) throws FileNotFoundException, IOException {
		String s = getPrettyString(doc);
		try(FileOutputStream out = new FileOutputStream(file)) {
			out.write(s.getBytes("utf-8"));
		}
	}
}
