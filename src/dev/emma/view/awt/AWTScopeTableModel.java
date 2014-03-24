package emma.view.awt;

import javax.swing.table.AbstractTableModel;

import emma.petri.model.Scope;
import emma.petri.view.ScopeFigure;

class AWTScopeTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 3978989890098771506L;
	
	final String[] columnNames = { "Attribute", "Value" };
	final Object[][] data;
	final Scope sub;

	AWTScopeTableModel(ScopeFigure s) {
		this.sub = s.getScope();
		this.data = new Object[][] {
			{ "Figure", "Subnet" },
			{ "Name", sub.getName() },
			{ "Parent", sub.getParent().getName() },
			{ "Places", sub.getPlaces().size() },
			{ "Transitions", sub.getTransitions().size() }
		};
	}

	public int getColumnCount(){
		return this.columnNames.length;
	}
	public int getRowCount(){
		return this.data.length;
	}
	public String getColumnName(int col){
		return this.columnNames[col];
	}
	public Object getValueAt(int row, int col){
		return this.data[row][col];
	}
	
	public Class<? extends Object> getColumnClass(int c){
		return getValueAt(0, c).getClass();
	}

	public boolean isCellEditable(int row, int col){
		if(col==0){
			return false;
		}
		if(row==1){
			return true;
		}
		return false;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch(rowIndex){
		case 1:
			sub.setName((String)aValue);
			break;
		default:	
			break;
		}
		this.data[rowIndex][columnIndex] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	public void rowSelected(int row) {
	}
}
