package org.collabthings.swt.view;

import javax.vecmath.Vector3d;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.math.LTransformation;
import org.collabthings.view.RunEnviromentDrawer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

import waazdoh.client.utils.ConditionWaiter.Condition;

public class RunEnvironment4xView extends Composite {
	private LTransformation freetransform;
	private double freeangle;
	private LOTRunEnvironment runenv;
	private RunEnvironmentCanvas ycanvas;
	private RunEnvironmentCanvas xcanvas;
	private RunEnvironmentCanvas fcanvas;
	private RunEnvironmentCanvas zcanvas;

	public RunEnvironment4xView(Composite parent, int style) {
		this(parent, style, null);
	}

	public RunEnvironment4xView(Composite parent, int style,
			LOTRunEnvironment nrunenv) {
		super(parent, style);

		this.runenv = nrunenv;
		setLayout(new GridLayout(2, false));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));

		RunEnviromentDrawer ydrawer = new RunEnviromentDrawer(runenv, (v) -> {
			v.y = v.z;
			v.z = 0;
		}, "Y");
		RunEnviromentDrawer xdrawer = new RunEnviromentDrawer(runenv, (v) -> {
			v.x = v.z;
			v.z = 0;
		}, "X");
		RunEnviromentDrawer zdrawer = new RunEnviromentDrawer(runenv, (v) -> {
			v.z = 0;
		}, "Z");
		RunEnviromentDrawer freedrawer = new RunEnviromentDrawer(runenv,
				(v) -> {
					freetransform.transform(v);
				}, "Z");

		ycanvas = new RunEnvironmentCanvas(this, SWT.NONE, ydrawer);
		ycanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		zcanvas = new RunEnvironmentCanvas(this, SWT.NONE, zdrawer);
		zcanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		xcanvas = new RunEnvironmentCanvas(this, SWT.NONE, xdrawer);
		xcanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		fcanvas = new RunEnvironmentCanvas(this, SWT.NONE, freedrawer);
		fcanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		step(0);
	}

	public synchronized void doRepaint() {
		getDisplay().syncExec(() -> {
			ycanvas.callRepaint();
			xcanvas.callRepaint();
			zcanvas.callRepaint();
			fcanvas.callRepaint();
		});
	}

	public void step(double dtime) {
		freeangle += dtime * 0.00002;
		LTransformation nfreetransform = new LTransformation();
		nfreetransform.mult(LTransformation.getRotate(new Vector3d(1, 0, 0),
				0.4));
		nfreetransform.mult(LTransformation.getRotate(new Vector3d(0, 1, 0),
				freeangle));
		freetransform = nfreetransform;
	}

	public void setRunEnvironment(LOTRunEnvironment runenv2) {
		this.runenv = runenv2;
		ycanvas.getDrawer().setRunEnvironment(runenv);
		xcanvas.getDrawer().setRunEnvironment(runenv);
		zcanvas.getDrawer().setRunEnvironment(runenv);
		fcanvas.getDrawer().setRunEnvironment(runenv);
	}

	public void runWhile(Condition c) {
		new Thread(() -> {
			long lasttime = System.currentTimeMillis();
			while (c.test()) {
				long dt = System.currentTimeMillis() - lasttime;
				lasttime = System.currentTimeMillis();

				step(dt);

				doRepaint();

				synchronized (this) {
					try {
						this.wait(60);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
