package at.ac.tuwien.kr.alpha.grounder.transformation.impl;

import at.ac.tuwien.kr.alpha.common.program.impl.InputProgram;
import at.ac.tuwien.kr.alpha.common.program.impl.NormalProgram;
import at.ac.tuwien.kr.alpha.grounder.atoms.EnumerationAtom;
import at.ac.tuwien.kr.alpha.grounder.transformation.ProgramTransformation;

/**
 * Encapsulates all transformations necessary to transform a given program into a @{link NormalProgram} that is understood by Alpha internally
 * 
 * Copyright (c) 2019, the Alpha Team.
 */
public class NormalizeProgramTransformation extends ProgramTransformation<InputProgram, NormalProgram> {

	private boolean useNormalizationGrid;

	public NormalizeProgramTransformation(boolean useNormalizationGrid) {
		this.useNormalizationGrid = useNormalizationGrid;
	}

	@Override
	public NormalProgram apply(InputProgram inputProgram) {
		InputProgram tmpPrg;
		// Transform choice rules.
		tmpPrg = new ChoiceHeadToNormal().apply(inputProgram);
		// Transform cardinality aggregates.
		tmpPrg = new CardinalityNormalization(!this.useNormalizationGrid).apply(tmpPrg);
		// Transform sum aggregates.
		tmpPrg = new SumNormalization().apply(tmpPrg);
		// Transform enumeration atoms.
		tmpPrg = new EnumerationRewriting().apply(tmpPrg);
		EnumerationAtom.resetEnumerations();

		// construct the normal program
		NormalProgram retVal = NormalProgram.fromInputProgram(tmpPrg);
		// Transform intervals - CAUTION - this MUST come before VariableEqualityRemoval!
		retVal = new IntervalTermToIntervalAtom().apply(retVal);
		// Remove variable equalities.
		retVal = new VariableEqualityRemoval().apply(retVal);
		return retVal;
	}

}
