package earth.cube.gradle.plugins.github;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import junit.framework.Assert;

public class ParserTest {
	
	@Test
	public void test_1() throws UnsupportedEncodingException, IOException, ParseException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("ParserTest.txt");
		JSONParser parser = new JSONParser();
		@SuppressWarnings("unchecked")
		Map<String,Object> result = (Map<String, Object>) parser.parse(new InputStreamReader(in, "utf-8"));
		
		Assert.assertEquals("https://uploads.github.com/repos/Cube-Earth/tools-coreos-ct/releases/6678654/assets{?name,label}", result.get("upload_url"));

		List<Map<String,Object>> assets = (List<Map<String,Object>>) result.get("assets");
		Assert.assertEquals(1, assets.size());
		
		Map<String,Object> asset = assets.get(0);
		System.out.println(result.get("id").getClass().getCanonicalName());
		Assert.assertEquals(4552389L, asset.get("id"));
		Assert.assertEquals("test.ct.gz", asset.get("name"));
		Assert.assertEquals("ct.gz (for Alpine)", asset.get("label"));
	}

}
