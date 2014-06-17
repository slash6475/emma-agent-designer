package emma.view.swing.petri.table;

import javax.swing.table.AbstractTableModel;

import emma.petri.model.Place;
import emma.tools.ClassFounder;

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
				{ "Token", p.hasToken()},
				{ "Type", ""}
			};
		}
		else{ 
			this.data = new Object[][] {
				{ "Figure", "Place" },
				{ "Name", p.getName() },
				{ "Parent", p.getParent().getName() },
				{ "Token", p.hasToken()},
				{ "Type", p.getData().getClass().getSimpleName()},
				{ "IsImported",p.getData().isImported()},
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
		if(row!=0 && row!=2){
			return true;
		}
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch(rowIndex){
		case 1:
			p.setName((String)aValue);
			break;
		case 3:
			p.setToken((boolean)aValue);
			break;
		case 4:
			try {
				p.setData(ClassFounder.getUnmappedResourceClass((String)aValue));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case 5:
			p.getData().setImport((boolean)aValue);
			break;
		case 6:
			p.getData().put((String)aValue);
			break;
		default:
			return;
		}
		this.data[rowIndex][columnIndex] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}
}