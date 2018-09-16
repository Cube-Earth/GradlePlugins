package earth.cube.gradle.plugins.commons.utils.fileset.paths;

import org.junit.Assert;
import org.junit.Test;

public class PathStructureTest {
	
	@Test
	public void test_isEmpty_1() {
		Assert.assertTrue(new PathStructure("").isEmpty());
		Assert.assertFalse(new PathStructure("/").isEmpty());
		Assert.assertFalse(new PathStructure("/abc").isEmpty());
		Assert.assertFalse(new PathStructure("/abc/").isEmpty());
	}

	@Test
	public void test_size_1() {
		Assert.assertEquals(0, new PathStructure("").size());
		Assert.assertEquals(2, new PathStructure("/").size());
		Assert.assertEquals(2, new PathStructure("/abc").size());
		Assert.assertEquals(2, new PathStructure("/abc/").size());
		Assert.assertEquals(3, new PathStructure("/abc/def").size());
		Assert.assertEquals(3, new PathStructure("/abc/def/").size());
	}

	@Test
	public void test_isDirectory_1() {
		Assert.assertFalse(new PathStructure("").isDirectory());
		Assert.assertTrue(new PathStructure("/").isDirectory());
		Assert.assertFalse(new PathStructure("/abc").isDirectory());
		Assert.assertTrue(new PathStructure("/abc/").isDirectory());
	}

	@Test
	public void test_isLastDirectory_1() {
		Assert.assertFalse(new PathStructure("").isLastDirectory(0));
		
		Assert.assertFalse(new PathStructure("/").isLastDirectory(0));
		Assert.assertTrue(new PathStructure("/").isLastDirectory(1));
		Assert.assertFalse(new PathStructure("/").isLastDirectory(2));
		
		Assert.assertTrue(new PathStructure("/abc").isLastDirectory(0));
		Assert.assertFalse(new PathStructure("/abc").isLastDirectory(1));
		Assert.assertFalse(new PathStructure("/abc").isLastDirectory(2));

		Assert.assertFalse(new PathStructure("/abc/").isLastDirectory(0));
		Assert.assertTrue(new PathStructure("/abc/").isLastDirectory(1));
		Assert.assertFalse(new PathStructure("/abc/").isLastDirectory(2));
		Assert.assertFalse(new PathStructure("/abc/").isLastDirectory(3));
	}

	@Test
	public void test_getFileName_1() {
		Assert.assertEquals(null, new PathStructure("").getFileName());
		Assert.assertEquals(null, new PathStructure("/").getFileName());
		Assert.assertEquals("abc", new PathStructure("/abc").getFileName());
		Assert.assertEquals(null, new PathStructure("/abc/").getFileName());
	}

	@Test
	public void test_substring_1() {
		PathStructure structure = new PathStructure("");
		try {
			structure.substring(0, 0);
			Assert.fail();
		}
		catch(IndexOutOfBoundsException e) {
		}
	}

	@Test
	public void test_substring_2() {
		PathStructure structure = new PathStructure("/");
		Assert.assertEquals("", structure.substring(0, 0));
		Assert.assertEquals("/", structure.substring(0, 1));
		try {
			structure.substring(0, 2);
			Assert.fail();
		}
		catch(IndexOutOfBoundsException e) {
		}
	}

	@Test
	public void test_substring_3() {
		PathStructure structure = new PathStructure("/abc");
		Assert.assertEquals("", structure.substring(0, 0));
		Assert.assertEquals("/abc", structure.substring(0, 1));
		try {
			structure.substring(0, 2);
			Assert.fail();
		}
		catch(IndexOutOfBoundsException e) {
		}
	}

	@Test
	public void test_substring_4() {
		PathStructure structure = new PathStructure("/abc/");
		Assert.assertEquals("", structure.substring(0, 0));
		Assert.assertEquals("/abc/", structure.substring(0, 1));
		try {
			structure.substring(0, 2);
			Assert.fail();
		}
		catch(IndexOutOfBoundsException e) {
		}
	}

	@Test
	public void test_substring_5() {
		PathStructure structure = new PathStructure("/abc/def");
		Assert.assertEquals("", structure.substring(0, 0));
		Assert.assertEquals("/abc/", structure.substring(0, 1));
		Assert.assertEquals("/abc/def", structure.substring(0, 2));
		Assert.assertEquals("def", structure.substring(1, 2));
		try {
			structure.substring(0, 3);
			Assert.fail();
		}
		catch(IndexOutOfBoundsException e) {
		}
	}

	@Test
	public void test_substring_6() {
		PathStructure structure = new PathStructure("/abc/def/");
		Assert.assertEquals("", structure.substring(0, 0));
		Assert.assertEquals("/abc/", structure.substring(0, 1));
		Assert.assertEquals("/abc/def/", structure.substring(0, 2));
		Assert.assertEquals("def/", structure.substring(1, 2));
		try {
			structure.substring(0, 3);
			Assert.fail();
		}
		catch(IndexOutOfBoundsException e) {
		}
	}

	@Test
	public void test_toString_1() {
		PathStructure structure = new PathStructure("/abc/def/");
		Assert.assertEquals("/abc/def/|0,5,9|3", structure.toString());
	}
}
