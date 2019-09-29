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
	public boolean interact(WorldObject interacter, Interaction interactionType) {
		switch (interactionType) {
		case PUSH:
			return push(interacter, this);
		case EAT:
			interacter.interact(this, Interaction.GIVE_FOOD_ENERGY);
			remove();
			return true;
		case PULL:
			return pull(interacter, this);
		case DISPLACE:
			return displace(interacter, this);
		default:
			return false;
		}
	}
	
	private void remove() {
		Display.grid[location.x][location.y] = null;
	}
}