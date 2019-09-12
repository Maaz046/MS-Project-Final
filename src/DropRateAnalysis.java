import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Hashtable;

import javafx.application.Application;
import javafx.stage.Stage;


public class DropRateAnalysis implements KpiAnalysis{
	
//	Util util;
	private ArrayList<String[]> al = new ArrayList<>();
	private ArrayList<String[]> cellHist;
	private Hashtable<String, Cell> ht;
	private String[] header;
	
	private ArrayList<Cell> cellList = new ArrayList<>();
	
	private int dropRateLim = 3;
	private int utilLimt = 50;
	private int noOfDays = 4;
	private int noOfCells = 72;
	private ArrayList<String[]> probCell = new ArrayList<String[]>();
	
	public DropRateAnalysis() {
		al = Util.read("CellHourly",noOfCells);
		cellHist = Util.readCellHistory(noOfCells,noOfDays);
		
//		al = Util.read("CellHourlyOneRowTest",noOfCells);  //For testing AKAT on one row data
		
		cellList = Util.createCells(al, cellHist);
		ht = Util.fillHashTable(cellList);
		header = Util.getHeaderOnly(al);
	}
	
	
	/**
	 * It would be more efficient to work on a simple array list or arrays rather than an array list of cell objects
	 * because using cell objects would require using 2 for loops. One to loop through cell objects themselves &
	 * the other to loop through the array of data within the cell object. Hence it would be better to use cell
	 * objects for visual facet of the program only.
	 */
	@Override
	public void analyze() {
		String s = "has high TCH Drop Rate due to downtime\n";
		StringBuilder sb = new StringBuilder(s);
		for(int i=1 ; i<al.size() ; i++) {
			String[] probArray  = new String[4];
			double dropRate = Double.parseDouble(al.get(i)[5]);
			double dT = Double.parseDouble(al.get(i)[4]);
			double dropCount = Double.parseDouble(al.get(i)[6]);
			
			if(dropRate>dropRateLim && dT>0 && dropCount>10) {
				String message = al.get(i)[3]+ " " + s + String.format("%20s %-45s", " ", "at "+al.get(i)[2])+"\n";
				System.out.println(message);
				probArray[0] = al.get(i)[3];
				probArray[1] = al.get(i)[2];
				probArray[2] = al.get(i)[1];
				probArray[3] = message;
				
				probCell.add(probArray);
			}
			
			else if (dropRate>dropRateLim && dT==0 && dropCount>10) {
				
				String ss = "has high TCH drop rate";
				Double[] dropPerc = {Double.parseDouble(al.get(i)[11])*100/dropCount,Double.parseDouble(al.get(i)[12])*100/dropCount,Double.parseDouble(al.get(i)[13])*100/dropCount,Double.parseDouble(al.get(i)[14])*100/dropCount};
				int pos = checkHighestDrops(dropPerc);
				String message = al.get(i)[3] + " " + al.get(0)[pos] + " detected to be elevated \n" + String.format("%20s %-45s", " ", "at "+ al.get(i)[2])+"\n";
				
				if(pos==11) {
					message += "\t\tCell may be overshooting, check antenna tilts or neighbor cell outage\n";
				}
				else if(pos==12) {
					message += "\t\tInterference issue, check for frequency clashes and neigbor cell power\n";
				}
				else if(pos==13) {
					message+= "\t\tCheck for erroneously set antenna tilts\n";
				}
				else {
					message+= "\t\tCheck cell for hardware faults";
				}
				
				probArray[0] = al.get(i)[3];
				probArray[1] = al.get(i)[2];
				probArray[2] = al.get(i)[1];
				probArray[3] = message;
				double utilization = Double.parseDouble(al.get(i)[10]);
				if(utilization>utilLimt) {
					message += String.format("%35s %-45f", "Current Utilization ", utilization)+"\n";
					String cellName = al.get(i)[3];
					String hour = al.get(i)[1];
					double delta = checkDelta(cellName, "Utilization", hour);
					message += String.format("%35s %-45f", "Utilization Delta", delta);
					probArray[0] = al.get(i)[3];
					probArray[1] = al.get(i)[2];
					probArray[2] = al.get(i)[1];
					probArray[3] = message;
				}
				System.out.println(message);
				probCell.add(probArray);
			}
		}
	}
	
	
	public int checkHighestDrops(Double[] dropsArray) {
		int pos=11; //11  is the column where drop types start (11-14)
		double max=0;
		for(int i=0 ; i<dropsArray.length-1 ; i++) {
			if(dropsArray[i]>max) {
				max = dropsArray[i];
				pos += i;
			}
		}
		return pos;
	}
	
	/*
	 * Kpi name is an argument in this to avoid hard coding column number
	 * Need to specify exact kpi name else it wouldn't work
	 */
	public double checkDelta(String cellID, String kpiName, String hour) {
		Cell c = ht.get(cellID);
		ArrayList<String[]> str = c.getCellData();
		
		//Finds column position of kpi given in method argument
		int pos = 0;
		for(int i=0 ; i<header.length ; i++) {
			String kpi = header[i];
			if(kpi.equals(kpiName)) {
				break;
			}
			pos++;
		}
		
		
		double sum = 0;
		double currentKpi = 0;
		for(int i=0 ; i<str.size() ; i++) {
			if(str.get(i)[1].equals(hour)) {
				if(i<str.size()-24) { //last 24 entries represent latest days stats which are not to be included in average to calculate delta hence subtract here
					double d = Double.parseDouble(str.get(i)[pos]);
					sum += d;
				}
				else {
					currentKpi = Double.parseDouble(str.get(i)[pos]);					
				}
			}
		}
		double delta = currentKpi - (sum/(noOfDays-1));
		return delta;
	}

	@Override
	public Hashtable<String, Cell> returnKpiHashtable() {
		return ht;
	}


	public ArrayList<String[]> getProbList() {
		return probCell;
	}	
}
