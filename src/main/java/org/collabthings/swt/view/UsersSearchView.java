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
import org.collabthings.datamodel.UserVO;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.view.UserSearchView.CTUserSearchResultFactory;
import org.collabthings.util.LLog;
import org.eclipse.swt.widgets.Composite;

public class UsersSearchView {
	private LLog log = LLog.getLogger(this);
	private UserSearchView search;

	public UsersSearchView(Composite c, CTApp app, AppWindow window) {
		this(c, app, window, false);
	}

	public UsersSearchView(Composite c, CTApp app, AppWindow window, boolean hidesearchbox) {
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
