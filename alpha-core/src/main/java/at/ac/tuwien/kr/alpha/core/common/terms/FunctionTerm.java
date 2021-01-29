package at.ac.tuwien.kr.alpha.core.common.terms;

import static at.ac.tuwien.kr.alpha.core.util.Util.join;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import at.ac.tuwien.kr.alpha.api.grounder.Substitution;
import at.ac.tuwien.kr.alpha.api.terms.Term;
import at.ac.tuwien.kr.alpha.api.terms.VariableTerm;
import at.ac.tuwien.kr.alpha.core.common.Interner;

/**
 * Copyright (c) 2016-2017, the Alpha Team.
 */
public class FunctionTerm extends CoreTerm {
	private static final Interner<FunctionTerm> INTERNER = new Interner<>();

	private final String symbol;
	private final List<Term> terms;
	private final boolean ground;

	private FunctionTerm(String symbol, List<Term> terms) {
		if (symbol == null) {
			throw new IllegalArgumentException();
		}

		this.symbol = symbol;
		this.terms = Collections.unmodifiableList(terms);

		boolean ground = true;
		for (Term term : terms) {
			if (!term.isGround()) {
				ground = false;
				break;
			}
		}
		this.ground = ground;
	}

	public static FunctionTerm getInstance(String functionSymbol, List<Term> termList) {
		return INTERNER.intern(new FunctionTerm(functionSymbol, termList));
	}

	public static FunctionTerm getInstance(String functionSymbol, Term... terms) {
		return getInstance(functionSymbol, Arrays.asList(terms));
	}

	public List<Term> getTerms() {
		return terms;
	}

	public String getSymbol() {
		return symbol;
	}

	@Override
	public boolean isGround() {
		return ground;
	}

	@Override
	public Set<VariableTerm> getOccurringVariables() {
		Set<VariableTerm> vars = new LinkedHashSet<>();
		for (Term term : terms) {
			vars.addAll(term.getOccurringVariables());
		}
		return vars;
	}

	@Override
	public FunctionTerm substitute(Substitution substitution) {
		List<Term> groundTermList = new ArrayList<>(terms.size());
		for (Term term : terms) {
			groundTermList.add(term.substitute(substitution));
		}
		return FunctionTerm.getInstance(symbol, groundTermList);
	}

	@Override
	public String toString() {
		if (terms.isEmpty()) {
			return symbol;
		}

		return join(symbol + "(", terms, ")");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FunctionTerm that = (FunctionTerm) o;

		if (!symbol.equals(that.symbol)) {
			return false;
		}
		return terms.equals(that.terms);
	}

	@Override
	public int hashCode() {
		return 31 * symbol.hashCode() + terms.hashCode();
	}

	@Override
	public int compareTo(Term o) {
		if (this == o) {
			return 0;
		}

		if (!(o instanceof FunctionTerm)) {
			return super.compareTo(o);
		}

		FunctionTerm other = (FunctionTerm) o;

		if (terms.size() != other.terms.size()) {
			return terms.size() - other.terms.size();
		}

		int result = symbol.compareTo(other.symbol);

		if (result != 0) {
			return result;
		}

		for (int i = 0; i < terms.size(); i++) {
			result = terms.get(i).compareTo(other.terms.get(i));
			if (result != 0) {
				return result;
			}
		}

		return 0;
	}

	@Override
	public Term renameVariables(String renamePrefix) {
		List<Term> renamedTerms = new ArrayList<>(terms.size());
		for (Term term : terms) {
			renamedTerms.add(term.renameVariables(renamePrefix));
		}
		return FunctionTerm.getInstance(symbol, renamedTerms);
	}

	@Override
	public Term normalizeVariables(String renamePrefix, Term.RenameCounter counter) {
		List<Term> normalizedTerms = new ArrayList<>(terms.size());
		for (Term term : terms) {
			normalizedTerms.add(term.normalizeVariables(renamePrefix, counter));
		}
		return FunctionTerm.getInstance(symbol, normalizedTerms);
	}
}
