package org.collabthings.swt;

import java.util.LinkedList;
import java.util.List;

import org.collabthings.CTClient;
import org.collabthings.CTStorage;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTInfo;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.model.CTScript;
import org.collabthings.model.CTTool;
import org.collabthings.model.impl.CTFactoryImpl;
import org.collabthings.model.run.CTRunEnvironmentBuilder;
import org.collabthings.model.run.impl.CTRunEnvironmentBuilderImpl;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.LocalObjectsMenu;
import org.collabthings.swt.dialog.LOTMessageDialog;
import org.collabthings.swt.view.FactoryView;
import org.collabthings.swt.view.PartBuilderView;
import org.collabthings.swt.view.RunEnvironmentBuildRunView;
import org.collabthings.swt.view.RunEnvironmentBuilderView;
import org.collabthings.swt.view.SCADView;
import org.collabthings.swt.view.ScriptView;
import org.collabthings.swt.view.SearchView;
import org.collabthings.swt.view.UserView;
import org.collabthings.swt.view.UsersSearchView;
import org.collabthings.swt.view.ValueEditorDialog;
import org.collabthings.swt.view.parteditor.PartEditor;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import waazdoh.common.MStringID;
import waazdoh.common.WaazdohInfo;
import waazdoh.common.vo.ObjectVO;
import waazdoh.common.vo.UserVO;

public final class AppWindow implements CTInfo {
	protected Shell shell;
	//
	private LOTApp app;

	private List<LOTAppControl> controls = new LinkedList<>();

	private CTabFolder tabFolder;
	private Label lblBottonInfo;

	private MenuItem objectmenu;
	private Menu menu;

	private LLog log = LLog.getLogger(this);
	private Menu menulocal;

	private final ViewTypes viewtypes;
	private LOTAppControl selectedcontrol;
	private Label lblStatus;
	private ProgressBar progressBar;
	private Menu mbookmarkslist;

	public AppWindow(LOTApp app) {
		this.app = app;
		this.viewtypes = new ViewTypes(this, app);
	}

	public void newPart() {
		try {
			CTPart p = app.newPart();
			PartEditor view = new PartEditor(tabFolder, app, p);
			addTab("part " + p, view, p);
		} catch (Exception e) {
			showError(e);
		}
	}

	public void newFactory() {
		CTFactory f = app.newFactory();
		viewFactory(f);
	}

	public void newRunEnvBuilder() {
		CTRunEnvironmentBuilder b = new CTRunEnvironmentBuilderImpl(this.app.getLClient());
		viewRunEnvironmentBuilder(b);
	}

	public void view(String id) {
		setInfo(0, 0, 0, "View id:" + id);

		new Thread(() -> {
			ObjectVO o = app.getLClient().getClient().getObjects().read(id);
			if (o != null) {
				String type = o.getObject().getType();
				viewtypes.view(type, new MStringID(id));
			}
		}).start();

	}

	public void view(String type, String id) {
		setInfo(0, 0, 0, "View " + type + " id:" + id);

		new Thread(() -> {
			viewtypes.view(type, new MStringID(id));
		}).start();
	}

	public void view(String text, UserVO user) {
		setInfo(0, 0, 0, "View User " + user.getUsername());

		shell.getDisplay().asyncExec(() -> {
			UserView v = new UserView(tabFolder, app, this, user.getUserid());
			addTab("" + user, v, user);
		});
	}

	public void viewRunEnvironmentBuilder(CTRunEnvironmentBuilder b) {
		setInfo(0, 0, 0, "Viewing Builder" + b.toString());
		shell.getDisplay().asyncExec(() -> {
			RunEnvironmentBuilderView v = new RunEnvironmentBuilderView(tabFolder, app, this, b);
			addTab("" + b, v, b);
		});
	}

	public void viewSimulation(CTRunEnvironmentBuilder builder) {
		RunEnvironmentBuildRunView v = new RunEnvironmentBuildRunView(tabFolder, app, this, builder);
		addTab("" + builder, v, builder);
	}

	public void viewFactory(CTFactory f) {
		setInfo(0, 0, 0, "Viewing factory " + f.toString());

		shell.getDisplay().asyncExec(() -> {
			FactoryView v = new FactoryView(tabFolder, app, this, f);
			addTab("" + f, v, f);
		});
	}

	public void viewPartBuilder(CTPartBuilder pb) {
		setInfo(0, 0, 0, "Viewing partbuilder " + pb);
		shell.getDisplay().asyncExec(() -> {
			PartBuilderView v = new PartBuilderView(tabFolder, app, this, pb);
			addTab("" + pb, v, pb);
		});
	}

	private void setInfo(int current, int min, int max, String string) {
		shell.getDisplay().asyncExec(() -> {
			progressBar.setMaximum(max);
			progressBar.setMinimum(min);
			progressBar.setSelection(current);

			lblStatus.setText("" + string);
		});
	}

