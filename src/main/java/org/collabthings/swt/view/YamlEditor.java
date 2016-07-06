package org.collabthings.swt.view;

import org.collabthings.model.CTObject;
import org.collabthings.swt.LOTSWT;
import org.collabthings.util.LLog;
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
import org.eclipse.wb.swt.SWTResourceManager;

import waazdoh.common.WObject;

public class YamlEditor extends Composite {
	private CTObject o;
	private Text text;
	private Text error;
	private Button btnSave;
	private int currenthash;
	private Thread t;

	public YamlEditor(Composite parent, int style, String title) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);
		setLayout(gridLayout);

		Composite top = new Composite(this, SWT.NONE);
		GridLayout gl_top = new GridLayout(2, false);
		LOTSWT.setDefaults(gl_top);
		top.setLayout(gl_top);
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
		ltitle.setText(title);

		text = new Text(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		text.setFont(SWTResourceManager.getFont("Open Sans", 10, SWT.NORMAL));
		text.setText("testing\ntestintintit");
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		text.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				textChange();
			}
		});

		error = new Text(this, SWT.MULTI);
		error.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	}

	private void save() {
		WObject no = new WObject();
		no.parse(text.getText());
		this.o.parse(no);
	}

	private void textChange() {
		try {
			String stext = text.getText();
			WObject o = new WObject();
			o.parse(stext);
			error.setText("OK");
			btnSave.setEnabled(true);
		} catch (Exception e) {
			error.setText("" + e);
			btnSave.setEnabled(false);
		}
	}

	public void setObject(CTObject o) {
		this.o = o;

		if (t == null) {
			t = new Thread(() -> checkObjectUpdate(), "Yamleditor checker");
			t.start();
		}

		t.setName("YamlEditor " + o);
	}

	private void setText(String stext) {
		getDisplay().asyncExec(() -> {
			this.text.setText(stext);
		});
	}

	private synchronized void checkObjectUpdate() {
		while (!isDisposed()) {
			if (this.o != null) {
				int nhash = this.o.getObject().hashCode();
				if (nhash != currenthash) {
					setText(this.o.getObject().toText());
					currenthash = nhash;
				} else {
					doWait(100);
				}
			} else {
				doWait(1000);
			}
		}
	}

	private void doWait(int timeout) {
		try {
			wait(timeout);
		} catch (InterruptedException e) {
			LLog.getLogger(this).error(this, "checkObjectUpdate", e);
		}
	}
}
