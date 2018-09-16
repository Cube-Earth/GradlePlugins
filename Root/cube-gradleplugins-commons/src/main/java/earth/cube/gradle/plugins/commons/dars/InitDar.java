package earth.cube.gradle.plugins.commons.dars;

import java.io.File;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class InitDar {
	
	private Project _project;
	
	public InitDar(Project project) {
		_project = project;
	}
	
	
	private void locateComposerProjectsDir(File dir) {
		if(dir == null)
			throw new IllegalStateException(_project.getRootDir().getAbsolutePath());
		File composerDir = new File(dir, "composer");
		if(composerDir.exists()) {
			_project.setProperty("composerProjectsDir", composerDir.getAbsolutePath());
		}
		else {
			composerDir = new File(dir, "Composer");
			if(composerDir.exists()) {
				_project.setProperty("composerProjectsDir", composerDir.getAbsolutePath());
			}
			else {
				locateComposerProjectsDir(dir.getParentFile());
			}
		}
	}
	

	private void addUpdateDarTask() {
		_project.allprojects(new Action<Project>() {

			@Override
			public void execute(Project project) {
				
				project.afterEvaluate(new Action<Project>() {

					@Override
					public void execute(final Project project) {
						if(project.hasProperty("dar")) {
							project.getTasks().create("updateDar", UpdateDarTask.class);
						}
					}
					
				});
			}
		});
	}
	
	
	private void addCreateDarTask() {
		_project.getTasks().create("createDar", CreateDarTask.class);
		
		_project.afterEvaluate(new Action<Project>() {

			@Override
			public void execute(Project project) {
				Task task = project.getTasks().findByName("createDar");
				task.setDependsOn(project.getTasksByName("updateDar", true));
			}
			
		});
	}
	
	
	public void execute() {
		locateComposerProjectsDir(_project.getProjectDir());
		addUpdateDarTask();
		addCreateDarTask();
	}

}
