package earth.cube.gradle.plugins.commons.purge;

import java.io.IOException;

import org.gradle.api.DefaultTask;

import earth.cube.gradle.plugins.commons.utils.GitRepoFinder;

public class PurgeTask extends DefaultTask {
	
	protected void init() throws IOException {
		Purge worker = new Purge(GitRepoFinder.findGitRepo(getProject().getRootDir()));
		if(getProject().hasProperty("probe"))
			worker.enableProbe();
		worker.setLogger(getLogger());
		worker.execute();
	}

}
