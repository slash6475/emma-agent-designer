package emma.view.test;

import java.awt.EventQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import emma.petri.view.XMLParser;
import emma.view.awt.AWTDrawTools;

public class Main {
	public static void main(String[] args){
		new Console();
		try {
			XMLParser.init();
			AWTDrawTools.init();
		} catch (TransformerConfigurationException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				AWTController control = new AWTController();
				AWTPetriWindow window = new AWTPetriWindow(control);
				window.setVisible(true);
			}
		});
	}
		
}
