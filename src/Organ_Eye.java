import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.PrintWriter;
import java.util.LinkedList;

class Organ_Eye extends Organ {
	static int energyCostPerTileSeen;

	int rayCastLength;
	int rotation;
	Neuron sensoryNeuron_eye_r;
	Neuron sensoryNeuron_eye_g;
	Neuron sensoryNeuron_eye_b;
	Neuron sensoryNeuron_eye_distance;
	
	Organ_Eye(int rayCastLength, int rotation){
		this.rayCastLength = rayCastLength;
		this.rotation = rotation;
	}
	
	public Organ_Eye clone() {
		Organ_Eye organ = new Organ_Eye(rayCastLength, rotation);
		organ.mutationChance = mutationChance;
		organ.sensoryNeuron_eye_r = sensoryNeuron_eye_r.clone();
		organ.sensoryNeuron_eye_g = sensoryNeuron_eye_g.clone();
		organ.sensoryNeuron_eye_b = sensoryNeuron_eye_b.clone();
		organ.sensoryNeuron_eye_distance = sensoryNeuron_eye_distance.clone();
		return organ;
	}
	
	void drawSenses(Graphics2D g){
		int distance = (Integer)rayCast(getDirection(), false)[1];
		Point eyeVector = getDirection().getVector();
		int drawScale = Display.drawScale;
		Point loc = owner.getLocation();
		g.drawLine(drawScale*loc.x, drawScale*loc.y, drawScale*(loc.x + distance*eyeVector.x), drawScale*(loc.y + distance*eyeVector.y));
	}
	
	private Direction getDirection(){
		Direction direction = owner.facing;
		for(int i = rotation; i > 0; i --){
			direction = direction.rotateCW();
		}
		return direction;
	}
	
	@Override
	String getLabel(Neuron neuron){
		if(neuron == sensoryNeuron_eye_r){
			return "S(r)";
		}
		if(neuron == sensoryNeuron_eye_g){
			return "S(g)";
		}
		if(neuron == sensoryNeuron_eye_b){
			return "S(b)";
		}
		if(neuron == sensoryNeuron_eye_distance){
			return "S(d)";
		}
		return "";
	}
	
	LinkedList<Neuron> getMotorNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		return neuronList;
	}
	
	LinkedList<Neuron> getSensoryNeurons() {
		LinkedList<Neuron> neuronList = new LinkedList<Neuron>();
		neuronList.add(sensoryNeuron_eye_r);
		neuronList.add(sensoryNeuron_eye_g);
		neuronList.add(sensoryNeuron_eye_b);
		neuronList.add(sensoryNeuron_eye_distance);
		return neuronList;
	}
	
	Organ_Eye loadNew(){
		int rayCastLength = M.randInt(1, 5);
		int direction = M.randInt(0, 8);
		return new Organ_Eye(rayCastLength, direction);
	}
	
	Organ_Eye loadNew(String[] data){
		int rayCastLength = Integer.parseInt(data[2]);
		int direction = Integer.parseInt(data[3]);
		return new Organ_Eye(rayCastLength, direction);
	}
	
	public void mutate() {
		if(M.roll(mutationChance)){
			rayCastLength += M.roll(0.5) ? 1 : -1;
			rayCastLength = Math.max(1, rayCastLength);
		}
		if(M.roll(mutationChance)){
			rotation = M.roll(0.5) ? (rotation + 1)%8 : (rotation + 7)%8;
		}
		mutationChance = M.mutateFloat(mutationChance);
	}
	
	void print(PrintWriter pw, LinkedList<Neuron> neuronList){
		pw.println("Organ:Eye:"+rayCastLength+":"+rotation+":");
		pw.println("S(r)=N"+neuronList.indexOf(sensoryNeuron_eye_r));
		pw.println("S(g)=N"+neuronList.indexOf(sensoryNeuron_eye_g));
		pw.println("S(b)=N"+neuronList.indexOf(sensoryNeuron_eye_b));
		pw.println("S(d)=N"+neuronList.indexOf(sensoryNeuron_eye_distance));
	}
	
	private Object[] rayCast(Direction direction, boolean costsEnergy){
		Object object = null;
		Point p = new Point(owner.getLocation());
		Point d = direction.getVector();
		int distance;
		for(distance = 1; distance <= rayCastLength; distance ++){
			if(costsEnergy){
				owner.energy -= energyCostPerTileSeen;
			}
			p.x += d.x;
			p.y += d.y;
			Display.wrapPoint(p);
			object = Display.grid[p.x][p.y];
			if(object != null){
				break;
			}
		}
		return new Object[]{object, Integer.valueOf(distance)};
	}
	
	void setNeuron(String label, Neuron neuron){
		if(label.equals("S(r)")){
			sensoryNeuron_eye_r = neuron;
		} else if(label.equals("S(g)")){
			sensoryNeuron_eye_g = neuron;
		} else if(label.equals("S(b)")){
			sensoryNeuron_eye_b = neuron;
		} else if(label.equals("S(d)")){
			sensoryNeuron_eye_distance = neuron;
		}
	}
	
	void setupNeurons() {
		sensoryNeuron_eye_r = new Neuron();
		sensoryNeuron_eye_g = new Neuron();
		sensoryNeuron_eye_b = new Neuron();
		sensoryNeuron_eye_distance = new Neuron();
	}
	
	void stepMotorNeurons(){
		;
	}
	
	void stepSensoryNeurons() {
		Color seenColor = null;
		float distance = -1;
		
		Object[] seenObject = rayCast(getDirection(), true);
		if(seenObject[0] != null){
			seenColor = ((WorldObject)seenObject[0]).getColor();
			distance = (Integer)seenObject[1];
		}
		// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //
		
		if(seenColor == null){
			// If nothing is seen, set to minimal firing strength (1.0f). //
			sensoryNeuron_eye_r.firingStrength = 1.0f; 
			sensoryNeuron_eye_g.firingStrength = 1.0f;
			sensoryNeuron_eye_b.firingStrength = 1.0f;
			sensoryNeuron_eye_distance.firingStrength = 1.0f;
			sensoryNeuron_eye_r.setFiring(false);
			sensoryNeuron_eye_g.setFiring(false);
			sensoryNeuron_eye_b.setFiring(false);
			sensoryNeuron_eye_distance.setFiring(false);
		} else {
			// If something is seen, set firing strengths. //
			sensoryNeuron_eye_r.firingStrength = 1 - (float)seenColor.getRed() / 255.0f;
			sensoryNeuron_eye_g.firingStrength = 1 - (float)seenColor.getGreen() / 255.0f;
			sensoryNeuron_eye_b.firingStrength = 1 - (float)seenColor.getBlue() / 255.0f;
			sensoryNeuron_eye_distance.firingStrength = distance/rayCastLength;
			sensoryNeuron_eye_r.setFiring(true);
			sensoryNeuron_eye_g.setFiring(true);
			sensoryNeuron_eye_b.setFiring(true);
			sensoryNeuron_eye_distance.setFiring(true);
		}
		
	}
	
	@Override
	public String toString() {
		return "Organ:Eye:"+rayCastLength+":"+rotation+":";
	}
}