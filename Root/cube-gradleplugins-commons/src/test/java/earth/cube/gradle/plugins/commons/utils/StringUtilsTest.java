package earth.cube.gradle.plugins.commons.utils;

import org.junit.Assert;
import org.junit.Test;


public class StringUtilsTest {
	
	@Test
	public void test_splitStrict_1() {
		Assert.assertArrayEquals(new String[] { "", "", "a", "bc", "", "", "d", "", "", "" }, StringUtils.splitStrict("//a/bc///d///", "/"));
	}

}
