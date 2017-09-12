package earth.cube.gradle.plugins.github;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class LocalRepository {
	
	private Repository _repository;

	public LocalRepository(File gitDir) throws IOException {
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
		repositoryBuilder.setMustExist(true);
		if(gitDir == null) {
			repositoryBuilder.findGitDir();
			if(repositoryBuilder.getGitDir() == null)
				throw new IllegalStateException("Could not determine GIT database directory!");
		}
		else
			repositoryBuilder.setGitDir(gitDir);
		_repository = repositoryBuilder.build();
		if(_repository.findRef("HEAD") == null )
			throw new IllegalStateException("Invalid GIT database directory '" + gitDir.getAbsolutePath() + "'!");
	}

	public Repository getRepository() {
		return _repository;
	}
	
	public String getRemoteUrl() {
		return null;
	}
	
	
	

}
