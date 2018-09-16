package earth.cube.gradle.plugins.commons.gitignoretpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.ignore.IgnoreNode;
import org.eclipse.jgit.ignore.IgnoreNode.MatchResult;
import org.eclipse.jgit.lib.Repository;

import earth.cube.gradle.plugins.commons.utils.fileset.FileSet;
import earth.cube.gradle.plugins.commons.utils.fileset.FileSetBuilder;
import earth.cube.gradle.plugins.commons.utils.fileset.IVisitor;

public class GitIgnoreTpl {
	
	protected Path _gitWorkTree;
	protected Path _gitDir;
	private IgnoreNode _ignore;
	private FileSet _ignoreTpl;
	private FileSet _purge;
	private List<String> _newIgnore = new ArrayList<>();
	private Path _ignoreFile;
	
	
	public GitIgnoreTpl(Path baseDir) throws IOException {
		_gitWorkTree = baseDir;
		_gitDir = baseDir.resolve(".git");
		init();
	}
	
	public GitIgnoreTpl(Repository repo) throws IOException {
		if(repo == null) {
			return;
		}
		_gitWorkTree = repo.getWorkTree().toPath();
		_gitDir = repo.getDirectory().toPath();
		init();
	}

	protected void init() throws IOException {
		_ignore = new IgnoreNode();
				
		_ignoreFile = _gitWorkTree.resolve(".gitignore");
		if(Files.exists(_ignoreFile))
			try(InputStream is = Files.newInputStream(_ignoreFile)) {
				_ignore.parse(is);
			}

		Path gitIgnoreTplFile = _gitWorkTree.resolve(".gitignore_tpl");
		_ignoreTpl = new FileSetBuilder().add(gitIgnoreTplFile).build();

		Path purgeFile = _gitWorkTree.resolve(".purge");
		_purge = new FileSetBuilder().add(purgeFile).build();
	}

	public void addIgnore(Path path, boolean bDir) {
		if(_gitDir == null || path.startsWith(_gitDir))
			return;

		Path relPath = _gitWorkTree.relativize(path);
		if(!path.startsWith(_gitDir) && _ignore.isIgnored(relPath.toString(), bDir) != MatchResult.IGNORED && !_purge.isMatched(_gitWorkTree, path)) {
			_newIgnore.add(relPath + (bDir ? "/**" : ""));
		}
	}
	
	public void save() throws IOException {
		if(_newIgnore.size() > 0)
			Files.write(_ignoreFile, ("\n" + String.join("\n", _newIgnore)).getBytes("utf-8"), StandardOpenOption.APPEND);
	}
	
	public void execute() throws IOException {
		_ignoreTpl.process(_gitWorkTree, new IVisitor() {
						
			@Override
			public void visitFile(Path file) {
				addIgnore(file, false);
			}
			
			@Override
			public void visitDirectory(Path dir, boolean bDirsOnly) throws IOException {
				addIgnore(dir, true);
			}
		});
		
		save();
	}

}
