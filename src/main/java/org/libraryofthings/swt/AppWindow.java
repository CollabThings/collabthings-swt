package org.libraryofthings.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.swt.app.LOTApp;
import org.libraryofthings.swt.dialog.LOTMessageDialog;
import org.libraryofthings.swt.view.FactoryView;
import org.libraryofthings.swt.view.PartView;

import waazdoh.client.model.WaazdohInfo;

public final class AppWindow {
	protected Shell shell;
	//
	private LOTApp app;
	private TabFolder tabFolder;
	private Table table;
	private Label lblBottonInfo;

	public AppWindow(LOTApp app) {
		this.app = app;
	}

	public void newPart() {
		try {
			LOTPart p = app.newPart();
			PartView view = new PartView(app, p, tabFolder);
			addTab("part " + p, view);
		} catch (Exception e) {
			showError(e);
		}
	}

	public void newFactory() {
		LOTFactory f = app.newFactory();
		viewFactory(f);
	}

	public void viewFactory(LOTFactory f) {
		FactoryView v = new FactoryView(app, this, f, tabFolder);
		addTab("" + f, v);
	}

	private void addTab(String name, Composite c) {
		TabItem i = new TabItem(tabFolder, SWT.None);
		i.setText(name);
		i.setControl(c);
		tabFolder.setSelection(i);
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		//
		shell.open();
		shell.layout();
		shell.setMaximized(true);
		//
		// FIXME TODO REMOVE
		newFactory();
		//
		while (!shell.isDisposed()) {
			readAndDispatch(display);
		}

		app.close();
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

	private void showError(Exception e) {
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

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem mntmNew = new MenuItem(menu, SWT.CASCADE);
		mntmNew.setText("New");

		Menu menu_new = new Menu(mntmNew);
		mntmNew.setMenu(menu_new);

		MenuItem mntmNewPart = new MenuItem(menu_new, SWT.NONE);
		mntmNewPart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newPart();
			}
		});
		mntmNewPart.setText("Part");

		MenuItem mntmNewFactory = new MenuItem(menu_new, SWT.NONE);
		mntmNewFactory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newFactory();
			}
		});
		mntmNewFactory.setText("Factory");

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

		tabFolder = new TabFolder(composite, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		TabItem tbtmInfo = new TabItem(tabFolder, SWT.NONE);
		tbtmInfo.setText("Search");

		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmInfo.setControl(composite_1);
		composite_1.setLayout(new GridLayout(1, false));

		Composite composite_2 = new Composite(composite_1, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1));
		composite_2.setLayout(new RowLayout(SWT.HORIZONTAL));

		Text text = new Text(composite_2, SWT.BORDER);
		text.setLayoutData(new RowData(167, SWT.DEFAULT));

		Button buttonSearch = new Button(composite_2, SWT.NONE);
		buttonSearch.setText("Search");

		ScrolledComposite scrolledComposite = new ScrolledComposite(
				composite_1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		table = new Table(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		scrolledComposite.setContent(table);
		scrolledComposite.setMinSize(table
				.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		lblBottonInfo = new Label(composite, SWT.NONE);
		lblBottonInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		lblBottonInfo.setAlignment(SWT.RIGHT);

		setBottomInfo();
	}

	private void setBottomInfo() {
		shell.getDisplay().timerExec(1000, new Runnable() {

			@Override
			public void run() {
				lblBottonInfo.setText("LOT:" + LOTClient.VERSION + " Waazdoh:"
						+ WaazdohInfo.version + " environment: "
						+ app.getEnvironment());
				//
				setBottomInfo();
			}
		});
	}

}
