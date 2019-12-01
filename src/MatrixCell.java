import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.PrintWriter;
import java.util.LinkedList;

import files.TextFileHandler;

class MatrixCell extends Cell {
	static int rayCastLength = 10;
	static Color[] validCellColors = validCellColors();
	static int energyCostPerTick = 20;
	static double mutationProbability = 0.05;
	static double mutationProbability_col = 0.9;
	static int mutationRate_col = 2;
	
	// Cell Data //
	int col;
	int energyPassedToChild;
	
	// Cell Variables //
	Direction facing = M.chooseRandom(Direction.values());
	boolean isDead = false;
	MatrixCell mate = null;
	
	// Neurons //
	double[] sensoryNeurons = new double[14];
	double[] memoryNeurons = new double[5];
	double[] conceptNeurons = new double[30];
	double[] motorNeurons = new double[8];
	
	// Connection layer 1 //
	double[][] sensoryConceptConnections = new double[conceptNeurons.length][sensoryNeurons.length];
	double[][] memoryConceptConnections = new double[conceptNeurons.length][memoryNeurons.length];
	double[] conceptBias = new double[conceptNeurons.length];
	
	// Connection layer 2 //
	double[][] conceptMotorConnections = new double[motorNeurons.length][conceptNeurons.length];
	double[] motorBias = new double[motorNeurons.length];
	double[][] conceptMemoryConnections = new double[memoryNeurons.length][conceptNeurons.length];
	double[] memoryBias = new double[memoryNeurons.length];
	
	private static MatrixCell createChild(MatrixCell parent) {
		MatrixCell child = new MatrixCell(parent);
		child.generation = parent.generation + 1;
		child.mutate();
		return child;
	}
	
	private static MatrixCell createChild(MatrixCell parent1, MatrixCell parent2) {
		MatrixCell child = new MatrixCell(parent1, parent2);
		child.generation = Math.max(parent1.generation, parent2.generation) + 1;
		child.mutate();
		return child;
	}
	
	private static double[][] loadMatrix(LinkedList<String> dataLineList, int rows) {
		double[][] matrix = new double[rows][];
		for(int row = 0; row < matrix.length; row ++) {
			matrix[row] = loadVector(dataLineList.remove());
		}
		return matrix;
	}
	
	private static double[] loadVector(String dataString) {
		String[] data = dataString.split(";");
		double[] vector = new double[data.length];
		for(int i = 0; i < vector.length; i ++) {
			vector[i] = Double.parseDouble(data[i]);
		}
		return vector;
	}
	
