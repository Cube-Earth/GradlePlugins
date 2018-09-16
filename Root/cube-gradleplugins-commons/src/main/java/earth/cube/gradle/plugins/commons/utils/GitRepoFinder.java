package earth.cube.gradle.plugins.commons.utils;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitRepoFinder {
	
	public static Repository findGitRepo(File currentDir) throws IOException {
		return findGitRepo(currentDir, true);
	}

	public static Repository findGitRepo(File currentDir, boolean bRaiseException) throws IOException {
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
		repositoryBuilder.setMustExist(true);
		repositoryBuilder.findGitDir(currentDir);
		if(repositoryBuilder.getGitDir() == null)
			if(bRaiseException)
				throw new IllegalStateException("Could not determine GIT database directory!");
			else
				return null;
		return repositoryBuilder.build();
	}

}
