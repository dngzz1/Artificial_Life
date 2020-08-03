import java.awt.*;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.*;

class InfoWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JLabel infoLabel_general = new JLabel();
	private JLabel infoLabel_species = new JLabel();
	private JLabel infoLabel_object = new JLabel();
	
	private static int generationMax(){
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
	
	private static int generationMin(){
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
	
	private static int generationCellCount(int generation){
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
	
	private static String infoText_general() {
		String infoText = "<html>";
		if(!Controls.isGameRunning) {
			infoText += "Step Counter = "+ArtificialLife.stepCounter+" (PAUSED)"+"<br>";
		} else if(Controls.isFramerateCapped) {
			infoText += "Step Counter = "+ArtificialLife.stepCounter+" x"+Controls.stepsPerDraw+" (CAPPED)"+"<br>";
		} else {
			infoText += "Step Counter = "+ArtificialLife.stepCounter+" x"+Controls.stepsPerDraw+" (UNCAPPED)"+"<br>";
		}
		infoText += "Season = "+(ArtificialLife.isSummer ? "Summer" : "Winter")+"<br>";
		infoText += "Number of cells = "+ArtificialLife.getCellCount()+"<br>";
		infoText += "spawning = "+(Controls.spawnNewCells ? "ON" : "OFF")+"<br>";
		int latestGeneration = generationMax();
		int oldestGeneration = generationMin();
		infoText += "Latest generation = "+latestGeneration+" with "+generationCellCount(latestGeneration)+" cells."+"<br>";
		infoText += "Oldest generation = "+oldestGeneration+" with "+generationCellCount(oldestGeneration)+" cells."+"<br>";
		infoText += "Total children = "+ArtificialLife.totalChildren+"<br>";
		infoText += "Total children with two parents = "+ArtificialLife.totalChildrenWithTwoParents+"<br>";
		for(CauseOfDeath causeOfDeath : CauseOfDeath.values()) {
			infoText += "Total deaths by "+causeOfDeath.name().toLowerCase()+" = "+ArtificialLife.totalDeathsBy[causeOfDeath.ordinal()]+"<br>";
		}
		infoText += "Median size = "+(float)ArtificialLife.getCellSizeMedian()+"<br>";
		infoText += "Median speed = "+(float)ArtificialLife.getCellSpeedMedian()+"<br>";
		infoText += "</html>";
		return infoText;
	}
	
	private static String infoText_object() {
		String infoText = "<html>";
		if(Controls.inPlaceMode) {
			infoText += "** Placing/Removing Objects **"+"<br>";
		}
		WorldObject hoveredObject = ArtificialLife.getObjectAtCursor();
		if(hoveredObject == null) {
			infoText += "Here: [no object]"+"<br>";
		} else if(hoveredObject == ArtificialLife.selectedCell) {
			infoText += "Here: "+hoveredObject.getDisplayName()+" (following)"+"<br>";
			infoText += hoveredObject.getInfo();
		} else {
			infoText += "Here: "+hoveredObject.getDisplayName()+"<br>";
			infoText += hoveredObject.getInfo();
		}
		infoText += "</html>";
		return infoText;
	}
	
	private static String infoText_species() {
		String infoText = "<html>";
		for(String line : speciesInfo()) {
			infoText += line+"<br>";
		}
		infoText += "</html>";
		return infoText;
	}
	
	private static LinkedList<String> speciesInfo() {
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
			String line = topSpecies.getDisplayName()+" : "+topCellCount;
			speciesInfo.add(line);
		}
		return speciesInfo;
	}
	
	InfoWindow(){
		setTitle("Info Window");
		setSize(800, 400);
		setLayout(new GridLayout(1, 0));
		add(infoLabel_general);
		add(infoLabel_species);
		add(infoLabel_object);
	}
	
	public void update(){
		// General/Species/Object info text. //
		infoLabel_general.setText(infoText_general());
		infoLabel_species.setText(infoText_species());
		infoLabel_object.setText(infoText_object());
		
		// Repaint once label text is updated. //
		repaint();
	}
}