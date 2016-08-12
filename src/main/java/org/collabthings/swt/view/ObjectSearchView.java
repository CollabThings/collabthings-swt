package org.collabthings.swt.view;

import java.util.List;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.view.SearchView.CTSearchResultFactory;
import org.collabthings.util.LLog;
import org.eclipse.swt.widgets.Composite;

import waazdoh.client.WClient;
import waazdoh.common.vo.ObjectVO;

public class ObjectSearchView {
	private LLog log = LLog.getLogger(this);

	private SearchView search;

	public ObjectSearchView(Composite composite, LOTApp app, AppWindow window) {
		search = new SearchView(composite, app, window, new CTSearchResultFactory() {

			@Override
			public List<String> search(String searchitem, int start, int count) {
				WClient client = app.getLClient().getClient();
				List<String> list = client.getObjects().search(searchitem, start, count);
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
