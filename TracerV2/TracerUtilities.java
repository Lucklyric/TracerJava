import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.swing.*;


public class TracerUtilities {

	public static ImageIcon getThisImage(String url, Object classObj){
		try{
			return new ImageIcon(classObj.getClass().getResource(url));
		}catch(Exception e){}
		try{
			return new ImageIcon(url);
		}catch(Exception e){}
		return new ImageIcon();
	}
	
	public static BufferedImage convertImageIconToBufferedImage(ImageIcon icon){
		  int w = icon.getIconWidth();
		  int h = icon.getIconHeight();
		  BufferedImage bufImg =new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		  Graphics2D imageGraphics = bufImg.createGraphics();
		  imageGraphics.drawImage(icon.getImage(), 0, 0, null);
		  return bufImg;
	  }
	public static void popUpMsg(String msg){
		JOptionPane.showMessageDialog(null, "<html><table width=400>"+msg+
				"</table></html>");
	}
	public static boolean askYesNoQuestion(String msg){
		if(JOptionPane.showConfirmDialog(null, "<html><table width=400>"+msg+
			  "</table></html>","Confirm Action",JOptionPane.YES_NO_OPTION)==0)
			return true;
		return false;
	}
	
	public static int convertRGBtoGreyScale(int r, int g, int b){
		int result = (int)( ( ((float)(r+g+b)) /3.0f) +0.5f );
		if(result < 0)
			return 0;
		if(result > 255)
			return 255;
		return result;
	}
	
	public static String getFileExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
	

	public static Object getFileExtension(String s) {
		String ext = "";
		int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
	}
	
	public static Vector<Object> tokenizeFile(File fin){
		// Correctly Classified Instances
		//results.indexOf("");
		Vector<Object> v = new Vector<Object>();
		
		try {
	        StreamTokenizer token =new StreamTokenizer(new FileReader(fin));
	        token.eolIsSignificant(false);
	        //token.wordChars('0','9');
	        //token.ordinaryChar('/');
		    
		    while(token.nextToken() != StreamTokenizer.TT_EOF){
		    	if(token.ttype == StreamTokenizer.TT_NUMBER)
		    		v.addElement(new Integer((int)token.nval));
		    	else if(token.ttype==StreamTokenizer.TT_WORD||token.sval!=null)
		    		v.addElement(token.sval);
		    	else{
		    		v.addElement(""+(char)token.ttype);
		    	}
		    }
	    } catch (IOException e) {
	    		System.out.println("Unable to tokenize!");
	    		System.err.println(e);
	    		return null;	
	    }
		return v;
	}
	
}
