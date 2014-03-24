//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package emma.view.awt;

/**/
import javax.swing.table.AbstractTableModel;

import emma.petri.model.Transition;
import emma.petri.view.TransitionFigure;

class AWTTransitionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 3978989890098771506L;
	
	final String[] columnNames = { "Attribute", "Value" };
	final Object[][] data;
	final Transition trans;

	AWTTransitionTableModel(TransitionFigure transitionFigure) {
		this.trans = transitionFigure.getTransition();
		this.data = new Object[][] {
			{ "Figure", "Transition" },
			{ "Parent", trans.getParent().getName() },
			{ "Place", trans.getPlace().getName() },
			{ "Condition", trans.getCondition() }
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
		if(row==3){
			return true;
		}
		return false;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch(rowIndex){
		case 3:
			trans.setCondition((String)aValue);
		default:	
			break;
		}
		this.data[rowIndex][columnIndex] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	public void rowSelected(int row) {
	}
}
