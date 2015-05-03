package org.collabthings.swt.view;

import java.util.Date;

import org.collabthings.model.LOTScript;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.app.LOTApp;
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

public class ScriptView extends Composite implements LOTAppControl {

	private AppWindow window;
	private Text scripttext;
	private LOTScript script;
	private Text bottomtext;

	private LOTApp app;

	public ScriptView(Composite c, LOTApp app, AppWindow appWindow,
			LOTScript script) {
		super(c, SWT.NONE);
		this.window = appWindow;
		this.app = app;

		setLayout(new GridLayout(1, false));
		this.script = script;

		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		GridData gd_sashForm = new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1);
		gd_sashForm.heightHint = 200;
		sashForm.setLayoutData(gd_sashForm);

		scripttext = new Text(sashForm, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		scripttext.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				key(arg0);
			}
		});

		scripttext.setText("" + script.getScript());

		bottomtext = new Text(sashForm, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		sashForm.setWeights(new int[] { 3, 1 });
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
		return "Script: " + script;
	}

	private void save(String sscripttext) {
		if (sscripttext != null
				&& (script.getScript() == null || !script.getScript().equals(
						sscripttext))) {

			LOTScript s = this.app.getObjectFactory().getScript();
			s.setScript(sscripttext);
			getDisplay().asyncExec(() -> {
				if (s.isOK()) {
					script.setScript(sscripttext);
					boolean b = script.isOK();
					bottomtext.append("OK " + new Date() + "\n");
				} else {
					String error = script.getError();
					bottomtext.append("ERROR " + error + "\n");
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