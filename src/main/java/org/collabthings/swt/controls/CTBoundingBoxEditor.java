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

import org.collabthings.model.CTBoundingBox;
import org.collabthings.tk.CTComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class CTBoundingBoxEditor extends CTComposite {

	private CTVectorEditor el;
	private CTVectorEditor en;
	private CTBoundingBox box;

	public CTBoundingBoxEditor(final Composite c, final CTBoundingBox no,
			final ChangeListener<CTBoundingBox> listener) {
		super(c, SWT.None);
		this.box = no;
		//
		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		el = new CTVectorEditor(this, box.getA(), v -> listener.changed(box));
		el.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		en = new CTVectorEditor(this, box.getB(), v -> listener.changed(box));
		en.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	}

	public static interface ChangeListener<T> {
		void changed(T t);
	}
}
