package org.collabthings.swt.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.model.CTPart;
import org.collabthings.model.CTSubPart;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTListener;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.collabthings.swt.controls.CTText;
import org.collabthings.swt.controls.ObjectViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.zest.core.widgets.GraphNode;

public class ObjectContextView extends CTComposite {

	private static final int SUBPART_COLUMN_INDEX_TOOLS = 0;
	private static final int SUBPART_COLUMN_INDEX_LINE = 1;
	private static final int SUBPART_COLUMN_INDEX_NAME = 2;
	private static final int SUBPART_COLUMN_INDEX_LOC = 3;
	private static final int SUBPART_COLUMN_INDEX_NORM = 4;
	private static final int SUBPART_COLUMN_INDEX_ANGLE = 5;

	private Set<SubpartListener> subpartlisteners = new HashSet<>();
	private Set<PartListener> partlisteners = new HashSet<>();
	private CTComposite ctools;
	private CTPart rootpart;
	private Map<Object, GraphNode> nodes = new HashMap<Object, GraphNode>();
	private CTText txtPoop;

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

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ObjectContextView(LOTApp app, AppWindow window, Composite parent, int style) {
		super(parent, style);

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

		txtPoop = new CTText(expandBar, SWT.NONE);
		txtPoop.setText("poop");
		xpndtmUsedIn.setControl(txtPoop);
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
		table = new Table(expandBar, SWT.BORDER | SWT.FULL_SELECTION);
		xpndtmChildren.setControl(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		xpndtmChildren.setHeight(xpndtmChildren.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

		tblclmnActions = new TableColumn(table, SWT.NONE);
		tblclmnActions.setWidth(19);
		tblclmnActions.setText(">");

		tblclmnIndex = new TableColumn(table, SWT.NONE);
		tblclmnIndex.setWidth(100);
		tblclmnIndex.setText("index");

		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("name");

		TableColumn tblclmnLocation = new TableColumn(table, SWT.NONE);
		tblclmnLocation.setWidth(100);
		tblclmnLocation.setText("location");

		TableColumn tblclmnNormal = new TableColumn(table, SWT.NONE);
		tblclmnNormal.setWidth(100);
		tblclmnNormal.setText("normal");

		TableColumn tblclmnAngle = new TableColumn(table, SWT.NONE);
		tblclmnAngle.setWidth(100);
		tblclmnAngle.setText("angle");

		tablee = new TableEditor(table);
		tablee.horizontalAlignment = SWT.LEFT;
		tablee.grabHorizontal = true;

		table.addListener(SWT.MouseMove, e -> {
			Point pt = new Point(e.x, e.y);
			TableItem item = table.getItem(pt);
			if (item != null) {
				CTSubPart subpart = (CTSubPart) item.getData();
				fireHovered(subpart);
			}
		});

		table.addListener(SWT.MouseDown, event -> {
			Rectangle clientArea = table.getClientArea();
			Point pt = new Point(event.x, event.y);
			int index = table.getTopIndex();
			while (index < table.getItemCount()) {
				boolean visible = false;
				final TableItem item = table.getItem(index);
				for (int i = 0; i < table.getColumnCount(); i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						final int column = i;
						final Text text = new Text(table, SWT.NONE);

						Listener textListener = e -> {
							switch (e.type) {
							case SWT.FocusOut:
								item.setText(column, text.getText());
								text.dispose();

								fireEvent(item);

								break;
							case SWT.Traverse:
								switch (e.detail) {
								case SWT.TRAVERSE_RETURN:
									item.setText(column, text.getText());
									fireEvent(item);

									// FALL THROUGH
								case SWT.TRAVERSE_ESCAPE:
									text.dispose();
									e.doit = false;
								}
								break;
							}
						};
						text.addListener(SWT.FocusOut, textListener);
						text.addListener(SWT.Traverse, textListener);

						tablee.setEditor(text, item, i);
						text.setText(item.getText(i));
						text.selectAll();
						text.setFocus();
						return;
					}
					if (!visible && rect.intersects(clientArea)) {
						visible = true;
					}
				}
				if (!visible)
					return;
				index++;
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
				TableItem tableItem = new TableItem(table, SWT.NONE);

				tableItem.setText(SUBPART_COLUMN_INDEX_TOOLS, "Tools");
				tableItem.setText(SUBPART_COLUMN_INDEX_LINE, "" + (count++));
				tableItem.setText(SUBPART_COLUMN_INDEX_NAME, "" + subpart.getName());
				tableItem.setText(SUBPART_COLUMN_INDEX_LOC, "" + subpart.getLocation());
				tableItem.setText(SUBPART_COLUMN_INDEX_NORM, "" + subpart.getNormal());
				tableItem.setText(SUBPART_COLUMN_INDEX_ANGLE, "" + subpart.getAngle());

				tableItem.setData(subpart);

				tableitemlisteners.put(tableItem, () -> {
					subpart.setName(tableItem.getText(SUBPART_COLUMN_INDEX_NAME));
					subpart.setAngle(Double.parseDouble(tableItem.getText(SUBPART_COLUMN_INDEX_ANGLE)));
				});
			}
		}
	}

	private void setTools(CTPart parent, CTSubPart subpart) {
		emptyTools();

		new CTButton("copy", ctools, SWT.NONE, () -> {
			CTSubPart n = parent.newSubPart();
			n.set(subpart);
		});

		new CTButton("remove", ctools, SWT.NONE, () -> {
			parent.removeSubPart(subpart);
		});

		ctools.pack();
		layout();
		ctools.layout();
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
