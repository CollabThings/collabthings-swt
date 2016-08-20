package org.collabthings.swt.view;

import org.collabthings.model.CTModel;
import org.collabthings.model.CTObject;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
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

public class CTMainView extends CTComposite implements CTAppControl {

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
		right.setLayout(new GridLayout(1, false));

		view = new GLSceneView(right);
		view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sashForm.setWeights(new int[] { 150, 287 });

		tabfolder.addSelectionListener(() -> {
			tabSelected();
		});
	}

	@Override
	public CTObject getObject() {
		return null;
	}

	private void tabSelected() {
		CTAppControl v = getSelectedControl();
		log.info("selected " + v);

		v.selected(window);
	}

	private CTAppControl getSelectedControl() {
		Control control = tabfolder.getSelection().getControl();
		CTAppControl v = (CTAppControl) control;
		return v;
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
			getDisplay().asyncExec(() -> {
				PartEditor pview = new PartEditor(tabfolder.getComposite(), app, window, p, view);
				addTab("part " + p, pview, p);
			});
		} catch (Exception e) {
			window.showError("newPart", e);
		}
	}

	public void setViewedPart(CTPart part) {
		view.setPart(part);
	}

	public CTObject getCurrentObject() {
		return getSelectedControl().getObject();
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

	public void viewSCAD(CTOpenSCAD scad) {
		if (scad != null) {
			getDisplay().asyncExec(() -> {
				try {
					SCADView v = new SCADView(tabfolder.getComposite(), app, window, scad);
					addTab("" + scad.getName(), v, scad);
				} catch (Exception e) {
					window.showError("viewPart", e);
				}
			});
		} else {
			log.info("ERROR part null");
		}
	}

	public void viewBuilder(String name, CTPart p, CTPartBuilder o) {
		if (o != null) {
			getDisplay().asyncExec(() -> {
				try {
					PartBuilderView v = new PartBuilderView(tabfolder.getComposite(), app, window, p, o, view);
					addTab("" + name, v, o);
				} catch (Exception e) {
					window.showError("viewPart", e);
				}
			});
		}
	}

	private void addTab(String name, CTAppControl c, Object data) {
		Control control = c.getControl();
		control.setBackground(SWTResourceManager.getControlBg());

		tabfolder.addTab(name, control, data);

		tabfolder.addCloseListener(name, () -> {
			control.dispose();
		});
	}

}
