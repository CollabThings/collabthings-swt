package org.collabthings.swt.dialog;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.collabthings.util.LLog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class LOTMessageDialog {

	private Shell shell;

	public LOTMessageDialog(Shell shell) {
		this.shell = shell;
	}

	public void show(Exception e) {
		StringWriter out2 = new StringWriter();
		e.printStackTrace(new PrintWriter(out2));
		String message = out2.toString();
		String title = "ERROR!! " + e;
		LLog.getLogger(this).error(this, "displayloop", e);

		show(title, message);
	}

	public void show(String title, String message) {
		MessageDialog dialog = new MessageDialog(shell, title, null, message,
				MessageDialog.ERROR, new String[] { "OK" }, 0);
		dialog.open();
	}
}
