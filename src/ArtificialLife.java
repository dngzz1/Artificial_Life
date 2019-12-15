import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import files.ImageHandler;
import files.TextFileHandler;

import maths.M;

class ArtificialLife implements Runnable, KeyListener {
	private static Display display;
	private static InfoWindow infoWindow;
	static NeuralNetworkViewer neuralNetworkViewer;
	
	static int fpsCap;
	static int width, height;
	static int minCellCount;
	
	private static int stepsPerDraw_acceleratedMode = 10000;
	
	static WorldObject [][] grid = new WorldObject[width][height];
	
	static TurnList turnList = new TurnList();
	
	static boolean isRunning;
	static int stepCounter;
	static int totalChildren = 0;
	
	// Controls //
	static boolean isAcceleratedModeOn = false;
	static boolean isDisplayOn = true;
	static boolean loadFile = false;
	static boolean printLog = false;
	static boolean step = false;
	static boolean spawnNewCells = true;
	
	// Auto-test variables //
	static boolean isAutotesting = false;
	static int simulationNumber;
	static String autoTestLogFilename;
	static int numberOfSimulations;
	static int simulationLength;
	
	private static void autoTest_start() {
		System.out.println("AUTOTESTING");
		System.out.println();
		
		// Get parameters from user. //
		String numberOfSimulationsInput = JOptionPane.showInputDialog("Number of simulations:");
		numberOfSimulations = Integer.parseInt(numberOfSimulationsInput);
		String simulationLengthInput = JOptionPane.showInputDialog("Simulation length (steps):");
		simulationLength = Integer.parseInt(simulationLengthInput);
		
		// Start printing log file. //
		autoTestLogFilename = "logs/autoTestLog_"+new Date().getTime()+".txt";
		PrintWriter pw = TextFileHandler.startWritingToFile(autoTestLogFilename);
		pw.println("AUTO-RUNNING "+numberOfSimulations+" SIMULATIONS FOR "+simulationLength+" STEPS EACH");
		pw.println();
		pw.println("sim = simulation number");
		pw.println("gen = latest generation");
		pw.println("#ch = total number of children");
		pw.println();
		pw.println("sim:gen:#ch");
		pw.close();
		
		// Begin simulation //
		isAutotesting = true;
		simulationNumber = 1;
		isAcceleratedModeOn = true;
		isDisplayOn = false;
		new ArtificialLife().start();
	}
	
	private void autoTest_step() {
		// If the simulation has reached the termination condition, we log the results and restart. //
		if(stepCounter >= simulationLength) {
			// Print the log of this simulation to file. //
			System.out.println("SIMULATION #"+simulationNumber+" COMPLETE");
			int generation = infoWindow.getGenerationCount(infoWindow.getLatestGeneration());
			PrintWriter pw = TextFileHandler.startWritingToFile(autoTestLogFilename, true);
			pw.println(simulationNumber+":"+generation+":"+ArtificialLife.totalChildren);
			pw.close();
			printGenerationToFile();
			
			simulationNumber ++;
			if(simulationNumber <= numberOfSimulations) {
				// Reset for the next simulation . //
				turnList.clear();
				ArtificialLife.setup();
				stepCounter = 0;
			} else {
				// End auto-test. //
				isRunning = false;
				System.exit(0);
			}
		}
	}
	
	public static int getCellCount(){
		int cellCount = 0;
		for(Stepable stepable : getStepList()){
			if(stepable instanceof Cell){
				cellCount ++;
			}
		}
		return cellCount;
	}
	
	public static int getCellIndex(Cell cell){
		int index = 0;
		for(Stepable stepable : getStepList()){
			if(stepable instanceof Cell){
				if(stepable == cell){
					return index;
				}
				index ++;
			}
		}
		return -1;
	}
	
	public static Cell getFirstCell() {
		for(Stepable stepable : getStepList()){
			if(stepable instanceof Cell){
				return (Cell)stepable;
			}
		}
		return null;
	}
	
	public static Cell getFollowedCell() {
		return infoWindow.getFollowedCell();
	}
	
	public static Cell getLastCell(){
		Cell lastCell = null;
		for(Stepable stepable : getStepList()){
			if(stepable instanceof Cell){
				lastCell = (Cell)stepable;
			}
		}
		return lastCell;
	}
	
