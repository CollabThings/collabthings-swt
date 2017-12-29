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

import java.awt.Canvas;
import java.awt.Frame;

import org.collabthings.jme.CTObjectViewer;
import org.collabthings.jme.CTObjectViewerImpl;
import org.collabthings.jme.CTSceneApp;
import org.collabthings.model.CTPart;
import org.collabthings.tk.CTComposite;
import org.collabthings.tk.CTLabel;
import org.collabthings.tk.CTResourceManagerFactory;
import org.collabthings.tk.CTText;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.jme3.math.Vector3f;

import waazdoh.client.utils.ConditionWaiter;

public class GLSceneView extends CTComposite {

	private LLog log = LLog.getLogger(this);

	private CTObjectViewer scene;
	private Label lhighlight;

	private CTSceneApp view;

	public GLSceneView(Composite parent) {
		super(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout(1, true);
		this.setLayout(gridLayout);

		Composite ctools = new CTComposite(this, SWT.NONE);
		ctools.setBackground(CTResourceManagerFactory.instance().getActiontitle2Background());
		ctools.setForeground(CTResourceManagerFactory.instance().getActionTitle2Color());
		ctools.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		GridLayout ctoolslayout = new GridLayout();
		ctoolslayout.numColumns = 10;
		ctools.setLayout(ctoolslayout);
		CTLabel l = new CTLabel(ctools, SWT.NONE);
		l.setText("Skip");

		CTText tskip = new CTText(ctools, SWT.BORDER);
		tskip.setForeground(ctools.getForeground());
		tskip.setBackground(ctools.getBackground());

		tskip.setText("0");
		tskip.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (scene != null && tskip != null) {
					scene.setSkip(Integer.parseInt(tskip.getText()));
				}
			}
		});

		lhighlight = new Label(ctools, SWT.NONE);
		lhighlight.setText("Highlight");
		lhighlight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Composite c = new CTComposite(this, SWT.EMBEDDED);
		c.setBackground(CTResourceManagerFactory.instance().getColor(248, 100, 100));
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		view = new CTSceneApp();
		view.init();

		Color controlBg = CTResourceManagerFactory.instance().getControlBg();

		getDisplay().asyncExec(() -> {
			Canvas canvas = view.getCanvas(c.getSize().x, c.getSize().y);

			Frame f = SWT_AWT.new_Frame(c);
			f.add(canvas);

			new Thread(() -> {
				log.info("setscenecwait");
				ConditionWaiter.wait(() -> view.isReady(), 20000);
				log.info("setscenecwait done");
				scene = new CTObjectViewerImpl(view.getAssetManager());
				view.setScene(scene);
				view.setBackgroundColor(controlBg.getRed() / 255.0F, controlBg.getGreen() / 255.0F,
						controlBg.getBlue() / 255.0F);
			}).start();
		});

		c.addDisposeListener(e -> {
			view.close();
		});
	}

	public void setPart(CTPart part) {
		scene.setPart(part);
	}

	public void setHighlight(Object o) {
		view.setHighlight(o);
		scene.setHighlight(o);
		lhighlight.setText("" + o);
	}

	public boolean isReady() {
		return view != null && view.isReady() && scene != null;
	}

	public void lookAt(Vector3f lookAt) {
		view.setLookAt(lookAt);
	}

}
