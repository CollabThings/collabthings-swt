package org.collabthings.swt.view;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.app.LOTApp;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wb.swt.SWTResourceManager;

import waazdoh.common.vo.UserVO;

public class UserView extends Composite implements LOTAppControl {
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
		super(parent, SWT.BORDER | SWT.NO_FOCUS | SWT.NO_MERGE_PAINTS | SWT.NO_REDRAW_RESIZE
				| SWT.NO_RADIO_GROUP | SWT.EMBEDDED);

		this.app = app;
		this.window = window;

		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		Label lname = new Label(this, SWT.NONE);
		lname.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		SearchView searchView = new SearchView(sashForm, app, window, true);
		searchView.search("" + userid, 0, 10);

		UserPublishedView dview = new UserPublishedView(sashForm, app, window);

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
