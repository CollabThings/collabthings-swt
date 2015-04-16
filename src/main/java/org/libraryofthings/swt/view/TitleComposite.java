package org.libraryofthings.swt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class TitleComposite extends Composite {

	public TitleComposite(Composite parent, String title) {
		super(parent, SWT.NONE);
		this.setLayout(new GridLayout(1, false));

		Composite ctitle = new Composite(this, SWT.NONE);
		ctitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		ctitle.setLayout(new GridLayout(1, false));
		ctitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblScripts = new Label(ctitle, SWT.NONE);
		lblScripts.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblScripts.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		lblScripts.setText(title);
	}
}
