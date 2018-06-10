package earth.cube.gradle.plugins.commons.xml_modification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import earth.cube.gradle.plugins.commons.utils.gradle.GradleUtils;
import groovy.lang.Closure;
import groovy.transform.Canonical;

@Canonical
public class XmlFileConfig {
	
	public String path;
	
	public boolean createBackup = false;
	
	private List<XmlModificationConfig> _modifications = new ArrayList<>();
	
	public void modification(@SuppressWarnings("rawtypes") Closure c) {
		XmlModificationConfig m = GradleUtils.evaluate(c, XmlModificationConfig.class);
		_modifications.add(m);
	}
	
	public List<XmlModificationConfig> getModifications() {
		return Collections.unmodifiableList(_modifications);
	}

}
