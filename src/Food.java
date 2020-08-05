import java.awt.Color;

class Food extends WorldObject {
	static int defaultFoodEnergy;
	static Color color = Color.white;
	
	int energy;
	
	Food(int energy){
		this.energy = energy;
	}
	
	Food(){
		this(defaultFoodEnergy);
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public String getDisplayName() {
		return "Food";
	}

	@Override
	public String getInfo() {
		return "Energy: "+energy;
	}

	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object data) {
		switch (interactionType) {
		case DISPLACE:
			return false;//displace(interacter, this);
		case EAT:
			int amountEaten = (Integer)data;
			if(amountEaten >= energy) {
				interacter.interact(this, Interaction.GIVE_ENERGY, Integer.valueOf(energy));
				remove();
			} else {
				interacter.interact(this, Interaction.GIVE_ENERGY, Integer.valueOf(amountEaten));
				energy -= amountEaten;
			}
			return true;
		case PULL:
			return pull(interacter, this);
		case PUSH:
			return false;//push(interacter, this);
		default:
			return false;
		}
	}
}