package org.collabthings.swt.view;

import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TitleComposite extends Composite {

	private Composite ctitle;

	public TitleComposite(Composite parent, String title) {
		super(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);
		this.setLayout(gridLayout);

		ctitle = new Composite(this, SWT.NONE);
		ctitle.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		GridLayout gl_ctitle = new GridLayout(10, false);
		LOTSWT.setDefaults(gl_ctitle);

		ctitle.setLayout(gl_ctitle);
		ctitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Label lblScripts = new Label(ctitle, SWT.NONE);
		lblScripts.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		lblScripts.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblScripts.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		lblScripts.setText(title);
	}

	public void addButton(String string, ButtonAction action) {
		Button b = new Button(ctitle, SWT.None);
		b.setText(string);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				action.action();
			}
		});
	}
	
	public interface ButtonAction {
		public void action();
	}
}
