import java.awt.*;

import javax.swing.*;

class InfoWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JLabel infoLabel_general = new JLabel();
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
			infoText += "Sim speed = PAUSED"+"<br>";
		} else if(Controls.isFramerateCapped) {
			infoText += "Sim speed = "+Controls.stepsPerDraw+"x (CAPPED)"+"<br>";
		} else {
			infoText += "Sim speed = "+Controls.stepsPerDraw+"x (UNCAPPED)"+"<br>";
		}
		infoText += "Step Counter = "+ArtificialLife.stepCounter+"<br>";
		infoText += "Season = "+(ArtificialLife.isSummer ? "Summer" : "Winter")+"<br>";
		infoText += "Number of cells = "+ArtificialLife.getCellCount()+"<br>";
		infoText += "spawning = "+(Controls.spawnNewCells ? "ON" : "OFF")+"<br>";
		int latestGeneration = generationMax();
		int oldestGeneration = generationMin();
		infoText += "Latest generation = "+latestGeneration+" with "+generationCellCount(latestGeneration)+" cells."+"<br>";
		infoText += "Oldest generation = "+oldestGeneration+" with "+generationCellCount(oldestGeneration)+" cells."+"<br>";
		infoText += "Total children = "+ArtificialLife.totalChildren+"<br>";
		infoText += "Total 2-parent children = "+ArtificialLife.totalChildrenWithTwoParents+"<br>";
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
			infoText += "<br>";
			infoText += hoveredObject.getInfo();
		} else {
			infoText += "Here: "+hoveredObject.getDisplayName()+"<br>";
			infoText += "<br>";
			infoText += hoveredObject.getInfo();
		}
		infoText += "</html>";
		return infoText;
	}
	
	InfoWindow(){
		setTitle("Info");
		setSize(600, 500);
		setLayout(new GridLayout(1, 0));
		add(infoLabel_general);
		add(infoLabel_object);
	}
	
	public void update(){
		// General/Species/Object info text. //
		infoLabel_general.setText(infoText_general());
		infoLabel_object.setText(infoText_object());
		
		// Repaint once label text is updated. //
		repaint();
	}
}