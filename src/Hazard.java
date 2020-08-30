import java.awt.Color;

class Hazard extends WorldObject {
	static Color color = Color.RED;
	
	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public String getDisplayName() {
		return "Hazard";
	}

	@Override
	public String getInfo() {
		return "";
	}
	
	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object[] data) {
		switch (interactionType) {
		case PUSH:
			interacter.interact(this, Interaction.KILL, new Object[] {CauseOfDeath.HAZARD});
			return true;
		case EAT:
			interacter.interact(this, Interaction.KILL, new Object[] {CauseOfDeath.HAZARD});
			return true;
		case PULL:
			interacter.interact(this, Interaction.KILL, new Object[] {CauseOfDeath.HAZARD});
		case DISPLACE:
			interacter.interact(this, Interaction.KILL, new Object[] {CauseOfDeath.HAZARD});
			return true;
		default:
			return false;
		}
	}
}