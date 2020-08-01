import java.awt.Color;

class Food extends WorldObject {
	static int energyGainPerFood;
	static Color color = Color.white;

	@Override
	public Color getColor() {
		return color;
	}
	
	public int getFoodValue(){
		return energyGainPerFood;
	}

	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object data) {
		switch (interactionType) {
		case DISPLACE:
			return displace(interacter, this);
		case EAT:
			interacter.interact(this, Interaction.GIVE_ENERGY, Integer.valueOf(energyGainPerFood));
			remove();
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