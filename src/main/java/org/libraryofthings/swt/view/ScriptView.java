package org.libraryofthings.swt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.LOTAppControl;
import org.libraryofthings.swt.app.LOTApp;

public class ScriptView extends Composite implements LOTAppControl {

	private AppWindow window;
	private Text scripttext;
	private LOTScript script;
	private Text bottomtext;

	public ScriptView(Composite c, LOTApp app, AppWindow appWindow,
			LOTScript script) {
		super(c, SWT.NONE);
		this.window = appWindow;
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

	protected void key(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	private void save() {
		String sscripttext = scripttext.getText();
		if (script.getScript() == null
				|| !script.getScript().equals(sscripttext)) {
			boolean b = script.setScript(sscripttext);
			if (!b) {
				String error = script.getError();
				bottomtext.append("" + error + "\n");
			} else {
				bottomtext.append("OK\n");
			}
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
