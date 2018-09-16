package earth.cube.gradle.plugins.commons.emptydirs;

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

import earth.cube.gradle.plugins.commons.utils.fileset.MemoryFileSystem;

public class KeepEmptyDirsTest {
	
	private MemoryFileSystem _mfs = new MemoryFileSystem();
	private List<String> _dirs = new ArrayList<>();
	
	
	protected void findMarkers() throws IOException {
		Files.walkFileTree(_mfs.get().getPath("/projects/project1"), new SimpleFileVisitor<Path>(){
		     @Override
		     public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		          if(!attrs.isDirectory() && file.getFileName().toString().equals(".keep")){
		               _dirs.add(file.getParent().toString());
		          }
		          return FileVisitResult.CONTINUE;
		      }
		     });
	}
	
	protected void checkMarkers(String... saExpected) throws IOException {
		KeepEmptyDirs task = new KeepEmptyDirs(_mfs.get().getPath("/projects/project1"));
		int n = task.mark();
		findMarkers();
		String s = String.join(", ", _dirs);
		Assert.assertEquals(s, saExpected.length, n);
		Assert.assertEquals(s, saExpected.length, _dirs.size());
		Assert.assertArrayEquals(s, saExpected, _dirs.toArray(new String[_dirs.size()]));
	}
	
	@Test
	public void test_simple_1() throws IOException {
		_mfs.addDirectory("/projects/project1/.git");
		_mfs.addDirectory("/projects/project1/a");
		_mfs.addFile("/projects/project1/b/a/a.txt");
		_mfs.addDirectory("/projects/project1/b/b");
		_mfs.addDirectory("/projects/project1/b/c");
		_mfs.addDirectory("/projects/project2");
		
		checkMarkers();
	}

	@Test
	public void test_simple_2() throws IOException {
		_mfs.addDirectory("/projects/project1/.git");
		_mfs.addDirectory("/projects/project1/a");
		_mfs.addFile("/projects/project1/b/a/a.txt");
		_mfs.addDirectory("/projects/project1/b/b");
		_mfs.addDirectory("/projects/project1/b/c");
		_mfs.addDirectory("/projects/project2");
		
		_mfs.addFile("/projects/project1/.keepdirs", "**/");
		
		checkMarkers("/projects/project1/a", "/projects/project1/b/b", "/projects/project1/b/c");
	}

	@Test
	public void test_ignore_1() throws IOException {
		_mfs.addDirectory("/projects/project1/.git");
		_mfs.addDirectory("/projects/project1/a");
		_mfs.addFile("/projects/project1/b/a/a.txt");
		_mfs.addDirectory("/projects/project1/b/b");
		_mfs.addDirectory("/projects/project1/b/c");
		_mfs.addDirectory("/projects/project2");
		
		_mfs.addFile("/projects/project1/.keepdirs", "**/");
		_mfs.addFile("/projects/project1/.gitignore", "**/*c/");
		
		checkMarkers("/projects/project1/a", "/projects/project1/b/b");
	}

	@Test
	public void test_ignore_purge_1() throws IOException {
		_mfs.addDirectory("/projects/project1/.git");
		_mfs.addDirectory("/projects/project1/a");
		_mfs.addFile("/projects/project1/b/a/a.txt");
		_mfs.addDirectory("/projects/project1/b/b");
		_mfs.addDirectory("/projects/project1/b/c");
		_mfs.addDirectory("/projects/project2");
		
		_mfs.addFile("/projects/project1/.keepdirs", "**/");
		_mfs.addFile("/projects/project1/.gitignore", "**/*c/");
		_mfs.addFile("/projects/project1/.purge", "**/*a*");
		
		checkMarkers("/projects/project1/b/b");
	}

}
