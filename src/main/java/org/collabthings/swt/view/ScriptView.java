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
import org.collabthings.model.CTObject;
import org.collabthings.model.CTScript;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
import org.collabthings.swt.app.CTSelectionAdapter;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTText;
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

public class ScriptView extends CTComposite implements CTAppControl {

	private CTText scripttext;
	private CTScript script;
	private CTText bottomtext;

	private CTApp app;

	public ScriptView(Composite c, CTApp app, AppWindow appWindow, CTScript script) {
		super(c, SWT.NONE);
		this.app = app;

		setLayout(new GridLayout(1, false));
		this.script = script;

		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		GridData gd_sashForm = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_sashForm.heightHint = 200;
		sashForm.setLayoutData(gd_sashForm);

		scripttext = new CTText(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		scripttext.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				key(arg0);
			}
		});

		if (script != null) {
			scripttext.setText("" + script.getScript());
		} else {
			scripttext.setText("Script null");
		}

		bottomtext = new CTText(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		sashForm.setWeights(new int[] { 3, 1 });
	}

	protected synchronized void key(KeyEvent arg0) {

	}

	@Override
	public CTObject getObject() {
		return script;
	}

	public synchronized void save() {
		String sstring = this.scripttext.getText();
		doSave(sstring);
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Script: " + script;
	}

	private void doSave(String sscripttext) {
		if (sscripttext != null && (script.getScript() == null || !script.getScript().equals(sscripttext))) {

			CTScript s = this.app.getObjectFactory().getScript();
			s.setScript(sscripttext);
			getDisplay().asyncExec(() -> {
				if (s.isOK()) {
					script.setScript(sscripttext);
					bottomtext.append("OK " + new Date() + "\n");
				} else {
					String error = s.getError();
					bottomtext.append("ERROR " + error + "\n");
				}
			});
		}
	}

	@Override
	public void selected(AppWindow appWindow) {

	}

	@Override
	public MenuItem createMenu(Menu menu) {
		MenuItem miscripts = new MenuItem(menu, SWT.CASCADE);
		miscripts.setText("Script");

		Menu mscript = new Menu(miscripts);
		miscripts.setMenu(mscript);

		MenuItem msave = new MenuItem(mscript, SWT.NONE);
		msave.setText("Save");
		msave.addSelectionListener(new CTSelectionAdapter(e -> save()));

		return miscripts;
	}
}
