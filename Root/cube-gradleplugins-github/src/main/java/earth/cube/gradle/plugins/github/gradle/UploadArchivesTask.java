package earth.cube.gradle.plugins.github.gradle;

import java.io.File;
import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.tasks.TaskAction;

import earth.cube.gradle.plugins.github.config.GithubAssetConfig;
import earth.cube.gradle.plugins.github.config.GithubCacheExtension;
import earth.cube.gradle.plugins.github.config.GithubExtension;
import earth.cube.gradle.plugins.github.model.GithubRelease;

public class UploadArchivesTask extends DefaultTask {
	
	@TaskAction
	public void upload() throws IOException {
		GithubExtension config = (GithubExtension) getProject().getExtensions().findByName("github");
		GithubRelease release = GithubCacheExtension.get(getProject()).getRelease(config, getLogger());
		
		GithubAssetConfig assetConfig = config.asset;
		
		Configuration archives = getProject().getConfigurations().findByName("archives");
		for(PublishArtifact a : archives.getAllArtifacts()) {
			File file = a.getFile();
			String sName = assetConfig.name != null && assetConfig.name.length() != 0 ? String.format(assetConfig.name, file.getName()) : file.getName();
			String sLabel = assetConfig.label != null && assetConfig.label.length() != 0 ? String.format(assetConfig.label, file.getName()) : file.getName();
			
			release.uploadAsset(sName, sLabel, file, assetConfig.overwrite);
		}
	}

}
