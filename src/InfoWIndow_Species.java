import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.*;

import general.Tuple;

class InfoWindow_Species extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private static JPanel speciesSelectPanel = new JPanel();
	private static JComboBox<String> speciesSelectBox = new JComboBox<String>();
	private static JButton refreshButton = new JButton("Refresh");
	private static JPanel infoPanel = new JPanel();
	private static JLabel infoLabel = new JLabel();
	
	private static LinkedList<Species> speciesList = new LinkedList<Species>();
	private static Species selectedSpecies = null;
	
	private static String infoText() {
		String infoText = "<html>";
		if(selectedSpecies == null) {
			infoText += "No species selected"+"<br>";
		} else {
			infoText += "Species: "+selectedSpecies.getFullName()+"<br>";
			infoText += "<br>";
			infoText += selectedSpecies.getInfo();
		}
		infoText += "</html>";
		return infoText;
	}
	
	InfoWindow_Species(){
		setTitle("Species Info");
		setSize(512, 512);
		setLayout(new BorderLayout());
		add(speciesSelectPanel, BorderLayout.NORTH);
		speciesSelectPanel.setLayout(new BorderLayout());
		speciesSelectPanel.add(speciesSelectBox, BorderLayout.CENTER);
		speciesSelectBox.addActionListener(this);
		speciesSelectPanel.add(refreshButton, BorderLayout.EAST);
		refreshButton.addActionListener(this);
		add(infoPanel, BorderLayout.CENTER);
		infoPanel.add(infoLabel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == refreshButton) {
			update(); // TODO - make this call update() in the main loop, so as to avoid comod issues.
		}
		if(e.getSource() == speciesSelectBox) {
			updateSpeciesInfo();
		}
	}
	
	public void update() {
		speciesList.clear();
		speciesSelectBox.removeAllItems();
		LinkedList<Tuple<Species, Integer>> speciesCountList = Species.speciesCountList(Integer.MAX_VALUE, 0);
		for(Tuple<Species, Integer> speciesCount : speciesCountList) {
			speciesList.add(speciesCount.e1);
			speciesSelectBox.addItem(speciesCount.e1.getDisplayName()+" ("+speciesCount.e2+" cell)");
		}
		updateSpeciesInfo();
		
		// Repaint once label text is updated. //
		repaint();
	}
	
	private void updateSpeciesInfo() {
		int selectedIndex = speciesSelectBox.getSelectedIndex();
		selectedSpecies = speciesList.get(selectedIndex);
		infoLabel.setText(infoText());
	}
}