package org.collabthings.swt.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CTErrorDialog {

	private Text terror;
	private Label ltext;
	private Shell shell;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public CTErrorDialog(Shell parent) {
		shell = new Shell(parent);

		createContents();
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public void open() {
		shell.open();
		shell.layout();
		new Thread(() -> {
			Display display = shell.getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}).start();
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell.setSize(450, 300);
		shell.setText("ERROR");
		shell.setLayout(new GridLayout(1, false));

		ltext = new Label(shell, SWT.NONE);
		ltext.setBounds(0, 0, 55, 15);
		ltext.setText("ERROR");

		terror = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		terror.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		terror.setEditable(false);
	}

	public void show(String string, Exception e) {
		ltext.setText("ERRORID " + string);
		setMessage("" + e);
	}

	public void show(Exception e) {
		ltext.setText("ERROR");
		setMessage("" + e);
	}

	public void show(String string, String message) {
		ltext.setText(string);
		setMessage(message);
	}

	private void setMessage(String message) {
		terror.setText(message);
	}
}
