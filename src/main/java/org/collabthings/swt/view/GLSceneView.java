package org.collabthings.swt.view;

import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.ogl.LOTGLScene;
import org.collabthings.ogl.LOTGLSceneImpl;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.collabthings.swt.controls.CTText;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class GLSceneView extends CTComposite {

	private GLCanvas glcanvas;
	private GLContext glcontext;

	private LLog log = LLog.getLogger(this);

	private LOTGLScene scene;

	public GLSceneView(Composite parent) {
		super(parent, SWT.NONE);

		scene = new LOTGLSceneImpl();

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

		Composite c = new CTComposite(this, SWT.EMBEDDED);
		c.setBackground(SWTResourceManager.getColor(248, 100, 100));
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		final GLCanvas glcanvas = new GLCanvas(glcapabilities);

		Frame f = SWT_AWT.new_Frame(c);
		f.add(glcanvas);

		FPSAnimator a = new FPSAnimator(glcanvas, 10);
		a.start();

		RGB rgb = getBackground().getRGB();

		glcanvas.addGLEventListener(new GLEventListener() {

			@Override
			public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height) {
				scene.setup(glautodrawable.getGL().getGL2(), width, height);
			}

			@Override
			public void init(GLAutoDrawable drawable) {
				GL gl = drawable.getGL();

				// Global settings.
				gl.glEnable(GL.GL_DEPTH_TEST);
				gl.glDepthFunc(GL.GL_LEQUAL);
				gl.glClearColor(rgb.red / 256.0f, rgb.green / 256.0f, rgb.blue / 256.0f, 1f);
			}

			@Override
			public void dispose(GLAutoDrawable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void display(GLAutoDrawable glautodrawable) {
				scene.render(glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(),
						glautodrawable.getSurfaceHeight());
			}
		});

		glcanvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				scene.setMouseDown(false);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				scene.setMouseDown(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		glcanvas.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				scene.mouseMoved(e.getX(), e.getY());
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				scene.mouseMoved(e.getX(), e.getY());
			}
		});

		glcanvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				scene.mouseScroll(e.getUnitsToScroll());
			}
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
	}

}
