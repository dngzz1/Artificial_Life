import java.awt.Color;
import java.awt.Point;

class Plant extends WorldObject implements Stepable {
	static Color fruitingCol = new Color(0, 153, 0);
	static Color idleCol = new Color(102, 255, 102);
	
	boolean fruitsInSummer;
	boolean isMovable = false;
	int stepsToBearFruit = M.randInt(50, 100);
	
	Plant(boolean fruitsInSummer){
		this.fruitsInSummer = fruitsInSummer;
	}
	
	private void produceFruit() {
		int x = M.randInt(location.x - 1, location.x + 1);
		int y = M.randInt(location.y - 1, location.y + 1);
		ArtificialLife.place(new Food(), x, y);
	}
	
	@Override
	public Color getColor() {
		return isFruiting() ? fruitingCol : idleCol;
	}

	@Override
	public String getDisplayName() {
		return "Plant";
	}

	@Override
	public String getInfo() {
		String info = "";
		info += "Produces in: "+(fruitsInSummer ? "summer" : "winter")+"<br>";
		info += "Produces every: "+stepsToBearFruit+" steps"+"<br>";
		return info;
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
			produceFruit();
		}
	}
}

class Plant_Fruit extends WorldObject {
	static Color color = new Color(102, 255, 102);
	static Color color_hasFruit = new Color(0, 153, 0);
	
	int lastPicked = 0;
	int stepsToBearFruit = M.randInt(400, 800);
	
	private void produceFruit() {
		for(Direction direction : Direction.values()) {
			ArtificialLife.place(new Food(), getAdjacentLocation(direction));
			lastPicked = ArtificialLife.stepCounter;
		}
	}

	@Override
	public Color getColor() {
		return hasFruit() ? color_hasFruit : color;
	}

	@Override
	public String getDisplayName() {
		return "Fruiting Plant";
	}

	@Override
	public String getInfo() {
		String info = "";
		info += "Produces every: "+stepsToBearFruit+" steps"+"<br>";
		info += "Has fruit: "+(hasFruit() ? "yes" : "no")+"<br>";
		return info;
	}
	
	private boolean hasFruit() {
		return (ArtificialLife.stepCounter >= lastPicked + stepsToBearFruit);
	}

	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object data) {
		switch (interactionType) {
		case ATTACK:
			if(hasFruit()) {
				produceFruit();
				return true;
			} else {
				return false;
			}
		default:
			return false;
		}
	}
}

class Plant_Tuber extends WorldObject implements Stepable {
	static Color color = new Color(61, 153, 61);
	
	int stepsToBearFruit = M.randInt(50, 100);
	
	private void produceFruit() {
		int x = M.randInt(location.x - 1, location.x + 1);
		int y = M.randInt(location.y - 1, location.y + 1);
		ArtificialLife.place(new Tuber(), x, y);
	}
	
	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public String getDisplayName() {
		return "Tuber Producing Plant";
	}

	@Override
	public String getInfo() {
		String info = "";
		info += "Produces every: "+stepsToBearFruit+" steps"+"<br>";
		return info;
	}
	
	@Override
	public int getStepsToNextTurn() {
		return stepsToBearFruit;
	}
	
	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object data) {
		switch (interactionType) {
		default:
			return false;
		}
	}
	
	@Override
	public void step(){
		produceFruit();
	}
}

class Tuber extends WorldObject {
	static Color color = new Color(102, 102, 61);

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public String getDisplayName() {
		return "Tuber";
	}

	@Override
	public String getInfo() {
		return "";
	}

	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object data) {
		switch (interactionType) {
		case PULL:
			Point originalLocation = new Point(location);
			boolean wasPulled = pull(interacter, this);
			if(wasPulled) {
				remove();
				ArtificialLife.place(new Food(), originalLocation);
			}
			return wasPulled;
		default:
			return false;
		}
	}
	
	
}