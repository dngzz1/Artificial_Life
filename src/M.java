class M extends maths.M {
	
	static double[][] cloneMatrix(double[][] matrix){
		if(matrix.length == 0) {
			return new double[0][0];
		} else {
			double[][] clone = new double[matrix.length][matrix[0].length];
			for(int row = 0; row < matrix.length; row ++) {
				for(int col = 0; col < matrix[row].length; col ++) {
					clone[row][col] = matrix[row][col];
				}
			}
			return clone;
		}
	}
	
	static double[] cloneVector(double[] vector){
		double[] clone = new double[vector.length];
		for(int i = 0; i < vector.length; i ++) {
			clone[i] = vector[i];
		}
		return clone;
	}
	
	static float mutateFloat(float value){
		float dice = M.randf(1.0f);
		dice = dice*dice*dice;
		float mutatedValue = M.roll(0.5) ? value + dice*(1.0f - value) : value - dice*value;
		if(mutatedValue <= 0 || mutatedValue >= 1){
			return value;
		} else {
			return mutatedValue;
		}
	}
	
	static void setRandomEntries(double[][] matrix, double min, double max) {
		for(int row = 0; row < matrix.length; row ++) {
			for(int col = 0; col < matrix[row].length; col ++) {
				matrix[row][col] = rand(min, max);
			}
		}
	}
	
	static void setRandomEntries(double[] vector, double min, double max) {
		for(int i = 0; i < vector.length; i ++) {
			vector[i] = rand(min, max);
		}
	}
}