package org.libraryofthings.swt.view;

import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
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

import waazdoh.common.WLogger;
import waazdoh.common.vo.UserVO;

public class UserView extends Composite implements LOTAppControl {

	private LOTApp app;
	private UserVO u;
	private Composite cpublisheditems;
	private AppWindow window;

	private int publishedcount;

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
		lblPublished.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblPublished.setText("Published");

		cpublisheditems = new Composite(cpublished, SWT.NONE);
		GridLayout gl_cpublisheditems = new GridLayout(1, false);
		gl_cpublisheditems.marginWidth = 0;
		gl_cpublisheditems.verticalSpacing = 0;
		gl_cpublisheditems.marginHeight = 0;
		cpublisheditems.setLayout(gl_cpublisheditems);
		cpublisheditems.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		addPublishedItem("testitem1");
		addPublishedItem("testitem2");

		List<String> published = app.getLClient().getStorage().getUserPublished(userid, 0, 50);
		WLogger.getLogger(this).info("got published list " + published);
		for (String string : published) {
			addPublishedItem(string);
		}

		Label lblSearch = new Label(this, SWT.NONE);
		lblSearch.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblSearch.setText("Search");

		SearchView searchView = new SearchView(this, app, window, true);
		searchView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		searchView.search("" + userid, 0, 10);
	}

	private void addPublishedItem(String string) {
		Composite item = new Composite(cpublisheditems, SWT.NONE);

		item.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_item = new GridLayout(2, false);
		gl_item.verticalSpacing = 0;
		gl_item.marginWidth = 0;
		gl_item.marginHeight = 0;
		gl_item.horizontalSpacing = 0;
		item.setLayout(gl_item);
		Label l = new Label(item, SWT.NONE);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setText(string);

		Button btnView = new Button(item, SWT.NONE);
		btnView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				viewPublished(string);
			}
		});
		btnView.setText("View");

		if (publishedcount++ % 2 == 0) {
			Color bgcolor = SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
			l.setBackground(bgcolor);
			item.setBackground(bgcolor);
			btnView.setBackground(bgcolor);
		}
	}

	private void viewPublished(String item) {
		String itemdata = app.getLClient().getStorage().readStorage(u, item);
		StringTokenizer st = new StringTokenizer(item, "/");
		st.nextToken(); // "published"
		String type = st.nextToken();
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
