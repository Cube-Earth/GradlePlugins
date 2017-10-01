package earth.cube.gradle.plugins.github.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import earth.cube.gradle.plugins.github.config.GithubExtension;
import earth.cube.gradle.plugins.github.config.GithubReleaseConfig;

public class Cache {
	
	private static GithubRepository _repository;
	private static Map<String,GithubRelease> _releases = new HashMap<>();

	public static synchronized GithubRepository getRepository(GithubExtension config) throws IOException {
		if(_repository == null) {
			_repository = new GithubRepository(null);
			_repository.setConfig(config);
		}
		return _repository;
	}

	public static synchronized GithubRelease getRelease(GithubReleaseConfig config) throws IOException {
		String sName = config.name != null ? config.name : config.tagName;
		GithubRelease release = _releases.get(sName);
		if(release == null) {
			release = new GithubRelease(_repository);
			release.setConfig(config);
			release.ensure();
			_releases.put(sName, release);
		}
		return release;
	}

	public static synchronized GithubRelease getRelease(GithubExtension config) throws IOException {
		getRepository(config);
		return getRelease(config.release);
	}

}
