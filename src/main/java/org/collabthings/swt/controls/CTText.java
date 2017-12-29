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

import org.collabthings.CTEvent;
import org.collabthings.CTListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class CTText extends CTComposite {
	private Text text;

	public CTText(Composite arg0, int arg1) {
		super(arg0, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		text = new Text(this, arg1);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackground(CTResourceManagerFactory.instance().getColor(SWT.COLOR_BLACK));
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_composite.heightHint = 1;
		composite.setLayoutData(gd_composite);
	}

	@Override
	public void addFocusListener(FocusListener l) {
		text.addFocusListener(l);
	}

	public void setEditable(boolean arg0) {
		text.setEditable(arg0);
	}

	public void setText(String stext) {
		this.text.setText(stext);
	}

	public String getText() {
		return text.getText();
	}

	public void append(String string) {
		text.append(string);
	}

	public void setValidated(boolean b) {
		if (b) {
			text.setForeground(CTResourceManagerFactory.instance().getTextEditorColor());
		} else {
			text.setForeground(CTResourceManagerFactory.instance().getTextErrorColor());
		}
	}

	public void addEditDoneListener(CTListener l) {
		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				l.event(new CTEvent("focus lost"));
			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
	}
}
