package earth.cube.gradle.plugins.github.config;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;


public class GithubExtension {
	
	public String password;
	
	public GithubReleaseConfig release = new GithubReleaseConfig();

	public GithubAssetConfig asset = new GithubAssetConfig();

	
	public GithubExtension() {
	}
	
	@javax.inject.Inject
	GithubExtension(ObjectFactory objectFactory) {
//		release = objectFactory.newInstance(GithubReleaseConfig.class);
//		release = new GithubReleaseConfig();
    }

    
    void release(Action<? super GithubReleaseConfig> action) {
        action.execute(release);
    }

    void asset(Action<? super GithubAssetConfig> action) {
        action.execute(asset);
    }
}
