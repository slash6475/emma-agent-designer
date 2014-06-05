package emma.view.swing;

import java.awt.EventQueue;

public class Main {
	public static void main(String[] args){
		new Console();
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				Window window = new Window();
				window.setVisible(true);
			}
		});
	}
}