	public void viewSearchUsers(String seachitem) {
		UsersSearchView v = new UsersSearchView(tabFolder, app, this);
		addTab("users", v, seachitem);
		v.search(seachitem);
	}

	public void viewSearch(String searchitem) {
		SearchView s = new SearchView(tabFolder, app, this);
		addTab("" + searchitem, s, searchitem);
		s.search(searchitem, 0, 50);
	}

	public void viewScript(CTScript script) {
		shell.getDisplay().asyncExec(() -> {
			ScriptView v = new ScriptView(tabFolder, app, this, script);
			addTab("" + script, v, script);
		});
	}

	public void viewUser(String name, String userid) {
		UserView v = new UserView(tabFolder, app, this, userid);
		addTab("" + name, v, userid);
	}

	public void viewOpenSCAD(CTOpenSCAD scad) {
		shell.getDisplay().asyncExec(() -> {
			SCADView v = new SCADView(tabFolder, app, this, scad);
			addTab("" + scad.getName(), v, scad);
		});
	}

	public void viewPart(CTPart part) {
		shell.getDisplay().asyncExec(() -> {
			PartEditor pv = new PartEditor(tabFolder, app, part);
			addTab("" + part.getName(), pv, part);
		});
	}

	private void addTab(String name, LOTAppControl c, Object data) {
		CTabItem i = new CTabItem(tabFolder, SWT.CLOSE);
		i.setText(name);
		i.setControl(c.getControl());
		i.setData(data);
		tabFolder.setSelection(i);
		tabSelected();

		controls.add(c);
		i.addDisposeListener(e -> {
			controls.remove(c);
			c.getControl().dispose();
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		try {
			try {
				Thread.currentThread().setName("App open");

				Display display = Display.getDefault();
				createContents();
				//
				shell.open();
				shell.layout();
				shell.setMaximized(true);
				openTestViews(display);

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

	private void openTestViews(Display display) {
		display.asyncExec(() -> {
			viewSearch("multiplegears");
		});

		display.asyncExec(() -> {
			viewSearchUsers("user");
			// newFactory();

			String latest = app.getLClient().getService().getStorageArea().read("juusoface",
					"DERP_published/factory/latest");
			if (latest != null) {
				CTFactory f = app.getObjectFactory().getFactory(new MStringID(latest));
				viewFactory(f);
			} else {
				// newRunEnvBuilder();
			}

			/*
			 * String latestpartbuilder = app.getLClient().getService()
			 * .getStorageArea() .read("juusoface",
			 * "published/partbuilder/latest"); if (latestpartbuilder != null) {
			 * LOTPartBuilder b = app.getObjectFactory().getPartBuilder( new
			 * MStringID(latestpartbuilder)); viewPartBuilder(b); } else { //
			 * newRunEnvBuilder(); }
			 */
			String latestscadpart = app.getLClient().getService().getStorageArea().read("juusoface",
					"published/part/latest");
			if (latestscadpart != null) {
				CTPart b = app.getObjectFactory().getPart(new MStringID(latestscadpart));
				viewPart(b);
			} else {
				newPart();
			}
		});

		display.asyncExec(() -> {
			CTStorage storage = app.getLClient().getStorage();
			storage.readStorage(app.getLClient().getClient().getService().getUser("juuso.vilmunen"),
					"/published/builder/latest");
		});
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
		this.shell.getDisplay().asyncExec(() -> {
			LLog.getLogger(this).info("ERROR " + message);
			LOTMessageDialog d = new LOTMessageDialog(shell);
			d.show("Error", message);
		});
	}

	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell();

		Image small = new Image(shell.getDisplay(), ClassLoader.getSystemResourceAsStream("logo.png"));
		shell.setImage(small);

		shell.setSize(589, 395);
		shell.setText("CollabThings - " + app.getLClient().getService().getUser().getUsername());
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

		MenuItem mntmRunenvBuilder = new MenuItem(menu_new, SWT.NONE);
		mntmRunenvBuilder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newRunEnvBuilder();
			}
		});
		mntmRunenvBuilder.setText("RunEnv Builder");

		MenuItem menulocalitem = new MenuItem(menufile, SWT.CASCADE);
		menulocalitem.setText("Local");

		menulocal = new Menu(menulocalitem);
		initLocalMenu();

		menulocalitem.setMenu(menulocal);

		MenuItem menulocaldate = new MenuItem(menulocal, SWT.NONE);
		menulocaldate.setText("Date");

		MenuItem misearch = new MenuItem(menu, SWT.CASCADE);
		misearch.setText("Search");

		Menu msearch = new Menu(misearch);
		misearch.setMenu(msearch);

		MenuItem misearchobjects = new MenuItem(msearch, SWT.CASCADE);
		misearchobjects.setText("Objects");

		MenuItem misearchusers = new MenuItem(msearch, SWT.NONE);
		misearchusers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				viewSearchUsers("");
			}
		});
		misearchusers.setText("Users");
		misearchobjects.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				viewSearch("search");
			}
		});

		MenuItem mntmRun = new MenuItem(menu, SWT.NONE);
		mntmRun.setText("Run");

		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");

		MenuItem mibookmarks = new MenuItem(menu, SWT.CASCADE);
		mibookmarks.setText("Bookmarks");

		Menu mbookmarks = new Menu(mibookmarks);
		mibookmarks.setMenu(mbookmarks);

		MenuItem miBookmarkCurrent = new MenuItem(mbookmarks, SWT.CASCADE);
		miBookmarkCurrent.setText("Current");

		new MenuItem(mbookmarks, SWT.SEPARATOR);

		mbookmarkslist = new Menu(mibookmarks);
		MenuItem miBookmarksList = new MenuItem(mbookmarks, SWT.CASCADE);
		miBookmarksList.setText("Bookmarks");
		miBookmarksList.setMenu(mbookmarkslist);

		this.app.addTask(() -> {
			this.shell.getDisplay().asyncExec(() -> {
				initBookmarks();
			});
		});

		Composite composite = new Composite(shell, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		LOTSWT.setDefaults(gl_composite);

		composite.setLayout(gl_composite);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.widthHint = 216;
		composite.setLayoutData(gd_composite);

		tabFolder = new CTabFolder(composite, SWT.NONE);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				tabSelected();
			}
		});
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_1.setLayout(new GridLayout(2, false));

		lblStatus = new Label(composite_1, SWT.NONE);
		lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblStatus.setText("status");

		progressBar = new ProgressBar(composite_1, SWT.NONE);

		lblBottonInfo = new Label(composite, SWT.NONE);
		lblBottonInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		lblBottonInfo.setAlignment(SWT.RIGHT);

		setBottomInfo();
	}

	private void initBookmarks() {
		Menu bookmarkmenu = mbookmarkslist;
		String path = "";
		initBookmarks(bookmarkmenu, path);
	}

	private void initBookmarks(Menu bookmarkmenu, String path) {
		List<String> bookmarklist = this.app.getLClient().getBookmarks().list(path);

		for (String bm : bookmarklist) {
			String value = this.app.getLClient().getBookmarks().get(path + "/" + bm);
			if (value != null) {
				MenuItem mi = new MenuItem(bookmarkmenu, SWT.CASCADE);
				mi.setText(bm);
				mi.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						view(value);
					}

				});
			} else {
				MenuItem mi = new MenuItem(bookmarkmenu, SWT.CASCADE);
				mi.setText(bm);
				Menu m = new Menu(mi);
				mi.setMenu(m);

				initBookmarks(m, path + "/" + bm);
			}
		}
	}

	private void initLocalMenu() {
		LocalObjectsMenu localmenu = new LocalObjectsMenu(this, menulocal);
		localmenu.addObjectHandler(CTFactoryImpl.BEANNAME, (data) -> {
			MStringID id = data.getIDValue("id");
			CTFactory f = getApp().getLClient().getObjectFactory().getFactory(id);
			if (f != null) {
				viewFactory(f);
			} else {
				showError("Failed to open factory " + id);
			}
		});
	}

	protected void tabSelected() {
		if (lblBottonInfo != null) {
			disposeObjectMenu();

			Control control = tabFolder.getSelection().getControl();

			if (control instanceof LOTAppControl) {
				LOTAppControl v = (LOTAppControl) control;
				selectedcontrol = v;
				log.info("selected " + v);

				v.selected(this);
				updateObjectMenu(v);
			} else {
				showError("Selected " + control + " that is not a LOTAppControl. Name:"
						+ tabFolder.getSelection().getText());
			}
		}
	}

	public void updateObjectMenu(LOTAppControl v) {
		log.info("updating object menu " + v);
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
				lblBottonInfo.setText("CT:" + CTClient.VERSION + " Waazdoh:" + WaazdohInfo.VERSION + " environment: "
						+ app.getLClient());
				//
				setBottomInfo();
			}
		});
	}

	public LOTApp getApp() {
		return this.app;
	}

	public List<LOTAppControl> getTablist() {
		return new LinkedList<LOTAppControl>(controls);
	}

	public boolean isSelected(LOTAppControl c) {
		return selectedcontrol == c;
	}

	public String openValueEditorDialog(String name, String value) {
		ValueEditorDialog e = new ValueEditorDialog(shell, name, value);
		e.open();
		return e.getValue();
	}

	public void viewTool(CTTool tool) {
		// TODO Auto-generated method stub
	}
}
