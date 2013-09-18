//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

/**/
import javax.swing.table.AbstractTableModel;

import org.rakiura.cpn.Net;


/**
 * Represents a properties table model for CPNNetFigure.
 *
 * <br><br>
 * CPNNetTableModel.java created on 30/05/2003 10:32:37<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0
 */
class CPNNetTableModel	extends AbstractTableModel implements RowSelectionListener {

	private static final long serialVersionUID = 4051327833882046775L;
	
	final String[] columnNames = { "Attribute", "Value" };
	final Object[][] data;
	final CPNAbstractNetFigure cpnNetFigure;
	private NetEditor editor;

	/**
	 * Creates a tableModel that represents a CPNNetFigure
	 * @param anEditor a NetEditor to edit the (java)code in.
	 * @param netFigure the Figure that is the data source
	 */
	CPNNetTableModel(NetEditor anEditor, CPNAbstractNetFigure netFigure) {
		this.editor = anEditor;
		this.cpnNetFigure = netFigure;
		Net net = this.cpnNetFigure.getNet();
		this.data = new Object[][] {
			{ "Figure", "CPN Net" },
			{ "ID", net.getID() },
			{ "Name", this.cpnNetFigure.getNet().getName() },
			{ "CPNLang", net.getCpnLang() },
			{ "Type", net.getTypeText() },
			{ "Imports", this.cpnNetFigure.getNet().getImportText() },
			{ "Implements", this.cpnNetFigure.getNet().getImplementsText() },
			{ "Declaration", this.cpnNetFigure.getNet().getDeclarationText() }
		};
		//String[] multiLineEditable = {(String) data[4][0], (String) data[5][0], (String) data[6][0]};
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
		if (col >= 1 && ( row == 3 || row == 2) ) {
			return true;
		}
		return false;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		this.data[rowIndex][columnIndex] = aValue;
		switch (rowIndex) {
			case 2:
				this.cpnNetFigure.getNameFigure().setText ((String) aValue);
				break;
			case 3:
				this.cpnNetFigure.getNet().setCpnLang((String) aValue);
				break;
			case 4:
				this.cpnNetFigure.getNet().setTypeText((String) aValue);
				break;
			case 5:
				this.cpnNetFigure.getImportsFigure().setAnnotation ((String) aValue);
				break;
			case 6:
				this.cpnNetFigure.getImplementFigure().setAnnotation ((String) aValue);
				break;
			case 7:
				this.cpnNetFigure.getDeclarationFigure().setAnnotation ((String) aValue);
				break;
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	public void rowSelected(final int row) {
		if (row == 7 || row == 6 || row == 4 || row == 5) {
			this.editor.figureTextInspector.setColumn(1);
			this.editor.figureTextInspector.setRow(row);
			String label, button;
//			if (row == 4) {
//				label = "Edit imports"; button = "Set imports";
//			} else if (row == 5) {
//				label = "Edit declarations"; button = "Set declarations";
//				String a = (String) data[row][0];
//			} else {
//				label = "Edit implements"; button = "Set implements";
//			}
			label = "Edit " + (String) this.data[row][0];
			button = "Set " + (String) this.data[row][0];;
			this.editor.inspectArea.setCallback (label, button, this.editor.figureTextInspector,
				((String) this.editor.figureAttributeTable.getModel().getValueAt(row, 1)));
		}
	}

} //end of CPNNetTableModel

