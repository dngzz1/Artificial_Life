package ActionTimeline; 

public class Time {
	
	int day;
	int hour;
	int minute;
	int second;
	
	public Time(int day, int hour, int minute, int second) {
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	public void advance(int days, int hours, int minutes, int seconds){
		this.day += days;
		this.hour += hours;
		this.minute += minutes;
		this.second += seconds;
		validate();
	}
	
	@Override
	public String toString(){
		String str = "Day "+day+", ";
		str = str+((hour < 10) ?  "0"+hour : hour)+":";
		str = str+((minute < 10) ?  "0"+minute : minute)+":";
		str = str+((second < 10) ?  "0"+second : second);
		
		return str;
	}
	
	private void validate(){
		minute += second/60;
		second = second%60;
		hour += minute/60;
		minute = minute%60;
		day += hour/24;
		hour = hour%24;
	}
}