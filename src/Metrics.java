import maths.M;

class Metrics {
	public static CellMetric<Integer> attackStrengthMetric = new CellMetric<Integer>() {
		@Override
		public Integer data(Cell cell) {
			return cell.attackStrength;
		}
	};
	public static CellMetric<Integer> biteSizeMetric = new CellMetric<Integer>() {
		@Override
		public Integer data(Cell cell) {
			return cell.biteSize;
		}
	};
	public static CellMetric<Integer> buildStrengthMetric = new CellMetric<Integer>() {
		@Override
		public Integer data(Cell cell) {
			return cell.buildStrength;
		}
	};
	public static CellMetric<Integer> energyCapacityMetric = new CellMetric<Integer>() {
		@Override
		public Integer data(Cell cell) {
			return cell.energyStoreSize;
		}
	};
	public static CellMetric<Integer> energyUseMetric = new CellMetric<Integer>() {
		@Override
		public Integer data(Cell cell) {
			return (int)(cell.getEnergyCostPerTurn()*cell.speed); // Multiplying by speed gives energy use per game step. //
		}
	};
	public static CellMetric<Integer> hpMetric = new CellMetric<Integer>() {
		@Override
		public Integer data(Cell cell) {
			return cell.hpMax;
		}
	};
	public static CellMetric<Integer> lifetimeFoodEatenMetric = new CellMetric<Integer>() {
		@Override
		public Integer data(Cell cell) {
			return cell.lifetimeFoodEaten;
		}
	};
	public static CellMetric<Integer> lifetimeFoodEatenByPredationMetric = new CellMetric<Integer>() {
		@Override
		public Integer data(Cell cell) {
			return cell.lifetimeFoodEatenByPredation;
		}
	};
	
	private static int getCellCount(CellCondition condition) {
		int count = 0;
		for(Stepable stepable : ArtificialLife.getStepList()){
			if(stepable instanceof Cell){
				if(condition.isSatisfiedBy((Cell)stepable)) {
					count ++;
				}
			}
		}
		return count;
	}
	
	public static int[] getMinMedMax(CellMetric<Integer> metric, CellCondition condition) {
		int cellCount = getCellCount(condition); // Number of cells satisfying the condition.
		if(cellCount == 0) {
			return new int[] {0, 0, 0};
		}
		int[] dataList = new int[cellCount];
		int min = Integer.MAX_VALUE;
		int max = 0;
		int i = 0;
		for(Stepable stepable : ArtificialLife.getStepList()){
			if(stepable instanceof Cell){
				Cell cell = (Cell)stepable;
				if(condition.isSatisfiedBy(cell)) {
					dataList[i] = metric.data(cell);
					min = Math.min(min, dataList[i]);
					max = Math.max(max, dataList[i]);
					i ++;
				}
			}
		}
		
		int[] data = new int[3];
		data[0] = min;
		data[1] = M.median(dataList);
		data[2] = max;
		return data;
	}
	
	public static int getMedian(CellMetric<Integer> metric, CellCondition condition) {
		int cellCount = getCellCount(condition); // Number of cells satisfying the condition.
		if(cellCount == 0) {
			return 0;
		}
		int[] dataList = new int[cellCount];
		int i = 0;
		for(Stepable stepable : ArtificialLife.getStepList()){
			if(stepable instanceof Cell){
				Cell cell = (Cell)stepable;
				if(condition.isSatisfiedBy(cell)) {
					dataList[i] = metric.data(cell);
					i ++;
				}
			}
		}
		return M.median(dataList);
	}
	
	public static int getTotal(CellMetric<Integer> metric, CellCondition condition) {
		int cellCount = getCellCount(condition); // Number of cells satisfying the condition.
		if(cellCount == 0) {
			return 0;
		}
		int dataTotal = 0;
		for(Stepable stepable : ArtificialLife.getStepList()){
			if(stepable instanceof Cell){
				Cell cell = (Cell)stepable;
				if(condition.isSatisfiedBy(cell)) {
					dataTotal += metric.data(cell);
				}
			}
		}
		return dataTotal;
	}
	
	private interface CellCondition {
		public boolean isSatisfiedBy(Cell cell);
	}
	
	public static class MatchSpeciesCondition implements CellCondition {
		
		Species species;
		
		MatchSpeciesCondition(Species species){
			this.species = species;
		}
		
		@Override
		public boolean isSatisfiedBy(Cell cell) {
			return (cell.species == species);
		}
	}
	
	public static interface CellMetric<X> {
		public X data(Cell cell);
	}
}


