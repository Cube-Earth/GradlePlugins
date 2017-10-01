package earth.cube.gradle.plugins.github.config;

public class GithubReleaseConfig {
	
	public String name;
	
	public String tagName;
	
	public String targetCommitish = "master";
	
	public String description;
	
	public boolean draft = false;
	
	public boolean preRelease = true;

}
