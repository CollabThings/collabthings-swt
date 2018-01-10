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

import org.collabthings.math.CTMath;
import org.collabthings.math.LOrientation;
import org.collabthings.swt.LOTSWT;
import org.collabthings.tk.CTComposite;
import org.collabthings.tk.CTDoubleEditor;
import org.collabthings.tk.CTLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class CTOrientationEditor extends CTComposite {

	private ChangeListener<LOrientation> listener;
	private CTVectorEditor el;
	private CTVectorEditor en;
	private CTDoubleEditor ea;
	private LOrientation o;

	public CTOrientationEditor(Composite c, LOrientation no,
			ChangeListener<LOrientation> listener) {
		super(c, SWT.None);
		this.o = no;
		this.listener = listener;
		//
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		LOTSWT.setDefaults(gridLayout);
		gridLayout.horizontalSpacing = 4;
		setLayout(gridLayout);

		el = new CTVectorEditor(this, o.getLocation(),
				v -> listener.changed(o));
		el.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		el.setName("loc");

		en = new CTVectorEditor(this, o.getNormal(), v -> {
			if (en != null) {
				o.getNormal().normalize();
				en.updateValues();
				listener.changed(o);
			}
		});
		en.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		en.setName("n:");

		Composite bottom = new CTComposite(this, SWT.NONE);
		bottom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout glbottom = new GridLayout(2, false);
		LOTSWT.setDefaults(glbottom);
		bottom.setLayout(glbottom);
		
		CTLabel langlename = new CTLabel(bottom, SWT.NONE);
		langlename.setText("Angle");
		ea = new CTDoubleEditor(bottom, CTMath.radToDegrees(o.getAngle()), e -> angleChanged(e));
		ea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
	}

	private void angleChanged(double na) {
		o.setAngle(CTMath.degreesToRad(na));
		listener.changed(o);
	}

	public static interface ChangeListener<T> {
		void changed(T t);
	}
}
