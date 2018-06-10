package earth.cube.gradle.plugins.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Spooler {
	
	private static final int BUF_SIZE = 0x8000;

	public static void spool (InputStream in, OutputStream out, boolean bCloseIn, boolean bCloseOut) throws IOException {
		try {
			byte[] buf = new byte[BUF_SIZE];
			int n = in.read(buf);
			while(n > 0) {
				out.write(buf, 0, n);
				n = in.read(buf);
			}
		}
		finally {
			Throwable t1 = null;
			if(bCloseIn)
				try {
					in.close();
				}
				catch(Throwable t) {
					t1 = t;
				}
			if(bCloseOut)
				try {
					out.close();
				}
				catch(Throwable t) {
					t1 = t1 == null ? t : new Exception(t1);
				}
			if(t1 != null)
				throw new IOException(t1);
		}
	}

}
