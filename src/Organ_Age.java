import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.LinkedList;

class Organ_Age extends Organ {
	Neuron sensoryNeuron_age;
	
	Organ_Age(){
		;
	}
	
	public Organ_Age clone() {
		Organ_Age organ = new Organ_Age();
		organ.mutationChance = mutationChance;
		organ.sensoryNeuron_age = sensoryNeuron_age.clone();
		return organ;
	}
	
	void drawSenses(Graphics2D g){
		;
	}
	
	@Override
	String getLabel(Neuron neuron){
		if(neuron == sensoryNeuron_age){
			return "S(a)";
		}
		return "";
	}
	
	LinkedList<Neuron> getMotorNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		return neuronList;
	}
	
	LinkedList<Neuron> getSensoryNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		neuronList.add(sensoryNeuron_age);
		return neuronList;
	}
	
	@Override
	boolean isMutatable(){
		return false;
	}
	
	Organ_Age loadNew(){
		return new Organ_Age();
	}
	
	Organ_Age loadNew(String[] data){
		return new Organ_Age();
	}
	
	public void mutate() {
		;
	}
	
	void print(PrintWriter pw, LinkedList<Neuron> neuronList){
		pw.println("Organ:Age:");
		pw.println("S(a)=N"+neuronList.indexOf(sensoryNeuron_age));
	}
	
	void setNeuron(String label, Neuron neuron){
		if(label.equals("S(a)")){
			sensoryNeuron_age = neuron;
		}
	}
	
	void setupNeurons() {
		sensoryNeuron_age = new Neuron();
	}
	
	void stepMotorNeurons(){
		;
	}
	
	void stepSensoryNeurons() {
		float age = (float)(owner.lifetime/1000.0f) + 1;
		sensoryNeuron_age.firingStrength = 1.0f / age;
		sensoryNeuron_age.isFiring = true;
	}
	
	@Override
	public String toString() {
		return "Organ:Age:";
	}
}