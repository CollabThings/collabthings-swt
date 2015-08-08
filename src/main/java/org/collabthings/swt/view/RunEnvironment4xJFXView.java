package org.collabthings.swt.view;

import javax.vecmath.Vector3d;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.math.LTransformation;
import org.collabthings.util.LLog;
import org.collabthings.view.RunEnvironmentDrawerImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

import waazdoh.client.utils.ConditionWaiter.Condition;

public class RunEnvironment4xJFXView extends Composite {
	private LTransformation freetransform;
	private double freeangle;
	private LOTRunEnvironment runenv;
	private JFXSimulationComposite yview;
	private JFXSimulationComposite xview;
	private JFXSimulationComposite zview;
	private JFXSimulationComposite fview;

	private LLog log = LLog.getLogger(this);

	public RunEnvironment4xJFXView(Composite parent, int style) {
		this(parent, style, null);
	}

	public RunEnvironment4xJFXView(Composite parent, int style,
			LOTRunEnvironment nrunenv) {
		super(parent, style);

		this.runenv = nrunenv;
		setLayout(new GridLayout(2, false));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));

		 new RunEnvironmentDrawerImpl(runenv,
				(v, b) -> {
					v.y = v.z;
					v.z = 0;
				}, "Y");
		new RunEnvironmentDrawerImpl(runenv,
				(v, b) -> {
					v.x = v.z;
					v.z = 0;
				}, "X");
		new RunEnvironmentDrawerImpl(runenv,
				(v, b) -> {
					v.z = 0;
				}, "Z");
		new RunEnvironmentDrawerImpl(runenv, (v, b) -> {
			if (b) {
				freetransform.transform(v);
			} else {
				freetransform.transformw0(v);
			}
		}, "Z");

		xview = new JFXSimulationComposite(this, nrunenv);
		yview = new JFXSimulationComposite(this, nrunenv);
		zview = new JFXSimulationComposite(this, nrunenv);
		fview = new JFXSimulationComposite(this, nrunenv);

		// ycanvas = new RunEnvironmentCanvas(this, SWT.NONE, ydrawer);
		// zcanvas = new RunEnvironmentCanvas(this, SWT.NONE, zdrawer);
		// xcanvas = new RunEnvironmentCanvas(this, SWT.NONE, xdrawer);
		// fcanvas = new RunEnvironmentCanvas(this, SWT.NONE, freedrawer);
		yview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		zview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		xview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		fview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		step(0);
	}

	public void step(double dtime) {
		freeangle += dtime * 0.00006;
		LTransformation nfreetransform = new LTransformation();
		nfreetransform.mult(LTransformation.getRotate(new Vector3d(1, 0, 0),
				0.4));
		nfreetransform.mult(LTransformation.getRotate(new Vector3d(0, 1, 0),
				freeangle));
		freetransform = nfreetransform;
	}

	public void stop() {
		xview.stop();
		yview.stop();
		zview.stop();
		fview.stop();
	}

	public void setRunEnvironment(LOTRunEnvironment runenv2) {
		this.runenv = runenv2;
		yview.setRunEnvironment(runenv);
		xview.setRunEnvironment(runenv);
		zview.setRunEnvironment(runenv);
		fview.setRunEnvironment(runenv);

		xview.getView().setSceneOrientation(0, 0, 0);
		yview.getView().setSceneOrientation(90, 0, 0);
		zview.getView().setSceneOrientation(0, 90, 0);
		fview.getView().setSceneOrientation(30, 30, 30);
	}

	public void runWhile(Condition c) {
		new Thread(() -> {
			log.info("Runwhile start");

			long lasttime = System.currentTimeMillis();
			while (c.test() && !isDisposed()) {
				long dt = System.currentTimeMillis() - lasttime;
				lasttime = System.currentTimeMillis();

				step(dt);

				synchronized (this) {
					try {
						this.wait(60);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			log.info("Runwhile out");
		}).start();
	}
}
