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
package org.collabthings.swt.controls.dialogs;

import org.collabthings.swt.app.CTSelectionAdapter;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CTTextDialog extends Dialog {
	private CTText etext;
	private String text;
	private boolean popupclosed = false;

	public CTTextDialog(Shell arg0) {
		super(arg0);
	}

	public void open(String title) {
		Shell parent = getParent();
		Shell shell = new Shell(parent, SWT.NO_TRIM | SWT.ON_TOP);
		shell.setSize(281, 125);
		shell.setText("Color");

		GridLayout glshell = new GridLayout();
		shell.setLayout(glshell);

		Label ltitle = new Label(shell, SWT.NONE);
		ltitle.setText(title);

		Composite composite = new CTComposite(shell, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.widthHint = 293;
		composite.setLayoutData(gd_composite);
		composite.setLayout(new GridLayout());

		etext = new CTText(composite, SWT.NONE);
		etext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		etext.addEditDoneListener((e) -> this.text = etext.getText());

		Button bOK = new Button(composite, SWT.NONE);
		bOK.addSelectionListener(new CTSelectionAdapter(e -> popupclosed = true));
		bOK.setText("OK");

		shell.pack();

		this.popupclosed = false;

		shell.forceFocus();
		shell.open();

		Display display = parent.getDisplay();
		while (!popupclosed && !parent.isDisposed() && !shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		shell.dispose();

	}

	public String getValue() {
		return this.text;
	}

}