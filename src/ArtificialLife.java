import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import files.ImageHandler;
import files.TextFileHandler;
import maths.M;

class ArtificialLife implements Runnable {
	private static final int DEFAULT_MAP_HEIGHT = 200;
	private static final int DEFAULT_MAP_WIDTH = 200;
	private static final double DEFAULT_MAP_HAZARD_DENSITY = 0.01;
	private static final double DEFAULT_MAP_PLANT_DENSITY = 0.05;
	private static final double DEFAULT_MAP_WALL_DENSITY = 0.1;
	
	private static InfoWindow infoWindow;
	private static InfoWindow_Species speciesWindow;
	static NeuralNetworkViewer neuralNetworkViewer;
	
	static int fpsCap;
	static int width, height;
	static int minCellCount;
	
	static int stepCounter = 0;
	static int totalChildren = 0;
	static int totalChildrenWithTwoParents = 0;
	static int totalDeathsBy[] = new int[CauseOfDeath.values().length];
	
	static Cell selectedCell = null;
	
	// The World //
	static WorldObject[][] grid = new WorldObject[width][height];
	static TurnList turnList = new TurnList();
	
	// Seasons //
	static int seasonDuration = 1000;
	static boolean isSummer = true;
	
	// Auto-test variables //
	static boolean isAutotesting = false;
	static AutotestManager autotestManager = null;
	
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
	
	public static double getCellSizeMedian() {
		int cellCount = getCellCount();
		if(cellCount == 0) {
			return 0;
		}
		double[] cellSizeList = new double[cellCount];
		int i = 0;
		for(Stepable stepable : getStepList()){
			if(stepable instanceof Cell){
				cellSizeList[i] = ((Cell)stepable).energyStoreSize;
				i ++;
			}
		}
		return M.median(cellSizeList);
	}
	
	public static double getCellSpeedMedian() {
		int cellCount = getCellCount();
		if(cellCount == 0) {
			return 0;
		}
		double[] cellSpeedList = new double[cellCount];
		int i = 0;
		for(Stepable stepable : getStepList()){
			if(stepable instanceof Cell){
				cellSpeedList[i] = ((Cell)stepable).speed;
				i ++;
			}
		}
		return M.median(cellSpeedList);
	}
	
	public static Cell getFirstCell() {
		for(Stepable stepable : getStepList()){
			if(stepable instanceof Cell){
				return (Cell)stepable;
			}
		}
		return null;
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
	
	public static WorldObject getObjectAt(int x, int y) {
		return getObjectAt(new Point(x, y));
	}
	
	public static WorldObject getObjectAt(Point p) {
		ArtificialLife.wrapPoint(p);
		return ArtificialLife.grid[p.x][p.y];
	}
	
	public static WorldObject getObjectAtCursor() {
		return getObjectAt(Display.viewX, Display.viewY);
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
	
	public static void loadFile() {
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
			if(cellType.startsWith("Cell2 #")) {
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
	
	private static void loadMap(String mapFilename) {
		try {
			loadMap_fromFile(mapFilename);
		} catch(Exception e) {
			// If loading the map fails, show an error message and load a default map. //
			String errorMessage = "Error loading map."+"\n"+"Continue with default map?";
			int choice = JOptionPane.showConfirmDialog(null, errorMessage, "", JOptionPane.YES_NO_OPTION);
			if(choice == 0) {
				loadMap_defaultMap();
			} else {
				System.exit(0);
				return;
			}
		}
	}
	
	private static void loadMap_defaultMap() {
		width = DEFAULT_MAP_WIDTH;
		height = DEFAULT_MAP_HEIGHT;
		grid = new WorldObject[width][height];
		
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				if(M.roll(DEFAULT_MAP_WALL_DENSITY)) {
					place(new Wall(), x, y);
				}
				if(M.roll(DEFAULT_MAP_HAZARD_DENSITY)) {
					place(new Hazard(), x, y);
				}
				if(M.roll(DEFAULT_MAP_PLANT_DENSITY)) {
					int plantType = M.randInt(4);
					switch(plantType) {
					case 0:
						place(new Plant(true), x, y);
						break;
					case 1:
						place(new Plant(false), x, y);
						break;
					case 2:
						place(new Plant_Fruit(), x, y);
						break;
					case 3:
						place(new Plant_Tuber(), x, y);
						break;
					}
				}
			}
		}
	}
	
	private static void loadMap_fromFile(String mapFilename) {
		BufferedImage mapImage = ImageHandler.loadImage("data/"+mapFilename);
		width = mapImage.getWidth();
		height = mapImage.getHeight();
		grid = new WorldObject[width][height];
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				int rgb = mapImage.getRGB(x, y);
				if(rgb == Color.BLACK.getRGB()){
					place(new Wall(), x, y);
				} else if(rgb == Color.RED.getRGB()){
					place(new Hazard(), x, y);
				} else if(rgb == Color.GREEN.getRGB()){
					place(new Plant(true), x, y);
				} else if(rgb == Color.YELLOW.getRGB()){
					place(new Plant(false), x, y);
				} else if(rgb == Plant_Fruit.color.getRGB()){
					place(new Plant_Fruit(), x, y);
				} else if(rgb == Plant_Tuber.color.getRGB()){
					place(new Plant_Tuber(), x, y);
				} else if(rgb == Color.MAGENTA.getRGB()){
					place(new Creator(), x, y);
				} else if(rgb == Door.color.getRGB()){
					place(new Door(), x, y);
				}
			}
		}
	}
	
