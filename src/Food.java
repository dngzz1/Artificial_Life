import java.awt.Color;

class Food extends WorldObject {
	static int defaultFoodEnergy;
	static Color color = Color.white;
	static Color color_flesh = new Color(255, 153, 153);
	
	int energy;
	boolean isFlesh;
	
	Food(int energy, boolean isFlesh){
		this.energy = energy;
		this.isFlesh = isFlesh;
	}
	
	Food(){
		this(defaultFoodEnergy, false);
	}

	@Override
	public Color getColor() {
		return isFlesh ? color_flesh : color;
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
	public boolean interact(WorldObject interacter, Interaction interactionType, Object[] data) {
		switch (interactionType) {
		case DISPLACE:
			return displace(interacter, this);
		case EAT:
			int amountEaten = (Integer)data[0];
			if(amountEaten >= energy) {
				interacter.interact(this, Interaction.GIVE_ENERGY, new Object[] {Integer.valueOf(energy), isFlesh});
				remove();
			} else {
				interacter.interact(this, Interaction.GIVE_ENERGY, new Object[] {Integer.valueOf(amountEaten), isFlesh});
				energy -= amountEaten;
			}
			return true;
		case PULL:
			return pull(interacter, this);
		case PUSH:
			return push(interacter, this);
		default:
			return false;
		}
	}
}