/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/
package org.collabthings.tk;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class CTDoubleEditor extends CTComposite {
	private static final int MAX_DECIMALS = 3;
	private static final double MIN_POS_VALUE = 0.001;
	private static final double MIN_NEG_VALUE = 0.001;
	private CTText s;
	private ChangeListener<Double> listener;

	public CTDoubleEditor(Composite c, Double d, ChangeListener<Double> listener) {
		super(c, SWT.None);
		this.listener = listener;

		setLayout(new FillLayout(SWT.HORIZONTAL));
		//
		s = new CTText(this, SWT.NONE);
		s.setEditable(true);
		setDoubleText(d);

		s.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				this_keyPressed(arg0);
			}
		});

		s.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				changed();
			}
		});
	}

	private double getDouble() {
		return Double.parseDouble(s.getText());
	}

	protected synchronized void this_keyPressed(KeyEvent arg0) {
		if (arg0.keyCode == SWT.ARROW_UP) {
			double nd = getDouble() + Math.abs(getDouble() * 0.01);
			if (nd >= 0.0 && nd < 0.001) {
				nd = MIN_POS_VALUE;
			}
			setDouble(nd);
		} else if (arg0.keyCode == SWT.ARROW_DOWN) {
			double nd = getDouble() - Math.abs(getDouble() * 0.01);
			if (nd <= 0.0 && nd > -0.001) {
				nd = -MIN_NEG_VALUE;
			}
			setDouble(nd);
		} else if (arg0.keyCode == SWT.TAB) {
			changed();
		}
	}

	private synchronized void changed() {
		try {
			this.s.setValidated(true);
			double value = getDouble();
			listener.changed(value);
		} catch (NumberFormatException e) {
			this.s.setValidated(false);
		}
	}

	private synchronized void setDouble(double nd) {
		setDoubleText(nd);
		listener.changed(nd);
	}

	private void setDoubleText(double nd) {
		String sd = "" + nd;
		if (sd.indexOf(".") > 0 && (sd.length() - sd.indexOf(".") - 1) > MAX_DECIMALS) {
			sd = sd.substring(0, sd.indexOf(".") + MAX_DECIMALS);
		}

		this.s.setText(sd);
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
