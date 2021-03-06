import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.PrintWriter;
import java.util.LinkedList;

import files.TextFileHandler;

class MatrixCell extends Cell {
	static int rayCastLength = 10;
	static double mutationProbability = 0.05;
	static double mutationProbability_col = 0.9;
	
	
	/////////////////////////////////////////////////
	// TODO - Run tests varying these parameters.
	boolean useNonlinearNormalisation = true;
	boolean useOnlyPositiveMotorSignals = true;
	// TODO - Also run tests varying the nonlinear normalisation function.
	/////////////////////////////////////////////////
	
	
	// Cell Data //
	int hue;
	int energyPassedToChild;
	
	// Cell Variables //
	MatrixCell mate = null;
	Action lastActionTaken = null;
	
	// Neurons //
	double[] sensoryNeurons;
	double[] memoryNeurons;
	double[] conceptNeurons;
	double[] motorNeurons;
	
	// Connection layer 1 //
	double[][] sensoryConceptConnections;
	double[][] memoryConceptConnections;
	double[] conceptBias;
	
	// Connection layer 2 //
	double[][] conceptMotorConnections;
	double[] motorBias;
	double[][] conceptMemoryConnections;
	double[] memoryBias;
	
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
	
	MatrixCell(){
		setupNeuralNetwork();
		initialiseNeuralNetworkRandomly();
		
		energy = energyUponBirth;
		hue = M.randInt(validCellColors.length);
		energyPassedToChild = energyUponBirth;
	}
	
	MatrixCell(MatrixCell parent){
		super(parent);
		setupNeuralNetwork();
		initialiseNeuralNetworkCloningParent(parent);
		
		hue = parent.hue;
		energyPassedToChild = parent.energyPassedToChild;
	}
	
