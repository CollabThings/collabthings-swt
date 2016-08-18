package org.collabthings.swt.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.CTListener;
import org.collabthings.math.CTMath;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTSubPart;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.collabthings.swt.controls.CTText;
import org.collabthings.swt.controls.ObjectViewer;
import org.collabthings.swt.controls.dialogs.CTSubPartPopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.zest.core.widgets.GraphNode;

public class ObjectContextView extends CTComposite {

	private static final int SUBPART_COLUMN_INDEX_VIEW = 0;
	private static final int SUBPART_COLUMN_INDEX_TOOLS = 1;
	private static final int SUBPART_COLUMN_INDEX_LINE = 2;
	private static final int SUBPART_COLUMN_INDEX_NAME = 3;
	private static final int SUBPART_COLUMN_INDEX_LOC = 4;
	private static final int SUBPART_COLUMN_INDEX_NORM = 5;
	private static final int SUBPART_COLUMN_INDEX_ANGLE = 6;

	private final LOTApp app;

	private Set<SubpartListener> subpartlisteners = new HashSet<>();
	private Set<PartListener> partlisteners = new HashSet<>();
	private CTComposite ctools;
	private CTPart rootpart;
	private Map<Object, GraphNode> nodes = new HashMap<Object, GraphNode>();
	private CTComposite cusedin;

