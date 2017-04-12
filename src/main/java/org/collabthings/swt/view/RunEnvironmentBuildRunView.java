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
import org.collabthings.environment.CTEnvironmentTask;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.CTRuntimeEvent;
import org.collabthings.environment.RunEnvironmentListener;
import org.collabthings.model.CTObject;
import org.collabthings.model.run.CTRunEnvironmentBuilder;
import org.collabthings.simulation.CTSimpleSimulation;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

public class RunEnvironmentBuildRunView extends CTComposite implements CTAppControl, RunEnvironmentListener {

	// an hour
	private static final int MAX_RUNTIME = 60 * 1000 * 60;
	private CTSimpleSimulation s;
	private CTText text;

	public RunEnvironmentBuildRunView(Composite parent, CTApp app, AppWindow appWindow,
			CTRunEnvironmentBuilder builder) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));

		CTRunEnvironment runEnvironment = builder.getRunEnvironment();

		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite = new CTComposite(sashForm, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		text = new CTText(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite c_view = new CTComposite(sashForm, SWT.NONE);
		c_view.setLayout(new GridLayout(1, false));

		sashForm.setWeights(new int[] { 143, 294 });

		runEnvironment.addListener(this);

		s = new CTSimpleSimulation(runEnvironment);

		new Thread(() -> {
			s.run(MAX_RUNTIME);
		}).start();

	}

	@Override
	public CTObject getObject() {
		return null;
	}

	@Override
	public void event(CTRuntimeEvent e) {
		appendText("" + e.getTime() + " " + e.getName());
		appendText(" --- " + e.getObject());
		appendText(" --- " + e.getValues());
		appendText("");
	}

	@Override
	public void taskFailed(CTRunEnvironment runenv, CTEnvironmentTask task) {
		appendText("Task failed : " + task);
	}

	private void appendText(String string) {
		if (!isDisposed()) {
			getDisplay().asyncExec(() -> {
				text.append(string);
				text.append("\n");
			});
		}
	}

	@Override
	public MenuItem createMenu(Menu menu) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Run";
	}

	@Override
	public void selected(AppWindow appWindow) {

	}
}
