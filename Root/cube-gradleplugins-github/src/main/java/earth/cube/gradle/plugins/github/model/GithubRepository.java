package earth.cube.gradle.plugins.github.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import earth.cube.gradle.plugins.github.config.GithubExtension;

public class GithubRepository {
	
	private Repository _repository;
	private URL _remoteUrl;
	private String _sHost;
	private String _sRepositoryName;
	private String _sOwnerName;
	private String _sUserEmail;
	private char[] _userPwd;

	public GithubRepository(File gitDir) throws IOException {
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

		_remoteUrl = new URL(_repository.getConfig().getString("remote", "origin", "url"));
		if(!_remoteUrl.getHost().equalsIgnoreCase("github.com"))
			throw new IllegalStateException("github repository expected!");
		if(!_remoteUrl.getProtocol().equalsIgnoreCase("https"))
			throw new IllegalStateException("HTTPS protocol expected!");
		
		_sHost = _remoteUrl.getHost();
		
		String[] saPath = _remoteUrl.getPath().split("/");
		_sOwnerName = saPath[1];
		_sRepositoryName = saPath[2].replaceFirst("\\.git$", "");
		
		_sUserEmail = _repository.getConfig().getString("user", null, "email");
	}
	
	public void setConfig(GithubExtension config) {
		_userPwd = config.password.toCharArray();
	}
	

	public Repository getRepository() {
		return _repository;
	}
	
	public URL getRemoteUrl() {
		return _remoteUrl;
	}
	
	public String getOwnerName() {
		return _sOwnerName;
	}
	
	public String getRepositoryName() {
		return _sRepositoryName;
	}
	
	public String getHost() {
		return _sHost;
	}
	
	public String getUserEmail() {
		return _sUserEmail;
	}

	public char[] getUserPwd() {
		return _userPwd;
	}
	
}