	private ObjectViewer propertiesview;
	private ExpandItem xpndtmProperties;
	private ExpandItem xpndtmChildren;
	private ScrolledComposite scrolledComposite;
	private ExpandBar expandBar;
	private TableColumn tblclmnIndex;
	private TableColumn tblclmnActions;
	private TableEditor tablee;
	private Table table;
	private Map<TableItem, CTListener> tableitemlisteners = new HashMap<>();
	private TableColumn tblclmnView;
	private CTComposite cchildrentools;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ObjectContextView(LOTApp app, AppWindow window, Composite parent, int style) {
		super(parent, style);

		this.app = app;

		setLayout(new GridLayout(1, false));

		ctools = new CTComposite(this, SWT.NONE);
		ctools.setBackground(SWTResourceManager.getActiontitle2Background());
		ctools.setForeground(SWTResourceManager.getActionTitle2Color());

		GridLayout ctoolslayout = new GridLayout();
		ctoolslayout.numColumns = 10;
		ctools.setLayout(ctoolslayout);
		ctools.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		CTLabel ltitle = new CTLabel(ctools, SWT.NONE);

		scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				updateLayout();
			}
		});

		expandBar = new ExpandBar(scrolledComposite, SWT.NONE);
		expandBar.setBackground(SWTResourceManager.getControlBg());

		ExpandItem xpndtmUsedIn = new ExpandItem(expandBar, SWT.NONE);
		xpndtmUsedIn.setExpanded(true);
		xpndtmUsedIn.setText("Used in");

		cusedin = new CTComposite(expandBar, SWT.NONE);

		xpndtmUsedIn.setControl(cusedin);
		xpndtmUsedIn.setHeight(xpndtmUsedIn.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

		xpndtmProperties = new ExpandItem(expandBar, SWT.NONE);
		xpndtmProperties.setExpanded(true);
		xpndtmProperties.setText("Properties");

		propertiesview = new ObjectViewer(app, window, expandBar);
		xpndtmProperties.setControl(propertiesview);

		xpndtmChildren = new ExpandItem(expandBar, SWT.NONE);
		xpndtmChildren.setExpanded(true);
		xpndtmChildren.setText("children");

		createSubpartsTable();

		scrolledComposite.setContent(expandBar);
		scrolledComposite.setMinSize(expandBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		ltitle.setText("Tree");
		ltitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		propertiesview.addObjectChangeListener(() -> {
			updateLayout();
		});
	}

	private void createSubpartsTable() {
		Composite children = new CTComposite(expandBar, SWT.NONE);
		children.setLayout(new GridLayout(1, false));

		cchildrentools = new CTComposite(children, SWT.NONE);
		cchildrentools.setLayout(new RowLayout(SWT.HORIZONTAL));
		cchildrentools.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		Label lblTools = new Label(cchildrentools, SWT.NONE);
		lblTools.setText("Tools");
		Label lblTools2 = new Label(cchildrentools, SWT.NONE);
		lblTools2.setText("Tools2");

		table = new Table(children, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		xpndtmChildren.setControl(children);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		xpndtmChildren.setHeight(xpndtmChildren.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

		tblclmnView = new TableColumn(table, SWT.NONE);
		tblclmnView.setWidth(19);
		tblclmnView.setText(">");

		tblclmnActions = new TableColumn(table, SWT.NONE);
		tblclmnActions.setWidth(19);
		tblclmnActions.setText("T");

		tblclmnIndex = new TableColumn(table, SWT.NONE);
		tblclmnIndex.setWidth(20);
		tblclmnIndex.setText("I");

		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("name");

		TableColumn tblclmnLocation = new TableColumn(table, SWT.NONE);
		tblclmnLocation.setWidth(200);
		tblclmnLocation.setText("location");

		TableColumn tblclmnNormal = new TableColumn(table, SWT.NONE);
		tblclmnNormal.setWidth(200);
		tblclmnNormal.setText("normal");

		TableColumn tblclmnAngle = new TableColumn(table, SWT.NONE);
		tblclmnAngle.setWidth(100);
		tblclmnAngle.setText("angle");

		table.addListener(SWT.Resize, e -> {
			int w = table.getSize().x - 20;
			tblclmnView.setWidth(19);
			tblclmnActions.setWidth(19);
			tblclmnIndex.setWidth(19);
			tblclmnName.setWidth(100);

			int rest = w - 19 * 3 - 100;
			tblclmnLocation.setWidth(rest * 4 / 10);
			tblclmnNormal.setWidth(rest * 4 / 10);
			tblclmnAngle.setWidth(rest * 2 / 10);
		});

		tablee = new TableEditor(table);
		tablee.horizontalAlignment = SWT.LEFT;
		tablee.grabHorizontal = true;

		table.addListener(SWT.MouseMove, e -> {
			Point pt = new Point(e.x, e.y);
			TableItem item = table.getItem(pt);
			if (item != null) {
				CTSubPart subpart = (CTSubPart) item.getData();
				fireHovered(subpart);
			} else {
				fireHovered(null);
			}
		});

		table.addListener(SWT.MouseDown, e -> {
			Point pt = new Point(e.x, e.y);
			TableItem item = table.getItem(pt);
			if (item != null) {
				int index = 0;

				for (; index <= table.getColumnCount(); index++) {
					Rectangle b = item.getBounds(index);
					if (b.contains(pt)) {
						break;
					}
				}

				CTSubPart subpart = (CTSubPart) item.getData();

				if (index == SUBPART_COLUMN_INDEX_VIEW) {
					fireView(subpart.getPart());
				} else if (index == SUBPART_COLUMN_INDEX_TOOLS) {
					setTools(rootpart, subpart);
				} else {
					CTSubPartPopupDialog dialog = new CTSubPartPopupDialog(getShell(), app, (CTSubPart) item.getData());
					dialog.open();
				}
			}
		});

	}

	private void fireEvent(final TableItem item) {
		CTListener listener = tableitemlisteners.get(item);
		if (listener != null) {
			listener.event();
		}
	}

	private void updateLayout() {

		getDisplay().asyncExec(() -> {
			if (scrolledComposite != null) {
				Rectangle clientArea = scrolledComposite.getClientArea();
				int w = clientArea.width;

				propertiesview.pack();
				propertiesview.layout();
				Point propsize = propertiesview.computeSize(w, SWT.DEFAULT);

				xpndtmProperties.setHeight(propsize.y);

				Point expandbarsize = expandBar.computeSize(w, SWT.DEFAULT);
				int height = expandbarsize.y;
				if (height < clientArea.height) {
					height = clientArea.height;
				}

				scrolledComposite.setMinSize(w, height);
			}
		});
	}

	public void setPart(CTPart p) {
		this.rootpart = p;
		propertiesview.setObject(p);
		xpndtmProperties.setHeight(xpndtmProperties.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

		updatePart();

	}

	public void updatePart() {
		clear();

		Control[] tcs = table.getChildren();
		for (Control control : tcs) {
			control.dispose();
		}

		TableItem[] items = table.getItems();
		for (TableItem ti : items) {
			ti.dispose();
		}

		addSubparts(rootpart);

		xpndtmChildren.setHeight(xpndtmChildren.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

		int w = scrolledComposite.getClientArea().width;
		scrolledComposite.setMinSize(w, expandBar.computeSize(w, SWT.DEFAULT).y);

	}

	private void addSubparts(CTPart p) {
		if (p != null) {
			int count = 0;
			List<CTSubPart> subparts = p.getSubParts();
			for (CTSubPart subpart : subparts) {
				TableItem tableitem = new TableItem(table, SWT.NONE);

				tableitem.setText(SUBPART_COLUMN_INDEX_VIEW, ">");
				tableitem.setText(SUBPART_COLUMN_INDEX_TOOLS, "T");
				tableitem.setText(SUBPART_COLUMN_INDEX_LINE, "" + (count++));
				tableitem.setText(SUBPART_COLUMN_INDEX_NAME, "" + subpart.getName());
				tableitem.setText(SUBPART_COLUMN_INDEX_LOC, "" + subpart.getLocation());
				tableitem.setText(SUBPART_COLUMN_INDEX_NORM, "" + subpart.getNormal());
				tableitem.setText(SUBPART_COLUMN_INDEX_ANGLE, "" + CTMath.radToDegrees(subpart.getAngle()));

				tableitem.setData(subpart);

				tableitemlisteners.put(tableitem, () -> {
					subpart.setName(tableitem.getText(SUBPART_COLUMN_INDEX_NAME));
					subpart.setAngle(Double.parseDouble(tableitem.getText(SUBPART_COLUMN_INDEX_ANGLE)));
				});
			}
		}
	}

	private void setTools(CTPart parent, CTSubPart subpart) {
		for (Widget c : cchildrentools.getChildren()) {
			c.dispose();
		}

		new CTButton("copy", cchildrentools, SWT.NONE, () -> {
			CTSubPart n = parent.newSubPart();
			n.set(subpart);
		});

		new CTButton("remove", cchildrentools, SWT.NONE, () -> {
			parent.removeSubPart(subpart);
		});

		cchildrentools.pack();
		layout();
		cchildrentools.layout();
	}

	private void setTools(CTPart part) {
		emptyTools();

		new CTButton("add", ctools, SWT.NONE, () -> {
			part.newSubPart();
		});

		new CTButton("view", ctools, SWT.NONE, () -> {
			fireView(part);
		});

		new CTButton("remove model", ctools, SWT.NONE, () -> {
			part.resetModel();
		});

		new CTButton("new scad", ctools, SWT.NONE, () -> {
			part.newSCAD();
		});

		ctools.pack();
		layout();
		ctools.layout();
	}

	private void emptyTools() {
		for (Widget c : ctools.getChildren()) {
			c.dispose();
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void addSubpartListener(SubpartListener subpartListener) {
		subpartlisteners.add(subpartListener);
	}

	private void fireHovered(CTSubPart subpart) {
		for (SubpartListener l : subpartlisteners) {
			l.hoverOver(subpart);
		}
	}

	public void addPartListener(PartListener partListener) {
		partlisteners.add(partListener);
	}

	private void fireView(CTPart subpart) {
		for (PartListener l : partlisteners) {
			l.view(subpart);
		}
	}

	private void clear() {
		nodes.clear();
		this.tableitemlisteners.clear();
	}

	private interface TreeItemLister {

		public void expanded();

		public void selected();

		public void collapsed();

		public void hovered();
	}

	public static interface SubpartListener {
		void hoverOver(CTSubPart subpart);
	}

	public static interface PartListener {
		void view(CTPart part);
	}
}
