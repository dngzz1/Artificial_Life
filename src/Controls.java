import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

class Controls implements KeyListener {
	public static final Controls instance = new Controls();
	public static int fastScrollSpeed = 5;
	public static final int[] SPEED_SETTING = {-1, 0, 1, 4, 16, 64, 256, 1024, 4096, 16384};
	
	private static boolean ctrlDown = false, shiftDown = false;
	
	public static boolean inPlaceMode = false;
	public static boolean isGameRunning;
	public static boolean isFramerateCapped = true;
	public static int stepsPerDraw = 1;
	public static boolean spawnNewCells = true;
	public static boolean stepSimulationOnce = false;
	
	public static void setSpeed(int speedSetting) {
		switch (speedSetting) {
		case -1:
			isGameRunning = false;
			isFramerateCapped = true;
			stepsPerDraw = 1;
			break;
		case 0:
			isGameRunning = true;
			isFramerateCapped = true;
			stepsPerDraw = 1;
			break;
		default:
			isGameRunning = true;
			isFramerateCapped = false;
			stepsPerDraw = speedSetting;
			break;
		}
	}
	
	public static void setup() {
		Control.DOORS_CLOSE.setTrigger(KeyEvent.VK_D, true, false);
		Control.DOORS_CLOSE_FORCIBLY.setTrigger(KeyEvent.VK_D, true, true);
		Control.DOORS_OPEN.setTrigger(KeyEvent.VK_D);
		Control.DRAW_CELL_VISION.setTrigger(KeyEvent.VK_V);
		Control.FOLLOW_CELL.setTrigger(KeyEvent.VK_F);
		Control.LOAD_FILE.setTrigger(KeyEvent.VK_L, false, true);
		Control.PAUSE.setTrigger(KeyEvent.VK_SPACE);
		Control.PLACEMODE_PLACE_FOOD.setTrigger(KeyEvent.VK_F);
		Control.PLACEMODE_PLACE_HAZARD.setTrigger(KeyEvent.VK_H);
		Control.PLACEMODE_PLACE_WALL.setTrigger(KeyEvent.VK_W);
		Control.PLACEMODE_REMOVE_OBJECT.setTrigger(KeyEvent.VK_R);
		Control.PRINT_LOG.setTrigger(KeyEvent.VK_P, false, true);
		Control.SIM_SPEED_1.setTrigger(KeyEvent.VK_1);
		Control.SIM_SPEED_2.setTrigger(KeyEvent.VK_2);
		Control.SIM_SPEED_3.setTrigger(KeyEvent.VK_3);
		Control.SIM_SPEED_4.setTrigger(KeyEvent.VK_4);
		Control.SIM_SPEED_5.setTrigger(KeyEvent.VK_5);
		Control.SIM_SPEED_6.setTrigger(KeyEvent.VK_6);
		Control.SIM_SPEED_7.setTrigger(KeyEvent.VK_7);
		Control.SIM_SPEED_8.setTrigger(KeyEvent.VK_8);
		Control.SIM_SPEED_9.setTrigger(KeyEvent.VK_9);
		Control.STEP_ONCE.setTrigger(KeyEvent.VK_PERIOD);
		Control.TOGGLE_PLACE_MODE.setTrigger(KeyEvent.VK_P);
		Control.TOGGLE_MAP_MODE.setTrigger(KeyEvent.VK_M);
		Control.TOGGLE_CELL_SPAWNING.setTrigger(KeyEvent.VK_S);
		Control.VIEW_DOWN.setTrigger(KeyEvent.VK_DOWN);
		Control.VIEW_DOWN_FAST.setTrigger(KeyEvent.VK_DOWN, true, false);
		Control.VIEW_LEFT.setTrigger(KeyEvent.VK_LEFT);
		Control.VIEW_LEFT_FAST.setTrigger(KeyEvent.VK_LEFT, true, false);
		Control.VIEW_RIGHT.setTrigger(KeyEvent.VK_RIGHT);
		Control.VIEW_RIGHT_FAST.setTrigger(KeyEvent.VK_RIGHT, true, false);
		Control.VIEW_UP.setTrigger(KeyEvent.VK_UP);
		Control.VIEW_UP_FAST.setTrigger(KeyEvent.VK_UP, true, false);
	}
	
