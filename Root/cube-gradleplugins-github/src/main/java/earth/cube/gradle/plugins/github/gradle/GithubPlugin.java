package earth.cube.gradle.plugins.github.gradle;

import org.gradle.BuildListener;
import org.gradle.BuildResult;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;

import earth.cube.gradle.plugins.github.config.GithubCacheExtension;
import earth.cube.gradle.plugins.github.config.GithubExtension;

public class GithubPlugin implements Plugin<Project>, BuildListener {
    public void apply(Project project) {
        project.getExtensions().create("github", GithubExtension.class);
        
        project.getTasks().create("uploadArchives", UploadArchivesTask.class);
        
        project.getGradle().addBuildListener(this);
    }

	@Override
	public void buildStarted(Gradle gradle) {
	}

	@Override
	public void buildFinished(BuildResult result) {
		result.getGradle().getRootProject().getLogger().quiet("clean after finished");
		GithubCacheExtension.get(result.getGradle().getRootProject()).clean();
	}

	@Override
	public void projectsEvaluated(Gradle gradle) {
		gradle.getRootProject().getLogger().quiet("clean after evaluated");
		GithubCacheExtension.get(gradle.getRootProject()).clean();
	}

	@Override
	public void projectsLoaded(Gradle gradle) {
	}

	@Override
	public void settingsEvaluated(Settings settings) {
	}
}
