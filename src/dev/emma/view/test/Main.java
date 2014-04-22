package emma.view.test;

import java.awt.EventQueue;

import emma.view.swing.PetriWindow;
import emma.view.swing.SwingController;

public class Main {
	public static void main(String[] args){
		new Console();
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				SwingController control = new SwingController();
				PetriWindow window = new PetriWindow(control);
				window.setVisible(true);
			}
		});
	}
		
}