	public static void step() {
		for(Control control : Control.getPressedControls()) {
			switch (control) {
			case DOORS_CLOSE:
				Door.closeAll(false);
				control.consume();
				break;
			case DOORS_CLOSE_FORCIBLY:
				Door.closeAll(true);
				control.consume();
				break;
			case DOORS_OPEN:
				Door.openAll();
				control.consume();
				break;
			case DRAW_CELL_VISION:
				Display.drawCellVision = !Display.drawCellVision;
				control.consume();
				break;
			case FOLLOW_CELL:
				ArtificialLife.selectHoveredObject();
				break;
			case LOAD_FILE:
				ArtificialLife.loadFile();
				control.consume();
				break;
			case PAUSE:
				setSpeed(isGameRunning ? -1 : 0);
				control.consume();
				break;
			case PLACEMODE_PLACE_FOOD:
				if(inPlaceMode)
					ArtificialLife.place(new Food(), Display.viewX, Display.viewY);
				control.consume();
				break;
			case PLACEMODE_PLACE_HAZARD:
				if(inPlaceMode)
					ArtificialLife.place(new Hazard(), Display.viewX, Display.viewY);
				control.consume();
				break;
			case PLACEMODE_PLACE_WALL:
				if(inPlaceMode)
					ArtificialLife.place(new Wall(false, false, Color.BLACK), Display.viewX, Display.viewY);
				control.consume();
				break;
			case PLACEMODE_REMOVE_OBJECT:
				if(inPlaceMode)
					ArtificialLife.removeObjectAtCursor();
				control.consume();
				break;
			case PRINT_LOG:
				ArtificialLife.printGenerationToFile();
				control.consume();
				break;
			case SIM_SPEED_1:
				setSpeed(SPEED_SETTING[1]);
				control.consume();
				break;
			case SIM_SPEED_2:
				setSpeed(SPEED_SETTING[2]);
				control.consume();
				break;
			case SIM_SPEED_3:
				setSpeed(SPEED_SETTING[3]);
				control.consume();
				break;
			case SIM_SPEED_4:
				setSpeed(SPEED_SETTING[4]);
				control.consume();
				break;
			case SIM_SPEED_5:
				setSpeed(SPEED_SETTING[5]);
				control.consume();
				break;
			case SIM_SPEED_6:
				setSpeed(SPEED_SETTING[6]);
				control.consume();
				break;
			case SIM_SPEED_7:
				setSpeed(SPEED_SETTING[7]);
				control.consume();
				break;
			case SIM_SPEED_8:
				setSpeed(SPEED_SETTING[8]);
				control.consume();
				break;
			case SIM_SPEED_9:
				setSpeed(SPEED_SETTING[9]);
				control.consume();
				break;
			case STEP_ONCE:
				stepSimulationOnce = true;
				control.consume();
				break;
			case TOGGLE_MAP_MODE:
				if(!inPlaceMode)
					Display.instance.toggleDisplayMode();
				control.consume();
				break;
			case TOGGLE_PLACE_MODE:
				inPlaceMode = !inPlaceMode;
				control.consume();
				break;
			case TOGGLE_CELL_SPAWNING:
				if(!inPlaceMode)
					spawnNewCells = !spawnNewCells;
				control.consume();
				break;
			case VIEW_DOWN:
				Display.move(Direction.S);
				control.consume();
				break;
			case VIEW_DOWN_FAST:
				for(int i = 0; i < fastScrollSpeed; i ++)
					Display.move(Direction.S);
				break;
			case VIEW_LEFT:
				Display.move(Direction.W);
				control.consume();
				break;
			case VIEW_LEFT_FAST:
				for(int i = 0; i < fastScrollSpeed; i ++)
					Display.move(Direction.W);
				break;
			case VIEW_RIGHT:
				Display.move(Direction.E);
				control.consume();
				break;
			case VIEW_RIGHT_FAST:
				for(int i = 0; i < fastScrollSpeed; i ++)
					Display.move(Direction.E);
				break;
			case VIEW_UP:
				Display.move(Direction.N);
				control.consume();
				break;
			case VIEW_UP_FAST:
				for(int i = 0; i < fastScrollSpeed; i ++)
					Display.move(Direction.N);
				break;
			}
		}
	}
	
