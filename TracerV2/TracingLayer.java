import java.awt.*;
import java.util.*;


public class TracingLayer {

	public int layerID;
	public Color pointColor;
	private Vector<Vector<Point>> points;
	private Vector<Vector<Color>> colors;
	
	public TracingLayer(){
		this.layerID = 0;
		this.pointColor = Color.RED;
		this.points = new Vector<Vector<Point>>();
		this.colors = new Vector<Vector<Color>>();
	}
	
	public void addNewPoints(Vector<Point> ps, Vector<Color> cs){
		this.points.addElement(ps);
		this.colors.addElement(cs);
	}
	
	public Vector<Vector<Point>> getPoints(){
		return points;
	}
	
	public Vector<Vector<Color>> getColors(){
		return this.colors;
	}
	
	public void cleanEmptyPaths(){
		for(int i=0; i<this.points.size();i++){
			if(this.points.elementAt(i).size()<=0){
				this.points.removeElementAt(i);
				this.colors.removeElementAt(i);
				i--;
			}
		}
	}
}
