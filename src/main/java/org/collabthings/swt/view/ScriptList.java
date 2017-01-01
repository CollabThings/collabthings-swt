package org.collabthings.swt.view;

import java.util.List;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.app.CTSelectionAdapter;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import waazdoh.common.WStringID;
import waazdoh.common.WObject;

public class ScriptList extends CTComposite {

	private AppWindow window;
	private LOTApp app;
	private WObject o;

	public ScriptList(Composite parent, LOTApp app, AppWindow window, WObject d) {
		super(parent, SWT.NONE);
		this.window = window;
		this.app = app;
		this.o = d;

		setLayout(new GridLayout(1, false));

		CTLabel l = new CTLabel(this, SWT.NONE);
		l.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
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
			Composite cscript = new CTComposite(this, SWT.NONE);
			cscript.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

			cscript.setLayout(new GridLayout(4, false));
			CTLabel clname = new CTLabel(cscript, SWT.NONE);
			clname.setText("" + name);

			CTButton btnView = new CTButton(cscript, SWT.NONE);
			btnView.addSelectionListener(() -> {
				window.viewScript(app.getObjectFactory().getScript(new WStringID(id)));
			});
			btnView.setText("View");

			CTButton btnId = new CTButton(cscript, SWT.NONE);
			btnId.addSelectionListener(() -> new CopyToClipbard(cscript, id));
			btnId.setText("ID");

			Composite composite = new CTComposite(cscript, SWT.NONE);
			GridLayout gl_composite = new GridLayout(2, false);
			LOTSWT.setDefaults(gl_composite);
			composite.setLayout(gl_composite);

			CTLabel lblAddTo = new CTLabel(composite, SWT.NONE);
			lblAddTo.setText("Add to");

			CTButton btnAdd = new CTButton(composite, SWT.FLAT | SWT.ARROW | SWT.DOWN);
			btnAdd.addSelectionListener(() -> {
				Menu menu = new Menu(btnAdd.getShell(), SWT.POP_UP);

				createAddToMenu(id, menu);

				menu.setVisible(true);
			});
			btnAdd.setText("Add to");
		}
	}

	private void createAddToMenu(String id, Menu menu) {
		List<CTAppControl> controls = window.getTablist();
		for (CTAppControl c : controls) {
			if (c instanceof ScriptUser) {
				ScriptUser su = (ScriptUser) c;
				MenuItem mcontrol = new MenuItem(menu, SWT.NONE);
				mcontrol.setText("" + c.getControlName());
				mcontrol.addSelectionListener(
						new CTSelectionAdapter(e -> su.addScript(app.getObjectFactory().getScript(new WStringID(id)))));
			}
		}
	}
}
