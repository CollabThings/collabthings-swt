package org.collabthings.swt.view;

import org.collabthings.model.CTPart;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTTabFolder;
import org.collabthings.swt.view.parteditor.PartEditor;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class CTMainView extends CTComposite implements LOTAppControl {

	private CTTabFolder tabfolder;
	private AppWindow window;
	private LLog log = LLog.getLogger(this);
	private LOTApp app;
	private GLSceneView view;

	public CTMainView(Composite parent, LOTApp app, AppWindow window) {
		super(parent, SWT.NONE);

		this.app = app;
		this.window = window;
		setLayout(new GridLayout(1, false));

		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sashForm.setLocation(0, 0);
		tabfolder = new CTTabFolder(sashForm, SWT.NONE);

		Composite right = new CTComposite(sashForm, SWT.NONE);
		sashForm.setWeights(new int[] { 1, 1 });
		right.setLayout(new GridLayout(1, false));

		view = new GLSceneView(right);
		view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tabfolder.addSelectionListener(() -> {
			tabSelected();
		});
	}

	private void tabSelected() {
		Control control = tabfolder.getSelection().getControl();

		if (control instanceof LOTAppControl) {
			LOTAppControl v = (LOTAppControl) control;
			log.info("selected " + v);

			v.selected(window);
		} else {
			window.showError(
					"Selected " + control + " that is not a LOTAppControl. Name:" + tabfolder.getSelection().getText());
		}
	}

	@Override
	public MenuItem createMenu(Menu menu) {
		return null;
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Main";
	}

	@Override
	public void selected(AppWindow appWindow) {

	}

	public void newPart() {
		try {
			CTPart p = app.newPart();
			PartEditor pview = new PartEditor(tabfolder.getComposite(), app, window, p, view);
			addTab("part " + p, pview, p);
		} catch (Exception e) {
			window.showError("newPart", e);
		}
	}

	public void setViewedPart(CTPart part) {
		view.setPart(part);
	}

	public void viewPart(CTPart part) {
		if (part != null) {
			getDisplay().asyncExec(() -> {
				try {
					PartEditor pv = new PartEditor(tabfolder.getComposite(), app, window, part, view);
					addTab("" + part.getName(), pv, part);
				} catch (Exception e) {
					window.showError("viewPart", e);
				}
			});
		} else {
			log.info("ERROR part null");
		}
	}

	private void addTab(String name, LOTAppControl c, Object data) {
		Control control = c.getControl();
		control.setBackground(SWTResourceManager.getControlBg());

		tabfolder.addTab(name, control, data);

		tabfolder.addCloseListener(name, () -> {
			control.dispose();
		});
	}
}
