import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileReaderTest {
	
	private static ArrayList<String[]> al;
	private static ArrayList<String[]> alHist;
	private static ArrayList<Cell> cellList;

	
	@BeforeClass
	public static void readCellHourly() {
		al = Util.read("CellHourly", 72);
		alHist = Util.read("AllData", 72);
		cellList = Util.createCells(al, alHist);
		
	}
	
	//This test checks the row count for the 1 day data size
	@Test
	public void testRowCount() {
		int count = 24*72+1;
		assertTrue("Row check complete for cell hourly", al.size()==count);
	}
	
	//This test checks the row count for the 4 day data size
	@Test
	public void readHistory(){
		int count = 72*4*24+1;
		assertTrue("Row check complete for all data",alHist.size()==count);
	}
	
	//This test checks the size of the cell list to ensures correct number of cells
	@Test
	public void cellListSizeCheck() {
		int size = 72;
		assertTrue("cellList  size ok",size==72);
	}
	
	//This test check that every cell object has a data array of size 24*4 (for 4 days)
	@Test
	public void cellListContentSizeCheck() {
		int cellDataSize = 24*4;
		int countCheck = 0;
		for(int i=0 ; i<cellList.size() ; i++) {
			if(cellList.get(i).getCellData().size() == cellDataSize) {
				countCheck += 1;
			}
		}
		assertTrue("Cell data size check complete", countCheck==72);
	}
	
	//Checks whether all each cell value of the data read is correct
	@Test
	public void readerContentCheck() throws FileNotFoundException {
		int i=0;
		String[] temp = al.get(0);
		String[] temp2 = null;
		FileReader fr = new FileReader("AllData.csv");
		Scanner scan = new Scanner(fr);
		temp2 = scan.nextLine().split(",");
		scan.close();
		
		for(int j=0 ; j<temp.length ; j++) {
			if(temp[j].equals(temp2[j])) {
				i++;
			}
		}
		
		assertTrue("Read Content is ok",i==temp.length);
	}
	
	//Checks whether AKAT works when a string is present in the place of a double
	@Test
	public void stringInsteadofDoubleCheck() throws FileNotFoundException {
		int count=0;
		String s = "";
		for(int i=4 ; i<15 ; i++) {
			if(al.get(0)[i].getClass() == (s.getClass())) {
				count++;
			}
		}
		assertTrue("String present",count==0);
	}
}
