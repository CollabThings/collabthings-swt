package org.libraryofthings.swt.view;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.LOTAppControl;
import org.libraryofthings.swt.app.LOTApp;

import swing2swt.layout.FlowLayout;
import waazdoh.common.vo.UserVO;

public class UserView extends Composite implements LOTAppControl {

	private LOTApp app;
	private UserVO u;
	private Composite cpublisheditems;
	private AppWindow window;

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

		new Thread(() -> {
			u = app.getLClient().getService().getUsers().getUser(userid);
			getDisplay().syncExec(() -> {
				lname.setText("" + u.getUsername());
			});
		}).start();

		Composite cpublished = new Composite(this, SWT.NONE);
		cpublished.setLayout(new GridLayout(1, false));
		cpublished.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblPublished = new Label(cpublished, SWT.NONE);
		lblPublished.setText("Published");

		cpublisheditems = new Composite(cpublished, SWT.NONE);
		cpublisheditems.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		cpublisheditems.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));

		addPublishedItem("test");

		List<String> published = app.getLClient().getStorage().getUserPublished(userid, 0, 50);
		for (String string : published) {
			addPublishedItem(string);
		}

		Label lblSearch = new Label(this, SWT.NONE);
		lblSearch.setText("Search");

		SearchView searchView = new SearchView(this, app, window, true);
		searchView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		searchView.search("" + userid, 0, 10);
	}

	private void addPublishedItem(String string) {
		Composite item = new Composite(cpublisheditems, SWT.NONE);
		item.setLayout(new GridLayout(2, false));
		Label l = new Label(item, SWT.NONE);
		l.setText(string);

		Button btnView = new Button(item, SWT.NONE);
		btnView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				viewPublished(string);
			}
		});
		btnView.setText("View");
	}

	private void viewPublished(String item) {
		String itemdata = app.getLClient().getStorage().readStorage(u, item);
		String type = item.substring(item.lastIndexOf('/') + 1);
		window.view(type, itemdata);
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