	public static void main(String[] args) {	
		ArtificialLife.setup();
		Controls.setup();
		
		// Auto-testing //
		if(isAutotesting) {
			autotestManager = new AutotestManager();
			autotestManager.setup();
			Controls.setSpeed(Controls.SPEED_SETTING[9]);
		}
		
		// Start Simulation //
		new ArtificialLife().start();
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
	
	public static void removeObjectAt(Point location) {
		WorldObject object = getObjectAt(location);
		if(object != null) {
			object.remove();
		}
	}
	
	public static void removeObjectAtCursor() {
		WorldObject object = getObjectAtCursor();
		if(object != null)
			object.remove();
	}
	
	public static void select(WorldObject selection) {
		if(selection instanceof Cell) {
			selectedCell = (Cell)selection;
		}
	}
	
	public static void selectHoveredObject() {
		select(getObjectAt(Display.viewX, Display.viewY));
	}
	
	public static void setup(){
		// Load parameters from the init file. //
		LinkedList<String> initData = TextFileHandler.readEntireFile("data/init.txt");
		String mapFilename = null;
		for(String line : initData){
			// Ignore comment lines. //
			if(!line.startsWith("//")){
				int dataIndex = line.indexOf("=") + 1;
				if(line.startsWith("autotest=")){
					isAutotesting = line.substring(dataIndex).equals("yes");
				}
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
					Display.tileSize_mapView = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("defaultAttackStrength=")){
					Cell.defaultAttackStrength = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("defaultBiteSize=")){
					Cell.defaultBiteSize = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("defaultBuildStrength=")){
					Cell.defaultBuildStrength = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("defaultEnergyStoreSize=")){
					Cell.defaultEnergyStoreSize = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("defaultHP=")){
					Cell.defaultHP = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("energyGainPerFood=")){
					Food.defaultFoodEnergy = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("baseEnergyCost=")){
					Cell.baseEnergyCost = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("energyCostMultiplier_attackStrength=")){
					Cell.energyCostMultiplier_attackStrength = Double.parseDouble(line.substring(dataIndex));
				}
				if(line.startsWith("energyCostMultiplier_biteSize=")){
					Cell.energyCostMultiplier_biteSize = Double.parseDouble(line.substring(dataIndex));
				}
				if(line.startsWith("energyCostMultiplier_buildStrength=")){
					Cell.energyCostMultiplier_buildStrength = Double.parseDouble(line.substring(dataIndex));
				}
				if(line.startsWith("energyCostMultiplier_energyStoreSize=")){
					Cell.energyCostMultiplier_energyStoreSize = Double.parseDouble(line.substring(dataIndex));
				}
				if(line.startsWith("energyCostMultiplier_hpMax=")){
					Cell.energyCostMultiplier_hpMax = Double.parseDouble(line.substring(dataIndex));
				}
				if(line.startsWith("energyCostMultiplier_speed=")){
					Cell.energyCostMultiplier_speed = Double.parseDouble(line.substring(dataIndex));
				}
				if(line.startsWith("birthEnergyRequirement=")){
					Cell.birthEnergyRequirement = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("energyUponBirth=")){
					Cell.energyUponBirth = Integer.parseInt(line.substring(dataIndex));
				}
			}
		}
		
		// Load the map. //
		loadMap(mapFilename);
		
		// Center the display. //
		Display.viewX = width/2;
		Display.viewY = height/2;
	}
	
