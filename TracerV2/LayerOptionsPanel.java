import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
	
public class LayerOptionsPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	protected static final String LAYER_CHANGE = "LAYER_CHANGE";
	protected static final String COLOR_CHANGE = "COLOR_CHANGE";
	protected static final String NEW_LAYER    = "NEW_LAYER";
	protected static final String DELETE_LAYER = "DELETE_LAYER";
	protected static final String CLEAR_LAYER  = "CLEAR_LAYER";
	protected static final String ERASE_MODE  = "ERASE_MODE";
	//Data Analysis Implement
	protected static final String ANA_MODE	 = "ANA_MODE";
	
	private ActionListener listener;
	private JComboBox layerSelector;
	private JButton colourButton;
	private JButton newButton;
	private JButton deleteButton;
	private JButton clearButton;
	private JToggleButton eraserToggleButton;
	private JToggleButton anaModeToggleButton;
	public int currentlySelectedLayer;
	
	public LayerOptionsPanel(ActionListener al, String imgURLs){
		this.listener = al;
		this.setLayout(new FlowLayout(FlowLayout.LEFT,1,1));
		this.setBorder(BorderFactory.createTitledBorder("Layer Options"));
		
		//First element is the combo box containing all the possible layers
		Vector<String> defaultLayers = new Vector<String>();
		defaultLayers.addElement("Not Initialized!");
		this.layerSelector = new JComboBox(defaultLayers);
		this.layerSelector.setToolTipText("Select which layer to trace on");
		this.layerSelector.setActionCommand(LAYER_CHANGE);
		this.layerSelector.addActionListener(this.listener);
		
		//Second is the button that controls what the colour for the layer is
		this.colourButton = new JButton(TracerUtilities.getThisImage(
				imgURLs+"color.png", this.listener));
		this.colourButton.setToolTipText("Change what color your tracings are");
		this.colourButton.setActionCommand(COLOR_CHANGE);
		this.colourButton.setBorder(BorderFactory.createRaisedBevelBorder());
		this.colourButton.addActionListener(this.listener);
		
		//Third is the new layer button
		this.newButton = new JButton(TracerUtilities.getThisImage(
				imgURLs+"new.png", this.listener));
		this.newButton.setToolTipText("Create a new Layer to trace on");
		this.newButton.setActionCommand(NEW_LAYER);
		this.newButton.setBorder(BorderFactory.createRaisedBevelBorder());
		this.newButton.addActionListener(this.listener);
		
		//Fourth is the delete layer button
		this.deleteButton = new JButton(TracerUtilities.getThisImage(
				imgURLs+"delete.png", this.listener));
		this.deleteButton.setToolTipText("Delete the currently selected layer");
		this.deleteButton.setActionCommand(DELETE_LAYER);
		this.deleteButton.setBorder(BorderFactory.createRaisedBevelBorder());
		this.deleteButton.addActionListener(this.listener);
		
		//Fifth is the clear layer button
		this.clearButton = new JButton(TracerUtilities.getThisImage(
				imgURLs+"reset.png", this.listener));
		this.clearButton.setToolTipText("Clear all tracings from the current layer");
		this.clearButton.setActionCommand(CLEAR_LAYER);
		this.clearButton.setBorder(BorderFactory.createRaisedBevelBorder());
		this.clearButton.addActionListener(this.listener);

		//Fifth is the clear layer button
		this.eraserToggleButton = new JToggleButton(TracerUtilities.getThisImage(
				imgURLs+"erase.png", this.listener));
		this.eraserToggleButton.setToolTipText("Toggle \"Eraser Mode\"");
		this.eraserToggleButton.setActionCommand(ERASE_MODE);
		this.eraserToggleButton.setBorder(BorderFactory.createRaisedBevelBorder());
		this.eraserToggleButton.addActionListener(this.listener);
		
		//Data Analysis mode button
		this.anaModeToggleButton = new JToggleButton(TracerUtilities.getThisImage(
				imgURLs+"erase.png", this.listener));
		this.anaModeToggleButton.setToolTipText("Toggle \"Analysis Mode\"");
		this.anaModeToggleButton.setActionCommand(ANA_MODE);
		this.anaModeToggleButton.setBorder(BorderFactory.createRaisedBevelBorder());
		this.anaModeToggleButton.addActionListener(this.listener);
		
		this.add(this.layerSelector);
		this.add(this.colourButton);
		this.add(this.newButton);
		this.add(this.deleteButton);
		this.add(this.clearButton);
		this.add(this.eraserToggleButton);
		this.add(this.anaModeToggleButton);
		
		
		this.currentlySelectedLayer = 0;
	}
	
	public boolean eraserModeActivated(){
		return this.eraserToggleButton.isSelected();
	}
	
	public boolean anaModeActivated(){
		return this.anaModeToggleButton.isSelected();
	}
	
	
	public void setCurrentLayer(int num){
		//System.out.println("Setting selected image to "+num);
		//System.out.println("component count is: "+this.layerSelector.getItemCount());
		if(num >= this.layerSelector.getItemCount()){
			this.layerSelector.setSelectedIndex(this.layerSelector
					.getItemCount()-1);
			this.currentlySelectedLayer = this.layerSelector.getItemCount()-1;
		}
		else{
			this.layerSelector.setSelectedIndex(num);
			this.currentlySelectedLayer = num;
		}
	}
	
	public int getCurrentLayer(){
		return this.currentlySelectedLayer;
	}
	
	public void updateLayersComboBox(Vector<String> layerNames){
		this.layerSelector.removeAllItems();
		for(int i = 0; i<layerNames.size(); i++)
			this.layerSelector.addItem(layerNames.elementAt(i));
		this.validate();
		this.repaint();
	}

}
