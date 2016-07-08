package org.collabthings.swt.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import waazdoh.common.WData;
import waazdoh.common.vo.UserVO;

public class UserSmallView extends CTComposite {
	private LOTApp app;

	private Map<String, DataHandler> handlers = new HashMap<>();
	private CTLabel ltype;

	private Composite items;

	private Set<String> ignorelist;

	public UserSmallView(Composite cc, LOTApp app, AppWindow window, UserVO user) {
		super(cc, SWT.BORDER);
		this.app = app;

		initIgnoreList();

		this.setLayout(new GridLayout());

		Composite ctitle = new CTComposite(this, SWT.NONE);
		ctitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		ctitle.setLayout(new GridLayout(2, false));

		CTLabel lname = new CTLabel(ctitle, SWT.NONE);
		lname.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lname.setText(user.getUsername());
		lname.setAlignment(SWT.CENTER);

		ltype = new CTLabel(ctitle, SWT.NONE);
		ltype.setText("Type");

		Composite cvalues = new CTComposite(this, SWT.NONE);
		cvalues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		cvalues.setLayout(new GridLayout(1, false));

		Composite ctools = new CTComposite(cvalues, SWT.BORDER);
		ctools.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		GridLayout gl_ctools = new GridLayout(2, false);
		ctools.setLayout(gl_ctools);

		CTButton bview = new CTButton(ctools, SWT.FLAT);
		bview.addSelectionListener(() -> {
			window.view(ltype.getText(), user);
		});
		bview.setText("View");

		CTButton bcopyid = new CTButton(ctools, SWT.NONE);
		bcopyid.addSelectionListener(() -> {
			new CopyToClipbard(this, user.getUserid());
		});
		bcopyid.setText("ID");

		items = new CTComposite(cvalues, getStyle());
		GridLayout gl_items = new GridLayout(1, false);
		gl_items.marginWidth = 0;
		gl_items.marginHeight = 0;
		items.setLayout(gl_items);
		items.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 2));
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
