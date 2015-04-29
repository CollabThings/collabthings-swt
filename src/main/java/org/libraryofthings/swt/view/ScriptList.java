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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.LOTAppControl;
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

		addRow(new WData("scripts"));

		List<WData> cs = d.getChildren();
		for (WData scriptdata : cs) {
			addRow(scriptdata);
		}
	}

	private void addRow(WData scriptdata) {
		String name = scriptdata.getValue("name");
		String id = scriptdata.getValue("id");
		if (id != null && name != null) {
			Composite cscript = new Composite(this, SWT.NONE);
			cscript.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

			cscript.setLayout(new GridLayout(4, false));
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

			Composite composite = new Composite(cscript, SWT.NONE);
			GridLayout gl_composite = new GridLayout(2, false);
			gl_composite.verticalSpacing = 0;
			gl_composite.horizontalSpacing = 0;
			composite.setLayout(gl_composite);

			Label lblAddTo = new Label(composite, SWT.NONE);
			lblAddTo.setText("Add to");

			Button btnAdd = new Button(composite, SWT.FLAT | SWT.ARROW | SWT.DOWN);
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
						su.addScript(app.getObjectFactory().getScript(id));
					}
				});
			}
		}
	}
}
