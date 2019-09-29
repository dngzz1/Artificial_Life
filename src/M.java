class M extends maths.M {
	
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
}