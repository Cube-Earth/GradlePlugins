package earth.cube.gradle.plugins.commons.utils.fileset;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class FileSetTest {
	
	private MemoryFileSystem _mfs = new MemoryFileSystem();
	private FileSetBuilder _fsb = new FileSetBuilder();
	private List<String> _dirs = new ArrayList<>();
	private List<String> _files = new ArrayList<>();
	
	
	protected class BackedByList implements IVisitor {

		@Override
		public void visitDirectory(Path dir, boolean bDirsOnly) {
			_dirs.add(dir.toAbsolutePath().toString());
		}

		@Override
		public void visitFile(Path file) {
			_files.add(file.toAbsolutePath().toString());
		}
		
	}
	
	
	protected void checkDirs(String... saDirs) {
		String[] saActual = _dirs.toArray(new String[_dirs.size()]);
		Assert.assertArrayEquals(String.join(", ", saActual), saDirs, saActual);
	}
	
	protected void checkFiles(String... saFiles) {
		String[] saActual = _files.toArray(new String[_files.size()]);
		Assert.assertArrayEquals(String.join(", ", saActual), saFiles, saActual);
	}
	
	
	
	@Test
	public void test_include_1() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/d.txt");
		
		FileSet fs = _fsb.add("/tmp").build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		checkDirs("/tmp");
		
		Assert.assertEquals(0, _files.size());
	}

	@Test
	public void test_include_2() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/d.txt");
		
		FileSet fs = _fsb.add("/tmp/").build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		checkDirs("/tmp");

		Assert.assertEquals(0, _files.size());
	}

	@Test
	public void test_include_3() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/d.txt");
		
		FileSet fs = _fsb.add("/tmp/*").build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		Assert.assertEquals(String.join(", ", _dirs), 0, _dirs.size());
		
		checkFiles("/tmp/d.txt");
	}
	
	@Test
	public void test_include_4() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/d.txt");
		
		FileSet fs = _fsb.add("/tmp/*/").build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		checkDirs("/tmp/a", "/tmp/b");

		Assert.assertEquals(String.join(", ", _files), 0, _files.size());
	}

	@Test
	public void test_include_5() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/d.txt");
		
		FileSet fs = _fsb.add("/tmp/**").build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		checkDirs("/tmp", "/tmp/a", "/tmp/a/b", "/tmp/b");

		checkFiles("/tmp/a/a.txt", "/tmp/a/b/c.txt", "/tmp/a/b.txt", "/tmp/b/a.txt", "/tmp/b/b.txt", "/tmp/b/c.txt", "/tmp/d.txt");
	}

	@Test
	public void test_include_6() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/d.txt");
		
		FileSet fs = _fsb.add("/tmp/**/").build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		checkDirs("/tmp", "/tmp/a", "/tmp/a/b", "/tmp/b");

		Assert.assertEquals(0, _files.size());
	}

	@Test
	public void test_include_7() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/d.txt");
		
		FileSet fs = _fsb.add("+/tmp/**/").build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		checkDirs("/tmp", "/tmp/a", "/tmp/a/b", "/tmp/b");

		Assert.assertEquals(0, _files.size());
	}

	@Test
	public void test_include_8() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		FileSet fs = _fsb.add("+/tmp/**/c.*").build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		Assert.assertEquals(0, _dirs.size());

		checkFiles("/tmp/a/b/c.txt", "/tmp/b/c.txt", "/tmp/c.txt");
	}
	
	@Test
	public void test_include_9() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/aa/c.txt");
		_mfs.addFile("/tmp/a/aa.txt");
		_mfs.addFile("/tmp/a/ab.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/ba/d.txt");
		_mfs.addFile("/tmp/a/ba.txt");
		_mfs.addFile("/tmp/a/bb.txt");
		_mfs.addFile("/tmp/b/a/b.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/aa/b.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		
		_fsb.add("+/tmp/**/a/**");
		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		checkDirs("/tmp/a", "/tmp/a/aa", "/tmp/a/b", "/tmp/a/ba", "/tmp/b/a");
		
		checkFiles("/tmp/a/a.txt", "/tmp/a/aa/c.txt", "/tmp/a/aa.txt", "/tmp/a/ab.txt", "/tmp/a/b/c.txt", "/tmp/a/b.txt",
				"/tmp/a/ba/d.txt", "/tmp/a/ba.txt", "/tmp/a/bb.txt", "/tmp/b/a/b.txt");
	}

	

	@Test
	public void test_exclude_1() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		_fsb.add("+/tmp/**/c.*");
		_fsb.add(" -c.txt");
		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		Assert.assertEquals(0, _dirs.size());

		checkFiles("/tmp/a/b/c.txt", "/tmp/b/c.txt");
	}


	@Test
	public void test_exclude_2() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		_fsb.add("+/tmp/**/c.*");
		_fsb.add(" -*/c.txt");
		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		Assert.assertEquals(0, _dirs.size());

		checkFiles("/tmp/a/b/c.txt", "/tmp/c.txt");
	}

	@Test
	public void test_exclude_3() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		_fsb.add("+/tmp/**/c.*");
		_fsb.add(" -**/c.txt");
		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		Assert.assertEquals(0, _dirs.size());

		Assert.assertEquals(0, _files.size());
	}

	@Test
	public void test_exclude_4() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		_fsb.add("+/tmp/**/c.*");
		_fsb.add(" -**/c.txt");
		_fsb.add("   -b/**");
		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		Assert.assertEquals(0, _dirs.size());

		checkFiles("/tmp/b/c.txt");
	}

	@Test
	public void test_exclude_5() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		_fsb.add("+/tmp/**/c.*");
		_fsb.add(" -**/c.txt");
		_fsb.add("   -**/b/**");
		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		Assert.assertEquals(0, _dirs.size());

		checkFiles("/tmp/a/b/c.txt", "/tmp/b/c.txt");
	}
	
	@Test
	public void test_exclude_6() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		_fsb.add("+/tmp/**/c.*");
		_fsb.add(" -**/c.txt");
		_fsb.add("   -**/b/**");
		_fsb.add("   -*");
		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());
		
		Assert.assertEquals(0, _dirs.size());

		checkFiles("/tmp/a/b/c.txt", "/tmp/b/c.txt", "/tmp/c.txt");

	}

	@Test
	public void test_exclude_7() throws IOException {
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/aa/c.txt");
		_mfs.addFile("/tmp/a/aa.txt");
		_mfs.addFile("/tmp/a/ab.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/ba/d.txt");
		_mfs.addFile("/tmp/a/ba.txt");
		_mfs.addFile("/tmp/a/bb.txt");
		_mfs.addFile("/tmp/b/a/b.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/aa/b.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		
		_fsb.add("+/tmp/**/a/**");
		_fsb.add(" -**/*a*/**");

		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());

		
		checkDirs();

		checkFiles();
	}
	
	

	@Test
	public void test_exclude_8() throws IOException {
		_mfs.addFile("/tmp/a/a/c.txt");
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/aa/c.txt");
		_mfs.addFile("/tmp/a/aa.txt");
		_mfs.addFile("/tmp/a/ab.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/ba.txt");
		_mfs.addFile("/tmp/a/bb.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/a/ba/d.txt");
		_mfs.addFile("/tmp/b/a/b.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/aa/b.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		_mfs.addFile("/tmp/a/c.txt");
		
		
		_fsb.add("+/tmp/**/a/**");
		_fsb.add(" -**/*a*/**");
		_fsb.add("   -**/b*/**");
		_fsb.add("   -a/c*");
		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());

		checkDirs("/tmp/a/b", "/tmp/a/ba", "/tmp/b/a");

		checkFiles("/tmp/a/b/c.txt", "/tmp/a/ba/d.txt", "/tmp/a/c.txt", "/tmp/b/a/b.txt");
		

	}

	@Test
	public void test_exists_1() throws IOException {
		_mfs.addFile("/tmp/a/a/c.txt");
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/aa/b.txt");
		_mfs.addFile("/tmp/a/aa/c.txt");
		_mfs.addFile("/tmp/a/aa.txt");
		_mfs.addFile("/tmp/a/ab.txt");
		_mfs.addFile("/tmp/a/b/a.txt");
		_mfs.addFile("/tmp/a/b/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/ba/d.txt");
		_mfs.addFile("/tmp/a/ba.txt");
		_mfs.addFile("/tmp/a/bb.txt");
		_mfs.addFile("/tmp/b/a/b.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/aa/b.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		
		_fsb.add("+/tmp/**/a/**");
		_fsb.add(" !c.txt");

		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());

		
		checkDirs("/tmp/a/a", "/tmp/a/aa", "/tmp/a/b");

		checkFiles("/tmp/a/a/c.txt", "/tmp/a/aa/b.txt", "/tmp/a/aa/c.txt", "/tmp/a/b/a.txt", "/tmp/a/b/b.txt", "/tmp/a/b/c.txt");
	}

	@Test
	public void test_exists_2() throws IOException {
		_mfs.addFile("/tmp/a/a/c.txt");
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/aa/b.txt");
		_mfs.addFile("/tmp/a/aa/c.txt");
		_mfs.addFile("/tmp/a/aa.txt");
		_mfs.addFile("/tmp/a/ab.txt");
		_mfs.addFile("/tmp/a/b/a.txt");
		_mfs.addFile("/tmp/a/b/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/ba/d.txt");
		_mfs.addFile("/tmp/a/ba.txt");
		_mfs.addFile("/tmp/a/bb.txt");
		_mfs.addFile("/tmp/b/a/b.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/aa/b.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		
		_fsb.add("+/tmp/**/a/**");
		_fsb.add(" !c.txt");
		_fsb.add(" !a.txt");

		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());

		
		checkDirs("/tmp/a/b");

		checkFiles("/tmp/a/b/a.txt", "/tmp/a/b/b.txt", "/tmp/a/b/c.txt");
	}

	@Test
	public void test_exists_3() throws IOException {
		_mfs.addFile("/tmp/a/a/c.txt");
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/aa/b.txt");
		_mfs.addFile("/tmp/a/aa/c.txt");
		_mfs.addFile("/tmp/a/aa.txt");
		_mfs.addFile("/tmp/a/ab.txt");
		_mfs.addFile("/tmp/a/b/a.txt");
		_mfs.addFile("/tmp/a/b/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/ba/d.txt");
		_mfs.addFile("/tmp/a/ba.txt");
		_mfs.addFile("/tmp/a/bb.txt");
		_mfs.addFile("/tmp/b/a/b.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/aa/b.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		
		_fsb.add("+/tmp/**/a/**");
		_fsb.add(" !aa/c.txt");

		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());

		
		checkDirs("/tmp/a");

		checkFiles("/tmp/a/a.txt", "/tmp/a/aa.txt", "/tmp/a/ab.txt", "/tmp/a/b.txt", "/tmp/a/ba.txt", "/tmp/a/bb.txt");
	}

	@Test
	public void test_exists_4() throws IOException {
		_mfs.addFile("/tmp/a/a/c.txt");
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/aa/b.txt");
		_mfs.addFile("/tmp/a/aa/c.txt");
		_mfs.addFile("/tmp/a/aa.txt");
		_mfs.addFile("/tmp/a/ab.txt");
		_mfs.addFile("/tmp/a/b/a.txt");
		_mfs.addFile("/tmp/a/b/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/ba/d.txt");
		_mfs.addFile("/tmp/a/ba.txt");
		_mfs.addFile("/tmp/a/bb.txt");
		_mfs.addFile("/tmp/b/a/b.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/aa/b.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		
		_fsb.add("+/tmp/**/a/**/");
		_fsb.add(" !aa/c.txt");

		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());

		
		checkDirs("/tmp/a");

		checkFiles();
	}
	

	
	@Test
	public void test_exists_5() throws IOException {
		_mfs.addFile("/tmp/a/a/c.txt");
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/aa/b.txt");
		_mfs.addFile("/tmp/a/aa/c.txt");
		_mfs.addFile("/tmp/a/aa.txt");
		_mfs.addFile("/tmp/a/ab.txt");
		_mfs.addFile("/tmp/a/b/a.txt");
		_mfs.addFile("/tmp/a/b/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/ba/d.txt");
		_mfs.addFile("/tmp/a/ba.txt");
		_mfs.addFile("/tmp/a/bb.txt");
		_mfs.addFile("/tmp/b/a/b.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/aa/b.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		
		_fsb.add("+/tmp/**/a/**");
		_fsb.add(" !c.txt");
		_fsb.add(" -**/c*");

		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());

		
		checkDirs("/tmp/a/a", "/tmp/a/aa", "/tmp/a/b");

		checkFiles("/tmp/a/aa/b.txt", "/tmp/a/b/a.txt", "/tmp/a/b/b.txt");
	}	
	
	@Test
	public void test_notExists_1() throws IOException {
		_mfs.addFile("/tmp/a/a/c.txt");
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/aa/b.txt");
		_mfs.addFile("/tmp/a/aa/c.txt");
		_mfs.addFile("/tmp/a/aa.txt");
		_mfs.addFile("/tmp/a/ab.txt");
		_mfs.addFile("/tmp/a/b/a.txt");
		_mfs.addFile("/tmp/a/b/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/ba/d.txt");
		_mfs.addFile("/tmp/a/ba.txt");
		_mfs.addFile("/tmp/a/bb.txt");
		_mfs.addFile("/tmp/b/a/b.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/aa/b.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		
		_fsb.add("+/tmp/**/a/**/");
		_fsb.add(" !!c.txt");

		FileSet fs = _fsb.build();
		fs.process(_mfs.get().getPath("."), new BackedByList());

		
		checkDirs("/tmp/a", "/tmp/a/ba", "/tmp/b/a");

		checkFiles();
	}

	
	@Test
	public void test_func_shiftName_1() {
		FileSet fs = new FileSet();
		String[] saPath = new String[] { "/abc/def/ghi", "" };
		
		Assert.assertTrue(fs.shiftName(saPath));
		Assert.assertArrayEquals(new String[] { "/abc/def/", "ghi" }, saPath);
		
		Assert.assertTrue(fs.shiftName(saPath));
		Assert.assertArrayEquals(new String[] { "/abc/", "def/ghi" }, saPath);

		Assert.assertFalse(fs.shiftName(saPath));
	}
	
	@Test
	public void test_func_shiftName_2() {
		FileSet fs = new FileSet();
		String[] saPath = new String[] { "/abc/def/ghi/", "" };
		
		Assert.assertTrue(fs.shiftName(saPath));
		Assert.assertArrayEquals(new String[] { "/abc/def/", "ghi/" }, saPath);
		
		Assert.assertTrue(fs.shiftName(saPath));
		Assert.assertArrayEquals(new String[] { "/abc/", "def/ghi/" }, saPath);

		Assert.assertFalse(fs.shiftName(saPath));
	}

	@Test
	public void test_func_isMatched_1() throws IOException {
		_mfs.addFile("/tmp/a/a/c.txt");
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/aa/b.txt");
		_mfs.addFile("/tmp/a/aa/c.txt");
		_mfs.addFile("/tmp/a/aa.txt");
		_mfs.addFile("/tmp/a/ab.txt");
		_mfs.addFile("/tmp/a/b/a.txt");
		_mfs.addFile("/tmp/a/b/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/a/b/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/ba/d.txt");
		_mfs.addFile("/tmp/a/ba.txt");
		_mfs.addFile("/tmp/a/bb.txt");
		_mfs.addFile("/tmp/b/a/b.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/aa/b.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		
		_fsb.add("+/tmp/**/a/**");
		_fsb.add(" !c.txt");
		_fsb.add(" -**/c*");

		FileSet fs = _fsb.build();
		
		Assert.assertTrue(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/a")));
		Assert.assertTrue(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/aa")));
		Assert.assertFalse(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/ab")));
		Assert.assertTrue(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/b")));
		Assert.assertFalse(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/b/a")));
		Assert.assertFalse(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/ba")));
		
		Assert.assertTrue(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/aa/b.txt")));
		Assert.assertTrue(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/b/a.txt")));
		Assert.assertTrue(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/b/b.txt")));
		Assert.assertFalse(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/b/a/a.txt")));
		
		Assert.assertTrue(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/a/a.txt")));
		Assert.assertFalse(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/a/c.txt")));
		
		Assert.assertTrue(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/aa/a.txt")));
		Assert.assertTrue(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/aa/e.txt")));
		Assert.assertFalse(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/ea/a.txt")));
		
		Assert.assertFalse(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/b/aa/")));

		Assert.assertFalse(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/k/")));

		Assert.assertFalse(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/")));

		Assert.assertFalse(fs.isMatched(_mfs.get().getPath("."), _mfs.get().getPath("/k/")));
	}	

	@Test
	public void test_func_isTransitiveMatched_1() throws IOException {
		_mfs.addFile("/tmp/a/a/c.txt");
		_mfs.addFile("/tmp/a/a.txt");
		_mfs.addFile("/tmp/a/aa/b.txt");
		_mfs.addFile("/tmp/a/aa/c.txt");
		_mfs.addFile("/tmp/a/aa.txt");
		_mfs.addFile("/tmp/a/ab.txt");
		_mfs.addFile("/tmp/a/b/a.txt");
		_mfs.addFile("/tmp/a/b/b.txt");
		_mfs.addFile("/tmp/a/b/c.txt");
		_mfs.addFile("/tmp/a/b/a/a.txt");
		_mfs.addFile("/tmp/a/b.txt");
		_mfs.addFile("/tmp/a/ba/d.txt");
		_mfs.addFile("/tmp/a/ba.txt");
		_mfs.addFile("/tmp/a/bb.txt");
		_mfs.addFile("/tmp/b/a/b.txt");
		_mfs.addFile("/tmp/b/a.txt");
		_mfs.addFile("/tmp/b/aa/b.txt");
		_mfs.addFile("/tmp/b/b.txt");
		_mfs.addFile("/tmp/b/c.txt");
		_mfs.addFile("/tmp/c.txt");
		
		
		_fsb.add("+/tmp/**/a/**");
		_fsb.add(" !c.txt");
		_fsb.add(" -**/c*");

		FileSet fs = _fsb.build();
		
		Assert.assertTrue(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/a")));
		Assert.assertTrue(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/aa")));
		Assert.assertFalse(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/ab")));
		Assert.assertTrue(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/b")));
		Assert.assertTrue(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/b/a")));
		Assert.assertFalse(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/ba")));
		
		Assert.assertTrue(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/aa/b.txt")));
		Assert.assertTrue(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/b/a.txt")));
		Assert.assertTrue(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/b/b.txt")));
		Assert.assertTrue(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/b/a/a.txt")));
		
		Assert.assertTrue(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/a/a.txt")));
		Assert.assertTrue(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/a/c.txt")));
		
		Assert.assertTrue(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/aa/a.txt")));
		Assert.assertTrue(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/aa/e.txt")));
		Assert.assertFalse(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/a/ea/a.txt")));
		
		Assert.assertFalse(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/b/aa/")));

		Assert.assertFalse(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/k/")));

		Assert.assertFalse(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/tmp/")));

		Assert.assertFalse(fs.isTransitiveMatched(_mfs.get().getPath("."), _mfs.get().getPath("/k/")));
	}	

}