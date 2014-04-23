package emma.view.swing.petri.table;

import javax.swing.table.AbstractTableModel;

import emma.model.resources.Resource;
import emma.petri.model.Place;

public class PlaceTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 3978989890098771506L;
	
	private static final String[] columnNames = { "Attribute", "Value" };
	private final Object[][] data;
	private final Place p;

	public PlaceTableModel(Place p) {
		this.p = p;
		if(p.getData()==null){
			this.data = new Object[][] {
				{ "Figure", "Place" },
				{ "Name", p.getName() },
				{ "Parent", p.getParent().getName() },
				{ "Type", ""}
			};
		}
		else{ 
			this.data = new Object[][] {
				{ "Figure", "Place" },
				{ "Name", p.getName() },
				{ "Parent", p.getParent().getName() },
				{ "Type", p.getData().getClass().getSimpleName()},
				{ "Value", p.getData().get()}
			};
		}
	}

	@Override
	public int getColumnCount(){
		return columnNames.length;
	}

	@Override
	public int getRowCount(){
		return this.data.length;
	}
	

	@Override
	public String getColumnName(int col){
		return columnNames[col];
	}
	

	@Override
	public Object getValueAt(int row, int col){
		return this.data[row][col];
	}

	@Override
	public Class<? extends Object> getColumnClass(int c){
		return this.getValueAt(0, c).getClass();
	}

	@Override
	public boolean isCellEditable(int row, int col){
		if(col==0){
			return false;
		}
		if(row==1){
			return true;
		}
		else if(p.getData()==null && row==3){
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch(rowIndex){
		case 1:
			p.setName((String)aValue);
			break;
		case 3:
			try {
				p.setType((Class<? extends Resource>) Class.forName("emma.model.resources."+aValue));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		default:	
			break;
		}
		this.data[rowIndex][columnIndex] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}
}