package earth.cube.gradle.plugins.commons.dars;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;

import earth.cube.gradle.plugins.commons.utils.gradle.GradleUtils;
import earth.cube.gradle.plugins.commons.xml_helper.XmlExtension;

public class UpdateDarTask extends DefaultTask {
	
	public UpdateDarTask() {
		setDependsOn(Arrays.asList("jar"));
	}
	
	@TaskAction
	public void copy() throws IOException {
		Project project = getProject();
		
		String sComposerProjDir = GradleUtils.findProperty(project, "composerProjectsDir");
		String sDarName = (String) project.getProperties().get("dar");
		String sDstRelPath = new XmlExtension().getString(sComposerProjDir + '/' + sDarName + "/Artifacts/JAR Definitions/" + project.getName().toLowerCase() + ".jardef", "/Artifact/contentStore/contentEntries/value/@filePath");
		
		Jar jarTask = (Jar) project.getTasks().findByName("jar");
		File dstFile = new File(sComposerProjDir + '/' + sDarName + '/' + sDstRelPath);
		File srcFile = jarTask.getArchivePath();
		
		Files.copy(srcFile.toPath(), dstFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
	}
}
