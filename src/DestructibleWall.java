import java.awt.Color;

class DestructibleWall extends WorldObject {
	
	int hp;
	
	public DestructibleWall(int hp) {
		this.hp = hp;
	}
	
	@Override
	public Color getColor() {
		return Color.BLACK;
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
}