package org.libraryofthings.swt.view;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.embed.swt.FXCanvas;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.GestureEvent;
import org.eclipse.swt.events.GestureListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.libraryofthings.LLog;
import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOT3DModel;
import org.libraryofthings.swt.dialog.LOTMessageDialog;
import org.xml.sax.SAXException;

import com.interactivemesh.jfx.importer.x3d.X3dModelImporter;

public class Model3DView extends Composite implements GestureListener {
	private LLog log = LLog.getLogger(this);
	private LOTEnvironment env;
	private double zoom = 1.0;
	private PerspectiveCamera camera;
	private Group cameraGroup;
	private Group objectgroup;
	private Group scenegroup;
	protected boolean mousedown;
	private int lastmousey;
	private int lastmousex;
	private double rotatex;
	private double rotatey;
	//
	private Map<LOT3DModel, Group> groups = new HashMap<LOT3DModel, Group>();

	public Model3DView(Composite c_view, int style) {
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

		canvas.addGestureListener(this);
		canvas.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseScrolled(MouseEvent arg0) {
				doMouseScrolled(arg0);
			}
		});

		canvas.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent arg0) {
				doMouseMoved(arg0);
			}
		});

		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				mousedown = true;
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
				mousedown = false;
			}
		});
	}

	private void doMouseMoved(MouseEvent e) {
		int dx = lastmousex - e.x;
		int dy = lastmousey - e.y;
		this.lastmousex = e.x;
		this.lastmousey = e.y;
		if (mousedown) {
			this.rotatex += 0.1 * dx;
			this.rotatey += 0.1 * dy;
			updateRotation();
		}
	}

	private void doMouseScrolled(MouseEvent e) {
		if (e.count == 0)
			return;

		int direction = e.count > 0 ? 1 : -1;
		zoom += direction * 0.1;
		updateZoom();
	}

	private void updateRotation() {
		log.info("updating zoom " + zoom);
		this.objectgroup.setRotate(rotatex);
	}

	private void updateZoom() {
		log.info("updating zoom " + zoom);
		this.objectgroup.setScaleX(zoom);
		this.objectgroup.setScaleY(zoom);
		this.objectgroup.setScaleZ(zoom);
	}

	@Override
	public void gesture(GestureEvent arg0) {
		log.info("gesture " + arg0);
	}

	private void createScene(FXCanvas canvas) {
		/* Create a JavaFX Group node */
		this.scenegroup = new Group();
		/* Create the Scene instance and set the group node as root */
		org.eclipse.swt.graphics.Color bg = getBackground();
		double red = bg.getRed() / 255.0 * 0.8f;
		double green = bg.getGreen() / 255.0 * 0.8f;
		double blue = bg.getBlue() / 255.0;
		Scene scene = new Scene(scenegroup, new Color(red, green, blue, 1));// ,
		// Color.rgb(getBackground().getRed(),
		// getBackground().getGreen(),
		// getBackground().getBlue()));
		this.camera = new PerspectiveCamera(true);
		scene.setCamera(camera);
		//
		this.cameraGroup = new Group();
		cameraGroup.getChildren().add(camera);
		scenegroup.getChildren().add(cameraGroup);
		cameraGroup.setTranslateZ(-75);
		this.objectgroup = new Group();
		scenegroup.getChildren().add(objectgroup);

		/* Attach an external stylesheet */
		scene.getStylesheets().add("twobuttons/Buttons.css");
		//
		createAmbientLight();

		updateRotation();
		updateZoom();

		canvas.setScene(scene);
	}

	private void createAmbientLight() {
		AmbientLight light = new AmbientLight();
		Group lightgroup = new Group();
		lightgroup.getChildren().add(light);
		scenegroup.getChildren().add(lightgroup);
	}

	public Group addModel(LOT3DModel b) {
		try {
			X3dModelImporter x3dImporter = new X3dModelImporter();
			File modelFile = b.getModelFile();
			log.info("reading " + modelFile);
			x3dImporter.read(modelFile);
			//
			Group ogroup = new Group();
			groups.put(b, ogroup);

			Node[] rootNodes = x3dImporter.getImport();
			log.info("imported nodes " + rootNodes);
			for (Node node : rootNodes) {
				ogroup.getChildren().add(node);
			}
			//
			objectgroup.getChildren().add(ogroup);
			//
			return ogroup;
		} catch (IOException | SAXException e) {
			log.error(this, "importModel", e);
			LOTMessageDialog d = new LOTMessageDialog(getShell());
			d.show(e);
			return null;
		}
	}

	public void refresh(LOT3DModel b) {
		Group group = groups.get(b);
		if (group != null) {
			double s = b.getScale();
			group.setScaleX(s);
			group.setScaleY(s);
			group.setScaleZ(s);
			//
			LVector t = b.getTranslation();
			group.setTranslateX(s);
		} else {
			log.info("calling refresh with 3dmodel " + b + "... group is null");
		}
	}
}
