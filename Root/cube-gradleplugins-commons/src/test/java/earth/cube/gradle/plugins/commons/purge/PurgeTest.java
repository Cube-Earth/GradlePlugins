package earth.cube.gradle.plugins.commons.purge;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import earth.cube.gradle.plugins.commons.utils.FileUtils;
import earth.cube.gradle.plugins.commons.utils.fileset.MemoryFileSystem;

public class PurgeTest {
	
	private MemoryFileSystem _mfs = new MemoryFileSystem();
	private List<String> _paths = new ArrayList<>();
	
	
	protected void findAll() throws IOException {
		Files.walkFileTree(_mfs.get().getPath("/"), new SimpleFileVisitor<Path>(){
		    @Override
		    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	    		_paths.add(file.toString());
		        return FileVisitResult.CONTINUE;
		    }
		     
		    @Override
		    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	    		if(!FileUtils.hasChildren(dir))
	    			_paths.add(dir.toString() + '/');
		        return FileVisitResult.CONTINUE;
		    }
		});
	}
	
	protected void check(String... saExpected) throws IOException {
		Purge task = new Purge(_mfs.get().getPath("/projects/project1"));
		task.execute();
		findAll();
		String s = String.join(", ", _paths);
		Assert.assertEquals(s, saExpected.length, _paths.size());
		Assert.assertArrayEquals(s, saExpected, _paths.toArray(new String[_paths.size()]));
	}
	

	@Test
	public void test_1() throws IOException {
		_mfs.addDirectory("/projects/project1/.git");
		_mfs.addDirectory("/projects/project1/a");
		_mfs.addFile("/projects/project1/b/a/a.txt");
		_mfs.addFile("/projects/project1/b/a/b/b.txt");
		_mfs.addDirectory("/projects/project1/b/b");
		_mfs.addDirectory("/projects/project1/b/c");
		_mfs.addDirectory("/projects/project2");
		
		_mfs.addFile("/projects/project1/.purge", "**/*a*/**");
		
		check("/projects/project1/.git/", "/projects/project1/.purge", "/projects/project1/b/b/", "/projects/project1/b/c/", "/projects/project2/", "/work/");
	}

}
