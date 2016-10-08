package at.ac.tuwien.kr.alpha.common;

import java.util.*;

import static java.util.Collections.singletonList;

/**
 * Copyright (c) 2016, the Alpha Team.
 */
public class BasicAnswerSet implements AnswerSet {
	private final Set<Predicate> predicates;
	private final Map<Predicate, Set<PredicateInstance>> predicateInstances;

	public BasicAnswerSet(Set<Predicate> predicates, Map<Predicate, Set<PredicateInstance>> predicateInstances) {
		this.predicates = predicates;
		this.predicateInstances = predicateInstances;
	}

	public BasicAnswerSet(Map<String, Set<List<String>>> stringed) {
		this.predicateInstances = new HashMap<>();
		this.predicates = new HashSet<>();
		for (Map.Entry<String, Set<List<String>>> entry : stringed.entrySet()) {
			final Set<List<String>> instances = entry.getValue();

			int guessedArity = instances.isEmpty() ? 0 : instances.iterator().next().size();

			final Predicate predicate = new BasicPredicate(entry.getKey(), guessedArity);

			this.predicates.add(predicate);

			Set<PredicateInstance> realInstances = new HashSet<>(instances.size());
			for (List<String> stringInstance : instances) {
				final Term[] terms = new Term[stringInstance.size()];
				for (int i = 0; i < stringInstance.size(); i++) {
					terms[i] = ConstantTerm.getInstance(stringInstance.get(i));
				}
				realInstances.add(new PredicateInstance(predicate, terms));
			}
			predicateInstances.put(predicate, realInstances);
		}
	}

	@Override
	public Set<Predicate> getPredicates() {
		return predicates;
	}

	@Override
	public Set<PredicateInstance> getPredicateInstances(Predicate predicate) {
		return predicateInstances.get(predicate);
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder("{ ");
		for (Iterator<Predicate> iterator = predicates.iterator(); iterator.hasNext();) {
			Predicate predicate = iterator.next();
			Set<PredicateInstance> instances = getPredicateInstances(predicate);

			if (instances == null || instances.isEmpty()) {
				sb.append(predicate.getPredicateName());
				continue;
			}

			for (Iterator<PredicateInstance> instanceIterator = instances.iterator(); instanceIterator.hasNext();) {
				sb.append(predicate.getPredicateName());
				sb.append(instanceIterator.next());
				if (instanceIterator.hasNext()) {
					sb.append(", ");
				}
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append(" }");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BasicAnswerSet)) {
			return false;
		}

		BasicAnswerSet that = (BasicAnswerSet) o;

		if (!predicates.equals(that.predicates)) {
			return false;
		}
		return predicateInstances.equals(that.predicateInstances);
	}

	@Override
	public int hashCode() {
		return  31 * predicates.hashCode() + predicateInstances.hashCode();
	}

	public static class Builder {
		private boolean firstInstance = true;
		private String predicateSymbol;
		private Predicate predicate;
		private Set<Predicate> predicates = new HashSet<>();
		private Set<PredicateInstance> instances = new HashSet<>();
		private Map<Predicate, Set<PredicateInstance>> predicateInstances = new HashMap<>();

		private void flush() {
			if (firstInstance) {
				predicate = new BasicPredicate(predicateSymbol, 0);
				predicates.add(predicate);
				predicateInstances.put(predicate, new HashSet<>(singletonList(new PredicateInstance(predicate))));
			} else {
				predicateInstances.put(predicate, new HashSet<>(instances));
			}
			firstInstance = true;
			instances.clear();
			predicate = null;
		}

		public Builder predicate(String predicateSymbol) {
			if (this.predicateSymbol != null) {
				flush();
			}
			this.predicateSymbol = predicateSymbol;
			return this;
		}

		public Builder instance(String... constantSymbols) {
			if (firstInstance) {
				firstInstance = false;
				predicate = new BasicPredicate(predicateSymbol, constantSymbols.length);
				predicates.add(predicate);
			}

			final Term[] terms = new Term[constantSymbols.length];
			for (int i = 0; i < constantSymbols.length; i++) {
				terms[i] = ConstantTerm.getInstance(constantSymbols[i]);
			}
			instances.add(new PredicateInstance(predicate, terms));
			return this;
		}

		public BasicAnswerSet build() {
			flush();
			return new BasicAnswerSet(predicates, predicateInstances);
		}
	}
}