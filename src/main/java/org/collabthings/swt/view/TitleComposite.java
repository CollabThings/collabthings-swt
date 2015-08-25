package org.collabthings.swt.view;

import org.collabthings.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TitleComposite extends Composite {

	public TitleComposite(Composite parent, String title) {
		super(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		this.setLayout(gridLayout);

		Composite ctitle = new Composite(this, SWT.NONE);
		ctitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		GridLayout gl_ctitle = new GridLayout(1, false);
		gl_ctitle.verticalSpacing = 1;
		ctitle.setLayout(gl_ctitle);
		ctitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblScripts = new Label(ctitle, SWT.NONE);
		lblScripts.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblScripts.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		lblScripts.setText(title);
	}
}
