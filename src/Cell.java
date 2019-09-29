import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedList;

import files.TextFileHandler;

class Cell extends WorldObject implements Stepable {
	static float colorMutationSpeed = 0.4f;
	static int initialMutations;
	static int maxMutations;
	static int maxStoredEnergy;
	static int birthEnergyRequirement;
	static int energyUponBirth;
	static int energyCostPerTick;
	static int energyCostPerNeuron;
	static float mutationChance_addConnection;
	static float mutationChance_addNeuron;
	static float mutationChance_changeNeuronThreshold;
	static float mutationChance_changeFiringStrength;
	static float mutationChance_collapseConection;
	static float mutationChance_removeConnection;
	static float mutationChance_splitConnection;
	static float mutationChance_addOrgan;
	static float mutationChance_removeOrgan;
	
	// Cell variables - remember to update the printToFile(String filename) and Cell(File file) methods when changing these! //
	int generation = 0;
	int children = 0;
	Direction facing = M.chooseRandom(Direction.values());
	int energy = energyUponBirth;
	int lifetime = 0;
	int lifetimeFoodEaten = 0;

	Color color;
	float birthChance;
	float mutationChance;
	
	// Organs //
	LinkedList<Organ> organList = new LinkedList<Organ>();
	
	// List of all neurons the cell has, including sensory and motor neurons. //
	LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
	
	// Sensory neurons. //
	LinkedList<Neuron> sensoryNeuronList = new LinkedList<Neuron>();
	
	// Motor neurons. //
	LinkedList<Neuron> motorNeuronList = new LinkedList<Neuron>();
	
	// List of non-sensory, non-motor neurons the cell has. //
	LinkedList<Neuron> conceptNeuronList = new LinkedList<Neuron>();
	
	
	
	private static Neuron chooseNeuronToMutate(LinkedList<Neuron> list){
		if(list.isEmpty()){
			return null;
		} else {
			return M.chooseRandom(list);
		}
	}
	
	static Cell cloneAndMutate(Cell cellToClone){
		int i = 0;
		Cell cloneCell = new Cell(cellToClone);
		cloneCell.generation ++;
		
		cloneCell.mutateBiology();
		
		for(Organ organ : cloneCell.organList){
			if(M.roll(organ.mutationChance)){
				organ.mutate();
			}
		}
		cloneCell.mutateOrgans();
		
		do {
			cloneCell.mutateNeuralNet();
			i ++;
		} while(M.roll(cellToClone.mutationChance) && i < maxMutations);
		return cloneCell;
	}
	
	private static Color mutateColor(Color color){
		// Mutate the colours. //
		float[] colorComponentList = color.getColorComponents(null);
		float red = colorComponentList[0] + (float)M.rand(-colorMutationSpeed, colorMutationSpeed);
		float green = colorComponentList[1] + (float)M.rand(-colorMutationSpeed, colorMutationSpeed);
		float blue = colorComponentList[2] + (float)M.rand(-colorMutationSpeed, colorMutationSpeed);
		red = Math.min(Math.max(0, red), 1);
		green = Math.min(Math.max(0, green), 1);
		blue = Math.min(Math.max(0, blue), 1);
		
		// Attempt to normalise the colours to full saturation and value. //
		if(red >= green && red >= blue){
			red = 1.0f;
		} else if (green >= red && green >= blue){
			green = 1.0f;
		} else if(blue >= red && blue >= green){
			blue = 1.0f;
		}
		if(red <= green && red <= blue){
			red = 0.0f;
		} else if (green <= red && green <= blue){
			green = 0.0f;
		} else if(blue <= red && blue <= green){
			blue = 0.0f;
		}
		
		return new Color(red, green, blue);
	}
	
	Cell(){
		setupBiology();
		setupOrgans();
		for(int i = 0; i < initialMutations; i ++){
			mutateNeuralNet();
		}
	}
	
	Cell(File file){
		// Read the file and split into the three sets of data: variables, organs and neurons. //
		LinkedList<String> lineList = TextFileHandler.readEntireFile(file.getPath());
		String[] data = lineList.remove().split(";");
		LinkedList<String> organData = new LinkedList<String>();
		while(!lineList.getFirst().startsWith("N")){
			organData.add(lineList.remove());
		}
		LinkedList<String> neuronData = lineList;
		
		// Load the cell variables. //
		loadVariables(data);
		
		// Load the neurons (before we set up connections or assign motor/sensory neurons). //
		loadNeurons(neuronData);
		
		// Load the organ data. //
		loadOrgans(organData);
		
		// Assign sensory/motor neurons. //
		for(Organ organ : organList){
			sensoryNeuronList.addAll(organ.getSensoryNeurons());
			motorNeuronList.addAll(organ.getMotorNeurons());
		}
		
		// Assign concept neurons. //
		conceptNeuronList.addAll(neuronList);
		conceptNeuronList.removeAll(sensoryNeuronList);
		conceptNeuronList.removeAll(motorNeuronList);
	}
	
