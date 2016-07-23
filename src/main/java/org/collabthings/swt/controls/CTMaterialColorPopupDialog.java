package org.collabthings.swt.controls;

import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.controls.LOTDoubleEditor.ChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

class CTMaterialColorPopupDialog extends Dialog {
	private CTMaterialEditor materialeditor;
	private LOTDoubleEditor ered;
	private LOTDoubleEditor egreen;
	private LOTDoubleEditor eblue;

	private boolean popupclosed;
	private Composite composite_1;
	private double dred;
	private double dgreen;
	private double dblue;

	public CTMaterialColorPopupDialog(Shell arg0) {
		super(arg0);
	}

	public void open() {
		Shell parent = getParent();
		Shell shell = new Shell(parent, SWT.NO_TRIM | SWT.ON_TOP);
		shell.setText("Color");

		ChangeListener<Double> listener = (e) -> {
			updateColor();
		};

		GridLayout gl_shell = new GridLayout();
		gl_shell.numColumns = 2;
		shell.setLayout(gl_shell);

		composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite = new CTComposite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout());

		ered = new LOTDoubleEditor(composite, this.materialeditor.material.getColor()[0], listener);
		ered.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		egreen = new LOTDoubleEditor(composite, this.materialeditor.material.getColor()[1], listener);
		egreen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		eblue = new LOTDoubleEditor(composite, this.materialeditor.material.getColor()[2], listener);
		eblue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		updateColor();

		Button bOK = new Button(composite, SWT.NONE);
		bOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				popupclosed = true;
			}
		});
		bOK.setText("OK");

		shell.pack();

		this.popupclosed = false;

		shell.forceFocus();
		shell.open();

		Display display = parent.getDisplay();
		while (!popupclosed) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		shell.dispose();

		this.materialeditor.colorChanged(dred, dgreen, dblue);
	}

	private void updateColor() {
		dred = ered.getValue();
		dgreen = egreen.getValue();
		dblue = eblue.getValue();

		RGB rgb = getRGB();
		composite_1.setBackground(SWTResourceManager.getColor(rgb));
	}

	public RGB getRGB() {
		double red = ered.getValue();
		double green = egreen.getValue();
		double blue = eblue.getValue();
		return SWTResourceManager.getRGBWithDoubled(red, green, blue);
	}

	public void setListener(CTMaterialEditor ctMaterialEditor) {
		this.materialeditor = ctMaterialEditor;
	}
}