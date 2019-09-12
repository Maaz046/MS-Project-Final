import java.util.ArrayList;
import java.util.Hashtable;

public class KpiAnalyzer extends Analyzer{

	KpiAnalysis kpia;
	Hashtable<String, Cell> ht;
	
	public KpiAnalyzer() {
		kpia = new DropRateAnalysis();
		ht = kpia.returnKpiHashtable();
	}
	
	public void performAnalysis() {
		kpia.analyze();
	}

	public Hashtable<String, Cell> returnHashTable() {
		return ht;
	}

	@Override
	public ArrayList<String[]> getProbList() {
		return kpia.getProbList();
	}
}