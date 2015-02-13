import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.*;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.rosuda.REngine.REXPMismatchException;

public class TracerMainPanel extends JPanel implements ActionListener, ComponentListener{

	private static final long serialVersionUID = 1L;
	private static final String OPEN_CMD = "OPEN_CMD";
	private static final String SAVE_CMD = "SAVE_CMD";
	//private Applet app;
	private JPanel fileOptionsPanel;
	private LayerOptionsPanel layerOptionsPanel;
	private ViewOptionsPanel viewOptionsPanel;
	private Vector<TracingLayer> tracingLayers;
	private String imagesURL;
	private int layoutRows;
	private static final int EXTRA_WIDTH_PIXELS = 14;
	private static final int EXTRA_HEIGHT_PIXELS= 28;
	private JFileChooser fileChooser;
	private JFileChooser saveChooser;
	private JLabel imageLabel;
	private GraphicsPanel mainCanvas;
	private Cursor eraserCursor;
	
	//TracingDataAnalysis
	private TracingDataAnalysis mTracingDataAnalysis;
	private DataAnalysisPanel mDataAnalysisMainPanel;
	private JFrame DataAnalysisWindow;
	//Makes a new Tracer main panel that controls virtually all aspects of our
	//i.e. It is the "mediator" class
	//application
	public TracerMainPanel(Applet ap){
		super();
		//this.app = ap;
		this.layoutRows =1;
		
		this.imageLabel = new JLabel("No Image loaded");
		this.add(this.imageLabel, BorderLayout.CENTER);
		//this.addComponentListener(this);
		//TODO: fix this
		this.imagesURL = "./images/";
		this.fileChooser = new JFileChooser("./");
		this.fileChooser.setFileFilter(new ImageFilter());
		this.fileChooser.setAccessory(new ImagePreview(this.fileChooser));
		
		this.saveChooser = new JFileChooser("./");
		this.saveChooser.setFileFilter(new SaveFilter());

		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createTitledBorder("Image Tracer"));
		
		this.fileOptionsPanel = this.createFileOptionsPanel();
		this.layerOptionsPanel= new LayerOptionsPanel(this,this.imagesURL);
		this.viewOptionsPanel = new ViewOptionsPanel(this,this.imagesURL);
		
