import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.LinkedList;

class Organ_Hunger extends Organ {
	Neuron sensoryNeuron_hunger;
	
	Organ_Hunger(){
		;
	}
	
	public Organ_Hunger clone() {
		Organ_Hunger organ = new Organ_Hunger();
		organ.mutationChance = mutationChance;
		organ.sensoryNeuron_hunger = sensoryNeuron_hunger.clone();
		return organ;
	}
	
	void drawSenses(Graphics2D g){
		;
	}
	
	@Override
	String getLabel(Neuron neuron){
		if(neuron == sensoryNeuron_hunger){
			return "S(h)";
		}
		return "";
	}
	
	LinkedList<Neuron> getMotorNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		return neuronList;
	}
	
	LinkedList<Neuron> getSensoryNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		neuronList.add(sensoryNeuron_hunger);
		return neuronList;
	}
	
	@Override
	boolean isMutatable(){
		return false;
	}
	
	Organ_Hunger loadNew(){
		return new Organ_Hunger();
	}
	
	Organ_Hunger loadNew(String[] data){
		return new Organ_Hunger();
	}
	
	public void mutate() {
		;
	}
	
	void print(PrintWriter pw, LinkedList<Neuron> neuronList){
		pw.println("Organ:Hunger:");
		pw.println("S(h)=N"+neuronList.indexOf(sensoryNeuron_hunger));
	}
	
	void setNeuron(String label, Neuron neuron){
		if(label.equals("S(h)")){
			sensoryNeuron_hunger = neuron;
		}
	}
	
	void setupNeurons() {
		sensoryNeuron_hunger = new Neuron();
	}
	
	void stepMotorNeurons(){
		;
	}
	
	void stepSensoryNeurons() {
		float hunger = Math.max(1.0f, (float)(owner.energy) / (float)(Food.energyGainPerFood));
		sensoryNeuron_hunger.firingStrength = 1.0f - (1.0f / hunger);
		sensoryNeuron_hunger.isFiring = true;
	}
	
	@Override
	public String toString() {
		return "Organ:Hunger:";
	}
}