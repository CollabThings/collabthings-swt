package org.collabthings.swt.view;

import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTPartBuilder;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.ObjectViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class PartBuilderView extends Composite implements LOTAppControl {

	private LOTApp app;
	private AppWindow window;
	private LOTPartBuilder builder;
	private ScriptView scriptview;
	private Model3DView partview;

	public PartBuilderView(Composite parent, LOTApp app, AppWindow appWindow,
			LOTPartBuilder pb) {
		super(parent, SWT.NONE);
		this.window = appWindow;
		this.app = app;
		this.builder = pb;
		setLayout(new GridLayout(1, false));

		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		Composite left = new Composite(sashForm, SWT.NONE);
		left.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(left, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));

		ObjectViewer oview = new ObjectViewer(app, window, composite, builder);
		oview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));

		Button btnSave = new Button(composite_1, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				save();
			}
		});
		btnSave.setBounds(0, 0, 75, 25);
		btnSave.setText("save");

		scriptview = new ScriptView(left, app, window, builder.getScript());
		scriptview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));

		partview = new Model3DView(sashForm, SWT.NONE);
		sashForm.setWeights(new int[] { 1, 1 });

		getDisplay().asyncExec(() -> {
			updateView();
		});

	}

	private void save() {
		scriptview.save();
	}

	private void updateView() {
		LOTPart p = this.app.getLClient().getObjectFactory().getPart();
		builder.run(p);
		partview.refresh(p.getModel());
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
