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
public class DataAnalysisPanel extends JPanel implements ActionListener, ComponentListener{

	javax.swing.JTextArea jLabel2 = null;
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DataAnalysisPanel(Applet ap){
		super();
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createTitledBorder("Data Analysis"));
		this.jLabel2 = new javax.swing.JTextArea();
		jLabel2.setText("OutPut:");
		
		this.add(jLabel2);
		}
	
	
	public void setOutPutLable(String txt){
		this.jLabel2.setText(txt);
	}
	
	
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
