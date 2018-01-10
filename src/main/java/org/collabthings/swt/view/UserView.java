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
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
import org.collabthings.tk.CTComposite;
import org.collabthings.tk.CTLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import waazdoh.datamodel.UserVO;

public class UserView extends CTComposite implements CTAppControl {
	private AppWindow window;
	private CTApp app;
	private UserVO u;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public UserView(Composite parent, CTApp app, AppWindow window, String userid) {
		super(parent, SWT.NO_FOCUS | SWT.NO_MERGE_PAINTS | SWT.NO_REDRAW_RESIZE | SWT.NO_RADIO_GROUP | SWT.EMBEDDED);

		this.app = app;
		this.window = window;

		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite = new CTComposite(sashForm, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Composite cuserinfo = new CTComposite(composite, SWT.NONE);

		cuserinfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		cuserinfo.setLayout(new GridLayout(1, false));

		CTLabel lname = new CTLabel(cuserinfo, SWT.NONE);
		lname.setText("TESTING");
		lname.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		ObjectSearchView searchView = new ObjectSearchView(composite, app, window);
		searchView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		searchView.search("" + userid, 0, 10);

		UserPublishedView dview = new UserPublishedView(sashForm, app, window);
		sashForm.setWeights(new int[] { 1, 1 });

		new Thread(() -> {
			u = app.getLClient().getService().getUsers().getUser(userid);
			getDisplay().syncExec(() -> {
				if (u != null) {
					lname.setText("" + u.getUsername());
				}
			});

			dview.setUser(u);
		}).start();

	}

	@Override
	public CTObject getObject() {
		return null;
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public String getControlName() {
		return "User: " + this.u.getUsername();
	}

	@Override
	public void selected(AppWindow appWindow) {

	}

	@Override
	public MenuItem createMenu(Menu menu) {
		// TODO Auto-generated method stub
		return null;
	}
}
