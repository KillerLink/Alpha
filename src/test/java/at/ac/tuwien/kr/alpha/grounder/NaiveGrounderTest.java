/**
 * Copyright (c) 2016-2017, the Alpha Team.
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
package at.ac.tuwien.kr.alpha.grounder;

import at.ac.tuwien.kr.alpha.common.*;
import at.ac.tuwien.kr.alpha.grounder.NaiveGrounder.VariableSubstitution;
import at.ac.tuwien.kr.alpha.grounder.parser.ParsedProgram;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Copyright (c) 2016, the Alpha Team.
 */
public class NaiveGrounderTest {
	@Test
	public void unifyTermsSimpleBinding() throws Exception {
		NaiveGrounder grounder = new NaiveGrounder(new ParsedProgram());
		VariableSubstitution variableSubstitution = new VariableSubstitution();
		Term groundTerm = ConstantTerm.getInstance("abc");
		Term nongroundTerm = VariableTerm.getInstance("Y");
		grounder.unifyTerms(nongroundTerm, groundTerm, variableSubstitution);
		assertEquals("Variable Y must bind to constant term abc", variableSubstitution.substitution.get(VariableTerm.getInstance("Y")), ConstantTerm.getInstance("abc"));
	}

	@Test
	public void unifyTermsFunctionTermBinding() throws Exception {
		NaiveGrounder grounder = new NaiveGrounder(new ParsedProgram());
		VariableSubstitution variableSubstitution = new VariableSubstitution();
		variableSubstitution.substitution.put(VariableTerm.getInstance("Z"), ConstantTerm.getInstance("aa"));
		FunctionTerm groundFunctionTerm = FunctionTerm.getFunctionTerm("f", asList(new Term[]{ConstantTerm.getInstance("bb"), ConstantTerm.getInstance("cc")}));

		Term nongroundFunctionTerm = FunctionTerm.getFunctionTerm("f", asList(ConstantTerm.getInstance("bb"), VariableTerm.getInstance("X")));
		grounder.unifyTerms(nongroundFunctionTerm, groundFunctionTerm, variableSubstitution);
		assertEquals("Variable X must bind to constant term cc", variableSubstitution.substitution.get(VariableTerm.getInstance("X")), ConstantTerm.getInstance("cc"));

		assertEquals("Variable Z must bind to constant term aa", variableSubstitution.substitution.get(VariableTerm.getInstance("Z")), ConstantTerm.getInstance("aa"));
	}
}