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
package org.collabthings.swt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.collabthings.CTEvent;
import org.collabthings.CTListener;
import org.collabthings.app.CTApp;
import org.collabthings.datamodel.ObjectVO;
import org.collabthings.datamodel.StorageAreaVO;
import org.collabthings.datamodel.UserVO;
import org.collabthings.datamodel.WStringID;
import org.collabthings.datamodel.WaazdohInfo;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTInfo;
import org.collabthings.model.CTObject;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.model.CTTool;
import org.collabthings.model.impl.CTConstants;
import org.collabthings.model.impl.CTFactoryImpl;
import org.collabthings.model.impl.CTPartImpl;
import org.collabthings.model.run.CTRunEnvironmentBuilder;
import org.collabthings.model.run.impl.CTRunEnvironmentBuilderImpl;
import org.collabthings.swt.app.CTRunner;
import org.collabthings.swt.app.CTRunners;
import org.collabthings.swt.controls.LocalObjectsMenu;
import org.collabthings.swt.dialog.FindOpenscadDialog;
import org.collabthings.swt.view.ApplicationView;
import org.collabthings.swt.view.CTMainView;
import org.collabthings.swt.view.FactoryView;
import org.collabthings.swt.view.ObjectSearchView;
import org.collabthings.swt.view.RunEnvironmentBuildRunView;
import org.collabthings.swt.view.RunEnvironmentBuilderView;
import org.collabthings.swt.view.UserView;
import org.collabthings.swt.view.UsersSearchView;
import org.collabthings.swt.view.ValueEditorDialog;
import org.collabthings.tk.CTComposite;
import org.collabthings.tk.CTLabel;
import org.collabthings.tk.CTResourceManagerFactory;
import org.collabthings.tk.CTSelectionAdapter;
import org.collabthings.tk.CTTabFolder;
import org.collabthings.tk.dialogs.CTErrorDialog;
import org.collabthings.tk.dialogs.CTTextDialog;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
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

public final class AppWindow implements CTInfo {
	public static final String STATUS_RUNNERS = "Runners";

	protected Shell shell;
	//
	private CTApp app;

	private List<CTAppControl> controls = new LinkedList<>();

	private CTTabFolder tabFolder;
	private CTLabel lblBottonInfo;

	private MenuItem objectmenu;
	private Menu menu;

	private LLog log = LLog.getLogger(this);
	private Menu menulocal;

	private final ViewTypes viewtypes;
	private CTAppControl selectedcontrol;
	private CTLabel lblStatus;
	private ProgressBar progressBar;
	private Menu mbookmarkslist;
	private CTMainView mainview;

	private CTRunners runners;
	private Label lblrunners;

