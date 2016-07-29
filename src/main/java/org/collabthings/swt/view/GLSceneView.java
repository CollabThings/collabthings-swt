package org.collabthings.swt.view;

import java.awt.Canvas;
import java.awt.Frame;

import org.collabthings.jme.CTObjectViewer;
import org.collabthings.jme.CTObjectViewerImpl;
import org.collabthings.jme.CTSceneApp;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.collabthings.swt.controls.CTText;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

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
		ctools.setBackground(SWTResourceManager.getActiontitle2Background());
		ctools.setForeground(SWTResourceManager.getActionTitle2Color());
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
				scene.setSkip(Integer.parseInt(tskip.getText()));
			}
		});

		lhighlight = new Label(ctools, SWT.NONE);
		lhighlight.setText("Highlight");
		lhighlight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Composite c = new CTComposite(this, SWT.EMBEDDED);
		c.setBackground(SWTResourceManager.getColor(248, 100, 100));
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);

		view = new CTSceneApp();
		view.init();

		getDisplay().asyncExec(() -> {
			Canvas canvas = view.getCanvas(c.getSize().x, c.getSize().y);

			Frame f = SWT_AWT.new_Frame(c);
			f.add(canvas);

			new Thread(() -> {
				log.info("setscenecwait");
				ConditionWaiter.wait(() -> view.isReady(), 20000);
				log.info("setscenecwait done");
				scene = new CTObjectViewerImpl(view);
				view.setScene(scene);
			}).start();
		});

		c.addDisposeListener(e -> {
			view.close();
		});
	}

	public void setPart(CTPart part) {
		scene.setPart(part);
	}

	public void setModelView(CTOpenSCAD scad) {
		scene.setModel(scad);
	}

	public void setHighlight(Object o) {
		scene.setHighlight(o);
		lhighlight.setText("" + o);
	}

	public boolean isReady() {
		return view != null && view.isReady() && scene != null;
	}

}
