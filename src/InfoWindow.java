import java.awt.*;

import javax.swing.*;

import general.Util;

class InfoWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	Cell followedCell = null;
	
	private JLabel infoLabel = new JLabel();
	private JLabel cellInfoLabel = new JLabel();
	
	InfoWindow(){
		setTitle("Info Window");
		setSize(800, 400);
		setLayout(new GridLayout(1, 0));
		add(infoLabel);
		add(cellInfoLabel);
	}
	
	public Cell getFollowedCell(){
		return followedCell;
	}
	
	public int getLatestGeneration(){
		int latestGeneration = 0;
		for(Stepable stepable : Util.cloneList(ArtificialLife.stepList)){
			if(stepable instanceof GraphCell){
				GraphCell cell = (GraphCell)stepable;
				if(cell.generation > latestGeneration){
					latestGeneration = cell.generation;
				}
			}
			if(stepable instanceof MatrixCell){
				MatrixCell cell = (MatrixCell)stepable;
				if(cell.generation > latestGeneration){
					latestGeneration = cell.generation;
				}
			}
		}
		return latestGeneration;
	}
	
	public int getOldestGeneration(){
		int oldestGeneration = Integer.MAX_VALUE;
		for(Stepable stepable : Util.cloneList(ArtificialLife.stepList)){
			if(stepable instanceof GraphCell){
				GraphCell cell = (GraphCell)stepable;
				if(cell.generation < oldestGeneration){
					oldestGeneration = cell.generation;
				}
			}
			if(stepable instanceof MatrixCell){
				MatrixCell cell = (MatrixCell)stepable;
				if(cell.generation < oldestGeneration){
					oldestGeneration = cell.generation;
				}
			}
		}
		return oldestGeneration;
	}
	
	public int getGenerationCount(int generation){
		int cellCount = 0;
		for(Stepable stepable : Util.cloneList(ArtificialLife.stepList)){
			if(stepable instanceof GraphCell){
				GraphCell cell = (GraphCell)stepable;
				if(cell.generation == generation){
					cellCount ++;
				}
			}
			if(stepable instanceof MatrixCell){
				MatrixCell cell = (MatrixCell)stepable;
				if(cell.generation == generation){
					cellCount ++;
				}
			}
		}
		return cellCount;
	}
	
	public void setFollowedCell(Cell cell){
		followedCell = cell;
	}
	
	public void update(){
		String infoText = "<html>";
		
		infoText += "Step Counter = "+ArtificialLife.stepCounter+"<br>";
		infoText += "Number of cells = "+ArtificialLife.getCellCount()+"<br>";
		infoText += "spawning = "+(ArtificialLife.spawnNewCells ? "ON" : "OFF")+"<br>";
		int latestGeneration = getLatestGeneration();
		int oldestGeneration = getOldestGeneration();
		infoText += "Latest generation = "+latestGeneration+" with "+getGenerationCount(latestGeneration)+" cells."+"<br>";
		infoText += "Oldest generation = "+oldestGeneration+" with "+getGenerationCount(oldestGeneration)+" cells."+"<br>";
		infoText += "Total Children = "+ArtificialLife.totalChildren+"<br>";
		
		if(followedCell == null){
			infoText += "Not following cell"+"<br>";
		} else {
			infoText += "Following cell #"+ArtificialLife.getCellIndex(followedCell)+"<br>";
			infoText += "generation = "+followedCell.generation+"<br>";
			infoText += "energy = "+followedCell.energy+"<br>";
			infoText += "lifetime = "+followedCell.lifetime+"<br>";
			infoText += "food eaten = "+followedCell.lifetimeFoodEaten+"<br>"; 
			infoText += "number of childern = "+followedCell.children+"<br>"; 
			
			if(followedCell instanceof GraphCell) {
				GraphCell cell = (GraphCell)followedCell;
				infoText += "organs = "+cell.organList.size()+"<br>";
				
				String organInfoText = "<html>";
				for(Organ organ : cell.organList){
					organInfoText = organInfoText+organ+"<br>";
				}
				organInfoText = organInfoText+"</html>";
				cellInfoLabel.setText(organInfoText);
			}
			if(followedCell instanceof MatrixCell) {
				MatrixCell cell = (MatrixCell)followedCell;
				String memoryInfoText = "<html>";
				for(int i = 0; i < cell.memoryNeurons.length; i ++) {
					memoryInfoText = memoryInfoText+"memoryNeurons["+i+"] = "+cell.memoryNeurons[i]+"<br>";
				}
				memoryInfoText = memoryInfoText+"</html>";
				cellInfoLabel.setText(memoryInfoText);
			}
		}
		
		infoText += "</html>";
		
		infoLabel.setText(infoText);
		
		repaint();
	}
	
}