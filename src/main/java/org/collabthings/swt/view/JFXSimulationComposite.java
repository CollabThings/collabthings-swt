package org.collabthings.swt.view;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.view.JFXSimulationView;
import org.collabthings.view.JFXSimulationView.ViewCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class JFXSimulationComposite extends Composite {

	private LOTRunEnvironment env;
	private FXCanvas canvas;
	private JFXSimulationView view;

	public JFXSimulationComposite(Composite parent, LOTRunEnvironment env) {
		super(parent, SWT.NONE);
		this.env = env;
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
		view = new JFXSimulationView(runenv);
		view.setCanvas(new ViewCanvas() {
			@Override
			public void refresh() {
				canvas.redraw();
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
	}

	public void stop() {
		view.stop();
	}

	public JFXSimulationView getView() {
		return this.view;
	}

}
