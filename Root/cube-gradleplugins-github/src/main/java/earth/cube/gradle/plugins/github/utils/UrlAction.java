package earth.cube.gradle.plugins.github.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UrlAction {

	@SuppressWarnings("unchecked")
	public static Map<String,Object> doAction(String sVerb, String sUrl, Object postData, char[] userPass) throws IOException {
		Map<String,Object> result = null;
		boolean bError = true;
		int nResponseCode = -1;
		String sResponseMessage = null;
		URL url = new URL(sUrl);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		try {
			conn.setRequestMethod(sVerb.toUpperCase());
			if(userPass != null)
				conn.setRequestProperty("Authorization", "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(new String(userPass).getBytes("iso-8859-1")));	
			if(postData != null) {
				conn.setDoOutput(true);
				if(postData instanceof File) {
					File f = (File) postData;
					conn.setRequestProperty("Content-length", Long.toString(f.length()));
					conn.setRequestProperty("Content-type", "application/octet-stream");
					Spooler.spool(new FileInputStream(f), conn.getOutputStream(), true, true);
				}
				else {
					String s = postData.toString();
					byte[] b = s.getBytes("utf-8");
					conn.setRequestProperty("Content-length", Long.toString(b.length));
					conn.setRequestProperty("Content-type", "application/vnd.github.v3+json; charset=utf-8");
					Spooler.spool(new ByteArrayInputStream(b), conn.getOutputStream(), true, true);
				}
			}
			
			nResponseCode = conn.getResponseCode();
			sResponseMessage = conn.getResponseMessage();
			
			bError = nResponseCode < 200 || nResponseCode >= 300;
			String sCharSet = conn.getContentEncoding();
			Reader in = new InputStreamReader(bError ? conn.getErrorStream() : conn.getInputStream(), sCharSet == null ? "iso-8859-1" : sCharSet);
			try {
				JSONParser parser = new JSONParser();
				result = (Map<String, Object>) parser.parse(in);
			}
			catch(ParseException e) {
			}
			finally {
				in.close();
			}
		}
		finally {
			if(result == null)
				result = new HashMap<>();

			result.put("#ResponseOk", !bError);
			result.put("#ResponseCode", nResponseCode);
			result.put("#ResponseMessage", sResponseMessage);
			conn.disconnect();
		}
		return result;
	}
	
	public static Map<String,Object> doGet(String sUrl) throws IOException {
		return doAction("GET", sUrl, null, null);
	}
	
	public static Map<String,Object> doPost(String sUrl, Object postData, char[] userPass) throws IOException {
		return doAction("POST", sUrl, postData, userPass);
	}

	public static void raiseException(Map<String,Object> params) throws IOException {
		StringBuilder sb = new StringBuilder("HTTP request failed.");
		sb.append("\nresponse code: ").append(params.get("#ResponseCode"));
		sb.append("\nresponse message: ").append(params.get("#ResponseMessage"));
		for(Entry<String, Object> e : params.entrySet())
			if(e.getValue() instanceof String)
				sb.append('\n').append(e.getKey()).append(": ").append(e.getValue());
		throw new IOException(sb.toString());
	}
	
	public static void dump(Map<String,Object> params) {
		StringBuilder sb = new StringBuilder("===================");
		for(Entry<String, Object> e : params.entrySet())
			sb.append('\n').append(e.getKey()).append(": ").append(e.getValue());
		System.out.println(sb.toString());
	}
	
	public static char[] combine(Object... arr) {
		int n = 0;
		for(Object o : arr) {
			if(o instanceof char[])
				n += ((char[]) o).length;
			else
				n += o.toString().length();
		}
		
		char[] buf = new char[n];

		int i = 0;
		for(Object o : arr) {
			char[] c = o instanceof char[] ? (char[]) o : o.toString().toCharArray();
			System.arraycopy(c, 0, buf, i, c.length);
			i += c.length;
		}

		return buf;
	}

}
