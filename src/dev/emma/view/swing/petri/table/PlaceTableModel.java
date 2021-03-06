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
				{ "Figure", "Place" },  //0
				{ "Name", p.getName() }, //1
				{ "Parent", p.getParent().getName() }, //2
				{ "Token", p.hasToken()}, //3
				{ "Type", p.getData().getClass().getSimpleName()}, //4
				{ "IsImported",p.getData().isImported()}, //5
				{ "Input",p.getData().hasInputRight()}, //6
				{ "Output",p.getData().hasOutputRight()}, //7
				{ "Value", p.getData().get()} //8
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
		boolean ok=true;
		switch(rowIndex){
		case 1:
			p.setName((String)aValue);
			break;
		case 3:
			ok = p.setToken((boolean)aValue);
			break;
		case 4:
			try {
				ok = p.setData(ClassFounder.getUnmappedResourceClass((String)aValue));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case 5:
			ok=p.getData().setImport((boolean)aValue);
			break;
		case 6:
			ok=p.getData().setInputRight((boolean)aValue);
			break;
		case 7:
			ok=p.getData().setOutputRight((boolean)aValue);
			break;
		case 8:
			p.getData().put((String)aValue);
			ok=p.getData().get().equals(aValue);
			break;
		default:
			return;
		}
		if(ok){
			this.data[rowIndex][columnIndex] = aValue;
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}
}