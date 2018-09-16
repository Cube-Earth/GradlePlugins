package earth.cube.gradle.plugins.commons.utils;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitRepoFinder {
	
	public static Repository findGitRepo(File currentDir) throws IOException {
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
		repositoryBuilder.setMustExist(true);
		repositoryBuilder.findGitDir(currentDir);
		if(repositoryBuilder.getGitDir() == null)
			throw new IllegalStateException("Could not determine GIT database directory!");
		return repositoryBuilder.build();
	}

}
