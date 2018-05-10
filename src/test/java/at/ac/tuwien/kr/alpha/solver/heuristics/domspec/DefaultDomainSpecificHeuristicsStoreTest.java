/**
 * Copyright (c) 2018 Siemens AG
 * All rights reserved.
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
package at.ac.tuwien.kr.alpha.solver.heuristics.domspec;

import org.junit.Ignore;

/**
 * Test {@link DefaultDomainSpecificHeuristicsStore}
 * 
 * @deprecated TODO: adapt for heuristic directives!
 */
@Deprecated
@Ignore
public class DefaultDomainSpecificHeuristicsStoreTest {

	private final DefaultDomainSpecificHeuristicsStore store = new DefaultDomainSpecificHeuristicsStore();

//	@Test
//	public void testInsert_1_atom() {
//		store.addInfo(info(1, 1, 1));
//		List<Set<Entry>> orderedList = store.streamEntriesOrderedByDecreasingPriority().collect(Collectors.toList());
//		assertEquals(1, orderedList.size());
//		Iterator<Set<Entry>> iterator = orderedList.iterator();
//		assertEquals(set(1), nextSetOfChoicePoints(iterator));
//	}
//
//	@Test
//	public void testInsert_2_atoms_sameWeight_sameLevel() {
//		store.addInfo(info(1, 2, 3));
//		store.addInfo(info(2, 2, 3));
//		List<Set<Entry>> orderedList = store.streamEntriesOrderedByDecreasingPriority().collect(Collectors.toList());
//		assertEquals(1, orderedList.size());
//		Iterator<Set<Entry>> iterator = orderedList.iterator();
//		assertEquals(set(1, 2), nextSetOfChoicePoints(iterator));
//	}
//
//	@Test
//	public void testInsert_3_atoms_sameWeight_differentLevel() {
//		store.addInfo(info(1, 2, 3));
//		store.addInfo(info(2, 2, 1));
//		store.addInfo(info(3, 2, 2));
//		List<Set<Entry>> orderedList = store.streamEntriesOrderedByDecreasingPriority().collect(Collectors.toList());
//		assertEquals(3, orderedList.size());
//		Iterator<Set<Entry>> iterator = orderedList.iterator();
//		assertEquals(set(1), nextSetOfChoicePoints(iterator));
//		assertEquals(set(3), nextSetOfChoicePoints(iterator));
//		assertEquals(set(2), nextSetOfChoicePoints(iterator));
//	}
//
//	@Test
//	public void testInsert_3_atoms_differentWeight_sameLevel() {
//		store.addInfo(info(1, 4, 1));
//		store.addInfo(info(2, 2, 1));
//		store.addInfo(info(3, 3, 1));
//		List<Set<Entry>> orderedList = store.streamEntriesOrderedByDecreasingPriority().collect(Collectors.toList());
//		assertEquals(3, orderedList.size());
//		Iterator<Set<Entry>> iterator = orderedList.iterator();
//		assertEquals(set(1), nextSetOfChoicePoints(iterator));
//		assertEquals(set(3), nextSetOfChoicePoints(iterator));
//		assertEquals(set(2), nextSetOfChoicePoints(iterator));
//	}
//
//	private DomainSpecificHeuristicValues info(int atom, int weight, int level) {
//		return new DomainSpecificHeuristicValues(atom, weight, level);
//	}
//
//	@SafeVarargs
//	private final <T> Set<T> set(T... elements) {
//		return Arrays.stream(elements).collect(Collectors.toSet());
//	}
//
//	private Set<Integer> nextSetOfChoicePoints(Iterator<Set<Entry>> iterator) {
//		return iterator.next().stream().map(Entry::getChoicePoint).collect(Collectors.toSet());
//	}

}