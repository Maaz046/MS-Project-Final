import java.awt.List;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.plaf.ButtonUI;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT;

import com.sun.javafx.charts.Legend;
import com.sun.javafx.geom.Rectangle;

import javafx.application.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.*;

public class Vizualizer extends Application{

	private Cell c;
	private Stage s2;
	private Scene sc;
	private Scene sc2;
	private Stage anotherStage;
	private Button b1;
	private Label l;
	private Hashtable<String, Cell> ht;
	private static int pos=0 ;
	private ArrayList<Cell> cellList;
	private int totDropTypes = 4;
	private ArrayList<String[]> probCell = new ArrayList<>();


	@Override
	public void start(Stage primaryStage) throws Exception {
		anotherStage = primaryStage;
		Processor p = new Processor();
		p.initiateAnalysis();
		probCell = p.returnProbList();
		cellList = Util.getCellList();

		ht = p.getHashTable();
		Cell c = ht.get("26091");

		primaryStage.setTitle("AKAT");

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
			
			
			LineChart<String, Number> utilChart = createUtilLineChart(c);
			utilChart.setMinSize(1300, 400);
			grid.add(utilChart, 0, 2);
			grid.setMargin(utilChart, new Insets(10));
			
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
			for(int j=0 ; j<c.getCellData().size() ; j++) {
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
