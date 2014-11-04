package org.libraryofthings.swt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.environment.impl.LOTFactoryState;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.impl.LOTEnvironmentImpl;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.SWTResourceManager;
import org.libraryofthings.swt.app.LOTApp;
import org.libraryofthings.swt.controls.ObjectViewer;
import org.libraryofthings.swt.controls.ObjectViewerListener;

public class FactoryView extends Composite {
	private LOTFactory factory;
	private RunEnvironment4xView view;
	private LLog log = LLog.getLogger(this);
	private LOTApp app;

	private Composite cchildrenlist;
	private ScrolledComposite scrolledComposite;
	private Composite composite;
	private AppWindow window;

	public FactoryView(LOTApp app, AppWindow w, LOTFactory f,
			Composite composite) {
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

	private void addChild() {
		LOTFactory child = this.factory.addFactory();

		Composite cc = new Composite(cchildrenlist, SWT.NONE);
		cc.setBackground(new Color(getDisplay(), 100, 200, 200));

		Composite childpanel = new Composite(cc, SWT.None);
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;

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

		cc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		updateLayout();
	}

	private void viewChild(LOTFactory f) {
		window.viewFactory(f);
	}

	private void updateLayout() {
		scrolledComposite.layout(true, true);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
	}

	private void init() {
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);

		Composite c_toolbar = new Composite(this, SWT.NONE);
		c_toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1));
		c_toolbar.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button button = new Button(c_toolbar, SWT.FLAT);
		button.setText("A");
		button.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));

		Button btnPublish = new Button(c_toolbar, SWT.NONE);
		btnPublish.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				publish();
			}
		});
		btnPublish.setText("Publish");

		SashForm composite_main = new SashForm(this, SWT.NONE);
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));

		this.scrolledComposite = new ScrolledComposite(composite_main,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		this.composite = new Composite(scrolledComposite, SWT.NONE);

		createDataEditors(composite, factory);

		Composite cchildren = new Composite(composite, SWT.NONE);
		cchildren.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1,
				1));
		GridLayout gl_cchildren = new GridLayout(1, false);
		gl_cchildren.marginWidth = 0;
		gl_cchildren.verticalSpacing = 0;
		gl_cchildren.marginHeight = 0;
		gl_cchildren.horizontalSpacing = 0;
		cchildren.setLayout(gl_cchildren);

		Composite cchildrenpanel = new Composite(cchildren, SWT.NONE);
		cchildrenpanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		cchildrenpanel.setSize(65, 25);
		GridLayout gl_cchildrenpanel = new GridLayout(2, false);
		gl_cchildrenpanel.horizontalSpacing = 0;
		gl_cchildrenpanel.marginHeight = 0;
		gl_cchildrenpanel.verticalSpacing = 0;
		gl_cchildrenpanel.marginWidth = 0;
		cchildrenpanel.setLayout(gl_cchildrenpanel);

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
		cchildrenlist.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		cchildrenlist.setSize(0, 0);
		cchildrenlist.setLayout(new GridLayout(1, false));

		scrolledComposite.setContent(composite);

		Composite c_view = new Composite(composite_main, SWT.NONE);
		c_view.setLayout(new FillLayout(SWT.HORIZONTAL));
		c_view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c_view.setBounds(0, 0, 64, 64);

		view = new RunEnvironment4xView(c_view, SWT.NONE);
		composite_main.setWeights(new int[] { 1, 1 });

		updateFactory();
	}

	private void createDataEditors(Composite c, LOTFactory f) {
		createFactoryDataViewer(c, f);
		createEnvironmentDataViewer(c, f);
	}

	private void updateFactory() {
		LOTClient client = app.getEnvironment();
		LOTEnvironment env = new LOTEnvironmentImpl(client);
		LOTRunEnvironment runenv = new LOTFactoryState(client, env, "view",
				factory).getRunEnvironment();
		view.setRunEnvironment(runenv);
		view.step(0);
		view.doRepaint();
	}

	protected void publish() {
		this.factory.publish();
	}

	private void createEnvironmentDataViewer(Composite c, LOTFactory f) {
		ObjectViewer envobjectviewer = new ObjectViewer(c, f.getEnvironment());
		envobjectviewer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		GridLayout gridLayout = (GridLayout) envobjectviewer.getLayout();
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;

		envobjectviewer.addListener(new ObjectViewerListener() {
			@Override
			public void valueChanged(String name, Object o) {
				environmentObjectChanged(name, o);
			}
		});
	}

	private void createFactoryDataViewer(Composite c, LOTFactory f) {
		GridLayout gl_c_factoryproperties_1 = new GridLayout(1, false);
		gl_c_factoryproperties_1.marginHeight = 0;
		gl_c_factoryproperties_1.verticalSpacing = 0;
		gl_c_factoryproperties_1.marginWidth = 0;
		c.setLayout(gl_c_factoryproperties_1);
		ObjectViewer factoryobjectviewer = new ObjectViewer(c, f);
		factoryobjectviewer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
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
}