	public void keyPressed(KeyEvent e) {
		for(Control control : Control.values()) {
			control.press(e.getKeyCode(), shiftDown, ctrlDown);
		}
		switch (e.getKeyCode()) {
		case KeyEvent.VK_CONTROL:
			ctrlDown = true;
			break;
		case KeyEvent.VK_SHIFT:
			shiftDown = true;
			break;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		for(Control control : Control.values()) {
			control.release(e.getKeyCode());
		}
		switch (e.getKeyCode()) {
		case KeyEvent.VK_CONTROL:
			ctrlDown = false;
			break;
		case KeyEvent.VK_SHIFT:
			shiftDown = false;
			break;
		}
	}
	
	public void keyTyped(KeyEvent e) {}
}

enum Control {
	DOORS_CLOSE, DOORS_CLOSE_FORCIBLY, DOORS_OPEN,
	DRAW_CELL_VISION, FOLLOW_CELL, LOAD_FILE, PAUSE,
	PLACEMODE_PLACE_FOOD, PLACEMODE_PLACE_HAZARD, PLACEMODE_PLACE_WALL, PLACEMODE_REMOVE_OBJECT,
	PRINT_LOG,
	SIM_SPEED_1, SIM_SPEED_2, SIM_SPEED_3, SIM_SPEED_4, SIM_SPEED_5, SIM_SPEED_6, SIM_SPEED_7, SIM_SPEED_8, SIM_SPEED_9,
	STEP_ONCE, 
	TOGGLE_MAP_MODE, TOGGLE_PLACE_MODE, TOGGLE_CELL_SPAWNING, 
	VIEW_DOWN, VIEW_DOWN_FAST, VIEW_LEFT, VIEW_LEFT_FAST, VIEW_RIGHT, VIEW_RIGHT_FAST, VIEW_UP, VIEW_UP_FAST;
	
	private static boolean[] isPressed = new boolean[values().length];
	private static int[] keyEvent = new int[values().length];
	private static boolean[] requiresShift = new boolean[values().length];
	private static boolean[] requiresCTRL = new boolean[values().length];
	
	public static LinkedList<Control> getPressedControls() {
		LinkedList<Control> pressedList = new LinkedList<Control>();
		for(Control control : Control.values()) {
			if(control.isPressed()) {
				pressedList.add(control);
			}
		}
		return pressedList;
	}
	
	public void consume() {
		isPressed[ordinal()] = false;
	}
	
	public boolean isPressed() {
		return isPressed[ordinal()];
	}
	
	public void press(int keyEvent, boolean shiftDown, boolean ctrlDown) {
		if(keyEvent == Control.keyEvent[ordinal()] && shiftDown == Control.requiresShift[ordinal()] && ctrlDown == Control.requiresCTRL[ordinal()]) {
			Control.isPressed[ordinal()] = true;
		}
	}
	
	public void release(int keyEvent) {
		if(keyEvent == Control.keyEvent[ordinal()]) {
			Control.isPressed[ordinal()] = false;
		}
	}
	
	public void setTrigger(int keyEvent, boolean requiresShift, boolean requiresCTRL) {
		Control.keyEvent[ordinal()] = keyEvent;
		Control.requiresShift[ordinal()] = requiresShift;
		Control.requiresCTRL[ordinal()] = requiresCTRL;
	}
	
	public void setTrigger(int keyEvent) {
		setTrigger(keyEvent, false, false);
	}
}