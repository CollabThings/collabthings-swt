package org.collabthings.swt.view;

import java.util.LinkedList;
import java.util.List;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.view.SearchView.CTSearchResultFactory;
import org.collabthings.util.LLog;
import org.eclipse.swt.widgets.Composite;

import waazdoh.client.WClient;
import waazdoh.common.vo.UserVO;

public class UsersSearchView {
	private LLog log = LLog.getLogger(this);
	private SearchView search;

	public UsersSearchView(Composite c, LOTApp app, AppWindow window) {
		this(c, app, window, false);
	}

	public UsersSearchView(Composite c, LOTApp app, AppWindow window, boolean hidesearchbox) {
		this.search = new SearchView(c, app, window, new CTSearchResultFactory() {
			@Override
			public List<String> search(String s, int start, int count) {
				if (s == null || s.length() == 0) {
					s = "user";
				}

				WClient client = app.getLClient().getClient();
				List<String> ret = new LinkedList<>();
				List<UserVO> list = client.searchUsers(s, 50);
				for (UserVO userVO : list) {
					ret.add(userVO.getUserid());
				}
				return ret;
			}

			@Override
			public void addRow(String id, Composite clist) {
				UserVO user = app.getLClient().getService().getUsers().getUser(id);
				new UserSmallView(clist, app, window, user);
			}
		});

	}

	public SearchView getView() {
		return search;
	}

	public void search(String searchitem, int start, int count) {
		this.search.search(searchitem, start, count);
	}
}
