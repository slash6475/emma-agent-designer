package emma.view.swing.petri.table;

import javax.swing.table.AbstractTableModel;

import emma.petri.model.Transition;

public class TransitionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 3978989890098771506L;
	
	private static final String[] columnNames = { "Attribute", "Value" };
	private final Object[][] data;
	private final Transition t;

	public TransitionTableModel(Transition s) {
		this.t = s;
		this.data = new Object[][] {
			{ "Figure", "Transition" },
			{ "Parent", t.getParent().getName() },
			{ "Place", t.getPlace().getName()}
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
		return false;
	}


	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		//fireTableCellUpdated(rowIndex, columnIndex);
	}
}