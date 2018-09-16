package earth.cube.gradle.plugins.commons.dars;

import java.io.IOException;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.api.tasks.TaskAction;

import earth.cube.gradle.plugins.commons.eclipse_launcher.Launcher;
import earth.cube.gradle.plugins.commons.utils.gradle.GradleUtils;

public class CreateDarTask extends DefaultTask {

	@TaskAction
	public void create() throws Exception {

		final Project project = getProject();
		final Project rootProject = project.getRootProject();
		
		rootProject.delete("$buildDir/composer");
		
		Launcher launcher = project.getTasks().create("__createDarLauncher__", Launcher.class, new Action<Launcher>() {

			@Override
			public void execute(Launcher launcher) {
				try {
					launcher.setApplication("earth.cube.eclipse.darbuilder.headless");
					launcher.setInstallationDir((String) GradleUtils.findProperty(project, "composerInstallationHome"));
					launcher.setWorkspaceDir(GradleUtils.getBuildDir(rootProject, "composer"));
					launcher.addPlugin("https://github.com/Cube-Earth/EclipsePlugins/releases/download/1.0/earth.cube.eclipse.darbuilder-1.0.0.jar");
					launcher.addArguments("-projectsDir", (String) GradleUtils.findProperty(project, "composerProjectsDir"), "-outputDir", GradleUtils.getBuildDir(rootProject, "dar"));
				} catch (IOException e) {
					throw new StopExecutionException(e.getMessage());
				}
			}
			
		});
		
		launcher.launch();
		
	}
	
}
