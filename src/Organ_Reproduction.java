import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.LinkedList;

class Organ_Reproduction extends Organ {
	int energyPassedToChild;
	Neuron motorNeuron_spawn;
	
	Organ_Reproduction(int energyPassedToChild){
		this.energyPassedToChild = energyPassedToChild;
	}

	@Override
	public Organ_Reproduction clone() {
		Organ_Reproduction organ = new Organ_Reproduction(energyPassedToChild);
		organ.mutationChance = mutationChance;
		organ.motorNeuron_spawn = motorNeuron_spawn.clone();
		return organ;
	}

	@Override
	void drawSenses(Graphics2D g) {
		;
	}
	
	@Override
	String getLabel(Neuron neuron){
		if(neuron == motorNeuron_spawn){
			return "M(s)";
		}
		return "";
	}

	@Override
	LinkedList<Neuron> getMotorNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		neuronList.add(motorNeuron_spawn);
		return neuronList;
	}

	@Override
	LinkedList<Neuron> getSensoryNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		return neuronList;
	}
	
	@Override
	boolean isMutatable(){
		return false;
	}

	@Override
	Organ loadNew() {
		int energyPassedToChild = Cell.energyUponBirth;
		return new Organ_Reproduction(energyPassedToChild);
	}

	@Override
	Organ loadNew(String[] data) {
		int energyPassedToChild = Integer.parseInt(data[2]);
		return new Organ_Reproduction(energyPassedToChild);
	}

	@Override
	void mutate() {
		if(M.roll(mutationChance)){
			energyPassedToChild = (int)(energyPassedToChild*M.rand(0.8, 1.25)*M.rand(0.8, 1.25));
		}
		mutationChance = M.mutateFloat(mutationChance);
	}

	@Override
	void print(PrintWriter pw, LinkedList<Neuron> neuronList) {
		pw.println("Organ:Reproduction:"+energyPassedToChild+":");
		pw.println("M(s)=N"+neuronList.indexOf(motorNeuron_spawn));
	}

	@Override
	void setNeuron(String label, Neuron neuron) {
		if(label.equals("M(s)")){
			motorNeuron_spawn = neuron;
		}
	}

	@Override
	void setupNeurons() {
		motorNeuron_spawn = new Neuron();
	}

	@Override
	void stepMotorNeurons() {
		if(motorNeuron_spawn.isFiring){
			owner.spawn(owner.facing, energyPassedToChild);
		}
	}

	@Override
	void stepSensoryNeurons() {
		;
	}

	@Override
	public String toString() {
		return "Organ:Reproduction:"+energyPassedToChild+":";
	}
}