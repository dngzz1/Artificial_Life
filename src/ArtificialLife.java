import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import files.ImageHandler;
import files.TextFileHandler;
import general.Util;

import maths.M;

class ArtificialLife implements Runnable, KeyListener {
	private static Display display;
	private static InfoWindow infoWindow;
	static NeuralNetworkViewer neuralNetworkViewer = new NeuralNetworkViewer();
	
	static int fpsCap;
	static int width, height;
	static int minCellCount;
	
	static WorldObject [][] grid = new WorldObject[width][height];
	static LinkedList<Stepable> stepList = new LinkedList<Stepable>();
	
	static int stepCounter = 0;
	static boolean isRunning = true;
	static boolean loadFile = false;
	static boolean printLog = false;
	static boolean step = false;
	static boolean isAcceleratedModeOn = false;
	static boolean isDisplayOn = true;
	static boolean spawnNewCells = true;
	
	static int totalChildren = 0;
	
	
	// XXX //
	static boolean useNewCellDefinitions = true;
	// XXX //
	
	public static int getCellCount(){
		int cellCount = 0;
		for(Stepable stepable : stepList){
			if(stepable instanceof Cell){
				cellCount ++;
			}
			if(stepable instanceof Cell2){
				cellCount ++;
			}
		}
		return cellCount;
	}
	
	public static int getCellIndex(Cell cell){
		int index = 0;
		for(Stepable stepable : stepList){
			if(stepable instanceof Cell){
				if(stepable == cell){
					return index;
				}
				index ++;
			}
			if(stepable instanceof Cell2){
				index ++;
			}
		}
		return -1;
	}
	
	public static int getCellIndex(Cell2 cell){
		int index = 0;
		for(Stepable stepable : stepList){
			if(stepable instanceof Cell){
				index ++;
			}
			if(stepable instanceof Cell2){
				if(stepable == cell){
					return index;
				}
				index ++;
			}
		}
		return -1;
	}
	
	public static Cell getFirstCell() {
		for(Stepable stepable : stepList){
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
		for(Stepable stepable : stepList){
			if(stepable instanceof Cell){
				lastCell = (Cell)stepable;
			}
		}
		return lastCell;
	}
	
	public static Cell getNextCell(Cell cell) {
		boolean returnNext = false;
		for(Stepable stepable : stepList){
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
		for(Stepable stepable : stepList){
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
	
	public static void loadCellFromFile(File file){
		if(file != null){
			// Erase the current world. //
			stepList.clear();
			grid = new WorldObject[width][height];
			
			// Load and place the cell. //
			LinkedList<String> lineList = TextFileHandler.readEntireFile(file.getPath());
			String cellType = lineList.remove();
			WorldObject loadedCell = null;
			if(cellType.startsWith("Cell #")) {
				loadedCell = new Cell(lineList);
			} else if(cellType.startsWith("Cell2 #")) {
				loadedCell = new Cell2(lineList);
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
			
			// Set up the new world. //
			setup();
			stepCounter = 0;
			minCellCount = 1;
		}
	}
	
	public static void main(String[] args) {
		Organ.setup();
		ArtificialLife.setup();
		ArtificialLife program = new ArtificialLife();
		new Thread(program).start();
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
				stepList.add((Stepable)object);
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
		for(Stepable stepable : stepList){
			if(stepable instanceof Cell){
				Cell cell = (Cell)stepable;
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
			if(stepable instanceof Cell2){
				Cell2 cell = (Cell2)stepable;
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
	
	public static void selectNextCell() {
		Cell selectedCell = infoWindow.getFollowedCell();
		if(selectedCell == null || !stepList.contains(selectedCell)){
			infoWindow.setFollowedCell(getFirstCell());
			infoWindow.update();
		} else {
			Cell nextCell = getNextCell(selectedCell);
			if(nextCell != null){
				infoWindow.setFollowedCell(nextCell);
				infoWindow.update();
			}
		}
	}
	
	public static void selectPrevoiusCell(){
		Cell selectedCell = infoWindow.getFollowedCell();
		if(selectedCell == null || !stepList.contains(selectedCell)){
			infoWindow.setFollowedCell(getLastCell());
			infoWindow.update();
		} else {
			Cell previousCell = getPreviousCell(selectedCell);
			if(previousCell != null){
				infoWindow.setFollowedCell(previousCell);
				infoWindow.update();
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
					Cell.initialMutations = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("maxMutations=")){
					Cell.maxMutations = Integer.parseInt(line.substring(dataIndex));
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
					Cell.energyCostPerTick = Integer.parseInt(line.substring(dataIndex));
				}
				if(line.startsWith("energyCostPerNeuron=")){
					Cell.energyCostPerNeuron = Integer.parseInt(line.substring(dataIndex));
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
					Cell.mutationChance_addConnection = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_addNeuron=")){
					Cell.mutationChance_addNeuron = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_changeNeuronThreshold=")){
					Cell.mutationChance_changeNeuronThreshold = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_changeFiringStrength=")){
					Cell.mutationChance_changeFiringStrength = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_collapseConection=")){
					Cell.mutationChance_collapseConection = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_removeConnection=")){
					Cell.mutationChance_removeConnection = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_splitConnection=")){
					Cell.mutationChance_splitConnection = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_addOrgan=")){
					Cell.mutationChance_addOrgan = Float.parseFloat(line.substring(dataIndex));
				}
				if(line.startsWith("mutationChance_removeOrgan=")){
					Cell.mutationChance_removeOrgan = Float.parseFloat(line.substring(dataIndex));
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
		for(Stepable stepable : Util.cloneList(stepList)){
			stepable.step();
		}
		
		// Spawn new cells if the population is too low. //
		if(spawnNewCells && getCellCount() < minCellCount){
			if(useNewCellDefinitions) {
				placeRandomly(new Cell2());
			} else {
				placeRandomly(new Cell());
			}
		}
		
		stepCounter ++;
		infoWindow.update();
	}
	
	public static void wrapPoint(Point p){//TODO : this should be improved.
		p.x = (p.x+width)%width;
		p.y = (p.y+height)%height;
	}
	
	private ArtificialLife(){
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
	}
	
	public void run() {
		while(true){
			if(isRunning){
				step();
			} else if(step){
				step();
				step = false;
			}
			if(isDisplayOn){
				display.draw();
			}
			if(loadFile){
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
		}
	}
}