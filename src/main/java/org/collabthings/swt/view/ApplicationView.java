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

import java.util.Date;

import org.collabthings.app.CTApp;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTObject;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
import org.collabthings.tk.CTComposite;
import org.collabthings.tk.CTSelectionAdapter;
import org.collabthings.tk.CTText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ApplicationView extends CTComposite implements CTAppControl {

	private CTText applicationtext;
	private CTApplication application;
	private CTText bottomtext;

	private CTApp app;

	public ApplicationView(Composite c, CTApp app, AppWindow appWindow, CTApplication application) {
		super(c, SWT.NONE);
		this.app = app;

		setLayout(new GridLayout(1, false));
		this.application = application;

		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		GridData gd_sashForm = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_sashForm.heightHint = 200;
		sashForm.setLayoutData(gd_sashForm);

		applicationtext = new CTText(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		applicationtext.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				key(arg0);
			}
		});

		if (application != null) {
			applicationtext.setText("" + application.getObject().toYaml());
		} else {
			applicationtext.setText("Application null");
		}

		bottomtext = new CTText(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		sashForm.setWeights(new int[] { 3, 1 });
	}

	protected synchronized void key(KeyEvent arg0) {

	}

	@Override
	public CTObject getObject() {
		return application;
	}

	public synchronized void save() {
		String sstring = this.applicationtext.getText();
		doSave(sstring);
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Application: " + application;
	}

	private void doSave(String sapplicationtext) {
		if (sapplicationtext != null && (!application.getObject().toYaml().equals(sapplicationtext))) {

			CTApplication s = this.app.getObjectFactory().getApplication();
			s.setApplication(sapplicationtext);
			getDisplay().asyncExec(() -> {
				application.setApplication(sapplicationtext);
				bottomtext.append("OK " + new Date() + "\n");
			});
		}
	}

	@Override
	public void selected(AppWindow appWindow) {

	}

	@Override
	public MenuItem createMenu(Menu menu) {
		MenuItem miapplications = new MenuItem(menu, SWT.CASCADE);
		miapplications.setText("Application");

		Menu mapplication = new Menu(miapplications);
		miapplications.setMenu(mapplication);

		MenuItem msave = new MenuItem(mapplication, SWT.NONE);
		msave.setText("Save");
		msave.addSelectionListener(new CTSelectionAdapter(e -> save()));

		return miapplications;
	}
}
