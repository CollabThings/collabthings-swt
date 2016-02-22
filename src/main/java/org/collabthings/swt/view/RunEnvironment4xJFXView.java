package org.collabthings.swt.view;

import javafx.application.Platform;

import javax.vecmath.Vector3d;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.math.LTransformation;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import waazdoh.client.utils.ConditionWaiter.Condition;
import waazdoh.common.MTimedFlag;

public class RunEnvironment4xJFXView extends Composite {
	private LTransformation freetransform;
	private double freeangle;
	private LOTRunEnvironment runenv;
	private JFXSimulationComposite yview;
	private JFXSimulationComposite xview;
	private JFXSimulationComposite zview;
	private JFXSimulationComposite fview;

	private LLog log = LLog.getLogger(this);

	public RunEnvironment4xJFXView(LOTApp app, Composite parent, int style) {
		this(app, parent, style, null);
	}

	public RunEnvironment4xJFXView(LOTApp app, Composite parent, int style,
			LOTRunEnvironment nrunenv) {
		super(parent, style);

		this.runenv = nrunenv;
		setLayout(new GridLayout(2, false));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));

		xview = new JFXSimulationComposite(app, this);
		yview = new JFXSimulationComposite(app, this);
		zview = new JFXSimulationComposite(app, this);
		fview = new JFXSimulationComposite(app, this);

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

		MTimedFlag flag = new MTimedFlag(1000);

		Platform.runLater(() -> {
			yview.setRunEnvironment(runenv);
			xview.setRunEnvironment(runenv);
			zview.setRunEnvironment(runenv);
			fview.setRunEnvironment(runenv);

			xview.getView().setSceneOrientation(0, 0, 0);
			yview.getView().setSceneOrientation(90, 0, 0);
			zview.getView().setSceneOrientation(0, 90, 0);
			fview.getView().setSceneOrientation(30, 30, 30);

			flag.trigger();
		});

		flag.waitTimer();
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
					} catch (InterruptedException e) {
						log.error(this, "runWhile", e);
					}
				}
			}

			log.info("Runwhile out");
		}).start();
	}
}
