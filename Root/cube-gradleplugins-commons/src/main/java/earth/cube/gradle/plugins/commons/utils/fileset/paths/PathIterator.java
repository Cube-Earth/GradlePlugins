package earth.cube.gradle.plugins.commons.utils.fileset.paths;

import java.util.Iterator;

public class PathIterator implements Iterable<PathInfo>, Iterator<PathInfo> {
	
	private PathInfo _path;
	
	public PathIterator(PathInfo path) {
		_path = path.safeShift();
	}
	
	@Override
	public Iterator<PathInfo> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return _path != null;
	}

	@Override
	public PathInfo next() {
		PathInfo next = _path;
		_path = next.shift();
		if(_path != null && !_path.isDirectory())
			_path = null;
		return next;
	}


}
