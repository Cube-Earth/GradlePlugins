package earth.cube.gradle.plugins.commons.eclipse_launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.api.tasks.TaskAction;


public class Launcher extends DefaultTask {

	private File _installationDir;
	private File _workspaceDir;
	private String _sApplication;
	public List<URL> _classpath = new ArrayList<>();
	public List<String> _args = new ArrayList<>();
	
	
	// TODO: remove
	public void __prepare(String sInstallationDir) {
		File dir = new File(sInstallationDir);
		new File(dir, "plugins").mkdirs();
		new File(dir, "startup.jar").mkdirs();
	}
	
	public void setInstallationDir(String sInstallationDir) {
		_installationDir = new File(sInstallationDir);
		if(!_installationDir.exists())
			throw new IllegalArgumentException("Directory '" + sInstallationDir + "' does not exist!");
		if(!new File(_installationDir, "startup.jar").exists())
			throw new IllegalArgumentException("Directory '" + sInstallationDir + "' is no valid Eclipse installation!");
	}
		
	public void setWorkspaceDir(String sWorkspaceDir) {
		_workspaceDir = new File(sWorkspaceDir);
	}
	
	public void addClasspath(String sClasspath) throws MalformedURLException {
		_classpath.add(new File(sClasspath).toURI().toURL());
	}
	
	private void download(URL url, File f) throws IOException {
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		try(FileOutputStream fos = new FileOutputStream(f)) {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}
	}

	public void addPlugin(String sRemoteUrl, String sLocalFileName) throws IOException {
		File f = new File(_installationDir, "plugins/" + sLocalFileName);
		if(!f.exists())
			download(new URL(sRemoteUrl), f);
		_classpath.add(f.toURI().toURL());
	}
	
	public void addPlugin(String sRemoteUrl) throws IOException {
		addPlugin(sRemoteUrl, new File(sRemoteUrl).getName());
	}

	
	public void setApplication(String sApplication) {
		_sApplication = sApplication;
	}
	
	public void addArguments(String... saArg) {
		_args.addAll(Arrays.asList(saArg));
	}
	
	@TaskAction
	public void launch() throws Exception {
		addClasspath(new File(_installationDir, "startup.jar").getAbsolutePath());
		
		List<String> args = new ArrayList<>();
		args.add("-clean");
		args.add("-application");
		args.add(_sApplication);
		if(_workspaceDir != null) {
			args.add("-data");
			args.add(_workspaceDir.getAbsolutePath());
		}
		args.addAll(_args);
		
		try(URLClassLoader cl = new URLClassLoader(_classpath.toArray(new URL[_classpath.size()]))) {
			Class<?> c = cl.loadClass("org.eclipse.equinox.launcher.Main");
			Method m = c.getDeclaredMethod("run", String[].class);
			Object o = c.newInstance();
			int nRC = (int) m.invoke(o, new Object[] { args.toArray(new String[args.size()]) });
			if(nRC != 0)
				throw new StopExecutionException("Eclipse returned exit code " + nRC + "!");
		}
	}
	
}
