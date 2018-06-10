package earth.cube.gradle.plugins.commons.lib_encrypter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import earth.cube.gradle.plugins.commons.utils.Spooler;

public class LibEncrypter extends DefaultTask {
	
	private static final int BUF_SIZE = 0x8000;

	private File _decryptedDir;
	private File _zipFile;
	private File _encryptedFile;
	private ZipOutputStream _zip;
	private int _nCount;
	
	@Input
	public String passphrase;
	
	
	private void addZip(File dir, String sRelPath) throws IOException {
		for(File file : dir.listFiles()) {
			if(file.isDirectory())
				addZip(file, sRelPath  + '/' + file.getName());
			else {
				ZipEntry entry = new ZipEntry(sRelPath + '/' + file.getName());
				entry.setTime(file.lastModified());
				_zip.putNextEntry(entry);
				Spooler.spool(new FileInputStream(file), _zip, true, false);
				_zip.closeEntry();
				_nCount++;
			}
		}
	}

	private void createZip() throws IOException {
		_zip = new ZipOutputStream(new FileOutputStream(_zipFile));
		try {
			addZip(_decryptedDir, "");
		}
		finally {
			_zip.close();
		}
		if(_nCount == 0)
			_zipFile.delete();
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
	public void encryptLibs() throws Exception {
		Project project = getProject();
		
		_decryptedDir = project.file("lib/decrypted");
		_encryptedFile = project.file("lib/lib.bin");
		_zipFile = new File(project.getBuildDir(), "tmp/lib.zip");
		_zipFile.getParentFile().mkdirs();
		
		createZip();
		
		String sKey = (String) project.getProperties().get("lib_passphrase");
		encrypt(Cipher.ENCRYPT_MODE, sKey, _zipFile, _encryptedFile);

		if(!_zipFile.delete())
			throw new IllegalStateException("Could not delete ZIP file!");
	}

}
