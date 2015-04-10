package org.libraryofthings.swt.view;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.app.LOTApp;

import waazdoh.common.WData;

public class ScriptList extends Composite {

	private AppWindow window;
	private LOTApp app;

	public ScriptList(Composite parent, LOTApp app, AppWindow window, WData d) {
		super(parent, SWT.NONE);
		this.window = window;
		this.app = app;

		setLayout(new GridLayout(1, false));

		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		l.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		l.setText("Scripts");

		List<WData> cs = d.getChildren();
		for (WData scriptdata : cs) {
			addRow(scriptdata);
		}
	}

	private void addRow(WData scriptdata) {
		Composite cscript = new Composite(this, SWT.NONE);
		cscript.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		String name = scriptdata.getValue("name");
		String id = scriptdata.getValue("id");
		cscript.setLayout(new GridLayout(3, false));
		Label clname = new Label(cscript, SWT.NONE);
		clname.setText("" + name);

		Button btnView = new Button(cscript, SWT.NONE);
		btnView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				window.viewScript(app.getObjectFactory().getScript(id));
			}
		});
		btnView.setText("View");

		Button btnId = new Button(cscript, SWT.NONE);
		btnId.addSelectionListener(new CopyToClipbardSelectionAdapter(cscript, id));
		btnId.setText("ID");
	}
}
