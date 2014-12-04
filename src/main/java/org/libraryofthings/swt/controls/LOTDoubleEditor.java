package org.libraryofthings.swt.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class LOTDoubleEditor extends Composite {
	private Text s;
	private ChangeListener<Double> listener;

	public LOTDoubleEditor(Composite c, Double d,
			ChangeListener<Double> listener) {
		super(c, SWT.None);
		this.listener = listener;

		setLayout(new FillLayout(SWT.HORIZONTAL));
		//
		s = new Text(this, SWT.NONE);
		s.setEditable(true);
		s.setText("" + d);

		s.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				changed();
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				this_keyPressed(arg0);
			}
		});
	}

	private double getDouble() {
		return Double.parseDouble(s.getText());
	}

	protected synchronized void this_keyPressed(KeyEvent arg0) {
		if (arg0.keyCode == SWT.ARROW_UP) {
			double nd = getDouble() + Math.abs(getDouble() * 0.01);
			if (nd == 0.0) {
				nd = 0.001;
			}
			setDouble(nd);
		} else if (arg0.keyCode == SWT.ARROW_DOWN) {
			double nd = getDouble() - Math.abs(getDouble() * 0.01);
			if (nd == 0.0) {
				nd = -0.001;
			}
			setDouble(nd);
		}
	}

	private synchronized void changed() {
		listener.changed(getDouble());
	}

	private synchronized void setDouble(double nd) {
		this.s.setText("" + nd);
		listener.changed(nd);
	}

	public static interface ChangeListener<T> {
		void changed(T t);
	}

	public double getValue() {
		return getDouble();
	}

	public synchronized void setValue(double d) {
		String ntext = "" + d;
		if (!ntext.equals(s.getText())) {
			this.s.setText(ntext);
		}
	}
}
