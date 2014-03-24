package emma.view.test;

import java.awt.BorderLayout;
import java.awt.Label;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import emma.view.FigureHandler;

public class BorderPanel extends JPanel {
	private static final long serialVersionUID = -3252042432305047846L;
	
	private JTable table;
	
	public BorderPanel(FigureHandler control){
		super();
		this.setLayout(new BorderLayout());
		this.add(new Label("Element Properties"),BorderLayout.NORTH);
		table = new JTable();
		table.setVisible(false);
		this.add(table,BorderLayout.CENTER);
		control.setPropertiesPanel(this);
	}
	
	public void setProperties(TableModel data){
		if(data==null){
			table.setVisible(false);
		}
		else{
			table.setModel(data);
			table.setVisible(true);
		}
	}
}
