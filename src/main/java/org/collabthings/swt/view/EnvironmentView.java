package org.collabthings.swt.view;

import java.util.Set;

import org.collabthings.model.CTEnvironment;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTSWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class EnvironmentView extends Composite {

	private CTEnvironment environment;
	private AppWindow window;
	private Composite cscriptslist;
	private TitleComposite ctools;
	private Composite ctoolslist;

	public EnvironmentView(Composite parent, AppWindow window,
			CTEnvironment environment) {
		super(parent, SWT.NONE);

		this.window = window;
		this.environment = environment;

		GridLayout gridLayout_1 = new GridLayout(1, false);
		setLayout(gridLayout_1);
		LOTSWT.setDefaults(gridLayout_1);

		addScripts();
		addTools();
		addParameters();
	}

	private void addParameters() {
		TitleComposite params = new TitleComposite(this, "PARAMETERS");
		params.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		GridLayout gridLayout = (GridLayout) params.getLayout();
		LOTSWT.setDefaults(gridLayout);

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
		GridLayout gl_c = new GridLayout(1, false);
		LOTSWT.setDefaults(gl_c);
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
		ctools = new TitleComposite(this, "TOOLS");
		ctools.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		GridLayout gridLayout = (GridLayout) ctools.getLayout();
		LOTSWT.setDefaults(gridLayout);

		ctoolslist = new Composite(ctools, SWT.NONE);
		ctoolslist.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1,
				1));
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.marginHeight = 0;
		fillLayout.marginWidth = 0;
		ctoolslist.setLayout(fillLayout);

		if (environment != null) {
			ctools.addButton(
					"+",
					() -> {
						String toolname = "tool"
								+ environment.getTools().size();
						this.environment.addTool(toolname, this.window.getApp()
								.getLClient().getObjectFactory().getTool());
						addTool(toolname);
						getParent().layout();
					});

			Set<String> tools = environment.getTools();
			for (String string : tools) {
				addTool(string);
			}
		}
	}

	private void addTool(String string) {
		Composite ctool = new Composite(ctoolslist, SWT.NONE);
		GridLayout gl_ctool = new GridLayout();
		gl_ctool.numColumns = 4;
		ctool.setLayout(gl_ctool);

		Text tname = new Text(ctool, SWT.NONE);
		GridData gd_tname = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_tname.widthHint = 146;
		tname.setLayoutData(gd_tname);
		tname.setText(string);
		tname.setEditable(true);

		Button btnrenametool = new Button(ctool, SWT.NONE);
		btnrenametool.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				environment.renameTool(string, tname.getText());
			}
		});
		btnrenametool.setText("rename");

		Button btnopentool = new Button(ctool, SWT.NONE);
		btnopentool.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				window.viewTool(environment.getTool(string));
			}
		});

		btnopentool.setText("open");

		Button btndeletetool = new Button(ctool, SWT.NONE);
		btndeletetool.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				environment.deleteTool(string);
			}
		});
		btndeletetool.setText("delete");
	}

	private void addScripts() {
		TitleComposite cscripts = new TitleComposite(this, "SCRIPTS");
		cscripts.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		GridLayout gridLayout = (GridLayout) cscripts.getLayout();
		LOTSWT.setDefaults(gridLayout);

		cscriptslist = new Composite(cscripts, SWT.NONE);

		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.marginHeight = 0;
		fillLayout.marginWidth = 0;

		cscriptslist.setLayout(fillLayout);
		cscriptslist.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		if (environment != null) {
			cscripts.addButton(
					"+",
					() -> {
						String scriptname = "script"
								+ environment.getScripts().size();
						this.environment.addScript(scriptname, this.window
								.getApp().getLClient().getObjectFactory()
								.getScript());
						addScript(scriptname);
						getParent().layout();
					});

			Set<String> scripts = environment.getScripts();
			for (String string : scripts) {
				addScript(string);
			}
		}
	}

	private void addScript(String string) {
		Composite cscript = new Composite(cscriptslist, SWT.NONE);
		GridLayout gl_cscript = new GridLayout();

		gl_cscript.numColumns = 4;
		cscript.setLayout(gl_cscript);

		Text tname = new Text(cscript, SWT.NONE);
		GridData gd_tname = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
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
