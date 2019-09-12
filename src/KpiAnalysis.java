import java.util.ArrayList;
import java.util.Hashtable;

public interface KpiAnalysis {
	
	public void analyze();
	
	public Hashtable<String, Cell> returnKpiHashtable();
	
	public ArrayList<String[]> getProbList();
	
}
