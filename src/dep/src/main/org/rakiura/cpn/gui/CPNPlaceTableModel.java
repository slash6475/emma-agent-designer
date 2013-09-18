//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

/**/
import javax.swing.table.AbstractTableModel;

/**
 * Represents CPNPlaceFigure table model for figure properties visualisation.
 *
 * <br><br>
 * CPNPlaceTableModel.java created on 30/05/2003 11:00:37<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0
 */
class CPNPlaceTableModel extends AbstractTableModel implements RowSelectionListener {

	private static final long serialVersionUID = 3978989890098771506L;
	
	final String[] columnNames = { "Attribute", "Value" };
	final Object[][] data;
	final CPNPlaceFigure cpnPlaceFigure;

	/**
	 * Creates a tableModel that represents a CPNPlaceFigure
	 * @param placeFigure the Figure that is the data source
	 */
	CPNPlaceTableModel(final CPNPlaceFigure placeFigure) {
		this.cpnPlaceFigure = placeFigure;
		this.data = new Object[][] {
			{ "Figure", "CPN Place" },
			{ "ID", this.cpnPlaceFigure.getPlace().getID() },
			{ "Name", this.cpnPlaceFigure.getPlace().getName() }
		};
	}

	public int getColumnCount() {
		return this.columnNames.length;
	}
	public int getRowCount() {
		return this.data.length;
	}
	public String getColumnName(int col) {
		return this.columnNames[col];
	}
	public Object getValueAt(int row, int col) {
		return this.data[row][col];
	}
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public boolean isCellEditable(int row, int col) {
		if (col < 1 || row < 2) {
			return false;
		}
		return true;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		this.cpnPlaceFigure.getNameFigure().setText((String) aValue);
		this.data[rowIndex][columnIndex] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	public void rowSelected(int row) {
		//no need to do anything
	}

} //end of CPNPlaceTableModel
