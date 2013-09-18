//This is copyrighted source file, part of Rakiura JFern package.
//See the file LICENSE for copyright information and the terms and conditions
//for copying, distributing and modifications of Rakiura JFern package.
//Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import org.rakiura.draw.Figure;
import org.rakiura.draw.figure.TextFigure;

/**
 * Represents a manager for text inspector area for figures.
 * 
 * <br><br>
 * TextInspectorManager.java created on 30/05/2003 10:40:25<br><br>
 *
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 
 */
class TextInspectorManager implements TextInspectorCallback {

	private Figure figure;
	private int row;
	private int column;
	private NetEditor editor;

	public TextInspectorManager (NetEditor anEditor) {
		this.editor = anEditor;
	}

	public void setRow (final int aRow) {
		this.figure = null;
		this.row = aRow;
	}
	public int getRow() {
		return this.row;
	}
	public void setColumn (final int aColumn) {
		this.figure = null;
		this.column = aColumn;
	}
	public int getColumn() {
		return this.column;
	}

	public void setFigure (final Figure aFigure) {
		this.figure = aFigure;
	}
	public Figure getFigure () {
		return this.figure;
	}

	public void commit (final String aText) {
		if (this.figure instanceof CPNAnnotationFigure) {
			((CPNAnnotationFigure) this.figure).setAnnotation (aText);
		} else if (this.figure instanceof TextFigure) {
			((TextFigure) this.figure).setText (aText);
		} else if (this.row != 0 || this.column != 0) {
			this.editor.figureAttributeTable.getModel().setValueAt (aText, this.row, this.column);
		}
		this.figure = null;
	}
		
	public void cancel () { //do nothing upon cancelation
		this.figure = null;
	}
		
} ///END TextInspectorManager
