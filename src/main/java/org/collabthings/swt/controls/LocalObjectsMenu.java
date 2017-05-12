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
package org.collabthings.swt.controls;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.collabthings.app.CTApp;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.app.CTSelectionAdapter;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import waazdoh.client.model.User;
import waazdoh.common.BeanStorage;
import waazdoh.common.WObject;
import waazdoh.common.WStringID;
import waazdoh.common.WUserID;

public class LocalObjectsMenu {
	private static final String MAX_LOCALMENU_OBJECTS = "ct.gui.local.menuobjects.max";

	private LLog log = LLog.getLogger(this);
	private AppWindow appwindow;
	private Menu menulocal;

	private Map<String, OpenObjectHandler> openobjecthandlers = new HashMap<String, OpenObjectHandler>();

	public LocalObjectsMenu(AppWindow appWindow, Menu menulocal) {
		this.appwindow = appWindow;
		this.menulocal = menulocal;

		menulocal.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent arg0) {
				localMenuShown();
			}
		});
	}

	public void addObjectHandler(String name, OpenObjectHandler h) {
		openobjecthandlers.put(name, h);
	}

	protected void localMenuShown() {
		log.info("Local menu shown");
		CTApp app = appwindow.getApp();
		BeanStorage storage = app.getBeanStorage();
		String search = "" + Calendar.getInstance().get(Calendar.YEAR);
		Iterable<WStringID> ids = storage.getLocalSetIDs(search);

		MenuItem[] items = menulocal.getItems();
		for (MenuItem menuItem : items) {
			menuItem.dispose();
		}

		int count = 0;
		for (WStringID id : ids) {
			WObject bean = storage.getBean(id);
			// modified -value should be in every bean.
			if (bean != null) {
				if (openobjecthandlers.get(bean.getType()) != null) {
					if (bean.getValue("modified") != null) {

						MenuItem i = addObjectMenu(id, bean);
						if (i != null
								&& count++ > app.getLClient().getPreferences().getInteger(MAX_LOCALMENU_OBJECTS, 40)) {
							MenuItem tbci = new MenuItem(menulocal, SWT.NONE);
							tbci.setText("...");
							break;
						}
					} else {
						log.info("Not showing " + bean.getType() + " in menu. modified missing " + bean);
					}
				} else {
					log.info("Bean not found " + id);
				}
			}
		}
	}

	private MenuItem addObjectMenu(WStringID id, WObject bean) {
		String localBeanInfo = getLocalBeanInfo(bean);
		if (localBeanInfo != null) {
			MenuItem i = new MenuItem(menulocal, SWT.NONE);
			i.setText(localBeanInfo);
			i.setData(id);
			i.addSelectionListener(new CTSelectionAdapter(e -> openLocal(id)));
			return i;
		} else

		{
			return null;
		}
	}

	private String getLocalBeanInfo(WObject bean) {
		StringBuilder sb = new StringBuilder();
		String userid = bean.getValue("creator");
		if (userid != null) {
			User user = appwindow.getApp().getLClient().getClient().getUser(new WUserID(userid));

			long modified = bean.getLongValue("modified");
			WObject content = bean.get("content");
			String name = content.getValue("name");

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(modified);

			sb.append("" + name + " by " + (user != null ? user.getName() : "Unknown") + " at " + cal.getTime());
			return sb.toString();
		}
		log.info("User not found " + userid);
		return null;
	}

	protected void openLocal(WStringID id) {
		WObject b = appwindow.getApp().getBeanStorage().getBean(id);
		log.info("opening " + b);

		OpenObjectHandler h = openobjecthandlers.get(b.getType());
		if (h != null) {
			h.open(b);
		} else {
			log.info("No handler for " + b.getType());
		}
	}

	public interface OpenObjectHandler {
		void open(WObject b);
	}
}
