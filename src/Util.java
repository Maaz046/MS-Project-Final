import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public abstract class Util {

	private final static Hashtable<String, Cell> ht = new Hashtable<>(100,0.67f); //why 0.67 used - good balance btw 0.75 and 0.5;
	private static ArrayList<Cell> cellList;
	private static ArrayList<String[]> alHist = new ArrayList<>();

	public static ArrayList<String[]> read(String fN, int nCells) {
		int nRow = nCells*24;//NRow allows us to determine the size of the ArrayList giving performance similar to arrays
		Scanner scan;
		ArrayList<String[]> arrrayLst = new ArrayList<>(nRow);//ArrayList is used because every index needs to hold an array of strings. Allow ease of looping rather than using 2 dim looping

		try {
			FileReader fr = new FileReader(fN+".csv");
			scan = new Scanner(fr);
			while(scan.hasNext()) {
				String s = scan.nextLine();
				String[] store = s.split(",");
				arrrayLst.add(store);
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return arrrayLst;
	}

	//This method reads the history file and concatenates latest data with the history
	//for ease in analysis. It is assumed that history will be present in a separate
	//file than the one containing present day data.

	public static ArrayList<String[]> readCellHistory(int nCells, int totalDays) { //can add days*cellNo data to determine arrayList size
		int listSize = nCells*totalDays*24;
		ArrayList<String[]> arrayLstHst = new ArrayList<>(listSize);//list size prevents array list expansion which reduces performance
		Scanner scan;

		try {
			FileReader fr = new FileReader("AllData.csv");
//			FileReader fr = new FileReader("AllDataOneRowTest.csv");// For testing AKAT on one row data with FileReaderTest2 in main
//			FileReader fr = new FileReader("8DayHistoryTest.csv");//For testing 8 day long history with FileReaderTest2 in main
			
			scan = new Scanner(fr);
			while(scan.hasNext()) {
				String s = scan.nextLine();
				String[] store = s.split(",");
				arrayLstHst.add(store);
			}
			scan.close();
			
		}
		catch (Exception e) {
			System.out.println("File not found");
		}
		alHist= arrayLstHst;
		return arrayLstHst;
	}

	public static Hashtable<String, Cell> fillHashTable(ArrayList<Cell> cellList) {
		for(int i=0 ; i<cellList.size() ; i++) {
			String name = cellList.get(i).getCellName();
			ht.put(name, cellList.get(i));			
		}

		return ht;
	}
	
	public static String[] getHeaderOnly(ArrayList<String[]> al) {
		return al.get(0);
	}


	public static ArrayList<Cell> createCells(ArrayList<String[]> currentCellData, ArrayList<String[]> allData) {
		
		Set<String> cellSet = new HashSet<>();
		for(int i=1 ; i<currentCellData.size() ; i++) {
			cellSet.add(currentCellData.get(i)[3]);
		}
		
		String[] cellArray = cellSet.toArray(new String[cellSet.size()]);
		cellList = new ArrayList<>();
		
		for(int i=0 ; i<cellArray.length; i++) {
			ArrayList<String[]> temp = new ArrayList<>();
			for(int j=0 ; j<alHist.size() ; j++) {
				if(cellArray[i].equals(alHist.get(j)[3])) {
					temp.add(alHist.get(j));
				}
			}
			Cell c = new Cell(cellArray[i],temp);
			cellList.add(c);
		}

		return cellList;
	}

	public static ArrayList<Cell> getCellList() {
		return cellList;
	}	
}