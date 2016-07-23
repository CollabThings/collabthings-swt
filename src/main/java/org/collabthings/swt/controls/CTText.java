package org.collabthings.swt.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class CTText extends CTComposite {
	private Text text;

	public CTText(Composite arg0, int arg1) {
		super(arg0, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		text = new Text(this, arg1);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_BLACK));
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_composite.heightHint = 1;
		composite.setLayoutData(gd_composite);
	}

	public void setEditable(boolean arg0) {
		text.setEditable(arg0);
		// text.setBackground(SWTResourceManager.getTextBackground());
	}

	public void setText(String stext) {
		this.text.setText(stext);
	}

	public String getText() {
		return text.getText();
	}

	public void append(String string) {
		text.append(string);
	}

}
