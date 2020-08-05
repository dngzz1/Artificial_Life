import java.awt.Color;
import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.LinkedList;

abstract class Cell extends WorldObject implements Stepable {
	static final Double MINIMUM_CELL_SPEED = 0.001;
	static final Color[] validCellColors = validCellColors();

	static int defaultAttackStrength;
	static int defaultBiteSize;
	static int defaultBuildStrength;
	static int defaultEnergyStoreSize;
	static int defaultHP;
	static int baseEnergyCost;
	static double energyCostMultiplier_attackStrength;
	static double energyCostMultiplier_biteSize;
	static double energyCostMultiplier_buildStrength;
	static double energyCostMultiplier_energyStoreSize;
	static double energyCostMultiplier_hpMax;
	static double energyCostMultiplier_speed;
	static int birthEnergyRequirement;
	static int energyUponBirth;
	static int mutationRate_col = 2;
	static double mutationChance_species = 0.02;
	static double mutationRate_attackStrength = 0.2;
	static double mutationRate_biteSize = 0.2;
	static double mutationRate_buildStrength = 0.2;
	static double mutationRate_energyStoreSize = 0.2;
	static double mutationRate_hp = 0.2;
	
	// Cell Metadata //
	Species species;
	int generation = 0;
	int children = 0;
	int lifetimeFoodEaten = 0;
	
	// Cell Data //
	int energy = energyUponBirth;
	int lifetime = 0;
	Direction facing = M.chooseRandom(Direction.values());
	boolean isDead = false;
	
	// Physical characteristics //
	int attackStrength;
	int biteSize;
	int buildStrength;
	int energyStoreSize;
	int hp, hpMax;
	double speed = 1.0; // turns/step (0.0 - 1.0) 
	double turnFraction = 0.0;
	
	protected static double[][] loadMatrix(LinkedList<String> dataLineList, int rows) {
		double[][] matrix = new double[rows][];
		for(int row = 0; row < matrix.length; row ++) {
			matrix[row] = loadVector(dataLineList.remove());
		}
		return matrix;
	}
	
	protected static double[] loadVector(String dataString) {
		String[] data = dataString.split(";");
		double[] vector = new double[data.length];
		for(int i = 0; i < vector.length; i ++) {
			vector[i] = Double.parseDouble(data[i]);
		}
		return vector;
	}
	
	protected static int mutateCol(int col, double probability) {
		if(M.roll(probability)) {
			int delta = M.randInt(mutationRate_col) - M.randInt(mutationRate_col);
			col = (col + validCellColors.length + delta)%validCellColors.length;
		}
		return col;
	}
	
	protected static double mutateDouble(double value, double minValue, double maxValue) {
		if(M.roll(0.5)) {
			// 50% chance to increase value. //
			double maxIncrease = maxValue - value;
			value += maxIncrease*M.rand(1)*M.rand(1)*M.rand(1);
		} else {
			// 50% chance to decrease value. //
			double maxDecrease = value - minValue;
			value -= maxDecrease*M.rand(1)*M.rand(1)*M.rand(1);
		}
		return value;
	}
	
	protected static double mutateDoubleProportionally(double value, double rateOfChange) {
		double multiplier = M.rand(1.0, 1.0 + rateOfChange);
		if(M.roll(0.5)) {
//			// 50% chance to increase. //
			return value*multiplier;
		} else {
//			// 50% chance to decrease. //
			return value/multiplier;
		}
	}
	
	protected static double mutateDoubleSigned(double value) {
		return M.rand(1)*M.rand(1)*M.rand(1) - M.rand(1)*M.rand(1)*M.rand(1);
	}
	
	protected static double mutateDoubleSigned(double value, double probability) {
		return M.roll(probability) ? mutateDoubleSigned(value) : value;
	}
	
