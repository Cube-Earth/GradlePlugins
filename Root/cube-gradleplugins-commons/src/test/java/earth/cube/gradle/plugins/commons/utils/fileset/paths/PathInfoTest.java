package earth.cube.gradle.plugins.commons.utils.fileset.paths;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

public class PathInfoTest {

	@Test
	public void test_init_1() {
		Assert.assertEquals("/abc/def/ghi|0,5,9,12|4|1|2", new PathInfo(new PathStructure("/abc/def/ghi"), 1, 2).toString());
	}

	@Test
	public void test_init_2() {
		Assert.assertEquals("/abc/def/ghi|0,5,9,12|4|0|0", new PathInfo("/abc/def/ghi").toString());
	}

	@Test
	public void test_get_1() {
		Assert.assertEquals("", new PathInfo(new PathStructure("/abc/def/ghi"), 0, 0).get());
		Assert.assertEquals("/abc/", new PathInfo(new PathStructure("/abc/def/ghi"), 0, 1).get());
		Assert.assertEquals("def/", new PathInfo(new PathStructure("/abc/def/ghi"), 1, 2).get());
		Assert.assertEquals("def/ghi", new PathInfo(new PathStructure("/abc/def/ghi"), 1, 3).get());
	}
	
	@Test
	public void test_reset_1() {
		Assert.assertEquals("/abc/def/ghi|0,5,9,12|4|0|0", new PathInfo(new PathStructure("/abc/def/ghi"), 1, 2).reset().toString());
	}

	@Test
	public void test_commit_1() {
		Assert.assertEquals("/abc/def/ghi|0,5,9,12|4|2|2", new PathInfo(new PathStructure("/abc/def/ghi"), 1, 2).commit().toString());
	}

	@Test
	public void test_isDirectory_1() {
		Assert.assertFalse(new PathInfo(new PathStructure("/abc/def/ghi"), 0, 0).isDirectory());
		Assert.assertTrue(new PathInfo(new PathStructure("/abc/def/ghi"), 0, 1).isDirectory());
		Assert.assertTrue(new PathInfo(new PathStructure("/abc/def/ghi"), 0, 2).isDirectory());
		Assert.assertFalse(new PathInfo(new PathStructure("/abc/def/ghi"), 0, 3).isDirectory());
		Assert.assertTrue(new PathInfo(new PathStructure("/abc/def/ghi/"), 0, 3).isDirectory());
	}


	@Test
	public void test_isLastDirectory_1() {
		Assert.assertFalse(new PathInfo(new PathStructure("/abc/def/ghi"), 0, 0).isLastDirectory());
		Assert.assertFalse(new PathInfo(new PathStructure("/abc/def/ghi"), 0, 1).isLastDirectory());
		Assert.assertTrue(new PathInfo(new PathStructure("/abc/def/ghi"), 0, 2).isLastDirectory());
		Assert.assertFalse(new PathInfo(new PathStructure("/abc/def/ghi"), 0, 3).isLastDirectory());
		Assert.assertTrue(new PathInfo(new PathStructure("/abc/def/ghi/"), 0, 3).isLastDirectory());
	}

	@Test
	public void test_getFileName_1() {
		Assert.assertEquals("ghi", new PathInfo(new PathStructure("/abc/def/ghi"), 0, 0).getFileName());
		Assert.assertNull(new PathInfo(new PathStructure("/abc/def/ghi/"), 0, 3).getFileName());
	}

	@Test
	public void test_crop_1() {
		Assert.assertEquals("/abc/def/|0,5,9|3|0|0", new PathInfo(new PathStructure("/abc/def/ghi"), 1, 2).crop().toString());
	}

	@Test
	public void test_shift_1() {
		Assert.assertEquals("/abc/def/ghi|0,5,9,12|4|1|3", new PathInfo(new PathStructure("/abc/def/ghi"), 1, 2).shift().toString());
		Assert.assertNull(new PathInfo(new PathStructure("/abc/def/ghi"), 1, 3).shift());
	}

	@Test
	public void test_getAllShifts_1() {
		Iterator<PathInfo> it = new PathInfo(new PathStructure("/abc/def/ghi"), 1, 2).getAllShifts().iterator();

		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("/abc/def/ghi|0,5,9,12|4|1|2", it.next().toString());
		
		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void test_getAllShifts_2() {
		Iterator<PathInfo> it = new PathInfo(new PathStructure("/abc/def/ghi/"), 1, 2).getAllShifts().iterator();

		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("/abc/def/ghi/|0,5,9,13|4|1|2", it.next().toString());
		
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("/abc/def/ghi/|0,5,9,13|4|1|3", it.next().toString());

		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void test_getAllShifts_3() {
		Iterator<PathInfo> it = new PathInfo(new PathStructure("/abc/def/ghi/"), 0, 0).getAllShifts().iterator();

		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("/abc/def/ghi/|0,5,9,13|4|0|1", it.next().toString());
		
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("/abc/def/ghi/|0,5,9,13|4|0|2", it.next().toString());

		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("/abc/def/ghi/|0,5,9,13|4|0|3", it.next().toString());

		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void test_getAllShifts_4() {
		Iterator<PathInfo> it = new PathInfo(new PathStructure(""), 0, 0).getAllShifts().iterator();

		Assert.assertFalse(it.hasNext());
	}

}
