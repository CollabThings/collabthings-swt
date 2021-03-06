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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.collabthings.app.CTApp;
import org.collabthings.datamodel.UserVO;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.app.CTRunner;
import org.collabthings.tk.CTButton;
import org.collabthings.tk.CTComposite;
import org.collabthings.tk.CTLabel;
import org.collabthings.tk.CTResourceManagerFactory;
import org.collabthings.tk.CTText;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class UserPublishedView extends CTComposite {
	public final CTApp app;
	public final AppWindow window;

	public UserVO u;

	private int publishedcount;
	private CTText publishedfilter;
	private ScrolledComposite scrolledComposite;
	private Composite clist;

	public UserPublishedView(Composite parent, CTApp app2, AppWindow window2) {
		super(parent, SWT.NONE);
		app = app2;
		window = window2;

		setLayout(new GridLayout(1, false));

		Composite composite = new CTComposite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		composite.setLayout(new GridLayout(6, false));

		CTLabel lblPublished = new CTLabel(composite, SWT.NONE);
		lblPublished.setText("Published");

		CTLabel lblFilter = new CTLabel(composite, SWT.NONE);
		lblFilter.setText("Filter");

		publishedfilter = new CTText(composite, SWT.BORDER);
		publishedfilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		CTButton btnSearchPublished = new CTButton(composite, SWT.NONE);
		btnSearchPublished.addSelectionListener(() -> {
			initPublishedList();
		});
		btnSearchPublished.setText("search");

		scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		clist = new CTComposite(scrolledComposite, SWT.NONE);
		clist.setLayout(new GridLayout(1, false));
		scrolledComposite.setContent(clist);

		if (window2 == null) {
			addPublishedItem("testitem1");
			addPublishedItem("testitem2");
		}
	}

	public void setUser(UserVO u) {
		this.u = u;
		initPublishedList();
	}

	void initPublishedList() {
		if (u != null) {
			getDisplay().asyncExec(() -> {
				for (Control c : clist.getChildren()) {
					c.dispose();
				}
				doSearch(publishedfilter.getText());
			});
		}
	}

	private void doSearch(String filter) {
		window.addRunner(new CTRunner<String>("dosearch " + filter)).run(() -> {
			List<String> published = app.getLClient().getStorage().getUserPublished(u.getUserid(), 0, 50);
			LLog.getLogger(this).info("got published list " + published);
			List<String> list = new ArrayList<>();
			published.stream().forEach(string -> {
				if (string.startsWith("/published") && (string.indexOf(filter) > 0 || filter.length() < 2)) {
					list.add(string);
				}
			});

			addPublishedItem(list);

		});
	}

	private void addPublishedItem(List<String> list) {
		getDisplay().asyncExec(() -> {
			list.forEach(item -> addPublishedItem(item));
			updateLayout();
		});
	}

	private void addPublishedItem(String string) {
		Composite item = new CTComposite(clist, SWT.NONE);
		item.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_item = new GridLayout(4, false);
		LOTSWT.setDefaults(gl_item);

		item.setLayout(gl_item);
		CTLabel l = new CTLabel(item, SWT.NONE);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setText(string.replace("/published/", ""));

		CTButton btnView = new CTButton(item, "View", () -> {
			viewPublished(string);
		});

		CTButton btncopy = new CTButton(item, "Copy", () -> {
			new CopyToClipbard(this, u.getUsername() + "/" + string);
		});

		CTButton btncopyid = new CTButton(item, "Id", () -> {
			new CopyToClipbard(this, readID(string));
		});

		if (publishedcount++ % 2 == 0) {
			Color bgcolor = CTResourceManagerFactory.instance().getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
			item.setBackground(bgcolor);
			btnView.setBackground(bgcolor);
			btncopy.setBackground(bgcolor);
			btncopyid.setBackground(bgcolor);
		}
	}

	private void viewPublished(String item) {
		String itemdata = readID(item);
		StringTokenizer st = new StringTokenizer(item, "/");
		st.nextToken(); // "published"
		String type = st.nextToken();
		window.view(type, itemdata);
	}

	private String readID(String item) {
		return app.getLClient().getStorage().readStorage(u, item);
	}

	private void updateLayout() {
		if (scrolledComposite != null) {
			clist.pack();

			int w = scrolledComposite.getClientArea().width;
			scrolledComposite.setMinSize(w, clist.computeSize(w, SWT.DEFAULT).y);
		}
	}

}