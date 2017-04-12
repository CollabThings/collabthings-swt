/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/
package org.collabthings.swt.view;

import org.collabthings.app.CTApp;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import waazdoh.client.model.WBinaryID;

public class CTBinaryImage extends CTComposite {

	private CTApp app;
	private WBinaryID id;
	private CTLabel label;

	public CTBinaryImage(CTApp app, Composite arg0, int arg1) {
		super(arg0, arg1);
		this.app = app;

		setLayout(new GridLayout());
		label = new CTLabel(this, SWT.NONE);
		label.setText("Image not found");
	}

	public void setId(WBinaryID binaryID) {
		this.id = binaryID;
		label.setText("Image loading");
	}

}
