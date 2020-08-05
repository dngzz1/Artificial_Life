class Species {
	// These values determine the range for generation zero cells. The caps do not apply to mutations. //
	private static int conceptNeuronMin = 5, conceptNeuronMax = 40;
	private static int memoryNeuronMin = 0, memoryNeuronMax = 20;
	
//	private static char[] characterList = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
	
	private String displayName;
	private String code_generationZero, code_mutationSequence;
	private int conceptNeuronCount, memoryNeuronCount;
	
	Species(){
		conceptNeuronCount = M.randInt(conceptNeuronMin, conceptNeuronMax);
		memoryNeuronCount = M.randInt(memoryNeuronMin, memoryNeuronMax);
		code_generationZero = conceptNeuronCount+"C"+memoryNeuronCount+"M";
		code_mutationSequence = "";
		setDisplayName();
	}
	
	private Species(Species parentSpecies) {
		conceptNeuronCount = parentSpecies.conceptNeuronCount;
		memoryNeuronCount = parentSpecies.memoryNeuronCount;
		code_generationZero = parentSpecies.code_generationZero;
		code_mutationSequence = parentSpecies.code_mutationSequence;
		setDisplayName();
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	void mutate(MatrixCell cell) {
		cell.species = new Species(this);
		int aspectToMutate = M.randInt(0, 1);
		switch (aspectToMutate) {
		case 0:
			// Concept neuron count. //
			mutate_conceptNeuronCount(cell);
			break;
		case 1:
			// Memory neuron count. //
			mutate_memoryNeuronCount(cell);
			break;
		default:
			throw new RuntimeException("Species mutation failed.");
		}
		setDisplayName();
	}
	
	private void mutate_conceptNeuronCount(MatrixCell cell) {
		// We cannot lower the concept neuron count below 1. //
		double chanceToRemove = (cell.species.conceptNeuronCount <= 1) ? 0.0 : 0.5;
		if(M.roll(chanceToRemove)) {
			cell.species.conceptNeuronCount --;
			cell.species.code_mutationSequence += "c";
			cell.mutate_conceptNeuron_remove();
		} else {
			cell.species.conceptNeuronCount ++;
			cell.species.code_mutationSequence += "C";
			cell.mutate_conceptNeuron_add();
		}
	}
	
	private void mutate_memoryNeuronCount(MatrixCell cell) {
		// We cannot lower the memory neuron count below 0. //
		double chanceToRemove = (cell.species.memoryNeuronCount == 0) ? 0.0 : 0.5;
		if(M.roll(chanceToRemove)) {
			cell.species.memoryNeuronCount --;
			cell.species.code_mutationSequence += "m";
			cell.mutate_memoryNeuron_remove();
		} else {
			cell.species.memoryNeuronCount ++;
			cell.species.code_mutationSequence += "M";
			cell.mutate_memoryNeuron_add();
		}
	}
	
	public int neuronCount_concept() {
		return conceptNeuronCount;
	}
	
	public int neuronCount_memory() {
		return memoryNeuronCount;
	}
	
	private void setDisplayName() {
		displayName = code_generationZero;
		int mutationSequenceLength = code_mutationSequence.length();
		displayName += "-"+mutationSequenceLength;
		if(mutationSequenceLength > 0) {
			if(mutationSequenceLength <= 5) {
				displayName += "-"+code_mutationSequence;
			} else {
				displayName += "-.."+code_mutationSequence.substring(mutationSequenceLength - 5);
			}
		}
	}
}

