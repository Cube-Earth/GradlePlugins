package earth.cube.gradle.plugins.commons.purge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.eclipse.jgit.ignore.IgnoreNode;
import org.eclipse.jgit.lib.Repository;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;

import earth.cube.gradle.plugins.commons.utils.GitRepoFinder;
import earth.cube.gradle.plugins.commons.utils.fileset.FileSet;
import earth.cube.gradle.plugins.commons.utils.fileset.FileSetBuilder;
import earth.cube.gradle.plugins.commons.utils.fileset.IVisitor;

public class PurgeTask extends DefaultTask {
	
	private IgnoreNode _ignore;
	private FileSet _purge;
	private File _gitWorkTree;
	private Path _gitDir;
	
	private int _nDeletedDirs;
	private int _nDeletedFiles;
	private long _nDeletedSize;
	

	protected void init() throws IOException {
		Repository repo = GitRepoFinder.findGitRepo(getProject().getRootDir());
		_ignore = new IgnoreNode();
		_gitWorkTree = repo.getWorkTree();
		_gitDir = repo.getDirectory().toPath();
		
		File ignoreFile = new File(_gitWorkTree, ".gitignore");
		if(ignoreFile.exists())
			try(InputStream is = new FileInputStream(ignoreFile)) {
				_ignore.parse(is);
			}

		File keepFile = new File(_gitWorkTree, ".purge");
		_purge = new FileSetBuilder().add(keepFile).build();
	}


	
	@TaskAction
	public void purge() throws IOException {
		init();
		final Path gitWorkTree = _gitWorkTree.toPath();
		final boolean bProbe = getProject().hasProperty("probe");
		final Logger logger = getLogger();
		
		_purge.process(gitWorkTree, new IVisitor() {
			
			private void delete(Path path) throws IOException {
				boolean bDir = Files.isDirectory(path);
				String sLabel = bDir ? "directory" : "file";

				if(bDir)
					_nDeletedDirs++;
				else {
					_nDeletedFiles++;
					_nDeletedSize += Files.size(path);
				}
				
				if(bProbe)
					logger.debug("PROBING: " + sLabel + " '" + path + "' would be purged.");
				else {
					logger.debug("purging " + sLabel + " '" + path + "' ...");
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
						public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
							delete(dir);
							return FileVisitResult.CONTINUE;
						}
						
					});
			}
		});
		
		if(bProbe) {
			logger.info("PROBING: Directories purged: " + _nDeletedDirs);
			logger.info("PROBING: Files       purged: " + _nDeletedFiles);
			logger.info("PROBING: Size (MB)   purged: %.02f", _nDeletedSize / 1024f / 1024);
		}
		else {
			logger.info("Directories purged: " + _nDeletedDirs);
			logger.info("Files       purged: " + _nDeletedFiles);
			logger.info("Size (MB)   purged: %.02f", _nDeletedSize / 1024f / 1024);
		}
	}

}
