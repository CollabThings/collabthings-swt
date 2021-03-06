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
package org.collabthings.swt.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.collabthings.CTClient;
import org.collabthings.app.CTApp;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.impl.CTFactoryState;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTObject;
import org.collabthings.model.impl.CTEnvironmentImpl;
import org.collabthings.model.impl.CTFactoryImpl;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.app.CTRunner;
import org.collabthings.swt.controls.LocalObjectsMenu;
import org.collabthings.tk.CTButton;
import org.collabthings.tk.CTComposite;
import org.collabthings.tk.CTSelectionAdapter;
import org.collabthings.tk.CTTabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class FactoryView extends CTComposite implements CTAppControl {
	private CTFactory factory;
	private CTApp app;

	private AppWindow window;

	private int currentfactoryhash;
	private YamlEditor yamleditor;
	private YamlEditor enveditor;

	public FactoryView(Composite composite, CTApp app, AppWindow w, CTFactory f) {
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
	public CTObject getObject() {
		return factory;
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Factory: " + factory.getName();
	}

	public void addApplication(CTApplication application) {
		factory.addApplication("application" + factory.getApplications().size() + " " + application.getName(), application);
	}

	private synchronized void updateFactoryHash() {
		currentfactoryhash = factory.getObject().hashCode();
	}

	@Override
	public void selected(AppWindow w) {
		updateView();
	}

	private void viewChild(CTFactory f) {
		window.viewFactory(f);
	}

	CTApplication addApplication(String name) {
		return factory.addApplication(name);
	}

	private void init() {
		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);
		setLayout(gridLayout);

		SashForm cmain = new SashForm(this, SWT.NONE);
		cmain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite = new CTComposite(cmain, SWT.NONE);
		GridLayout glcomposite = new GridLayout(1, false);
		LOTSWT.setDefaults(glcomposite);
		composite.setLayout(glcomposite);

		Composite cpanel = new CTComposite(composite, SWT.NONE);
		GridLayout glcpanel = new GridLayout(1, false);
		LOTSWT.setDefaults(glcpanel);
		cpanel.setLayout(glcpanel);
		cpanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		CTButton btnAddChild = new CTButton(cpanel, SWT.NONE);
		btnAddChild.addSelectionListener(() -> addChild());

		btnAddChild.setText("add child");

		CTTabFolder tabFolder = new CTTabFolder(composite, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		yamleditor = new YamlEditor(tabFolder, SWT.NONE, "Factory");
		tabFolder.addTab("main", yamleditor, null);

		yamleditor.setObject(this.factory);

		enveditor = new YamlEditor(tabFolder, SWT.NONE, "Environment");
		tabFolder.addTab("env", enveditor, null);

		enveditor.setObject(factory.getEnvironment());

		Composite cview = new CTComposite(cmain, SWT.NONE);
		cview.setLayout(new FillLayout(SWT.HORIZONTAL));
		cview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		cview.setBounds(0, 0, 64, 64);

		cmain.setWeights(new int[] { 384, 421 });

		Menu tempmenu = new Menu(this);
		setMenu(tempmenu);
		createMenu(tempmenu);

		checkFactoryUpdate();
	}

	private void updateFactory() {
		new Thread(() -> {
			CTClient client = app.getLClient();
			CTEnvironment env = new CTEnvironmentImpl(client);
			CTRunEnvironment runenv = new CTFactoryState(client, env, "view", factory).getRunEnvironment();
		}).start();
	}

	private synchronized void updateView() {
		getDisplay().asyncExec(() -> {
			updateDataEditors();
		});
	}

	private synchronized void updateDataEditors() {
		window.updateObjectMenu(this);
		updateFactory();
	}

	protected void environmentObjectChanged(String name, Object o) {
		updateFactory();
	}

	@Override
	public MenuItem createMenu(Menu menu) {
		MenuItem mifactory = new MenuItem(menu, SWT.CASCADE);
		mifactory.setText("Factory");

		Menu mfactory = new Menu(mifactory);
		mifactory.setMenu(mfactory);

		MenuItem mifapplications = new MenuItem(mfactory, SWT.CASCADE);
		mifapplications.setText("Applications");

		Menu mfmapplications = new Menu(mifapplications);
		mifapplications.setMenu(mfmapplications);

		MenuItem mfsaddnew = new MenuItem(mfmapplications, SWT.NONE);
		mfsaddnew.setText("New");
		mfsaddnew.addSelectionListener(
				new CTSelectionAdapter(e -> addApplication("application" + factory.getEnvironment().getApplications().size())));

		MenuItem micapplications = new MenuItem(mfmapplications, SWT.CASCADE);
		micapplications.setText("list");

		Menu mapplications = new Menu(micapplications);
		micapplications.setMenu(mapplications);

		Set<String> applications = factory.getEnvironment().getApplications();
		for (String string : applications) {
			MenuItem mapplication = new MenuItem(mapplications, SWT.NONE);
			mapplication.setText(string);
			mapplication.addSelectionListener(new CTSelectionAdapter(e -> applicationMenuSelected(string)));
		}

		MenuItem mfmImport = new MenuItem(mfmapplications, SWT.NONE);
		mfmImport.setText("Import");
		mfmImport.addSelectionListener(new CTSelectionAdapter(e -> importSelected()));

		MenuItem mifpublish = new MenuItem(mfactory, SWT.NONE);
		mifpublish.setText("Publish");

		MenuItem mntmAddChild = new MenuItem(mfactory, SWT.CASCADE);
		mntmAddChild.setText("Add child");

		Menu mAddChild = new Menu(mntmAddChild);
		mntmAddChild.setMenu(mAddChild);

		MenuItem mifaddnewchild = new MenuItem(mAddChild, SWT.NONE);
		mifaddnewchild.setText("New");

		MenuItem miAddLocalChild = new MenuItem(mAddChild, SWT.CASCADE);
		miAddLocalChild.setText("Local");

		Menu mAddLocalChild = new Menu(miAddLocalChild);
		miAddLocalChild.setMenu(mAddLocalChild);
		initLocalMenu(mAddLocalChild);

		mifaddnewchild.addSelectionListener(new CTSelectionAdapter(e -> addChild()));
		mifpublish.addSelectionListener(new CTSelectionAdapter(e -> publish()));

		return mifactory;
	}

	private void publish() {
		factory.publish();
	}

	private void initLocalMenu(Menu mAddLocalChild) {
		LocalObjectsMenu m = new LocalObjectsMenu(window, mAddLocalChild);
		m.addObjectHandler(CTFactoryImpl.BEANNAME, (data) -> {
			CTFactory f = window.getApp().getLClient().getObjectFactory().getFactory(data.getIDValue("id"));
			addChild(f);
		});
	}

	private void addChild() {
		this.factory.addFactory();
	}

	private void addChild(CTFactory f) {
		this.factory.addFactory("factory" + this.factory.getFactories().size(), f);
	}

	private synchronized void checkFactoryUpdate() {
		window.addRunner(new CTRunner<>("factoryupdatecheck", 300)).runWhile(() -> isDisposed()).run(() -> {
			int nhash = factory.getObject().hashCode();
			if (nhash != currentfactoryhash) {
				yamleditor.setObject(factory);
				enveditor.setObject(factory.getEnvironment());
				currentfactoryhash = nhash;
				updateView();
			} else {
				try {
					wait(100);
				} catch (InterruptedException e) {
					window.showError("Interrupted", e);
					Thread.currentThread().interrupt();
				}
			}
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
					CTApplication s = addApplication(string);
					byte[] bs = Files.readAllBytes(Paths.get(fpath + File.separator + string));
					s.setApplication(new String(bs));
				}
			} catch (IOException e) {
				window.showError("importError", e);
			}
		});
	}

	protected void applicationMenuSelected(String string) {
		window.viewApplication(factory.getApplication(string));
	}
}
