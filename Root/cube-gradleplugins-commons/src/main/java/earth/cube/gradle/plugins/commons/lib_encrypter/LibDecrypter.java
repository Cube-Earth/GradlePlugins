package earth.cube.gradle.plugins.commons.lib_encrypter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import earth.cube.gradle.plugins.commons.utils.Spooler;

public class LibDecrypter extends DefaultTask {
	
	private static final int BUF_SIZE = 0x8000;

	private File _decryptedDir;
	private File _zipFile;
	private File _encryptedFile;
	private ZipFile _zip;
	
	@Input
	public String passphrase;
	
	
	private void unzip() throws IOException {
		_zip = new ZipFile(_zipFile);
		try {
			for(ZipEntry e : Collections.list(_zip.entries())) {
				File file = new File(_decryptedDir, e.getName());
				Spooler.spool(_zip.getInputStream(e), new FileOutputStream(file), true, true);
				file.setLastModified(e.getTime());
			}
		}
		finally {
			_zip.close();
		}
	}
	
	private void encrypt(int cipherMode, String sKey, File inFile, File outFile) throws Exception {
       Key key = new SecretKeySpec(sKey.getBytes(), "AES");
       Cipher cipher = Cipher.getInstance("AES");
       cipher.init(cipherMode, key);
       
       byte[] buf = new byte[BUF_SIZE];

       try(InputStream in = new FileInputStream(inFile)) {
    	   try(OutputStream out = new CipherOutputStream(new FileOutputStream(outFile), cipher)) {
    		   int n = in.read(buf);
    		   out.write(buf,  0, n);
    	   }
       }
	}

	@TaskAction
	public void decryptLibs() throws Exception {
		Project project = getProject();
		
		_decryptedDir = project.file("lib/decrypted2");
		_decryptedDir.mkdirs();
		_encryptedFile = project.file("lib/lib.bin");
		_zipFile = new File(project.getBuildDir(), "tmp/lib.zip");
		
		String sKey = (String) project.getProperties().get("lib_passphrase");
		encrypt(Cipher.DECRYPT_MODE, sKey, _encryptedFile, _zipFile);

		unzip();

		if(!_zipFile.delete())
			throw new IllegalStateException("Could not delete ZIP file!");
	}

}
