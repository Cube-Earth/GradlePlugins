package earth.cube.gradle.plugins.commons.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CommonsPluginTest implements Plugin<Project> {

	public void apply(Project project) {
		new CommonsPlugin().apply(project);

	}

}
