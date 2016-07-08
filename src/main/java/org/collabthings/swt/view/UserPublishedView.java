package org.collabthings.swt.view;

import java.util.List;
import java.util.StringTokenizer;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import waazdoh.common.WLogger;
import waazdoh.common.vo.UserVO;

public class UserPublishedView extends Composite {
	public final LOTApp app;
	public final AppWindow window;

	public UserVO u;

	private int publishedcount;
	private Text publishedfilter;
	private ScrolledComposite scrolledComposite;
	private Composite clist;

	public UserPublishedView(Composite parent, LOTApp app2, AppWindow window2) {
		super(parent, SWT.NONE);
		app = app2;
		window = window2;

		setLayout(new GridLayout(1, false));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		composite.setLayout(new GridLayout(6, false));

		Label lblPublished = new Label(composite, SWT.NONE);
		lblPublished.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblPublished.setText("Published");

		Label lblFilter = new Label(composite, SWT.NONE);
		lblFilter.setText("Filter");

		publishedfilter = new Text(composite, SWT.BORDER);
		publishedfilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnSearchPublished = new Button(composite, SWT.NONE);
		btnSearchPublished.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				initPublishedList();
			}
		});
		btnSearchPublished.setText("search");

		scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		clist = new Composite(scrolledComposite, SWT.NONE);
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

				List<String> published = app.getLClient().getStorage().getUserPublished(u.getUserid(), 0, 50);
				WLogger.getLogger(this).info("got published list " + published);
				String filter = "" + publishedfilter.getText();
				for (String string : published) {
					if (string.indexOf(filter) > 0 || filter.length() < 2) {
						addPublishedItem(string);
					}
				}

				updateLayout();
			});
		}
	}

	private void addPublishedItem(String string) {
		Composite item = new Composite(clist, SWT.NONE);
		item.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_item = new GridLayout(2, false);
		LOTSWT.setDefaults(gl_item);

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

	private void updateLayout() {
		if (scrolledComposite != null) {
			clist.pack();

			int w = scrolledComposite.getClientArea().width;
			scrolledComposite.setMinSize(w, clist.computeSize(w, SWT.DEFAULT).y);
		}
	}

}