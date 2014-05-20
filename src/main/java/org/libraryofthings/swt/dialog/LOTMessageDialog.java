package org.libraryofthings.swt.dialog;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.libraryofthings.LLog;

public class LOTMessageDialog {

	private Shell shell;

	public LOTMessageDialog(Shell shell) {
		this.shell = shell;
	}

	public void show(Exception e) {
		StringWriter out2 = new StringWriter();
		e.printStackTrace(new PrintWriter(out2));
		MessageDialog dialog = new MessageDialog(shell, "ERROR!! " + e, null,
				out2.toString(), MessageDialog.ERROR, new String[] { "OK" }, 0);
		LLog.getLogger(this).error(this, "displayloop", e);
		dialog.open();
	}

}
