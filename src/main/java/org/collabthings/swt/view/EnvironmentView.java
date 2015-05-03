package org.collabthings.swt.view;

import java.util.Set;

import org.collabthings.model.LOTEnvironment;
import org.collabthings.swt.AppWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class EnvironmentView extends Composite {

	private LOTEnvironment environment;
	private AppWindow window;

	public EnvironmentView(Composite parent, AppWindow window,
			LOTEnvironment environment) {
		super(parent, SWT.NONE);

		this.window = window;
		this.environment = environment;
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.fill = true;
		setLayout(rowLayout);

		addScripts();
		addTools();
		addParameters();
	}

	private void addParameters() {
		TitleComposite params = new TitleComposite(this, "PARAMETERS");

		if (environment != null) {
			Set<String> ps = environment.getParameters();
			for (String name : ps) {
				addParameterEditor(params, name);
			}
		}
	}

	private void addParameterEditor(TitleComposite params, String name) {
		String value = environment != null ? ""
				+ environment.getParameter(name) : "value";
		Composite c = new Composite(params, SWT.NONE);
		c.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_c = new GridLayout(2, false);
		gl_c.verticalSpacing = 1;
		gl_c.horizontalSpacing = 1;
		c.setLayout(gl_c);

		Label l = new Label(c, SWT.NONE);
		l.setText(name);

		if (value.length() > 20) {
			Button b = new Button(c, SWT.NONE);
			b.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
					1));
			b.setText("edit");
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					String nvalue = window.openValueEditorDialog(name, value);
					environment.setParameter(name, nvalue);
				}
			});
		} else {
			Text t = new Text(c, SWT.NONE);
			t.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
					1));
			t.setText("" + value);

			t.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent arg0) {
					environment.setParameter(name, t.getText());
				}
			});
		}
	}

	private void addTools() {
		TitleComposite ctools = new TitleComposite(this, "TOOLS");
		GridLayout gl_c = new GridLayout(2, false);
		gl_c.verticalSpacing = 1;
		gl_c.horizontalSpacing = 1;
		ctools.setLayout(gl_c);

		if (environment != null) {
			Set<String> tools = environment.getTools();
			for (String string : tools) {
				Composite ctool = new Composite(ctools, SWT.NONE);
				GridLayout gl_ctool = new GridLayout();
				gl_ctool.numColumns = 4;
				ctool.setLayout(gl_ctool);

				Text tname = new Text(ctool, SWT.NONE);
				GridData gd_tname = new GridData(SWT.LEFT, SWT.CENTER, false,
						false, 1, 1);
				gd_tname.widthHint = 146;
				tname.setLayoutData(gd_tname);
				tname.setText(string);
				tname.setEditable(true);

				Button btnrenametool = new Button(ctool, SWT.NONE);
				/*
				 * btnrenametool.addSelectionListener(new SelectionAdapter() {
				 * public void widgetSelected(SelectionEvent arg0) {
				 * environment.renametool(string, tname.getText()); } });
				 * btnrenametool.setText("rename");
				 * 
				 * Button btnopentool = new Button(ctool, SWT.NONE);
				 * btnopentool.addSelectionListener(new SelectionAdapter() {
				 * public void widgetSelected(SelectionEvent arg0) {
				 * window.viewtool(environment.gettool(string)); } });
				 * 
				 * btnopentool.setText("open");
				 * 
				 * Button btndeletetool = new Button(ctool, SWT.NONE);
				 * btndeletetool.addSelectionListener(new SelectionAdapter() {
				 * 
				 * @Override public void widgetSelected(SelectionEvent arg0) {
				 * environment.deletetool(string); } });
				 * btndeletetool.setText("delete");
				 */
			}
		}
	}

	private void addScripts() {
		Composite cscripts = new TitleComposite(this, "SCRIPTS");
		Composite cscriptslist = new Composite(cscripts, SWT.NONE);
		cscriptslist.setLayout(new FillLayout(SWT.VERTICAL));
		cscriptslist.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		if (environment != null) {
			Set<String> scripts = environment.getScripts();
			for (String string : scripts) {
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
		}
	}
}