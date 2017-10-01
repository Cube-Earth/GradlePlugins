package earth.cube.gradle.plugins.github.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import earth.cube.gradle.plugins.github.model.GithubRelease;
import earth.cube.gradle.plugins.github.model.GithubRepository;

public class GithubCacheExtension {
	
	private GithubRepository _repository;
	private Map<String,GithubRelease> _releases = new HashMap<>();
	
	public synchronized void clean() {
		_repository = null;
		_releases.clear();
	}
	
	public synchronized GithubRepository getRepository(GithubExtension config) throws IOException {
		if(_repository == null) {
			_repository = new GithubRepository(null);
			_repository.setConfig(config);
		}
		return _repository;
	}

	public synchronized GithubRelease getRelease(GithubReleaseConfig config, Logger logger) throws IOException {
		String sName = config.name != null ? config.name : config.tagName;
		GithubRelease release = _releases.get(sName);
		if(release == null) {
			release = new GithubRelease(_repository);
			release.setLogger(logger);
			release.setConfig(config);
			release.ensure();
			_releases.put(sName, release);
		}
		else
			release.setLogger(logger);
		return release;
	}

	public synchronized GithubRelease getRelease(GithubExtension config, Logger logger) throws IOException {
		getRepository(config);
		return getRelease(config.release, logger);
	}
	
	public static GithubCacheExtension get(Project project) {
        GithubCacheExtension cache = (GithubCacheExtension) project.getRootProject().getExtensions().findByName("githubCache");
        if(cache == null) {
        	cache = project.getRootProject().getExtensions().create("githubCache", GithubCacheExtension.class);
        }
        return cache;
	}

}
