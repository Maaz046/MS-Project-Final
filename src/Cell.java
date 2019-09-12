import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class Cell {

	private ArrayList<String[]> cellData;
	private String name;

	public Cell(String name, ArrayList<String[]> cellData) {
		this.cellData = cellData;
		this.name = name;
	}

	/*	Overriding equals is important when making hash tables to set a custom equality 
		criterion in case 2 different objects with the same cell names are created
	 */
	public boolean equals(Cell c) {
		if(this.name == c.name) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public ArrayList<String[]> getCellData(){
		return cellData;
	}
	
	public String getCellName() {
		return name;
	}
}