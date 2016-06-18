package org.collabthings.swt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ValueEditorDialog {

	private String value;
	private Text text;
	private String name;

	public ValueEditorDialog(Shell parentShell, String name, String value2) {
		this.name = name;
		this.value = value2;
	}

	public String getValue() {
		return value;
	}

	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, false));

		Label lvaluename = new Label(c, SWT.NONE);
		lvaluename.setBounds(0, 0, 55, 15);
		lvaluename.setText(name);

		text = new Text(c, SWT.BORDER | SWT.MULTI);
		text.setText(value);

		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				value = text.getText();
			}
		});
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		text.setBounds(0, 0, 76, 21);

		return c;
	}

	public void open() {
		// TODO Auto-generated method stub
	}
}
