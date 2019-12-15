import java.awt.Color;
import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.LinkedList;

abstract class Cell extends WorldObject implements Stepable {
	static int birthEnergyRequirement;
	static int energyUponBirth;
	static int maxStoredEnergy;
	static double mutationChance_species = 0.02;
	static int mutationRate_col = 2;
	static double mutationRate_size = 0.2;
	static Color[] validCellColors = validCellColors();
	static double predationSizeThreshold = 0.8;
	
	// Cell Metadata //
	Species species; // TODO  - species stuff //
	int generation = 0;
	int children = 0;
	int lifetimeFoodEaten = 0;
	
	// Cell Data //
	int energy = energyUponBirth;
	int lifetime = 0;
	Direction facing = M.chooseRandom(Direction.values());
	boolean isDead = false;
	
	// TODO : speed and size //
	double size;
	double speed; // turns/step (0.0 - 1.0) 
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
		double multiplier;
		if(M.roll(0.5)) {
			// 50% chance to increase. //
			double maxMultiplier = 1.0 + rateOfChange;
			multiplier = M.rand(1.0, maxMultiplier);
		} else {
			// 50% chance to decrease. //
			double minMultiplier = 1.0 / (1.0 + rateOfChange);
			multiplier = M.rand(minMultiplier, 1.0);
		}
		return value*multiplier;
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
	
	Cell(){
		species = new Species();
		size = 20.0;
		speed = 0.5;
	}
	
	Cell(Cell parent){
		species = parent.species;
		size = parent.size;
		speed = parent.speed;
	}
	
	Cell(Cell parent1, Cell parent2){
		species = parent1.species;
		size = M.roll(0.5) ? parent1.size : parent2.size;
		speed = M.roll(0.5) ? parent1.speed : parent2.speed;
	}
	
	abstract void drawSenses(Graphics2D g);
	
	protected int getEnergyCostPerStep() {
		return getEnergyCostPerTurnFromSize() + getEnergyCostPerTurnFromSpeed();
	}
	
	private int getEnergyCostPerTurnFromSize() {
		return (int) size;
	}
	
	private int getEnergyCostPerTurnFromSpeed() {
		int maxEnergyCostPerTurnFromSpeed = 30;
		return (int) (maxEnergyCostPerTurnFromSpeed*speed);
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
		speed = mutateDouble(speed, 0.0, 1.0);
		size = mutateDoubleProportionally(size, mutationRate_size);
	}
	
	public void mutateSpecies() {
		species.mutate((MatrixCell)this);
	}
}