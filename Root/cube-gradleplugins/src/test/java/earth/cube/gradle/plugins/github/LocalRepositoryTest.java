package earth.cube.gradle.plugins.github;

import java.io.IOException;

import org.junit.Test;

public class LocalRepositoryTest {

	@Test
	public void test_1() throws IOException {
		LocalRepository repo = new LocalRepository(null);
		System.out.println("#" + repo.getRepository().getConfig().getString("remote", "origin", "url"));
	}
	
}
