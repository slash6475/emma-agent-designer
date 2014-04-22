package emma.view.swing.petri.table;

import javax.swing.table.AbstractTableModel;

import emma.petri.model.Arc;
import emma.petri.model.InputArc;

public class ArcTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 3978989890098771506L;
	
	private static final String[] columnNames = { "Attribute", "Value" };
	private final Object[][] data;
	private final Arc arc;

	public ArcTableModel(Arc s) {
		this.arc = s;
		this.data = new Object[][] {
			{ "Figure", "Arc" },
			{ "Place", arc.getPlace() },
			{ "Transition", arc.getTransition() },
			{ "Type", (arc instanceof InputArc)?"Input":"Output"},
			{ "Expression", arc.getExpression()}
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
		if(row==4){
			return true;
		}
		return false;
	}


	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		arc.setExpression((String) aValue);
		this.fireTableCellUpdated(rowIndex, columnIndex);
	}
}