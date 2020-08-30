import java.io.PrintWriter;
import java.util.Date;

import javax.swing.JOptionPane;

import files.TextFileHandler;

class AutotestManager {
	Autotest test;
	String logFileName;
	int numberOfSimulations;
	int simulationLength;
	int simulationNumber = 1;
	
	private static Autotest chooseAutotest() {
		Autotest[] choiceList = {new Autotest_Population(), new Autotest_Predation()};
		Autotest choice = (Autotest) JOptionPane.showInputDialog(null, "Choose autotest type", "Autotest", JOptionPane.QUESTION_MESSAGE, null, choiceList, null);
		if(choice == null) {
			System.exit(0);
		}
		return choice;
	}
	
	private static void resetSimulation() {
		ArtificialLife.turnList.clear();
		ArtificialLife.setup();
		ArtificialLife.stepCounter = 0;
		ArtificialLife.totalChildren = 0;
		ArtificialLife.totalChildrenWithTwoParents = 0;
		ArtificialLife.totalDeathsBy = new int[CauseOfDeath.values().length];
	}
	
	public AutotestManager() {
		this(chooseAutotest());
	}
	
	private AutotestManager(Autotest test) {
		this.test = test;
		this.logFileName = "logs/"+test.toString()+"-"+new Date().getTime()+".txt";
	}
	
	public void setup() {
		// Get parameters from user. //
		String numberOfSimulationsInput = JOptionPane.showInputDialog("Number of simulations:");
		numberOfSimulations = Integer.parseInt(numberOfSimulationsInput);
		String simulationLengthInput = JOptionPane.showInputDialog("Simulation length (steps):");
		simulationLength = Integer.parseInt(simulationLengthInput);
		test.setup();
		
		// Start printing log file. //
		PrintWriter pw = startWritingLog();
		pw.println(test.toString());
		pw.println("Format: "+test.formatString());
		pw.println("AUTO-RUNNING "+numberOfSimulations+" SIMULATIONS FOR "+simulationLength+" STEPS EACH");
		pw.println();
		pw.println("SIMULATION #"+simulationNumber+" START");
		pw.close();
	}
	
	public PrintWriter startWritingLog() {
		return TextFileHandler.startWritingToFile(logFileName, true);
	}
	
	public void step() {
		// Step the autotest. //
		test.step(this);
		
		// If the simulation has reached the termination condition, we log the results and restart. //
		if(ArtificialLife.stepCounter >= simulationLength) {
			// Print the log of this simulation to file. //
			String simulationEndMessage = "SIMULATION #"+simulationNumber+" COMPLETE";
			PrintWriter pw = startWritingLog();
			pw.println(simulationEndMessage);
			System.out.println(simulationEndMessage);
			
			simulationNumber ++;
			if(simulationNumber <= numberOfSimulations) {
				// Reset for the next simulation. //
				resetSimulation();
				pw.println();
				pw.println("SIMULATION #"+simulationNumber+" START");
				pw.close();
			} else {
				// Exit program if this was the final simulation. //
				System.exit(0);
			}
		}
	}
}

interface Autotest {
	public abstract String formatString();
	public abstract void setup();
	public abstract void step(AutotestManager manager);
}

class Autotest_Population implements Autotest {
	int stepsPerCensus;
	
	@Override
	public String formatString() {
		return "STEP:POPULATION";
	}
	
	@Override
	public void setup() {
		String stepsPerCensusInput = JOptionPane.showInputDialog("Steps per census:");
		stepsPerCensus = Integer.parseInt(stepsPerCensusInput);
	}
	
	@Override
	public void step(AutotestManager manager) {
		if(ArtificialLife.stepCounter % stepsPerCensus == 0) {
			PrintWriter pw = manager.startWritingLog();
			pw.println(ArtificialLife.stepCounter+":"+ArtificialLife.getCellCount());
			pw.close();
		}
	}
	
	@Override
	public String toString() {
		return "Autotest_Population";
	}
}

class Autotest_Predation implements Autotest {
	int stepsPerCensus;
	
	@Override
	public String formatString() {
		return "STEP:FOOD_EATEN_TOTAL:FOOD_EATEN_BY_PREDATION";
	}
	
	@Override
	public void setup() {
		String stepsPerCensusInput = JOptionPane.showInputDialog("Steps per census:");
		stepsPerCensus = Integer.parseInt(stepsPerCensusInput);
	}

	@Override
	public void step(AutotestManager manager) {
		if(ArtificialLife.stepCounter % stepsPerCensus == 0) {
			int totalFoodEaten = 0, totalFoodEatenByPredation = 0;
			for(Stepable stepable : ArtificialLife.getStepList()) {
				if(stepable instanceof Cell) {
					Cell cell = (Cell) stepable;
					totalFoodEaten += cell.lifetimeFoodEaten;
					totalFoodEatenByPredation += cell.lifetimeFoodEatenByPredation;
				}
			}
			PrintWriter pw = manager.startWritingLog();
			pw.println(ArtificialLife.stepCounter+":"+totalFoodEaten+":"+totalFoodEatenByPredation);
			pw.close();
		}
	}
	
	@Override
	public String toString() {
		return "Autotest_Predation";
	}
}