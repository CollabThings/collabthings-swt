package org.collabthings.swt.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.collabthings.LOTClient;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.impl.LOTFactoryState;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.LOTScript;
import org.collabthings.model.impl.LOTEnvironmentImpl;
import org.collabthings.model.impl.LOTFactoryImpl;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.LocalObjectsMenu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class FactoryView extends Composite implements LOTAppControl, ScriptUser {
	private LOTFactory factory;
	private RunEnvironment4xJFXView view;
	private LOTApp app;

	private ScrolledComposite scrolledComposite;
	private AppWindow window;
	private FactoryInfoView infoview;

	private int currentfactoryhash;

	public FactoryView(Composite composite, LOTApp app, AppWindow w,
			LOTFactory f) {
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
		factory.addScript(
				"script" + factory.getScripts().size() + " " + script.getName(),
				script);
	}

	private synchronized void updateFactoryHash() {
		currentfactoryhash = factory.getBean().hashCode();
	}

	@Override
	public void selected(AppWindow w) {
		updateView();
	}

	private void viewChild(LOTFactory f) {
		window.viewFactory(f);
	}

	LOTScript addScript(String name) {
		return factory.addScript(name);
	}

	private void updateLayout() {
		int w = scrolledComposite.getClientArea().width;
		infoview.pack();
		scrolledComposite.setMinSize(w, infoview.computeSize(w, SWT.DEFAULT).y);
	}

	private void init() {
		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		SashForm composite_main = new SashForm(this, SWT.NONE);
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));

		this.scrolledComposite = new ScrolledComposite(composite_main,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				updateLayout();
			}
		});

		this.infoview = new FactoryInfoView(scrolledComposite, app, window,
				factory);
		scrolledComposite.setContent(infoview);

		Composite c_view = new Composite(composite_main, SWT.NONE);
		c_view.setLayout(new FillLayout(SWT.HORIZONTAL));
		c_view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c_view.setBounds(0, 0, 64, 64);

		view = new RunEnvironment4xJFXView(c_view, SWT.NONE);
		composite_main.setWeights(new int[] { 275, 421 });

		Menu tempmenu = new Menu(this);
		setMenu(tempmenu);
		createMenu(tempmenu);

		new Thread(() -> checkFactoryUpdate()).start();
	}

	private void updateFactory() {

		new Thread(() -> {
			LOTClient client = app.getLClient();
			LOTEnvironment env = new LOTEnvironmentImpl(client);
			LOTRunEnvironment runenv = new LOTFactoryState(client, env, "view",
					factory).getRunEnvironment();
			view.setRunEnvironment(runenv);
			view.step(0);
			view.stop();
			// view.doRepaint();
			}).start();
	}

	private synchronized void updateView() {
		getDisplay().asyncExec(() -> {
			infoview.updateDataEditors();
			updateFactory();

			updateLayout();
		});
	}

	private synchronized void updateDataEditors() {
		window.updateObjectMenu(this);
		infoview.updateDataEditors();
		updateLayout();
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
				addScript("script"
						+ factory.getEnvironment().getScripts().size());
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
				infoview.addChild();
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
	}

	private void initLocalMenu(Menu mAddLocalChild) {
		LocalObjectsMenu m = new LocalObjectsMenu(window, mAddLocalChild);
		m.addObjectHandler(LOTFactoryImpl.BEANNAME, (data) -> {
			LOTFactory f = window.getApp().getLClient().getObjectFactory()
					.getFactory(data.getIDValue("id"));
			infoview.addChild(f);
		});
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
					window.showError(e);
				}
			}
		}
	}

	protected void importSelected() {
		getDisplay().asyncExec(
				() -> {
					try {
						FileDialog fd = new FileDialog(getShell(), SWT.MULTI);
						fd.open();

						String fpath = fd.getFilterPath();
						String[] fns = fd.getFileNames();
						for (String string : fns) {
							LOTScript s = addScript(string);
							byte[] bs = Files.readAllBytes(Paths.get(fpath
									+ File.separator + string));
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
