package emma.view.test;

import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import emma.petri.view.XMLParser;
import emma.view.PetriViewer;
import emma.view.awt.AWTController;
import emma.view.awt.AWTPetriCanvas;

public class Main {
	public static void main(String[] args){
		new Console();
		try {
			XMLParser.init();
		} catch (TransformerConfigurationException
				| ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				AWTController control = new AWTController();
				PetriViewer window = new PetriSwingWindow(control);
				AWTPetriCanvas canvas = new AWTPetriCanvas(control);
				window.setCanvas(canvas);
				window.setVisible(true);
			}
		});
	}
		
}
