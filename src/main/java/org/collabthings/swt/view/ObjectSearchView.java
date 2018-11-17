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
import org.collabthings.core.WClient;
import org.collabthings.datamodel.ObjectVO;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
import org.collabthings.swt.view.SearchView.CTSearchResultFactory;
import org.collabthings.util.LLog;
import org.eclipse.swt.widgets.Composite;

public class ObjectSearchView {
	private LLog log = LLog.getLogger(this);

	private SearchView search;

	public ObjectSearchView(Composite composite, CTApp app, AppWindow window) {
		search = new SearchView(composite, app, window, new CTSearchResultFactory() {

			@Override
			public List<ObjectVO> search(String searchitem, int start, int count) {
				WClient client = app.getLClient().getClient();
				List<ObjectVO> list = client.getObjects().search(searchitem, start, count);
				return list;
			}

			@Override
			public void addRow(String id, Composite cresultlist) {
				try {
					new ObjectSmallView(cresultlist, app, window, id);
				} catch (ClassCastException e) {
					log.error(this, "addRow " + id, e);
					ObjectVO o = app.getLClient().getService().getObjects().read(id);
					log.info("failed object " + o.toObject());
				}
			}
		});

	}

	public CTAppControl getControl() {
		return search;
	}

	public void search(String searchitem, int start, int count) {
		this.search.search(searchitem, start, count);
	}

	public void setLayoutData(Object ldata) {
		search.setLayoutData(ldata);
	}

}
