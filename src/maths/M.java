package maths;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.LinkedList;

public class M {
	public static final double degrees5 = Math.toRadians(5);
	public static final double degrees10 = Math.toRadians(10);
	public static final double degrees15 = Math.toRadians(15);
	public static final double degrees30 = Math.toRadians(30);
	public static final double degrees45 = Math.toRadians(45);
	public static final double degrees60 = Math.toRadians(60);
	
	public static int loopUp1(int value, int modulus){
		return (value + 1)%modulus;
	}
	
	public static int loopDown1(int value, int modulus){
		return (value + modulus - 1)%modulus;
	}
	
	public static double angularDistance(double angle1, double angle2){
		double a1, a2;
		if(angle1 < angle2){
			a1 = angle1;
			a2 = angle2;
		} else {
			a1 = angle2;
			a2 = angle1;
		}
		return Math.min(Math.abs(a2 - a1), Math.abs(a1 + Math.PI*2 - a2));
	}
	
	public static int constrainValue(int min, int value, int max){
		if(value < min){
			return min;
		} else if(value > max){
			return max;
		} else {
			return value;
		}
	}
	
	public static float constrainValue(float min, float value, float max){
		if(value < min){
			return min;
		} else if(value > max){
			return max;
		} else {
			return value;
		}
	}
	
	public static double constrainValue(double min, double value, double max){
		if(value < min){
			return min;
		} else if(value > max){
			return max;
		} else {
			return value;
		}
	}
	
	/**
	 * Returns the n-th hex number (or centred hexagonal number). 
	 * @param n the radius (or equivalently, side length) of a hexagonal ball.
	 * @return the number of hexagonal tiles in that hexagonal ball.
	 */
	public static int hexNumber(int n) {
		return 3*n*(n+1) + 1;
	}
	
	/**
	 * Returns true if the shorter arc between angle1 and angle2 lies clockwise of angle1, assuming clockwise=positive angle. 
	 * @param angle1
	 * @param angle2
	 * @return
	 */
	public static boolean isClockwiseOf(double angle1, double angle2){
		angle1 = wrapAngle(angle1);
		angle2 = wrapAngle(angle2);
		angle2 -= angle1;
		angle2 = wrapAngle(angle2);
		return (angle2 < Math.PI);
	}
	
	public static double mean(double[] valueList){
		float total = 0;
		for(double value : valueList){
			total += value;
		}
		return total/valueList.length;
	}
	
	public static int mean(int[] valueList){
		int total = 0;
		for(int value : valueList){
			total += value;
		}
		return total/valueList.length;
	}
	
	public static float mean(float[] valueList){
		float total = 0;
		for(float value : valueList){
			total += value;
		}
		return total/valueList.length;
	}
	
	public static double median(double[] valueList){
		Arrays.sort(valueList);
		int medIndex = valueList.length/2;
		return valueList[medIndex];
	}
	
	public static float median(float[] valueList){
		Arrays.sort(valueList);
		int medIndex = valueList.length/2;
		return valueList[medIndex];
	}
	
	public static double medianPercentile(double[] valueList, int percentile){
		Arrays.sort(valueList);
		int percentileIndex = (valueList.length*percentile)/100;
		return valueList[percentileIndex];
	}

	public static double rand(double max){
		return (Math.random()*max);
	}

	public static double rand(double min, double max){
		return (Math.random()*(max - min) + min);
	}

	public static double randAngle(){
		return rand(Math.PI*2);
	}
	
	public static float randf(double max){
		return (float)rand(max);
	}
	
	public static float randf(double min, double max){
		return (float)rand(min, max);
	}

	public static float randfAngle(){
		return randf(Math.PI*2);
	}
	
	/**
	 * Returns an integer 0 <= x < max
	 * @param max
	 * @return
	 */
	public static int randInt(int max){
		return (int)(Math.random()*max);
	}
	