	private static int mutate(int value, double probability) {
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
	
	private static double mutate(double value) {
//		return M.rand(-1, 1);
		return M.rand(1)*M.rand(1)*M.rand(1) - M.rand(1)*M.rand(1)*M.rand(1);
	}
	
	private static double mutate(double value, double probability) {
		return M.roll(probability) ? mutate(value) : value;
	}
	
	private static void mutate(double[] vector, double probability) {
		for(int i = 0; i < vector.length; i ++) {
			vector[i] = mutate(vector[i], probability);
		}
	}
	
	private static void mutate(double[][] matrix, double probability) {
		for(int i = 0; i < matrix.length; i ++) {
			for(int j = 0; j < matrix[i].length; j ++) {
				matrix[i][j] = mutate(matrix[i][j], probability);
			}
		}
	}
	
	private static int mutateCol(int col) {
		if(M.roll(mutationProbability_col)) {
			int delta = M.randInt(mutationRate_col) - M.randInt(mutationRate_col);
			col = (col + validCellColors.length + delta)%validCellColors.length;
		}
		return col;
	}
	
	private static void printMatrix(PrintWriter pw, double[][] matrix) {
		for(int row = 0; row < matrix.length; row ++) {
			printVector(pw, matrix[row]);
		}
	}
	
	private static void printVector(PrintWriter pw, double[] vector) {
		for(int i = 0; i < vector.length; i ++) {
			pw.print(vector[i]+";");
		}
		pw.println();
	}
	
	private static final Color[] validCellColors() {
		if(validCellColors == null) {
			int n = 8;
			validCellColors = new Color[6*n];
			for(int i = 0; i < n; i ++) {
				int g = i*255/n;
				validCellColors[i] = new Color(255, g, 0);
			}
			for(int i = 0; i < n; i ++) {
				int r = 255 - i*255/n;
				validCellColors[n + i] = new Color(r, 255, 0);
			}
			for(int i = 0; i < n; i ++) {
				int b = i*255/n;
				validCellColors[2*n + i] = new Color(0, 255, b);
			}
			for(int i = 0; i < n; i ++) {
				int g = 255 - i*255/n;
				validCellColors[3*n + i] = new Color(0, g, 255);
			}
			for(int i = 0; i < n; i ++) {
				int r = i*255/n;
				validCellColors[4*n + i] = new Color(r, 0, 255);
			}
			for(int i = 0; i < n; i ++) {
				int b = 255 - i*255/n;
				validCellColors[5*n + i] = new Color(255, 0, b);
			}
		}
		return validCellColors;
	}
	
	MatrixCell(){
		energy = GraphCell.energyUponBirth;
		col = M.randInt(validCellColors.length);
		energyPassedToChild = GraphCell.energyUponBirth;

		boolean initialiseRandomly = true;
		boolean initialiseWithMutations = true;
		
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
	}
	
	MatrixCell(MatrixCell cell){
		col = cell.col;
		energyPassedToChild = cell.energyPassedToChild;
		sensoryConceptConnections = M.cloneMatrix(cell.sensoryConceptConnections);
		memoryConceptConnections = M.cloneMatrix(cell.memoryConceptConnections);
		conceptBias = M.cloneVector(cell.conceptBias);
		conceptMotorConnections = M.cloneMatrix(cell.conceptMotorConnections);
		motorBias = M.cloneVector(cell.motorBias);
		conceptMemoryConnections = M.cloneMatrix(cell.conceptMemoryConnections);
		memoryBias = M.cloneVector(cell.memoryBias);
	}
	
	MatrixCell(MatrixCell parent1, MatrixCell parent2){
		col = M.roll(0) ? parent1.col : parent2.col;
		energyPassedToChild = M.roll(0) ? parent1.energyPassedToChild : parent2.energyPassedToChild;
		sensoryConceptConnections = M.mergeMatrices(parent1.sensoryConceptConnections, parent2.sensoryConceptConnections);
		memoryConceptConnections = M.mergeMatrices(parent1.memoryConceptConnections, parent2.memoryConceptConnections);
		conceptBias = M.mergeVectors(parent1.conceptBias, parent2.conceptBias);
		conceptMotorConnections = M.mergeMatrices(parent1.conceptMotorConnections, parent2.conceptMotorConnections);
		motorBias = M.mergeVectors(parent1.motorBias, parent2.motorBias);
		conceptMemoryConnections = M.mergeMatrices(parent1.conceptMemoryConnections, parent2.conceptMemoryConnections);
		memoryBias = M.mergeVectors(parent1.memoryBias, parent2.memoryBias);
	}
	
	MatrixCell(LinkedList<String> lineList){
		// Cell metadata //
		lineList.remove();
		generation = Integer.parseInt(lineList.remove());
		lineList.remove();
		children = Integer.parseInt(lineList.remove());
		lineList.remove();
		lifetimeFoodEaten = Integer.parseInt(lineList.remove());
		
		// Cell data //
		lineList.remove();
		col = Integer.parseInt(lineList.remove());
		lineList.remove();
		energyPassedToChild = Integer.parseInt(lineList.remove());
		
		// Cell variables //
		lineList.remove();
		int x = Integer.parseInt(lineList.remove());
		lineList.remove();
		int y = Integer.parseInt(lineList.remove());
		location = new Point(x, y);
		lineList.remove();
		facing = Direction.parseDir(lineList.remove());
		lineList.remove();
		energy = Integer.parseInt(lineList.remove());
		lineList.remove();
		lifetime = Integer.parseInt(lineList.remove());
		lineList.remove();
		isDead = Boolean.parseBoolean(lineList.remove());
		lineList.remove();
		mate = null;lineList.remove(); // TODO - reassign mates after loading from file.
		
		// Neuron data //
		// Strictly, we only need the neuron data values for memory neurons. For the rest, the neuron count is sufficient.
		lineList.remove();
		sensoryNeurons = loadVector(lineList.remove());
		lineList.remove();
		memoryNeurons = loadVector(lineList.remove());
		lineList.remove();
		conceptNeurons = loadVector(lineList.remove());
		lineList.remove();
		motorNeurons = loadVector(lineList.remove());
		
		// Neuron connections layer 1 //
		lineList.remove();
		sensoryConceptConnections = loadMatrix(lineList, conceptNeurons.length);
		lineList.remove();
		memoryConceptConnections = loadMatrix(lineList, conceptNeurons.length);
		lineList.remove();
		conceptBias = loadVector(lineList.remove());
		
		// Neuron connections layer 2 //
		lineList.remove();
		conceptMotorConnections = loadMatrix(lineList, motorNeurons.length);
		lineList.remove();
		motorBias = loadVector(lineList.remove());
		lineList.remove();
		conceptMemoryConnections = loadMatrix(lineList, memoryNeurons.length);
		lineList.remove();
		memoryBias = loadVector(lineList.remove());
	}
	
	@Override
	protected MatrixCell clone() {
		return new MatrixCell(this);
	}
	
	private boolean displace() {
		Point targetPoint = getAdjacentLocation(facing);
		ArtificialLife.wrapPoint(targetPoint);
		WorldObject target = ArtificialLife.grid[targetPoint.x][targetPoint.y];
		if(target != null){
			return target.interact(this, Interaction.DISPLACE, null);
		} else {
			return false;
		}
	}
	
	void drawSenses(Graphics2D g){
		Point[] eyeVectorList = new Point[3];
		eyeVectorList[0] = facing.getVector();
		eyeVectorList[1] = facing.rotateACW().getVector();
		eyeVectorList[2] = facing.rotateCW().getVector();
		for(Point eyeVector : eyeVectorList) {
			int distance = rayCastLength;
			int drawScale = Display.drawScale;
			Point loc = getLocation();
			g.drawLine(drawScale*loc.x, drawScale*loc.y, drawScale*(loc.x + distance*eyeVector.x), drawScale*(loc.y + distance*eyeVector.y));
		}
	}
	
	private boolean eat() {
		Point targetPoint = getAdjacentLocation(facing);
		WorldObject target = ArtificialLife.grid[targetPoint.x][targetPoint.y];
		if(target != null){
			return target.interact(this, Interaction.EAT, null);
		} else {
			return false;
		}
	}
	
	@Override
	public Color getColor() {
		return validCellColors[col];
	}

	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object data) {
		switch (interactionType) {
		case DISPLACE:
			return displace(interacter, this);
		case EAT:
			interacter.interact(this, Interaction.GIVE_ENERGY, energy);
			energy = 0;
			kill();
			return true;
		case GIVE_ENERGY:
			int amount = (Integer)data;
			energy = Math.min(energy + amount, GraphCell.maxStoredEnergy);
			lifetimeFoodEaten += amount;
			return true;
		case KILL:
			kill();
			return true;
		case PULL:
			return pull(interacter, this);
		case PUSH:
			push(interacter, this);
			return true;
		default:
			return false;
		}
	}
	
