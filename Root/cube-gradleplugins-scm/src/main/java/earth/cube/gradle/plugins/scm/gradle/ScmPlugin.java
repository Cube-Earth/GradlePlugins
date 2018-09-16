package earth.cube.gradle.plugins.scm.gradle;

import java.io.File;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

public class ScmPlugin implements Plugin<Project> {
    public void apply(Project project) {
//        project.getExtensions().create("github", GithubExtension.class);
        
		JavaPlugin java = project.getPlugins().getPlugin(JavaPlugin.class);
		if(java != null) {
	        /*
	        groovy:
	        -------
		  	eclipseClasspath.doFirst {
		    	sourceSets*.java.srcDirs*.each { it.mkdirs() }
		    	sourceSets*.resources.srcDirs*.each { it.mkdirs() }
		  	}         
		  	*/

			Task task = project.getTasks().create("eclipseClasspath", Task.class);
	        task.doFirst(new Action<Task>() {
	
				@Override
				public void execute(Task task) {
					Project proj = task.getProject();
					JavaPluginConvention javaConvention = proj.getConvention().getPlugin(JavaPluginConvention.class);

					SourceSet main = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
				    for(File dir : main.getJava().getSrcDirs())
				    	dir.mkdirs();
				    for(File dir : main.getResources().getSrcDirs())
				    	dir.mkdirs();
				    
				    SourceSet test = javaConvention.getSourceSets().getByName(SourceSet.TEST_SOURCE_SET_NAME);
				    for(File dir : test.getJava().getSrcDirs())
				    	dir.mkdirs();
				    for(File dir : test.getResources().getSrcDirs())
				    	dir.mkdirs();
				    
				    // TODO add "bin", "build" to svnignore / .gitignore
				}
	        	
	        });
		}
    }

}
