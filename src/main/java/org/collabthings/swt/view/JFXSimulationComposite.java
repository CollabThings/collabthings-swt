package org.collabthings.swt.view;

import javafx.embed.swt.FXCanvas;
import javafx.geometry.Point2D;
import javafx.scene.Scene;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.view.JFXRunEnvironmentView;
import org.collabthings.view.JFXRunEnvironmentView.ViewCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class JFXSimulationComposite extends Composite {

	private FXCanvas canvas;
	private JFXRunEnvironmentView view;
	private LOTApp app;

	public JFXSimulationComposite(LOTApp app, Composite parent) {
		super(parent, SWT.NONE);
		this.app = app;

		setLayout(new GridLayout(1, false));
		canvas = new FXCanvas(this, SWT.BORDER);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		canvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				view.mouseUp(arg0.x, arg0.y, arg0.button);
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				view.mouseDown(arg0.x, arg0.y, arg0.button);
			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
		});

		canvas.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent arg0) {
				if (view != null) {
					view.mouseMove(arg0.x, arg0.y, arg0.button);
				}
			}
		});
	}

	public void setRunEnvironment(LOTRunEnvironment runenv) {
		view = new JFXRunEnvironmentView(runenv);
		app.addTask(() -> {
			view.setCanvas(new ViewCanvas() {
				@Override
				public boolean isVisible() {
					return !canvas.isDisposed() && canvas.isVisible();
				}

				@Override
				public void refresh() {
					canvas.redraw();
				}

				@Override
				public Point2D getUpperLeft() {
					Point d = canvas.toDisplay(1, 1);
					return new Point2D(d.x, d.y);
				}

				@Override
				public void setScene(Scene scene) {
					canvas.setScene(scene);
				}

				@Override
				public double getWidth() {
					return canvas.getSize().x;
				}

				@Override
				public double getHeight() {
					return canvas.getSize().y;
				}
			});
		});
	}

	public void stop() {
		view.stop();
	}

	public JFXRunEnvironmentView getView() {
		return this.view;
	}

}
