import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FileReaderTestTest2 extends Application{

	private static ArrayList<String[]> al;
	private static ArrayList<String[]> alHist;
	private static ArrayList<Cell> cellList;
	private Cell c;
	private Stage s2;
	private Scene sc;
	private Scene sc2;
	private Stage anotherStage;
	private Button b1;
	private Label l;
	private static Hashtable<String, Cell> ht;
	private static int pos=0 ;
	private int totDropTypes = 4;
	private ArrayList<String[]> probCell = new ArrayList<>();
	private static String[] header;
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		anotherStage = primaryStage;
		MyProcessor p = new MyProcessor();
		p.initiateAnalysis();
		probCell = p.returnProbList();
		cellList = Util.getCellList();

		ht = p.getHashTable();

		primaryStage.setTitle("First JavaFx Stage");

		VBox v = new VBox();
		b1 = new Button("Next");
		l = new Label("Click next to see results");
		b1.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler);

		sc = new Scene(v);
		primaryStage.setScene(sc);
		primaryStage.setMaximized(true);
		primaryStage.show();

		v.getChildren().addAll(b1,l);
	}

	EventHandler<MouseEvent> mouseEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent arg0) {
			String[] temp = null;
			VBox vb2 = new VBox();
			GridPane grid = new GridPane();
			ScrollPane sp = new ScrollPane();
			vb2.getChildren().addAll(b1);
			
			Cell c = null;
			if(pos<probCell.size()) {
				c = ht.get(probCell.get(pos)[0]);
				l.setText(probCell.get(pos)[3]);
				l.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
				temp = probCell.get(pos);
				pos++;
			
			vb2.getChildren().add(l);

			LineChart<String, Number> myLineChart = createDoubleLineChart(c,temp);
			myLineChart.setMinSize(1300, 400);
			grid.add(myLineChart, 0, 0);
			grid.setMargin(myLineChart, new Insets(10));

			StackedBarChart<String, Number> dropTypeBarChart = createDropTypeBarChart(c);
			dropTypeBarChart.setMaxSize(1300, 400);
			grid.add(dropTypeBarChart, 0, 1);
			grid.setMargin(dropTypeBarChart, new Insets(10));
//			vb2.getChildren().add(dropTypeBarChart);
			
			
			LineChart<String, Number> utilChart = createUtilLineChart(c);
			utilChart.setMinSize(1300, 400);
			grid.add(utilChart, 0, 2);
			grid.setMargin(utilChart, new Insets(10));
//			vb2.getChildren().add(utilChart);
			
			vb2.getChildren().add(grid);
			
			
			sp.setContent(vb2);
			sc = new Scene(sp);
			anotherStage.setScene(sc);
			anotherStage.setMaximized(true);
			anotherStage.show();
		}
			else {
				Label l2 = new Label("No more cells");
				VBox vb = new VBox();
				b1.setDisable(true);
				grid.add(b1, 0, 0);
				grid.add(l2, 0, 1);
				vb.getChildren().add(grid);
				sp.setContent(vb);
				Scene endScene = new Scene(vb);
				anotherStage.setScene(endScene);
				anotherStage.setMaximized(true);
				anotherStage.show();
			}
		}
	};
	
	public LineChart<String,Number> createDoubleLineChart(Cell c, String[] problem){
		Series<String,Number> s = new Series<String,Number>();
		s.setName("Drop rate");
		Series<String,Number> s1 = new Series<String,Number>();
		s1.setName("Dowtntime/3600");
		CategoryAxis xAxis= new CategoryAxis();
		xAxis.setLabel("Time");
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Drop Rate & Downtime");
		LineChart<String, Number> myLineChart = new LineChart<String,Number>(xAxis,yAxis);
//		myLineChart.styleProperty()
		
		for(int i=0 ; i<c.getCellData().size() ; i++) {
			String time = c.getCellData().get(i)[2];
			if(time.equals(problem[3])) {
				
			}
			double dropRate = Double.parseDouble(c.getCellData().get(i)[5]);
			double downTime = Double.parseDouble(c.getCellData().get(i)[4])/3600;
			Data<String, Number> d = new XYChart.Data<String,Number>(time,dropRate);
			Data<String, Number> d2 = new XYChart.Data<String,Number>(time,downTime);
			s.getData().addAll(d);
			s1.getData().addAll(d2);
		}
		myLineChart.getData().addAll(s,s1);
		myLineChart.setTitle("Drop Rate and Downtime vs Time");
		myLineChart.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
		
		return myLineChart;
		
	}
	
	public StackedBarChart<String, Number> createDropTypeBarChart(Cell c){
		NumberAxis dropTypeYAxis = new NumberAxis();
		dropTypeYAxis.setLabel("Drop Count");
		CategoryAxis dropTypeXAxis = new CategoryAxis();
		dropTypeXAxis.setLabel("Time");
		
		StackedBarChart<String, Number> dropTypeBarChart = new StackedBarChart<String, Number>(dropTypeXAxis, dropTypeYAxis);
		dropTypeBarChart.setTitle("Distribution of drops");
		String[] dropTypes = {"Signal Strength","Quality","Timing Advance","Other"};
		ArrayList<Series<String, Number>> alSeries = new ArrayList<>();
		for(int i=0 ; i<dropTypes.length ; i++) {
			Series<String, Number> s2 = new Series<String, Number>();
			s2.setName(dropTypes[i]);
			for(int j=0 ; j<c.getCellData().size()-1 ; j++) {
				Data<String, Number> d= new Data<String, Number>(c.getCellData().get(j)[2], Double.parseDouble(c.getCellData().get(j)[i+11]));
				s2.getData().add(d);
			}
			alSeries.add(s2);
			dropTypeBarChart.getData().add(s2);
		}
		dropTypeBarChart.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
		return dropTypeBarChart;
	}
	
	public LineChart<String, Number> createUtilLineChart(Cell c){
		Series<String,Number> s = new Series<String,Number>();
		s.setName("Utilization");
		CategoryAxis xAxis= new CategoryAxis();
		xAxis.setLabel("Time");
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Utilization %");
		LineChart<String, Number> myLineChart = new LineChart<String,Number>(xAxis,yAxis);
		
		for(int i=0 ; i<c.getCellData().size() ; i++) {
			String time = c.getCellData().get(i)[2];
			double Utilization = Double.parseDouble(c.getCellData().get(i)[10]);
			Data<String, Number> d = new XYChart.Data<String,Number>(time,Utilization);
			s.getData().addAll(d);
		}
		myLineChart.getData().addAll(s);
		myLineChart.setTitle("Utilization % vs Time");
		myLineChart.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
		return myLineChart;
	}

	public static void main(String[] args) {
		launch();
	}
}

 class myKpiAnalyzer extends Analyzer{

	KpiAnalysis kpia;
	Hashtable<String, Cell> ht;
	 
	public myKpiAnalyzer() {
		kpia = new myDropRateAnalysis();
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
 
  class MyProcessor {
	  	private Analyzer an;
		private Hashtable<String, Cell> ht;
		

		public MyProcessor() {
			an = new myKpiAnalyzer();
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
  
  class myDropRateAnalysis implements KpiAnalysis{
		
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
		
		public myDropRateAnalysis() {
			al = Util.read("CellHourly", 72);
			cellHist = Util.read("AllData", 72);
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
//					CellFlag cf = new CellFlag(ht.get(al.get(i)[3]), al.get(i)[1], true, false);
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
					
					System.out.println(message);
					probArray[0] = al.get(i)[3];
					probArray[1] = al.get(i)[2];
					probArray[2] = al.get(i)[1];
					probArray[3] = message;
					probCell.add(probArray);
					double utilization = Double.parseDouble(al.get(i)[10]);
					if(utilization>utilLimt) {
//						double util = checkUtil(al.get(i)[3],al.get(i)[2]);
						message += String.format("%35s %-45f", "Current Utilization ", utilization)+"\n";
						String cellName = al.get(i)[3];
						String hour = al.get(i)[1];
						double delta = checkDelta(cellName, "Utilization", hour);
						message += String.format("%35s %-45f", "Utilization Delta", delta);
						System.out.println(message);
						probArray[0] = al.get(i)[3];
						probArray[1] = al.get(i)[2];
						probArray[2] = al.get(i)[1];
						probArray[3] = message;
						probCell.add(probArray);
					}
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
