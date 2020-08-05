package files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

public class TextFileHandler {
	
	public static Scanner startReadingFromFile(String filename){
		Scanner scanner = null;
		try {
			filename = FileUtils.executionPath+filename;// Append the execution path to the start of the filename to ensure we look in the correct directory.
			File file = new File(filename);
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return scanner;
	}
	
	public static Scanner startReadingFromFile(String filename, String charsetName){
		Scanner scanner = null;
		try {
			filename = FileUtils.executionPath+filename;// Append the execution path to the start of the filename to ensure we look in the correct directory.
			File file = new File(filename);
			scanner = new Scanner(file, charsetName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return scanner;
	}
	
	public static PrintWriter startWritingToFile(String filename, boolean append){
		PrintWriter pw = null;
		try {
			filename = FileUtils.executionPath+filename;// Append the execution path to the start of the filename to ensure we look in the correct directory.
			File file = new File(filename);
			if(file.getParentFile() != null){
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
			pw = new PrintWriter(new FileOutputStream(file, append));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pw;
	}
	
	public static PrintWriter startWritingToFile(String filename){
		return startWritingToFile(filename, false);
	}
	
	public static LinkedList<String> readEntireFile(String filename){
		LinkedList<String> lineList = new LinkedList<String>();
		Scanner s = TextFileHandler.startReadingFromFile(filename);
		while(s.hasNext()){
			lineList.add(s.nextLine());
		}
		return lineList;
	}
	
	public static LinkedList<String> readEntireFile(String filename, String charsetName){
		LinkedList<String> lineList = new LinkedList<String>();
		Scanner s = TextFileHandler.startReadingFromFile(filename, charsetName);
		while(s.hasNext()){
			lineList.add(s.nextLine());
		}
		return lineList;
	}
	
	public static void writeEntireFile(String filename, LinkedList<String> lineList){
		PrintWriter pw = startWritingToFile(filename);
		for(String line : lineList){
			pw.println(line);
		}
		pw.close();
	}
}