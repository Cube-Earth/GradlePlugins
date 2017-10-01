package earth.cube.gradle.plugins.github.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Encoder {
	
	public static String encodeJsonString(String s) {
		return s.replaceAll("\"", "\\\"").replaceAll("\r", "\\r").replaceAll("\n", "\\n").replaceAll("\t", "\\t");
	}
	
	public static String encodeUrlString(String s) {
		try {
			return URLEncoder.encode(s, "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
