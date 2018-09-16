package earth.cube.gradle.plugins.commons.purge;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.eclipse.jgit.lib.Repository;
import org.gradle.api.logging.Logger;

import earth.cube.gradle.plugins.commons.utils.fileset.FileSet;
import earth.cube.gradle.plugins.commons.utils.fileset.FileSetBuilder;
import earth.cube.gradle.plugins.commons.utils.fileset.IVisitor;

public class Purge {
	
	private FileSet _purge;
	private Path _gitWorkTree;
	private Path _gitDir;
	private boolean _bProbe;
	private Logger _logger;
	
	private int _nDeletedDirs;
	private int _nDeletedFiles;
	private long _nDeletedSize;
	
	
	public Purge(Path baseDir) throws IOException {
		_gitWorkTree = baseDir;
		_gitDir = baseDir.resolve(".git");
		init();
	}
	
	public Purge(Repository repo) throws IOException {
		_gitWorkTree = repo.getWorkTree().toPath();
		_gitDir = repo.getDirectory().toPath();
		init();
	}
	
	public void enableProbe() {
		_bProbe = true;
	}	
	
	public void setLogger(Logger logger) {
		_logger = logger;
	}

	
	protected void init() throws IOException {
		Path keepFile = _gitWorkTree.resolve(".purge");
		_purge = new FileSetBuilder().add(keepFile).build();
	}


	
	public void execute() throws IOException {
		_purge.process(_gitWorkTree, new IVisitor() {
			
			private void delete(Path path) throws IOException {
				if(path.startsWith(_gitDir))
					return;

				boolean bDir = Files.isDirectory(path);
				String sLabel = bDir ? "directory" : "file";

				if(bDir)
					_nDeletedDirs++;
				else {
					_nDeletedFiles++;
					_nDeletedSize += Files.size(path);
				}
				
				if(_bProbe) {
					if(_logger != null)
						_logger.debug("PROBING: " + sLabel + " '" + path + "' would be purged.");
				}
				else {
					if(_logger != null)
						_logger.debug("purging " + sLabel + " '" + path + "' ...");
					Files.delete(path);
				}
			}
			
			@Override
			public void visitFile(Path file) throws IOException {
				delete(file);
			}

			
			@Override
			public void visitDirectory(Path dir, boolean bDirsOnly) throws IOException {
				if(!dir.startsWith(_gitDir))
					Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
						
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							delete(file);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
							delete(dir);
							return FileVisitResult.CONTINUE;
						}
						
					});
			}
		});
		
		if(_logger != null)
			if(_bProbe) {
				_logger.info("PROBING: Directories purged: " + _nDeletedDirs);
				_logger.info("PROBING: Files       purged: " + _nDeletedFiles);
				_logger.info("PROBING: Size (MB)   purged: %.02f", _nDeletedSize / 1024f / 1024);
			}
			else {
				_logger.info("Directories purged: " + _nDeletedDirs);
				_logger.info("Files       purged: " + _nDeletedFiles);
				_logger.info("Size (MB)   purged: %.02f", _nDeletedSize / 1024f / 1024);
			}
	}

}
