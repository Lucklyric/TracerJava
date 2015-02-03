import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.util.Vector;

import javax.swing.*;


public class GraphicsPanel extends JLabel implements MouseListener,
													 MouseMotionListener{
	private double zoom;
	private static final String EMPTY_MSG="Use \"Open\" Button to load an image";
	protected static final String DEMAND_VALIDATION = "DEMAND_VALIDATION";
	protected static final String PATH_DONE_CMD = "PATH_DONE_CMD";
	protected static final String REFRESH_IMG_CMD = "REFRESH_IMG_CMD";
	protected static final String ERASE_HERE_CMD = "ERASE_HERE_CMD";
	protected static final int ERASER_RADIUS = 3;
	protected static final float POINTER_OFFEST_CORRECTION = 0.1f;
	private boolean bImageLoaded;
	private Dimension dims;
	private BufferedImage bufImg;
	private BufferedImage canvasBuffer;
	public Vector<Point> currentPath;
	public Vector<Color> currentColors;
	private Graphics2D canvasGraphics;
	private AffineTransform affineTransform;
	private AffineTransformOp transformOpLarge;
	private AffineTransformOp transformOpSmall;
	private ActionListener listener;
	public boolean bDisableTracing;
	public boolean bEraseModeActivated;
	public boolean bAnaModeActivated;
	public Point anaStartPoint;
	public Point oldEraserPoint;
	public int oldEraserRadius;
	
	public GraphicsPanel(ActionListener al){
		
		this.listener = al;
		this.dims = new Dimension(256,256);
		this.fixSize(this.dims);
		this.setZoom(1.0);
		this.bImageLoaded = false;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.bDisableTracing = false;
		this.bEraseModeActivated = false;
		this.bAnaModeActivated = false;
		this.oldEraserPoint = null;
		this.oldEraserRadius= 0;
	}
	
	private void fixSize(Dimension newSize){
		this.setPreferredSize(newSize);
		this.setMinimumSize(newSize);
		this.setMaximumSize(newSize);
		this.setSize(newSize);
		this.setBorder(BorderFactory.createTitledBorder("canvas"));
		//this.dims = newSize;
		this.listener.actionPerformed(new ActionEvent(
				this,0,DEMAND_VALIDATION));
	}
	
	private void fixSize(double w, double h){
		this.fixSize(new Dimension((int)(w+0.5),(int)(h+0.5)));
	}
	
	public void setZoom(double newZoom){
		this.zoom = newZoom;
		this.setAffineTransformation(new AffineTransform(
				this.zoom,0.0,
				0.0,this.zoom,
				0.0,0.0));
		this.fixSize(this.dims.width*this.zoom,this.dims.height*this.zoom);
	}
	public double getZoom(){
		return this.zoom;
	}
	public void reSyncSize(){
		this.fixSize(this.dims);
	}
	public void paint(Graphics g){
		Graphics2D g2d =(Graphics2D)g;
		if(this.bImageLoaded){
			
			//decide which resize algorithm to use based on image zoom
			if(this.getZoom()>=1.0)
				g2d.drawImage(this.canvasBuffer, this.transformOpLarge, 0, 0);
			else
				g2d.drawImage(this.canvasBuffer, this.transformOpSmall, 0, 0);
		}else{
			super.paint(g);
			g2d.drawString(EMPTY_MSG, 2, 30);
		}
		return;	
	}
	
	public void setBufferedImage(ImageIcon i){
		this.bufImg = TracerUtilities.convertImageIconToBufferedImage(i);
		int newW = this.bufImg.getWidth();
		int newH = this.bufImg.getHeight();
		this.dims = new Dimension(newW,newH);
		this.canvasBuffer = new BufferedImage(newW,newH,
				BufferedImage.TYPE_INT_ARGB);
		//this.fixSize(i.getIconWidth()*this.zoom,i.getIconHeight()*this.zoom);
		this.bImageLoaded = true;
		//TODO: Create external method for this
		this.canvasGraphics = this.canvasBuffer.createGraphics();
		this.canvasGraphics.setColor(Color.RED);
		this.refreshCanvas();
	}
	
	public void setBufferedImage(BufferedImage bi){
		this.bufImg = bi;
		int newW = this.bufImg.getWidth();
		int newH = this.bufImg.getHeight();
		this.dims = new Dimension(newW,newH);
		this.canvasBuffer = new BufferedImage(newW,newH,
				BufferedImage.TYPE_INT_ARGB);
		//this.fixSize(i.getIconWidth()*this.zoom,i.getIconHeight()*this.zoom);
		this.bImageLoaded = true;
		//TODO: Create external method for this
		this.canvasGraphics = this.canvasBuffer.createGraphics();
		this.canvasGraphics.setColor(Color.RED);
		this.refreshCanvas();
	}
	
	public void refreshCanvas(){
		int w = (int)(this.canvasBuffer.getWidth()*this.zoom+0.5);
		int h = (int)(this.canvasBuffer.getHeight()*this.zoom+0.5);
		if(this.getWidth() != w || this.getHeight() != h)
			this.fixSize(new Dimension(w,h));
		this.canvasBuffer.createGraphics().setColor(Color.DARK_GRAY);
		this.canvasBuffer.createGraphics().fillRect(0, 0, w, h);
		//Buffer is clear so now the redrawing can begin
		this.canvasBuffer.createGraphics().drawImage(this.bufImg,null,0,0);
	}
	
	public void setAffineTransformation(AffineTransform at){
		this.affineTransform = at;
		this.transformOpLarge = new AffineTransformOp(at,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		this.transformOpSmall = new AffineTransformOp(at,
				AffineTransformOp.TYPE_BILINEAR);
	}
	
	public void mouseClicked(MouseEvent evt) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	
	//Mouse pressed should mean that the user intends to start tracing
	public void mousePressed(MouseEvent e) {
		if(this.bDisableTracing){
			TracerUtilities.popUpMsg("You currently have <u>No Layer</u> "+
					"selected! " +
					"You must select a layer from the drop-down menu (or "+
					"create a new one) before you can begin tracing!");
			return;
		}
		if(!this.bEraseModeActivated && !this.bAnaModeActivated){
			//System.out.println("Mouse Down!");
			if(this.canvasGraphics != null){
				this.currentPath = new Vector<Point>();
				this.currentPath.addElement(new Point(
						(int)(e.getX()/this.zoom+POINTER_OFFEST_CORRECTION),
						(int)(e.getY()/this.zoom+POINTER_OFFEST_CORRECTION)));
				this.currentColors=new Vector<Color>();
				try{this.currentColors.addElement(new Color(this.bufImg.getRGB(
						(int)(e.getX()/this.zoom+POINTER_OFFEST_CORRECTION),
						(int)(e.getY()/this.zoom+POINTER_OFFEST_CORRECTION))));
				}catch(Exception ex){
					this.currentColors.addElement(Color.BLACK);
				}
			}else{
				TracerUtilities.popUpMsg("You need to open an image before" +
						" you can start tracing.");
			}
		}else{
			if (this.bEraseModeActivated){
				int eraserRadius = this.calculateRadiusFromZoom(this.zoom);
				this.eraseHere((int)(e.getX()/this.zoom+POINTER_OFFEST_CORRECTION),
							   (int)(e.getY()/this.zoom+POINTER_OFFEST_CORRECTION),
							   eraserRadius);
			}else if(this.bAnaModeActivated){
				this.anaStartPoint = new Point((int)(e.getX()/this.zoom+POINTER_OFFEST_CORRECTION), (int)(e.getY()/this.zoom+POINTER_OFFEST_CORRECTION));

			}
		}
	}
	
	private void eraseHere(int x, int y, int eraserRadius) {
		//this.canvasGraphics.drawOval(x, y, eraserRadius, eraserRadius);
		//System.out.println("Zoom level: "+this.zoom+" eraserRadius:"
		//		+eraserRadius+" x:"+x+" y:"+y);
		this.oldEraserPoint = new Point(x,y);
		this.oldEraserRadius= eraserRadius;
		this.listener.actionPerformed(new ActionEvent(this,0,ERASE_HERE_CMD));
	}

	private int calculateRadiusFromZoom(double zoomLevel) {
		return (int)((double)ERASER_RADIUS/zoomLevel);
	}

	public void mouseDragged(MouseEvent e) {
		
		if(this.bDisableTracing){
			return;
		}
		if(!this.bEraseModeActivated && !this.bAnaModeActivated){
		//System.out.println("Mouse Dragged!");
			if(this.currentPath != null){
				int newX = (int)(e.getX()/this.zoom+POINTER_OFFEST_CORRECTION);
				int newY = (int)(e.getY()/this.zoom+POINTER_OFFEST_CORRECTION);
				Point prevPoint = this.currentPath.elementAt(
						this.currentPath.size()-1);
				int prevX= prevPoint.x;
				int prevY= prevPoint.y;
				
				this.currentPath.addElement(new Point(newX,newY));
				try{this.currentColors.addElement(new Color(this.bufImg.getRGB(
						newX,newY)));
				}catch(Exception ex){
					this.currentColors.addElement(Color.BLACK);
				}
				//System.out.println("Trying to draw...");
				this.canvasGraphics.drawLine(prevX,prevY,newX,newY);
		
				this.repaint();
			}else{
				
					this.currentPath = new Vector<Point>();
					this.currentPath.addElement(new Point(
							(int)(e.getX()/this.zoom+POINTER_OFFEST_CORRECTION),
							(int)(e.getY()/this.zoom+POINTER_OFFEST_CORRECTION)));
					this.currentColors=new Vector<Color>();
					try{this.currentColors.addElement(new Color(this.bufImg.getRGB(
							(int)(e.getX()/this.zoom+POINTER_OFFEST_CORRECTION),
							(int)(e.getY()/this.zoom+POINTER_OFFEST_CORRECTION))));
					}catch(Exception ex){
						this.currentColors.addElement(Color.BLACK);
					}
			
			}	
		}else{
			if(this.bEraseModeActivated){
				int eraserRadius = this.calculateRadiusFromZoom(this.zoom);
				this.eraseHere((int)(e.getX()/this.zoom+POINTER_OFFEST_CORRECTION),
							   (int)(e.getY()/this.zoom+POINTER_OFFEST_CORRECTION),
							   eraserRadius);
			}else if(this.bAnaModeActivated){
				int newX = (int)(e.getX()/this.zoom+POINTER_OFFEST_CORRECTION);
				int newY = (int)(e.getY()/this.zoom+POINTER_OFFEST_CORRECTION);
				this.canvasGraphics.drawLine(this.anaStartPoint.x,this.anaStartPoint.y,newX,newY);
				this.listener.actionPerformed(new ActionEvent(this,
						0,GraphicsPanel.REFRESH_IMG_CMD));
				this.repaint();
			}
		}
	}
	public void mouseReleased(MouseEvent e) {
		//System.out.println("Mouse Released!");
		if(this.bDisableTracing || this.bEraseModeActivated || this.bAnaModeActivated){
			return;
		}
		if(this.currentPath != null){
			int newX = (int)(e.getX()/this.zoom+POINTER_OFFEST_CORRECTION);
			int newY = (int)(e.getY()/this.zoom+POINTER_OFFEST_CORRECTION);
			Point prevPoint = this.currentPath.elementAt(
					this.currentPath.size()-1);
			int prevX= prevPoint.x;
			int prevY= prevPoint.y;
			
			this.currentPath.addElement(new Point(newX,newY));
			try{this.currentColors.addElement(new Color(this.bufImg.getRGB(
					newX,newY)));
			}catch(Exception ex){
				this.currentColors.addElement(Color.BLACK);
			}
			this.canvasGraphics.drawLine(prevX,prevY,newX,newY);
		}
		this.repaint();
		this.listener.actionPerformed(new ActionEvent(this,0,PATH_DONE_CMD));
		this.listener.actionPerformed(new ActionEvent(this,0,REFRESH_IMG_CMD));
	}
	
	public void reportSizes(){
		System.out.println("dims:"+this.dims.width+", "+this.dims.height);
		System.out.println("getSize:"
				+this.getSize().width+", "+this.getSize().height);
	}
	public void mouseMoved(MouseEvent arg0) {}
	
	public boolean clearAllTracings(){
		if(this.canvasBuffer == null && this.bufImg == null)
			return false;
		this.canvasBuffer.createGraphics().drawImage(this.bufImg,null,0,0);
		this.repaint();
		return true;
	}
	
	public void drawTheseLines(Vector<Point> points){
		if(this.canvasGraphics ==null){
		   System.out.println("Warning! Can't draw lines, no canvas defined!");
		}
		for(int i = 0; i < points.size()-1; i++){
			Point point1 = points.elementAt(i);
			Point point2 = points.elementAt(i+1);
			this.canvasGraphics.drawLine(point1.x,point1.y,point2.x,point2.y);
		}
	}
	public void drawThesePoints(Vector<Point> points){
		if(this.canvasGraphics ==null){
		   System.out.println("Warning! Can't draw lines, no canvas defined!");
		}
		for(int i = 0; i < points.size()-1; i++){
			this.canvasGraphics.drawLine(points.elementAt(i).x,
				points.elementAt(i).y,points.elementAt(i).x,points.elementAt(i).y);
			//this.canvasGraphics.drawOval(points.elementAt(i).x,
			//		points.elementAt(i).y,1,1);
		}
	}
	
	public void drawOnePoint(Point point){
		this.canvasGraphics.drawLine(point.x, point.y, point.x, point.y);
		this.repaint();
	}
	
	public void setDrawingColor(Color c){
		if(c != null && this.canvasGraphics != null)
			this.canvasGraphics.setColor(c);
	}
	public Color getDrawingColor(){
		if(this.canvasGraphics != null)
			return this.canvasGraphics.getColor();
		else
			return Color.red;
	}
	
	public boolean isAnImageLoaded(){
		return this.bImageLoaded;
	}
}
