package org.libraryofthings.swt.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.environment.impl.LOTFactoryState;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.impl.LOTEnvironmentImpl;
import org.libraryofthings.model.impl.LOTFactoryImpl;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.LOTAppControl;
import org.libraryofthings.swt.app.LOTApp;
import org.libraryofthings.swt.controls.LocalObjectsMenu;
import org.libraryofthings.swt.controls.ObjectViewer;
import org.libraryofthings.swt.controls.ObjectViewerListener;

public class FactoryView extends Composite implements LOTAppControl, ScriptUser {
	private LOTFactory factory;
	private RunEnvironment4xView view;
	private LLog log = LLog.getLogger(this);
	private LOTApp app;

	private Composite cchildrenlist;
	private ScrolledComposite scrolledComposite;
	private Composite composite;
	private AppWindow window;
	private int currentfactoryhash;

	public FactoryView(Composite composite, LOTApp app, AppWindow w, LOTFactory f) {
		super(composite, SWT.None);
		this.app = app;
		this.window = w;
		this.factory = f;
		init();
	}

	private FactoryView(Composite c, int i) {
		super(c, i);
		init();
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Factory: " + factory.getName();
	}

	@Override
	public void addScript(LOTScript script) {
		factory.addScript("script" + factory.getScripts().size() + " " + script.getName(), script);
	}

	@Override
	public void selected(AppWindow w) {
		updateView();
	}

	private void addChild() {
		this.factory.addFactory();
		updateDataEditors();
	}

	private void addChild(LOTFactory f) {
		this.factory.addFactory("child" + this.factory.getFactories().size(), f);
		updateDataEditors();
	}

	private void viewChild(LOTFactory f) {
		window.viewFactory(f);
	}

	LOTScript addScript(String name) {
		return factory.addScript(name);
	}

	private void updateLayout() {
		int w = scrolledComposite.getClientArea().width;
		composite.pack();
		scrolledComposite.setMinSize(w, composite.computeSize(w, SWT.DEFAULT).y);
	}

