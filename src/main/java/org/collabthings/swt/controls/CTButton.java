package org.collabthings.swt.controls;

import org.collabthings.swt.SWTResourceManager;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class CTButton {
	private StyledText l;
	private int padding = 10;

	public CTButton(String string, CTComposite ctools, int none, ButtonListener listener) {
		this(ctools, none);

		l.setText("" + string);
		addSelectionListener(listener);
	}

	public CTButton(Composite parent, int style) {
		l = new StyledText(parent, style);
		// l = new CTLabel(parent, style);

		l.setEditable(false);
		l.setCaret(null);

		l.setBackground(SWTResourceManager.getColor(220, 220, 220));
		l.setFont(SWTResourceManager.getDefaultFont());
		l.setLeftMargin(padding);
		l.setRightMargin(padding);
		l.setTopMargin(padding);
		l.setBottomMargin(padding);
	}

	public void setText(String string) {
		l.setText(string);
	}

	public void addSelectionListener(ButtonListener listener) {
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				listener.selected();
			}
		});
	}

	public static interface ButtonListener {
		void selected();
	}

	public void setLayoutData(Object gridData) {
		l.setLayoutData(gridData);
	}

	public void setBounds(int x, int y, int w, int h) {
		this.l.setBounds(x, y, w, h);
	}

	public void setEnabled(boolean b) {
		this.l.setEnabled(b);
	}

	public Shell getShell() {
		return l.getShell();
	}

	public void setBackground(Color bgcolor) {
		l.setBackground(bgcolor);
	}

}
