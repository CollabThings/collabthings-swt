package org.collabthings.swt.view;

import java.util.Date;

import org.collabthings.model.CTObject;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class SCADView extends CTComposite implements CTAppControl {

	private AppWindow window;
	private CTText scripttext;
	private CTOpenSCAD scad;
	private CTText bottomtext;

	private Composite composite;
	private Composite ctools;

	public SCADView(Composite c, LOTApp app, AppWindow window2, CTOpenSCAD o) {
		this(c, app, window2, o, true);
	}

	public SCADView(Composite c, LOTApp app, AppWindow nwindow, CTOpenSCAD o, boolean b) {
		super(c, SWT.NONE);
		this.window = nwindow;

		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);
		setLayout(gridLayout);
		this.scad = o;

		if (b) {
			ctools = new CTComposite(this, SWT.NONE);
			ctools.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			ctools.setLayout(gridLayout);
			ctools.setBackground(SWTResourceManager.getActiontitleBackground());
			CTButton bsave = new CTButton(ctools, SWT.NONE);
			bsave.setText("Save");
			bsave.addSelectionListener(() -> save());
		}

		Composite left;

		if (b) {
			SashForm sashForm_1 = new SashForm(this, SWT.NONE);
			left = sashForm_1;
		} else {
			left = new CTComposite(this, SWT.NONE);
			GridLayout leftlayout = new GridLayout();
			LOTSWT.setDefaults(leftlayout);
			left.setLayout(leftlayout);
		}

		left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		int textstyle = SWT.BORDER | SWT.CANCEL | SWT.MULTI | SWT.V_SCROLL;
		if (b) {
			textstyle = textstyle | SWT.H_SCROLL;
		}
		scripttext = new CTText(left, textstyle);
		scripttext.setFont(SWTResourceManager.getDefaultFont());

		if (!b) {
			scripttext.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		}

		scripttext.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				key(arg0);
			}
		});

		scripttext.setText("" + scad.getScript());

		if (b) {
			SashForm sashForm = new SashForm(left, SWT.VERTICAL);

			composite = new CTComposite(sashForm, SWT.NONE);
			composite.setLayout(gridLayout);

			bottomtext = new CTText(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
			sashForm.setWeights(new int[] { 1, 1 });

			new Thread(() -> {
				int hash = 0;
				while (!window.getMainView().isDisposed()) {
					if (scad.hashCode() != hash) {
						hash = scad.hashCode();
						setModel();
					}

					synchronized (this) {
						try {
							this.wait(200);
						} catch (Exception e) {
							window.showError("Interrupted", e);
						}
					}
				}
			}).start();
		}
	}

	@Override
	public CTObject getObject() {
		return scad;
	}

	private void setModel() {
		window.getMainView().setViewedModel(scad);
	}

	protected synchronized void key(KeyEvent arg0) {

	}

	public synchronized void save() {
		String sstring = this.scripttext.getText();
		doSave(sstring);
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Script: " + scad;
	}

	private void doSave(String sscripttext) {
		if (sscripttext != null && (scad.getScript() == null || !scad.getScript().equals(sscripttext))) {

			String oldscript = scad.getScript();
			getDisplay().asyncExec(() -> {
				scad.setScript(sscripttext);
				if (scad.getModel() != null && scad.isOK()) {
					if (bottomtext != null) {
						bottomtext.append("OK " + new Date() + "\n");
					}
				} else {
					String error = scad.getError();
					if (bottomtext != null) {
						bottomtext.append("ERROR " + error + "\n");
					} else {
						window.showError("ERROR " + error);
					}

					scad.setScript(oldscript);

				}
			});
		}
	}

	@Override
	public void selected(AppWindow appWindow) {
		setModel();
	}

	@Override
	public MenuItem createMenu(Menu menu) {
		MenuItem miscripts = new MenuItem(menu, SWT.CASCADE);
		miscripts.setText("Script");

		Menu mscript = new Menu(miscripts);
		miscripts.setMenu(mscript);

		MenuItem msave = new MenuItem(mscript, SWT.NONE);
		msave.setText("Save");
		msave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				save();
			}
		});

		return miscripts;
	}
}
