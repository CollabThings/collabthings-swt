package org.collabthings.swt.view;

import java.util.Date;

import org.collabthings.model.CTOpenSCAD;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTComposite;
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
import org.eclipse.swt.widgets.Text;

public class SCADView extends CTComposite implements LOTAppControl {

	private AppWindow window;
	private Text scripttext;
	private CTOpenSCAD scad;
	private Text bottomtext;

	private SashForm sashForm_1;
	private GLSceneView canvas;
	private Composite composite;

	public SCADView(final Composite c, final LOTApp app, final AppWindow appWindow, final CTOpenSCAD scad) {
		super(c, SWT.NONE);
		this.window = appWindow;

		setLayout(new GridLayout(1, false));
		this.scad = scad;

		sashForm_1 = new SashForm(this, SWT.NONE);
		sashForm_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		scripttext = new Text(sashForm_1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		scripttext.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				key(arg0);
			}
		});

		scripttext.setText("" + scad.getScript());

		SashForm sashForm = new SashForm(sashForm_1, SWT.VERTICAL);

		composite = new CTComposite(sashForm, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		canvas = new GLSceneView(composite);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		bottomtext = new Text(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		sashForm.setWeights(new int[] { 1, 1 });
		sashForm_1.setWeights(new int[] { 1, 1 });

		new Thread(() -> {
			int hash = 0;
			while (!canvas.isDisposed()) {
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

	private void setModel() {
		canvas.setModelView(scad);
	}

	protected synchronized void key(KeyEvent arg0) {

	}

	private synchronized void startSave() {
		String sstring = this.scripttext.getText();
		save(sstring);
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Script: " + scad;
	}

	private void save(String sscripttext) {
		if (sscripttext != null && (scad.getScript() == null || !scad.getScript().equals(sscripttext))) {

			String oldscript = scad.getScript();
			getDisplay().asyncExec(() -> {
				scad.setScript(sscripttext);
				if (scad.getModel() != null && scad.isOK()) {
					bottomtext.append("OK " + new Date() + "\n");
				} else {
					String error = scad.getError();
					bottomtext.append("ERROR " + error + "\n");
					scad.setScript(oldscript);
				}
			});
		}
	}

	@Override
	public void selected(AppWindow appWindow) {

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
				startSave();
			}
		});

		return miscripts;
	}
}
