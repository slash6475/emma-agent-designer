// This is copyrighted source file, part of Rakiura JFern package.
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn.gui;

import org.rakiura.cpn.Net;

/**
 * Figure representing a CPN Net.
 *
 *@author Martin Fleurke
 *@author <a href="mailto:mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.14 $
 *@since 3.0
 */
public interface CPNAbstractNetFigure extends CPNAbstractFigure {
	
	/**
	 * Returns imports annotation figure.
	 * @return imports annotation figure.
	 */
	CPNAnnotationFigure getImportsFigure();
	void setImportsFigure (CPNAnnotationFigure aFig);
	
	/**
	 * Returns declaration annotation figure.
	 * @return declaration annotation figure.
	 */
	CPNAnnotationFigure getDeclarationFigure();
	void setDeclarationFigure (CPNAnnotationFigure aFig);
	
	/**
	 * Returns implements annotation figure.
	 * @return implements annotation figure.
	 */
	CPNAnnotationFigure getImplementFigure();
	void setImplementFigure (CPNAnnotationFigure aFig);

	/**
	 * Returns the net that is represented by this figure.
	 * @return a {@link org.rakiura.cpn.Net Net} represented by this figure.
	 */
	Net getNet();

}