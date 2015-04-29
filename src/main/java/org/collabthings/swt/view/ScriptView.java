package org.collabthings.swt.view;

import org.collabthings.model.LOTScript;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.app.LOTApp;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

public class ScriptView extends Composite implements LOTAppControl {

	private AppWindow window;
	private Text scripttext;
	private LOTScript script;
	private Text bottomtext;
	private String shouldsave;
	private Thread savingthread;

	public ScriptView(Composite c, LOTApp app, AppWindow appWindow, LOTScript script) {
		super(c, SWT.NONE);
		this.window = appWindow;
		setLayout(new GridLayout(1, false));
		this.script = script;

		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		GridData gd_sashForm = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_sashForm.heightHint = 200;
		sashForm.setLayoutData(gd_sashForm);

		scripttext = new Text(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL
				| SWT.MULTI);
		scripttext.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				key(arg0);
			}
		});

		scripttext.setText("" + script.getScript());

		bottomtext = new Text(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL
				| SWT.MULTI);
		sashForm.setWeights(new int[] { 3, 1 });
	}

	protected synchronized void key(KeyEvent arg0) {
		startSave();
	}

	private void startSave() {
		shouldsave = this.scripttext.getText();
		if (savingthread == null) {
			savingthread = new Thread(() -> {
				synchronized (savingthread) {
					do {
						shouldsave = null;
						try {
							savingthread.wait(500);
						} catch (Exception e) {
						}
						save(shouldsave);
					} while (shouldsave != null);
					savingthread = null;
				}
			});
			savingthread.start();
		}
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Script: " + script;
	}

	private void save(String sscripttext) {
		if (sscripttext != null
				&& (script.getScript() == null || !script.getScript().equals(sscripttext))) {
			getDisplay().asyncExec(() -> {
				script.setScript(sscripttext);
				boolean b = script.isOK();
				if (!b) {
					String error = script.getError();
					bottomtext.append("ERROR " + error + "\n");
				} else {
					bottomtext.append("OK\n");
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

		return miscripts;
	}
}
