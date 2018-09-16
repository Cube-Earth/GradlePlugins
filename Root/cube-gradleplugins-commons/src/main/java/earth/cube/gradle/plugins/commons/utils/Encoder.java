package earth.cube.gradle.plugins.commons.utils;

import java.util.regex.Pattern;

public class Encoder {
	
	public static final String SPECIAL_CHARS_REGEX = "*()?+{}.\\[]|$^";
	
	protected static final String GLOB_STAR_MAND = "[^/]+";
	protected static final String GLOB_STAR_OPT = "[^/]*";
	protected static final String GLOB_STAR_DOUBLE = "(.*/|)";
	protected static final String GLOB_STAR_DOUBLE_END = "(.*)";
	
	public static String encodeRegEx(String s) {
		return s.replaceAll("([*()?+{}.\\\\\\[\\]|$^])", "\\\\$1");
	}

	public static String globToRegEx(String sGlob) {
		StringBuilder sb = new StringBuilder();
		boolean bNoDelim = true;
		String[] saNames = StringUtils.splitStrict(sGlob, "/");
		int i = 0;
		for(String s : saNames) {
			if(bNoDelim)
				bNoDelim = false;
			else
				sb.append('/');
			if(s.length() == 0)
				sb.append(s);
			else
				if(s.equals("*"))
					sb.append(GLOB_STAR_MAND);
				else
					if(s.equals("**")) {
						sb.append(i < saNames.length - 1 ? GLOB_STAR_DOUBLE : GLOB_STAR_DOUBLE_END);
						bNoDelim = true;
					}
					else {
						s = s.replaceAll("([()?+{}.\\\\\\[\\]|$^])", "\\\\$1");
						s = s.replaceAll("\\*", GLOB_STAR_OPT);
						sb.append(s);
					}
			i++;
		}
		return sb.toString();
		
		
/*		
		StringBuilder sb = new StringBuilder();
		int cLast = -1;
		
		for(CharacterIterator it = new StringCharacterIterator(sGlob); it.current() != CharacterIterator.DONE; ) {
			char c = it.current();
			if(c == '*') {
				if(it.next() == '*') {
					
				}
			}
			else
				if(SPECIAL_CHARS_REGEX.indexOf(c) != -1) {
					sb.append('\\').append(c);
					cLast = c;
					it.next();
				}
		}
		
		return sb.toString();
		
*/		

//		String s = Encoder.encodeRegEx(sGlob);
////		s = s.replaceAll("/\\\\\\*\\\\\\*/", "(/.*/|)");
//		s = s.replaceAll("\\\\\\*\\\\\\*/", "(.*/|)");
////		s = s.replaceAll("/\\\\\\*\\\\\\*", "(/.*|)");
//		s = s.replaceAll("\\\\\\*\\\\\\*", ".*");
//		s = s.replaceAll("\\\\\\*", "[^/]+");
//		s = s.replaceAll("\\\\\\*", "[^/]*");
//		return s;

	}
	
	public static Pattern globToRegExPattern(String sGlob) {
		String sRegEx = globToRegEx(sGlob);
		return Pattern.compile(sRegEx, Pattern.CASE_INSENSITIVE);
	}
}
