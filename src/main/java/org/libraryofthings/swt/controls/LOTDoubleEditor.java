package org.libraryofthings.swt.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class LOTDoubleEditor extends Composite {
	private Text s;

	public LOTDoubleEditor(Composite c, Double d,
			ChangeListener<Double> listener) {
		super(c, SWT.None);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		//
		s = new Text(this, SWT.NONE);
		s.setEditable(true);
		s.setText("" + d);
		s.addListener(SWT.Modify,
				event -> listener.changed(Double.parseDouble(s.getText())));
	}

	public static interface ChangeListener<T> {
		void changed(T t);
	}

	public double getValue() {
		return Double.parseDouble(s.getText());
	}
}
