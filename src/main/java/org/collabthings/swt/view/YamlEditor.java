package org.collabthings.swt.view;

import org.collabthings.model.LOTObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import waazdoh.common.WObject;

public class YamlEditor extends Composite {
	private LOTObject o;
	private Text text_1;
	private Text error;
	private Button btnSave;

	public YamlEditor(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		Composite top = new Composite(this, SWT.NONE);
		top.setLayout(new GridLayout(2, false));
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		btnSave = new Button(top, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				save();
			}
		});
		btnSave.setText("save");

		Label ltitle = new Label(top, SWT.NONE);
		ltitle.setText("Title");

		text_1 = new Text(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL
				| SWT.MULTI);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		text_1.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				textChange();
			}
		});

		error = new Text(this, SWT.MULTI);
		error.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
	}

	private void save() {
		WObject o = new WObject();
		o.parse(text_1.getText());
		this.o.parse(o);
	}

	private void textChange() {
		try {
			String text = text_1.getText();
			WObject o = new WObject();
			o.parse(text);
			error.setText("OK");
			btnSave.setEnabled(true);
		} catch (Exception e) {
			error.setText("" + e);
			btnSave.setEnabled(false);
		}
	}

	public void setObject(LOTObject o) {
		this.o = o;
		setText(o.getObject().toText());
	}

	private void setText(String text) {
		text_1.setText(text);
	}
}
