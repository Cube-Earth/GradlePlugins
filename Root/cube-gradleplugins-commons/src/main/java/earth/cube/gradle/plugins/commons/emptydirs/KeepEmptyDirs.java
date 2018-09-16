package earth.cube.gradle.plugins.commons.emptydirs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jgit.ignore.IgnoreNode;
import org.eclipse.jgit.ignore.IgnoreNode.MatchResult;
import org.eclipse.jgit.lib.Repository;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;

import earth.cube.gradle.plugins.commons.utils.FileUtils;
import earth.cube.gradle.plugins.commons.utils.Flag;
import earth.cube.gradle.plugins.commons.utils.fileset.FileSet;
import earth.cube.gradle.plugins.commons.utils.fileset.FileSetBuilder;
import earth.cube.gradle.plugins.commons.utils.fileset.IVisitor;

public class KeepEmptyDirs {
	
	protected Path _gitWorkTree;
	protected Path _gitDir;
	private IgnoreNode _ignore;
	private FileSet _keep;
	private FileSet _purge;
	private int _nCreated;
	private Logger _logger;
	private boolean _bProbe;
	
	
	public KeepEmptyDirs(Path baseDir) throws IOException {
		_gitWorkTree = baseDir;
		_gitDir = baseDir.resolve(".git");
		init();
	}
	
	public KeepEmptyDirs(Repository repo) throws IOException {
		_gitWorkTree = repo.getWorkTree().toPath();
		_gitDir = repo.getDirectory().toPath();
		init();
	}

	protected void init() throws IOException {
		_ignore = new IgnoreNode();
		
		Path ignoreFile = _gitWorkTree.resolve(".gitignore");
		if(Files.exists(ignoreFile))
			try(InputStream is = Files.newInputStream(ignoreFile)) {
				_ignore.parse(is);
			}

		Path keepFile = _gitWorkTree.resolve(".keepdirs");
		_keep = new FileSetBuilder().add(keepFile).build();

		Path purgeFile = _gitWorkTree.resolve(".purge");
		_purge = new FileSetBuilder().add(purgeFile).build();
	}
	
	public void enableProbe() {
		_bProbe = true;
	}

	
	@TaskAction
	public int mark() throws IOException {
		_nCreated = 0;
		init();
		final Flag alreadyWarned = new Flag();
		
		_keep.process(_gitWorkTree, new IVisitor() {
			
			@Override
			public void visitFile(Path file) {
				if(!alreadyWarned.get()) {
					if(_logger != null)
						_logger.warn("Exclude all files in your .keep definition file to be more efficient!");
					alreadyWarned.set();
				}
			}
			
			@Override
			public void visitDirectory(Path dir, boolean bDirsOnly) throws IOException {
				Path relPath = _gitWorkTree.relativize(dir);
				if(!dir.startsWith(_gitDir) && !FileUtils.hasChildren(dir) && _ignore.isIgnored(relPath.toString(), true) != MatchResult.IGNORED && !_purge.isTransitiveMatched(_gitWorkTree, dir)) {
					_nCreated++;
					if(_bProbe) {
						if(_logger != null)
							_logger.debug("PROBING: .keep file would be created for directory '" + dir + "'.");
					}
					else {
						if(_logger != null)
							_logger.debug("creating .keep file for directory '" + dir + "' ...");
						Files.newOutputStream(dir.resolve(".keep")).close();
					}
				}
			}
		});
		
		if(_logger != null)
			if(_bProbe)
				_logger.info("PROBING: " + _nCreated + " empty directories would have been marked!");
			else
				_logger.info(_nCreated + " empty directories marked!");
		
		return _nCreated;
	}

}
