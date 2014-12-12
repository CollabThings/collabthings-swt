package org.libraryofthings.swt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.libraryofthings.LLog;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.app.LOTApp;

import waazdoh.client.model.JBean;
import waazdoh.util.MStringID;

public class ObjectSmallView extends Composite {
	private Text text;
	private MStringID id;
	private LOTApp app;
	private Label lname;
	private Label lid;

	public ObjectSmallView(Composite cc, LOTApp app, AppWindow window,
			MStringID id) {
		super(cc, SWT.NONE);
		this.id = id;
		this.app = app;

		setLayout(new GridLayout(2, false));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
		composite.setLayout(new GridLayout(1, false));

		lname = new Label(composite, SWT.NONE);
		lname.setText("Name");

		lid = new Label(composite, SWT.NONE);
		lid.setText("ID");

		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		composite_1.setLayout(new GridLayout(1, false));

		text = new Text(composite_1, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		text.setEditable(false);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		//
		setData();
	}

	private void setData() {
		if (id != null && app != null) {
			JBean o = this.app.getLClient().getService().read(id);
			LLog.getLogger(this).info("Got object " + o);

			text.setText("" + o);
			JBean fname = o.find("name");
			if (fname != null) {
				lname.setText("" + fname.getText());
				lid.setText("" + o.getChildren().get(0).getAttribute("id"));
			}
		} else {
			text.setText("Null id or app is not initialized");
			lname.setText("name");
			lid.setText("null");
		}
	}
}