	protected static int mutateInt(int value, double probability) {
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
	
	protected static void mutateMatrix(double[][] matrix, double probability) {
		for(int i = 0; i < matrix.length; i ++) {
			for(int j = 0; j < matrix[i].length; j ++) {
				matrix[i][j] = mutateDoubleSigned(matrix[i][j], probability);
			}
		}
	}
	
	protected static void mutateVector(double[] vector, double probability) {
		for(int i = 0; i < vector.length; i ++) {
			vector[i] = mutateDoubleSigned(vector[i], probability);
		}
	}
	
	protected static void printMatrix(PrintWriter pw, double[][] matrix) {
		for(int row = 0; row < matrix.length; row ++) {
			printVector(pw, matrix[row]);
		}
	}
	
	protected static void printVector(PrintWriter pw, double[] vector) {
		for(int i = 0; i < vector.length; i ++) {
			pw.print(vector[i]+";");
		}
		pw.println();
	}
	
	private static final Color[] validCellColors() {
		int n = 8;
		Color[] validCellColors = new Color[6*n];
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
		return validCellColors;
	}
	
	Cell(){
		species = new Species();
		attackStrength = defaultAttackStrength;
		biteSize = defaultBiteSize;
		buildStrength = defaultBuildStrength;
		energyStoreSize = defaultEnergyStoreSize;
		hp = hpMax = defaultHP;
	}
	
	Cell(Cell parent){
		species = parent.species;
		attackStrength = parent.attackStrength;
		biteSize = parent.biteSize;
		buildStrength = parent.buildStrength;
		energyStoreSize = parent.energyStoreSize;
		hp = hpMax = parent.hpMax;
	}
	
	Cell(Cell parent1, Cell parent2){
		species = parent1.species;
		attackStrength = M.roll(0.5) ? parent1.attackStrength : parent2.attackStrength;
		biteSize = M.roll(0.5) ? parent1.biteSize : parent2.biteSize;
		buildStrength = M.roll(0.5) ? parent1.buildStrength : parent2.buildStrength;
		energyStoreSize = M.roll(0.5) ? parent1.energyStoreSize : parent2.energyStoreSize;
		hp = hpMax = M.roll(0.5) ? parent1.hpMax : parent2.hpMax;
	}
	
	abstract void drawSenses(Graphics2D g);
	
	protected int getEnergyCostPerTurn() {
		// Add up all the energy costs with the appropriate multipliers. //
		double energyCostPerGameStep = baseEnergyCost;
		energyCostPerGameStep += attackStrength*energyCostMultiplier_attackStrength;
		energyCostPerGameStep += biteSize*energyCostMultiplier_biteSize;
		energyCostPerGameStep += buildStrength*energyCostMultiplier_buildStrength;
		energyCostPerGameStep += energyStoreSize*energyCostMultiplier_energyStoreSize;
		energyCostPerGameStep += hpMax*energyCostMultiplier_hpMax;
		energyCostPerGameStep += speed*energyCostMultiplier_speed;
		
		// Divide by speed to convert from energy per game step to energy per cell step. //
		double energyCostPerCellStep = energyCostPerGameStep/speed;
		return (int)(energyCostPerCellStep);
	}
	
	protected int getMaxStoredEnergy() {
		return energyStoreSize;
	}
	
	@Override
	public int getStepsToNextTurn() {
		if(isDead) {
			return -1;
		} else {
			double stepsPerTurn = 1.0/speed;
			double stepsThisTurn = turnFraction + stepsPerTurn;
			int turnTime = (int)stepsThisTurn;
			turnFraction = stepsThisTurn - turnTime;
			return turnTime;
		}
	}
	
	public void mutate() {
		if(M.roll(mutationChance_species)) {
			mutateSpecies();
		}
		attackStrength = Math.max(1, mutateInt(attackStrength, mutationRate_attackStrength));
		biteSize = Math.max(1, mutateInt(biteSize, mutationRate_biteSize));
		buildStrength = Math.max(1, mutateInt(buildStrength, mutationRate_buildStrength));
		energyStoreSize = Math.max(1, mutateInt(energyStoreSize, mutationRate_energyStoreSize));
		hp = hpMax = Math.max(1, mutateInt(hpMax, mutationRate_hp));
	}
	
	public void mutateSpecies() {
		species.mutate((MatrixCell)this);
	}
}