	private static void spawnNewCells() {
		int cellCount = getCellCount();
		int failedPlaceAttempts = 0;
		int maxFailedPlaceAttempts = 100;
		while(cellCount < minCellCount) {
			boolean placedSuccessfully = placeRandomly(new MatrixCell());
			if(placedSuccessfully) {
				cellCount ++;
			} else {
				failedPlaceAttempts ++;
				if(failedPlaceAttempts > maxFailedPlaceAttempts) {
					break;
				}
			}
		}
	}
	
	public static void step(){
		// Step the things that need to. //
		turnList.step();
		
		// Spawn new cells if the population is too low. //
		if(Controls.spawnNewCells) {
			spawnNewCells();
		}
		
		// Pause simulation if there has been an extinction. //
		if(!Controls.spawnNewCells && getCellCount() == 0) {
			Controls.setSpeed(Controls.SPEED_SETTING[0]);
		}
		
		// Update the view location if we are following a cell. //
		if(selectedCell != null) {
			Display.viewX = ArtificialLife.selectedCell.getX();
			Display.viewY = ArtificialLife.selectedCell.getY();
		}
		
		// Seasons//
		if(stepCounter % seasonDuration == 0) {
			isSummer = !isSummer;
		}
		
		// Finally, increment the step counter. //
		stepCounter ++;
	}
	
	public static void wrapPoint(Point p){//TODO : this should be improved.
		while(p.x < 0) {
			p.x += width;
		}
		while(p.y < 0) {
			p.y += height;
		}
		p.x = p.x%width;
		p.y = p.y%height;
	}
	
	private ArtificialLife(){
//		neuralNetworkViewer = new NeuralNetworkViewer();
		Display.instance.addKeyListener(Controls.instance);
		Display.instance.setVisible(true);
		speciesWindow = new InfoWindow_Species();
		speciesWindow.addKeyListener(Controls.instance);
		speciesWindow.setVisible(true);
		infoWindow = new InfoWindow();
		infoWindow.addKeyListener(Controls.instance);
		infoWindow.setVisible(true);
	}
	
	public void run() {
		while(true){
			Controls.step();
			if(Controls.isGameRunning){
				step();
			} else if(Controls.stepSimulationOnce){
				Controls.stepSimulationOnce = false;
				step();
			}
			if(stepCounter % Controls.stepsPerDraw == 0){
				Display.instance.draw();
				infoWindow.update();
				speciesWindow.update();
			}
			if(Controls.isFramerateCapped){
				try{
					Thread.sleep(1000/fpsCap);
				} catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			if(isAutotesting) {
				autotestManager.step();
			}
		}
	}
	
	public void start() {
		stepCounter = 0;
		Controls.isGameRunning = true;
		new Thread(this).start();
	}
}