import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import files.ImageHandler;
import files.TextFileHandler;
import general.Util;

import maths.M;

class Display implements Runnable, KeyListener {
	private static JFrame frame;
	private static InfoWindow infoWindow = new InfoWindow();
	static NeuralNetworkViewer neuralNetworkViewer = new NeuralNetworkViewer();
	
	static int fpsCap;
	static int width, height, drawScale;
	static int minCellCount;
	
	static Color bgColor = Color.cyan;
	static Color gridColor = Color.gray;
	
	static WorldObject [][] grid = new WorldObject[width][height];
	static LinkedList<Stepable> stepList = new LinkedList<Stepable>();
	
	static int stepCounter = 0;
	static boolean isRunning = true;
	static boolean loadFile = false;
	static boolean printLog = false;
	static boolean step = false;
	static boolean drawEyeRays = false;
	static boolean drawFollowHighlight = false;
	static boolean isAcceleratedModeOn = false;
	static boolean isDisplayOn = true;
	static boolean spawnNewCells = false;
	
	
	// XXX //
	static boolean useNewCellDefinitions = true;
	// XXX //
	
	
	public static void draw(){
		Graphics2D g = (Graphics2D)frame.getBufferStrategy().getDrawGraphics();
		g.setBackground(bgColor);
		g.clearRect(0, 0, frame.getWidth(), frame.getHeight());
		g.translate(8, 31);
		
		//Draw background
		g.setColor(gridColor);
		g.fillRect(0, 0, drawScale*width, drawScale*height);
		
		// Draw world objects //
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				if(grid[x][y] != null){
					g.setColor(grid[x][y].getColor());
					g.fillRect(drawScale*x, drawScale*y, drawScale, drawScale);
				}
			}
		}
		
		// Draw UI over followed cell. //
		Cell cell = infoWindow.getFollowedCell();
		if(cell != null){
			g.setColor(Color.WHITE);
			if(drawFollowHighlight){
				g.drawLine(0, 0, drawScale*cell.location.x, drawScale*cell.location.y);
			}
			if(drawEyeRays){
				cell.drawSenses(g);
			}
		}
		
		g.dispose();
		frame.getBufferStrategy().show();
	}
	
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
			Cell loadedCell = new Cell(file);
			Point p = loadedCell.getLocation();
			boolean placedSuccessfully = place(loadedCell, p);
			while(!placedSuccessfully) {
				p.x = M.randInt(width-1);
				p.y = M.randInt(height-1);
				placedSuccessfully = place(loadedCell, p);
			}
			
			// Set up the new world. //
			setup();
			stepCounter = 0;
			minCellCount = 1;
		}
	}
	
	public static void main(String[] args) {
		Organ.setup();
		setup();
		Display display = new Display();
		infoWindow.setVisible(true);
		Thread runner = new Thread(display);
		runner.start();
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
		int bestCell = 0;
		int longestLife = 0;
		int oldestCell = 0;
		int i = 0;
		for(Stepable stepable : stepList){
			if(stepable instanceof Cell){
				Cell cell = (Cell)stepable;
				cell.printToFile(filename+"/cell"+i+".txt");
				if(cell.lifetimeFoodEaten > mostFoodEaten){
					bestCell = i;
					mostFoodEaten = cell.lifetimeFoodEaten;
				}
				if(cell.lifetime > longestLife){
					oldestCell = i;
					longestLife = cell.lifetime;
				}
				i ++;
			}
		}
		System.out.println("DONE PRINTING LOG");
		System.out.println("BEST CELL IS #"+bestCell+" WITH "+mostFoodEaten+" FOOD EATEN");
		System.out.println("OLDEST CELL IS #"+oldestCell+" AT "+longestLife+" TICKS");
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
					drawScale = Integer.parseInt(line.substring(dataIndex));
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
				if(rgb == Color.black.getRGB()){
					place(new Wall(false, Color.BLACK), x, y);
				} else if(rgb == Color.blue.getRGB()){
					place(new Wall(true, Color.BLUE), x, y);
				} else if(rgb == Color.red.getRGB()){
					place(new Hazard(), x, y);
				} else if(rgb == Color.green.getRGB()){
					place(new Plant(), x, y);
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
		updateTitle();
		infoWindow.update();
	}
	
	public static void updateTitle(){
		frame.setTitle("Artificial Life Sim : step "+stepCounter);
	}
	
	public static void wrapPoint(Point p){//TODO : this should be improved.
		p.x = (p.x+width)%width;
		p.y = (p.y+height)%height;
	}
	
	Display(){
		frame = new JFrame();
		updateTitle();
		frame.setResizable(false);
		frame.setSize(drawScale*width + 16, drawScale*height + 39);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		frame.addKeyListener(this);
	}
	
	public void keyPressed(KeyEvent e) {
	}
	
	public void keyReleased(KeyEvent e) {
	}
	
	public void keyTyped(KeyEvent e) {
		if(e.getKeyChar() == 'a'){
			isAcceleratedModeOn = !isAcceleratedModeOn;
		}
		if(e.getKeyChar() == 'd'){
			isDisplayOn = !isDisplayOn;
		}
		if(e.getKeyChar() == 'e'){
			drawEyeRays = !drawEyeRays;
		}
		if(e.getKeyChar() == 'l'){
			loadFile = true;
		}
		if(e.getKeyChar() == 'n'){
			if(infoWindow.followedCell != null){
				Display.neuralNetworkViewer.loadCell(infoWindow.followedCell);
			}
		}
		if(e.getKeyChar() == 'p'){
			printLog = true;
		}
		if(e.getKeyChar() == 's'){
			spawnNewCells = !spawnNewCells;
		}
		if(e.getKeyChar() == ' '){
			isRunning = !isRunning;
		}
		if(e.getKeyChar() == '.'){
			step = true;
		}
		if(e.getKeyChar() == '+'){
			selectNextCell();
		}
		if(e.getKeyChar() == '-'){
			selectPrevoiusCell();
		}
		if(e.getKeyChar() == '*'){
			drawFollowHighlight = !drawFollowHighlight;
		}
		if(e.getKeyChar() == 'q') {//XXX//
//			placeRandomly(new Cell2());
			
			
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
				draw();
			}
			if(loadFile){
				int choice = JOptionPane.showOptionDialog(null, "Load one cell or whole population?", "load", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"one cell", "population"}, null); 
				
				if(choice == 0){
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File("logs"));
					fileChooser.showOpenDialog(null);
					File file = fileChooser.getSelectedFile();
					loadCellFromFile(file);
					
				} else if(choice == 1){
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