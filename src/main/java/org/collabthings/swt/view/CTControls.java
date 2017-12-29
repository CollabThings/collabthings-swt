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
package org.collabthings.swt.view;

import org.collabthings.swt.controls.CTResourceManagerFactory;
import org.collabthings.swt.controls.CTText;
import org.eclipse.swt.widgets.Composite;

public class CTControls {

	public static CTText getText(Composite composite, int none) {
		CTText text = new CTText(composite, none);
		
		text.setFont(CTResourceManagerFactory.instance().getDefaultFont());
		return text;
	}

}
