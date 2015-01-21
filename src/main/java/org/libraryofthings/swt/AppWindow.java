package org.libraryofthings.swt;

import java.util.HashMap;
import java.util.Map;

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
import org.libraryofthings.model.impl.LOTFactoryImpl;
import org.libraryofthings.swt.app.LOTApp;
import org.libraryofthings.swt.dialog.LOTMessageDialog;
import org.libraryofthings.swt.view.FactoryView;
import org.libraryofthings.swt.view.PartView;
import org.libraryofthings.swt.view.ScriptView;
import org.libraryofthings.swt.view.SearchView;

import waazdoh.client.model.User;
import waazdoh.client.model.UserID;
import waazdoh.client.model.WData;
import waazdoh.client.model.WaazdohInfo;
import waazdoh.util.MStringID;

public final class AppWindow {
	protected Shell shell;
	//
	private LOTApp app;
	private CTabFolder tabFolder;
	private Label lblBottonInfo;

	private MenuItem objectmenu;
	private Menu menu;

	private LLog log = LLog.getLogger(this);
	private Menu menulocal;

	public AppWindow(LOTApp app) {
		this.app = app;
	}

	public void newPart() {
		try {
			LOTPart p = app.newPart();
			PartView view = new PartView(tabFolder, app, p);
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

	public void showError(String message) {
		LLog.getLogger(this).info("ERROR " + message);
		LOTMessageDialog d = new LOTMessageDialog(shell);
		d.show("Error", message);
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

		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");

		Menu menufile = new Menu(mntmFile);
		mntmFile.setMenu(menufile);

		MenuItem mntmNew = new MenuItem(menufile, SWT.CASCADE);
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

		MenuItem menulocalitem = new MenuItem(menufile, SWT.CASCADE);
		menulocalitem.setText("Local");

		menulocal = new Menu(menulocalitem);
		new LocalObjectsMenu(this, menulocal);
		menulocalitem.setMenu(menulocal);

		MenuItem menulocaldate = new MenuItem(menulocal, SWT.NONE);
		menulocaldate.setText("Date");

		MenuItem mntmSearch = new MenuItem(menu, SWT.CASCADE);
		mntmSearch.setText("Search");
		mntmSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				viewSearch("search");
			}
		});

		MenuItem mntmRun = new MenuItem(menu, SWT.NONE);
		mntmRun.setText("Run");

		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");

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
			disposeObjectMenu();

			// Really like this? //TODO
			int selectionIndex = tabFolder.getSelectionIndex() + 1;
			Control control = tabFolder.getTabList()[selectionIndex];

			if (control instanceof LOTAppControl) {
				LOTAppControl v = (LOTAppControl) control;
				log.info("selected " + v);

				v.selected(this);
				updateObjectMenu(v);
			} else {
				showError("Selected " + control
						+ " that is not a LOTAppControl. Index "
						+ selectionIndex + " Name:"
						+ tabFolder.getSelection().getText());
			}
		}
	}

	public void updateObjectMenu(LOTAppControl v) {
		disposeObjectMenu();
		objectmenu = v.createMenu(menu);
	}

	private void disposeObjectMenu() {
		if (objectmenu != null) {
			objectmenu.dispose();
			objectmenu = null;
		}
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

	public LOTApp getApp() {
		return this.app;
	}

}
