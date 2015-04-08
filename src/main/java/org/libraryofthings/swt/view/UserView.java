package org.libraryofthings.swt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.LOTAppControl;
import org.libraryofthings.swt.app.LOTApp;

import waazdoh.common.vo.UserVO;

public class UserView extends Composite implements LOTAppControl {

	private LOTApp app;

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

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 1;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 1;
		setLayout(gridLayout);

		Label lname = new Label(this, SWT.NONE);
		lname.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		new Thread(() -> {
			UserVO u = app.getLClient().getService().getUsers().getUser(userid);
			getDisplay().syncExec(() -> {
				lname.setText("" + u.getUsername());
			});
		}).start();

		SearchView searchView = new SearchView(this, app, window, false);
		searchView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		searchView.search("" + userid);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
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
