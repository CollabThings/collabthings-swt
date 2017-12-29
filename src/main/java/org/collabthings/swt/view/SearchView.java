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

import java.util.List;

import org.collabthings.app.CTApp;
import org.collabthings.model.CTObject;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTResourceManagerFactory;
import org.collabthings.swt.controls.CTText;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import waazdoh.common.vo.ObjectVO;

public class SearchView extends CTComposite implements CTAppControl {
	private static final int COLUMN_WIDTH = 500;
	private AppWindow window;
	private CTText text;
	private CTApp app;
	private LLog log = LLog.getLogger(this);
	private Composite clist;
	private ScrolledComposite scrolledComposite;
	private GridLayout clistlayout;

	private final CTSearchResultFactory factory;

	/**
	 * @wbp.parser.constructor
	 */
	public SearchView(Composite c, CTApp app, AppWindow appWindow, CTSearchResultFactory factory) {
		this(c, app, appWindow, false, factory);
	}

	public SearchView(Composite c, CTApp app, AppWindow appWindow, boolean hidesearchbox,
			CTSearchResultFactory nfactory) {
		super(c, SWT.NONE);
		this.app = app;
		this.window = appWindow;
		this.factory = nfactory;

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginLeft = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;

		this.setLayout(gridLayout);

		if (!hidesearchbox) {
			Composite composite = new CTComposite(this, SWT.NONE);
			composite.setBackground(CTResourceManagerFactory.instance().getActiontitleBackground());

			composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			GridLayout clayout = new GridLayout(2, false);
			clayout.marginLeft = 6;
			clayout.marginRight = 6;
			clayout.marginTop = 3;
			clayout.marginBottom = 3;
			composite.setLayout(clayout);

			text = CTControls.getText(composite, SWT.BORDER);

			text.addTraverseListener(new TraverseListener() {

				@Override
				public void keyTraversed(TraverseEvent e) {
					if (e.detail == SWT.TRAVERSE_RETURN) {
						searchSelected();
					}
				}
			});
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

			CTButton bsearch = new CTButton(composite, SWT.NONE);
			bsearch.addSelectionListener(() -> {
				searchSelected();
			});

			bsearch.setText("Search");
		}

		scrolledComposite = new ScrolledComposite(this, SWT.NONE | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		clist = new CTComposite(scrolledComposite, SWT.NONE);
		clistlayout = new GridLayout();
		clist.setLayout(clistlayout);

		scrolledComposite.setContent(clist);
		scrolledComposite.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				updateLayout();
			}
		});
	}

	@Override
	public void selected(AppWindow appWindow) {

	}

	@Override
	public CTObject getObject() {
		return null;
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Search";
	}

	private void searchSelected() {
		String s = this.text.getText();
		search(s, 0, 50);
	}

	public void search(String searchitem, int start, int count) {
		new Thread(() -> {
			getDisplay().asyncExec(() -> {
				if (text != null) {
					text.setText(searchitem);
				}
			});

			List<ObjectVO> list = factory.search(searchitem, start, count);

			log.info("search got list " + list);
			handleResponse(list);
		}).start();
	}

	private void handleResponse(List<ObjectVO> list) {
		if (list != null) {
			getDisplay().asyncExec(() -> {
				addRows(list);
				updateLayout();
				clist.pack();
			});
		}
	}

	private void addRows(List<ObjectVO> list) {
		Control[] cs = clist.getChildren();
		for (Control control : cs) {
			control.dispose();
		}

		for (ObjectVO vo : list) {
			factory.addRow(vo.getId(), clist);
		}

		updateLayout();
	}

	@Override
	public MenuItem createMenu(Menu menu) {
		MenuItem miscripts = new MenuItem(menu, SWT.CASCADE);
		miscripts.setText("Search");

		Menu mscript = new Menu(miscripts);
		miscripts.setMenu(mscript);

		return miscripts;
	}

	private void updateLayout() {
		if (scrolledComposite != null) {
			int w = scrolledComposite.getClientArea().width;
			int count = w / COLUMN_WIDTH;
			if (count < 1) {
				count = 1;
			}

			clistlayout.numColumns = count;
			log.info("columncount " + clistlayout.numColumns + " w:" + w);

			for (Control c : clist.getChildren()) {
				c.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			}

			scrolledComposite.setMinSize(w, clist.computeSize(w, SWT.DEFAULT).y);
		}
	}

	public static interface CTSearchResultFactory {

		void addRow(String id, Composite clist);

		List<ObjectVO> search(String s, int start, int count);

	}
}
