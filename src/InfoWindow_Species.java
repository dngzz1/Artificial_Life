import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.*;

import general.Tuple;

class InfoWindow_Species extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	// Datasheet //
	private static JPanel datasheetPanel = new JPanel();
	private static int speciesListLength = 10;
	
	// Columns //
	private static String[] columnTitles = {"Species", "Population", "Neurons", "Predation", "Energy Use", "Attack Strength", "Bite Size", "Build Strength", "Energy Capacity", "Max HP", "Actions"};
	private static JLabel[] dataLabel_speciesName = new JLabel[speciesListLength];
	private static JLabel[] dataLabel_cellCount = new JLabel[speciesListLength];
	private static JLabel[] dataLabel_neurons = new JLabel[speciesListLength];
	private static JLabel[] dataLabel_predation = new JLabel[speciesListLength];
	private static JLabel[] dataLabel_energyUse = new JLabel[speciesListLength];
	private static JLabel[] dataLabel_attackStrength = new JLabel[speciesListLength];
	private static JLabel[] dataLabel_biteSize = new JLabel[speciesListLength];
	private static JLabel[] dataLabel_buildStrength = new JLabel[speciesListLength];
	private static JLabel[] dataLabel_energyCapacity = new JLabel[speciesListLength];
	private static JLabel[] dataLabel_hp = new JLabel[speciesListLength];
	private static JLabel[][] dataLabelLists = {dataLabel_speciesName, dataLabel_cellCount, dataLabel_neurons, dataLabel_predation, dataLabel_energyUse, 
			dataLabel_attackStrength, dataLabel_biteSize, dataLabel_buildStrength, dataLabel_energyCapacity, dataLabel_hp};
	
	// Action Buttons //
	private static String[] actionButtonLabels = {"H"};
	private static JButton[] actionButton_highlight = new JButton[speciesListLength];
	private static JButton[][] actionButtonLists = {actionButton_highlight};
	
	private static LinkedList<Species> speciesList = new LinkedList<Species>();
	
	private static String getInfo(Species species, Metrics.CellMetric<Integer> metric) {
		// This gives an array containing the min=data[0], median=data[1] and max=data[2]. //  
		int[] data = Metrics.getMinMedMax(metric, new Metrics.MatchSpeciesCondition(species));
		
		// Put this data into a string. //
		return infoDataToString(data);
	}
	
	private static String getInfo_predation(Species species) {
		int totalFoodEaten = Metrics.getTotal(Metrics.lifetimeFoodEatenMetric, new Metrics.MatchSpeciesCondition(species));
		int totalFoodEatenByPredation = Metrics.getTotal(Metrics.lifetimeFoodEatenByPredationMetric, new Metrics.MatchSpeciesCondition(species));
		
		double proportionFoodByPredation = (double)totalFoodEatenByPredation/(double)totalFoodEaten;
		return M.toString(proportionFoodByPredation*100, 3)+"%";
	}
	
	private static String infoDataToString(int[] data) {
		return M.abbreviate(data[0])+" : "+M.abbreviate(data[1])+" : "+M.abbreviate(data[2]);
	}
	
	InfoWindow_Species(){
		setTitle("Species Info");
		setSize(512, 512);
		setLayout(new BorderLayout());
		add(datasheetPanel, BorderLayout.CENTER);
		setupDatasheet(columnTitles, dataLabelLists, actionButtonLabels, actionButtonLists);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for(int i = 0; i < speciesListLength; i ++) {
			if(e.getSource() == actionButton_highlight[i]) {
				if(i < speciesList.size()) {
					Display.highlightSpecies(speciesList.get(i));
				}
			}
		}
	}
	
	private void setupDatasheet(String[] columnTitles, JLabel[][] dataLabelLists, String[] actionButtonLabels, JButton[][] actionButtonLists) {
		datasheetPanel.setLayout(new GridLayout(speciesListLength + 1, columnTitles.length));
		for(String columnTitle : columnTitles) {
			datasheetPanel.add(new JLabel(columnTitle, JLabel.CENTER));
		}
		for(int row = 0; row < speciesListLength; row ++) {
			// Add the i'th row of data labels. //
			for(int col = 0; col < dataLabelLists.length; col ++) {
				dataLabelLists[col][row] = new JLabel("", JLabel.CENTER);
				datasheetPanel.add(dataLabelLists[col][row]);
			}
			// Add the i'th set of action buttons. //
			JPanel actionButtonPanel = new JPanel();
			datasheetPanel.add(actionButtonPanel);
			for(int action = 0; action < actionButtonLists.length; action ++) {
				actionButtonLists[action][row] = new JButton(actionButtonLabels[action]);
				actionButtonLists[action][row].addActionListener(this);
				actionButtonPanel.add(actionButtonLists[action][row]);
			}
			
		}
	}
	
	public void update() {
		speciesList.clear();
		LinkedList<Tuple<Species, Integer>> speciesCountList = Species.speciesCountList(Integer.MAX_VALUE, 2);
		for(Tuple<Species, Integer> speciesCount : speciesCountList) {
			speciesList.add(speciesCount.e1);
		}
		
		for(int i = 0; i < speciesListLength; i ++) {
			if(i < speciesList.size()) {
				Species species = speciesList.get(i);
				dataLabel_speciesName[i].setText(species.getDisplayName());
				dataLabel_cellCount[i].setText(""+speciesCountList.get(i).e2);
				dataLabel_neurons[i].setText("C="+species.neuronCount_concept()+"; M="+species.neuronCount_memory());
				dataLabel_predation[i].setText(getInfo_predation(species));
				dataLabel_energyUse[i].setText(getInfo(species, Metrics.energyUseMetric));
				dataLabel_attackStrength[i].setText(getInfo(species, Metrics.attackStrengthMetric));
				dataLabel_biteSize[i].setText(getInfo(species, Metrics.biteSizeMetric));
				dataLabel_buildStrength[i].setText(getInfo(species, Metrics.buildStrengthMetric));
				dataLabel_energyCapacity[i].setText(getInfo(species, Metrics.energyCapacityMetric));
				dataLabel_hp[i].setText(getInfo(species, Metrics.hpMetric));
				actionButton_highlight[i].setEnabled(true);
			} else {
				dataLabel_speciesName[i].setText("---");
				dataLabel_cellCount[i].setText("---");
				dataLabel_neurons[i].setText("---");
				dataLabel_predation[i].setText("---");
				dataLabel_energyUse[i].setText("---");
				dataLabel_attackStrength[i].setText("---");
				dataLabel_biteSize[i].setText("---");
				dataLabel_buildStrength[i].setText("---");
				dataLabel_energyCapacity[i].setText("---");
				dataLabel_hp[i].setText("---");
				actionButton_highlight[i].setEnabled(false);
			}
		}
		
		// Repaint once label text is updated. //
		repaint();
	}
}