//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

/**/
import javax.swing.table.AbstractTableModel;

/**
 * Represents CPNTransitionFigure property sheet for visualisation.
 *
 * <br><br>
 * CPNTransitionTableModel.java created on 30/05/2003 11:08:08<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0
 */

class CPNTransitionTableModel extends AbstractTableModel
	implements RowSelectionListener {

	private static final long serialVersionUID = 3978986578678788149L;
	
	final String[] columnNames = { "Attribute", "Value" };
	final Object[][] data;
	final CPNTransitionFigure cpnTransitionFigure;
	final NetEditor editor;

	/**
	 * Creates a tableModel that represents a CPNTransitionFigure
	 * @param transitionFigure the Figure that is the data source
	 * to be able to set the CellEditorListeners
	 */
	CPNTransitionTableModel (NetEditor anEditor, CPNTransitionFigure transitionFigure) {
		this.editor = anEditor;
		this.cpnTransitionFigure = transitionFigure;
		this.data = new Object[][] {
			{ "Figure", "CPN Transition" },
			{ "ID", this.cpnTransitionFigure.getTransition().getID()},
			{ "Name", this.cpnTransitionFigure.getTransition().getName()},
			{ "Type", this.cpnTransitionFigure.getTransition().getTypeText() },
			{ "Specification", this.cpnTransitionFigure.getTransition().getSpecificationText() },
			{ "Guard", this.cpnTransitionFigure.getTransition().getGuardText()	},
			{ "Action", this.cpnTransitionFigure.getTransition().getActionText()}
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
		if (col < 1 || row < 2 || row  > 4) {
			return false;
		}
		return true;
	}

	@SuppressWarnings({"unqualified-field-access","unqualified-field-access", "unqualified-field-access"})
	public void setValueAt(
		final Object aValue,
		final int rowIndex,
		final int columnIndex) {
		this.data[rowIndex][columnIndex] = aValue;
		switch (rowIndex) {
			case 2:
				this.cpnTransitionFigure.getNameFigure().setText((String) aValue);
				break;
			case 3:
				cpnTransitionFigure.getTransition().setTypeText((String) aValue);
				break;
			case 4:
				cpnTransitionFigure.getTransition().setSpecificationText((String) aValue);
				break;
			case 5:
				cpnTransitionFigure.getGuardFigure().setAnnotation((String) aValue);
				break;
			case 6:
				cpnTransitionFigure.getActionFigure().setAnnotation((String) aValue);
				break;
			default:
				throw new RuntimeException ("Bad row index");
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	public void rowSelected(final int row) {
		if (row == 5 || row == 6  ) {
			this.editor.figureTextInspector.setColumn(1);
			this.editor.figureTextInspector.setRow(row);
			String label;
			String button;
			label = "Edit " + (String) this.data[row][0];
			button = "Set " + (String) this.data[row][0];;

			this.editor.inspectArea.setCallback(
					label, button, this.editor.figureTextInspector,(String)
					this.editor.figureAttributeTable.getModel().getValueAt(row, 1));
		}
	}

} /////////////end of CPNTransitionTableModel ///////////////////////