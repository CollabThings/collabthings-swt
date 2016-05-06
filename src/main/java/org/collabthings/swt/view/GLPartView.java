package org.collabthings.swt.view;

import java.awt.Frame;

import org.collabthings.model.LOTPart;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;

public class GLPartView extends Composite {

	private LOTPart part;
	private GLCanvas glcanvas;
	private GLContext glcontext;

	private LLog log = LLog.getLogger(this);

	public GLPartView(Composite parent) {
		super(parent, SWT.NONE);

		Composite cthis = this;

		GridLayout gridLayout = new GridLayout(1, false);
		this.setLayout(gridLayout);

		Composite c = new Composite(this, SWT.EMBEDDED);
		c.setBackground(SWTResourceManager.getColor(248, 100, 100));

		this.setBackground(SWTResourceManager.getColor(248, 100, 255));

		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		final GLCanvas glcanvas = new GLCanvas(glcapabilities);

		Frame f = SWT_AWT.new_Frame(c);
		f.add(glcanvas);
		f.setSize(600, 400);

		this.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent arg0) {
				log.info("this resized " + cthis.getSize());
				c.setSize(cthis.getSize().x, cthis.getSize().y);
			}

			@Override
			public void controlMoved(ControlEvent arg0) {
				// TODO Auto-generated method stub
			}
		});

		c.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent arg0) {
				log.info("composite resized " + c.getSize());
				f.setSize(c.getSize().x, c.getSize().y);
			}

			@Override
			public void controlMoved(ControlEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		Animator a = new Animator(glcanvas);
		a.start();

		glcanvas.addGLEventListener(new GLEventListener() {

			@Override
			public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height) {
				OneTriangle.setup(glautodrawable.getGL().getGL2(), width, height);
			}

			@Override
			public void init(GLAutoDrawable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dispose(GLAutoDrawable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void display(GLAutoDrawable glautodrawable) {
				OneTriangle.render(glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(),
						glautodrawable.getSurfaceHeight());
			}
		});

	}

	private GLContext getContext() {
		if (glcontext == null) {
			glcontext = GLDrawableFactory.getFactory(glcanvas.getGLProfile()).createExternalGLContext();
		}

		return glcontext;
	}

	public void setPart(LOTPart part) {
		this.part = part;
	}

	public static class OneTriangle {
		protected static void setup(GL2 gl2, int width, int height) {
			gl2.glMatrixMode(GL2.GL_PROJECTION);
			gl2.glLoadIdentity();

			// coordinate system origin at lower left with width and height same
			// as the window
			GLU glu = new GLU();
			glu.gluOrtho2D(0.0f, width, 0.0f, height);

			gl2.glMatrixMode(GL2.GL_MODELVIEW);
			gl2.glLoadIdentity();

			gl2.glViewport(0, 0, width, height);
		}

		protected static void render(GL2 gl2, int width, int height) {
			gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

			// draw a triangle filling the window
			gl2.glLoadIdentity();
			gl2.glBegin(GL.GL_TRIANGLES);
			gl2.glColor3f(1, 0, 0);
			gl2.glVertex2f(0, 0);
			gl2.glColor3f(0, 1, 0);
			gl2.glVertex2f(width, 0);
			gl2.glColor3f(0, 0, 1);
			gl2.glVertex2f(width / 2, height);
			gl2.glEnd();
		}
	}
}