	/**
	 * We require that both parents belong to the same species.
	 */
	MatrixCell(MatrixCell parent1, MatrixCell parent2){
		super(parent1, parent2);
		setupNeuralNetwork();
		initialiseNeuralNetworkMergingParents(parent1, parent2);
		
		hue = M.roll(0.5) ? parent1.hue : parent2.hue;
		energyPassedToChild = M.roll(0.5) ? parent1.energyPassedToChild : parent2.energyPassedToChild;
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
		hue = Integer.parseInt(lineList.remove());
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
	
	private boolean action_attack() {
		int energyCost = Food.defaultFoodEnergy/10;
		if(energy > energyCost) {
			Point targetPoint = getAdjacentLocation(facing);
			WorldObject target = ArtificialLife.getObjectAt(targetPoint);
			if(target != null){
				energy -= energyCost;
				return target.interact(this, Interaction.ATTACK, new Object[] {attackStrength});
			}
		}
		return false;
	}
	
	private boolean action_displace() {
		Point targetPoint = getAdjacentLocation(facing);
		ArtificialLife.wrapPoint(targetPoint);
		WorldObject target = ArtificialLife.grid[targetPoint.x][targetPoint.y];
		if(target != null){
			return target.interact(this, Interaction.DISPLACE, null);
		} else {
			return false;
		}
	}
	
	private boolean action_eat() {
		Point targetPoint = getAdjacentLocation(facing);
		WorldObject target = ArtificialLife.grid[targetPoint.x][targetPoint.y];
		if(target != null){
			return target.interact(this, Interaction.EAT, new Object[] {biteSize});
		} else {
			return false;
		}
	}
	
	private boolean action_mate() {
		Point targetPoint = getAdjacentLocation(facing);
		WorldObject target = ArtificialLife.grid[targetPoint.x][targetPoint.y];
		if(target != null && target instanceof MatrixCell){
			((MatrixCell)target).setMate(this);
			return true;
		} else {
			return false;
		}
	}
	
	private boolean action_move() {
		return moveTo(M.add(facing.getVector(), getLocation()));
	}
	
	private boolean action_pull() {
		Point targetPoint = getAdjacentLocation(facing);
		WorldObject target = ArtificialLife.grid[targetPoint.x][targetPoint.y];
		if(target != null){
			return target.interact(this, Interaction.PULL, null);
		} else {
			return false;
		}
	}
	
	private boolean action_rotateACW() {
		facing = facing.rotateACW();
		return true;
	}
	
	private boolean action_rotateCW() {
		facing = facing.rotateCW();
		return true;
	}
	
	private boolean action_spawnChild(Direction direction, int energyPassedToChild) {
		int energyCost = birthEnergyRequirement + energyPassedToChild;
		if(energy > energyCost){
			Point p = getAdjacentLocation(direction);
			MatrixCell child = (mate == null) ? createChild(this) : createChild(this, mate);
			boolean placedSuccessfully = ArtificialLife.place(child, p);
			if(placedSuccessfully){
				energy -= energyCost;
				child.energy = energyPassedToChild;
				children ++;
				ArtificialLife.totalChildren ++;
				if(mate != null) {
					mate.children ++;
					ArtificialLife.totalChildrenWithTwoParents ++;
				}
				return true;
			}
		}
		return false;
	}
	
	private boolean action_spawnFood() {
		int energyCost = Food.defaultFoodEnergy;
		if(energy > energyCost) {
			Point p = getAdjacentLocation(facing);
			Food food = new Food();
			boolean placedSuccessfully = ArtificialLife.place(food, p);
			if(placedSuccessfully) {
				energy -= energyCost;
				return true;
			}
		}
		return false;
	}
	
	private boolean action_spawnWall() {
		int energyCost = Food.defaultFoodEnergy;
		if(energy > energyCost) {
			Point p = getAdjacentLocation(facing);
			DestructibleWall wall = new DestructibleWall(buildStrength);
			boolean placedSuccessfully = ArtificialLife.place(wall, p);
			if(placedSuccessfully) {
				energy -= energyCost;
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected MatrixCell clone() {
		return new MatrixCell(this);
	}
	
	private void control_speed(double motorNeuronFiringStrength) {
		speed = Math.max(MINIMUM_CELL_SPEED, motorNeuronFiringStrength);
	}
	
	void drawSenses(Graphics2D g){
		Point location = getLocation();
		Direction[] eyeDirectionList = new Direction[3];
		eyeDirectionList[0] = facing;
		eyeDirectionList[1] = facing.rotateACW();
		eyeDirectionList[2] = facing.rotateCW();
		int x1, y1, x2, y2;
		for(Direction eyeDirection : eyeDirectionList) {
			RayCastResult result = RayCast.castRay(location, eyeDirection, rayCastLength);
			int distance = result.distanceToObject + 1;
			Point eyeVector = eyeDirection.getVector();
			if(Display.mapView) {
				int tileSize = Display.tileSize_mapView;
				x1 = tileSize*location.x + tileSize/2;
				y1 = tileSize*location.y + tileSize/2;
				x2 = x1 + tileSize*distance*eyeVector.x;
				y2 = y1 + tileSize*distance*eyeVector.y;
			} else {
				int tileSize = Display.tileSize;
				x1 = tileSize*(Display.viewRadiusInTiles) + tileSize/2;
				y1 = tileSize*(Display.viewRadiusInTiles) + tileSize/2;
				x2 = x1 + tileSize*distance*eyeVector.x;
				y2 = y1 + tileSize*distance*eyeVector.y;
			}
			g.drawLine(x1, y1, x2, y2);
		}
	}
	
	@Override
	public Color getColor() {
		return validCellColors[hue];
	}

	@Override
	public String getDisplayName() {
		return toString();
	}

	@Override
	public String getInfo() {
		String info = "";
		info += "Species Data:"+"<br>";
		info += "species = "+species.getDisplayName()+"<br>";
		info += "# memory neurons = "+memoryNeurons.length+"<br>"; 
		info += "# concept neurons = "+conceptNeurons.length+"<br>"; 
		info += "<br>";
		info += "Cell Data:"+"<br>";
		info += "generation = "+generation+"<br>";
		info += "attackStrength = "+attackStrength+"<br>"; 
		info += "biteSize = "+biteSize+"<br>"; 
		info += "buildStrength = "+buildStrength+"<br>"; 
		info += "energy = "+energy+" / "+energyStoreSize+"<br>"; 
		info += "hp = "+hp+" / "+hpMax+"<br>"; 
		info += "speed = "+speed+"<br>"; 
		info += "<br>";
		info += "Metadata:"+"<br>";
		info += "last action taken: "+getInfo_lastActionTaken()+"<br>";
		info += "lifetime = "+lifetime+"<br>";
		info += "food eaten = "+lifetimeFoodEaten+"<br>"; 
		info += "number of children = "+children+"<br>"; 
		return info;
	}
	
	private String getInfo_lastActionTaken() {
		if(lastActionTaken == null) {
			return "-";
		} else {
			return lastActionTaken.name().toLowerCase();
		}
	}
	
	private void hit(int strength) {
		hp -= strength;
		
		//XXX//
		if(Controls.trackPredation) {
			Display.viewX = getX();
			Display.viewY = getY();
			Controls.setSpeed(Controls.SPEED_SETTING[0]);
		}
		
		
		if(hp <= 0) {
			Food remains = new Food(energy, true);
			energy = 0;
			kill(CauseOfDeath.PREDATION);
			ArtificialLife.place(remains, location);
		}
	}
	
	private void initialiseNeuralNetworkCloningParent(MatrixCell parent) {
		sensoryConceptConnections = M.cloneMatrix(parent.sensoryConceptConnections);
		memoryConceptConnections = M.cloneMatrix(parent.memoryConceptConnections);
		conceptBias = M.cloneVector(parent.conceptBias);
		conceptMotorConnections = M.cloneMatrix(parent.conceptMotorConnections);
		motorBias = M.cloneVector(parent.motorBias);
		conceptMemoryConnections = M.cloneMatrix(parent.conceptMemoryConnections);
		memoryBias = M.cloneVector(parent.memoryBias);
	}
	
	private void initialiseNeuralNetworkMergingParents(MatrixCell parent1, MatrixCell parent2) {
		sensoryConceptConnections = M.mergeMatrices(parent1.sensoryConceptConnections, parent2.sensoryConceptConnections);
		memoryConceptConnections = M.mergeMatrices(parent1.memoryConceptConnections, parent2.memoryConceptConnections);
		conceptBias = M.mergeVectors(parent1.conceptBias, parent2.conceptBias);
		conceptMotorConnections = M.mergeMatrices(parent1.conceptMotorConnections, parent2.conceptMotorConnections);
		motorBias = M.mergeVectors(parent1.motorBias, parent2.motorBias);
		conceptMemoryConnections = M.mergeMatrices(parent1.conceptMemoryConnections, parent2.conceptMemoryConnections);
		memoryBias = M.mergeVectors(parent1.memoryBias, parent2.memoryBias);
	}
	
	private void initialiseNeuralNetworkRandomly() {
		double min = -1, max = 1;
		M.setRandomEntries(sensoryConceptConnections, min, max);
		M.setRandomEntries(memoryConceptConnections, min, max);
		M.setRandomEntries(conceptBias, min, max);
		M.setRandomEntries(conceptMotorConnections, min, max);
		M.setRandomEntries(motorBias, min, max);
		M.setRandomEntries(conceptMemoryConnections, min, max);
		M.setRandomEntries(memoryBias, min, max);
		mutate();
	}

	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object[] data) {
		switch (interactionType) {
		case ATTACK:
			hit((Integer)data[0]);
			return true;
		case DISPLACE:
			return displace(interacter, this);
		case GIVE_ENERGY:
			int amount = (Integer)data[0];
			boolean isFlesh = (Boolean)data[1];
			energy = Math.min(energy + amount, getMaxStoredEnergy());
			lifetimeFoodEaten += amount;
			if(isFlesh) {
				lifetimeFoodEatenByPredation += amount;
			}
			return true;
		case KILL:
			kill((CauseOfDeath)data[0]);
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
	
	void kill(CauseOfDeath cause){
		isDead = true;
		ArtificialLife.remove(this);
		ArtificialLife.totalDeathsBy[cause.ordinal()] ++;
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
	
	@Override
	public void mutate() {
		super.mutate();
		hue = mutateCol(hue, mutationProbability_col);
		energyPassedToChild = Math.max(1, mutateInt(energyPassedToChild, mutationProbability));
		mutateMatrix(sensoryConceptConnections, mutationProbability);
		mutateMatrix(memoryConceptConnections, mutationProbability);
		mutateVector(conceptBias, mutationProbability);
		mutateMatrix(conceptMotorConnections, mutationProbability);
		mutateVector(motorBias, mutationProbability);
		mutateMatrix(conceptMemoryConnections, mutationProbability);
		mutateVector(memoryBias, mutationProbability);
	}
	
	public void mutate_conceptNeuron_add() {
		conceptNeurons = M.overwriteVector(new double[conceptNeurons.length + 1], conceptNeurons);
		conceptBias = M.overwriteVector(new double[conceptNeurons.length], conceptBias);
		sensoryConceptConnections = M.overwriteMatrix(new double[conceptNeurons.length][sensoryNeurons.length], sensoryConceptConnections);
		memoryConceptConnections = M.overwriteMatrix(new double[conceptNeurons.length][memoryNeurons.length], memoryConceptConnections);
		conceptMotorConnections = M.overwriteMatrix(new double[motorNeurons.length][conceptNeurons.length], conceptMotorConnections);
		conceptMemoryConnections = M.overwriteMatrix(new double[memoryNeurons.length][conceptNeurons.length], conceptMemoryConnections);
	}
	
	public void mutate_conceptNeuron_remove() {
		int removedNeuronIndex = M.randInt(conceptNeurons.length);
		conceptNeurons = M.shrinkVector(conceptNeurons, removedNeuronIndex);
		conceptBias = M.shrinkVector(conceptBias, removedNeuronIndex);
		sensoryConceptConnections = M.shrinkMatrixRows(sensoryConceptConnections, removedNeuronIndex);
		memoryConceptConnections = M.shrinkMatrixRows(memoryConceptConnections, removedNeuronIndex);
		conceptMotorConnections = M.shrinkMatrixCols(conceptMotorConnections, removedNeuronIndex);
		conceptMemoryConnections = M.shrinkMatrixCols(conceptMemoryConnections, removedNeuronIndex);
	}
	
	public void mutate_memoryNeuron_add() {
		memoryNeurons = M.overwriteVector(new double[memoryNeurons.length + 1], memoryNeurons);
		memoryBias = M.overwriteVector(new double[memoryNeurons.length], memoryBias);
		memoryConceptConnections = M.overwriteMatrix(new double[conceptNeurons.length][memoryNeurons.length], memoryConceptConnections);
		conceptMemoryConnections = M.overwriteMatrix(new double[memoryNeurons.length][conceptNeurons.length], conceptMemoryConnections);
	}
	
	public void mutate_memoryNeuron_remove() {
		int removedNeuronIndex = M.randInt(memoryNeurons.length);
		memoryNeurons = M.shrinkVector(memoryNeurons, removedNeuronIndex);
		memoryBias = M.shrinkVector(memoryBias, removedNeuronIndex);
		memoryConceptConnections = M.shrinkMatrixCols(memoryConceptConnections, removedNeuronIndex);
		conceptMemoryConnections = M.shrinkMatrixRows(conceptMemoryConnections, removedNeuronIndex);
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
		pw.println(hue);
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
	
	private void setMate(MatrixCell cell) {
		if(species == cell.species) {
			mate = cell;
		}
	}
	
	private void senseVision(Direction direction, int distanceNeuron, int redNeuron, int greenNeuron, int blueNeuron) {
		// Perform a vision ray-cast. //
		RayCastResult result = RayCast.castRay(getLocation(), direction, rayCastLength);
		WorldObject seenObject = result.object;
		int distance = result.distanceToObject;
		
		// Represent the distance to the seen object in a sensory neuron. //
		double sensoryInput = (float)distance / (float)rayCastLength;
		sensoryNeurons[distanceNeuron] = sensoryInput;
		
		// Represent the red/green/blue values of the seen object in sensory neurons. //
		Color seenColor = seenObject != null ? seenObject.getColor() : null;
		seenColor = (seenColor == null) ? Color.BLACK : seenColor;
		sensoryInput = (float)seenColor.getRed() / 255.0f;
		sensoryNeurons[redNeuron] = sensoryInput;
		sensoryInput = (float)seenColor.getGreen() / 255.0f;
		sensoryNeurons[greenNeuron] = sensoryInput;
		sensoryInput = (float)seenColor.getBlue() / 255.0f;
		sensoryNeurons[blueNeuron] = sensoryInput;
	}
	
	private void setupNeuralNetwork() {
		// Neurons //
		sensoryNeurons = new double[15];
		memoryNeurons = new double[species.neuronCount_memory()];
		conceptNeurons = new double[species.neuronCount_concept()];
		motorNeurons = new double[Action.values().length];
		
		// Connection layer 1 //
		sensoryConceptConnections = new double[conceptNeurons.length][sensoryNeurons.length];
		memoryConceptConnections = new double[conceptNeurons.length][memoryNeurons.length];
		conceptBias = new double[conceptNeurons.length];
		
		// Connection layer 2 //
		conceptMotorConnections = new double[motorNeurons.length][conceptNeurons.length];
		motorBias = new double[motorNeurons.length];
		conceptMemoryConnections = new double[memoryNeurons.length][conceptNeurons.length];
		memoryBias = new double[memoryNeurons.length];
	}

	@Override
	public void step() {
		// If we were killed before our step, we don't get to act. //
		if(isDead) {
			return;
		}
		
		// Set sensory neuron #0 to represent the age of the cell. //
		double age = (double)(lifetime/1000.0f) + 1;
		age = 1.0f / age;
		sensoryNeurons[0] = age;
		
		// Set sensory neuron #1 to represent the energy reserves of the cell. //
		double hunger = Math.max(1.0f, (double)(energy) / (double)(Food.defaultFoodEnergy));
		hunger = 1.0f / hunger;
		sensoryNeurons[1] = hunger;
		
		// Set sensory neuron #2 to represent the hp of the cell. //
		double pain = hp*1.0/hpMax;
		sensoryNeurons[2] = pain;
		
		// Do a vision ray cast. //
		senseVision(facing, 3, 4, 5, 6);
		senseVision(facing.rotateACW(), 7, 8, 9, 10);
		senseVision(facing.rotateCW(), 11, 12, 13, 14);
		
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
			if(useNonlinearNormalisation) {
				conceptNeurons[i] = M.normalise(conceptNeurons[i]);
			} else {
				conceptNeurons[i] /= 1 + sensoryNeurons.length + memoryNeurons.length;
			}
		}
		for(int i = 0; i < motorNeurons.length; i ++) {
			// Set neuron to the bias value. //
			motorNeurons[i] = motorBias[i];
			
			// Add the concept connections. //
			for(int j = 0; j < conceptNeurons.length; j ++) {
				motorNeurons[i] += conceptMotorConnections[i][j]*conceptNeurons[j];
			}
			
			// Normalise so that the value is between 0 and 1. //
			if(useNonlinearNormalisation) {
				motorNeurons[i] = M.normalise(motorNeurons[i]);
			} else {
				motorNeurons[i] /= 1 + conceptNeurons.length;
			}
		}
		for(int i = 0; i < memoryNeurons.length; i ++) {
			// Set neuron to the bias value. //
			memoryNeurons[i] = memoryBias[i];
			
			// Add the concept connections. //
			for(int j = 0; j < conceptNeurons.length; j ++) {
				memoryNeurons[i] += conceptMemoryConnections[i][j]*conceptNeurons[j];
			}
			
			// Normalise so that the value is between 0 and 1. //
			if(useNonlinearNormalisation) {
				memoryNeurons[i] = M.normalise(memoryNeurons[i]);
			} else {
				memoryNeurons[i] /= 1 + conceptNeurons.length;
			}
		}
		
		// Motor neuron #0 sets the cell's speed (so is excluded from the list of possible actions). //
		control_speed(motorNeurons[Action.NONACTION_SPEED_CONTROL.ordinal()]);
		
		// Make a list of the other motor neuron indexes
		LinkedList<Integer> actionList = new LinkedList<Integer>();
		for(int i = 1; i < motorNeurons.length; i ++) {
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
			
			if(useOnlyPositiveMotorSignals && motorNeurons[motorToFire] <= 0) {
				break;
			}
			
			boolean successful = takeAction(Action.values()[motorToFire]);
			if(successful){
				break;
			}
		}
		
		// Life and energy. //
		lifetime ++;
		energy -= getEnergyCostPerTurn();
		if(energy < 0){
			kill(CauseOfDeath.STARVATION);
		}
	}
	
	private boolean takeAction(Action action){
		lastActionTaken = action;
		switch (action) {
		case NONACTION_SPEED_CONTROL:
			return false;
		case MOVE:
			return action_move();
		case ROTATE_ACW:
			return action_rotateACW();
		case ROTATE_CW:
			return action_rotateCW();
		case ATTACK:
			return action_attack();
		case EAT:
			return action_eat();
		case MATE:
			return action_mate();
		case DISPLACE:
			return action_displace();
		case PULL:
			return action_pull();
		case SPAWN_CHILD:
			return action_spawnChild(M.chooseRandom(Direction.values()), energyPassedToChild);
		case SPAWN_FOOD:
			return action_spawnFood();
		case SPAWN_WALL:
			return action_spawnWall();
		default:
			return false;
		}
	}
	
	private enum Action {
		NONACTION_SPEED_CONTROL, MOVE, ROTATE_ACW, ROTATE_CW, ATTACK, EAT, MATE, DISPLACE, PULL, SPAWN_CHILD, SPAWN_FOOD, SPAWN_WALL;
	}
}