package earth.cube.gradle.plugins.commons.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import earth.cube.gradle.plugins.commons.dars.InitDar;
import earth.cube.gradle.plugins.commons.eclipse_launcher.Launcher;
import earth.cube.gradle.plugins.commons.emptydirs.KeepEmptyDirsTask;
import earth.cube.gradle.plugins.commons.lib_encrypter.LibDecrypter;
import earth.cube.gradle.plugins.commons.lib_encrypter.LibEncrypter;
import earth.cube.gradle.plugins.commons.purge.PurgeTask;
import earth.cube.gradle.plugins.commons.shorten.Shortener;
import earth.cube.gradle.plugins.commons.xml_helper.XmlExtension;
import earth.cube.gradle.plugins.commons.xml_modification.ModifyXmlTask;


public class CommonsPlugin implements Plugin<Project> {

	public void apply(Project project) {
		project.getExtensions().create("xml", XmlExtension.class);
		
        project.getTasks().create("modifyXml", ModifyXmlTask.class);   

        project.getTasks().create("launchEclipse", Launcher.class);   

        project.getTasks().create("encryptLibs", LibEncrypter.class);   
        project.getTasks().create("decryptLibs", LibDecrypter.class);
        
        project.getTasks().create("keepEmptyDirs", KeepEmptyDirsTask.class);
        project.getTasks().create("purge", PurgeTask.class);

        new Shortener(project).prepare();
    	
    	new InitDar(project).execute();
	}

}
