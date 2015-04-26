package org.libraryofthings.swt.view;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.app.LOTApp;
import org.libraryofthings.swt.controls.ObjectViewer;
import org.libraryofthings.swt.controls.ObjectViewerListener;

public class FactoryInfoView extends Composite {

	private LOTFactory factory;
	private AppWindow window;
	private LOTApp app;

	private Composite cchildrenlist;

	public FactoryInfoView(Composite parent, LOTApp app, AppWindow window, LOTFactory factory) {
		super(parent, SWT.NONE);

		this.app = app;
		this.window = window;
		this.factory = factory;

		createDataView();
	}

	private void createDataView() {
		createDataEditors(this, factory);

		if (factory != null) {
			EnvironmentView ev = new EnvironmentView(this, window, factory.getEnvironment());
			ev.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		}

		Composite cchildren = new Composite(this, SWT.NONE);
		createChildrenComposite(cchildren);
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

		if (factory != null) {
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
						window.viewFactory(child);
					}
				});
				createDataEditors(cc, child);
			}
		}
	}

	public void updateDataEditors() {
		Control[] cc = getChildren();
		for (Control control : cc) {
			control.dispose();
		}

		createDataView();
	}

	public void addChild() {
		this.factory.addFactory();
		updateDataEditors();
	}

	public void addChild(LOTFactory f) {
		this.factory.addFactory("child" + this.factory.getFactories().size(), f);
		updateDataEditors();
	}

	private synchronized void createDataEditors(Composite c, LOTFactory f) {
		createFactoryDataViewer(c, f);
	}

	private void createFactoryDataViewer(Composite c, LOTFactory f) {
		GridLayout gl_c_factoryproperties_1 = new GridLayout(1, false);
		gl_c_factoryproperties_1.marginTop = 5;
		gl_c_factoryproperties_1.marginHeight = 0;
		c.setLayout(gl_c_factoryproperties_1);
		ObjectViewer factoryobjectviewer = new ObjectViewer(app, window, c, f);

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
		updateDataEditors();
	}

}