	public static Cell getNextCell(Cell cell) {
		boolean returnNext = false;
		for(Stepable stepable : getStepList()){
			if(returnNext && stepable instanceof Cell){
				return (Cell)stepable;
			} else if(stepable == cell){
				returnNext = true;
			}
		}
		return null;
	}
	
	public static Cell getPreviousCell(Cell cell) {
		Cell previousCell = null;
		for(Stepable stepable : getStepList()){
			if(stepable instanceof Cell){
				if(stepable == cell){
					return previousCell;
				} else {
					previousCell = (Cell)stepable;
				}
			}
		}
		return null;
	}
	
	public static LinkedList<Stepable> getStepList(){
		return turnList.getStepList();
	}
	
	private static void loadFile() {
		int choice = JOptionPane.showOptionDialog(null, "Load one cell or whole population?", "load", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"one cell", "population"}, null); 
		
		if(choice == 0){
			// User chose to load one cell. //
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("logs"));
			fileChooser.showOpenDialog(null);
			File file = fileChooser.getSelectedFile();
			loadCellFromFile(file);
			
		} else if(choice == 1){
			// User chose to load a population of cells. //
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setCurrentDirectory(new File("logs"));
			fileChooser.showOpenDialog(null);
			File folder = fileChooser.getSelectedFile();
			
			String path = folder.getPath();
			
			int i = 0;
			File file;
			while(true){
				file = new File(path+File.separator+"cell"+i+".txt");
				if(file.canRead()){
					loadCellFromFile(file);
					i ++;
				} else break;
			} 
			
		}
	}
	
	public static void loadCellFromFile(File file){
		if(file != null){
			// Load and place the cell. //
			LinkedList<String> lineList = TextFileHandler.readEntireFile(file.getPath());
			String cellType = lineList.remove();
			WorldObject loadedCell = null;
			if(cellType.startsWith("Cell #")) {
				loadedCell = new GraphCell(lineList);
			} else if(cellType.startsWith("Cell2 #")) {
				loadedCell = new MatrixCell(lineList);
			}
			if(loadedCell != null) {
				Point p = loadedCell.getLocation();
				boolean placedSuccessfully = place(loadedCell, p);
				while(!placedSuccessfully) {
					p.x = M.randInt(width-1);
					p.y = M.randInt(height-1);
					placedSuccessfully = place(loadedCell, p);
				}
			} else {
				System.out.println("ERROR LOADING CELL FROM FILE: "+file.getPath());
			}
		}
	}
	
	public static void main(String[] args) {
		// Ask if we want to run auto-testing. //
		int choice = JOptionPane.showConfirmDialog(null, "Run Auto-testing?", "", JOptionPane.YES_NO_OPTION);
		boolean doAutotest = (choice == 0);
		if(choice == -1) {
			System.exit(0);
			return;
		}
		
		Organ.setup();
		ArtificialLife.setup();
		
		// Auto-testing //
		if(doAutotest) {
			autoTest_start();
		} else {
			new ArtificialLife().start();
		}
	}
	
	public static boolean place(WorldObject object, int x, int y){
		return place(object, new Point(x, y));
	}
	
	public static boolean place(WorldObject object, Point location){
		wrapPoint(location);
		if(grid[location.x][location.y] == null){
			object.setLocation(location);
			grid[location.x][location.y] = object;
			if(object instanceof Stepable){
				turnList.add((Stepable)object, 1);
			}
			return true;
		} else return false;
	}
	
	public static boolean placeRandomly(WorldObject object){
		int x = M.randInt(width - 1);
		int y = M.randInt(height - 1);
		return place(object, new Point(x, y));
	}
	
	public static void printGenerationToFile(){
		System.out.println("PRINTING LOG");
		Date date = new Date();
		String filename = "logs/log_"+date.getTime();

		int mostFoodEaten = 0;
		int bestCell_foodEaten = 0;
		int mostChildren = 0;
		int bestCell_children = 0;
		int longestLife = 0;
		int bestCell_oldest = 0;
		int i = 0;
		for(Stepable stepable : getStepList()){
			if(stepable instanceof GraphCell){
				GraphCell cell = (GraphCell)stepable;
				cell.printToFile(filename+"/cell"+i+".txt");
				if(cell.lifetimeFoodEaten > mostFoodEaten){
					bestCell_foodEaten = i;
					mostFoodEaten = cell.lifetimeFoodEaten;
				}
				if(cell.children > mostChildren){
					bestCell_children = i;
					mostChildren = cell.children;
				}
				if(cell.lifetime > longestLife){
					bestCell_oldest = i;
					longestLife = cell.lifetime;
				}
				i ++;
			}
			if(stepable instanceof MatrixCell){
				MatrixCell cell = (MatrixCell)stepable;
				cell.printToFile(filename+"/cell"+i+".txt");
				if(cell.lifetimeFoodEaten > mostFoodEaten){
					bestCell_foodEaten = i;
					mostFoodEaten = cell.lifetimeFoodEaten;
				}
				if(cell.children > mostChildren){
					bestCell_children = i;
					mostChildren = cell.children;
				}
				if(cell.lifetime > longestLife){
					bestCell_oldest = i;
					longestLife = cell.lifetime;
				}
				i ++;
			}
		}
		System.out.println("DONE PRINTING LOG");
		System.out.println("BEST CELLS ARE:");
		System.out.println("#"+bestCell_foodEaten+" WITH "+mostFoodEaten+" FOOD EATEN");
		System.out.println("#"+bestCell_children+" WITH "+mostChildren+" CHILDREN");
		System.out.println("#"+bestCell_oldest+" WITH "+longestLife+" STEP LIFETIME");
		System.out.println();
	}
	
	public static void remove(WorldObject object) {
		if(object instanceof Stepable) {
			turnList.remove((Stepable)object);
		}
		ArtificialLife.grid[object.location.x][object.location.y] = null;
	}
	
	public static void selectNextCell() {
		Cell selectedCell = infoWindow.getFollowedCell();
		if(selectedCell == null || !turnList.contains(selectedCell)){
			infoWindow.setFollowedCell(getFirstCell());
		} else {
			Cell nextCell = getNextCell(selectedCell);
			if(nextCell != null){
				infoWindow.setFollowedCell(nextCell);
			}
		}
	}
	
	public static void selectPrevoiusCell(){
		Cell selectedCell = infoWindow.getFollowedCell();
		if(selectedCell == null || !turnList.contains(selectedCell)){
			infoWindow.setFollowedCell(getLastCell());
		} else {
			Cell previousCell = getPreviousCell(selectedCell);
			if(previousCell != null){
				infoWindow.setFollowedCell(previousCell);
			}
		}
	}
	
	public static void setup(){
		LinkedList<String> initData = TextFileHandler.readEntireFile("data/init.txt");
		String mapFilename = null;
		for(String line : initData){
			// Ignore comment lines. //
			if(!line.startsWith("//")){
				int dataIndex = line.indexOf("=") + 1;
				if(line.startsWith("fpsCap=")){
					fpsCap = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("map=")){
					mapFilename = line.substring(dataIndex);
				}
				if(line.startsWith("minCellCount=")){
					minCellCount = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("drawScale=")){
					Display.drawScale = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("initialMutations=")){
					GraphCell.initialMutations = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("maxMutations=")){
					GraphCell.maxMutations = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("energyGainPerFood=")){
					Food.energyGainPerFood = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("maxStoredEnergy=")){
					Cell.maxStoredEnergy = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("birthEnergyRequirement=")){
					Cell.birthEnergyRequirement = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("energyUponBirth=")){
					Cell.energyUponBirth = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("energyCostPerTick=")){
					GraphCell.energyCostPerTick = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("energyCostPerNeuron=")){
					GraphCell.energyCostPerNeuron = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("energyCostToRotate=")){
					Organ_Interaction.energyCostToRotate = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("energyCostToMove=")){
					Organ_Interaction.energyCostToMove = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("energyCostPerTileSeen=")){
					Organ_Eye.energyCostPerTileSeen = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_addConnection=")){
					GraphCell.mutationChance_addConnection = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_addNeuron=")){
					GraphCell.mutationChance_addNeuron = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_changeNeuronThreshold=")){
					GraphCell.mutationChance_changeNeuronThreshold = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_changeFiringStrength=")){
					GraphCell.mutationChance_changeFiringStrength = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_collapseConection=")){
					GraphCell.mutationChance_collapseConection = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_removeConnection=")){
					GraphCell.mutationChance_removeConnection = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_splitConnection=")){
					GraphCell.mutationChance_splitConnection = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_addOrgan=")){
					GraphCell.mutationChance_addOrgan = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_removeOrgan=")){
					GraphCell.mutationChance_removeOrgan = Float.parseFloat(line.substring(dataIndex));
				}
			}
		}
		BufferedImage mapImage = ImageHandler.loadImage("data/"+mapFilename);
		width = mapImage.getWidth();
		height = mapImage.getHeight();
		grid = new WorldObject[width][height];
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				int rgb = mapImage.getRGB(x, y);
				if(rgb == Color.BLACK.getRGB()){
					place(new Wall(false, Color.BLACK), x, y);
				} else if(rgb == Color.BLUE.getRGB()){
					place(new Wall(true, Color.BLUE), x, y);
				} else if(rgb == Color.RED.getRGB()){
					place(new Hazard(), x, y);
				} else if(rgb == Color.GREEN.getRGB()){
					place(new Plant(), x, y);
				} else if(rgb == Color.MAGENTA.getRGB()){
					place(new Creator(), x, y);
				}
			}
		}
	}
	
	public static void step(){
		// Step the things that need to. //
		turnList.step();
		
		// Spawn new cells if the population is too low. //
		if(spawnNewCells && getCellCount() < minCellCount){
			placeRandomly(new MatrixCell());
		}
		
		stepCounter ++;
		infoWindow.update();
	}
	
	public static void wrapPoint(Point p){//TODO : this should be improved.
		p.x = (p.x+width)%width;
		p.y = (p.y+height)%height;
	}
	
	private ArtificialLife(){
		neuralNetworkViewer = new NeuralNetworkViewer();
		display = new Display();
		display.addKeyListener(this);
		display.setVisible(true);
		infoWindow = new InfoWindow();
		infoWindow.addKeyListener(this);
		infoWindow.setVisible(true);
	}
	
	public void keyPressed(KeyEvent e) {}
	
	public void keyReleased(KeyEvent e) {}
	
	public void keyTyped(KeyEvent e) {
		switch (e.getKeyChar()) {
		case 'a':
			isAcceleratedModeOn = !isAcceleratedModeOn;
			break;
		case 'd':
			isDisplayOn = !isDisplayOn;
			break;
		case 'e':
			Display.drawEyeRays = !Display.drawEyeRays;
			break;
		case 'l':
			loadFile = true;
			break;
		case 'n':
			if(infoWindow.followedCell != null){
				neuralNetworkViewer.loadCell(infoWindow.followedCell);
			}
			break;
		case 'p':
			printLog = true;
			break;
		case 's':
			spawnNewCells = !spawnNewCells;
			break;
		case ' ':
			isRunning = !isRunning;
			break;
		case '.':
			step = true;
			break;
		case '+':
			selectNextCell();
			break;
		case '-':
			selectPrevoiusCell();
			break;
		case '*':
			Display.drawFollowHighlight = !Display.drawFollowHighlight;
			break;
		case 'q'://XXX
			
//			placeRandomly(new Cell2());
			
			break;
		default:
			break;
		}
		infoWindow.update();
	}
	
	public void run() {
		while(true){
			if(isRunning){
				step();
			} else if(step){
				step();
				step = false;
			}
			if(isDisplayOn || stepCounter % stepsPerDraw_acceleratedMode == 0){
				display.draw();
			}
			if(loadFile){
				loadFile();
				loadFile = false;
			}
			if(printLog){
				printGenerationToFile();
				printLog = false;
			}
			if(!isAcceleratedModeOn){
				try{
					Thread.sleep(1000/fpsCap);
				} catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			if(isAutotesting) {
				autoTest_step();
			}
		}
	}
	
	public void start() {
		stepCounter = 0;
		isRunning = true;
		new Thread(this).start();
	}
}