package earth.cube.gradle.plugins.commons.utils.fileset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import earth.cube.gradle.plugins.commons.utils.fileset.rules.RuleSet;
import earth.cube.gradle.plugins.commons.utils.fileset.rules.RuleType;

public class FileSetBuilder {
	
	protected FileSet _root = new FileSet();
	protected Stack<Scope> _scopes = new Stack<>();
	protected Scope _current = new Scope(_root);
	protected Scope _parent = new Scope(_root);
	protected Pattern _p = Pattern.compile("( *)((?:\\+|-|!!|!)?)(.*)");
	protected int _nLine;
	
	
	protected static class Scope {
		public final static int FILESET = 0;
		public final static int RULESET = 1;
		
		public int intend;
		public int type;
		public FileSet fs;
		public RuleSet rs;
		
		
		public Scope(FileSet fs) {
			intend = -1;
			type = Scope.FILESET;
			this.fs = fs;
		}


		public Scope(int nIntend, RuleType ruleType, String sPath) {
			intend = nIntend;
			if(ruleType == RuleType.NONE) {
				type = FILESET;
				fs = new FileSet(sPath);
			}
			else {
				type = RULESET;
				rs = new RuleSet(ruleType, sPath);
			}
		}
	}
	
	public FileSetBuilder() {
		_scopes.push(_parent);
	}
	
	
	public FileSet build() {
		return _root;
	}
	
	
	private void locateScope(int nIntend) {
		while(_current.intend > nIntend) {
			_current = _scopes.pop();
			if(_current == null || _current.intend > nIntend || _scopes.isEmpty())
				throw new IllegalStateException("Malformed intends (line " + _nLine + ")!");
			_parent = _scopes.peek();
		}
	}
	
	private void changeScope(Scope newScope) {
		if(newScope.intend > _current.intend) {
			_scopes.push(_current);
			_parent = _current;
		}
		else
			if(newScope.intend < _current.intend)
				throw new AssertionError();
		_current = newScope;
		
		if(_parent.type == Scope.FILESET)
			if(_current.type == Scope.FILESET)
				_parent.fs.add(_current.fs);
			else
				_parent.fs.getRules().add(_current.rs);
		else
			if(_current.type == Scope.FILESET)
				throw new IllegalStateException("File sets can only be placed underneath other file sets (line " + _nLine + ")!");
			else
				_parent.rs.add(_current.rs);
	}
	
	
	private RuleType convertToType(String sType) {
		RuleType type;
		
		switch(sType) {
		
			case "+":
				type = RuleType.NONE;
				break;
				
			case "-":
				type = RuleType.EXCLUDE;
				break;
	
			case "!":
				type = RuleType.EXISTS;
				break;
	
			case "!!":
				type = RuleType.NOT_EXISTS;
				break;
	
			default:
				throw new IllegalStateException("Unknown type '" + sType + "'!");
		}
		return type;
	}
	
	
	public FileSetBuilder add(String sLine) {
		_nLine++;
		Matcher m = _p.matcher(sLine);
		m.matches();
		int nIntend = m.group(1).length() == 0 ? 0 : m.group(1).length();
		RuleType type = convertToType(m.group(2).length() == 0 ? "+" : m.group(2));
		String sPath = m.group(3);
		
		locateScope(nIntend);
		
		Scope newScope = new Scope(nIntend, type, sPath);
		
		changeScope(newScope);

		return this;
	}
	
	public FileSetBuilder add(InputStream in, boolean bClose) throws IOException {
		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String sLine = bis.readLine();
			while(sLine != null) {
				add(sLine);
				sLine = bis.readLine();
			}
		}
		finally {
			if(bClose && in != null)
				in.close();
		}
		return this;
	}
	
	public FileSetBuilder add(Path path) throws IOException {
		return Files.exists(path) ? add(Files.newInputStream(path), true) : this;
	}

	public FileSetBuilder add(File file) throws IOException {
		return file.exists() ? add(new FileInputStream(file), true) : this;
	}

	

}
