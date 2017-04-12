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
package org.collabthings.swt.controls;

import org.collabthings.swt.SWTResourceManager;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CTLabel {

	private Label label;

	public CTLabel(Composite composite_1, int none) {
		this.label = new Label(composite_1, none);

		label.setBackground(composite_1.getBackground());
		label.setFont(SWTResourceManager.getDefaultFont());
		label.setForeground(composite_1.getForeground());
	}

	public void setAlignment(int right) {
		this.label.setAlignment(right);
	}

	public void setLayoutData(Object gridData) {
		this.label.setLayoutData(gridData);
	}

	public void setText(String string) {
		if (string != null) {
			this.label.setText(string);
		} else {
			this.label.setText("unknown");
		}
	}

	public String getText() {
		return label.getText();
	}

	public void addMouseListener(MouseListener mouseAdapter) {
		this.label.addMouseListener(mouseAdapter);
	}

	public Control getControl() {
		return this.label;
	}

	public void setBounds(int i, int j, int k, int l) {
		this.label.setBounds(i, j, k, l);
	}

	public boolean isDisposed() {
		return this.label.isDisposed();
	}

	public Display getDisplay() {
		return this.label.getDisplay();
	}

	public void setBackground(Color color) {
		this.label.setBackground(color);
	}

	public void setFont(int size, int style) {
		this.label.setFont(SWTResourceManager.getDefaultFont(size, style));
	}

	public void setEnabled(boolean b) {
		this.label.setEnabled(b);
	}

	public Shell getShell() {
		return label.getShell();
	}

	public Label getLabel() {
		return this.label;
	}

	public void setTitleFont() {
		label.setFont(SWTResourceManager.getTitleFont());
		label.setForeground(SWTResourceManager.getTitleColor());
	}

	public void setFont(Font font) {
		label.setFont(font);
	}

	public void setColor(Color color) {
		label.setForeground(color);
	}

}
