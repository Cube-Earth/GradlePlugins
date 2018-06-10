package earth.cube.gradle.plugins.commons.xml_modification;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPathConstants;

import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.TaskAction;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import earth.cube.gradle.plugins.commons.utils.XmlUtils;
import earth.cube.gradle.plugins.commons.utils.gradle.GradleUtils;
import groovy.lang.Closure;

public class ModifyXmlTask extends DefaultTask {
	
	public List<XmlFileConfig> _files = new ArrayList<>();

	public ModifyXmlTask() {
		getOutputs().upToDateWhen(new Spec<Task>() {
            public boolean isSatisfiedBy(Task element) {
                return false;
            }
        });
	}
	
	public void file(@SuppressWarnings("rawtypes") Closure c) {
		XmlFileConfig f = GradleUtils.evaluate(c, XmlFileConfig.class);
		_files.add(f);
	}

	private void process(XmlFileConfig file) throws IOException {
		boolean bModified = false;
		Document doc = XmlUtils.parse(new File(file.path));
		for(XmlModificationConfig mod : file.getModifications()) {
			Node node = XmlUtils.evaluate(doc.getDocumentElement(), mod.select, XPathConstants.NODE);
			bModified |= XmlUtils.setValue(node, mod.value);
		}
		if(bModified) {
			File f = new File(file.path);
			if(file.createBackup && f.exists()) {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
				String sNewName = String.format("%s.bac-%s", file.path, df.format(new Date()));
				f.renameTo(new File(sNewName));
			}
			XmlUtils.saveTo(doc, f);
			getLogger().info("File '" + file.path + "' modified!");
		}
	}

	@TaskAction
	public void modify() throws IOException {
		for(XmlFileConfig file : _files)
			process(file);
	}


}
