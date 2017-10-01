package earth.cube.gradle.plugins.github.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import earth.cube.gradle.plugins.github.config.GithubExtension;

public class GithubPlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getExtensions().create("github", GithubExtension.class);
        project.getTasks().create("uploadArchives", UploadArchivesTask.class);
    }
}
