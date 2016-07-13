package org.collabthings.swt.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.collabthings.model.CTPart;
import org.collabthings.model.CTSubPart;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class ObjectTreeView extends CTComposite implements TreeListener {

	private TreeItem tiroot;
	private Set<SubpartListener> subpartlisteners = new HashSet<>();
	private Set<PartListener> partlisteners = new HashSet<>();
	private CTComposite ctools;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ObjectTreeView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		ctools = new CTComposite(this, SWT.NONE);
		ctools.setBackground(SWTResourceManager.getActiontitle2Background());
		ctools.setForeground(SWTResourceManager.getActionTitle2Color());

		GridLayout ctoolslayout = new GridLayout();
		ctoolslayout.numColumns = 10;
		ctools.setLayout(ctoolslayout);
		ctools.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		CTLabel lblNewLabel = new CTLabel(ctools, SWT.NONE);
		lblNewLabel.setText("Tree");
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Tree tree = new Tree(this, SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.addTreeListener(this);

		tree.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				TreeItem item = tree.getItem(new Point(e.x, e.y));
				fireHovered(null);
				if (item != null) {
					Object listenerdata = item.getData("listener");
					if (listenerdata != null) {
						TreeItemLister l = (TreeItemLister) listenerdata;
						l.hovered();
					}
				}
			}
		});

		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				TreeItemLister l = getListener((TreeItem) arg0.item);
				if (l != null) {
					l.selected();
				}
			}

		});

		tiroot = new TreeItem(tree, SWT.NONE);
		tiroot.setText("item");
	}

	@Override
	public void treeCollapsed(TreeEvent e) {
		TreeItem i = (TreeItem) e.item;
		TreeItemLister l = getListener(i);
		if (l != null) {
			l.collapsed();
		}
	}

	@Override
	public void treeExpanded(TreeEvent e) {
		TreeItem i = (TreeItem) e.item;
		TreeItemLister l = getListener(i);
		if (l != null) {
			l.expanded();
		}
	}

	private TreeItemLister getListener(TreeItem i) {
		return (TreeItemLister) i.getData("listener");
	}

	public void setPart(CTPart p) {
		for (TreeItem i : tiroot.getItems()) {
			i.dispose();
		}

		tiroot.setText("" + p.getName());
		addSubparts(tiroot, p);
		tiroot.setExpanded(true);
	}

	public void updatePart() {
		// TODO Auto-generated method stub

	}

	private void addSubparts(TreeItem tiparent, CTPart p) {
		List<CTSubPart> sp = p.getSubParts();
		for (CTSubPart subpart : sp) {
			add(tiparent, p, subpart);
		}
	}

	private void setTools(CTPart part, CTSubPart subpart) {
		emptyTools();

		new CTButton("add", ctools, SWT.NONE, () -> {
			part.newSubPart();
		});

		new CTButton("copy", ctools, SWT.NONE, () -> {
			CTSubPart n = part.newSubPart();
			n.set(subpart);
		});

		new CTButton("remove", ctools, SWT.NONE, () -> {
			part.removeSubPart(subpart);
			TreeItem i = findItemWith(subpart);
			i.dispose();
		});

		ctools.pack();
		layout();
		ctools.layout();
	}

	private void setTools(CTPart part) {
		emptyTools();

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

	private TreeItem findItemWith(CTSubPart subpart) {
		return findItemWith(tiroot, subpart);
	}

	private TreeItem findItemWith(TreeItem ti, CTSubPart subpart) {
		if (subpart.equals(ti.getData())) {
			return ti;
		}

		for (TreeItem i : ti.getItems()) {
			TreeItem ret = findItemWith(i, subpart);
			if (ret != null) {
				return ret;
			}
		}

		return null;
	}

	private void emptyTools() {
		for (Widget c : ctools.getChildren()) {
			c.dispose();
		}
	}

	private void add(TreeItem tiparent, CTPart part, CTSubPart subpart) {
		TreeItem item = new TreeItem(tiparent, SWT.NONE);
		item.setText("" + subpart);

		addSubPartItems(subpart, item);

		item.setData(subpart);
		item.setData("listener", new TreeItemLister() {
			@Override
			public void selected() {
				setTools(part, subpart);
			}

			@Override
			public void hovered() {
				fireHovered(subpart);
			}

			@Override
			public void collapsed() {
				for (TreeItem i : item.getItems()) {
					i.dispose();
				}
				addSubPartItems(subpart, item);
			}

			@Override
			public void expanded() {
				addExpandedItems(subpart, item);
			}

		});

	}

	private void addExpandedItems(CTSubPart subpart, TreeItem item) {
		TreeItem itempart = new TreeItem(item, SWT.NONE);
		itempart.setText("Part: " + subpart.getPart());
		itempart.setData("listener", new TreeItemLister() {

			@Override
			public void selected() {
				setTools(subpart.getPart());
			}

			@Override
			public void hovered() {
			}

			@Override
			public void expanded() {
			}

			@Override
			public void collapsed() {
			}
		});

		if (subpart.getPart() != null) {
			addSubparts(item, subpart.getPart());
		}
	}

	private void addSubPartItems(CTSubPart subpart, TreeItem item) {
		TreeItem loc = new TreeItem(item, SWT.NONE);
		loc.setText("" + subpart.getLocation());
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
