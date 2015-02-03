import java.awt.Dimension;
import javax.swing.*;



public class Start {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		//TracerWindow t = new TracerWindow();
		//t.setVisible(true);
		JFrame tracerWindow = new JFrame("Image Tracer");
		tracerWindow.setSize(new Dimension(800,600));
		tracerWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tracerWindow.getContentPane().add(new TracerMainPanel(null));
		tracerWindow.setVisible(true);
	}

}
