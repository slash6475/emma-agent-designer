package emma.view.swing;

import java.awt.EventQueue;
import emma.view.swing.petri.SwingController;

public class Main {
	public static void main(String[] args){
		new Console();
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				SwingController control = new SwingController();
				Window window = new Window(control);
				window.setVisible(true);
			}
		});
	}
		
}
