class Species {
	private static int conceptNeuronMin = 5, conceptNeuronMax = 40;
	private static int memoryNeuronMin = 0, memoryNeuronMax = 20;
	
	String name;
	private int conceptNeuronCount, memoryNeuronCount;
	
	Species(){
		name = "S"+M.randInt(10)+""+M.randInt(10);
		conceptNeuronCount = M.randInt(conceptNeuronMin, conceptNeuronMax);
		memoryNeuronCount = M.randInt(memoryNeuronMin, memoryNeuronMax);
	}
	
	private Species(Species parentSpecies) {
		name = parentSpecies.name+"-"+M.randInt(10)+""+M.randInt(10);
		conceptNeuronCount = parentSpecies.conceptNeuronCount;
		memoryNeuronCount = parentSpecies.memoryNeuronCount;
	}
	
	int conceptNeuronCount() {
		return conceptNeuronCount;
	}
	
	int memoryNeuronCount() {
		return memoryNeuronCount;
	}
	
	void mutate(MatrixCell cell) {
		cell.species = new Species(this);
		int aspectToMutate = M.randInt(0, 1);
		switch (aspectToMutate) {
		case 0:
			// Memory neuron count. //
			mutate_memoryNeuronCount(cell);
			break;
		case 1:
			// Concept neuron count. //
			mutate_conceptNeuronCount(cell);
			break;
		default:
			throw new RuntimeException("Species mutation failed.");
		}
	}
	
	private void mutate_conceptNeuronCount(MatrixCell cell) {
		// We lower the concept neuron count below 1. //
		double chanceToRemove = (cell.species.conceptNeuronCount <= 1) ? 0.0 : 0.5;
		if(M.roll(chanceToRemove)) {
			cell.species.conceptNeuronCount --;
			cell.mutate_conceptNeuron_remove();
		} else {
			cell.species.conceptNeuronCount ++;
			cell.mutate_conceptNeuron_add();
		}
	}
	
	private void mutate_memoryNeuronCount(MatrixCell cell) {
		// We lower the memory neuron count below 0. //
		double chanceToRemove = (cell.species.memoryNeuronCount == 0) ? 0.0 : 0.5;
		if(M.roll(chanceToRemove)) {
			cell.species.memoryNeuronCount --;
			cell.mutate_memoryNeuron_remove();
		} else {
			cell.species.memoryNeuronCount ++;
			cell.mutate_memoryNeuron_add();
		}
	}
}

