package earth.cube.gradle.plugins.commons.utils.fileset;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import earth.cube.gradle.plugins.commons.utils.Encoder;
import earth.cube.gradle.plugins.commons.utils.fileset.paths.PathInfo;
import earth.cube.gradle.plugins.commons.utils.fileset.rules.RuleSet;
import earth.cube.gradle.plugins.commons.utils.fileset.rules.RuleSetOutcome;

/**
abc/projects
	+** /Documentum
		!interlockedExchange.lock
	-src/
 */

public class FileSet {
	
	private String _sPath;
	private String _sPattern;
	private List<FileSet> _fileSet = new ArrayList<>();
	private RuleSet _rules = new RuleSet();
	private Pattern _pattern;
	private Path _root;
	private IVisitor _visitor;
	private boolean _bDirsOnly;
	
	public FileSet() {
		_bDirsOnly = true;
		_sPath = "";
	}
	
	public FileSet(String sPath) {
		init(sPath);
	}

	
	protected void init(String sPath) {
		_bDirsOnly = sPath.endsWith("/");
//		Pattern p = Pattern.compile("(/?(?:[^*/]+/)*)(.*?)");       wrong regex
		Pattern p = Pattern.compile("(/?(?:[^*/]+(?:/|$))*)(.*?)");
		Matcher m = p.matcher(sPath);
		m.matches();
		_sPath = m.group(1);
		_sPattern = m.group(2);
		if(_sPattern != null && _sPattern.length() != 0)
			_pattern = Encoder.globToRegExPattern(_sPattern);
	}
	
	public void add(FileSet fs) {
		_fileSet.add(fs);
	}
	
	public RuleSet getRules() {
		return _rules;
	}
	
	public boolean isAbsolute() {
		return _sPath.startsWith("/");
	}

	private void processFileSets(Path dir) throws IOException {
		for(FileSet fs : _fileSet) {
			fs.process(dir, _visitor);
		}
	}

	
	private void processDir(Path dir, String sPath) throws IOException {
		boolean bSkipDir = false;
		if(_pattern == null) {
			if(_sPath.length() != 0)
				_visitor.visitDirectory(dir, _bDirsOnly);
		}
		else
			if(_pattern.matcher(sPath).matches()) {
				RuleSetOutcome outcome = _rules.evaluate(dir, sPath);
				if(outcome.isIncluded())
					_visitor.visitDirectory(dir, _bDirsOnly);
				bSkipDir = outcome.shouldSkipDir();
			}
		processFileSets(dir);
		
		if(Files.exists(dir))
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
				for (Path entry : stream) {
					if(Files.isDirectory(entry))
						processDir(entry, sPath + entry.getFileName() + '/');
					else
						if(!bSkipDir && !_bDirsOnly && _pattern.matcher(sPath + entry.getFileName()).matches() && _rules.evaluateIncluded(null, sPath + entry.getFileName()))
							_visitor.visitFile(entry);
				}
			}		
		
	}

	public void process(Path root, IVisitor visitor) throws IOException {
		_root = root;
		_visitor = visitor;
		Path dir = root.resolve(_sPath);
		if(_pattern != null || _fileSet.size() > 0 || !_rules.isEmpty())
			processDir(dir, "");
		else
			_visitor.visitDirectory(dir, _bDirsOnly);
			
		_root = null;
	}
	
		
	protected boolean shiftName(String[] saPath) {
		String sPath = saPath[0];
		int i = sPath.lastIndexOf('/', sPath.length() - 2);
		if(i < 1)
			return false;

		saPath[0] = sPath.substring(0, i+1);
		saPath[1] = sPath.substring(i+1) + saPath[1];
		return true;
	}
	
	
	private boolean isMatched(Path root, PathInfo path) {
		Path relRoot = root.resolve(_sPath);
		if(_sPath.startsWith("/"))
			path.reset();
		
		boolean bDirMatched;
		for(PathInfo shiftedPath : path.getAllShifts()) {
			Path dir = root.resolve(shiftedPath.get());
			String sRelPath = isAbsolute() ? dir.toString() + '/' : dir.startsWith(relRoot) ? relRoot.relativize(dir).toString() + '/' : null;
			if(sRelPath != null) {
				boolean bSkipDir = false;
				if(_pattern == null)
					bDirMatched = true;
				else
					if(_pattern.matcher(sRelPath).matches()) {
						RuleSetOutcome outcome = _rules.evaluate(dir, sRelPath);
						if((outcome.getMatched() && outcome.getNegate()))
							continue;
						bSkipDir = outcome.shouldSkipDir();
						bDirMatched = outcome.getMatched();
					}
					else
						bDirMatched = false;
				
				// TODO use isAbsolute
				
				boolean bMatched = false;
				if(bSkipDir) {
					continue;
				}
				else {
					if(bDirMatched) {
						String sFileName = shiftedPath.getFileName();
						if(shiftedPath.isLastDirectory()) {
							if(_bDirsOnly)
								bMatched = sFileName == null;
							else {
								bMatched = _pattern.matcher(sRelPath + sFileName).matches() && _rules.evaluateIncluded(null, sRelPath + sFileName);
								if(bMatched)
									return true;
								else
									continue;
							}
						}
					}
				}
				
				if(!bDirMatched) {
					for(FileSet fs : _fileSet) {
						if(fs.isAbsolute() && fs.isMatched(relRoot, shiftedPath))
							return true;
					}
				}
				else {
					PathInfo remainingPath = shiftedPath.commit();
					for(FileSet fs : _fileSet) {
						if(fs.isMatched(relRoot, remainingPath))
							return true;
					}
				}
			}
			
		}
		
		return false;
	}

	
	public boolean isMatched(Path root, Path path) {
		return isMatched(root, new PathInfo(path));
	}

	public boolean isTransitiveMatched(Path root, Path path) {
		PathInfo entry = new PathInfo(path);
		for(PathInfo variant : entry.getAllShifts()) {
			if(isMatched(root, variant.crop()))
				return true;
		}
		
		return false;
	}
	

}