	/**
	 * Returns an integer min <= x <= max
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randInt(int min, int max){
		return M.randInt(max + 1 - min) + min;
	}
	
	public static boolean roll(double percentChance){
		return (Math.random() < percentChance);
	}
	
	public static Point2D.Double rotate(double x, double y, double angle){
		return new Point2D.Double(x*Math.cos(angle) - y*Math.sin(angle), x*Math.sin(angle) + y*Math.cos(angle));
	}
	
	public static Point2D.Double rotate(Point2D vector, double angle){
		return rotate(vector.getX(), vector.getY(), angle);
	}
	
	public static Point2D scale(Point2D p, double scale){
		return new Point2D.Double(p.getX()*scale, p.getY()*scale);
	}

	public static double sum(double[] valueList){
		double total = 0;
		for(double value: valueList){
			total += value;
		}
		return total;
	}

	public static int sum(int[] valueList){
		int total = 0;
		for(int value: valueList){
			total += value;
		}
		return total;
	}
	
	public static double angle(Point2D p){
		return Math.atan2(p.getY(), p.getX());
	}
	
	public static double angle(Point2D p1, Point2D p2){
		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();
		double angle = Math.atan2(dy, dx);
		return angle;
	}
	
	public static double angle(Point2D p, double x, double y){
		double dx = x - p.getX();
		double dy = y - p.getY();
		double angle = Math.atan2(dy, dx);
		return angle;
	}
	
	public static double angle(double x1, double y1, double x2, double y2){
		double dx = x2 - x1;
		double dy = y2 - y1;
		double angle = Math.atan2(dy, dx);
		return angle;
	}

	public static Point add(Point p1, Point p2){
		return new Point(p1.x + p2.x, p1.y + p2.y);
	}

	public static Point2D add(Point2D p1, Point2D p2){
		return new Point2D.Double(p1.getX() + p2.getX(), p1.getY() + p2.getY());
	}

	public static Point2D.Float add(Point2D.Float p1, Point2D.Float p2){
		return new Point2D.Float(p1.x + p2.x, p1.y + p2.y);
	}

	public static Point subtract(Point p1, Point p2){
		return new Point(p1.x - p2.x, p1.y - p2.y);
	}

	public static Point add(Point p, int x, int y){
		return new Point(p.x + x, p.y + y);
	}

	public static Point2D.Double add(Point2D p, double x, double y){
		return new Point2D.Double(p.getX() + x, p.getY() + y);
	}

	public static Point2D.Float add(Point2D.Float p, float x, float y){
		return new Point2D.Float(p.x + x, p.y + y);
	}

	public static double lengthSq(Point2D p){
		return Point.distanceSq(0, 0, p.getX(), p.getY());
	}

	public static double length(Point2D p){
		return Point.distance(0, 0, p.getX(), p.getY());
	}

	public static double dist(Point2D p1, Point2D p2){
		return Point.distance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
	
	public static <X> X chooseRandom(X[] list){
		return list[(int)(Math.random()*list.length)];
	}
	
	public static <X> X chooseRandom(LinkedList<X> list){
		return list.get((int)(Math.random()*list.size()));
	}
	
	public static <X> X removeRandom(LinkedList<X> list){
		return list.remove((int)(Math.random()*list.size()));
	}
	
	public static <X> int indexOf(X[] list, X element){
		for(int i = 0; i < list.length; i ++){
			if(list[i] == element){
				return i;
			}
		}
		return -1;
	}
	
	public static String toString(double value, int decimalPlaces){
		double order = Math.pow(10, decimalPlaces);
		value = Math.round(value*order);
		int units = (int)(value/order);
		int decimal = (int)(value%order);
		return units+"."+decimal;
	}
	
	/**
	 * Returns a value equivalent to the given angle, in the interval [0, 2*PI).
	 * @param angle
	 * @return
	 */
	public static double wrapAngle(double angle){
		angle = angle % (2*Math.PI);
		angle = (angle + 2*Math.PI) % (2*Math.PI);
		return angle;
	}
}