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
package org.collabthings.swt.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.jme3.math.Vector3f;

public class CTVectorEditor extends CTComposite {

	private ChangeListener<Vector3f> listener;
	private CTDoubleEditor ey;
	private CTDoubleEditor ez;
	private CTDoubleEditor ex;
	private Vector3f v;
	private CTLabel lname;

	public CTVectorEditor(Composite c, Vector3f nv, ChangeListener<Vector3f> listener) {
		super(c, SWT.None);
		this.v = nv;
		this.listener = listener;
		//
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 4;
		setLayout(gridLayout);

		if (v == null) {
			v = new Vector3f();
		}

		lname = new CTLabel(this, SWT.None);

		ex = new CTDoubleEditor(this, (Double) (double) v.x, d -> {
			v.x = d.floatValue();
			changed();
		});
		ex.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		ey = new CTDoubleEditor(this, (Double) (double) v.x, d -> {
			v.y = d.floatValue();
			changed();
		});
		ey.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		ez = new CTDoubleEditor(this, (Double) (double) v.x, d -> {
			v.z = d.floatValue();
			changed();
		});
		ez.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		updateValues();
	}

	public void setName(String name) {
		lname.setText(name);
	}

	private void changed() {
		listener.changed(getValue());
	}

	public Vector3f getValue() {
		return new Vector3f((float) ex.getValue(), (float) ey.getValue(), (float) ez.getValue());
	}

	public static interface ChangeListener<T> {
		void changed(T t);
	}

	public void updateValues() {
		ex.setValue(v.x);
		ey.setValue(v.y);
		ez.setValue(v.z);
	}

}
