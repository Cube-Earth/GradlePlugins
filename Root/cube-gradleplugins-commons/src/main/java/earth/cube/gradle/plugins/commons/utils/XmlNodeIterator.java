package earth.cube.gradle.plugins.commons.utils;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlNodeIterator<T extends Node> implements Iterator<T>, Iterable<T> {
	
	private NodeList _nodes;
	private int _nCount;
	private int _nIdx;

	public XmlNodeIterator(NodeList nodes) {
		_nodes = nodes;
		_nCount = nodes.getLength();
		_nIdx = 0;
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return _nIdx < _nCount;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T next() {
		return (T) _nodes.item(_nIdx++);
	}
	

}
