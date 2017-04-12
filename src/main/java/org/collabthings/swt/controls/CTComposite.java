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

import org.collabthings.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class CTComposite extends Composite {

	public CTComposite(Composite shell, int none) {
		super(shell, none | SWT.NONE);
		setBackground(SWTResourceManager.getControlBg());
		setFont(SWTResourceManager.getDefaultFont());
	}

}
