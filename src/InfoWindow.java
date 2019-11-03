import java.awt.*;

import javax.swing.*;

class InfoWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	Cell followedCell = null;
	
	private JLabel infoLabel = new JLabel();
	private JLabel organInfoLabel = new JLabel();
	
	InfoWindow(){
		setTitle("Info Window");
		setSize(800, 400);
		setLayout(new GridLayout(1, 0));
		add(infoLabel);
		add(organInfoLabel);
	}
	
	public Cell getFollowedCell(){
		return followedCell;
	}
	
	private int getLatestGeneration(){
		int latestGeneration = 0;
		for(Stepable stepable : ArtificialLife.stepList){
			if(stepable instanceof Cell){
				Cell cell = (Cell)stepable;
				if(cell.generation > latestGeneration){
					latestGeneration = cell.generation;
				}
			}
			if(stepable instanceof Cell2){
				Cell2 cell = (Cell2)stepable;
				if(cell.generation > latestGeneration){
					latestGeneration = cell.generation;
				}
			}
		}
		return latestGeneration;
	}
	
	private int getOldestGeneration(){
		int oldestGeneration = Integer.MAX_VALUE;
		for(Stepable stepable : ArtificialLife.stepList){
			if(stepable instanceof Cell){
				Cell cell = (Cell)stepable;
				if(cell.generation < oldestGeneration){
					oldestGeneration = cell.generation;
				}
			}
			if(stepable instanceof Cell2){
				Cell2 cell = (Cell2)stepable;
				if(cell.generation < oldestGeneration){
					oldestGeneration = cell.generation;
				}
			}
		}
		return oldestGeneration;
	}
	
	private int getGenerationCount(int generation){
		int cellCount = 0;
		for(Stepable stepable : ArtificialLife.stepList){
			if(stepable instanceof Cell){
				Cell cell = (Cell)stepable;
				if(cell.generation == generation){
					cellCount ++;
				}
			}
			if(stepable instanceof Cell2){
				Cell2 cell = (Cell2)stepable;
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
			infoText += "organs = "+followedCell.organList.size()+"<br>";
			
			String organInfoText = "<html>";
			for(Organ organ : followedCell.organList){
				organInfoText = organInfoText+organ+"<br>";
			}
			organInfoText = organInfoText+"</html>";
			organInfoLabel.setText(organInfoText);
		}
		
		infoText += "</html>";
		
		infoLabel.setText(infoText);
		
		repaint();
	}
	
}