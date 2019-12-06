import java.util.LinkedList;
import java.util.ListIterator;

import general.Util;

class TurnList {
	
	private LinkedList<Turn> turnList = new LinkedList<Turn>();
	
	public void add(Stepable object, int stepsFromNow) {
		ListIterator<Turn> iterator = turnList.listIterator();
		while(iterator.hasNext()) {
			Turn turn = iterator.next();
			if(turn.stepsFromNow == stepsFromNow) {
				// If this is the turn the object belongs to, then add it here. //
				turn.stepList.add(object);
				return;
			} else if(turn.stepsFromNow > stepsFromNow) {
				// If this turn happens after the object's turn, then add a turn before this one. //
				Turn turnToInsert = new Turn(stepsFromNow);
				turnToInsert.stepList.add(object);
				iterator.previous();
				iterator.add(turnToInsert);
				return;
			}
		}
		// If we have gone through the whole list and not added the object's turn, then it goes at the end. //
		Turn turnToInsert = new Turn(stepsFromNow);
		turnToInsert.stepList.add(object);
		turnList.addLast(turnToInsert);
	}
	
	public void clear() {
		turnList.clear();
	}
	
	public boolean contains(Stepable stepable) {
		for(Turn turn : turnList) {
			if(turn.stepList.contains(stepable)) {
				return true;
			}
		}
		return false;
	}
	
	public LinkedList<Stepable> getStepList(){
		LinkedList<Stepable> stepList = new LinkedList<Stepable>();
		for(Turn turn : turnList) {
			stepList.addAll(turn.stepList);
		}
		return stepList;
	}
	
	public void remove(Stepable object) {
		for(Turn turn : turnList) {
			turn.stepList.remove(object);
		}
	}
	
	public void step() {
		if(!turnList.isEmpty()) {
			// If the first turn is set to happen this step, then make it happen and remove it. //
			if(turnList.getFirst().stepsFromNow == 0) {
				turnList.removeFirst().step();
			}
			// After the turn has happened, all remaining turns are one step closer to happening. //
			for(Turn turn : turnList) {
				turn.stepsFromNow --;
			}
		}
	}
	


	private class Turn {
		int stepsFromNow;
		LinkedList<Stepable> stepList = new LinkedList<Stepable>();
		
		Turn(int stepsFromNow){
			this.stepsFromNow = stepsFromNow;
		}
		
		void step() {
			for(Stepable stepable : Util.cloneList(stepList)){
				stepable.step();
				int stepsToNextTurn = stepable.getStepsToNextTurn();
				if(stepsToNextTurn > 0) {
					add(stepable, stepsToNextTurn);
				}
			}
		}
	}
}