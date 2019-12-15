import java.awt.*;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.*;

class InfoWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private int stepsPerUpdate_acceleratedMode_displayOn = 100, stepsPerUpdate_acceleratedMode_displayOff = 1000;
	
	Cell followedCell = null;
	
	private JLabel infoLabel = new JLabel();
	private JLabel speciesInfoLabel = new JLabel();
	private JLabel cellInfoLabel = new JLabel();
	
	InfoWindow(){
		setTitle("Info Window");
		setSize(800, 400);
		setLayout(new GridLayout(1, 0));
		add(infoLabel);
		add(speciesInfoLabel);
		add(cellInfoLabel);
	}
	
	public Cell getFollowedCell(){
		return followedCell;
	}
	
	public int getLatestGeneration(){
		int latestGeneration = 0;
		for(Stepable stepable : ArtificialLife.getStepList()){
			if(stepable instanceof Cell){
				Cell cell = (Cell)stepable;
				if(cell.generation > latestGeneration){
					latestGeneration = cell.generation;
				}
			}
		}
		return latestGeneration;
	}
	
	public int getOldestGeneration(){
		int oldestGeneration = Integer.MAX_VALUE;
		for(Stepable stepable : ArtificialLife.getStepList()){
			if(stepable instanceof Cell){
				Cell cell = (Cell)stepable;
				if(cell.generation < oldestGeneration){
					oldestGeneration = cell.generation;
				}
			}
		}
		return oldestGeneration;
	}
	
	public int getGenerationCount(int generation){
		int cellCount = 0;
		for(Stepable stepable : ArtificialLife.getStepList()){
			if(stepable instanceof Cell){
				Cell cell = (Cell)stepable;
				if(cell.generation == generation){
					cellCount ++;
				}
			}
		}
		return cellCount;
	}
	
	private LinkedList<String> getSpeciesInfo() {
		// Get the data. //
		LinkedList<Species> speciesList = new LinkedList<Species>();
		LinkedList<Integer> speciesCellCountList = new LinkedList<Integer>();
		for(Stepable stepable : ArtificialLife.getStepList()){
			if(stepable instanceof Cell){
				Species species = ((Cell)stepable).species;
				int index = speciesList.indexOf(species);
				if(index == -1) {
					speciesList.add(species);
					speciesCellCountList.add(1);
				} else {
					int speciesNewCellCount = speciesCellCountList.get(index) + 1;
					speciesCellCountList.set(index, speciesNewCellCount);
				}
			}
		}
		
		// Put the data into a string list. //
		LinkedList<String> speciesInfo = new LinkedList<String>();
		while(!speciesList.isEmpty() && speciesInfo.size() < 20) {
			// Add the data for the species with the highest population first. //
			Species topSpecies = speciesList.getFirst();
			int topCellCount = speciesCellCountList.getFirst();
			ListIterator<Species> speciesIterator = speciesList.listIterator();
			ListIterator<Integer> cellCountIterator = speciesCellCountList.listIterator();
			while(speciesIterator.hasNext()) {
				Species species = speciesIterator.next();
				int cellCount = cellCountIterator.next();
				if(cellCount > topCellCount) {
					topSpecies = species;
					topCellCount = cellCount;
				}
			}
			
			// Don't display species with one member. //
			if(topCellCount <= 1) {
				break;
			}
			
			// Remove the top species from the list of data once found. //
			int indexToRemove = speciesList.indexOf(topSpecies);
			speciesList.remove(indexToRemove);
			speciesCellCountList.remove(indexToRemove);
			String line = topSpecies.name+" : "+topCellCount;
			speciesInfo.add(line);
		}
		return speciesInfo;
	}
	
	public void setFollowedCell(Cell cell){
		followedCell = cell;
	}
	
	public void update(){
		// We don't update every step when in accelerated mode. //
		if(ArtificialLife.isAcceleratedModeOn && ArtificialLife.isRunning) {
			if(ArtificialLife.isDisplayOn) {
				if(ArtificialLife.stepCounter % stepsPerUpdate_acceleratedMode_displayOn != 0) {
					return;
				}
			} else if(ArtificialLife.stepCounter % stepsPerUpdate_acceleratedMode_displayOff != 0) {
				return;
			}
		}
		
		// General info text. //
		String infoText = "<html>";
		infoText += "Step Counter = "+ArtificialLife.stepCounter+"<br>";
		infoText += "Number of cells = "+ArtificialLife.getCellCount()+"<br>";
		infoText += "spawning = "+(ArtificialLife.spawnNewCells ? "ON" : "OFF")+"<br>";
		int latestGeneration = getLatestGeneration();
		int oldestGeneration = getOldestGeneration();
		infoText += "Latest generation = "+latestGeneration+" with "+getGenerationCount(latestGeneration)+" cells."+"<br>";
		infoText += "Oldest generation = "+oldestGeneration+" with "+getGenerationCount(oldestGeneration)+" cells."+"<br>";
		infoText += "Total Children = "+ArtificialLife.totalChildren+"<br>";
		infoText += "</html>";
		infoLabel.setText(infoText);
		
		// Species info text. //
		String speciesInfoText = "<html>";
		for(String line : getSpeciesInfo()) {
			speciesInfoText += line+"<br>";
		}
		speciesInfoText += "</html>";
		speciesInfoLabel.setText(speciesInfoText);
		
		// Followed cell info text. //
		String cellInfoText = "<html>";
		if(followedCell == null){
			cellInfoText += "Not following cell"+"<br>";
		} else {
			cellInfoText += "Following cell #"+ArtificialLife.getCellIndex(followedCell)+"<br>";
			cellInfoText += "species = "+followedCell.species.name+"<br>";
			cellInfoText += "generation = "+followedCell.generation+"<br>";
			cellInfoText += "size = "+followedCell.size+"<br>"; 
			cellInfoText += "speed = "+followedCell.speed+"<br>"; 
			cellInfoText += "energy = "+followedCell.energy+"<br>";
			cellInfoText += "lifetime = "+followedCell.lifetime+"<br>";
			cellInfoText += "food eaten = "+followedCell.lifetimeFoodEaten+"<br>"; 
			cellInfoText += "number of childern = "+followedCell.children+"<br>"; 
			
			MatrixCell cell = (MatrixCell)followedCell;
			cellInfoText += "# memory neurons = "+cell.memoryNeurons.length+"<br>"; 
			cellInfoText += "# concept neurons = "+cell.conceptNeurons.length+"<br>"; 
		}
		cellInfoText += "</html>";
		cellInfoLabel.setText(cellInfoText);
		
		// Repaint once label text is updated. //
		repaint();
	}
	
}