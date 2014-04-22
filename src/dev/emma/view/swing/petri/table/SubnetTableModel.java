package emma.view.swing.petri.table;

import javax.swing.table.AbstractTableModel;

import emma.petri.model.Subnet;

public class SubnetTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 3978989890098771506L;
	
	private static final String[] columnNames = { "Attribute", "Value" };
	private final Object[][] data;
	private final Subnet sub;

	public SubnetTableModel(Subnet s) {
		this.sub = s;
		this.data = new Object[][] {
			{ "Figure", "Subnet" },
			{ "Name", sub.getName() },
			{ "Scopes", sub.getScopes().size() }
		};
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
		return false;
	}


	@Override
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
}