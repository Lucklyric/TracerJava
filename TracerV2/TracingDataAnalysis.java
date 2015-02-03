import java.awt.*;
import java.io.File;
import java.util.*;

import org.math.R.RserverConf;
import org.math.R.Rsession;
import org.rosuda.REngine.REXPMismatchException;


public class TracingDataAnalysis {
	private Vector<TracingLayer> sampleLayers;
	private Vector<Vector<Point>> samples;
	private Point averageCenterPoint;
	private int totalSamples;
	Vector<Vector<Double>> disctancsByGroups;
	private Rsession Rserver;
	public TracingDataAnalysis(){
		this.sampleLayers = new Vector<TracingLayer>();
		this.disctancsByGroups = new Vector<Vector<Double>>();
		this.samples = new Vector<Vector<Point>>();
		this.Rserver = Rsession.newInstanceTry(System.out,null);
		averageCenterPoint = new Point();
		totalSamples = 0;
	}
	
	public void addLayer(TracingLayer tempLayer){
		if (tempLayer.getPoints().size() == 0) return;
		this.sampleLayers.addElement(tempLayer);
		Vector<Point> tmpPointList = new Vector<Point>();
		for (int i = 0 ; i < tempLayer.getPoints().size(); i++){
			for (int j = 0 ; j < (tempLayer.getPoints()).get(i).size(); j++){
				tmpPointList.addElement(tempLayer.getPoints().get(i).get(j));
				this.totalSamples++;
			}
		}
		this.samples.addElement(tmpPointList);
	}
	
	
	public void disConnectR(){
		this.Rserver.end();
	}
	

	
	public String initData() throws REXPMismatchException {
		System.out.println("Number of Tests:"+ this.samples.size() );
		System.out.println("Number of Samples:"+ this.totalSamples );
		double[][] RdataSet = new double[totalSamples][3];
		int k = 0;
		for (int i = 0 ; i < this.samples.size() ; i++){
			//System.out.println("Num:"+i+"th test");
			
			for (int j = 0; j < this.samples.get(i).size(); j++) {
				RdataSet[k][0] = this.samples.get(i).get(j).x;
				RdataSet[k][1] = this.samples.get(i).get(j).y;
				RdataSet[k][2] = i;
				//System.out.println(this.samples.get(i).get(j).x +" "+ this.samples.get(i).get(j).y );
				k++;
			}
		}
		 
		//Test R 
		//Rsession s = Rsession.newInstanceTry(System.out,null);
		Rserver.set("df", RdataSet, "x", "y", "g");
		Rserver.eval("y<-as.matrix(df[,1:2])");
		//s.eval("factor=df[,3]");
		Rserver.eval("Group<-as.factor(df[,3])");
		Rserver.eval("result<-manova(y~Group)");
		String resultForWilks = Rserver.asString("summary.manova(result,test=c('Wilks'))");
		String resultForPillai = Rserver.asString("summary.manova(result,test=c('Pillai'))");
		String resultForHotelling = Rserver.asString("summary.manova(result,test=c('Hotelling-Lawley'))");
		String resultForRoy = Rserver.asString("summary.manova(result,test=c('Roy'))");
		String txt = resultForWilks + "\n" + resultForPillai + "\n" + resultForHotelling + "\n" + resultForRoy;
        System.out.println(txt);
        this.averageCenterPoint = this.returnAverageCenterPoint();
        return txt+"\n"+this.averageCenterPoint.toString();
	}
	