	Cell(Cell cell){
		// Clone the cell's physical characteristics (color). //
		generation = cell.generation;
		color = cell.color;
		birthChance = cell.birthChance;
		mutationChance = cell.mutationChance;
		
		// Clone the cell's organs. //
		for(Organ organ : cell.organList){
			Organ newOrgan = organ.clone();
			newOrgan.owner = this;
			organList.add(newOrgan);
		}
		
		// Clone the cell's sensory and motor neurons. //
		for(Organ organ : organList){
			motorNeuronList.addAll(organ.getMotorNeurons());
			sensoryNeuronList.addAll(organ.getSensoryNeurons());
			neuronList.addAll(organ.getNeurons());
		}
		
		// Clone the cell's concept neurons. //
		for(Neuron conceptNeuron : cell.conceptNeuronList){
			Neuron newConceptNeuron = conceptNeuron.clone();
			conceptNeuronList.add(newConceptNeuron);
			neuronList.add(newConceptNeuron);
		}
		
		// Clone the cell's neuron connections. //
		for(int i = 0; i < neuronList.size(); i ++){
			neuronList.get(i).cloneConnections(cell.neuronList.get(i), cell.neuronList, neuronList);
		}
	}
	
	void attemptBirth(Direction direction, int energyPassedToChild){
		int energyCost = birthEnergyRequirement + energyPassedToChild;
		if(energy > energyCost){
			Point p = M.add(direction.getVector(), location);
			Cell child = cloneAndMutate(this);
			boolean placedSuccessfully = Display.place(child, p);
			if(placedSuccessfully){
				energy -= energyCost;
				child.energy = energyPassedToChild;
				children ++;
			}
		}
	}
	
	boolean displace(){
		Point targetPoint = getAdjacentLocation(facing);
		Display.wrapPoint(targetPoint);
		WorldObject target = Display.grid[targetPoint.x][targetPoint.y];
		if(target != null){
			return target.interact(this, Interaction.DISPLACE);
		} else {
			return false;
		}
	}
	
	void drawSenses(Graphics2D g){
		for(Organ organ : organList){
			organ.drawSenses(g);
		}
	}
	
	boolean eat() {
		Point targetPoint = getAdjacentLocation(facing);
		Display.wrapPoint(targetPoint);
		WorldObject target = Display.grid[targetPoint.x][targetPoint.y];
		if(target != null){
			return target.interact(this, Interaction.EAT);
		} else {
			return false;
		}
	}
	
	public Color getColor() {
		return color;
	}
	
	private LinkedList<Neuron> getValidConnectionSources(){
		LinkedList<Neuron> validSourceList = new LinkedList<Neuron>();
		validSourceList.addAll(conceptNeuronList);
		validSourceList.addAll(sensoryNeuronList);
		return validSourceList;
	}
	
