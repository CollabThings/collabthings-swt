package org.collabthings.swt.view;

import java.util.List;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import waazdoh.client.WClient;
import waazdoh.common.vo.ObjectVO;

public class SearchView extends CTComposite implements LOTAppControl {
	private static final int COLUMN_WIDTH = 500;
	private AppWindow window;
	private Text text;
	private LOTApp app;
	private LLog log = LLog.getLogger(this);
	private Composite clist;
	private ScrolledComposite scrolledComposite;
	private GridLayout clistlayout;

	/**
	 * @wbp.parser.constructor
	 */
	public SearchView(Composite c, LOTApp app, AppWindow appWindow) {
		this(c, app, appWindow, false);
	}

	public SearchView(Composite c, LOTApp app, AppWindow appWindow, boolean hidesearchbox) {
		super(c, SWT.BORDER);
		this.app = app;
		this.window = appWindow;
		setLayout(new GridLayout(1, false));

		if (!hidesearchbox) {
			Composite composite = new CTComposite(this, SWT.NONE);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			composite.setLayout(new GridLayout(2, false));

			text = CTControls.getText(composite, SWT.NONE);

			text.addTraverseListener(new TraverseListener() {

				@Override
				public void keyTraversed(TraverseEvent e) {
					if (e.detail == SWT.TRAVERSE_RETURN) {
						searchSelected();
					}
				}
			});
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

			CTButton bsearch = new CTButton(composite, SWT.NONE);
			bsearch.addSelectionListener(() -> {
				searchSelected();
			});

			bsearch.setText("Search");
		}

		scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		clist = new CTComposite(scrolledComposite, SWT.NONE);
		clistlayout = new GridLayout(1, false);
		clist.setLayout(clistlayout);

		// RowLayout clistlayout = new RowLayout(SWT.HORIZONTAL);
		// clistlayout.fill = true;
		// clist.setLayout(clistlayout);

		scrolledComposite.setContent(clist);
		scrolledComposite.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				updateLayout();
			}
		});
	}

	@Override
	public void selected(AppWindow appWindow) {

	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Search";
	}

	private void searchSelected() {
		String s = this.text.getText();
		search(s, 0, 50);
	}

	public void search(String searchitem, int start, int count) {
		new Thread(() -> {
			getDisplay().asyncExec(() -> {
				if (text != null) {
					text.setText(searchitem);
				}
			});

			WClient client = app.getLClient().getClient();
			List<String> list = client.getObjects().search(searchitem, start, count);
			log.info("search got list " + list);
			handleResponse(list);
		}).start();
	}

	private void handleResponse(List<String> list) {
		if (list != null) {
			getDisplay().asyncExec(() -> {
				addRows(list);
				updateLayout();
			});
		}
	}

	private void addRows(List<String> list) {
		Control[] cs = clist.getChildren();
		for (Control control : cs) {
			control.dispose();
		}

		for (String id : list) {
			addRow(id);
		}

		updateLayout();
	}

	private void addRow(String id) {
		try {
			new ObjectSmallView(clist, this.app, this.window, id);
		} catch (ClassCastException e) {
			log.error(this, "addRow " + id, e);
			ObjectVO o = this.app.getLClient().getService().getObjects().read(id);
			log.info("failed object " + o.toObject());
		}
	}

	@Override
	public MenuItem createMenu(Menu menu) {
		MenuItem miscripts = new MenuItem(menu, SWT.CASCADE);
		miscripts.setText("Search");

		Menu mscript = new Menu(miscripts);
		miscripts.setMenu(mscript);

		return miscripts;
	}

	private void updateLayout() {
		if (scrolledComposite != null) {
			int w = scrolledComposite.getClientArea().width;
			int count = w / COLUMN_WIDTH;
			if (count < 1) {
				count = 1;
			}

			clistlayout.numColumns = count;
			log.info("columncount " + clistlayout.numColumns + " w:" + w);

			for (Control c : clist.getChildren()) {
				c.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			}

			scrolledComposite.setMinSize(w, clist.computeSize(w, SWT.DEFAULT).y);
		}
	}

}
