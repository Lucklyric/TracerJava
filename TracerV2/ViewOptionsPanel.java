import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;


public class ViewOptionsPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	protected static final String ZOOM_OUT = "ZOOM_OUT";
	protected static final String ZOOM_CHANGE = "ZOOM_CHANGE";
	protected static final String ZOOM_IN = "ZOOM_IN";
	protected static final String SUPERIMPOSE_TOGGLE = "SUPERIMPOSE_TOGGLE";
	protected static final String POINT_MODE_TOGGLE = "POINT_MODE_TOGGLE";
	
	private JCheckBox pointModeCheckbox;
	private JCheckBox superimposeCheckbox;
	 
	
	private ActionListener listener;
	
	JTextField manualZoomField;
	
	public ViewOptionsPanel(ActionListener al, String imgURLs){
		this.listener = al;
		this.setLayout(new FlowLayout(FlowLayout.LEFT,1,1));
		this.setBorder(BorderFactory.createTitledBorder("View Options"));
		
		//Firstly, we have a button that shrinks the image or "zooms out"
		JButton shrinkButton = new JButton(TracerUtilities.getThisImage(
				imgURLs+"zoomout.png", listener));
		shrinkButton.setActionCommand(ZOOM_OUT);
		shrinkButton.addActionListener(this.listener);
		shrinkButton.setBorder(null);
		this.add(shrinkButton);
		
		//Secondly, we have a Text field to manually adjust zoom
		this.manualZoomField = new JTextField("100",3);
		this.manualZoomField.setActionCommand(ZOOM_CHANGE);
		this.manualZoomField.addActionListener(this.listener);
		this.manualZoomField.setHorizontalAlignment(JTextField.CENTER);
		this.add(this.manualZoomField);
		this.add(new JLabel("%"));
		
		//Thirdly, we have a button expand the image or "zoom in"
		JButton expandButton = new JButton(TracerUtilities.getThisImage(
				imgURLs+"zoomin.png", this.listener));
		expandButton.setActionCommand(ZOOM_IN);
		expandButton.addActionListener(this.listener);
		expandButton.setBorder(null);
		this.add(expandButton);
		
		//Fourthly, we add the Checkbox to toggle "superimpose"
		this.superimposeCheckbox = new JCheckBox("Superimpose");
		this.superimposeCheckbox.setActionCommand(SUPERIMPOSE_TOGGLE);
		this.superimposeCheckbox.addActionListener(this.listener);
		this.add(this.superimposeCheckbox);
		
		//Fifthly, we add the Checkbox to toggle "point mode"
		this.pointModeCheckbox = new JCheckBox("Point mode");
		this.pointModeCheckbox.setActionCommand(POINT_MODE_TOGGLE);
		this.pointModeCheckbox.addActionListener(this.listener);
		this.add(this.pointModeCheckbox);
		
	}
	
	
	public void setZoomFieldText(double zval){
		this.manualZoomField.setText(""+(int)(zval*100.0+0.5));
	}
	
	public void setZoomFieldText(String text){
		this.manualZoomField.setText(text);
	}
	
	public String getZoomFieldText(){
		return this.manualZoomField.getText();
	}

	public boolean isPointsModeOn() {
		return this.pointModeCheckbox.isSelected();
	}

	public boolean isSuperImposeModeOn() {
		return this.superimposeCheckbox.isSelected();
	}
	
	public void setPointModeCheckbox(boolean selected){
		this.pointModeCheckbox.setSelected(selected);
	}
	
	public void setSuperimposeModeCheckbox(boolean selected){
		this.superimposeCheckbox.setSelected(selected);
	}
}
