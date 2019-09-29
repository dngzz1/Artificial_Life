import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.LinkedList;

class Organ_Emotion extends Organ {
	// Intensity determines sensory neuron firing strength, so 1 = min intensity, 0 = max intensity. //
	float intensity;
	float initialIntensity;
	float decreaseRate;
	float increaseRate;
	Neuron motorNeuron_decrease;
	Neuron motorNeuron_increase;
	Neuron sensoryNeuron_read;
	
	Organ_Emotion(float initialIntensity, float decreaseRate, float increaseRate){
		this.intensity = initialIntensity;
		this.initialIntensity = initialIntensity;
		this.decreaseRate = decreaseRate;
		this.increaseRate = increaseRate;
	}
	
	@Override
	public Organ_Emotion clone() {
		Organ_Emotion organ = new Organ_Emotion(initialIntensity, decreaseRate, increaseRate);
		organ.mutationChance = mutationChance;
		organ.motorNeuron_decrease = motorNeuron_decrease.clone();
		organ.motorNeuron_increase = motorNeuron_increase.clone();
		organ.sensoryNeuron_read = sensoryNeuron_read.clone();
		return organ;
	}

	@Override
	void drawSenses(Graphics2D g) {
		;
	}
	
	@Override
	String getLabel(Neuron neuron){
		if(neuron == motorNeuron_decrease){
			return "M(d)";
		}
		if(neuron == motorNeuron_increase){
			return "M(i)";
		}
		if(neuron == sensoryNeuron_read){
			return "S(r)";
		}
		return "";
	}

	@Override
	LinkedList<Neuron> getMotorNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		neuronList.add(motorNeuron_decrease);
		neuronList.add(motorNeuron_increase);
		return neuronList;
	}

	@Override
	LinkedList<Neuron> getSensoryNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		neuronList.add(sensoryNeuron_read);
		return neuronList;
	}

	@Override
	Organ loadNew() {
		float initialIntensity = M.randf(1);
		float decreaseRate = M.randf(1);
		float increaseRate = M.randf(1);
		return new Organ_Emotion(initialIntensity, decreaseRate, increaseRate);
	}

	@Override
	Organ loadNew(String[] data) {
		float initialIntensity = Float.parseFloat(data[2]);
		float decreaseRate = Float.parseFloat(data[3]);
		float increaseRate = Float.parseFloat(data[4]);
		return new Organ_Emotion(initialIntensity, decreaseRate, increaseRate);
	}

	@Override
	void mutate() {
		if(M.roll(mutationChance)){
			initialIntensity = M.mutateFloat(initialIntensity);
		}
		if(M.roll(mutationChance)){
			decreaseRate = M.mutateFloat(decreaseRate);
		}
		if(M.roll(mutationChance)){
			increaseRate = M.mutateFloat(increaseRate);
		}
		mutationChance = M.mutateFloat(mutationChance);
	}

	@Override
	void print(PrintWriter pw, LinkedList<Neuron> neuronList) {
		pw.println("Organ:Emotion:"+initialIntensity+":"+intensity+":"+decreaseRate+":"+increaseRate+":");
		pw.println("M(d)=N"+neuronList.indexOf(motorNeuron_decrease));
		pw.println("M(i)=N"+neuronList.indexOf(motorNeuron_increase));
		pw.println("S(r)=N"+neuronList.indexOf(sensoryNeuron_read));
	}

	@Override
	void setNeuron(String label, Neuron neuron) {
		if(label.equals("M(d)")){
			motorNeuron_decrease = neuron;
		} else if(label.equals("M(i)")){
			motorNeuron_increase = neuron;
		} else if(label.equals("S(r)")){
			sensoryNeuron_read = neuron;
		}
	}

	@Override
	void setupNeurons() {
		motorNeuron_decrease = new Neuron();
		motorNeuron_increase = new Neuron();
		sensoryNeuron_read = new Neuron();
	}

	@Override
	void stepMotorNeurons() {
		if(motorNeuron_decrease.isFiring){
			intensity = 1 - (1 - intensity)*decreaseRate;
		}
		if(motorNeuron_increase.isFiring){
			intensity = intensity*increaseRate;
		}
	}

	@Override
	void stepSensoryNeurons() {
		sensoryNeuron_read.firingStrength = intensity;
		sensoryNeuron_read.isFiring = true;
	}
	
	@Override
	public String toString() {
		return "Organ:Emotion("+intensity+"):"+initialIntensity+":"+decreaseRate+":"+increaseRate+":";
	}
}