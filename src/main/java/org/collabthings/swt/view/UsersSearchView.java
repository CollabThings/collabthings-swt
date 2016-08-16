package org.collabthings.swt.view;

import java.util.List;

import org.collabthings.swt.AppWindow;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.view.UserSearchView.CTUserSearchResultFactory;
import org.collabthings.util.LLog;
import org.eclipse.swt.widgets.Composite;

import waazdoh.client.WClient;
import waazdoh.common.vo.UserVO;

public class UsersSearchView {
	private LLog log = LLog.getLogger(this);
	private UserSearchView search;

	public UsersSearchView(Composite c, LOTApp app, AppWindow window) {
		this(c, app, window, false);
	}

	public UsersSearchView(Composite c, LOTApp app, AppWindow window, boolean hidesearchbox) {
		this.search = new UserSearchView(c, app, window, new CTUserSearchResultFactory() {
			@Override
			public List<UserVO> search(String s, int start, int count) {
				if (s == null || s.length() == 0) {
					s = "user";
				}

				WClient client = app.getLClient().getClient();
				List<UserVO> list = client.searchUsers(s, 50);
				return list;
			}

			@Override
			public void addRow(String id, Composite clist) {
				UserVO user = app.getLClient().getService().getUsers().getUser(id);
				new UserSmallView(clist, app, window, user);
			}
		});

	}

	public UserSearchView getView() {
		return search;
	}

	public void search(String searchitem, int start, int count) {
		this.search.search(searchitem, start, count);
	}
}
