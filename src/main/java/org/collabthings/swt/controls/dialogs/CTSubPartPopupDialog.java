package org.collabthings.swt.controls.dialogs;

import org.collabthings.model.CTSubPart;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTText;
import org.collabthings.swt.controls.LOTDoubleEditor;
import org.collabthings.swt.controls.LOTVectorEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import waazdoh.common.MStringID;

public class CTSubPartPopupDialog extends Dialog {

	private boolean popupclosed;

	private CTSubPart subpart;

	private LOTVectorEditor loc;

	private Composite composite_1;

	private LOTVectorEditor n;

	private LOTDoubleEditor a;
	private Label lblLoc;
	private Label lblN;
	private Label lblAngle;
	private CTText tname;
	private Label lblName;
	private Label lblPartid;
	private CTText tpartid;

	private LOTApp app;

	public CTSubPartPopupDialog(Shell shell, LOTApp app, CTSubPart data) {
		super(shell);
		this.subpart = data;
		this.app = app;
	}

	public void open() {
		Shell parent = getParent();
		Shell shell = new Shell(parent, SWT.DIALOG_TRIM);
		shell.setSize(337, 262);
		shell.setText("SubPart " + subpart.getNamePath());

		GridLayout gl_shell = new GridLayout();
		shell.setLayout(gl_shell);

		composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		lblName = new Label(composite_1, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("name");

		tname = new CTText(composite_1, SWT.BORDER);
		tname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tname.setText(subpart.getName());
		tname.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				subpart.setName(tname.getText());
			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});

		lblLoc = new Label(composite_1, SWT.NONE);
		lblLoc.setText("loc");

		loc = new LOTVectorEditor(composite_1, subpart.getLocation(), v -> {

		});

		loc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblN = new Label(composite_1, SWT.NONE);
		lblN.setText("n");

		n = new LOTVectorEditor(composite_1, subpart.getNormal(), v -> {
		});

		GridData gd_n = new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1);
		gd_n.widthHint = 349;
		n.setLayoutData(gd_n);

		lblAngle = new Label(composite_1, SWT.NONE);
		lblAngle.setText("angle");

		a = new LOTDoubleEditor(composite_1, subpart.getAngle(), d -> {
			subpart.setAngle(d);
		});
		a.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblPartid = new Label(composite_1, SWT.NONE);
		lblPartid.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPartid.setText("Part id");

		tpartid = new CTText(composite_1, SWT.BORDER);
		tpartid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tpartid.setText(subpart.getPart().getID().toString());
		tpartid.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				subpart.setPart(app.getObjectFactory().getPart(new MStringID(tpartid.getText())));
			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});

		new Label(composite_1, SWT.NONE);

		Button bOK = new Button(composite_1, SWT.NONE);
		bOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				subpart.getNormal().normalize();
				popupclosed = true;
			}
		});
		bOK.setText("OK");

		shell.pack();

		this.popupclosed = false;

		shell.forceFocus();
		shell.open();

		Display display = parent.getDisplay();
		while (!popupclosed && !parent.isDisposed() && !shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		if (!shell.isDisposed()) {
			shell.dispose();
		}

	}

}