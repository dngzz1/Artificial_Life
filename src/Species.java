class Species {
	private static int conceptNeuronMin = 5, conceptNeuronMax = 40;
	private static int memoryNeuronMin = 0, memoryNeuronMax = 20;
	
	private static char[] characterList = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
	
	private String name;
	private int conceptNeuronCount, memoryNeuronCount;
	
	Species(){
		conceptNeuronCount = M.randInt(conceptNeuronMin, conceptNeuronMax);
		memoryNeuronCount = M.randInt(memoryNeuronMin, memoryNeuronMax);
		name = "C"+conceptNeuronCount+"M"+memoryNeuronCount;
	}
	
	private Species(Species parentSpecies) {
		conceptNeuronCount = parentSpecies.conceptNeuronCount;
		memoryNeuronCount = parentSpecies.memoryNeuronCount;
		name = parentSpecies.name;
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
			cell.species.name += "c";
			cell.mutate_conceptNeuron_remove();
		} else {
			cell.species.conceptNeuronCount ++;
			cell.species.name += "C";
			cell.mutate_conceptNeuron_add();
		}
	}
	
	private void mutate_memoryNeuronCount(MatrixCell cell) {
		// We lower the memory neuron count below 0. //
		double chanceToRemove = (cell.species.memoryNeuronCount == 0) ? 0.0 : 0.5;
		if(M.roll(chanceToRemove)) {
			cell.species.memoryNeuronCount --;
			cell.species.name += "m";
			cell.mutate_memoryNeuron_remove();
		} else {
			cell.species.memoryNeuronCount ++;
			cell.species.name += "M";
			cell.mutate_memoryNeuron_add();
		}
	}
	
	public String shortName() {
		int nameLength = name.length();
		if(nameLength <= 15) {
			return name;
		} else {
			return name.substring(0, 5)+"..."+nameLength+"..."+name.substring(nameLength - 5);
		}
	}
}

