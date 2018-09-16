package earth.cube.gradle.plugins.commons.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
	public static String join(Collection<?> c, String sDelim) {
		if(c == null)
			return null;
		
		StringBuilder sb = new StringBuilder();
		boolean bFirst = true;
		for(Object o : c) {
			if(bFirst)
				bFirst = false;
			else
				sb.append(sDelim);
			if(o != null)
				sb.append(o);
		}
		return sb.toString();
	}
	
	public static <T> String join(Collection<T> c, String sDelim, IConverter<T,String> converter) {
		if(c == null)
			return null;
		
		StringBuilder sb = new StringBuilder();
		boolean bFirst = true;
		for(T o : c) {
			if(bFirst)
				bFirst = false;
			else
				sb.append(sDelim);
			if(o != null)
				sb.append(converter.convert(o));
		}
		return sb.toString();
	}
	
	public static <T> List<T> split(String sSource, String sDelimRegex, IConverter<String,T> converter) {
		List<T> values = new ArrayList<>();
		for(String sValue : sSource.split(sDelimRegex)) {
			values.add(converter.convert(sValue));
		}
		return values;
	}

	public static <T> List<T> split(String sSource, String sDelim, char cEscapeChar, IConverter<String, T> converter) {
		if(sSource == null)
			return null;
		
		List<T> values = new ArrayList<>();
		Pattern p = Pattern.compile(String.format("((?:%1$s%2$s|%1$s.|.)*?)(%2$s|$)", cEscapeChar == '\\' ? "\\\\" : Character.toString(cEscapeChar), sDelim));
		Matcher m = p.matcher(sSource);
		while(m.find()) {
			values.add(converter.convert(m.group(1)));
			if(m.hitEnd())
				break;
		}
		return values;
	}

	public static String[] splitAsStringArray(String sSource, String sDelim, char cEscapeChar, IConverter<String, String> converter) {
		List<String> values = split(sSource, sDelim, cEscapeChar, converter);
		return values == null ? null : values.toArray(new String[values.size()]);
	}
	
	
	public static String[] splitStrict(String sSource, String sDelim) {
		List<String> values = new ArrayList<>();
		
		int j = 0;
		int i = sSource.indexOf(sDelim);
		while(i != -1) {
			values.add(sSource.substring(j, i));
			j = i + 1;
			i = sSource.indexOf(sDelim, j);
		}
		values.add(sSource.substring(j));
		return values.toArray(new String[values.size()]);
	}

}
