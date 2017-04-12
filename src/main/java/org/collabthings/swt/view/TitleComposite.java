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

import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class TitleComposite extends CTComposite {

	private Composite ctitle;

	public TitleComposite(Composite parent, String title) {
		super(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);
		this.setLayout(gridLayout);

		ctitle = new CTComposite(this, SWT.NONE);
		GridLayout gl_ctitle = new GridLayout(10, false);
		LOTSWT.setDefaults(gl_ctitle);

		ctitle.setLayout(gl_ctitle);
		ctitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		CTLabel lblScripts = new CTLabel(ctitle, SWT.NONE);
		lblScripts.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblScripts.setText(title);
	}

	public void addButton(String string, ButtonAction action) {
		CTButton b = new CTButton(ctitle, SWT.None);
		b.setText(string);
		b.addSelectionListener(() -> {
			action.action();
		});
	}

	public interface ButtonAction {
		public void action();
	}
}
