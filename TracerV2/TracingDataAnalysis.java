import java.awt.*;
import java.io.File;
import java.util.*;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

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
	private MatlabProxy matLabProxy;
	private float estimateError;
	
	public TracingDataAnalysis(){
		this.sampleLayers = new Vector<TracingLayer>();
		this.disctancsByGroups = new Vector<Vector<Double>>();
		this.samples = new Vector<Vector<Point>>();
		//this.Rserver = Rsession.newInstanceTry(System.out,null);
		try {
			this.initMatLabProxy();
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MatlabConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		averageCenterPoint = new Point();
		totalSamples = 0;
		estimateError = 0;
	}
	
	
	public void resetData(){
		this.sampleLayers = new Vector<TracingLayer>();
		this.disctancsByGroups = new Vector<Vector<Double>>();
		this.samples = new Vector<Vector<Point>>();
		//this.Rserver = Rsession.newInstanceTry(System.out,null);

		averageCenterPoint = new Point();
		totalSamples = 0;
		estimateError = 0;
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
	
	
	
	/**
	 * Disconnect With R
	 */
	public void disConnectR(){
		this.Rserver.end();
	}
	

	/**
	 * Init the R data set And enable For MANOVA Test
	 * @return
	 * @throws REXPMismatchException
	 */
	public String initData() throws REXPMismatchException {
	
	    this.clearMatLab();
		System.out.println("Number of Tests:"+ this.samples.size() );
		System.out.println("Number of Samples:"+ this.totalSamples );
		this.inputOriginalPointToMatLab();
		double[] totalDistanceList;
		
		System.out.println("Asize:"+this.samples.get(0).size()+"Bsize:"+this.samples.get(1));
		
		totalDistanceList = (this.compareCorrelationsBetweenTwoGroupofPoints(this.samples.get(1), this.samples.get(0)));
		try {
			this.inputDistArray(totalDistanceList,1);
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MatlabConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		totalDistanceList = (this.compareCorrelationsBetweenTwoGroupofPoints(this.samples.get(0), this.samples.get(2)));
		try {
			this.inputDistArray(totalDistanceList,2);
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MatlabConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		totalDistanceList = (this.compareCorrelationsBetweenTwoGroupofPoints(this.samples.get(1), this.samples.get(2)));
		try {
			this.inputDistArray(totalDistanceList,3);
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MatlabConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		System.out.println(totalDistanceList.toString());
		double[] RDistanceSet = new double[totalDistanceList.size()];
		for (int i = 0 ; i < totalDistanceList.size(); i++){
			RDistanceSet[i] = totalDistanceList.get(i);
		}
		
		Rserver.set("df", RDistanceSet);
		
		for(float dis : totalDistanceList){
			System.out.println(dis);
		}
		
		//Rserver.eval("x")

		 * */
		
		
		
		return this.outputDistanceFigure();
		

	}
	
	/**
	 * Calculate The Average Point
	 * @return
	 */
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
	
	/**
	 * Split the data according to the given degree to number of group arrays
	 * @param degree
	 * @return
	 */
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
	
	/**
	 * Return the angle form the Center Point
	 * @param curPoint
	 * @return
	 */
	public double angleFromTheCenterPoint(Point curPoint){
		double angle = Math.toDegrees(Math.atan2(curPoint.x - this.averageCenterPoint.x, curPoint.y - this.averageCenterPoint.y));
		//System.out.println("Angle"+angle);
		if (angle < 0) angle += 360;
		return angle;
	}
	
	/**
	 * Return the Group Splitting Result
	 * @return
	 */
	public Boolean checkGroupResults(){
		return true;
	}
	
	/**
	 * Return Standard Div For the give distance Array
	 * @return
	 */
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
	
	/**
	 * Split the degree participation
	 * @param degree
	 */
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
	
	/**
	 * Split the points by the given participation degree
	 * @param degree
	 * @return
	 */
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
	

	public double[] compareCorrelationsBetweenTwoGroupofPoints(Vector<Point> _setA,Vector<Point> _setB){
		
		float result = 0;
		
		Vector<Point> setA = new Vector<Point>(_setA);
		Vector<Point> setB = new Vector<Point>(_setB);
		Vector<Float> distanceList = new Vector<Float>();
		System.out.println("SetBSize:"+setB.size());
		float checkDistance = 0;
		double[] orderDistance = new double[_setB.size()];
		for(int i = 0 ; i < _setB.size();i++){
			orderDistance[i] = -1;
		}
		
		while(result < setB.size()){
			for (Point pA : setA){
				for (int i = 0 ; i < setB.size() ; i++){
					if (orderDistance[i] == -1){
						float x = Math.abs(setB.get(i).x - pA.x);
						float y = Math.abs(setB.get(i).y - pA.y);
						if ((x <= checkDistance && y <= checkDistance)){
							System.out.println("！！NeiPoints:"+setB.size());
							result++;
							distanceList.add(checkDistance);
							orderDistance[i] = checkDistance;
						}
					}
				}
			}
			checkDistance++;
		}
		return orderDistance;
	}
	
	/**
	 * Test MatLab
	 * @throws MatlabInvocationException 
	 * @throws MatlabConnectionException 
	 */
	public void testMatLab() throws MatlabInvocationException, MatlabConnectionException {
//		MatlabProxyFactory factory = new MatlabProxyFactory();
//	    MatlabProxy proxy = factory.getProxy();
//	    //Set a variable, add to it, retrieve it, and print the result
//	    proxy.setVariable("a", 5);
//	    proxy.eval("a = a + 6");
//	    Object result = proxy.getVariable("a");
//	    System.out.println("Result: " + result);
//	    //Disconnect the proxy from MATLAB
//	    proxy.disconnect();
	}
	
	public void inputDistArray(double[] disArray,int sampleIndex) throws MatlabInvocationException, MatlabConnectionException {
		String arrayName = "sample"+sampleIndex;
		double[][] matDisArray = new double[1][disArray.length];
		for(int i = 0 ; i < disArray.length ; i++){
			matDisArray[0][i] = disArray[i];
		}
		MatlabTypeConverter processor = new MatlabTypeConverter(this.matLabProxy);
	    processor.setNumericArray("array", new MatlabNumericArray(matDisArray, null));
	    this.matLabProxy.eval(arrayName+" = transpose(array)");
	}
	
	public String outputDistanceFigure(){
		String result = "Result:";
		try {
			this.matLabProxy.eval("drawSamplesFigure");
			 MatlabTypeConverter processor = new MatlabTypeConverter(this.matLabProxy);
			    MatlabNumericArray array = processor.getNumericArray("result");
			    //Print out the same entry, using Java's 0-based indexing
			    result += ("\nAB:" + Math.round(array.getRealValue(0, 0)*1000)/1000.0f) + "%";
			    result += ("\nAC:" + Math.round(array.getRealValue(1, 0)*1000)/1000.0f) + "%";
			    result += ("\nBC:" + Math.round(array.getRealValue(2, 0)*1000)/1000.0f) + "%";
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public String outputAreaFigure(){
		String result= "Result:";
		//TODO MatLab Area check function
		return result;
	}
	
	public void outputDistanceCircleFigure(){
		try {
			this.matLabProxy.eval("drawDistanceCircle(sample1,sample2,sample3)");
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void clearMatLab(){
		try {
			this.matLabProxy.eval("clear");
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void inputOriginalPointToMatLab(){
		
		for (int i = 0 ; i < this.samples.size(); i++){
			String arrayName = "originalPoints"+(i+1);
			double[][] dataSets = new double[2][this.samples.get(i).size()];
			for (int j = 0 ; j < this.samples.get(i).size() ; j++){
				dataSets[0][j] = this.samples.get(i).get(j).x;
				dataSets[1][j] = this.samples.get(i).get(j).y;
				MatlabTypeConverter processor = new MatlabTypeConverter(this.matLabProxy);
			    try {
					processor.setNumericArray("array", new MatlabNumericArray(dataSets, null));
					this.matLabProxy.eval(arrayName+" = transpose(array)");
				} catch (MatlabInvocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void initMatLabProxy() throws MatlabInvocationException, MatlabConnectionException {
		MatlabProxyFactory factory = new MatlabProxyFactory();
	    this.matLabProxy = factory.getProxy();	
	}
	
	public void disconnectMatLabProxy() throws MatlabInvocationException, MatlabConnectionException {
		this.matLabProxy.disconnect();	
	}
	
	
	
}
