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

import org.collabthings.model.CTMaterial;
import org.collabthings.tk.CTComposite;
import org.collabthings.tk.CTDoubleEditor;
import org.collabthings.tk.CTResourceManagerFactory;
import org.collabthings.tk.CTSelectionAdapter;
import org.collabthings.tk.CTDoubleEditor.ChangeListener;
import org.collabthings.swt.controls.CTMaterialEditor;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CTMaterialColorPopupDialog extends Dialog {
	private CTMaterialEditor materialeditor;
	private CTMaterial material;

	private CTDoubleEditor ered;
	private CTDoubleEditor egreen;
	private CTDoubleEditor eblue;

	private boolean popupclosed;
	private Composite composite_1;
	private double dred;
	private double dgreen;
	private double dblue;

	public CTMaterialColorPopupDialog(Shell arg0, CTMaterial material, CTMaterialEditor editor) {
		super(arg0);
		this.material = material;
		this.materialeditor = editor;
	}

	public void open() {
		Shell parent = getParent();
		Shell shell = new Shell(parent, SWT.NO_TRIM | SWT.ON_TOP);
		shell.setText("Color");

		ChangeListener<Double> listener = (e) -> {
			updateColor();
		};

		GridLayout gl_shell = new GridLayout();
		gl_shell.numColumns = 2;
		shell.setLayout(gl_shell);

		composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite = new CTComposite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout());

		ered = new CTDoubleEditor(composite, this.material.getColor()[0], listener);
		ered.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		egreen = new CTDoubleEditor(composite, this.material.getColor()[1], listener);
		egreen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		eblue = new CTDoubleEditor(composite, this.material.getColor()[2], listener);
		eblue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		updateColor();

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

		this.materialeditor.colorChanged(dred, dgreen, dblue);
	}

	private void updateColor() {
		try {
			dred = ered.getValue();
			dgreen = egreen.getValue();
			dblue = eblue.getValue();

			RGB rgb = getRGB();
			composite_1.setBackground(CTResourceManagerFactory.instance().getColor(rgb));
		} catch (IllegalArgumentException e) {
			LLog.getLogger(this).error(this, "updateColor", e);
		}
	}

	public RGB getRGB() {
		double red = ered.getValue();
		double green = egreen.getValue();
		double blue = eblue.getValue();
		return CTResourceManagerFactory.instance().getRGBWithDoubled(red, green, blue);
	}

	public void setListener(CTMaterialEditor ctMaterialEditor) {
		this.materialeditor = ctMaterialEditor;
	}
}