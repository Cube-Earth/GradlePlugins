package earth.cube.gradle.plugins.commons.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

public class DefaultXPathNamespaceContext implements NamespaceContext {
	
	private Map<String,String> _namespaces = new HashMap<>();
	
	public DefaultXPathNamespaceContext(String... saNamespace) {
		for(String sNamespace : saNamespace) {
			int i = sNamespace.indexOf('=');
			if(i == -1)
				throw new IllegalStateException("Malformed namespace definition!");
			_namespaces.put(sNamespace.substring(0, i), sNamespace.substring(i+1));
		}
	}
	
	
	@Override
	public String getNamespaceURI(String sPrefix) {		
		return _namespaces.get(sPrefix);
	}

	@Override
	public String getPrefix(String sNamespaceURI) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator getPrefixes(String namespaceURI) {
		throw new UnsupportedOperationException();
	}

}
