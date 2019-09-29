import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.LinkedList;

class Organ_Location extends Organ {
	Neuron sensoryNeuron_x;
	Neuron sensoryNeuron_y;
	
	Organ_Location(){
		;
	}
	
	public Organ_Location clone() {
		Organ_Location organ = new Organ_Location();
		organ.mutationChance = mutationChance;
		organ.sensoryNeuron_x = sensoryNeuron_x.clone();
		organ.sensoryNeuron_y = sensoryNeuron_y.clone();
		return organ;
	}
	
	void drawSenses(Graphics2D g){
		;
	}
	
	@Override
	String getLabel(Neuron neuron){
		if(neuron == sensoryNeuron_x){
			return "S(x)";
		}
		if(neuron == sensoryNeuron_y){
			return "S(y)";
		}
		return "";
	}
	
	LinkedList<Neuron> getMotorNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		return neuronList;
	}
	
	LinkedList<Neuron> getSensoryNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		neuronList.add(sensoryNeuron_x);
		neuronList.add(sensoryNeuron_y);
		return neuronList;
	}
	
	@Override
	boolean isMutatable(){
		return false;
	}
	
	Organ_Location loadNew(){
		return new Organ_Location();
	}
	
	Organ_Location loadNew(String[] data){
		return new Organ_Location();
	}
	
	public void mutate() {
		;
	}
	
	void print(PrintWriter pw, LinkedList<Neuron> neuronList){
		pw.println("Organ:Location:");
		pw.println("S(x)=N"+neuronList.indexOf(sensoryNeuron_x));
		pw.println("S(y)=N"+neuronList.indexOf(sensoryNeuron_y));
	}
	
	void setNeuron(String label, Neuron neuron){
		if(label.equals("S(x)")){
			sensoryNeuron_x = neuron;
		} else if(label.equals("S(y)")){
			sensoryNeuron_y = neuron;
		}
	}
	
	void setupNeurons() {
		sensoryNeuron_x = new Neuron();
		sensoryNeuron_y = new Neuron();
	}
	
	void stepMotorNeurons(){
		;
	}
	
	void stepSensoryNeurons() {
		sensoryNeuron_x.firingStrength = (float) owner.getX() / (float) Display.width;
		sensoryNeuron_x.isFiring = true;
		sensoryNeuron_y.firingStrength = (float) owner.getY() / (float) Display.height;
		sensoryNeuron_y.isFiring = true;
	}
	
	@Override
	public String toString() {
		return "Organ:Location";
	}
}