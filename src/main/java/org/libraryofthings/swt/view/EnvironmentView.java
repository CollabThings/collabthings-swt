package org.libraryofthings.swt.view;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.swt.AppWindow;

public class EnvironmentView extends Composite {

	private LOTEnvironment environment;
	private AppWindow window;

	public EnvironmentView(Composite parent, AppWindow window,
			LOTEnvironment environment) {
		super(parent, SWT.NONE);

		this.window = window;
		this.environment = environment;
		setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite cscripts = new Composite(this, SWT.NONE);

		cscripts.setLayout(new GridLayout(1, false));

		Composite cscriptstitle = new Composite(cscripts, SWT.NONE);
		cscriptstitle.setLayout(new GridLayout(1, false));
		cscriptstitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		cscriptstitle.setBounds(0, 0, 64, 64);

		Label lblScripts = new Label(cscriptstitle, SWT.NONE);
		lblScripts.setText("SCRIPTS");

		Composite cscriptslist = new Composite(cscripts, SWT.NONE);
		cscriptslist.setLayout(new FillLayout(SWT.VERTICAL));
		cscriptslist.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		cscriptslist.setBounds(0, 0, 64, 64);

		if (environment != null) {
			Set<String> scripts = environment.getScripts();
			for (String string : scripts) {
				// String string = "test";

				Composite cscript = new Composite(cscriptslist, SWT.NONE);
				GridLayout gl_cscript = new GridLayout();
				gl_cscript.numColumns = 4;
				cscript.setLayout(gl_cscript);

				Text tname = new Text(cscript, SWT.NONE);
				GridData gd_tname = new GridData(SWT.LEFT, SWT.CENTER, false,
						false, 1, 1);
				gd_tname.widthHint = 146;
				tname.setLayoutData(gd_tname);
				tname.setText(string);
				tname.setEditable(true);

				Button btnrenamescript = new Button(cscript, SWT.NONE);
				btnrenamescript.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent arg0) {
						environment.renameScript(string, tname.getText());
					}
				});
				btnrenamescript.setText("rename");

				Button btnopenscript = new Button(cscript, SWT.NONE);
				btnopenscript.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent arg0) {
						window.viewScript(environment.getScript(string));
					}
				});

				btnopenscript.setText("open");

				Button btndeletescript = new Button(cscript, SWT.NONE);
				btndeletescript.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						environment.deleteScript(string);
					}
				});
				btndeletescript.setText("delete");
			}
		} else {

		}
	}
}
