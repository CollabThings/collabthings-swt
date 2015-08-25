package org.collabthings.swt.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import waazdoh.common.WData;
import waazdoh.common.vo.UserVO;

public class UserSmallView extends Composite {
	private LOTApp app;

	private Map<String, DataHandler> handlers = new HashMap<>();
	private Label ltype;

	private Composite items;

	private Set<String> ignorelist;

	public UserSmallView(Composite cc, LOTApp app, AppWindow window, UserVO user) {
		super(cc, SWT.NONE);
		this.app = app;

		initIgnoreList();

		this.setLayout(new GridLayout());

		Composite ctitle = new Composite(this, SWT.NONE);
		ctitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		ctitle.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		ctitle.setLayout(new GridLayout(2, false));

		Label lname = new Label(ctitle, SWT.NONE);
		lname.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1,
				1));
		lname.setText(user.getUsername());
		lname.setAlignment(SWT.CENTER);
		lname.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		lname.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));

		ltype = new Label(ctitle, SWT.NONE);
		ltype.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		ltype.setText("Type");

		Composite cvalues = new Composite(this, SWT.NONE);
		cvalues.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_LIST_BACKGROUND));
		cvalues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		cvalues.setLayout(new GridLayout(1, false));

		Composite ctools = new Composite(cvalues, SWT.NONE);
		ctools.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		ctools.setLayout(new GridLayout(2, false));

		Button bview = new Button(ctools, SWT.NONE);
		bview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				window.view(ltype.getText(), user);
			}
		});
		bview.setText("View");

		Button bcopyid = new Button(ctools, SWT.NONE);
		bcopyid.addSelectionListener(new CopyToClipbardSelectionAdapter(this,
				user.getUserid()));
		bcopyid.setText("ID");

		items = new Composite(cvalues, getStyle());
		GridLayout gl_items = new GridLayout(1, false);
		gl_items.marginWidth = 0;
		gl_items.marginHeight = 0;
		items.setLayout(gl_items);
		items.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 2));
		items.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_LIST_BACKGROUND));
	}

	private void initIgnoreList() {
		if (ignorelist == null) {
			ignorelist = new HashSet<String>();
			String s = app.getLClient().getPreferences()
					.get("smallview.ignorelist", "list");
			StringTokenizer st = new StringTokenizer(s, ",");
			while (st.hasMoreTokens()) {
				String t = st.nextToken();
				ignorelist.add(t);
			}
		}
	}

	void addDataHandler(String name, DataHandler d) {
		handlers.put(name, d);
	}

	private interface DataHandler {
		public void handle(WData d);
	}
}
