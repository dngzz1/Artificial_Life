import java.awt.Color;

class DestructibleWall extends WorldObject implements Stepable {
	static Color color = new Color(153, 153, 153);
	
	int hp;
	
	public DestructibleWall(int hp) {
		this.hp = hp;
	}
	
	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public String getDisplayName() {
		return "Destructible Wall";
	}

	@Override
	public String getInfo() {
		return "HP: "+hp;
	}
	
	private void hit(int attackStrength) {
		hp -= attackStrength;
		if(hp <= 0) {
			remove();
		}
	}
	
	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object data) {
		switch (interactionType) {
		case ATTACK:
			hit((Integer)data);
			return true;
		default:
			return false;
		}
	}

	@Override
	public int getStepsToNextTurn() {
		return hp*10;
	}

	@Override
	public void step() {
		hit(10);
	}
}