	void kill(){
		isDead = true;
		ArtificialLife.stepList.remove(this);
		ArtificialLife.grid[location.x][location.y] = null;
	}
	
	private boolean mate() {
		Point targetPoint = getAdjacentLocation(facing);
		WorldObject target = ArtificialLife.grid[targetPoint.x][targetPoint.y];
		if(target != null && target instanceof MatrixCell){
			((MatrixCell)target).mate = this;
			return true;
		} else {
			return false;
		}
	}
	
	private boolean move() {
		return moveTo(M.add(facing.getVector(), getLocation()));
	}
	
	private boolean moveTo(Point p){
		ArtificialLife.wrapPoint(p);
		if(ArtificialLife.grid[p.x][p.y] == null){
			setLocation(p);
			return true;
		} else {
			return ArtificialLife.grid[p.x][p.y].interact(this, Interaction.PUSH, null);
		}
	}
	
	public void mutate() {
		col = mutateCol(col);
		energyPassedToChild = mutate(energyPassedToChild, mutationProbability);
		mutate(sensoryConceptConnections, mutationProbability);
		mutate(memoryConceptConnections, mutationProbability);
		mutate(conceptBias, mutationProbability);
		mutate(conceptMotorConnections, mutationProbability);
		mutate(motorBias, mutationProbability);
		mutate(conceptMemoryConnections, mutationProbability);
		mutate(memoryBias, mutationProbability);
		
	}
	
