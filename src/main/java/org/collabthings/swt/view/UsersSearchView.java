package org.collabthings.swt.view;

import java.util.List;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.util.LLog;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import waazdoh.client.WClient;
import waazdoh.common.vo.UserVO;

public class UsersSearchView extends Composite implements LOTAppControl {
	private AppWindow window;
	private Text text;
	private LOTApp app;
	private LLog log = LLog.getLogger(this);
	private Composite clist;
	private ScrolledComposite scrolledComposite;

	/**
	 * @wbp.parser.constructor
	 */
	public UsersSearchView(Composite c, LOTApp app, AppWindow appWindow) {
		this(c, app, appWindow, false);
	}

	public UsersSearchView(Composite c, LOTApp app, AppWindow appWindow,
			boolean hidesearchbox) {
		super(c, SWT.NONE);
		this.app = app;
		this.window = appWindow;
		setLayout(new GridLayout(1, false));

		if (!hidesearchbox) {
			Composite composite = new Composite(this, SWT.NONE);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false, 1, 1));
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
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
					1));

			Button bsearch = new Button(composite, SWT.NONE);
			bsearch.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					searchSelected();
				}
			});

			bsearch.setText("Search");
		}

		scrolledComposite = new ScrolledComposite(this, SWT.BORDER
				| SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		clist = new Composite(scrolledComposite, SWT.NONE);

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
		search(s);
	}

	public void search(String searchitem) {
		new Thread(() -> {
			getDisplay().asyncExec(() -> {
				text.setText(searchitem);
			});

			WClient client = app.getLClient().getClient();
			List<UserVO> list = client.searchUsers(searchitem, 50);
			log.info("search got list " + list);
			handleResponse(list);
		}).start();
	}

	private void handleResponse(List<UserVO> list) {
		if (list != null) {
			getDisplay().asyncExec(() -> {
				addRows(list);
				updateLayout();
			});
		}
	}

	private void addRows(List<UserVO> list) {
		Control[] cs = clist.getChildren();
		for (Control control : cs) {
			control.dispose();
		}

		for (UserVO id : list) {
			addRow(id);
		}

		updateLayout();
	}

	private void addRow(UserVO user) {
		clist.setLayout(new RowLayout(SWT.HORIZONTAL));
		new UserSmallView(clist, this.app, this.window, user);
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
			clist.pack();

			int w = scrolledComposite.getClientArea().width;
			scrolledComposite
					.setMinSize(w, clist.computeSize(w, SWT.DEFAULT).y);
		}
	}

}
