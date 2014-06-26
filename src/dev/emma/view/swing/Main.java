package emma.view.swing;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class Main {
	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			System.out.println("Unable to set Nimbus look&feel:"+e.getMessage());
		}
		new Console();
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				Window window = new Window();
				window.setVisible(true);
			}
		});
	}
}
