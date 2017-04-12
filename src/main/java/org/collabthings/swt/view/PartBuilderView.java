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
import org.collabthings.model.CTObject;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.ObjectViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class PartBuilderView extends CTComposite implements CTAppControl {

	private CTApp app;
	private AppWindow window;
	private CTPartBuilder builder;
	private ScriptView scriptview;
	private GLSceneView view;

	public PartBuilderView(Composite parent, CTApp app, AppWindow appWindow, CTPart p, CTPartBuilder pb,
			GLSceneView view) {
		super(parent, SWT.NONE);
		this.window = appWindow;
		this.app = app;
		this.builder = pb;
		this.view = view;

		setLayout(new GridLayout(1, false));

		Composite composite = new CTComposite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		ObjectViewer oview = new ObjectViewer(app, window, composite);
		oview.setObject(builder);
		oview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite ctools = new CTComposite(composite, SWT.NONE);
		GridLayout ctoolslayout = new GridLayout(1, false);
		ctoolslayout.numColumns = 5;
		ctools.setLayout(ctoolslayout);
		ctools.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		CTButton btnSave = new CTButton(ctools, SWT.NONE);
		btnSave.addSelectionListener(() -> {
			save();
		});
		btnSave.setText("save");

		CTButton btnRun = new CTButton(ctools, SWT.NONE);
		btnRun.addSelectionListener(() -> {
			pb.run(p);
		});

		btnRun.setText("Run");

		scriptview = new ScriptView(this, app, window, builder.getScript());
		scriptview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	@Override
	public CTObject getObject() {
		return this.builder;
	}

	private void save() {
		scriptview.save();
	}

	@Override
	public MenuItem createMenu(Menu menu) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void selected(AppWindow appWindow) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getControlName() {
		return "PartBuilder:" + builder;
	}

	@Override
	public Control getControl() {
		return this;
	}
}
