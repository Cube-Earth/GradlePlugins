package earth.cube.gradle.plugins.commons.utils.fileset.paths;

import java.util.ArrayList;
import java.util.List;

import earth.cube.gradle.plugins.commons.utils.StringUtils;


public class PathStructure {
	
	private String _sPath;
	
	private List<Integer> _parts;

	private int _nSize;
	
	
	public PathStructure(String sPath) {
		init(sPath);
	}

	private void init(String sPath) {
		_sPath = sPath;
		
		_parts = new ArrayList<>();
		if(sPath.length() == 0)
			return;
		
		_parts.add(0);
		
		int j = 0;
		int i = sPath.indexOf('/');
		while(i != -1) {
			if(i != 0) {
				_parts.add(i + 1);
			}
			j = i;
			i = sPath.indexOf('/', j+1);
		}
		if(sPath.equals("/") || (!sPath.endsWith("/") && sPath.length() > 0)) {
			_parts.add(sPath.length());
		}
		_nSize = _parts.size();
	}
	
	public int size() {
		return _nSize;
	}
	
	public boolean isEmpty() {
		return _nSize == 0;
	}
	
	public String substring(int nStart, int nEnd) {
		return _sPath.substring(_parts.get(nStart), _parts.get(nEnd));
	}
	
	public boolean isDirectory() {
		return _sPath.endsWith("/");
	}
	
	public boolean isLastDirectory(int nIdx) {
		return nIdx == _parts.size() - (isDirectory() ? 1 : 2);
	}
	
	public String getFileName() {
		String s = isDirectory() || _nSize == 0 ? null : substring(_nSize-2, _nSize-1);
		return s != null && s.startsWith("/") ? s.substring(1) : s;
	}
	
	public String toString() {
		return _sPath + '|' + StringUtils.join(_parts, ",") + '|' + _nSize;
	}

}
