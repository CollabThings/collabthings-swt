package org.libraryofthings.swt.view;

import java.net.URL;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.embed.swt.FXCanvas;
import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.util.Duration;

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
		/* Create the Scene instance and set the group node as root */
		org.eclipse.swt.graphics.Color bg = getBackground();
		double red = bg.getRed() / 255.0 * 0.8f;
		double green = bg.getGreen() / 255.0 * 0.8f;
		double blue = bg.getBlue() / 255.0;
		Scene scene = new Scene(group, new Color(red, green, blue, 1));// ,
		// Color.rgb(getBackground().getRed(),
		// getBackground().getGreen(),
		// getBackground().getBlue()));
		Camera camera = new PerspectiveCamera(true);
		scene.setCamera(camera);
		//
		Group cameraGroup = new Group();
		cameraGroup.getChildren().add(camera);
		group.getChildren().add(cameraGroup);
		cameraGroup.setTranslateZ(-75);

		/* Attach an external stylesheet */
		scene.getStylesheets().add("twobuttons/Buttons.css");
		//
		testImport(group);

		canvas.setScene(scene);
	}

	private void testImport(Group group) {
		X3dModelImporter x3dImporter = new X3dModelImporter();
		try {
			log.info("example resource "
					+ this.getClass().getResource("/example").getPath());

			URL modelUrl = this.getClass().getResource(
					"/example/models/cube.x3d");
			log.info("loading model " + modelUrl);
			x3dImporter.read(modelUrl);
		} catch (ImportException e) {
			// handle exception
			log.error(this, "testview3d", e);
		}
		//
		Group ogroup = new Group();

		Node[] rootNodes = x3dImporter.getImport();
		log.info("imported nodes " + rootNodes);
		for (Node node : rootNodes) {
			ogroup.getChildren().add(node);
		}
		// ogroup.setTranslateY(20);
		ogroup.setScaleY(10);
		ogroup.setScaleX(2);
		//
		group.getChildren().add(ogroup);
		//

		AmbientLight light = new AmbientLight();
		Group lightgroup = new Group();
		lightgroup.getChildren().add(light);
		group.getChildren().add(lightgroup);

		//
		// Cylinder myCylinder = new Cylinder(1, 2);
		// group.getChildren().add(myCylinder);
		final Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(true);
		final KeyValue kv = new KeyValue(ogroup.rotateProperty(), 360);
		final KeyFrame kf = new KeyFrame(Duration.millis(2000), kv);
		timeline.getKeyFrames().add(kf);
		timeline.play();
	}
}
