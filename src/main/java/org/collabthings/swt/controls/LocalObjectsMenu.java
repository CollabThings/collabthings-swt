package org.collabthings.swt.controls;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.collabthings.LLog;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.app.LOTApp;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import waazdoh.client.model.User;
import waazdoh.client.storage.BeanStorage;
import waazdoh.common.MStringID;
import waazdoh.common.UserID;
import waazdoh.common.WData;

public class LocalObjectsMenu {
	private static final String MAX_LOCALMENU_OBJECTS = "lot.gui.local.menuobjects.max";

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
		LOTApp app = appwindow.getApp();
		BeanStorage storage = app.getBeanStorage();
		String search = "" + Calendar.getInstance().get(Calendar.YEAR);
		Iterable<MStringID> ids = storage.getLocalSetIDs(search);

		MenuItem[] items = menulocal.getItems();
		for (MenuItem menuItem : items) {
			menuItem.dispose();
		}

		int count = 0;
		for (MStringID id : ids) {
			WData bean = storage.getBean(id);
			// modified -value should be in every bean.
			if (bean != null) {
				if (openobjecthandlers.get(bean.getName()) != null
						&& bean.getValue("modified") != null) {
					addObjectMenu(id, bean);

					if (count++ > app.getLClient().getPreferences()
							.getInteger(MAX_LOCALMENU_OBJECTS, 40)) {
						MenuItem tbci = new MenuItem(menulocal, SWT.NONE);
						tbci.setText("...");
						break;
					}
				} else {
					log.info("Not showing " + bean.getName() + " "
							+ bean.getValue("name") + " in menu");
				}
			}
		}
	}

	private void addObjectMenu(MStringID id, WData bean) {
		MenuItem i = new MenuItem(menulocal, SWT.NONE);
		i.setText(getLocalBeanInfo(bean));
		i.setData(id);
		i.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				openLocal(id);
			}
		});
	}

	private String getLocalBeanInfo(WData bean) {
		StringBuilder sb = new StringBuilder();
		String userid = bean.getValue("creator");
		if (userid != null) {
			User user = appwindow.getApp().getLClient().getClient()
					.getUser(new UserID(userid));
			if (user != null) {
				long modified = bean.getLongValue("modified");
				String name = bean.getValue("name");

				sb.append("" + name + " by " + user.getName() + " at "
						+ modified);
			} else {
				sb.append("User not found");
			}
		} else {
			sb.append("Userid not found");
		}

		return sb.toString();
	}

	protected void openLocal(MStringID id) {
		WData b = appwindow.getApp().getBeanStorage().getBean(id);
		log.info("opening " + b);

		OpenObjectHandler h = openobjecthandlers.get(b.getName());
		if (h != null) {
			h.open(b);
		} else {
			log.info("No handler for " + b.getName());
		}
	}

	public interface OpenObjectHandler {
		void open(WData b);
	}
}
