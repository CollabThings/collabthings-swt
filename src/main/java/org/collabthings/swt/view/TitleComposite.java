package org.collabthings.swt.view;

import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class TitleComposite extends CTComposite {

	private Composite ctitle;

	public TitleComposite(Composite parent, String title) {
		super(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);
		this.setLayout(gridLayout);

		ctitle = new CTComposite(this, SWT.NONE);
		GridLayout gl_ctitle = new GridLayout(10, false);
		LOTSWT.setDefaults(gl_ctitle);

		ctitle.setLayout(gl_ctitle);
		ctitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		CTLabel lblScripts = new CTLabel(ctitle, SWT.NONE);
		lblScripts.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
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
