package org.collabthings.swt.controls;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.collabthings.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CTTabFolder extends CTComposite {
	private Map<String, TabInfo> tabs = new HashMap<>();
	private Map<String, CTTabsListener> closelisteners = new HashMap<>();
	private List<CTTabsListener> selectionlisteners = new LinkedList<>();

	private Composite ctabs;
	private TabInfo selected;
	private StackLayout stackLayout;
	private Composite cstack;

	public CTTabFolder(Composite composite, int flat) {
		super(composite, flat);
		setBackground(SWTResourceManager.getControlBg());
		setFont(SWTResourceManager.getDefaultFont());
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);

		ctabs = new CTComposite(this, SWT.NONE);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.spacing = 10;
		ctabs.setLayout(rowLayout);
		ctabs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		cstack = new CTComposite(this, SWT.NONE);
		stackLayout = new StackLayout();
		cstack.setLayout(stackLayout);
		cstack.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	public void addTab(String name, Control control, Object object) {
		addTab(name, control, object, true);
	}

	public void addTab(String name, Control control, Object data, boolean b) {
		if (name.length() < 3) {
			name = "TAB " + name;
		}

		CTLabel l = new CTLabel(ctabs, SWT.NONE);
		TabInfo i = new TabInfo(name, l, control, data);
		tabs.put(name, i);

		l.setText(name.toUpperCase());
		l.setTitleFont();
		l.setColor(SWTResourceManager.getTabNotSelectedColor());

		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				select(i);
			}

		});

		if (b) {
			select(i);
		}
		
		ctabs.pack();
		layout();
	}

	private void select(TabInfo i) {
		if (selected != null) {
			selected.l.setTitleFont();
			selected.l.setColor(SWTResourceManager.getTabNotSelectedColor());
		}

		selected = i;
		i.l.setColor(SWTResourceManager.getTabSelectedColor());

		stackLayout.topControl = i.getControl();
		cstack.layout();

		List<CTTabsListener> ls = selectionlisteners;

		for (CTTabsListener ctTabsListener : ls) {
			ctTabsListener.event();
		}
	}

	public TabInfo getSelection() {
		return selected;
	}

	public class TabInfo {
		private Object d;
		private Control c;
		private String text;
		private CTLabel l;

		public TabInfo(String text, CTLabel l, Control control, Object data) {
			this.c = control;
			this.d = data;
			this.l = l;
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public Control getControl() {
			return c;
		}
	}

	public void addCloseListener(String name, CTTabsListener listener) {
		closelisteners.put(name, listener);
	}

	public void addSelectionListener(CTTabsListener l) {
		selectionlisteners.add(l);
	}

	public static interface CTTabsListener {
		void event();
	}

	public Composite getComposite() {
		return cstack;
	}

}
