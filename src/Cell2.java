import java.awt.Color;
import java.awt.Point;

class Cell2 extends WorldObject implements Stepable {
	static int energyCostPerTick = 20;
	static double mutationProbability = 0.05;
	
	// Cell Metadata //
	int generation = 0;
	int children = 0;
	
	// Cell Data //
	Color color;
	int energyPassedToChild;
	
	// Cell Variables //
	Direction facing = M.chooseRandom(Direction.values());
	int energy = Cell.energyUponBirth;
	int lifetime = 0;
	
	// Neurons //
	double[] sensoryNeurons = new double[6];
	double[] memoryNeurons = new double[5];
	double[] conceptNeurons = new double[20];
	double[] motorNeurons = new double[7];
	
	// Connection layer 1 //
	double[][] sensoryConceptConnections = new double[conceptNeurons.length][sensoryNeurons.length];
	double[][] memoryConceptConnections = new double[conceptNeurons.length][memoryNeurons.length];
	double[] conceptBias = new double[conceptNeurons.length];
	
	// Connection layer 2 //
	double[][] conceptMotorConnections = new double[motorNeurons.length][conceptNeurons.length];
	double[] motorBias = new double[motorNeurons.length];
	double[][] conceptMemoryConnections = new double[memoryNeurons.length][conceptNeurons.length];
	double[] memoryBias = new double[memoryNeurons.length];
	
	Cell2(){
		color = Color.GREEN;
		energyPassedToChild = Cell.energyUponBirth;

		boolean initialiseRandomly = true;
		boolean initialiseWithMutations = false;
		boolean initialiseWithReflexes = true;
		
		if(initialiseRandomly) {
			double min = -1, max = 1;
			M.setRandomEntries(sensoryConceptConnections, min, max);
			M.setRandomEntries(memoryConceptConnections, min, max);
			M.setRandomEntries(conceptBias, min, max);
			M.setRandomEntries(conceptMotorConnections, min, max);
			M.setRandomEntries(motorBias, min, max);
			M.setRandomEntries(conceptMemoryConnections, min, max);
			M.setRandomEntries(memoryBias, min, max);
		}
		
		if(initialiseWithMutations) {
			for(int i = 0; i < 10; i ++)
				mutate();
		}
		
		if(initialiseWithReflexes) {
			// Default movement reflex. //
			motorBias[0] = 0.8;
			
			// Default eat reflex. //
			sensoryConceptConnections[0][0] = 1.0;
			conceptMotorConnections[0][0] = 1.0;
			motorBias[3] = 0.8001;
			
			// Default reproduction reflex. //
			
//			defaultHungerOrgan.sensoryNeuron_hunger.addConnection(defaultReproductionOrgan.motorNeuron_spawn);
//			defaultReproductionOrgan.motorNeuron_spawn.isThresholdUpperLimit = false;
//			defaultReproductionOrgan.motorNeuron_spawn.threshold = 0.9f;
		}
	}
	
	Cell2(Cell2 cell){
		color = cell.color;
		energyPassedToChild = cell.energyPassedToChild;
		sensoryConceptConnections = M.cloneMatrix(cell.sensoryConceptConnections);
		memoryConceptConnections = M.cloneMatrix(cell.memoryConceptConnections);
		conceptBias = M.cloneVector(cell.conceptBias);
		conceptMotorConnections = M.cloneMatrix(cell.conceptMotorConnections);
		motorBias = M.cloneVector(cell.motorBias);
		conceptMemoryConnections = M.cloneMatrix(cell.conceptMemoryConnections);
		memoryBias = M.cloneVector(cell.memoryBias);
	}
	
	@Override
	protected Cell2 clone() {
		return new Cell2(this);
	}
	
	private boolean displace() {
		Point targetPoint = getAdjacentLocation(facing);
		Display.wrapPoint(targetPoint);
		WorldObject target = Display.grid[targetPoint.x][targetPoint.y];
		if(target != null){
			return target.interact(this, Interaction.DISPLACE);
		} else {
			return false;
		}
	}
	
