package ActionTimeline;

import java.util.LinkedList;

public class ActionTimeline<X> {
	
	private Time time;
	private LinkedList<Turn<X>> turnList = new LinkedList<Turn<X>>();
	
	public ActionTimeline(Time time){
		this.time = time;
	}
	
	public void addItemToTimeline(X item, int timeUntilTurn){
		addTurnToTimeline(new Turn<X>(item, timeUntilTurn));
	}
	
	private void addTurnToTimeline(Turn<X> turnToAdd){
		int index = 0;
		for(Turn<X> turn : turnList){
			if(turn.timeUntilTurn > turnToAdd.timeUntilTurn){
				break;
			}
			index ++;
		}
		turnList.add(index, turnToAdd);
	}
	
	public X getCurrentTurnItem(){
		if(turnList.isEmpty()){
			return null;
		} else {
			return turnList.getFirst().item;
		}
	}
	
	private void incrementTime(){
		int timeIncrement = turnList.getFirst().timeUntilTurn;
		for(Turn<X> turn : turnList){
			turn.timeUntilTurn -= timeIncrement;
		}
		time.advance(0, 0, 0, timeIncrement);
	}
	
	public boolean isEmpty(){
		return turnList.isEmpty();
	}
	
	public void takeTurnForCurrentItem(Action<X> actionToPerform){
		Turn<X> currentTurn = turnList.getFirst();
		// It it important that performAction() is called before the current turn is removed from thefront of the timeline. //
		currentTurn.timeUntilTurn = actionToPerform.performAction(currentTurn.item);
		turnList.remove(currentTurn);
		addTurnToTimeline(currentTurn);
		incrementTime();
	}
	
	public void removeItemFromTimeline(X item){
		Turn<X> turnToRemove = null;
		for(Turn<X> turn : turnList){
			if(turn.item  == item){
				turnToRemove = turn;
			}
		}
		turnList.remove(turnToRemove);
	}
	
	private class Turn<Y> {
		Y item;
		int timeUntilTurn;

		Turn(Y item, int timeUntilTurn){
			this.item = item;
			this.timeUntilTurn = timeUntilTurn;
		}
	}
}