	//Calculate The Average Point
	public Point returnAverageCenterPoint(){
		Point centerAveragePoint = new Point();
		try {
			centerAveragePoint.x = Rserver.eval("mean(df$x)").asInteger();
			centerAveragePoint.y = Rserver.eval("mean(df$y)").asInteger();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.averageCenterPoint = centerAveragePoint;
		return centerAveragePoint;
	}
	
	//Split the data according to the given degree to number of group arrays
	public Vector<Vector<Point>> splitThePointsByDegree(double degree){
		int numGroup = (int) (360.0/degree);
//		Vector<Integer> specPointIndex = new Vector<Integer>();
		Vector<Double> lowerDegreeBounds = new Vector<Double>();
		Vector<Double> upperDegreeBounds = new Vector<Double>();
		Vector<Vector<Point>> pointsByGroups = new Vector<Vector<Point>>();
		for (int i = 0; i < numGroup; i++){
			double lowerDegree = i * degree;
			double upperDegree = (i+1)*degree;
			lowerDegreeBounds.addElement((lowerDegree));
			upperDegreeBounds.addElement((upperDegree));
			pointsByGroups.addElement(new Vector<Point>());
		}
		//System.out.println("1"+this.samples.toString());
		for (int k = 0 ; k < this.samples.size() ; k++){
			//System.out.println("Num:"+i+"th test");
			for (int j = 0; j < this.samples.get(k).size(); j++) {
				Point curPoint = this.samples.get(k).get(j);
				double angle = Math.toDegrees(Math.atan2(curPoint.x - this.averageCenterPoint.x, curPoint.y - this.averageCenterPoint.y));
				//System.out.println("Angle"+angle);
				if (angle < 0) angle += 360;
				for (int i = 0 ; i < lowerDegreeBounds.size();i++){
					if(angle >= lowerDegreeBounds.get(i) && angle < upperDegreeBounds.get(i)){
						pointsByGroups.get(i).add(curPoint);
					}
				}
			}
		}
		//System.out.println("pointsByGroups :"+pointsByGroups.size());
		//System.out.println("2"+this.samples.toString());
		//System.out.println(this.samples.get(0).get(0).x + "Y:"+this.samples.get(0).get(0).y);
		//System.out.println(this.returnAverageCenterPoint().x + "(AV)Y:"+this.returnAverageCenterPoint().y);
		return pointsByGroups;
	}
	
	public double angleFromTheCenterPoint(Point curPoint){
		double angle = Math.toDegrees(Math.atan2(curPoint.x - this.averageCenterPoint.x, curPoint.y - this.averageCenterPoint.y));
		//System.out.println("Angle"+angle);
		if (angle < 0) angle += 360;
		return angle;
	}
	
	public Boolean checkGroupResults(){
		return true;
	}
	
	public String RsessionProcessCalculateIntervalSD(){
		StringBuilder output = new StringBuilder();
		
		for (Vector<Double> discLists : this.disctancsByGroups){
			double[][] disDataSet = new double[discLists.size()][1];
			for(int i = 0; i < discLists.size(); i++){
				disDataSet[i][0] = discLists.get(i);
			}
			this.Rserver.set("disSet",disDataSet,"dis");
			String resultForWilks = Rserver.asString("sd(disSet$dis)");
			output.append("\n"+resultForWilks);
		}
		System.out.println(output.toString());
		System.out.println("Groups"+this.disctancsByGroups.size());
		return null;
	}
	
	public void processAnalysisTheBoundariesByFloorDegreeInterval(double degree){
		double testDegree = degree;
		
		while (true){
			if (this.analysisTheBoundariesByDegreeInterval(testDegree)){
				System.out.println("OptimDegree:"+testDegree);
				//System.out.println(this.disctancsByGroups.toString());
				break;
			}else{
				testDegree ++; //plus one degree
			}
			
			if (testDegree > 180){
				break; //wrong
			}
		}
	}
	
	public Boolean analysisTheBoundariesByDegreeInterval(double degree){
		Vector<Vector<Point>> pointsByGroups = this.splitThePointsByDegree(degree);
		Vector<Vector<Double>> tmpDiscByGroups = new Vector<Vector<Double>>();
		int count = 0;
		System.out.println("==========================");
		for (Vector<Point> group : pointsByGroups) {
			count+= group.size();
			System.out.println("GroupSize:"+group.size());
		}
		System.out.println("TotalPoint:"+count);
		for (Vector<Point> group : pointsByGroups) {
			if (group.size() < 2) return false;
			Vector<Double> distanceLists = new Vector<Double>();
			for(int i = 0 ; i < group.size(); i++){
				Vector<Point> sameLinePoints = new Vector<Point>();
				Point sPoint = group.get(i);
				Point sumPoint = new Point(0,0);
				for (int j = 0 ; j < group.size(); j++){
					Point tPoint = group.get(j);
					if ((this.angleFromTheCenterPoint(tPoint)) == (this.angleFromTheCenterPoint(sPoint))){
						sameLinePoints.add(tPoint);
						sumPoint.x = sumPoint.x + tPoint.x;
						sumPoint.y = sumPoint.y + tPoint.y;
						//group.remove(j);
					}
				}
				Point averagePoint = new Point();
				averagePoint.setLocation((sumPoint.getX()/sameLinePoints.size()),(sumPoint.getY()/sameLinePoints.size()));
				System.out.println("SameLinePonits:"+sameLinePoints.toString());
				System.out.println("Averager Point:"+averagePoint);
				for (Point p : sameLinePoints){
					double length = p.distance(averagePoint);
					if (p.distance(this.averageCenterPoint) > averagePoint.distance(this.averageCenterPoint)){
						length = -length;
					}
					distanceLists.addElement(length);
					
				}	
			}
			tmpDiscByGroups.add(distanceLists);
			System.out.println("Distance lists:"+distanceLists.toString());
		}
		
		
		this.disctancsByGroups = tmpDiscByGroups;

		return true;
	}
}
