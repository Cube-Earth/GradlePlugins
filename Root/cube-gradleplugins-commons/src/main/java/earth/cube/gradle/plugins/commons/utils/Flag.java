package earth.cube.gradle.plugins.commons.utils;

public class Flag {
	
	private boolean _b;
	
	public Flag() {
	}

	public Flag(boolean bInitialValue) {
		_b = bInitialValue;
	}
	
	public void set() {
		_b = true;
	}
	
	public void unset() {
		_b = false;
	}

	public boolean get() {
		return _b;
	}
	
}