	private void init() {
		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		SashForm composite_main = new SashForm(this, SWT.NONE);
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.scrolledComposite = new ScrolledComposite(composite_main, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				updateLayout();
			}
		});

		this.composite = new Composite(scrolledComposite, SWT.NONE);

		createDataView();

		scrolledComposite.setContent(composite);

		Composite c_view = new Composite(composite_main, SWT.NONE);
		c_view.setLayout(new FillLayout(SWT.HORIZONTAL));
		c_view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c_view.setBounds(0, 0, 64, 64);

		view = new RunEnvironment4xView(c_view, SWT.NONE);
		composite_main.setWeights(new int[] { 275, 421 });

		Menu tempmenu = new Menu(this);
		setMenu(tempmenu);
		createMenu(tempmenu);

		updateFactory();
		//
		new Thread(() -> checkFactoryUpdate()).start();
	}

	private void createDataView() {
		createDataEditors(composite, factory);

		EnvironmentView ev = new EnvironmentView(composite, window, factory.getEnvironment());
		ev.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite cchildren = new Composite(composite, SWT.NONE);
		createChildrenComposite(cchildren);

		updateLayout();
	}

	private void createChildrenComposite(Composite cchildren) {
		cchildren.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		GridLayout gl_cchildren = new GridLayout(1, false);
		cchildren.setLayout(gl_cchildren);

		Composite cchildrenpanel = new Composite(cchildren, SWT.NONE);
		cchildrenpanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cchildrenpanel.setSize(65, 25);
		GridLayout gl_cchildrenpanel = new GridLayout(2, false);
		cchildrenpanel.setLayout(gl_cchildrenpanel);

		Label lblChildren = new Label(cchildrenpanel, SWT.NONE);
		lblChildren.setText("CHILDREN");

		Button bnewchild = new Button(cchildrenpanel, SWT.NONE);
		bnewchild.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addChild();
			}
		});
		bnewchild.setToolTipText("New Child factory");
		bnewchild.setText("+");

		this.cchildrenlist = new Composite(cchildren, SWT.NONE);
		cchildrenlist.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));
		cchildrenlist.setSize(0, 0);
		cchildrenlist.setLayout(new GridLayout(1, false));

		Set<String> children = factory.getFactories();
		for (String childname : children) {
			LOTFactory child = factory.getFactory(childname);

			Composite cc = new Composite(cchildrenlist, SWT.NONE);
			cc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

			Composite childpanel = new Composite(cc, SWT.None);
			GridLayout gridLayout = new GridLayout();

			childpanel.setLayout(gridLayout);
			Button b = new Button(childpanel, getStyle());
			b.setText("view");
			b.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					viewChild(child);
				}
			});
			createDataEditors(cc, child);

		}
	}

	private synchronized void checkFactoryUpdate() {
		while (!isDisposed()) {
			int nhash = factory.getBean().hashCode();
			if (nhash != currentfactoryhash && !window.isSelected(this)) {
				currentfactoryhash = nhash;
				updateView();
			} else {
				try {
					wait(100);
				} catch (InterruptedException e) {
					log.info("" + e);
				}
			}
		}
	}

	private synchronized void updateView() {
		getDisplay().asyncExec(() -> {
			updateDataEditors();
			updateFactory();

			updateLayout();
		});
	}

	private synchronized void updateDataEditors() {
		updateDataEditors(composite);
	}

	private synchronized void updateDataEditors(Composite c) {
		Control[] cc = c.getChildren();
		for (Control control : cc) {
			control.dispose();
		}
		//
		window.updateObjectMenu(this);

		createDataView();

		updateFactoryHash();
		updateLayout();
	}

	private synchronized void updateFactoryHash() {
		currentfactoryhash = factory.getBean().hashCode();
	}

	private synchronized void createDataEditors(Composite c, LOTFactory f) {
		createFactoryDataViewer(c, f);
	}

	private void updateFactory() {
		updateFactoryHash();

		new Thread(
				() -> {
					LOTClient client = app.getLClient();
					LOTEnvironment env = new LOTEnvironmentImpl(client);
					LOTRunEnvironment runenv = new LOTFactoryState(client, env, "view", factory)
							.getRunEnvironment();
					view.setRunEnvironment(runenv);
					view.step(0);
					view.doRepaint();
				}).start();
	}

	private void createFactoryDataViewer(Composite c, LOTFactory f) {
		GridLayout gl_c_factoryproperties_1 = new GridLayout(1, false);
		gl_c_factoryproperties_1.marginTop = 5;
		gl_c_factoryproperties_1.marginHeight = 0;
		c.setLayout(gl_c_factoryproperties_1);
		ObjectViewer factoryobjectviewer = new ObjectViewer(c, f);

		factoryobjectviewer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		GridLayout gridLayout = (GridLayout) factoryobjectviewer.getLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		factoryobjectviewer.addListener(new ObjectViewerListener() {
			@Override
			public void valueChanged(String name, Object o) {
				factoryObjectChanged(name, o);
			}
		});
	}

	protected void factoryObjectChanged(String name, Object o) {
		updateFactory();
	}

	protected void environmentObjectChanged(String name, Object o) {
		updateFactory();
	}

	public MenuItem createMenu(Menu menu) {
		MenuItem mifactory = new MenuItem(menu, SWT.CASCADE);
		mifactory.setText("Factory");

		Menu mfactory = new Menu(mifactory);
		mifactory.setMenu(mfactory);

		MenuItem mifscripts = new MenuItem(mfactory, SWT.CASCADE);
		mifscripts.setText("Scripts");

		Menu mfmscripts = new Menu(mifscripts);
		mifscripts.setMenu(mfmscripts);

		MenuItem mfsaddnew = new MenuItem(mfmscripts, SWT.NONE);
		mfsaddnew.setText("New");
		mfsaddnew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addScript("script" + factory.getEnvironment().getScripts().size());
			}
		});

		MenuItem micscripts = new MenuItem(mfmscripts, SWT.CASCADE);
		micscripts.setText("list");

		Menu mscripts = new Menu(micscripts);
		micscripts.setMenu(mscripts);

		Set<String> scripts = factory.getEnvironment().getScripts();
		for (String string : scripts) {
			MenuItem mscript = new MenuItem(mscripts, SWT.NONE);
			mscript.setText(string);
			mscript.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					scriptMenuSelected(string);
				};
			});
		}

		MenuItem mfmImport = new MenuItem(mfmscripts, SWT.NONE);
		mfmImport.setText("Import");
		mfmImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				importSelected();
			}
		});

		MenuItem mifpublish = new MenuItem(mfactory, SWT.NONE);
		mifpublish.setText("Publish");

		MenuItem mntmAddChild = new MenuItem(mfactory, SWT.CASCADE);
		mntmAddChild.setText("Add  child");

		Menu mAddChild = new Menu(mntmAddChild);
		mntmAddChild.setMenu(mAddChild);

		MenuItem mifaddnewchild = new MenuItem(mAddChild, SWT.NONE);
		mifaddnewchild.setText("New");

		MenuItem miAddLocalChild = new MenuItem(mAddChild, SWT.CASCADE);
		miAddLocalChild.setText("Local");

		Menu mAddLocalChild = new Menu(miAddLocalChild);
		miAddLocalChild.setMenu(mAddLocalChild);
		initLocalMenu(mAddLocalChild);

		mifaddnewchild.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addChild();
			}
		});
		mifpublish.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				publish();
			}

		});

		return mifactory;
	}

	private void publish() {
		factory.publish();
		app.getLClient().publish("lastpublished/factory", factory);
	}

	private void initLocalMenu(Menu mAddLocalChild) {
		LocalObjectsMenu m = new LocalObjectsMenu(window, mAddLocalChild);
		m.addObjectHandler(
				LOTFactoryImpl.BEANNAME,
				(data) -> {
					LOTFactory f = window.getApp().getLClient().getObjectFactory()
							.getFactory(data.getIDValue("id"));
					addChild(f);
				});
	}

	protected void importSelected() {
		getDisplay().asyncExec(() -> {
			try {
				FileDialog fd = new FileDialog(getShell(), SWT.MULTI);
				fd.open();

				String fpath = fd.getFilterPath();
				String[] fns = fd.getFileNames();
				for (String string : fns) {
					LOTScript s = addScript(string);
					byte[] bs = Files.readAllBytes(Paths.get(fpath + File.separator + string));
					s.setScript(new String(bs));
				}
			} catch (IOException e) {
				window.showError(e);
			}
		});
	}

	protected void scriptMenuSelected(String string) {
		window.viewScript(factory.getScript(string));
	}
}
