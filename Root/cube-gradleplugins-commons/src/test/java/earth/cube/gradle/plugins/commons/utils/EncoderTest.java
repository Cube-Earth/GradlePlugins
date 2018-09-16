package earth.cube.gradle.plugins.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import earth.cube.gradle.plugins.commons.utils.Encoder;

public class EncoderTest {
	
	
	@Test
	public void test_encodeRegEx_1() {
		Assert.assertEquals("/\\*\\.", Encoder.encodeRegEx("/*."));
	}
	
	@Test
	public void test_globToRegEx_1() {
		Assert.assertEquals("/" + Encoder.GLOB_STAR_MAND + "/", Encoder.globToRegEx("/*/"));
		Assert.assertEquals("/" + Encoder.GLOB_STAR_OPT + "a/", Encoder.globToRegEx("/*a/"));
		Assert.assertEquals("/" + Encoder.GLOB_STAR_DOUBLE, Encoder.globToRegEx("/**/"));
		Assert.assertEquals("/" + Encoder.GLOB_STAR_DOUBLE_END, Encoder.globToRegEx("/**"));
		Assert.assertEquals("/" + Encoder.GLOB_STAR_OPT + Encoder.GLOB_STAR_OPT + "a", Encoder.globToRegEx("/**a"));
	}
	
}
