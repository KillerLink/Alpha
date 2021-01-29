package at.ac.tuwien.kr.alpha.core.grounder;

import static at.ac.tuwien.kr.alpha.core.util.Util.join;

import java.util.Arrays;
import java.util.List;

import at.ac.tuwien.kr.alpha.api.program.Atom;
import at.ac.tuwien.kr.alpha.api.terms.Term;
import at.ac.tuwien.kr.alpha.core.util.Util;

/**
 * An instance is a positional association of terms, e.g., representing a variable substitution, or a ground instance of
 * a predicate.
 * Copyright (c) 2016, the Alpha Team.
 */
public class Instance {
	public final List<Term> terms;

	public Instance(Term... terms) {
		this(Arrays.asList(terms));
	}

	public Instance(List<Term> terms) {
		this.terms = terms;
	}

	public static Instance fromAtom(Atom atom) {
		if (!atom.isGround()) {
			throw Util.oops("Cannot create instance from non-ground atom " + atom.toString());
		}
		return new Instance(atom.getTerms());
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		return terms.equals(((Instance) o).terms);
	}

	@Override
	public int hashCode() {
		return terms.hashCode();
	}

	@Override
	public String toString() {
		return join("(", terms, ")");
	}
}
