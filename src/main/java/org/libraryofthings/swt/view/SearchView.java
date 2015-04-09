package org.libraryofthings.swt.view;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.libraryofthings.LLog;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.LOTAppControl;
import org.libraryofthings.swt.app.LOTApp;

import waazdoh.client.WClient;
import waazdoh.common.MStringID;
import org.eclipse.swt.widgets.Label;

public class SearchView extends Composite implements LOTAppControl {
	private AppWindow window;
	private Text text;
	private LOTApp app;
	private LLog log = LLog.getLogger(this);
	private Composite clist;
	private ScrolledComposite scrolledComposite;

	/**
	 * @wbp.parser.constructor
	 */
	public SearchView(Composite c, LOTApp app, AppWindow appWindow) {
		this(c, app, appWindow, false);
	}

	public SearchView(Composite c, LOTApp app, AppWindow appWindow, boolean hidesearchbox) {
		super(c, SWT.NONE);
		this.app = app;
		this.window = appWindow;
		setLayout(new GridLayout(1, false));

		// if (!hidesearchbox) {
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(2, false));

		text = new Text(composite, SWT.BORDER);
		text.addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN) {
					searchSelected();
				}
			}
		});
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Button bsearch = new Button(composite, SWT.NONE);
		bsearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				searchSelected();
			}
		});
		bsearch.setBounds(0, 0, 75, 25);
		bsearch.setText("Search");

		scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		clist = new Composite(scrolledComposite, SWT.NONE);

		scrolledComposite.setContent(clist);
		scrolledComposite.setMinSize(clist.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		//
		addRow(new MStringID().toString());
		addRow(new MStringID().toString());
	}

	@Override
	public void selected(AppWindow appWindow) {

	}

	private void searchSelected() {
		String s = this.text.getText();
		search(s);
	}

	public void search(String searchitem) {
		new Thread(() -> {
			WClient client = app.getLClient().getClient();
			List<String> list = client.getObjects().search(searchitem, 0, 1000);
			log.info("search got list " + list);
			handleResponse(list);
		}).start();
	}

	private void handleResponse(List<String> list) {
		if (list != null) {
			getDisplay().asyncExec(() -> {
				addRows(list);
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
		RowLayout rl_clist = new RowLayout(SWT.VERTICAL);
		clist.setLayout(rl_clist);
		ObjectSmallView view = new ObjectSmallView(clist, this.app, this.window, id);
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
			scrolledComposite.layout(true, true);
			scrolledComposite.setMinSize(clist.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}
	}

}
