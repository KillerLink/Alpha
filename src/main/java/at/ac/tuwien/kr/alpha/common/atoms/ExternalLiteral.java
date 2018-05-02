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
package at.ac.tuwien.kr.alpha.common.atoms;

import at.ac.tuwien.kr.alpha.common.terms.ConstantTerm;
import at.ac.tuwien.kr.alpha.common.terms.Term;
import at.ac.tuwien.kr.alpha.common.terms.VariableTerm;
import at.ac.tuwien.kr.alpha.grounder.Substitution;

import java.util.*;

import static java.util.Collections.emptyList;

/**
 * Contains a potentially negated {@link ExternalAtom}.
 */
public class ExternalLiteral extends FixedInterpretationLiteral {

	public ExternalLiteral(ExternalAtom atom, boolean negated) {
		super(atom, negated);
	}
	
	@Override
	public ExternalAtom getAtom() {
		return (ExternalAtom)atom;
	}

	/**
	 * Returns a new copy of this literal whose {@link Literal#isNegated()} status is inverted
	 */
	@Override
	public ExternalLiteral negate() {
		return new ExternalLiteral(getAtom(), !negated);
	}

	/**
	 * @see Atom#substitute(Substitution)
	 */
	@Override
	public ExternalLiteral substitute(Substitution substitution) {
		return new ExternalLiteral(getAtom().substitute(substitution), negated);
	}

	@Override
	public Set<VariableTerm> getBindingVariables() {
		// If the external atom is negative, then all variables of input and output are non-binding
		// and there are no binding variables (like for ordinary atoms).
		// If the external atom is positive, then variables of output are binding.

		if (negated) {
			return Collections.emptySet();
		}
		
		List<Term> output = getAtom().getOutput();

		Set<VariableTerm> binding = new HashSet<>(output.size());

		for (Term out : output) {
			if (out instanceof VariableTerm) {
				binding.add((VariableTerm) out);
			}
		}

		return binding;
	}

	@Override
	public Set<VariableTerm> getNonBindingVariables() {
		List<Term> input = getAtom().getInput();
		List<Term> output = getAtom().getOutput();
		
		// External atoms have their input always non-binding, since they cannot
		// be queried without some concrete input.
		Set<VariableTerm> nonbindingVariables = new HashSet<>();
		for (Term term : input) {
			nonbindingVariables.addAll(term.getOccurringVariables());
		}

		// If the external atom is negative, then all variables of input and output are non-binding.
		if (negated) {
			for (Term out : output) {
				if (out instanceof VariableTerm) {
					nonbindingVariables.add((VariableTerm) out);
				}
			}
		}

		return nonbindingVariables;
	}

	@Override
	public List<Substitution> getSubstitutions(Substitution partialSubstitution) {
		List<Term> input = getAtom().getInput();
		List<Term> output = getAtom().getOutput();
		List<Substitution> substitutions = new ArrayList<>();
		List<Term> substitutes = new ArrayList<>(input.size());

		for (Term t : input) {
			substitutes.add(t.substitute(partialSubstitution));
		}

		Set<List<ConstantTerm<?>>> results = getAtom().getInterpretation().evaluate(substitutes);

		if (results == null) {
			throw new NullPointerException("Predicate " + getPredicate().getName() + " returned null. It must return a Set.");
		}

		if (results.isEmpty()) {
			return emptyList();
		}

		for (List<ConstantTerm<?>> bindings : results) {
			if (bindings.size() < output.size()) {
				throw new RuntimeException("Predicate " + getPredicate().getName() + " returned " + bindings.size() + " terms when at least " + output.size() + " were expected.");
			}

			Substitution ith = new Substitution(partialSubstitution);
			boolean skip = false;
			for (int i = 0; i < output.size(); i++) {
				Term out = output.get(i);

				if (out instanceof VariableTerm) {
					ith.put((VariableTerm) out, bindings.get(i));
				} else {
					if (!bindings.get(i).equals(out)) {
						skip = true;
						break;
					}
				}
			}

			if (!skip) {
				substitutions.add(ith);
			}
		}

		return substitutions;
	}

}