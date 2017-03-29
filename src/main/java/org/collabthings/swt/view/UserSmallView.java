package org.collabthings.swt.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.collabthings.app.CTApp;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import waazdoh.common.WData;
import waazdoh.common.vo.UserVO;

public class UserSmallView extends CTComposite {
	private CTApp app;
	private AppWindow window;
	private UserVO user;

	private Map<String, DataHandler> handlers = new HashMap<>();

	private Set<String> ignorelist;

	public UserSmallView(Composite cc, CTApp app, AppWindow window, UserVO user) {
		super(cc, SWT.NONE);
		this.app = app;
		this.window = window;
		this.user = user;

		initIgnoreList();

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		this.setLayout(gridLayout);

		Label label = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));

		Composite composite = new CTComposite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(1, false));

		addTitleLine(user, composite);

		addValues(composite);

	}

	private void addItems(Composite cvalues) {
		CTComposite items = new CTComposite(cvalues, SWT.NONE);
		GridLayout gl_items = new GridLayout(1, false);
		gl_items.marginWidth = 0;
		gl_items.marginHeight = 0;
		items.setLayout(gl_items);
		items.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 2));
	}

	private void addValues(Composite composite) {
		Composite cvalues = new CTComposite(composite, SWT.NONE);
		cvalues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		cvalues.setLayout(new GridLayout(1, false));

		addItems(cvalues);
		addTools(cvalues);
	}

	private void addTools(Composite cvalues) {
		Composite ctools = new CTComposite(cvalues, SWT.NONE);
		ctools.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		GridLayout gl_ctools = new GridLayout(2, false);
		ctools.setLayout(gl_ctools);

		CTButton bview = new CTButton(ctools, SWT.FLAT);

		CTButton bcopyid = new CTButton(ctools, SWT.NONE);
		bview.addSelectionListener(() -> {
			window.view("user", user);
		});
		bview.setText("View");
		bcopyid.addSelectionListener(() -> {
			new CopyToClipbard(this, user.getUserid());
		});
		bcopyid.setText("ID");
	}

	private void addTitleLine(UserVO user, Composite composite) {
		Composite ctitle = new CTComposite(composite, SWT.NONE);
		ctitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		ctitle.setLayout(new GridLayout(2, false));

		CTLabel lname = new CTLabel(ctitle, SWT.NONE);
		lname.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lname.setText(user.getUsername());
		lname.setAlignment(SWT.CENTER);
		lname.setTitleFont();
	}

	private void initIgnoreList() {
		if (ignorelist == null) {
			ignorelist = new HashSet<String>();
			String s = app.getLClient().getPreferences().get("smallview.ignorelist", "list");
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
