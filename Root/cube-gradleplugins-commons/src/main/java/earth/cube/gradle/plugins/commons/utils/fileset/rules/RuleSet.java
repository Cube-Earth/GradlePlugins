package earth.cube.gradle.plugins.commons.utils.fileset.rules;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import earth.cube.gradle.plugins.commons.utils.Encoder;

public class RuleSet {
	
	private RuleType _type;
	private String _sPath;
	private Pattern _pattern;
	private List<RuleSet> _rules = new ArrayList<>();

	
	public RuleSet() {
		_type = RuleType.NONE;
	}
	
	public RuleSet(RuleType type, String sPath) {
		_type = type;
		_sPath = sPath;
		if(_type == RuleType.EXCLUDE)
			_pattern = Encoder.globToRegExPattern(sPath);
		else
			if(sPath.contains("*"))
				throw new IllegalArgumentException("Wildcard not allowed in '" + sPath + "'!");
	}
	
	public void add(RuleSet rule) {
		_rules.add(rule);
	}
	
	
	protected RuleSetOutcome merge(RuleSetOutcome main, RuleSetOutcome sub) {
		RuleSetOutcome r = new RuleSetOutcome();
		r.setMatched(main.getMatched() && sub.getMatched());
		r.setStop(main.shouldStop());
		r.setNegate(main.getNegate() ^ sub.getNegate());
		r.setSkipDir(main.shouldSkipDir() || sub.shouldSkipDir());
		return r;
	}
	
	public RuleSetOutcome evaluate(Path currDir, String sPath) {
		RuleSetOutcome outcome = new RuleSetOutcome();
		switch(_type) {
			case NONE:
				outcome.setMatched(true);
				break;
		
			case EXISTS:
				if(currDir != null) {
					outcome.setMatched(Files.exists(currDir.resolve(_sPath)));
					outcome.setStop(!outcome.getMatched());
					outcome.setSkipDir(!outcome.getMatched());
				}
				break;
				
			case NOT_EXISTS:
				if(currDir != null) {
					outcome.setMatched(!Files.exists(currDir.resolve(_sPath)));
					outcome.setStop(!outcome.getMatched());
					outcome.setSkipDir(!outcome.getMatched());
				}
				break;
				
			case EXCLUDE:
				outcome.setMatched(_pattern.matcher(sPath).matches());
				outcome.setNegate(true);
				outcome.setStop(outcome.getMatched());
				break;
				
			default:
				throw new UnsupportedOperationException("Unsupoorted type: " + _type);
		}
		
		if(outcome.getMatched()) {
			RuleSetOutcome subOutcome = new RuleSetOutcome();
			for(RuleSet rule : _rules) {
				subOutcome = rule.evaluate(currDir, sPath);
				if(subOutcome.shouldStop()) {
					outcome = merge(outcome, subOutcome);
					break;
				}
			}
		}
		
		return outcome;
	}

	
	public boolean evaluateIncluded(Path currDir, String sPath) {
		RuleSetOutcome outcome = evaluate(currDir, sPath);
		return outcome.getMatched() ^ outcome.getNegate();
	}

	public boolean isEmpty() {
		return _rules.size() == 0;
	}

}
