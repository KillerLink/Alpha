package at.ac.tuwien.kr.alpha.common.program.impl;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.kr.alpha.common.atoms.Atom;
import at.ac.tuwien.kr.alpha.common.program.AbstractProgram;
import at.ac.tuwien.kr.alpha.common.rule.impl.BasicRule;
import at.ac.tuwien.kr.alpha.common.rule.impl.NormalRule;
import at.ac.tuwien.kr.alpha.grounder.parser.InlineDirectives;

public class NormalProgram extends AbstractProgram<NormalRule> {

	public NormalProgram(List<NormalRule> rules, List<Atom> facts, InlineDirectives inlineDirectives) {
		super(rules, facts, inlineDirectives);
	}

	public static NormalProgram fromInputProgram(InputProgram inputProgram) {
		List<NormalRule> normalRules = new ArrayList<>();
		for (BasicRule r : inputProgram.getRules()) {
			normalRules.add(NormalRule.fromBasicRule(r));
		}
		return new NormalProgram(normalRules, inputProgram.getFacts(), inputProgram.getInlineDirectives());
	}

}
