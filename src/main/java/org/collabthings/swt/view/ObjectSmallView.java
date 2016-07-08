package org.collabthings.swt.view;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import waazdoh.common.WLogger;
import waazdoh.common.WObject;
import waazdoh.common.vo.ObjectVO;
import waazdoh.common.vo.UserVO;

public class ObjectSmallView extends CTComposite {
	private LOTApp app;

	private Map<String, DataHandler> handlers = new HashMap<>();
	private CTLabel ltype;

	private Composite items;

	private String id;
	private Set<String> ignorelist;
	private WLogger log = WLogger.getLogger(this);

	private CTButton btnBookmark;

	public ObjectSmallView(Composite cc, LOTApp app, AppWindow window, String id) {
		super(cc, SWT.NONE);
		this.app = app;
		this.id = id;

		log.info("viewing " + id);

		initIgnoreList();

		this.setLayout(new GridLayout());

		Composite ctitle = new CTComposite(this, SWT.NONE);
		ctitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		ctitle.setLayout(new GridLayout(2, false));

		CTLabel lname = new CTLabel(ctitle, SWT.NONE);
		lname.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lname.setText("Name");
		lname.setAlignment(SWT.CENTER);

		ltype = new CTLabel(ctitle, SWT.NONE);

		ltype.setText("Type");
		addDataHandler("name", (n, d) -> {
			lname.setText(d.getValue(n));
		});

		Composite cvalues = new CTComposite(this, SWT.NONE);
		cvalues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		cvalues.setLayout(new GridLayout(1, false));

		Composite ctools = new CTComposite(cvalues, SWT.NONE);
		ctools.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		ctools.setLayout(new GridLayout(3, false));

		CTButton bview = new CTButton(ctools, SWT.NONE);
		bview.addSelectionListener(() -> {
			window.view(ltype.getText(), id);
		});
		bview.setText("View");

		CTButton bcopyid = new CTButton(ctools, SWT.NONE);
		bcopyid.addSelectionListener(() -> new CopyToClipbard(this, id));
		bcopyid.setText("ID");

		this.btnBookmark = new CTButton(ctools, SWT.NONE);
		btnBookmark.setText("Bookmark");

		Composite cdates = new CTComposite(cvalues, SWT.NONE);
		cdates.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cdates.setLayout(new GridLayout(4, false));

		CTLabel lblModified = new CTLabel(cdates, SWT.NONE);
		lblModified.setText("Modified");

		CTLabel lmodified = new CTLabel(cdates, SWT.NONE);
		lmodified.setText("date");
		GridData gd_lmodified = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
		gd_lmodified.minimumWidth = 60;
		lmodified.setLayoutData(gd_lmodified);

		CTLabel lblCreated = new CTLabel(cdates, SWT.NONE);
		lblCreated.setText("Created");

		CTLabel lcreated = new CTLabel(cdates, SWT.NONE);
		lcreated.setText("date");
		GridData gd_lcreated = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_lcreated.minimumWidth = 60;
		lcreated.setLayoutData(gd_lcreated);

		addDataHandler("modified", (n, d) -> {
			lmodified.setText("" + new Date(Long.parseLong(d.getValue(n))));
		});
		addDataHandler("creationtime", (n, d) -> {
			lcreated.setText("" + new Date(Long.parseLong(d.getValue(n))));
		});

		Composite ccreator = new CTComposite(cvalues, SWT.NONE);
		ccreator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		ccreator.setLayout(new GridLayout(3, false));

		CTLabel lblCreator = new CTLabel(ccreator, SWT.NONE);
		lblCreator.setText("Created by");

		CTLabel lcreator = new CTLabel(ccreator, SWT.NONE);
		lcreator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lcreator.setText("Creator");

		CTLabel lversion = new CTLabel(ccreator, SWT.NONE);
		addDataHandler("version", lversion);
		addDataHandler("license", (n, e) -> {
		});

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
				if (!lcreated.isDisposed()) {
					lcreated.getDisplay().syncExec(() -> {
						if (!lcreated.isDisposed()) {
							lcreator.setText("" + u.getUsername());
						}
					});
				}
			}).start();
		});

		items = new CTComposite(cvalues, getStyle());
		GridLayout gl_items = new GridLayout(1, false);
		gl_items.marginWidth = 0;
		gl_items.marginHeight = 0;
		items.setLayout(gl_items);
		items.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 2));

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

		setData();
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

				for (String name : o.getChildren()) {
					log.debug("addData " + name);

					DataHandler dh = handlers.get(name);
					if (dh != null) {
						dh.handle(name, o);
					} else if (!ignorelist.contains(name)) {
						CTLabel l = new CTLabel(items, getStyle());
						l.setText("NAME " + name + " " + name);
						setListLayoutData(l.getControl());
					}
				}
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
