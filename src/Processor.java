import java.util.ArrayList;
import java.util.Hashtable;

public class Processor {

	private Analyzer an;
	private Hashtable<String, Cell> ht;
	

	public Processor() {
		an = new KpiAnalyzer();
	}

	public void initiateAnalysis() {
		an.performAnalysis();
		ht = an.returnHashTable();
	}
	
	public Hashtable<String, Cell> getHashTable() {
		return ht;
	}
	
	public ArrayList<String[]> returnProbList(){
		return an.getProbList();
	}
}