	private boolean eat() {
		Point targetPoint = getAdjacentLocation(facing);
		Display.wrapPoint(targetPoint);
		WorldObject target = Display.grid[targetPoint.x][targetPoint.y];
		if(target != null){
			return target.interact(this, Interaction.EAT);
		} else {
			return false;
		}
	}
	
	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType) {
		switch (interactionType) {
		case PUSH:
			push(interacter, this);
			return true;
		case GIVE_FOOD_ENERGY:
			int foodValue = Food.energyGainPerFood;
			energy = Math.min(energy + foodValue, Cell.maxStoredEnergy);
//			lifetimeFoodEaten += foodValue;
			return true;
		case KILL:
			kill();
			return true;
		case PULL:
			return pull(interacter, this);
		case DISPLACE:
			return displace(interacter, this);
		default:
			return false;
		}
	}
	
	void kill(){
		Display.stepList.remove(this);
		Display.grid[location.x][location.y] = null;
	}
	
	private void move() {
		moveTo(M.add(facing.getVector(), getLocation()));
	}
	
	private boolean moveTo(Point p){
		Display.wrapPoint(p);
		if(Display.grid[p.x][p.y] == null){
			setLocation(p);
			return true;
		} else {
			return Display.grid[p.x][p.y].interact(this, Interaction.PUSH);
		}
	}
	
	private void mutate() {
		color = mutate(color, mutationProbability);
		energyPassedToChild = mutate(energyPassedToChild, mutationProbability);
		mutate(sensoryConceptConnections, mutationProbability);
		mutate(memoryConceptConnections, mutationProbability);
		mutate(conceptBias, mutationProbability);
		mutate(conceptMotorConnections, mutationProbability);
		mutate(motorBias, mutationProbability);
		mutate(conceptMemoryConnections, mutationProbability);
		mutate(memoryBias, mutationProbability);
		
	}
	
	private Color mutate(Color color, double probability) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		r = mutate(r, probability);
		g = mutate(g, probability);
		b = mutate(b, probability);
		r = M.constrainValue(0, r, 255);
		g = M.constrainValue(0, g, 255);
		b = M.constrainValue(0, b, 255);
		return new Color(r, g, b);
	}
	
	private int mutate(int value, double probability) {
		if(M.roll(probability)) {
			double multiplier;
			if(M.roll(0.5)) {
				// 50% chance to increase between x1 to x2. //
				multiplier = M.rand(1, 2);
			} else {
				// 50% chance to decrease between x0.5 to x1. //
				multiplier = M.rand(0.5, 1);
			}
			return (int)(value*multiplier);
		} else {
			return value;
		}
	}
	
	private double mutate(double value) {
//		return M.rand(-1, 1);
		return M.rand(1)*M.rand(1)*M.rand(1) - M.rand(1)*M.rand(1)*M.rand(1);
	}
	
	private double mutate(double value, double probability) {
		return M.roll(probability) ? mutate(value) : value;
	}
	
	private void mutate(double[] vector, double probability) {
		for(int i = 0; i < vector.length; i ++) {
			vector[i] = mutate(vector[i], probability);
		}
	}
	
	private void mutate(double[][] matrix, double probability) {
		for(int i = 0; i < matrix.length; i ++) {
			for(int j = 0; j < matrix[i].length; j ++) {
				matrix[i][j] = mutate(matrix[i][j], probability);
			}
		}
	}
	
	private boolean pull() {
		Point targetPoint = getAdjacentLocation(facing);
		WorldObject target = Display.grid[targetPoint.x][targetPoint.y];
		if(target != null){
			return target.interact(this, Interaction.PULL);
		} else {
			return false;
		}
	}
	
	private void rotateACW() {
		facing = facing.rotateACW();
	}
	
	private void rotateCW() {
		facing = facing.rotateCW();
	}
	
	private void spawn(Direction direction, int energyPassedToChild){
		int energyCost = energyPassedToChild;
		if(energy > energyCost){
			Point p = M.add(direction.getVector(), location);
			Cell2 child = clone();
			child.generation = generation + 1;
			child.mutate();
			boolean placedSuccessfully = Display.place(child, p);
			if(placedSuccessfully){
				energy -= energyCost;
				child.energy = energyPassedToChild;
			}
		}
	}

	@Override
	public void step() {
		double sensoryInput;
		
		// Do a vision ray cast. //
		WorldObject seenObject = null;
		Point p = new Point(getLocation());
		Point d = facing.getVector();
		int rayCastLength = 10;
		int distance;
		for(distance = 0; distance < rayCastLength; distance ++){
			p.x += d.x;
			p.y += d.y;
			Display.wrapPoint(p);
			seenObject = Display.grid[p.x][p.y];
			if(seenObject != null){
				break;
			}
		}
		
		// Set sensory neuron 0 to represent the distance to the seen object. //
		sensoryInput = (float)distance / (float)rayCastLength;
		sensoryNeurons[0] = sensoryInput;
		
		// Set sensory neurons 1-3 to represent the colour of the seen object. //
		Color seenColor = seenObject != null ? seenObject.getColor() : null;
		seenColor = (seenColor == null) ? Color.BLACK : seenColor;
		sensoryInput = (float)seenColor.getRed() / 255.0f;
		sensoryNeurons[1] = sensoryInput;
		sensoryInput = (float)seenColor.getGreen() / 255.0f;
		sensoryNeurons[2] = sensoryInput;
		sensoryInput = (float)seenColor.getBlue() / 255.0f;
		sensoryNeurons[3] = sensoryInput;
		
		// Set sensory neuron 4 to represent the age of the cell. //
		float age = (float)(lifetime/1000.0f) + 1;
		sensoryInput = 1.0f / age;
		sensoryNeurons[4] = sensoryInput;
		
		// Set sensory neuron 5 to represent the energy reserves of the cell. //
		float hunger = Math.max(1.0f, (float)(energy) / (float)(Food.energyGainPerFood));
		sensoryInput = 1.0f / hunger;
		sensoryNeurons[5] = sensoryInput;
		
		
		// Evaluate neural connections. //
		for(int i = 0; i < conceptNeurons.length; i ++) {
			// Set neuron to the bias value. //
			conceptNeurons[i] = conceptBias[i];
			
			// Add the sensory connections. //
			for(int j = 0; j < sensoryNeurons.length; j ++) {
				conceptNeurons[i] += sensoryConceptConnections[i][j]*sensoryNeurons[j];
			}
			
			// Add the memory connections. //
			for(int j = 0; j < memoryNeurons.length; j ++) {
				conceptNeurons[i] += memoryConceptConnections[i][j]*memoryNeurons[j];
			}
			
			// Normalise so that the value is between 0 and 1. //
			conceptNeurons[i] /= 1 + sensoryNeurons.length + memoryNeurons.length;
		}
		for(int i = 0; i < motorNeurons.length; i ++) {
			// Set neuron to the bias value. //
			motorNeurons[i] = motorBias[i];
			
			// Add the concept connections. //
			for(int j = 0; j < conceptNeurons.length; j ++) {
				motorNeurons[i] += conceptMotorConnections[i][j]*conceptNeurons[j];
			}
			
			// Normalise so that the value is between 0 and 1. //
			motorNeurons[i] /= 1 + conceptNeurons.length;
		}
		for(int i = 0; i < memoryNeurons.length; i ++) {
			// Set neuron to the bias value. //
			memoryNeurons[i] = memoryBias[i];
			
			// Add the concept connections. //
			for(int j = 0; j < conceptNeurons.length; j ++) {
				memoryNeurons[i] += conceptMemoryConnections[i][j]*conceptNeurons[j];
			}
			
			// Normalise so that the value is between 0 and 1. //
			memoryNeurons[i] /= 1 + conceptNeurons.length;
		}
		
		// Act on motor neurons. //
		int motorToFire = 0;
		for(int i = 0; i < motorNeurons.length; i ++) {
			if(motorNeurons[i] > motorNeurons[motorToFire]) {
				motorToFire = i;
			}
		}
		switch (motorToFire) {
		case 0:
			move();
			break;
		case 1:
			rotateACW();
			break;
		case 2:
			rotateCW();
			break;
		case 3:
			eat();
			break;
		case 4:
			pull();
			break;
		case 5:
			displace();
			break;
		case 6:
			spawn(facing, energyPassedToChild);
			break;
		default:
			break;
		}
		
		// Life and energy. //
		lifetime ++;
		energy -= energyCostPerTick;
		if(energy < 0){
			kill();
		}
	}
}