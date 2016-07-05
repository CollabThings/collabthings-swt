package org.collabthings.swt.view;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import waazdoh.common.WLogger;
import waazdoh.common.WObject;
import waazdoh.common.vo.ObjectVO;
import waazdoh.common.vo.UserVO;

public class ObjectSmallView extends Composite {
	private LOTApp app;

	private Map<String, DataHandler> handlers = new HashMap<>();
	private Label ltype;

	private Composite items;

	private String id;
	private Set<String> ignorelist;
	private WLogger log = WLogger.getLogger(this);

	private Button btnBookmark;

	public ObjectSmallView(Composite cc, LOTApp app, AppWindow window, String id) {
		super(cc, SWT.NONE);
		this.app = app;
		this.id = id;

		log.info("viewing " + id);

		initIgnoreList();

		this.setLayout(new GridLayout());

		Composite ctitle = new Composite(this, SWT.NONE);
		ctitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		ctitle.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		ctitle.setLayout(new GridLayout(2, false));

		Label lname = new Label(ctitle, SWT.NONE);
		lname.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1,
				1));
		lname.setText("Name");
		lname.setAlignment(SWT.CENTER);
		lname.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		lname.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));

		ltype = new Label(ctitle, SWT.NONE);
		ltype.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		ltype.setText("Type");
		addDataHandler("name", (n, d) -> {
			lname.setText(d.getValue(n));
		});

		Composite cvalues = new Composite(this, SWT.NONE);
		cvalues.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_LIST_BACKGROUND));
		cvalues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		cvalues.setLayout(new GridLayout(1, false));

		Composite ctools = new Composite(cvalues, SWT.NONE);
		ctools.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		ctools.setLayout(new GridLayout(3, false));

		Button bview = new Button(ctools, SWT.NONE);
		bview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				window.view(ltype.getText(), id);
			}
		});
		bview.setText("View");

		Button bcopyid = new Button(ctools, SWT.NONE);
		bcopyid.addSelectionListener(new CopyToClipbardSelectionAdapter(this,
				id));
		bcopyid.setText("ID");

		this.btnBookmark = new Button(ctools, SWT.NONE);
		btnBookmark.setText("Bookmark");

		Composite cdates = new Composite(cvalues, SWT.NONE);
		cdates.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		cdates.setLayout(new GridLayout(4, false));

		Label lblModified = new Label(cdates, SWT.NONE);
		lblModified
				.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblModified.setText("Modified");

		Label lmodified = new Label(cdates, SWT.NONE);
		lmodified.setText("date");
		GridData gd_lmodified = new GridData(SWT.LEFT, SWT.FILL, true, false,
				1, 1);
		gd_lmodified.minimumWidth = 60;
		lmodified.setLayoutData(gd_lmodified);

		Label lblCreated = new Label(cdates, SWT.NONE);
		lblCreated.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblCreated.setText("Created");

		Label lcreated = new Label(cdates, SWT.NONE);
		lcreated.setText("date");
		GridData gd_lcreated = new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1);
		gd_lcreated.minimumWidth = 60;
		lcreated.setLayoutData(gd_lcreated);

		addDataHandler("modified", (n, d) -> {
			lmodified.setText("" + new Date(Long.parseLong(d.getValue(n))));
		});
		addDataHandler("creationtime", (n, d) -> {
			lcreated.setText("" + new Date(Long.parseLong(d.getValue(n))));
		});

		Composite ccreator = new Composite(cvalues, SWT.NONE);
		ccreator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
				1));
		ccreator.setLayout(new GridLayout(3, false));

		Label lblCreator = new Label(ccreator, SWT.NONE);
		lblCreator.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblCreator.setText("Created by");

		Label lcreator = new Label(ccreator, SWT.NONE);
		lcreator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
				1));
		lcreator.setBounds(0, 0, 55, 15);
		lcreator.setText("Creator");

		Label lversion = new Label(ccreator, SWT.NONE);
		addDataHandler("version", lversion);
		addDataHandler("license", (n, e) -> {
		});

		addDataHandler("creator",
				(n, d) -> {
					String userid = d.getValue(n);

					lcreator.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseDown(MouseEvent arg0) {
							window.viewUser(lcreator.getText(), userid);
						}
					});

					lcreator.setText(userid);
					new Thread(() -> {
						UserVO u = app.getLClient().getService().getUsers()
								.getUser(userid);
						if (!lcreated.isDisposed()) {
							lcreated.getDisplay().syncExec(() -> {
								if (!lcreated.isDisposed()) {
									lcreator.setText("" + u.getUsername());
								}
							});
						}
					}).start();
				});

		items = new Composite(cvalues, getStyle());
		GridLayout gl_items = new GridLayout(1, false);
		gl_items.marginWidth = 0;
		gl_items.marginHeight = 0;
		items.setLayout(gl_items);
		items.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 2));
		items.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_LIST_BACKGROUND));

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
			String s = app.getLClient().getPreferences()
					.get("smallview.ignorelist", "list");
			StringTokenizer st = new StringTokenizer(s, ",");
			while (st.hasMoreTokens()) {
				String t = st.nextToken();
				ignorelist.add(t);
			}
		}
	}

	private void addDataHandler(String name, Label label) {
		addDataHandler(name, (n, d) -> {
			label.setText(d.getValue(n));
		});
	}

	private void setData() {
		ObjectVO vo;
		if (id != null && app != null) {
			vo = this.app.getLClient().getService().getObjects()
					.read(id.toString());
		} else {
			vo = null;
		}

		WObject o;

		if (vo != null && (o = vo.toObject()) != null) {

			btnBookmark.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
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

					app.getLClient().getBookmarks()
							.add("" + o.getType() + "/" + name, id);
				}
			});

			if (o.getType() != null) {
				ltype.setText(o.getType());

				for (String name : o.getChildren()) {
					log.debug("addData " + name);

					DataHandler dh = handlers.get(name);
					if (dh != null) {
						dh.handle(name, o);
					} else if (!ignorelist.contains(name)) {
						Label l = new Label(items, getStyle());
						l.setText("NAME " + name + " " + name);
						setListLayoutData(l);
					}
				}
			} else {
				Label nulll = new Label(items, getStyle());
				nulll.setText("Missing type");
				this.app.getLClient().getService().getObjects()
						.error(id, "missing type");
			}
		} else {
			Label nulll = new Label(items, getStyle());
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