		JPanel upperControlsPanel = new JPanel();
		upperControlsPanel.addComponentListener(this);
		upperControlsPanel.setBorder(
				BorderFactory.createTitledBorder(""));//Upper controls
		upperControlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT,1,1));
		
		upperControlsPanel.add(this.fileOptionsPanel);
		upperControlsPanel.add(this.layerOptionsPanel);
		upperControlsPanel.add(this.viewOptionsPanel);
	
		//upperControlsPanel.setPreferredSize(new Dimension(0,500));
		
		this.add(upperControlsPanel, BorderLayout.PAGE_START);
		this.validate();
		
		this.tracingLayers = new Vector<TracingLayer>();
		this.createNewLayer();
		
		this.mainCanvas = new GraphicsPanel(this);
		this.mainCanvas.setCursor(Cursor.getPredefinedCursor(
				Cursor.CROSSHAIR_CURSOR));
		JPanel emptyPanel = new JPanel();
		//emptyPanel.setBorder(BorderFactory.createTitledBorder("emptypanel"));
		emptyPanel.add(this.mainCanvas);
		
		JScrollPane jsp = new JScrollPane(emptyPanel);
		jsp.getVerticalScrollBar().setUnitIncrement(20);
		jsp.getVerticalScrollBar().setBlockIncrement(60);
		jsp.getHorizontalScrollBar().setUnitIncrement(20);
		jsp.getHorizontalScrollBar().setBlockIncrement(60);
		//jsp.add(this.mainCanvas);
		this.add(jsp, BorderLayout.CENTER);
		//this.mainCanvas.reSyncSize();
		
		this.runFlowLayoutHack(upperControlsPanel);
		this.validate();
		
		//Get the default toolkit  
		Toolkit toolkit = Toolkit.getDefaultToolkit();  
		//Load an image for the cursor  
		Image eraserImage = toolkit.getImage(this.imagesURL+"bigRedX.png");
		//Create the custom eraser cursor  
		this.eraserCursor = toolkit.createCustomCursor(eraserImage, 
				new Point(6,6), "Eraser");
		
		this.mTracingDataAnalysis = new TracingDataAnalysis();
		this.mDataAnalysisMainPanel = new DataAnalysisPanel(null);
		DataAnalysisWindow = new JFrame("Data Analysis");
		DataAnalysisWindow.setSize(new Dimension(300,300));
		DataAnalysisWindow.getContentPane().add(this.mDataAnalysisMainPanel);
		DataAnalysisWindow.setVisible(false);
		//this.mainCanvas.reSyncSize();
		//this.mainCanvas.repaint();
	}

	private void createNewLayer() {
		TracingLayer tLayer = new TracingLayer();
		this.tracingLayers.addElement(tLayer);
		
		this.synchronizeLayerIDs();
		this.layerOptionsPanel.setCurrentLayer(this.tracingLayers.size());
	}
	
	private void createAnaLayer(){
		TracingLayer tLayer = new TracingLayer();
		this.tracingLayers.addElement(tLayer);
		this.synchronizeLayerIDs();
		this.layerOptionsPanel.setCurrentLayer(this.tracingLayers.size());
	}
	
	private void synchronizeLayerIDs() {
		for(int i = 1; i <= this.tracingLayers.size(); i++)
			this.tracingLayers.elementAt(i-1).layerID = i;
		Vector<String> defaultLayerNames = new Vector<String>();
		for(int i = 0; i <= this.tracingLayers.size(); i++){
			if(i == 0)
				defaultLayerNames.addElement("No Layer");
			else
				defaultLayerNames.addElement("Layer "+i);
		}
		this.layerOptionsPanel.updateLayersComboBox(defaultLayerNames);
	}

	//creates our buttons with the standard file options (i.e. open, save)
	private JPanel createFileOptionsPanel() {
		JPanel foptionsPanel = new JPanel();
		//First we make the "open" button
		JButton openButton = new JButton(TracerUtilities.getThisImage(
				this.imagesURL+"open.png", this));
		openButton.setText("Open");
		openButton.setToolTipText("Open a new image to trace");
		openButton.setActionCommand(TracerMainPanel.OPEN_CMD);
		openButton.setBorder(BorderFactory.createRaisedBevelBorder());
		openButton.addActionListener(this);
		//Next we make the "save" button
		JButton saveButton = new JButton(TracerUtilities.getThisImage(
				this.imagesURL+"save.png", this));
		saveButton.setText("Save");
		saveButton.setToolTipText("Save your tracings");
		saveButton.setActionCommand(TracerMainPanel.SAVE_CMD);
		saveButton.setBorder(BorderFactory.createRaisedBevelBorder());
		saveButton.addActionListener(this);

		foptionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
		foptionsPanel.setBorder(
				BorderFactory.createTitledBorder("File Options"));
		foptionsPanel.add(openButton);
		foptionsPanel.add(saveButton);
		foptionsPanel.validate();
		
		return foptionsPanel;
	}

	
	
	public void actionPerformed(ActionEvent e) {
		//System.out.println("Action fired: \"" + e.getActionCommand()+"\"");
		
		if(e.getActionCommand().equals(LayerOptionsPanel.LAYER_CHANGE)){
			//String option_picked = (String)
			//		((JComboBox)e.getSource()).getSelectedItem();
			int selectedIndex =((JComboBox)e.getSource()).getSelectedIndex();
			this.layerOptionsPanel.currentlySelectedLayer = selectedIndex;
			
			//System.out.println("Selected layer is: \""+option_picked+"\"");
			//int selectedLayer = this.layerOptionsPanel.getCurrentLayer();
			//System.out.println("Selected index is: "+selectedIndex);
			
			if(this.mainCanvas != null && this.mainCanvas.isAnImageLoaded()){
				this.mainCanvas.clearAllTracings();
				//Don't let the user draw on the background canvas (layer 0)
				if(selectedIndex == 0)
					this.mainCanvas.bDisableTracing = true;
				else
					this.mainCanvas.bDisableTracing = false;
				this.redrawTracings(selectedIndex,
						this.viewOptionsPanel.isPointsModeOn(),
						this.viewOptionsPanel.isSuperImposeModeOn());
			}
		}else if(e.getActionCommand().equals(LayerOptionsPanel.COLOR_CHANGE)){
			int currentLayer = this.layerOptionsPanel.getCurrentLayer();
			Color newColor = JColorChooser.showDialog(this,
					"Choose Tracing Color",this.tracingLayers.elementAt(
							currentLayer-1).pointColor);
			if(newColor != null){
				this.tracingLayers.elementAt(currentLayer-1).pointColor=newColor;
				this.mainCanvas.setDrawingColor(newColor);
			}
			this.redrawTracings(this.layerOptionsPanel.currentlySelectedLayer,
					this.viewOptionsPanel.isPointsModeOn(),
					this.viewOptionsPanel.isSuperImposeModeOn());
			
		}else if(e.getActionCommand().equals(OPEN_CMD)){ //OPEN
			if(this.fileChooser.showOpenDialog(this)
					== JFileChooser.APPROVE_OPTION){
				if(TracerUtilities.getFileExtension(
						this.fileChooser.getSelectedFile()).equals("txt")){
					if(!this.mainCanvas.isAnImageLoaded()){
						TracerUtilities.popUpMsg("You must first load an " +
								"image before opening a tracings file!");
					} else
						try {
							this.loadTracings(this.fileChooser.getSelectedFile());
						} catch (REXPMismatchException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}else{
					this.tracingLayers = new Vector<TracingLayer>();
					this.createNewLayer();
					this.setBackgroundImage(this.fileChooser.getSelectedFile());
				}
			}
		}else if(e.getActionCommand().equals(SAVE_CMD)){
			String filename = null;
			if(this.saveChooser.showSaveDialog(this)
					== JFileChooser.APPROVE_OPTION){
				if(this.saveChooser.getSelectedFile().getName() == null || 
					  this.saveChooser.getSelectedFile().getName().equals("")){
					TracerUtilities.popUpMsg("Unable to save!" +
					" Invalid file name entered!");
				}else if(this.saveChooser.getSelectedFile().
						getName().endsWith(".txt")){
					filename = this.saveChooser.getSelectedFile().getName();
				}else{
					filename = this.saveChooser.getSelectedFile().getName()
								+".txt";
				}
			}
			//String filename = (String)JOptionPane.showInputDialog(this,
				//	"Enter the name for your save file", "Save Tracings",
		        //    JOptionPane.PLAIN_MESSAGE, null, null, "save.txt");
			if(filename!=null)
				this.saveTracings(filename);
		}else if(e.getActionCommand().equals(LayerOptionsPanel.NEW_LAYER)){
			this.createNewLayer();
		}else if(e.getActionCommand().equals(LayerOptionsPanel.DELETE_LAYER)){
			if(TracerUtilities.askYesNoQuestion("Are you sure you want to " +
					"permanently delete the current layer?"))
				this.deleteCurrentLayer();
		}else if(e.getActionCommand().equals(LayerOptionsPanel.ANA_MODE)){
			//this.mainCanvas.bAnaModeActivated = this.layerOptionsPanel.anaModeActivated();
			//Give CenterPoint
			this.mainCanvas.drawOnePoint(this.mTracingDataAnalysis.returnAverageCenterPoint());
			this.mTracingDataAnalysis.processAnalysisTheBoundariesByFloorDegreeInterval(2.0);
			this.mTracingDataAnalysis.RsessionProcessCalculateIntervalSD();
			
		}else if(e.getActionCommand().equals(LayerOptionsPanel.ERASE_MODE)){
			if(this.layerOptionsPanel.eraserModeActivated()){
				this.mainCanvas.setCursor(this.eraserCursor);
				this.mainCanvas.bEraseModeActivated = true;
				//Forcibly turn on points mode
				if(!this.viewOptionsPanel.isPointsModeOn()){
					this.viewOptionsPanel.setPointModeCheckbox(true);
					this.actionPerformed(new ActionEvent(this,0,
							ViewOptionsPanel.POINT_MODE_TOGGLE));
				}
				//Forcibly turn off Super-Impose mode
				if(this.viewOptionsPanel.isSuperImposeModeOn()){
					this.viewOptionsPanel.setSuperimposeModeCheckbox(false);
					this.actionPerformed(new ActionEvent(this,0,
							ViewOptionsPanel.SUPERIMPOSE_TOGGLE));
				}
			}else{
				this.mainCanvas.setCursor(Cursor.getPredefinedCursor(
						Cursor.CROSSHAIR_CURSOR));
				this.mainCanvas.bEraseModeActivated = false;
			}
		}else if(e.getActionCommand().equals(LayerOptionsPanel.CLEAR_LAYER)){
			if(TracerUtilities.askYesNoQuestion("Are you sure you wish to " +
				"clear all tracings from the current layer?")){
					
				int selectedIndex =this.layerOptionsPanel.getCurrentLayer();
				if(selectedIndex >0){
					this.clearThisLayer(selectedIndex-1);
					this.mainCanvas.clearAllTracings();
					this.redrawTracings(selectedIndex,
							this.viewOptionsPanel.isPointsModeOn(),
							this.viewOptionsPanel.isSuperImposeModeOn());
				}
			}
			
		}else if(e.getActionCommand().equals(ViewOptionsPanel.ZOOM_CHANGE)){
			double newZoomLevel = 1.0;
			try{
			    newZoomLevel = Double.parseDouble(
			    		this.viewOptionsPanel.getZoomFieldText())/100.0;
			}catch(Exception x){
				TracerUtilities.popUpMsg("Error: \""
					+ this.viewOptionsPanel.getZoomFieldText()
					+ "\" is not a valid zoom value! Please enter a number!");
			}
			newZoomLevel = this.clampValidZoom(newZoomLevel);
			this.mainCanvas.setZoom(newZoomLevel);
		}else if(e.getActionCommand().equals(ViewOptionsPanel.ZOOM_IN)){
			double oldZoom = this.mainCanvas.getZoom();
			double newZoom = 1.0;
			if(oldZoom < 1.0)
				newZoom = oldZoom + 0.1;
			else
				newZoom = oldZoom + 0.5;
			newZoom = this.clampValidZoom(newZoom);
			this.mainCanvas.setZoom(newZoom);
			this.viewOptionsPanel.setZoomFieldText(newZoom);
		}else if(e.getActionCommand().equals(ViewOptionsPanel.ZOOM_OUT)){
			double oldZoom = this.mainCanvas.getZoom();
			double newZoom = 1.0;
			if(oldZoom <= 1.0)
				newZoom = oldZoom - 0.1;
			else
				newZoom = oldZoom - 0.5;
			newZoom = this.clampValidZoom(newZoom);
			this.mainCanvas.setZoom(newZoom);
			this.viewOptionsPanel.setZoomFieldText(newZoom);
			
		}else if(e.getActionCommand().equals(ViewOptionsPanel.SUPERIMPOSE_TOGGLE)){
			this.mainCanvas.clearAllTracings();
			this.redrawTracings(this.layerOptionsPanel.currentlySelectedLayer,
					this.viewOptionsPanel.isPointsModeOn(),
					this.viewOptionsPanel.isSuperImposeModeOn());
		}else if(e.getActionCommand().equals(ViewOptionsPanel.POINT_MODE_TOGGLE)){
			this.mainCanvas.clearAllTracings();
			this.redrawTracings(this.layerOptionsPanel.currentlySelectedLayer,
					this.viewOptionsPanel.isPointsModeOn(),
					this.viewOptionsPanel.isSuperImposeModeOn());
		}else if(e.getActionCommand().equals(GraphicsPanel.DEMAND_VALIDATION)){
			this.validate();
		}
		else if(e.getActionCommand().equals(GraphicsPanel.PATH_DONE_CMD)){
			int selectedLayer = this.layerOptionsPanel.getCurrentLayer();
			if(selectedLayer>0)
				this.tracingLayers.elementAt(selectedLayer-1).addNewPoints(
					this.mainCanvas.currentPath,this.mainCanvas.currentColors);
		}else if(e.getActionCommand().equals(GraphicsPanel.ERASE_HERE_CMD)){
			TracingLayer currentLayer = null;
			int selectedIndex = this.layerOptionsPanel.getCurrentLayer();
			if(selectedIndex <= 0){
				return;
			}
			int eraseX = this.mainCanvas.oldEraserPoint.x;
			int eraseY = this.mainCanvas.oldEraserPoint.y;
			int eraseRadius = this.mainCanvas.oldEraserRadius;
			currentLayer = this.tracingLayers.elementAt(selectedIndex-1);
			Vector<Vector<Point>> points = currentLayer.getPoints();
			Vector<Vector<Color>> colors = currentLayer.getColors();
			
			for(int i = 0; i<points.size(); i++){
				Vector<Point> path = points.elementAt(i);
				Vector<Color> color= colors.elementAt(i);
				for(int j = 0; j<path.size(); j++){
					if(path.elementAt(j).x >= eraseX - eraseRadius
					&& path.elementAt(j).x <= eraseX + eraseRadius
					&& path.elementAt(j).y >= eraseY - eraseRadius
					&& path.elementAt(j).y <= eraseY + eraseRadius){
						//Create a new path with the stuff past the pivot
						Vector<Point> newpath = new Vector<Point>();
						Vector<Color> newcolor= new Vector<Color>();
						for(int k= j+1; k < path.size(); k++){
							newpath.addElement(path.elementAt(k));
							newcolor.addElement(color.elementAt(k));
						}
						if(newpath.size()>0)
							currentLayer.addNewPoints(newpath, newcolor);
						//delete everything from the old pivot
						for(int k= j; k < path.size();){
							path.removeElementAt(k);
							color.removeElementAt(k);
						}
					}
				}
			}
			currentLayer.cleanEmptyPaths();//clean up any completely empty paths
			this.actionPerformed(new ActionEvent(this,
					0,GraphicsPanel.REFRESH_IMG_CMD));
			
		}else if(e.getActionCommand().equals(GraphicsPanel.REFRESH_IMG_CMD)){
			this.mainCanvas.clearAllTracings();
			this.redrawTracings(this.layerOptionsPanel.currentlySelectedLayer,
					this.viewOptionsPanel.isPointsModeOn(),
					this.viewOptionsPanel.isSuperImposeModeOn());
		}
	}
	
	//TODO: load tracing files
	private void loadTracings(File fin) throws REXPMismatchException {
		Vector <Object> tokens = TracerUtilities.tokenizeFile(fin);
		if(tokens == null){
			TracerUtilities.popUpMsg("Error: Unable to load file! Make sure" +
					" its not in use by a different program.");
			return;
		}

		this.tracingLayers = new Vector<TracingLayer>();
		
		TracingLayer tempLayer = null;
		Vector<Point> pathPoints = null;
		Vector<Color> pathColors = null;
		
		boolean bInsideAPath = false;//Are we currently reading a path?
		boolean bExpectingAColor = false;//Are we trying to read a color?
		
		for(int i = 0; i<tokens.size(); i++){
			Object tok = tokens.elementAt(i);
			Object tok2= null;
			if(i+1 < tokens.size())
				tok2= tokens.elementAt(i+1);
			if(tok instanceof String){
				if(((String)tok).equals("New") && (tok2.equals("Layer")||tok2.equals("Slice"))){
					i++;//i.e. skip over "Layer"
					if(bInsideAPath){
						System.out.println("Warning! Creating a new Layer " +
								"without stopping the previous path!");
						if(tempLayer != null && pathPoints != null 
								&& pathColors!=null){
							tempLayer.addNewPoints(pathPoints, pathColors);
						}
					}
					if(tempLayer != null){
						this.tracingLayers.addElement(tempLayer);
						this.mTracingDataAnalysis.addLayer(tempLayer);
					}
					tempLayer = new TracingLayer();
					bInsideAPath = false;
					bExpectingAColor = false;
				}
				else if(tok.equals("Start")){
					if(bInsideAPath){
						System.out.println("Warning! Starting a new path " +
								"without Stopping the previous one!");
						if(tempLayer != null && pathPoints != null 
								&& pathColors!=null){
							tempLayer.addNewPoints(pathPoints, pathColors);
						}
					}
					bInsideAPath = true;
					bExpectingAColor = false;
					pathPoints = new Vector<Point>();
					pathColors = new Vector<Color>();
				}else if(tok.equals("Stop")){
					if(!bInsideAPath){
						System.out.println("Warning! \"Stop\" reached while " +
								"not even inside a path!");
					}
					if(bExpectingAColor){
						System.out.println("Warning! Expected a color value, "+
								"but \"Stop\" found instead!");
						pathColors.addElement(Color.BLACK);
					}
					bInsideAPath = false;
					bExpectingAColor = false;
					if(tempLayer != null && pathPoints != null 
							&& pathColors != null){
						tempLayer.addNewPoints(pathPoints, pathColors);
					}
					pathPoints = null;
					pathColors = null;
				}else{
					System.out.println("Unkown String: \""+tok+"\"!");
				}
			}else if(tok instanceof Integer){
				int value = ((Integer)tok).intValue();
				if(bInsideAPath){
					if(bExpectingAColor){
						pathColors.addElement(new Color(value,value,value));
						bExpectingAColor = false;
					}else{
						int x = value;
						int y = ((Integer)tok2).intValue();
						Point newPoint = new Point(x,y);
						pathPoints.addElement(newPoint);
						i++;
						bExpectingAColor = true;
					}
				}else{
					System.out.println("Warning! found an Integer but not " +
					"currently inside a path!");
				}
			}else{
				System.out.println("Warning unknown token \""+tok+"\"!");
			}
		}
		//Flush out the last tempLayer
		if(tempLayer != null)
			this.tracingLayers.addElement(tempLayer);
			this.mTracingDataAnalysis.addLayer(tempLayer);
		this.synchronizeLayerIDs();
		//Forcibly turn on Super-Impose mode
		if(!this.viewOptionsPanel.isSuperImposeModeOn()){
			this.viewOptionsPanel.setSuperimposeModeCheckbox(true);
		}
		this.autoAssignLayerColors();
		this.layerOptionsPanel.setCurrentLayer(1);
		this.DataAnalysisWindow.setVisible(true);
		this.DataAnalysisWindow.setLocation(getLocation());
		this.mDataAnalysisMainPanel.setOutPutLable(this.mTracingDataAnalysis.initData());
		return;
	}

	private void saveTracings(String filename) {
		try{
			FileWriter fw = new FileWriter(filename);
			
			for(int i = 0; i<this.tracingLayers.size();i++){
				fw.write("New Layer\n");
				Vector<Vector<Point>> paths = 
						this.tracingLayers.elementAt(i).getPoints();
				Vector<Vector<Color>> colorPaths = 
						this.tracingLayers.elementAt(i).getColors();
				for(int j=0;j<paths.size();j++){
					fw.write("Start\n");
					Vector<Point> points = paths.elementAt(j);
					Vector<Color> colors = colorPaths.elementAt(j);
					for(int k=0;k<points.size();k++){
						fw.write(""+points.elementAt(k).x+" ");
						fw.write(""+points.elementAt(k).y+" ");
						fw.write(""+TracerUtilities.convertRGBtoGreyScale(
								colors.elementAt(k).getRed(),
								colors.elementAt(k).getGreen(),
								colors.elementAt(k).getBlue())
								+"\n");
					}
					fw.write("Stop\n");
					fw.flush();
				}
			}
			fw.flush();
			fw.close();
			
		}catch(Exception e){
			System.out.println(e);
			TracerUtilities.popUpMsg("Error: Unable to save!\n" + e);
			return;
		}
		TracerUtilities.popUpMsg("Tracing data saved to " + filename
				+ " succesfully.");
	}

	private void redrawTracings(int selectedIndex, boolean pointsMode, 
			boolean superImpose) {
		int original_Index = selectedIndex;
		if(this.mainCanvas == null || this.layerOptionsPanel == null)
			return;
		try{
			if(this.tracingLayers.elementAt(selectedIndex-1) == null)
				return;
		}catch(Exception e){
			return;
		}
		
		if(!superImpose){
			//i.e. superImpose mode is off (default)
			if(selectedIndex>0 &&selectedIndex<=this.tracingLayers.size()){
				this.mainCanvas.setDrawingColor(
					this.tracingLayers.elementAt(selectedIndex-1).pointColor);
				Vector<Vector<Point>> points =this.tracingLayers.elementAt(
						selectedIndex-1).getPoints();
				if(!pointsMode){//i.e. points mode is off (default)
					for(int i =0; i<points.size();i++){
						this.mainCanvas.drawTheseLines(points.elementAt(i));
					}
				}
				else{
					for(int i =0; i<points.size();i++){
						this.mainCanvas.drawThesePoints(points.elementAt(i));
					}
				}
			}
		}else{
			for(selectedIndex = 1; selectedIndex-1 < this.tracingLayers.size();
					selectedIndex++){
				this.mainCanvas.setDrawingColor(
					this.tracingLayers.elementAt(selectedIndex-1).pointColor);
				Vector<Vector<Point>> points =this.tracingLayers.elementAt(
						selectedIndex-1).getPoints();
				if(!pointsMode){//i.e. points mode is off (default)
					for(int i =0; i<points.size();i++){
						this.mainCanvas.drawTheseLines(points.elementAt(i));
					}
				}
				else{
					for(int i =0; i<points.size();i++){
						this.mainCanvas.drawThesePoints(points.elementAt(i));
					}
				}
			}
		}
		if(original_Index > 0 && original_Index-1<this.tracingLayers.size())
			this.mainCanvas.setDrawingColor(
				this.tracingLayers.elementAt(original_Index-1).pointColor);
		this.mainCanvas.repaint();
	}

	private void clearThisLayer(int index) {
		this.tracingLayers.setElementAt(new TracingLayer(),index);
	}

	private void deleteCurrentLayer() {
		int selectedLayer = this.layerOptionsPanel.getCurrentLayer();
		if(selectedLayer == 0){
			TracerUtilities.popUpMsg("Cannot delete! You currently have no" +
					" layer selected!");
			return;
		}
		if(selectedLayer > this.tracingLayers.size()){
			System.out.println("Warning! layer "+selectedLayer+
					" is out of bounds!");
			return;
		}
		this.tracingLayers.removeElementAt(selectedLayer-1);
		this.synchronizeLayerIDs();
		this.layerOptionsPanel.setCurrentLayer(selectedLayer);
	}

	private double clampValidZoom(double zoomLevel) {
		if(zoomLevel<0.1)
			return 0.1;
		if(zoomLevel > 10)
			return 10;
		return zoomLevel;
	}

	private void setBackgroundImage(File file) {
		//this.remove(this.imageLabel);
		Toolkit toolkit = Toolkit.getDefaultToolkit();

		/*if(TracerUtilities.getFileExtension(file).equals("jpg")
				|| TracerUtilities.getFileExtension(file).equals("gif")
				|| TracerUtilities.getFileExtension(file).equals("png"))
			this.mainCanvas.setBufferedImage(new ImageIcon(file.getAbsolutePath()));
		else*/
		try{
			BufferedImage bmpImage = ImageIO.read(file);
			//Wait for image to load
			//System.out.println("Image created. Finalizing.");

			if (!toolkit.prepareImage(bmpImage, -1, -1, null)) {
				try {
					final int COMPLETE = ImageObserver.ALLBITS | ImageObserver.FRAMEBITS;
					int status = toolkit.checkImage(bmpImage, -1, -1, null);
					while ((status & COMPLETE) == 0) {
						Thread.sleep(100); // sleep for 100 milliseconds
						status = toolkit.checkImage(bmpImage, -1, -1, null);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//for(int i = 0; i<bmpImage.getWidth(); i++){
			//System.out.println("");
			//	for(int j = 0; j<bmpImage.getHeight();j++){
			//		System.out.print(bmpImage.getRGB(i, j)+' ');
			//	}
			//}
			this.mainCanvas.setBufferedImage(bmpImage);
		}catch(Exception e){
			TracerUtilities.popUpMsg("Error: Unable to load image!");
		}
		//this.add(this.imageLabel, BorderLayout.CENTER);
		this.mainCanvas.repaint();
		this.revalidate();
	}

	public void componentHidden(ComponentEvent arg0) {}
	public void componentMoved(ComponentEvent arg0) {}
	public void componentResized(ComponentEvent ce) {

		//System.out.println("ComponentWidths: "+this.countWidths(
		//		(Container)ce.getComponent()));
		//System.out.println("Rows needed: " + this.findRowsNeeded(
		//		(Container)ce.getComponent()));
		try{
			this.runFlowLayoutHack((Container)ce.getComponent());
		}catch(Exception e){
			System.out.println("Warning! Flow layout hack failure: "+e);
		}
	}

	private void runFlowLayoutHack(Container c){
		int neededRows = this.findRowsNeeded(c);
		if(this.layoutRows != neededRows){
			this.layoutRows = neededRows;
			int maxheight=this.findMaximalHeight(c);
			c.setPreferredSize(
				new Dimension(0, maxheight*this.layoutRows+EXTRA_HEIGHT_PIXELS));
/*			c.setMinimumSize(
				new Dimension(0, maxheight*this.layoutRows+EXTRA_HEIGHT_PIXELS));
			c.setMaximumSize(
				new Dimension(0, maxheight*this.layoutRows+EXTRA_HEIGHT_PIXELS));
*/				
			System.out.println("Maximal height is: "+maxheight+".");
			System.out.println("New rows requested: "+this.layoutRows);
			c.setSize(c.getWidth(), maxheight*this.layoutRows+EXTRA_HEIGHT_PIXELS);
			System.out.println("ActualSize->Width:"+c.getWidth() +
					" Height:"+c.getHeight());
			
		}
		c.validate();
		c.repaint();
	}
	
	
	private int findMaximalHeight(Container c) {
		int maxHeight = 0;
		for(int i = 0; i<c.getComponentCount(); i++){
			if(c.getComponent(i).getHeight() > maxHeight)
				maxHeight = c.getComponent(i).getHeight();
		}
		return maxHeight;
	}

	private int findRowsNeeded(Container c) {
		int count = 1;
		int acceptableWidth = c.getWidth() - EXTRA_WIDTH_PIXELS;
		int runningTotal = 0;
		for(int i=0; i < c.getComponentCount(); i++){
			if(runningTotal == 0)
				runningTotal = c.getComponent(i).getWidth();
			else if(runningTotal+c.getComponent(i).getWidth()>acceptableWidth){
				count++;
				runningTotal = c.getComponent(i).getWidth();
			}
			else{
				runningTotal += c.getComponent(i).getWidth();
			}
		}
		
		return count;
	}

	public void componentShown(ComponentEvent arg0) {}
	
	private class ImageFilter extends FileFilter {

		public final static String jpeg = "jpeg";
	    public final static String jpg = "jpg";
	    public final static String gif = "gif";
	    public final static String tiff = "tiff";
	    public final static String tif = "tif";
	    public final static String png = "png";
	    public final static String bmp = "bmp";
	    public final static String txt = "txt";

		    /*
		     * Get the extension of a file.
		     */
	    public String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
	            ext = s.substring(i+1).toLowerCase();
	        }
	        return ext;
	    }
	    //Accept all directories and all gif, jpg, tiff, or png files.
	    public boolean accept(File f) {
	        if (f.isDirectory()) {
	            return true;
	        }
	        
	        String extension = this.getExtension(f);
	        if (extension != null) {
	            if (//extension.equals(this.tiff) ||
	                //extension.equals(this.tif) ||
		            extension.equals(this.txt) ||
	                extension.equals(this.gif) ||
	                extension.equals(this.jpeg) ||
	                extension.equals(this.jpg) ||
	                extension.equals(this.bmp) ||
	                extension.equals(this.png)) {
	                    return true;
	            } else {
	                return false;
	            }
	        }
	        return false;
	    }

	    //The description of this filter
	    public String getDescription() {
	        return "Supported Files";
	    }
	}
	
	private class SaveFilter extends FileFilter {

	    public final static String txt = "txt";

		    /*
		     * Get the extension of a file.
		     */
	    public String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
	            ext = s.substring(i+1).toLowerCase();
	        }
	        return ext;
	    }
	    //Accept all directories and all gif, jpg, tiff, or png files.
	    public boolean accept(File f) {
	        if (f.isDirectory()) {
	            return true;
	        }
	        
	        String extension = this.getExtension(f);
	        if (extension != null) {
	            if(extension.equals(this.txt)){
	                return true;
	            }else{
	                return false;
	            }
	        }
	        return false;
	    }

	    //The description of this filter
	    public String getDescription() {
	        return "Text files *.txt";
	    }
	}
	
	private class ImagePreview extends JComponent implements PropertyChangeListener {
		ImageIcon thumbnail = null;
		File file = null;
		public ImagePreview(JFileChooser fc) {
			setPreferredSize(new Dimension(100, 50));
			fc.addPropertyChangeListener(this);
		}
		public void loadImage() {
			if (file == null) {
				thumbnail = null;
			return;
		}

	//Don't use createImageIcon (which is a wrapper for getResource)
	//because the image we're trying to load is probably not one
	//of this program's own resources.
	ImageIcon tmpIcon = new ImageIcon(file.getPath());
		if (tmpIcon != null) {
			if (tmpIcon.getIconWidth() > 90) {
				thumbnail = new ImageIcon(tmpIcon.getImage().
			                    getScaledInstance(90, -1,Image.SCALE_DEFAULT));
			} else { //no need to miniaturize
				thumbnail = tmpIcon;
			}
		}
	}

	public void propertyChange(PropertyChangeEvent e) {
		boolean update = false;
		String prop = e.getPropertyName();
		//If the directory changed, don't show an image.
		if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
			file = null;
			update = true;			
			//If a file became selected, find out which one.
		} else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
			file = (File) e.getNewValue();
			update = true;
		}
		//Update the preview accordingly.
		if (update) {
			thumbnail = null;
			if (isShowing()) {
				loadImage();
				repaint();
			}
		}
	}
	
	protected void paintComponent(Graphics g) {
		if (thumbnail == null) {
			loadImage();
		}
		if (thumbnail != null) {
			int x = getWidth()/2 - thumbnail.getIconWidth()/2;
			int y = getHeight()/2 - thumbnail.getIconHeight()/2;
			if (y < 0) {
				y = 0;
			}
			if (x < 5) {
				x = 5;
			}
				thumbnail.paintIcon(this, g, x, y);
			}
		}
	}
	private void autoAssignLayerColors(){
		for(int i = 0; i<this.tracingLayers.size();i++){
			final int TOTAL_COLORS = 8;
			if(i%TOTAL_COLORS==0){
				this.tracingLayers.elementAt(i).pointColor = Color.RED;
			}else if(i%TOTAL_COLORS==1){
				this.tracingLayers.elementAt(i).pointColor = Color.GREEN;
			}else if(i%TOTAL_COLORS==2){
				this.tracingLayers.elementAt(i).pointColor = Color.BLUE;
			}else if(i%TOTAL_COLORS==3){
				this.tracingLayers.elementAt(i).pointColor = Color.ORANGE;
			}else if(i%TOTAL_COLORS==4){
				this.tracingLayers.elementAt(i).pointColor = Color.CYAN;
			}else if(i%TOTAL_COLORS==5){
				this.tracingLayers.elementAt(i).pointColor = Color.YELLOW;
			}else if(i%TOTAL_COLORS==6){
				this.tracingLayers.elementAt(i).pointColor = Color.MAGENTA;
			}else{
				this.tracingLayers.elementAt(i).pointColor = Color.PINK;
			}
		}
	}
}
