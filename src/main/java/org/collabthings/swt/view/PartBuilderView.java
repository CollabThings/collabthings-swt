package org.collabthings.swt.view;

import org.collabthings.model.CTPart;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.ObjectViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class PartBuilderView extends CTComposite implements LOTAppControl {

	private LOTApp app;
	private AppWindow window;
	private CTPartBuilder builder;
	private ScriptView scriptview;
	private GLSceneView partview;

	public PartBuilderView(Composite parent, LOTApp app, AppWindow appWindow, CTPartBuilder pb) {
		super(parent, SWT.NONE);
		this.window = appWindow;
		this.app = app;
		this.builder = pb;
		setLayout(new GridLayout(1, false));

		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite left = new CTComposite(sashForm, SWT.NONE);
		left.setLayout(new GridLayout(1, false));

		Composite composite = new CTComposite(left, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		ObjectViewer oview = new ObjectViewer(app, window, composite);
		oview.setObject(builder);
		oview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite_1 = new CTComposite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		CTButton btnSave = new CTButton(composite_1, SWT.NONE);
		btnSave.addSelectionListener(() -> {
			save();
		});
		btnSave.setBounds(0, 0, 75, 25);
		btnSave.setText("save");

		scriptview = new ScriptView(left, app, window, builder.getScript());
		scriptview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		partview = new GLSceneView(sashForm);
		sashForm.setWeights(new int[] { 1, 1 });

		getDisplay().asyncExec(() -> {
			updateView();
		});

	}

	private void save() {
		scriptview.save();
	}

	private void updateView() {
		CTPart p = this.app.getLClient().getObjectFactory().getPart();
		builder.run(p);
		partview.setPart(p);
	}

	@Override
	public MenuItem createMenu(Menu menu) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void selected(AppWindow appWindow) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getControlName() {
		return "PartBuilder:" + builder;
	}

	@Override
	public Control getControl() {
		return this;
	}
}
