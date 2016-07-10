package org.collabthings.swt.view;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import waazdoh.common.vo.UserVO;

public class UserView extends CTComposite implements LOTAppControl {
	private AppWindow window;
	private LOTApp app;
	private UserVO u;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public UserView(Composite parent, LOTApp app, AppWindow window, String userid) {
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
				lname.setText("" + u.getUsername());
			});

			dview.setUser(u);
		}).start();
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