	private LinkedList<Neuron> getValidConnectionTargets(){
		LinkedList<Neuron> validTargetList = new LinkedList<Neuron>();
		validTargetList.addAll(conceptNeuronList);
		validTargetList.addAll(motorNeuronList);
		return validTargetList;
	}

	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType) {
		switch (interactionType) {
		case PUSH:
			push(interacter, this);
			return true;
		case GIVE_FOOD_ENERGY:
			int foodValue = Food.energyGainPerFood;
			energy = Math.min(energy + foodValue, maxStoredEnergy);
			lifetimeFoodEaten += foodValue;
			return true;
		case KILL:
			kill();
			return true;
		case PULL:
			return pull(interacter, this);
		case DISPLACE:
			return displace(interacter, this);
		default:
			return false;
		}
	}
	
	void kill(){
		Display.stepList.remove(this);
		Display.grid[location.x][location.y] = null;
	}
	
	private Color loadColorData(String data){
		String[] colData = data.substring(data.indexOf("=") + 1).split(",");
		int r = Integer.parseInt(colData[0]);
		int g = Integer.parseInt(colData[1]);
		int b = Integer.parseInt(colData[2]);
		return new Color(r, g, b);
	}
	
	private float loadFloatData(String data){
		return Float.parseFloat(data.substring(data.indexOf("=") + 1));
	}
	
	private int loadIntData(String data){
		return Integer.parseInt(data.substring(data.indexOf("=") + 1));
	}
	
	private void loadNeurons(LinkedList<String> data){
		int i = 0;
		for(String line : data){
			if(line.startsWith("N")){
				Neuron neuron = new Neuron();
				neuronList.add(neuron);
				line = line.substring(line.indexOf(":") + 1);
				neuron.isThresholdUpperLimit = line.charAt(0) == '<';
				String thresholdString = line.substring(1, (line.indexOf(":")));
				String fireingStrengthString = line.substring(line.indexOf(":") + 1);
				neuron.threshold = Float.parseFloat(thresholdString);
				neuron.firingStrength = Float.parseFloat(fireingStrengthString);
				i ++;
			}
		}
		
		// Load the neuron connections. //
		i = 0;
		Neuron neuron = null;
		while(!data.isEmpty()){
			String line = data.remove();
			if(line.startsWith("N")){
				neuron = neuronList.get(i);
				i ++;
			} else if(line.startsWith("=>")){
				int targetNeuronIndex = Integer.parseInt(line.substring(3));
				neuron.addConnection(neuronList.get(targetNeuronIndex));
			}
		}
	}
	
	private void loadOrgans(LinkedList<String> data){
		Organ organ = null;
		for(String line : data){
			if(line.startsWith("Organ:")){
				String[] organData = line.split(":");
				organ = Organ.load(organData);
				organ.owner = this;
				organList.add(organ);
			} else if(organ != null){
				String[] organData = line.split("=N");
				Neuron neuron = neuronList.get(Integer.parseInt(organData[1]));
				organ.setNeuron(organData[0], neuron);
			}
		}
	}
	
	private void loadVariables(String[] data){
		location = new Point();
		for(int i = 0; i < data.length; i ++){
			if(data[i].startsWith("generation=")){
				generation = loadIntData(data[i]);
			} else if(data[i].startsWith("children=")){
				children = loadIntData(data[i]);
			} else if(data[i].startsWith("x=")){
				location.x = loadIntData(data[i]);
			} else if(data[i].startsWith("y=")){
				location.y = loadIntData(data[i]);
			} else if(data[i].startsWith("energy=")){
				energy = loadIntData(data[i]);
			} else if(data[i].startsWith("lifetime=")){
				lifetime = loadIntData(data[i]);
			} else if(data[i].startsWith("lifetimeFoodEaten=")){
				lifetimeFoodEaten = loadIntData(data[i]);
			} else if(data[i].startsWith("color=")){
				color = loadColorData(data[i]);
			} else if(data[i].startsWith("birthChance=")){
				birthChance = loadFloatData(data[i]);
			} else if(data[i].startsWith("mutationChance=")){
				mutationChance = loadFloatData(data[i]);
			}
		}
	}
	
	boolean moveTo(Point p){
		Display.wrapPoint(p);
		if(Display.grid[p.x][p.y] == null){
			setLocation(p);
			return true;
		} else {
			return Display.grid[p.x][p.y].interact(this, Interaction.PUSH);
		}
	}
	
	private void mutate_addConnection(){
		Neuron sourceNeuron = chooseNeuronToMutate(getValidConnectionSources());
		Neuron targetNeuron = chooseNeuronToMutate(getValidConnectionTargets());
		if(sourceNeuron != null && targetNeuron != null){
			if(sourceNeuron != targetNeuron && !sourceNeuron.hasConnectionTo(targetNeuron)){
				sourceNeuron.addConnection(targetNeuron);
			}
		}
	}
	
	private void mutate_addNeuron(){
		Neuron targetNeuron = chooseNeuronToMutate(getValidConnectionTargets());
		if(targetNeuron != null){
			Neuron newNeuron = new Neuron();
			newNeuron.addConnection(targetNeuron);
			neuronList.add(newNeuron);
			conceptNeuronList.add(newNeuron);
		}
	}
	
	private void mutate_changeNeuronThreshold(){
		Neuron neuron = chooseNeuronToMutate(neuronList);
		neuron.threshold = M.randf(1);
		neuron.isThresholdUpperLimit = (M.randf(1) < 0.5f);
	}
	
	private void mutate_changeFiringStrength(){
		Neuron neuron = chooseNeuronToMutate(neuronList);
		neuron.firingStrength = M.randf(1);
	}
	
	private void mutate_collapseConection(){
		if(!conceptNeuronList.isEmpty()){
			Neuron sourceNeuron = chooseNeuronToMutate(conceptNeuronList);
			if(!sourceNeuron.connectionList.isEmpty()){
				Neuron targetNeuron = chooseNeuronToMutate(sourceNeuron.connectionList);
				if(conceptNeuronList.contains(targetNeuron)){
					for(Neuron n : sourceNeuron.connectionList){
						if(n != targetNeuron && !targetNeuron.hasConnectionTo(n)){
							targetNeuron.addConnection(n);
						}
					}
					for(Neuron n : neuronList){
						if(n.hasConnectionTo(sourceNeuron)){
							n.connectionList.remove(sourceNeuron);
							if(n != sourceNeuron && n != targetNeuron && !n.hasConnectionTo(targetNeuron)){
								n.addConnection(targetNeuron);
							}
						}
					}
					conceptNeuronList.remove(sourceNeuron);
					neuronList.remove(sourceNeuron);
				}
			}
		}
	}
	
	private void mutate_removeConnection(){
		Neuron sourceNeuron = chooseNeuronToMutate(neuronList);
		if(!sourceNeuron.connectionList.isEmpty()){
			M.removeRandom(sourceNeuron.connectionList);
		}
	}
	
	private void mutate_splitConnection(){
		Neuron sourceNeuron = chooseNeuronToMutate(getValidConnectionSources());
		if(sourceNeuron != null){
			if(!sourceNeuron.connectionList.isEmpty()){
				Neuron newNeuron = sourceNeuron.splitConnection(M.chooseRandom(sourceNeuron.connectionList));
				neuronList.add(newNeuron);
				conceptNeuronList.add(newNeuron);
			}
		}
	}
	
	private void mutateBiology(){
		color = mutateColor(color);
		birthChance = M.mutateFloat(birthChance);
		mutationChance = M.mutateFloat(mutationChance);
	}
	
	private void mutateNeuralNet(){
		double weight = mutationChance_addConnection + mutationChance_addNeuron
		+ mutationChance_changeNeuronThreshold + mutationChance_changeFiringStrength 
		+ mutationChance_collapseConection + mutationChance_splitConnection;
		double dice = Math.random()*weight;
		dice -= mutationChance_addConnection;
		if(dice < 0){
			mutate_addConnection();
			return;
		}
		dice -= mutationChance_addNeuron;
		if(dice < 0){
			mutate_addNeuron();
			return;
		}
		dice -= mutationChance_changeNeuronThreshold;
		if(dice < 0){
			mutate_changeNeuronThreshold();
			return;
		}
		dice -= mutationChance_changeFiringStrength;
		if(dice < 0){
			mutate_changeFiringStrength();
			return;
		}
		dice -= mutationChance_collapseConection;
		if(dice < 0){
			mutate_collapseConection();
			return;
		}
		dice -= mutationChance_removeConnection;
		if(dice < 0){
			mutate_removeConnection();
			return;
		}
		dice -= mutationChance_splitConnection;
		if(dice < 0){
			mutate_splitConnection();
			return;
		}
	}
	
	private void mutateOrgans(){
		// Chance to remove an organ. //
		if(M.roll(mutationChance_removeOrgan)){
			if(!organList.isEmpty()){
				Organ organ = M.chooseRandom(organList);
				if(organ.isMutatable() && M.roll(organ.mutationChance)){
					organList.remove(organ);
					for(Neuron neuron : organ.getNeurons()){
						removeNeuron(neuron);
					}
				}
			}
		}
		
		// Chance to add a new organ. //
		if(M.roll(mutationChance_addOrgan)){
			Organ organ = Organ.newRandomOrgan();
			organ.owner = this;
			organ.setupNeurons();
			sensoryNeuronList.addAll(organ.getSensoryNeurons());
			motorNeuronList.addAll(organ.getMotorNeurons());
			neuronList.addAll(organ.getNeurons());
			organList.add(organ);
		}
	}
	
	void printToFile(String filename){
		PrintWriter pw = TextFileHandler.startWritingToFile(filename);
		// Print variables in first line, separated by ";". //
		pw.print(";generation="+generation);
		pw.print(";children="+children);
		pw.print(";x="+location.x);
		pw.print(";y="+location.y);
		pw.print(";energy="+energy);
		pw.print(";lifetime="+lifetime);
		pw.print(";lifetimeFoodEaten="+lifetimeFoodEaten);
		pw.print(";color="+color.getRed()+","+color.getBlue()+","+color.getGreen());
		pw.print(";birthChance="+birthChance);
		pw.print(";mutationChance="+mutationChance);
		pw.println();
		// Print organ data. //
		for(Organ organ : organList){
			organ.print(pw, neuronList);
		}
		// Print neuron data. //
		for(Neuron neuron : neuronList){
			neuron.print(pw, neuronList);
		}
		pw.close();
	}
	
	boolean pull() {
		Point targetPoint = getAdjacentLocation(facing);
		Display.wrapPoint(targetPoint);
		WorldObject target = Display.grid[targetPoint.x][targetPoint.y];
		if(target != null){
			return target.interact(this, Interaction.PULL);
		} else {
			return false;
		}
	}
	
	private void removeNeuron(Neuron neuronToRemove){
		neuronList.remove(neuronToRemove);
		sensoryNeuronList.remove(neuronToRemove);
		motorNeuronList.remove(neuronToRemove);
		for(Neuron neuron : neuronList){
			neuron.connectionList.remove(neuronToRemove);
		}
	}
	
	private void setupBiology(){
		color = mutateColor(new Color(M.randf(1.0f), M.randf(1.0f), M.randf(1.0f)));
		birthChance = M.randf(1.0f);
		mutationChance = M.randf(1.0f);
	}
	
	private void setupOrgans(){
		Organ defaultAgeOrgan = new Organ_Age();
		defaultAgeOrgan.owner = this;
		organList.add(defaultAgeOrgan);
		
		Organ_Eye defaultEyeOrgan = new Organ_Eye(10, 0);
		defaultEyeOrgan.owner = this;
		organList.add(defaultEyeOrgan);
		
		Organ_Hunger defaultHungerOrgan = new Organ_Hunger();
		defaultHungerOrgan.owner = this;
		organList.add(defaultHungerOrgan);
		
		Organ_Interaction defaultMovementOrgan = new Organ_Interaction();
		defaultMovementOrgan.owner = this;
		organList.add(defaultMovementOrgan);
		
		Organ_Reproduction defaultReproductionOrgan = new Organ_Reproduction(Cell.energyUponBirth);
		defaultReproductionOrgan.owner = this;
		organList.add(defaultReproductionOrgan);
		
		for(Organ organ : organList){
			organ.setupNeurons();
			sensoryNeuronList.addAll(organ.getSensoryNeurons());
			motorNeuronList.addAll(organ.getMotorNeurons());
			neuronList.addAll(organ.getNeurons());
		}
		
		// Default movement reflex. //
		defaultMovementOrgan.motorNeuronList[Organ_Interaction.interaction_move].isThresholdUpperLimit = false;
		defaultMovementOrgan.motorNeuronList[Organ_Interaction.interaction_move].threshold = 0.5f;
		
		// Default eat reflex. //
		defaultEyeOrgan.sensoryNeuron_eye_distance.addConnection(defaultMovementOrgan.motorNeuronList[Organ_Interaction.interaction_eat]);
		defaultMovementOrgan.motorNeuronList[Organ_Interaction.interaction_eat].isThresholdUpperLimit = true;
		defaultMovementOrgan.motorNeuronList[Organ_Interaction.interaction_eat].threshold = 0.15f;
		
		// Default reproduction reflex. //
		defaultHungerOrgan.sensoryNeuron_hunger.addConnection(defaultReproductionOrgan.motorNeuron_spawn);
		defaultReproductionOrgan.motorNeuron_spawn.isThresholdUpperLimit = false;
		defaultReproductionOrgan.motorNeuron_spawn.threshold = 0.9f;
	}
	
	@Override
	public void step(){
		// Set sensory neuron output strength. //
		for(Organ organ : organList){
			organ.stepSensoryNeurons();
		}
		
		// Recalculate input strength for all neurons. //
		for(Neuron neuron : neuronList){
			// Set input strenth to off. //
			neuron.currentInputStrength = 1;
		}
		for(Neuron neuron : neuronList){
			// Push output strength to connections if firing. //
			neuron.calculateConnections();
		}
		
		// Decide whether each neuron should be firing based on the new input strength. //
		for(Neuron neuron : neuronList){
			neuron.isFiring = neuron.isThresholdCrossed();
		}
		// Sensory neurons should always be firing. //
		for(Organ organ : organList){
			organ.fireSensoryNeurons();
		}
		
		// Decide on action based upon which motor neurons are firing. //
		for(Organ organ : organList){
			organ.stepMotorNeurons();
		}
		
		// Life and energy. //
		lifetime ++;
		energy -= energyCostPerTick + neuronList.size()*energyCostPerNeuron;
		if(energy < 0){
			kill();
		}
	}
}