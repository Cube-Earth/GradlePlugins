package earth.cube.gradle.plugins.commons.utils.fileset.paths;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;

public class PathInfo {
	
	private PathStructure _structure;
	private int _nComitted;
	private int _nInEvaluation;
	
	
	public PathInfo(String sPath) {
		init(new PathStructure(sPath), 0, 0);
	}
	
	protected PathInfo(PathStructure structure, int nCommitted, int nInEvaluation) {
		init(structure, nCommitted, nInEvaluation);
	}
	
	public PathInfo(Path path) {
		String sPath = path.toAbsolutePath().toString();
		if(Files.isDirectory(path))
			sPath += '/';
		init(new PathStructure(sPath), 0, 0);
	}

	private void init(PathStructure structure, int nComitted, int nInEvaluation) {
		_structure = structure;
		_nComitted = nComitted;
		_nInEvaluation = nInEvaluation;
	}
	
	public PathInfo shift() {
		if(_nInEvaluation >= _structure.size())
			throw new NoSuchElementException();
		return _nInEvaluation + 1 == _structure.size() ? null : new PathInfo(_structure, _nComitted, _nInEvaluation+1);
	}
	
	public PathInfo reset() {
		return new PathInfo(_structure, 0, 0);
	}

	public PathInfo commit() {
		return new PathInfo(_structure, _nInEvaluation, _nInEvaluation);
	}
	
	public String get() {
		return _structure.substring(_nComitted, _nInEvaluation);
	}
	
	public boolean isDirectory() {
		return get().endsWith("/");
	}
	
	public Iterable<PathInfo> getAllShifts() {
		return new PathIterator(this);
	}
	
	public boolean isLastDirectory() {
		return _structure.isLastDirectory(_nInEvaluation);
	}
	
	public String getFileName() {
		return _structure.getFileName();
	}
	
	public PathInfo crop() {
		return new PathInfo(_structure.substring(0, _nInEvaluation));
	}
	
	
	protected PathInfo safeShift() {
		return _nComitted == _nInEvaluation ? _nInEvaluation >= _structure.size() ? null : shift() : this;
	}

	@Override
	public String toString() {
		return _structure.toString() + '|' + _nComitted + '|' + _nInEvaluation;
	}
	
}
