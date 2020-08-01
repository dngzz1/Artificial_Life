import java.awt.Color;

class Plant extends WorldObject implements Stepable {
	static Color fruitingCol = new Color(153, 153, 0);
	static Color idleCol = new Color(102, 102, 0);
	
	boolean fruitsInSummer;
	boolean isMovable = false;
	int stepsToBearFruit = M.randInt(50, 70);
	
	Plant(boolean fruitsInSummer){
		this.fruitsInSummer = fruitsInSummer;
	}
	
	private void fruit() {
		int x = M.randInt(location.x - 1, location.x + 1);
		int y = M.randInt(location.y - 1, location.y + 1);
		ArtificialLife.place(new Food(), x, y);
	}
	
	@Override
	public Color getColor() {
		return isFruiting() ? fruitingCol : idleCol;
	}
	
	@Override
	public int getStepsToNextTurn() {
		return stepsToBearFruit;
	}
	
	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object data) {
		switch (interactionType) {
		case PUSH:
			if(isMovable) {
				return push(interacter, this);
			} else {
				return false;
			}
		case PULL:
			if(isMovable) {
				return pull(interacter, this);
			} else {
				return false;
			}
		case DISPLACE:
			if(isMovable) {
				return displace(interacter, this);
			} else {
				return false;
			}
		default:
			return false;
		}
	}
	
	private boolean isFruiting() {
		return (ArtificialLife.isSummer && fruitsInSummer) || (!ArtificialLife.isSummer && !fruitsInSummer);
	}
	
	@Override
	public void step(){
		if(isFruiting()) {
			fruit();
		}
	}
}