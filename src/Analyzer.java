import java.util.ArrayList;
import java.util.Hashtable;

public abstract class Analyzer{
	
	public abstract void performAnalysis();
	
	public abstract Hashtable<String, Cell> returnHashTable();
	
	public abstract ArrayList<String[]> getProbList();
	

}
