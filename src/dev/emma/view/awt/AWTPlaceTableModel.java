//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package emma.view.awt;

/**/
import javax.swing.table.AbstractTableModel;

import emma.petri.model.Place;
import emma.petri.view.PlaceFigure;

class AWTPlaceTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 3978989890098771506L;
	
	final String[] columnNames = { "Attribute", "Value" };
	final Object[][] data;
	final PlaceFigure place;

	AWTPlaceTableModel(PlaceFigure placeFigure) {
		this.place = placeFigure;
		Place p = place.getPlace();
		this.data = new Object[][] {
			{ "Figure", "Place" },
			{ "Name", p.getName() },
			{ "Type", p.getType() },
			{ "Scope", p.getParent().getName() }
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
		this.place.setName((String) aValue);
		this.data[rowIndex][columnIndex] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	public void rowSelected(int row) {
		//no need to do anything
	}
}
