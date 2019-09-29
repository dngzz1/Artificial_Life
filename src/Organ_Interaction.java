import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.LinkedList;

class Organ_Interaction extends Organ {
	static int energyCostToRotate = 3;
	static int energyCostToMove = 5;
	
	Neuron[] motorNeuronList = new Neuron[6];
	static final int interaction_rotateACW = 0;
	static final int interaction_rotateCW = 1;
	static final int interaction_move = 2;
	static final int interaction_eat = 3;
	static final int interaction_pull = 4;
	static final int interaction_displace = 5;
	
	Organ_Interaction(){
		;
	}
	
	public Organ_Interaction clone() {
		Organ_Interaction organ = new Organ_Interaction();
		organ.mutationChance = mutationChance;
		for(int i = 0; i < motorNeuronList.length; i ++){
			organ.motorNeuronList[i] = motorNeuronList[i].clone();
		}
		return organ;
	}
	
	void drawSenses(Graphics2D g){
		;
	}
	
	@Override
	String getLabel(Neuron neuron){
		if(neuron == motorNeuronList[interaction_rotateACW]){
			return "M(acw)";
		}
		if(neuron == motorNeuronList[interaction_rotateCW]){
			return "M(cw)";
		}
		if(neuron == motorNeuronList[interaction_move]){
			return "M(move)";
		}
		if(neuron == motorNeuronList[interaction_eat]){
			return "M(eat)";
		}
		if(neuron == motorNeuronList[interaction_pull]){
			return "M(pull)";
		}
		if(neuron == motorNeuronList[interaction_displace]){
			return "M(displace)";
		}
		return "";
	}
	
	LinkedList<Neuron> getMotorNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		for(Neuron neuron : motorNeuronList){
			neuronList.add(neuron);
		}
		return neuronList;
	}
	
	LinkedList<Neuron> getSensoryNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		return neuronList;
	}
	
	@Override
	boolean isMutatable(){
		return false;
	}
	
	Organ_Interaction loadNew(){
		return new Organ_Interaction();
	}
	
	Organ_Interaction loadNew(String[] data){
		return new Organ_Interaction();
	}
	
	public void mutate() {
		;
	}
	
	void print(PrintWriter pw, LinkedList<Neuron> cellNeuronList){
		pw.println("Organ:Movement:");
		pw.println("M(acw)=N"+cellNeuronList.indexOf(motorNeuronList[interaction_rotateACW]));
		pw.println("M(cw)=N"+cellNeuronList.indexOf(motorNeuronList[interaction_rotateCW]));
		pw.println("M(move)=N"+cellNeuronList.indexOf(motorNeuronList[interaction_move]));
		pw.println("M(eat)=N"+cellNeuronList.indexOf(motorNeuronList[interaction_move]));
		pw.println("M(pull)=N"+cellNeuronList.indexOf(motorNeuronList[interaction_pull]));
		pw.println("M(displace)=N"+cellNeuronList.indexOf(motorNeuronList[interaction_displace]));
	}
	
	void setNeuron(String label, Neuron neuron){
		if(label.equals("M(acw)")){
			motorNeuronList[interaction_rotateACW] = neuron;
		} else if(label.equals("M(cw)")){
			motorNeuronList[interaction_rotateCW] = neuron;
		} else if(label.equals("M(move)")){
			motorNeuronList[interaction_move] = neuron;
		} else if(label.equals("M(eat)")){
			motorNeuronList[interaction_eat] = neuron;
		} else if(label.equals("M(pull)")){
			motorNeuronList[interaction_pull] = neuron;
		} else if(label.equals("M(displace)")){
			motorNeuronList[interaction_displace] = neuron;
		}
	}
	
	void setupNeurons() {
		for(int i = 0; i < motorNeuronList.length; i ++){
			motorNeuronList[i] = new Neuron();
		}
	}
	
	void stepMotorNeurons(){
		// Decide which action to take. //
		
		// List each fired motor neuron by index. // 
		LinkedList<Neuron> actionList = new LinkedList<Neuron>();
		for(int i = 0; i < motorNeuronList.length; i ++){
			if(motorNeuronList[i].isFiring){
				actionList.add(motorNeuronList[i]);
			}
		}
		
		// Attempt each action in order of neuron input strength. //
		while(!actionList.isEmpty()){
			Neuron strongestNeuron = null;
			float strongestInputStrength = 1.0f;
			
			for(Neuron neuron : actionList){
				if(neuron.currentInputStrength <= strongestInputStrength){
					strongestNeuron = neuron;
					strongestInputStrength = neuron.currentInputStrength;
				}
			}
			
			actionList.remove(strongestNeuron);
			boolean successful = takeAction(M.indexOf(motorNeuronList, strongestNeuron));
			if(successful){
				break;
			}
		}
	}
	
	void stepSensoryNeurons() {
		;
	}
	
	boolean takeAction(int actionType){
		switch (actionType) {
		case interaction_rotateACW:
			owner.facing = owner.facing.rotateACW();
			owner.energy -= energyCostToRotate;
			return true;
		case interaction_rotateCW:
			owner.facing = owner.facing.rotateCW();
			owner.energy -= energyCostToRotate;
			return true;
		case interaction_move:
			boolean moved = owner.moveTo(M.add(owner.facing.getVector(), owner.getLocation()));
			if(moved){
				owner.energy -= energyCostToMove;
			}
			return moved;
		case interaction_eat:
			return owner.eat();
		case interaction_pull:
			return owner.pull();
		case interaction_displace:
			return owner.displace();
		default:
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "Organ:Movement:";
	}
}