	void printToFile(String filename){
		PrintWriter pw = TextFileHandler.startWritingToFile(filename);
		pw.println("Cell2 #"+ArtificialLife.getCellIndex(this));
		
		// Cell metadata //
		pw.println("generation=");
		pw.println(generation);
		pw.println("children=");
		pw.println(children);
		pw.println("lifetimeFoodEaten=");
		pw.println(lifetimeFoodEaten);
		
		// Cell data //
		pw.println("col=");
		pw.println(col);
		pw.println("energyPassedToChild=");
		pw.println(energyPassedToChild);
		
		// Cell variables //
		pw.println("x=");
		pw.println(location.x);
		pw.println("y=");
		pw.println(location.y);
		pw.println("facing=");
		pw.println(facing.name());
		pw.println("energy=");
		pw.println(energy);
		pw.println("lifetime=");
		pw.println(lifetime);
		pw.println("isDead=");
		pw.println(isDead);
		pw.println("mate=");
		pw.println(ArtificialLife.getCellIndex(mate));
		
		// Neuron data //
		pw.println("sensoryNeurons=");
		printVector(pw, sensoryNeurons);
		pw.println("memoryNeurons=");
		printVector(pw, memoryNeurons);
		pw.println("conceptNeurons=");
		printVector(pw, conceptNeurons);
		pw.println("motorNeurons=");
		printVector(pw, motorNeurons);
		
		// Neuron connections layer 1 //
		pw.println("sensoryConceptConnections=");
		printMatrix(pw, sensoryConceptConnections);
		pw.println("memoryConceptConnections=");
		printMatrix(pw, memoryConceptConnections);
		pw.println("conceptBias=");
		printVector(pw, conceptBias);
		
		// Neuron connections layer 2 //
		pw.println("conceptMotorConnections=");
		printMatrix(pw, conceptMotorConnections);
		pw.println("motorBias=");
		printVector(pw, motorBias);
		pw.println("conceptMemoryConnections=");
		printMatrix(pw, conceptMemoryConnections);
		pw.println("memoryBias=");
		printVector(pw, memoryBias);
		
		// Done //
		pw.close();
	}
	
	private boolean pull() {
		Point targetPoint = getAdjacentLocation(facing);
		WorldObject target = ArtificialLife.grid[targetPoint.x][targetPoint.y];
		if(target != null){
			return target.interact(this, Interaction.PULL, null);
		} else {
			return false;
		}
	}
	
