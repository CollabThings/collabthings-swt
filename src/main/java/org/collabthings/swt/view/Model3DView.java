package org.collabthings.swt.view;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.embed.swt.FXCanvas;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import org.collabthings.math.LVector;
import org.collabthings.model.LOTBinaryModel;
import org.collabthings.model.LOTMaterial;
import org.collabthings.model.LOTModel;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.GestureEvent;
import org.eclipse.swt.events.GestureListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class Model3DView extends Composite implements GestureListener {
	private LLog log = LLog.getLogger(this);

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
	private Map<LOTBinaryModel, Group> groups = new HashMap<>();
	private FXCanvas canvas;

	public Model3DView(Composite c_view, int style) {
		super(c_view, style);

		setLayout(new FillLayout(SWT.HORIZONTAL));
		//
		canvas = new FXCanvas(this, SWT.NONE) {
			public Point computeSize(int wHint, int hHint, boolean changed) {
				getScene().getWindow().sizeToScene();
				int width = (int) getScene().getWidth();
				int height = (int) getScene().getHeight();
				return new Point(width, height);
			}
		};
		createScene();

		canvas.addGestureListener(this);
		canvas.addMouseWheelListener(arg0 -> doMouseScrolled(arg0));
		canvas.addMouseMoveListener(arg0 -> doMouseMoved(arg0));

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
			this.rotatex += 0.3 * dx;
			this.rotatey -= 0.3 * dy;
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
		Rotate rx = new Rotate();
		rx.setAxis(Rotate.X_AXIS);
		Rotate ry = new Rotate();
		ry.setAxis(Rotate.Y_AXIS);
		Rotate rz = new Rotate();
		rz.setAxis(Rotate.Z_AXIS);

		rx.setAngle(this.rotatey);
		ry.setAngle(this.rotatex);

		objectgroup.getTransforms().clear();
		objectgroup.getTransforms().addAll(rx, rz, ry);
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

	private void createScene() {
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
		//
		cameraGroup.setTranslateZ(-35);
		this.objectgroup = new Group();
		scenegroup.getChildren().add(objectgroup);

		/* Attach an external stylesheet */
		scene.getStylesheets().add("twobuttons/Buttons.css");
		//
		createLights();

		updateRotation();
		updateZoom();

		canvas.setScene(scene);
	}

	private void createLights() {
		AmbientLight light = new AmbientLight();
		light.setColor(Color.DARKGRAY);
		Group lightgroup = new Group();
		// lightgroup.getChildren().add(light);

		Color pc = Color.WHITE;
		PointLight p = new PointLight(pc);
		lightgroup.getChildren().add(p);

		lightgroup.setTranslateX(10);
		lightgroup.setTranslateY(10);
		lightgroup.setTranslateZ(-35);

		scenegroup.getChildren().add(lightgroup);
	}

	public Group getGroup(LOTBinaryModel model) {
		return groups.get(model);
	}

	public Group addModel(LOTMaterial material, LOTModel model) {
		Group ogroup = new Group();
		getDisplay().asyncExec(() -> {
			createScene();
			model.addTo(ogroup);
			refresh(model);
		});

		return ogroup;
	}

	public void refresh(LOTModel lot3dModel) {
		Group group = groups.get(lot3dModel);

		if (group != null) {
			double s = lot3dModel.getScale();
			group.setScaleX(s);
			group.setScaleY(s);
			group.setScaleZ(s);
			//
			LVector t = lot3dModel.getTranslation();
			group.setTranslateX(t.x);
			group.setTranslateY(t.y);
			group.setTranslateZ(t.z);
		}
	}

	public void clear() {
		ObservableList<Node> cs = objectgroup.getChildren();
		cs.clear();
		// cs.get(0).setDisable(true);
	}
}
