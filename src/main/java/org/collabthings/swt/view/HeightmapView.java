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
import org.collabthings.model.CTHeightmap;
import org.collabthings.model.CTObject;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
import org.collabthings.swt.LOTSWT;
import org.collabthings.tk.CTButton;
import org.collabthings.tk.CTComposite;
import org.collabthings.tk.CTResourceManagerFactory;
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

public class HeightmapView extends CTComposite implements CTAppControl {

	private AppWindow window;
	private CTText scripttext;
	private CTHeightmap hm;
	private CTText bottomtext;

	private Composite composite;
	private Composite ctools;

	public HeightmapView(Composite c, CTApp app, AppWindow window2, CTHeightmap o) {
		this(c, app, window2, o, true);
	}

	public HeightmapView(Composite c, CTApp app, AppWindow nwindow, CTHeightmap o, boolean b) {
		super(c, SWT.NONE);
		this.window = nwindow;

		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);
		setLayout(gridLayout);
		this.hm = o;

		if (b) {
			ctools = new CTComposite(this, SWT.NONE);
			ctools.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			ctools.setLayout(gridLayout);
			ctools.setBackground(CTResourceManagerFactory.instance().getActiontitleBackground());
			CTButton bsave = new CTButton(ctools, SWT.NONE);
			bsave.setText("Save");
			bsave.addSelectionListener(() -> save());
		}

		Composite left;

		if (b) {
			SashForm sashForm_1 = new SashForm(this, SWT.NONE);
			left = sashForm_1;
		} else {
			left = new CTComposite(this, SWT.NONE);
			GridLayout leftlayout = new GridLayout();
			LOTSWT.setDefaults(leftlayout);
			left.setLayout(leftlayout);
		}

		left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		int textstyle = SWT.BORDER | SWT.CANCEL | SWT.MULTI | SWT.V_SCROLL;
		if (b) {
			textstyle = textstyle | SWT.H_SCROLL;
		}
		scripttext = new CTText(left, textstyle);
		scripttext.setFont(CTResourceManagerFactory.instance().getDefaultFont());

		if (!b) {
			scripttext.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		}

		scripttext.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				key(arg0);
			}
		});

		scripttext.setText("" + hm.getScript());

		if (b) {
			SashForm sashForm = new SashForm(left, SWT.VERTICAL);

			composite = new CTComposite(sashForm, SWT.NONE);
			composite.setLayout(gridLayout);

			bottomtext = new CTText(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
			sashForm.setWeights(new int[] { 1, 1 });
		}
	}

	@Override
	public CTObject getObject() {
		return hm;
	}

	protected synchronized void key(KeyEvent arg0) {

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
		return "Script: " + hm;
	}

	private void doSave(String sscripttext) {
		if (sscripttext != null && (hm.getScript() == null || !hm.getScript().equals(sscripttext))) {

			String oldscript = hm.getScript();
			getDisplay().asyncExec(() -> {
				hm.setScript(sscripttext);
				if (hm.isOK()) {
					if (bottomtext != null) {
						bottomtext.append("OK " + new Date() + "\n");
					}
				} else {
					String error = hm.getError();
					if (bottomtext != null) {
						bottomtext.append("ERROR " + error + "\n");
					} else {
						window.showError("ERROR " + error);
					}

					hm.setScript(oldscript);

				}
			});
		}
	}

	@Override
	public void selected(AppWindow appWindow) {
		//
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
