package org.libraryofthings.swt.view;

import java.net.URL;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.libraryofthings.LLog;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.x3d.X3dModelImporter;

public class Part3DView extends Composite {
	private LLog log = LLog.getLogger(this);

	public Part3DView(Composite c_view, int style) {
		super(c_view, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		//
		FXCanvas canvas = new FXCanvas(this, SWT.NONE) {
			public Point computeSize(int wHint, int hHint, boolean changed) {
				getScene().getWindow().sizeToScene();
				int width = (int) getScene().getWidth();
				int height = (int) getScene().getHeight();
				return new Point(width, height);
			}
		};
		createScene(canvas);
	}

	private void createScene(FXCanvas canvas) {
		/* Create a JavaFX Group node */
		Group group = new Group();
		/* Create a JavaFX button */
		final Button jfxButton = new Button("JFX Button");
		/* Assign the CSS ID ipad-dark-grey */
		jfxButton.setId("ipad-dark-grey");
		/* Add the button as a child of the Group node */
		group.getChildren().add(jfxButton);
		/* Create the Scene instance and set the group node as root */
		Scene scene = new Scene(group, Color.BLACK);// ,
		// Color.rgb(getBackground().getRed(),
		// getBackground().getGreen(),
		// getBackground().getBlue()));

		/* Attach an external stylesheet */
		scene.getStylesheets().add("twobuttons/Buttons.css");
		//
		canvas.setScene(scene);
	}
}
