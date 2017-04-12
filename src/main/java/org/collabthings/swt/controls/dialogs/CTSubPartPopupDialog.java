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

import org.collabthings.app.CTApp;
import org.collabthings.math.CTMath;
import org.collabthings.model.CTSubPart;
import org.collabthings.swt.app.CTSelectionAdapter;
import org.collabthings.swt.controls.CTText;
import org.collabthings.swt.controls.CTDoubleEditor;
import org.collabthings.swt.controls.CTVectorEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import waazdoh.common.WStringID;

public class CTSubPartPopupDialog extends Dialog {

	private boolean popupclosed;

	private CTSubPart subpart;

	private CTVectorEditor loc;

	private Composite composite;

	private CTVectorEditor n;

	private CTDoubleEditor a;
	private Label lblLoc;
	private Label lblN;
	private Label lblAngle;
	private CTText tname;
	private Label lblName;
	private Label lblPartid;
	private CTText tpartid;

	private CTApp app;
	private Label lpartbookmark;
	private CTText tpartbookmark;

	public CTSubPartPopupDialog(Shell shell, CTApp app, CTSubPart data) {
		super(shell);
		this.subpart = data;
		this.app = app;
	}

	public void open() {
		Shell parent = getParent();
		Shell shell = new Shell(parent, SWT.DIALOG_TRIM);
		shell.setSize(337, 262);
		shell.setText("SubPart " + subpart.getNamePath());

		GridLayout gl_shell = new GridLayout();
		shell.setLayout(gl_shell);

		composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		lblName = new Label(composite, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("name");

		tname = new CTText(composite, SWT.BORDER);
		tname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tname.setText(subpart.getName());
		tname.addEditDoneListener(e -> subpart.setName(tname.getText()));
		lblLoc = new Label(composite, SWT.NONE);
		lblLoc.setText("loc");

		loc = new CTVectorEditor(composite, subpart.getLocation(), v -> {
			updateOrientation();
		});

		loc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblN = new Label(composite, SWT.NONE);
		lblN.setText("n");

		n = new CTVectorEditor(composite, subpart.getNormal(), v -> {
			updateOrientation();
		});

		GridData gd_n = new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1);
		gd_n.widthHint = 349;
		n.setLayoutData(gd_n);

		lblAngle = new Label(composite, SWT.NONE);
		lblAngle.setText("angle");

		a = new CTDoubleEditor(composite, CTMath.radToDegrees(subpart.getAngle()), d -> {
			updateOrientation();
		});
		a.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblPartid = new Label(composite, SWT.NONE);
		lblPartid.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPartid.setText("Part id");

		tpartid = new CTText(composite, SWT.BORDER);
		tpartid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tpartid.setText(subpart.getPart().getID().toString());
		tpartid.addEditDoneListener(
				e -> subpart.setPart(app.getObjectFactory().getPart(new WStringID(tpartid.getText()))));

		lpartbookmark = new Label(composite, SWT.NONE);
		lpartbookmark.setText("Part bm");

		tpartbookmark = new CTText(composite, SWT.BORDER);
		tpartbookmark.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		if (subpart.getPartBookmark() != null) {
			tpartbookmark.setText(subpart.getPartBookmark());
		}
		tpartbookmark.addEditDoneListener(e -> subpart.setPartBookmark(tpartbookmark.getText()));

		new Label(composite, SWT.NONE);

		Button bOK = new Button(composite, SWT.NONE);
		bOK.addSelectionListener(new CTSelectionAdapter(e -> {
			subpart.getNormal().normalize();
			popupclosed = true;

		}));
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

		if (!shell.isDisposed()) {
			shell.dispose();
		}

	}

	private void updateOrientation() {
		subpart.setOrientation(loc.getValue(), this.n.getValue().normalize(), CTMath.degreesToRad(a.getValue()));
	}

}