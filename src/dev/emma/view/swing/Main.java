package emma.view.swing;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.apache.log4j.Logger;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class);
	
	public static void main(String[] args){
		try {
			//Setting cooja (moderne) UI
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			System.out.println("Unable to set Nimbus look&feel:"+e.getMessage());
			logger.warn("Unable to set Nimbus look&feel:"+e.getMessage());
		}
		//GUI pour afficher la sortie standard
		new Console();
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				Window window = new Window();
				window.setVisible(true);
			}
		});
	}
}
