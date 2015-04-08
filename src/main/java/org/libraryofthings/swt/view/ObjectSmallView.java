package org.libraryofthings.swt.view;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.app.LOTApp;

import waazdoh.common.WData;
import waazdoh.common.vo.ObjectVO;
import waazdoh.common.vo.UserVO;

public class ObjectSmallView extends Composite {
	private Composite items;
	private String id;
	private LOTApp app;

	private Map<String, DataHandler> handlers = new HashMap<>();
	private Label ltype;
	private Label lid;

	public ObjectSmallView(Composite cc, LOTApp app, AppWindow window, String id) {
		super(cc, SWT.NONE);
		this.id = id;
		this.app = app;

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 1;
		gridLayout.horizontalSpacing = 1;
		setLayout(gridLayout);

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(2, false));

		Label lname = new Label(composite, SWT.NONE);
		lname.setText("Name");
		lname.setAlignment(SWT.CENTER);
		lname.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lname.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		ltype = new Label(composite, SWT.NONE);
		ltype.setText("Type");
		addDataHandler("name", d -> {
			lname.setText(d.getText());
		});

		Composite composite_3 = new Composite(this, SWT.NONE);
		composite_3.setLayout(new GridLayout(2, false));
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		Label lblId = new Label(composite_3, SWT.NONE);
		lblId.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblId.setText("ID");

		lid = new Label(composite_3, SWT.NONE);
		lid.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));

		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite_1.setLayout(new GridLayout(4, false));

		Label lblModified = new Label(composite_1, SWT.NONE);
		lblModified.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblModified.setText("Modified");

		Label lmodified = new Label(composite_1, SWT.NONE);
		lmodified.setText("date");
		GridData gd_lmodified = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_lmodified.minimumWidth = 60;
		lmodified.setLayoutData(gd_lmodified);

		Label lblCreated = new Label(composite_1, SWT.NONE);
		lblCreated.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblCreated.setText("Created");
		addDataHandler("modified", d -> {
			lmodified.setText("" + new Date(Long.parseLong(d.getText())));
		});

		Label lcreated = new Label(composite_1, SWT.NONE);
		lcreated.setText("date");
		GridData gd_lcreated = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lcreated.minimumWidth = 60;
		lcreated.setLayoutData(gd_lcreated);
		addDataHandler("created", d -> {
			lcreated.setText("" + new Date(Long.parseLong(d.getText())));
		});

		Composite composite_2 = new Composite(this, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		composite_2.setLayout(new GridLayout(3, false));

		Label lblCreator = new Label(composite_2, SWT.NONE);
		lblCreator.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblCreator.setText("Created by");

		Label lcreator = new Label(composite_2, SWT.NONE);
		lcreator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lcreator.setBounds(0, 0, 55, 15);
		lcreator.setText("Creator");

		Label lversion = new Label(composite_2, SWT.NONE);
		addDataHandler("version", lversion);
		addDataHandler("license", e -> {
		});

		addDataHandler("creator", d -> {
			String userid = d.getText();

			lcreator.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent arg0) {
					window.viewUser(lcreator.getText(), userid);
				}
			});

			lcreator.setText(userid);
			new Thread(() -> {
				UserVO u = app.getLClient().getService().getUsers().getUser(userid);
				cc.getDisplay().syncExec(() -> {
					lcreator.setText("" + u.getUsername());
				});
			}).start();
		});

		items = new Composite(this, getStyle());

		GridLayout gl_items = new GridLayout(1, false);
		gl_items.marginWidth = 1;
		gl_items.verticalSpacing = 1;
		gl_items.marginHeight = 1;
		gl_items.horizontalSpacing = 1;
		items.setLayout(gl_items);
		items.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		setData();
	}

	private void addDataHandler(String name, Label label) {
		addDataHandler(name, d -> {
			label.setText(d.getText());
		});
	}

	private void setData() {
		ObjectVO vo;
		if (id != null && app != null) {
			vo = this.app.getLClient().getService().getObjects().read(id.toString());
		} else {
			vo = null;
		}

		WData o;
		if (vo != null && (o = vo.getWData()) != null) {
			ltype.setText(o.getName());
			lid.setText(o.getAttribute("id"));

			for (WData child : o.getChildren()) {
				String name = child.getName();
				DataHandler dh = handlers.get(name);
				if (dh != null) {
					dh.handle(child);
				} else {
					Label l = new Label(items, getStyle());
					l.setText("" + child);
				}
			}
		} else {
			Label nulll = new Label(items, getStyle());
			nulll.setText("Null id or app is not initialized");
		}
	}

	void addDataHandler(String name, DataHandler d) {
		handlers.put(name, d);
	}

	private interface DataHandler {
		public void handle(WData d);
	}
}
