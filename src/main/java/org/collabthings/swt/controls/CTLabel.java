package org.collabthings.swt.controls;

import org.collabthings.swt.SWTResourceManager;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CTLabel {

	private Label label;

	public CTLabel(Composite composite_1, int none) {
		this.label = new Label(composite_1, none);

		label.setBackground(SWTResourceManager.getControlBg());
		label.setFont(SWTResourceManager.getDefaultFont());

	}

	public void setAlignment(int right) {
		this.label.setAlignment(right);
	}

	public void setLayoutData(Object gridData) {
		this.label.setLayoutData(gridData);
	}

	public void setText(String string) {
		this.label.setText(string);
	}

	public String getText() {
		return label.getText();
	}

	public void addMouseListener(MouseListener mouseAdapter) {
		this.label.addMouseListener(mouseAdapter);
	}

	public Control getControl() {
		return this.label;
	}

	public void setBounds(int i, int j, int k, int l) {
		this.label.setBounds(i, j, k, l);
	}

	public boolean isDisposed() {
		return this.label.isDisposed();
	}

	public Display getDisplay() {
		return this.label.getDisplay();
	}

	public void setBackground(Color color) {
		this.label.setBackground(color);
	}

	public void setFont(int size, int style) {
		this.label.setFont(SWTResourceManager.getDefaultFont(size, style));
	}

	public void setEnabled(boolean b) {
		this.label.setEnabled(b);
	}

	public Shell getShell() {
		return label.getShell();
	}

	public Label getLabel() {
		return this.label;
	}

}
