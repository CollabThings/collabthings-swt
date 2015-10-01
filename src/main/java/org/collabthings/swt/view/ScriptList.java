package org.collabthings.swt.view;

import java.util.List;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import waazdoh.common.MStringID;
import waazdoh.common.WObject;

public class ScriptList extends Composite {

	private AppWindow window;
	private LOTApp app;
	private WObject o;

	public ScriptList(Composite parent, LOTApp app, AppWindow window, WObject d) {
		super(parent, SWT.NONE);
		this.window = window;
		this.app = app;
		this.o = d;

		setLayout(new GridLayout(1, false));

		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		l.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		l.setText("Scripts");

		addRow("scripts");

		for (String name : d.getChildren()) {
			addRow(name);
		}
	}

	private void addRow(String name) {
		WObject scriptdata = o.get(name);
		String id = scriptdata.getValue("id");
		if (id != null && name != null) {
			Composite cscript = new Composite(this, SWT.NONE);
			cscript.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
					false, 1, 1));

			cscript.setLayout(new GridLayout(4, false));
			Label clname = new Label(cscript, SWT.NONE);
			clname.setText("" + name);

			Button btnView = new Button(cscript, SWT.NONE);
			btnView.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					window.viewScript(app.getObjectFactory().getScript(
							new MStringID(id)));
				}
			});
			btnView.setText("View");

			Button btnId = new Button(cscript, SWT.NONE);
			btnId.addSelectionListener(new CopyToClipbardSelectionAdapter(
					cscript, id));
			btnId.setText("ID");

			Composite composite = new Composite(cscript, SWT.NONE);
			GridLayout gl_composite = new GridLayout(2, false);
			LOTSWT.setDefaults(gl_composite);
			composite.setLayout(gl_composite);

			Label lblAddTo = new Label(composite, SWT.NONE);
			lblAddTo.setText("Add to");

			Button btnAdd = new Button(composite, SWT.FLAT | SWT.ARROW
					| SWT.DOWN);
			btnAdd.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					Menu menu = new Menu(btnAdd.getShell(), SWT.POP_UP);

					createAddToMenu(id, menu);

					menu.setVisible(true);
				}

			});
			btnAdd.setText("Add to");
		}
	}

	private void createAddToMenu(String id, Menu menu) {
		List<LOTAppControl> controls = window.getTablist();
		for (LOTAppControl c : controls) {
			if (c instanceof ScriptUser) {
				ScriptUser su = (ScriptUser) c;
				MenuItem mcontrol = new MenuItem(menu, SWT.NONE);
				mcontrol.setText("" + c.getControlName());
				mcontrol.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						su.addScript(app.getObjectFactory().getScript(
								new MStringID(id)));
					}
				});
			}
		}
	}
}
