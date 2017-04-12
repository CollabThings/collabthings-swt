/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/
package org.collabthings.swt.view;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.collabthings.app.CTApp;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import waazdoh.client.model.WBinaryID;
import waazdoh.common.WLogger;
import waazdoh.common.WObject;
import waazdoh.common.vo.ObjectVO;
import waazdoh.common.vo.UserVO;

public class ObjectSmallView extends CTComposite {
	private CTApp app;

	private Map<String, DataHandler> handlers = new HashMap<>();
	private CTLabel ltype;

	private Composite items;

	private String id;
	private Set<String> ignorelist;
	private WLogger log = WLogger.getLogger(this);

	private AppWindow window;

	private CTButton btnBookmark;

	private CTBinaryImage thumbnail;

	public ObjectSmallView(Composite cc, CTApp app, AppWindow window, String id) {
		super(cc, SWT.NONE);
		this.app = app;
		this.id = id;
		this.window = window;

		log.info("viewing " + id);

		initIgnoreList();

		setLayout(new GridLayout(3, false));

		Label label = new Label(this, SWT.SEPARATOR | SWT.SHADOW_NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));

		Composite cthumbnail = new CTComposite(this, SWT.NONE);
		FillLayout fl_cthumbnail = new FillLayout(SWT.HORIZONTAL);
		fl_cthumbnail.marginHeight = 15;
		cthumbnail.setLayout(fl_cthumbnail);
		cthumbnail.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true, 1, 1));

		thumbnail = new CTBinaryImage(app, cthumbnail, SWT.BORDER);
		GridLayout gridLayout = (GridLayout) thumbnail.getLayout();

		Composite composite = new CTComposite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(1, false));

		addTitle(composite);

		Composite cvalues = new CTComposite(composite, SWT.NONE);
		cvalues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		cvalues.setLayout(new GridLayout(1, false));

		addTools(id, cvalues);
		addDates(cvalues);

		addCreatorAndVersion(cvalues);

		items = new CTComposite(cvalues, getStyle());
		GridLayout gl_items = new GridLayout(1, false);
		gl_items.marginWidth = 0;
		gl_items.marginHeight = 0;
		items.setLayout(gl_items);
		items.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 2));

		addHandlers();

		setData();
	}

	private void addHandlers() {
		addDataHandler("license", (n, e) -> {
		});

		addDataHandler("scripts", (n, d) -> {
			ScriptList sl = new ScriptList(items, app, window, d);
			setListLayoutData(sl);
		});

		addDataHandler("environmentid", (n, d) -> {
			//
		});

		addDataHandler("value", (n, d) -> {
			// Probably script base64 value
		});
	}

	private void addTitle(Composite composite) {
		Composite ctitle = new CTComposite(composite, SWT.NONE);
		ctitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		ctitle.setLayout(new GridLayout(2, false));

		CTLabel lname = new CTLabel(ctitle, SWT.NONE);
		lname.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lname.setText("Name");
		lname.setAlignment(SWT.CENTER);
		lname.setTitleFont();

		addDataHandler("name", (n, d) -> {
			lname.setText(d.getValue(n));
		});

		ltype = new CTLabel(ctitle, SWT.NONE);
		ltype.setText("Type");
	}

	private void addDates(Composite cvalues) {
		Composite cdates = new CTComposite(cvalues, SWT.NONE);
		cdates.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout glcdates = new GridLayout(2, false);
		glcdates.marginWidth = 0;
		cdates.setLayout(glcdates);

		CTLabel lblModified = new CTLabel(cdates, SWT.NONE);
		lblModified.setText("Modified");

		CTLabel lmodified = new CTLabel(cdates, SWT.NONE);
		lmodified.setText("date");
		GridData gdlmodified = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
		gdlmodified.minimumWidth = 60;
		lmodified.setLayoutData(gdlmodified);

		CTLabel lblCreated = new CTLabel(cdates, SWT.NONE);
		lblCreated.setText("Created");
		CTLabel lcreated = new CTLabel(cdates, SWT.NONE);

		lcreated.setText("date");
		GridData gdlcreated = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gdlcreated.minimumWidth = 60;
		lcreated.setLayoutData(gdlcreated);

		addDataHandler("creationtime", (n, d) -> lcreated.setText("" + new Date(Long.parseLong(d.getValue(n)))));
		addDataHandler("modified", (n, d) -> lmodified.setText("" + new Date(Long.parseLong(d.getValue(n)))));
		addDataHandler("thumbnail", (n, d) -> thumbnail.setId(new WBinaryID(d.getValue(n))));
		addDataHandler("content", (n, d) -> handle(d.get(n)));
	}

	private void addCreatorAndVersion(Composite cvalues) {
		Composite ccreator = new CTComposite(cvalues, SWT.NONE);
		ccreator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		GridLayout gl_ccreator = new GridLayout(3, false);
		gl_ccreator.marginWidth = 0;
		ccreator.setLayout(gl_ccreator);

		CTLabel lblCreator = new CTLabel(ccreator, SWT.NONE);
		CTLabel lcreator = new CTLabel(ccreator, SWT.NONE);

		lblCreator.setText("Created by");
		lcreator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lcreator.setText("Creator");

		addDataHandler("creator", (n, d) -> {
			String userid = d.getValue(n);

			lcreator.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent arg0) {
					window.viewUser(lcreator.getText(), userid);
				}
			});

			lcreator.setText(userid);
			new Thread(() -> {
				UserVO u = app.getLClient().getService().getUsers().getUser(userid);
				if (!lcreator.isDisposed()) {
					lcreator.getDisplay().syncExec(() -> {
						if (!lcreator.isDisposed()) {
							lcreator.setText("" + u.getUsername());
						}
					});
				}
			}).start();
		});

		CTLabel lversion = new CTLabel(ccreator, SWT.NONE);
		addDataHandler("version", lversion);
	}

	private void addTools(String id, Composite cvalues) {
		Composite ctools = new CTComposite(cvalues, SWT.NONE);
		ctools.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		GridLayout gl_ctools = new GridLayout(3, false);
		gl_ctools.marginWidth = 0;
		ctools.setLayout(gl_ctools);

		CTButton bview = new CTButton(ctools, SWT.NONE);
		bview.addSelectionListener(() -> {
			window.view(ltype.getText(), id);
		});
		bview.setText("View");

		CTButton bcopyid = new CTButton(ctools, SWT.NONE);
		bcopyid.addSelectionListener(() -> new CopyToClipbard(this, id));
		bcopyid.setText("ID");

		btnBookmark = new CTButton(ctools, SWT.NONE);
		btnBookmark.setText("Bookmark");
	}

	private void initIgnoreList() {
		if (ignorelist == null) {
			ignorelist = new HashSet<String>();
			String s = app.getLClient().getPreferences().get("smallview.ignorelist", "list");
			StringTokenizer st = new StringTokenizer(s, ",");
			while (st.hasMoreTokens()) {
				String t = st.nextToken();
				ignorelist.add(t);
			}

			ignorelist.add("id");
			ignorelist.add("type");
		}
	}

	private void addDataHandler(String name, CTLabel label) {
		addDataHandler(name, (n, d) -> {
			label.setText(d.getValue(n));
		});
	}

	private void setData() {
		ObjectVO vo;
		if (id != null && app != null) {
			vo = this.app.getLClient().getService().getObjects().read(id.toString());
		} else {
			vo = null;
		}

		WObject o;

		if (vo != null && (o = vo.toObject()) != null) {

			btnBookmark.addSelectionListener(() -> {
				WObject content = o.get("content");
				if (content == null) {
					content = o;
				}

				String namevalue = content.getValue("name");
				String name;
				if (namevalue != null) {
					name = namevalue;
				} else {
					name = "unknown";
				}

				app.getLClient().getBookmarks().add("" + o.getType() + "/" + name, id);
			});

			if (o.getType() != null) {
				ltype.setText(o.getType());

				handle(o);
			} else {
				CTLabel nulll = new CTLabel(items, getStyle());
				nulll.setText("Missing type");
				this.app.getLClient().getService().getObjects().error(id, "missing type");
			}
		} else {
			CTLabel nulll = new CTLabel(items, getStyle());
			nulll.setText("Null id or app is not initialized");
		}
	}

	private void handle(WObject o) {
		for (String name : o.getChildren()) {
			log.debug("addData " + name);

			DataHandler dh = handlers.get(name);
			if (dh != null) {
				dh.handle(name, o);
			} else if (!ignorelist.contains(name)) {
				try {
					String value = o.getValue(name);
					CTLabel l = new CTLabel(items, getStyle());
					l.setText("" + name + " " + value);
					setListLayoutData(l.getControl());
				} catch (ClassCastException e) {
					log.info("Wrong type with " + name);
				}

			}
		}
	}

	private void setListLayoutData(Control l) {
		l.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
	}

	void addDataHandler(String name, DataHandler d) {
		handlers.put(name, d);
	}

	private interface DataHandler {
		public void handle(String name, WObject child);
	}
}
