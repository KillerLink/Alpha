/**
 * Copyright (c) 2017-2018, the Alpha Team.
 * All rights reserved.
 *
 * Additional changes made by Siemens.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package at.ac.tuwien.kr.alpha.core.programs.transformation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.kr.alpha.api.program.Atom;
import at.ac.tuwien.kr.alpha.api.program.Literal;
import at.ac.tuwien.kr.alpha.api.rules.NormalHead;
import at.ac.tuwien.kr.alpha.api.rules.Rule;
import at.ac.tuwien.kr.alpha.api.terms.Term;
import at.ac.tuwien.kr.alpha.api.terms.VariableTerm;
import at.ac.tuwien.kr.alpha.commons.FunctionTermImpl;
import at.ac.tuwien.kr.alpha.commons.IntervalTerm;
import at.ac.tuwien.kr.alpha.commons.VariableTermImpl;
import at.ac.tuwien.kr.alpha.core.atoms.IntervalAtom;
import at.ac.tuwien.kr.alpha.core.programs.NormalProgram;
import at.ac.tuwien.kr.alpha.core.rules.NormalRule;
import at.ac.tuwien.kr.alpha.core.rules.heads.NormalHeadImpl;

/**
 * Rewrites all interval terms in a rule into a new variable and an IntervalAtom.
 *
 * Copyright (c) 2017-2019, the Alpha Team.
 */
public class IntervalTermToIntervalAtom extends ProgramTransformation<NormalProgram, NormalProgram> {
	private static final String INTERVAL_VARIABLE_PREFIX = "_Interval";

	/**
	 * Rewrites intervals into a new variable and special IntervalAtom.
	 * 
	 * @return true if some interval occurs in the rule.
	 */
	private static Rule<NormalHead> rewriteIntervalSpecifications(Rule<NormalHead> rule) {
		// Collect all intervals and replace them with variables.
		Map<VariableTerm, IntervalTerm> intervalReplacements = new LinkedHashMap<>();

		List<Literal> rewrittenBody = new ArrayList<>();

		for (Literal literal : rule.getBody()) {
			rewrittenBody.add(rewriteLiteral(literal, intervalReplacements));
		}
		NormalHead rewrittenHead = rule.isConstraint() ? null :
			new NormalHeadImpl(rewriteLiteral(rule.getHeadAtom().toLiteral(), intervalReplacements).getAtom());

		// If intervalReplacements is empty, no IntervalTerms have been found, keep rule as is.
		if (intervalReplacements.isEmpty()) {
			return rule;
		}

		// Add new IntervalAtoms representing the interval specifications.
		for (Map.Entry<VariableTerm, IntervalTerm> interval : intervalReplacements.entrySet()) {
			rewrittenBody.add(new IntervalAtom(interval.getValue(), interval.getKey()).toLiteral());
		}
		return new NormalRule(rewrittenHead, rewrittenBody);
	}

	/**
	 * Replaces every IntervalTerm by a new variable and returns a mapping of the replaced VariableTerm -> IntervalTerm.
	 */
	private static Literal rewriteLiteral(Literal lit, Map<VariableTerm, IntervalTerm> intervalReplacement) {
		Atom atom = lit.getAtom();
		List<Term> termList = new ArrayList<>(atom.getTerms());
		boolean didChange = false;
		for (int i = 0; i < termList.size(); i++) {
			Term term = termList.get(i);
			if (term instanceof IntervalTerm) {
				VariableTermImpl replacementVariable = VariableTermImpl.getInstance(INTERVAL_VARIABLE_PREFIX + intervalReplacement.size());
				intervalReplacement.put(replacementVariable, (IntervalTerm) term);
				termList.set(i, replacementVariable);
				didChange = true;
			}
			if (term instanceof FunctionTermImpl) {
				// Rewrite function terms recursively.
				FunctionTermImpl rewrittenFunctionTerm = rewriteFunctionTerm((FunctionTermImpl) term, intervalReplacement);
				termList.set(i, rewrittenFunctionTerm);
				didChange = true;
			}
		}
		if (didChange) {
			Atom rewrittenAtom = atom.withTerms(termList);
			return lit.isNegated() ? rewrittenAtom.toLiteral().negate() : rewrittenAtom.toLiteral();
		}
		return lit;
	}

	private static FunctionTermImpl rewriteFunctionTerm(FunctionTermImpl functionTerm, Map<VariableTerm, IntervalTerm> intervalReplacement) {
		List<Term> termList = new ArrayList<>(functionTerm.getTerms());
		boolean didChange = false;
		for (int i = 0; i < termList.size(); i++) {
			Term term = termList.get(i);
			if (term instanceof IntervalTerm) {
				VariableTermImpl replacementVariable = VariableTermImpl.getInstance("_Interval" + intervalReplacement.size());
				intervalReplacement.put(replacementVariable, (IntervalTerm) term);
				termList.set(i, replacementVariable);
				didChange = true;
			}
			if (term instanceof FunctionTermImpl) {
				// Recursively rewrite function terms.
				FunctionTermImpl rewrittenFunctionTerm = rewriteFunctionTerm((FunctionTermImpl) term, intervalReplacement);
				if (rewrittenFunctionTerm != term) {
					termList.set(i, rewrittenFunctionTerm);
					didChange = true;
				}
			}
		}
		if (didChange) {
			return FunctionTermImpl.getInstance(functionTerm.getSymbol(), termList);
		}
		return functionTerm;
	}

	@Override
	public NormalProgram apply(NormalProgram inputProgram) {
		boolean didChange = false;
		List<Rule<NormalHead>> rewrittenRules = new ArrayList<>();
		for (Rule<NormalHead> rule : inputProgram.getRules()) {
			Rule<NormalHead> rewrittenRule = rewriteIntervalSpecifications(rule);
			rewrittenRules.add(rewrittenRule);

			// If no rewriting occurred, the output rule is the same as the input to the rewriting.
			if (rewrittenRule != rule) {
				didChange = true;
			}
		}
		// Return original program if no rule was actually rewritten.
		if (!didChange) {
			return inputProgram;
		}
		return new NormalProgram(rewrittenRules, inputProgram.getFacts(), inputProgram.getInlineDirectives());
	}
}