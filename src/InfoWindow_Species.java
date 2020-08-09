import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.*;

import general.Tuple;

class InfoWindow_Species extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private static JPanel datasheetPanel = new JPanel();
	private static int speciesListLength = 10;
	private static int speciesDataLength = 4;
	private static JLabel[] dataLabel_speciesName = new JLabel[speciesListLength];
	private static JLabel[] dataLabel_cellCount = new JLabel[speciesListLength];
	private static JLabel[] dataLabel_neurons = new JLabel[speciesListLength];
	private static JButton[] actionButton_highlight = new JButton[speciesListLength];
	
	private static LinkedList<Species> speciesList = new LinkedList<Species>();
	
	InfoWindow_Species(){
		setTitle("Species Info");
		setSize(512, 512);
		setLayout(new BorderLayout());
		add(datasheetPanel, BorderLayout.CENTER);
		String[] columnTitles = {"Species", "Population", "Neurons", "Actions"};
		JLabel[][] dataLabelLists = {dataLabel_speciesName, dataLabel_cellCount, dataLabel_neurons};
		String[] actionButtonLabels = {"H"};
		JButton[][] actionButtonLists = {actionButton_highlight};
		setupDatasheet(columnTitles, dataLabelLists, actionButtonLabels, actionButtonLists);
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
		datasheetPanel.setLayout(new GridLayout(speciesListLength + 1, speciesDataLength));
		for(String columnTitle : columnTitles) {
			datasheetPanel.add(new JLabel(columnTitle));
		}
		for(int row = 0; row < speciesListLength; row ++) {
			// Add the i'th row of data labels. //
			for(int col = 0; col < dataLabelLists.length; col ++) {
				dataLabelLists[col][row] = new JLabel();
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
		LinkedList<Tuple<Species, Integer>> speciesCountList = Species.speciesCountList(Integer.MAX_VALUE, 0);
		for(Tuple<Species, Integer> speciesCount : speciesCountList) {
			speciesList.add(speciesCount.e1);
		}
		
		for(int i = 0; i < speciesListLength; i ++) {
			if(i < speciesList.size()) {
				Species species = speciesList.get(i);
				dataLabel_speciesName[i].setText(species.getDisplayName());
				dataLabel_cellCount[i].setText(""+speciesCountList.get(i).e2);
				dataLabel_neurons[i].setText("C="+species.neuronCount_concept()+"; M="+species.neuronCount_memory());
				actionButton_highlight[i].setEnabled(true);
			} else {
				dataLabel_speciesName[i].setText("---");
				dataLabel_cellCount[i].setText("---");
				dataLabel_neurons[i].setText("---");
				actionButton_highlight[i].setEnabled(false);
			}
		}
		
		// Repaint once label text is updated. //
		repaint();
	}
}