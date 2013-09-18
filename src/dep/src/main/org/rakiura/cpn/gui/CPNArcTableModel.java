//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import javax.swing.table.AbstractTableModel;

/**
 * Represents arc figures properties sheet.
 *
 * <br><br>
 * CPNArcTableModel.java created on 30/05/2003 11:11:11<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0
 */
class CPNArcTableModel extends AbstractTableModel implements RowSelectionListener {

	private static final long serialVersionUID = 3833185835066799924L;
	
	final String[] columnNames = { "Attribute", "Value" };
	final Object[][] data;
	final CPNArcFigure cpnArcFigure;
	final NetEditor editor;
	final int type;

	/**
	 * Creates a tableModel that represents a CPNTransitionFigure
	 * @param arcFigure the Figure that is the data source
	 * to be able to set the CellEditorListeners
	 */
	CPNArcTableModel (NetEditor anEditor, CPNArcFigure arcFigure) {
		this.editor = anEditor;
		this.cpnArcFigure = arcFigure;
		this.type = ((Integer) this.cpnArcFigure.getAttribute("ArcType")).intValue();
		String typeString;
		if (this.type == CPNArcFigure.INPUT_ARC)
			typeString = "INPUT_ARC";
		else if (this.type == CPNArcFigure.OUTPUT_ARC)
			typeString = "OUTPUT_ARC";
		else if (this.type == CPNArcFigure.UNDEFINED_ARC)
			typeString = "UNDEFINED_ARC";
		else
			typeString = "???";
	  if (this.type == CPNArcFigure.INPUT_ARC)  {
			this.data = new Object[][] {
				{"Figure", "CPN Arc" },
				{"ID", this.cpnArcFigure.getArc().getID() },
				{"Name", this.cpnArcFigure.getArc().getName()},
				{"Type", typeString },
				{"Expression", this.cpnArcFigure.getExpressionFigure().getAnnotation()	},
				{"Guard", this.cpnArcFigure.getGuardFigure().getAnnotation() }
			};
	  	} else if (this.type == CPNArcFigure.OUTPUT_ARC) {
			this.data = new Object[][] {
				{"Figure", "CPN Arc" },
				{"ID", this.cpnArcFigure.getArc().getID() },
				{"Name", this.cpnArcFigure.getArc().getName()},
				{"Type", typeString },
				{"Expression", this.cpnArcFigure.getExpressionFigure().getAnnotation()	}
			};
		} else this.data = new Object[][] {{ "Figure", "CPN Arc" }, {"Type", typeString }};
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
		return false;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		this.data[rowIndex][columnIndex] = aValue;
		if (rowIndex == 4) {
			this.cpnArcFigure.getExpressionFigure().setAnnotation ((String) aValue);
		} else if (rowIndex == 5) {
			this.cpnArcFigure.getGuardFigure().setAnnotation((String) aValue);
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	public void rowSelected (final int row) {
		if (row == 4 || row == 5) {
			this.editor.figureTextInspector.setColumn (1);
			this.editor.figureTextInspector.setRow (row);
			String label;
			String button;
//			if (row == 4) {
//				label = "Edit Expression";
//				button = "Set Expression";
//			} else {
//				label = "Edit Guard";
//				button = "Set Guard";
//			}
			label = "Edit " + (String) this.data[row][0];
			button = "Set " + (String) this.data[row][0];;

			this.editor.inspectArea.setCallback (label, button, this.editor.figureTextInspector,
			   (String) this.editor.figureAttributeTable.getModel().getValueAt(row, 1));
		}
	}

} //end of CPNArcTableModel



