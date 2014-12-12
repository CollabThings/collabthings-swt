package org.libraryofthings.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.swt.app.LOTApp;
import org.libraryofthings.swt.dialog.LOTMessageDialog;
import org.libraryofthings.swt.view.FactoryView;
import org.libraryofthings.swt.view.PartView;
import org.libraryofthings.swt.view.ScriptView;
import org.libraryofthings.swt.view.SearchView;

import waazdoh.client.model.WaazdohInfo;

public final class AppWindow {
	protected Shell shell;
	//
	private LOTApp app;
	private CTabFolder tabFolder;
	private Label lblBottonInfo;

	private MenuItem objectmenu;
	private LOTAppControl currentappcontrol;
	private Menu menu;

	public AppWindow(LOTApp app) {
		this.app = app;
	}

	public void newPart() {
		try {
			LOTPart p = app.newPart();
			PartView view = new PartView(app, p, tabFolder);
			addTab("part " + p, view, p);
		} catch (Exception e) {
			showError(e);
		}
	}

	public void newFactory() {
		LOTFactory f = app.newFactory();
		viewFactory(f);
	}

	public void viewFactory(LOTFactory f) {
		FactoryView v = new FactoryView(tabFolder, app, this, f);
		addTab("" + f, v, f);
	}

	public void viewSearch(String searchitem) {
		SearchView s = new SearchView(tabFolder, app, this);
		addTab("" + searchitem, s, searchitem);
		s.search(searchitem);
	}

	public void viewScript(LOTScript script) {
		ScriptView v = new ScriptView(tabFolder, app, this, script);
		addTab("" + script, v, script);
	}

	private void addTab(String name, Composite c, Object data) {
		CTabItem i = new CTabItem(tabFolder, SWT.CLOSE);
		i.setText(name);
		i.setControl(c);
		i.setData(data);
		tabFolder.setSelection(i);
		tabSelected();
	}

	/**
	 * Open the window.
	 */
	public void open() {
		try {
			try {
				Display display = Display.getDefault();
				createContents();
				//
				shell.open();
				shell.layout();
				shell.setMaximized(true);
				//
				// FIXME TODO REMOVE
				newFactory();
				viewSearch("test");
				//
				while (!shell.isDisposed()) {
					readAndDispatch(display);
				}
			} catch (Exception e) {
				// TODO shouldn't catch Exception, but didn't come up with
				// anything better.
				showError(e);
			}
		} finally {
			if (shell != null) {
				shell.dispose();
			}
			app.close();
		}
	}

	private void readAndDispatch(Display display) {
		try {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		} catch (Exception e) {
			showError(e);
		}
	}

	public void showError(Exception e) {
		LLog.getLogger(this).error(this, null, e);
		LOTMessageDialog d = new LOTMessageDialog(shell);
		d.show(e);
	}

	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(589, 395);
		shell.setText("SWT Application");
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.verticalSpacing = 1;
		gl_shell.horizontalSpacing = 1;
		gl_shell.marginHeight = 1;
		gl_shell.marginWidth = 1;
		shell.setLayout(gl_shell);

		menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem mntmNew = new MenuItem(menu, SWT.CASCADE);
		mntmNew.setText("New");

		Menu menu_new = new Menu(mntmNew);
		mntmNew.setMenu(menu_new);

		MenuItem mntmNewFactory = new MenuItem(menu_new, SWT.NONE);
		mntmNewFactory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newFactory();
			}
		});
		mntmNewFactory.setText("Factory");

		MenuItem mntmNewPart = new MenuItem(menu_new, SWT.NONE);
		mntmNewPart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newPart();
			}
		});
		mntmNewPart.setText("Part");

		MenuItem mntmRun = new MenuItem(menu, SWT.NONE);
		mntmRun.setText("Run");

		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");

		Menu menu_1 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_1);

		MenuItem mntmNewSubmenu_1 = new MenuItem(menu_1, SWT.CASCADE);
		mntmNewSubmenu_1.setText("New SubMenu");

		Menu menu_2 = new Menu(mntmNewSubmenu_1);
		mntmNewSubmenu_1.setMenu(menu_2);

		MenuItem mntmNewSubmenu_3 = new MenuItem(menu_1, SWT.CASCADE);
		mntmNewSubmenu_3.setText("New SubMenu");

		Menu menu_4 = new Menu(mntmNewSubmenu_3);
		mntmNewSubmenu_3.setMenu(menu_4);

		MenuItem mntmNewSubmenu_2 = new MenuItem(menu_1, SWT.CASCADE);
		mntmNewSubmenu_2.setText("New SubMenu");

		Menu menu_3 = new Menu(mntmNewSubmenu_2);
		mntmNewSubmenu_2.setMenu(menu_3);

		MenuItem mntmNewItem_2 = new MenuItem(menu_3, SWT.NONE);
		mntmNewItem_2.setText("New Item");

		MenuItem mntmNewItem_3 = new MenuItem(menu_3, SWT.NONE);
		mntmNewItem_3.setText("New Item");

		Composite composite = new Composite(shell, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.horizontalSpacing = 0;
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		gl_composite.verticalSpacing = 0;
		composite.setLayout(gl_composite);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1);
		gd_composite.widthHint = 216;
		composite.setLayoutData(gd_composite);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_MAGENTA));

		tabFolder = new CTabFolder(composite, SWT.NONE);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				tabSelected();
			}
		});
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		lblBottonInfo = new Label(composite, SWT.NONE);
		lblBottonInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		lblBottonInfo.setAlignment(SWT.RIGHT);

		setBottomInfo();
	}

	protected void tabSelected() {
		if (lblBottonInfo != null) {
			Control control = tabFolder.getTabList()[tabFolder
					.getSelectionIndex()];
			if (control instanceof LOTAppControl) {
				LOTAppControl v = (LOTAppControl) control;
				v.selected(this);
				updateObjectMenu(v);
				this.currentappcontrol = v;
			}
		}
	}

	public void updateObjectMenu(LOTAppControl v) {
		if (objectmenu != null) {
			objectmenu.dispose();
		}
		objectmenu = v.createMenu(menu);
	}

	private void setBottomInfo() {
		shell.getDisplay().timerExec(1000, new Runnable() {

			@Override
			public void run() {
				lblBottonInfo.setText("LOT:" + LOTClient.VERSION + " Waazdoh:"
						+ WaazdohInfo.version + " environment: "
						+ app.getLClient());
				//
				setBottomInfo();
			}
		});
	}
}
