import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.LinkedList;

abstract class Organ {
	private static LinkedList<Organ> organTypeList = new LinkedList<Organ>();
	private static LinkedList<String> organTypeLabelList = new LinkedList<String>();
	private static LinkedList<Organ> mutatableOrganTypeList = new LinkedList<Organ>();
	
	Cell owner;
	float mutationChance = M.randf(1.0f);
	
	static Organ load(String[] data){
		for(String organTypeLabel : organTypeLabelList){
			if(data[1].equals(organTypeLabel)){
				Organ organType = organTypeList.get(organTypeLabelList.indexOf(organTypeLabel));
				return organType.loadNew(data);
			}
		}
		return null;
	}
	
	static Organ newRandomOrgan(){
		return M.chooseRandom(mutatableOrganTypeList).loadNew();
	}
	
	private static void registerOrganType(Organ organ, String label){
		organTypeList.add(organ);
		organTypeLabelList.add(label);
		if(organ.isMutatable()){
			mutatableOrganTypeList.add(organ);
		}
	}
	
	public static void setup(){
		registerOrganType(new Organ_Age(), "Age");
		registerOrganType(new Organ_Emotion(0.5f, 0.5f, 0.5f), "Emotion");
		registerOrganType(new Organ_Eye(1, 0), "Eye");
		registerOrganType(new Organ_Hunger(), "Hunger");
		registerOrganType(new Organ_Location(), "Location");
		registerOrganType(new Organ_Interaction(), "Movement");
		registerOrganType(new Organ_Reproduction(Cell.birthEnergyRequirement), "Reproduction");
	}
	
	@Override
	public abstract Organ clone();
	
	abstract void drawSenses(Graphics2D g);
	
	public void fireSensoryNeurons() {
		for(Neuron neuron : getSensoryNeurons()){
			neuron.isFiring = true;
		}
	}
	
	LinkedList<Neuron> getNeurons(){
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		neuronList.addAll(getSensoryNeurons());
		neuronList.addAll(getMotorNeurons());
		return neuronList;
		
	}
	
	abstract String getLabel(Neuron neuron);
	
	abstract LinkedList<Neuron> getMotorNeurons();
	
	abstract LinkedList<Neuron> getSensoryNeurons();
	
	boolean isMutatable(){
		return true;
	}
	
	abstract Organ loadNew();
	
	abstract Organ loadNew(String[] data);
	
	abstract void mutate();
	
	abstract void print(PrintWriter pw, LinkedList<Neuron> neuronList);
	
	abstract void setNeuron(String label, Neuron neuron);
	
	abstract void setupNeurons();
	
	abstract void stepMotorNeurons();
	
	abstract void stepSensoryNeurons();
	
	@Override
	public abstract String toString();
	
}