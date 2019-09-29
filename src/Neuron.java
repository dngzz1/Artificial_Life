import java.io.PrintWriter;
import java.util.LinkedList;

import maths.M;

class Neuron {
	LinkedList<Neuron> connectionList = new LinkedList<Neuron>();
	
	float threshold = M.randf(1);
	boolean isThresholdUpperLimit = true;
	
	float currentInputStrength = 1;//Range: 1-0,1=off, 0=on
	boolean isFiring = false;
	float firingStrength = M.randf(1);
	
	Neuron(){
		
	}
	
	public void addConnection(Neuron target){
		connectionList.add(target);
	}
	
	public void cloneConnections(Neuron neuronToClone, LinkedList<Neuron> neuronListToClone, LinkedList<Neuron> newNeuronList){
		for(Neuron connectionToClone : neuronToClone.connectionList){
			addConnection(newNeuronList.get(neuronListToClone.indexOf(connectionToClone)));
		}
	}
	
	public void calculateConnections(){
		if(isFiring){
			for(Neuron connection : connectionList){
				connection.currentInputStrength *= firingStrength;
			}
		}
	}
	
	@Override
	protected Neuron clone() {
		Neuron neuron = new Neuron();
		neuron.threshold = threshold;
		neuron.firingStrength = firingStrength;
		neuron.isThresholdUpperLimit = isThresholdUpperLimit;
		return neuron;
	}
	
	public LinkedList<Neuron> getConnectedNeurons(){
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		for(Neuron connection : connectionList){
			neuronList.add(connection);
		}
		return neuronList;
	}
	
	public boolean  isThresholdCrossed(){
		boolean belowThreshold = (currentInputStrength <= threshold);
		return ( (isThresholdUpperLimit && belowThreshold) || (!isThresholdUpperLimit && !belowThreshold) );
	}
	
	public boolean hasConnectionTo(Neuron neuron){
		return connectionList.contains(neuron);
	}
	
	public void print(PrintWriter pw, LinkedList<Neuron> neuronList){
		char thresholdRelation = isThresholdUpperLimit ? '<' : '>';
		pw.println("N"+neuronList.indexOf(this)+":"+thresholdRelation+threshold+":"+firingStrength);
		for(Neuron connection : connectionList){
			pw.println("=>N"+neuronList.indexOf(connection));
		}
	}
	
	public void setFiring(boolean firing){
		isFiring = firing;
	}
	
	public Neuron splitConnection(Neuron targetNeuron){
		Neuron neuron = new Neuron();
		neuron.threshold = targetNeuron.threshold;
		neuron.firingStrength = firingStrength;
		neuron.isThresholdUpperLimit = isThresholdUpperLimit;
		neuron.addConnection(targetNeuron);
		connectionList.remove(targetNeuron);
		addConnection(neuron);
		return neuron;
	}
}