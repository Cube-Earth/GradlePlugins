package earth.cube.gradle.plugins.commons.utils.fileset.rules;

public class RuleSetOutcome {
	
	private boolean _bMatched;
	
	private boolean _bNegate;

	private boolean _bStop;
	
	private boolean _bSkipDir;
	
	
	public RuleSetOutcome setMatched(boolean bMatched) {
		_bMatched = bMatched;
		return this;
	}


	public boolean getMatched() {
		return _bMatched;
	}


	public void setStop(boolean bStop) {
		_bStop = bStop;
	}


	public void setNegate(boolean bNegate) {
		_bNegate = bNegate;
	}

	public void setSkipDir(boolean bSkipDir) {
		_bSkipDir = bSkipDir;
	}


	public boolean shouldStop() {
		return _bStop;
	}


	public boolean getNegate() {
		return _bNegate;
	}
	
	
	public boolean shouldSkipDir() {
		return _bSkipDir;
	}
	
	public boolean isIncluded() {
		return _bMatched ^ _bNegate;
	}
	
	

}