	private void rayCast(Direction direction, int distanceNeuron, int redNeuron, int greenNeuron, int blueNeuron) {
		WorldObject seenObject = null;
		Point p = new Point(getLocation());
		Point d = direction.getVector();
		int distance;
		for(distance = 0; distance < rayCastLength; distance ++){
			p.x += d.x;
			p.y += d.y;
			ArtificialLife.wrapPoint(p);
			seenObject = ArtificialLife.grid[p.x][p.y];
			if(seenObject != null){
				break;
			}
		}
		
		// Set sensory neuron 0 to represent the distance to the seen object. //
		double sensoryInput = (float)distance / (float)rayCastLength;
		sensoryNeurons[distanceNeuron] = sensoryInput;
		
		// Set sensory neurons 1-3 to represent the colour of the seen object. //
		Color seenColor = seenObject != null ? seenObject.getColor() : null;
		seenColor = (seenColor == null) ? Color.BLACK : seenColor;
		sensoryInput = (float)seenColor.getRed() / 255.0f;
		sensoryNeurons[redNeuron] = sensoryInput;
		sensoryInput = (float)seenColor.getGreen() / 255.0f;
		sensoryNeurons[greenNeuron] = sensoryInput;
		sensoryInput = (float)seenColor.getBlue() / 255.0f;
		sensoryNeurons[blueNeuron] = sensoryInput;
		
	}
	
	private boolean rotateACW() {
		facing = facing.rotateACW();
		return true;
	}
	
	private boolean rotateCW() {
		facing = facing.rotateCW();
		return true;
	}
	
	private boolean spawn(Direction direction, int energyPassedToChild){
		int energyCost = GraphCell.birthEnergyRequirement + energyPassedToChild;
		if(energy > energyCost){
			Point p = M.add(direction.getVector(), location);
			MatrixCell child = (mate == null) ? createChild(this) : createChild(this, mate);
			boolean placedSuccessfully = ArtificialLife.place(child, p);
			if(placedSuccessfully){
				energy -= energyCost;
				child.energy = energyPassedToChild;
				children ++;
				if(mate != null) {
					mate.children ++;
				}
				ArtificialLife.totalChildren ++;
				return true;
			}
		}
		return false;
	}

	@Override
	public void step() {
		// If we were killed before our step, we don't get to act. //
		if(isDead) {
			return;
		}
		
		double sensoryInput;
		
		// Set sensory neuron 0 to represent the age of the cell. //
		float age = (float)(lifetime/1000.0f) + 1;
		sensoryInput = 1.0f / age;
		sensoryNeurons[0] = sensoryInput;
		
		// Set sensory neuron 1 to represent the energy reserves of the cell. //
		float hunger = Math.max(1.0f, (float)(energy) / (float)(Food.energyGainPerFood));
		sensoryInput = 1.0f / hunger;
		sensoryNeurons[1] = sensoryInput;
		
		// Do a vision ray cast. //
		rayCast(facing, 2, 3, 4, 5);
		rayCast(facing.rotateACW(), 6, 7, 8, 9);
		rayCast(facing.rotateCW(), 10, 11, 12, 13);
		
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
		
		
		
		// Make a list of all the motor neuron indexes
		LinkedList<Integer> actionList = new LinkedList<Integer>();
		for(int i = 0; i < motorNeurons.length; i ++) {
			actionList.add(i);
		}
		
		// Attempt each action in order of neuron firing strength. //
		while(!actionList.isEmpty()){
			int motorToFire = actionList.getFirst();
			
			for(Integer neuronIndex: actionList){
				if(motorNeurons[neuronIndex] > motorNeurons[motorToFire]) {
					motorToFire = neuronIndex;
				}
			}
			
			actionList.remove(Integer.valueOf(motorToFire));
			boolean successful = takeAction(motorToFire);
			if(successful){
				break;
			}
		}
		
		// Life and energy. //
		lifetime ++;
		energy -= energyCostPerTick;
		if(energy < 0){
			kill();
		}
	}
	
	private boolean takeAction(int motorToFire){
		switch (motorToFire) {
		case 0:
			return move();
		case 1:
			return rotateACW();
		case 2:
			return rotateCW();
		case 3:
			return eat();
		case 4:
			return pull();
		case 5:
			return displace();
		case 6:
			return spawn(M.chooseRandom(Direction.values()), energyPassedToChild);
		case 7:
			return mate();
		default:
			return false;
		}
	}
}