	public AppWindow(CTApp app) {
		this.app = app;
		this.viewtypes = new ViewTypes(this, app);
		this.runners = new CTRunners(this, app);

		app.getLClient().addErrorListener((name, e) -> {
			showError(name, e);
		});
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
				String type = o.toObject().getType();
				viewtypes.view(type, new WStringID(id));
			}
		}).start();

	}

	public void view(String type, String id) {
		setInfo(0, 0, 0, "View " + type + " id:" + id);

		new Thread(() -> {
			viewtypes.view(type, new WStringID(id));
		}).start();
	}

	public void view(String text, UserVO user) {
		setInfo(0, 0, 0, "View User " + user.getUsername());

		shell.getDisplay().asyncExec(() -> {
			UserView v = new UserView(tabFolder.getComposite(), app, this, user.getUserid());
			addTab("" + user.getUsername(), v, user);
		});
	}

	public void viewRunEnvironmentBuilder(CTRunEnvironmentBuilder b) {
		setInfo(0, 0, 0, "Viewing Builder" + b.toString());
		shell.getDisplay().asyncExec(() -> {
			RunEnvironmentBuilderView v = new RunEnvironmentBuilderView(tabFolder.getComposite(), app, this, b);
			addTab("" + b, v, b);
		});
	}

	public void viewSimulation(CTRunEnvironmentBuilder builder) {
		RunEnvironmentBuildRunView v = new RunEnvironmentBuildRunView(tabFolder.getComposite(), app, this, builder);
		addTab("" + builder, v, builder);
	}

	public void viewFactory(CTFactory f) {
		setInfo(0, 0, 0, "Viewing factory " + f.toString());

		shell.getDisplay().asyncExec(() -> {
			FactoryView v = new FactoryView(tabFolder.getComposite(), app, this, f);
			addTab("" + f, v, f);
		});
	}

	public void viewPartBuilder(CTPart p, CTPartBuilder pb) {
		setInfo(0, 0, 0, "Viewing partbuilder " + pb);
		mainview.viewBuilder("" + p.getShortname() + "/" + pb.getName(), p, pb);
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
		shell.getDisplay().asyncExec(() -> {
			UsersSearchView v = new UsersSearchView(tabFolder.getComposite(), app, this);
			addTab("users", v.getView(), seachitem);
			v.search(seachitem, 0, 100);
		});
	}

	public void viewSearch(String searchitem) {
		ObjectSearchView s = new ObjectSearchView(tabFolder.getComposite(), app, this);
		addTab("Search " + searchitem, s.getControl(), searchitem);
		s.search(searchitem, 0, 50);
	}

	public void viewApplication(CTApplication application) {
		shell.getDisplay().asyncExec(() -> {
			ApplicationView v = new ApplicationView(tabFolder.getComposite(), app, this, application);
			addTab("" + application, v, application);
		});
	}

	public void viewUser(String name, String userid) {
		shell.getDisplay().asyncExec(() -> {
			UserView v = new UserView(tabFolder.getComposite(), app, this, userid);
			addTab("" + name, v, userid);
		});
	}

	private void addTab(String name, CTAppControl c, Object data) {
		Control control = c.getControl();
		control.setBackground(CTResourceManagerFactory.instance().getControlBg());

		tabFolder.addTab(name, control, data);

		tabSelected();

		controls.add(c);

		tabFolder.addCloseListener(name, () -> {
			controls.remove(c);
			control.dispose();
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
				showError("Open", e);
			}
		} finally {
			if (shell != null) {
				shell.dispose();
			}
			app.close();
		}
	}

	private void openTestViews(Display display) {
		// viewSearch("two");

		// viewSearchUsers("user");
		// newFactory();

		new Thread(() -> {
			UserVO user = app.getLClient().getService().getUser();
			viewUser(user.getUsername(), user.getUserid());

			// cities hopefully
			// 201801CT_eb500e3c-edb8-48c2-905f-2c9904a659cc_A6923f848b4a2bce1f020a90f5afacf90575987b1da564b142b6568ed3c587f8e_QmSEWzxrcNpQhydBnw4XXqHuBEcVG9sj9wDsrHDNXCb23Y
			// 201801CT_4b8ee11c-7adb-4323-9d76-2dd2abbdeb10_A21af7aefc11d725c2be9966e789234447f7a7a8d6b0278cdf6e5eaa586826eb8_QmdQjmUTwM8f1sAhVAnAie1NBL3wXVBH7fH22fgmVUf3DL
			// 201801CT_b9d3ebb9-bee0-4aa5-9e94-81db425ae036_4d35c3954995ddb54e2c5010b482ee717a9fdaeba371336757b33dc141f3641a_QmW8zj9xcBaeLmKK7DoJEeE2ZBewgSCZcQSSbb6pG7eWUJ
			// 201801CT_df31d189-2181-4461-b518-0e48b6635c50_A7546ddb0892d318634eeae01f38faa73793aeac7bb805697ad231d1b6bcec495_QmV4VAWvs72SK2wK37kLkJKJwthgX8br75EtTZs5zWKEdF
			// 201801CT_a8b11ebe-da2c-4968-97b3-75415cecd368_3ca4ed315ab565b3a923d90abaf7317d0999f53e02282c7e378fe687f8601b82_QmZohJExF2kCYEataDYK5Xk7fe22wQvdAN2zF6xwK5mTKg
			// 201801CT_adef987c-8436-4f27-8ce3-448b60821d95_A56a14b06edfbbf7d6dd100ae92102621b929d38d687d0fa25504d507fd0980bb_QmPuy1atH4uhH7KEmfqUZMRdM4kRf5V5AVjtG27L15kTbZ
			try {
				CTPart cities = app.getObjectFactory().getPart(new WStringID(
						"201801CT_adef987c-8436-4f27-8ce3-448b60821d95_A56a14b06edfbbf7d6dd100ae92102621b929d38d687d0fa25504d507fd0980bb_QmPuy1atH4uhH7KEmfqUZMRdM4kRf5V5AVjtG27L15kTbZ"));
				if (cities != null) {
					mainview.viewPart(cities);
				}
			} catch (Exception e) {
				log.error(this, "view cities", e);
			}

			String latestscadpart = app.getLClient().getService().getStorageArea()
					.read(new StorageAreaVO(user.getUsername(), "published/part/latest", null)).getData();
			if (latestscadpart != null) {
				CTPart b = app.getObjectFactory().getPart(new WStringID(latestscadpart));
				mainview.viewPart(b);
			} else {
				mainview.newPart();
			}
		}).start();
	}

	private void readAndDispatch(Display display) {
		try {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		} catch (Exception e) {
			showError("readAndDispatch", e);
		}
	}

	public void showError(String name, Exception e) {
		if (name.equals(CTConstants.ERROR_OPENSCADFAILED)) {
			new FindOpenscadDialog(app, this, shell);
		} else {
			LLog.getLogger(this).info("Error  " + name + " " + e);
			LLog.getLogger(this).error(this, name, e);
			if (!shell.isDisposed()) {
				CTErrorDialog d = new CTErrorDialog(shell);
				this.shell.getDisplay().asyncExec(() -> {
					d.open();
					d.show(name, e);
				});
			}
		}
	}

	public void showError(String message) {
		this.shell.getDisplay().asyncExec(() -> {
			LLog.getLogger(this).info("ERROR " + message);
			CTErrorDialog d = new CTErrorDialog(shell);
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
		shell.setBackground(CTResourceManagerFactory.instance().getControlBg());

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
		mntmNewFactory.addSelectionListener(new CTSelectionAdapter(e -> newFactory()));

		mntmNewFactory.setText("Factory");

		MenuItem mntmNewPart = new MenuItem(menu_new, SWT.NONE);
		mntmNewPart.addSelectionListener(new CTSelectionAdapter(e -> mainview.newPart()));

		mntmNewPart.setText("Part");

		MenuItem mntmRunenvBuilder = new MenuItem(menu_new, SWT.NONE);
		mntmRunenvBuilder.addSelectionListener(new CTSelectionAdapter(e -> newRunEnvBuilder()));

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
		misearchusers.addSelectionListener(new CTSelectionAdapter(e -> viewSearchUsers("")));
		misearchusers.setText("Users");
		misearchobjects.addSelectionListener(new CTSelectionAdapter(e -> viewSearch("search")));

		MenuItem mntmRun = new MenuItem(menu, SWT.NONE);
		mntmRun.setText("Run");

		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");

		MenuItem mibookmarks = new MenuItem(menu, SWT.CASCADE);
		mibookmarks.setText("Bookmarks");

		Menu mbookmarks = new Menu(mibookmarks);
		mibookmarks.setMenu(mbookmarks);

		MenuItem miupdate = new MenuItem(mbookmarks, SWT.NONE);
		miupdate.addSelectionListener(new CTSelectionAdapter(e -> initBookmarks()));

		miupdate.setText("Update");

		new MenuItem(mbookmarks, SWT.SEPARATOR);

		mbookmarkslist = new Menu(mibookmarks);
		MenuItem miBookmarksList = new MenuItem(mbookmarks, SWT.CASCADE);
		miBookmarksList.setText("Bookmarks");
		miBookmarksList.setMenu(mbookmarkslist);

		this.app.addTask(() -> {
			this.shell.getDisplay().asyncExec(() -> initBookmarks());
		});

		Composite composite = new CTComposite(shell, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.horizontalSpacing = 0;
		LOTSWT.setDefaults(gl_composite);

		composite.setLayout(gl_composite);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.widthHint = 216;
		composite.setLayoutData(gd_composite);

		tabFolder = new CTTabFolder(composite, SWT.FLAT);
		tabFolder.addSelectionListener(() -> {
			tabSelected();
		});

		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite_1 = new CTComposite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_1.setLayout(new GridLayout(3, false));
		composite_1.setBackground(CTResourceManagerFactory.instance().getActiontitleBackground());

		lblStatus = new CTLabel(composite_1, SWT.NONE);
		lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblStatus.setText("status");

		lblrunners = new Label(composite_1, SWT.NONE);
		lblrunners.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblrunners.setText("Runners status");

		progressBar = new ProgressBar(composite_1, SWT.NONE);
		progressBar.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		new Label(composite_1, SWT.NONE);

		lblBottonInfo = new CTLabel(composite, SWT.NONE);
		lblBottonInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lblBottonInfo.setAlignment(SWT.RIGHT);

		setBottomInfo();

		mainview = new CTMainView(tabFolder.getComposite(), app, this);
		addTab("main", mainview, null);
	}

	private void bookmarkCurrent(String path) {
		CTObject o = mainview.getCurrentObject();
		if (o != null) {
			this.app.getLClient().getBookmarks().add(path + "/" + o.getName(), o.getID().toString());
		}
	}

	private void initBookmarks() {
		Menu bookmarkmenu = mbookmarkslist;
		String path = "";
		initBookmarks(bookmarkmenu, path);
	}

	private void initBookmarks(Menu bookmarkmenu, String path) {
		for (MenuItem i : bookmarkmenu.getItems()) {
			i.dispose();
		}

		class BM {
			String path;
			String value;
		}

		addAddMenu(bookmarkmenu, path);

		List<BM> bookmarklist = new ArrayList<>();
		addRunner(new CTRunner<String>("updatebookmarkmenu " + path).run(() -> {
			this.app.getLClient().getBookmarks().list(path).forEach(s -> {
				BM bm = new BM();
				bm.path = s;
				bm.value = this.app.getLClient().getBookmarks().get(path + "/" + s);
				bookmarklist.add(bm);
			});
		}).gui(v -> {
			for (BM bm : bookmarklist) {
				log.info("bookmark " + path + " " + bm.path + "->" + bm.value);

				if (bm.value != null) {
					MenuItem mi = new MenuItem(bookmarkmenu, SWT.CASCADE);
					mi.setText(bm.path);
					mi.addSelectionListener(new CTSelectionAdapter(e -> view(bm.value)));
				} else if (!bm.path.startsWith("_")) {
					MenuItem mi = new MenuItem(bookmarkmenu, SWT.CASCADE);
					mi.setText(bm.path);
					Menu m = new Menu(mi);
					mi.setMenu(m);

					initBookmarks(m, path + "/" + bm.path);
				}

			}
		}));
	}

	private void addAddMenu(Menu m, String path) {
		MenuItem add = new MenuItem(m, SWT.CASCADE);
		add.setText("add");
		add.addSelectionListener(new CTSelectionAdapter(arg0 -> {
			CTTextDialog dialog = new CTTextDialog(shell);
			dialog.open("Bookmark folder name");
			String text = dialog.getValue();

			addRunner(new CTRunner<String>("addbookmarkfolder").run(() -> {
				app.getLClient().getBookmarks().addFolder(path + "/" + text);
			}).gui((v) -> {
				initBookmarks();
			}));
		}));

		MenuItem current = new MenuItem(m, SWT.CASCADE);
		current.setText("current");
		current.addSelectionListener(new CTSelectionAdapter(e -> bookmarkCurrent(path)));

		new MenuItem(m, SWT.SEPARATOR);

	}

	private void initLocalMenu() {
		LocalObjectsMenu localmenu = new LocalObjectsMenu(this, menulocal);
		localmenu.addObjectHandler(CTFactoryImpl.BEANNAME, (data) -> {
			WStringID id = data.getIDValue("id");
			CTFactory f = getApp().getLClient().getObjectFactory().getFactory(id);
			if (f != null) {
				viewFactory(f);
			} else {
				showError("Failed to open factory " + id);
			}
		});

		localmenu.addObjectHandler(CTPartImpl.BEANNAME, (data) -> {
			WStringID id = data.getIDValue("id");
			CTPart p = getApp().getLClient().getObjectFactory().getPart();
			p.parse(data);
			mainview.viewPart(p);
		});
	}

	protected void tabSelected() {
		if (lblBottonInfo != null) {
			disposeObjectMenu();

			Control control = tabFolder.getSelection().getControl();

			if (control instanceof CTAppControl) {
				CTAppControl v = (CTAppControl) control;
				this.selectedcontrol = v;
				log.info("selected " + v);

				v.selected(this);
				updateObjectMenu(v);
			} else {
				showError("Selected " + control + " that is not a LOTAppControl. Name:"
						+ tabFolder.getSelection().getText());
			}
		}
	}

	public void updateObjectMenu(CTAppControl v) {
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
		addRunner(new CTRunner<String>("BottomUpdateInfo", 1000).runWhile(() -> {
			return !lblBottonInfo.isDisposed();
		}).action(() -> {
			return " CT:" + CTConstants.VERSION + " Waazdoh:" + WaazdohInfo.VERSION + " environment: "
					+ app.getLClient() + " " + app.getLClient().getBinarySource().getStats();
		}).gui((o) -> lblBottonInfo.setText("" + o)));// addRunner(runner);
	}

	public CTApp getApp() {
		return this.app;
	}

	public List<CTAppControl> getTablist() {
		return new LinkedList<CTAppControl>(controls);
	}

	public boolean isSelected(CTAppControl c) {
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

	public CTMainView getMainView() {
		return mainview;
	}

	@SuppressWarnings("rawtypes")
	public CTRunner addRunner(CTRunner runner) {
		return runners.add(runner);
	}

	public void launch(CTListener l) {
		if (!shell.getDisplay().isDisposed()) {
			shell.getDisplay().asyncExec(() -> l.event(new CTEvent("launch")));
		}
	}

	public void setStatus(String string, String status) {
		if (shell != null && !shell.isDisposed() && !shell.getDisplay().isDisposed()) {
			shell.getDisplay().asyncExec(() -> {
				if (STATUS_RUNNERS.equals(string)) {
					lblrunners.setText(status);
				}
			});
		}
	}

}
