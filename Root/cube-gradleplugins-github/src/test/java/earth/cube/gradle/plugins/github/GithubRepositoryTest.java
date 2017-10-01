package earth.cube.gradle.plugins.github;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import earth.cube.gradle.plugins.github.model.GithubRepository;


public class GithubRepositoryTest {
	
	private static GithubRepository _repo;

	@BeforeClass
	public static void setUp() throws IOException {
		_repo = new GithubRepository(null);
	}

	@Test
	public void test_1() throws IOException {
		Assert.assertEquals("Cube-Earth", _repo.getOwnerName());
		Assert.assertEquals("GradlePlugins", _repo.getRepositoryName());
		Assert.assertEquals("https://necromancerr@github.com/Cube-Earth/GradlePlugins.git", _repo.getRemoteUrl().toString());
		Assert.assertEquals("necromancerr@users.noreply.github.com", _repo.getUserEmail());
	}
	
}
