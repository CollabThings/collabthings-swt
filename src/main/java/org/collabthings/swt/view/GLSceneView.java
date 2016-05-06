package org.collabthings.swt.view;

import java.awt.Frame;

import org.collabthings.swt.SWTResourceManager;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;

public class GLSceneView extends Composite {

	private GLCanvas glcanvas;
	private GLContext glcontext;

	private LLog log = LLog.getLogger(this);

	private LOTGLScene scene;

	public GLSceneView(Composite parent) {
		super(parent, SWT.NONE);

		scene = new LOTGLSceneImpl();

		Composite cthis = this;

		GridLayout gridLayout = new GridLayout(1, true);
		this.setLayout(gridLayout);

		Composite c = new Composite(this, SWT.EMBEDDED);
		c.setBackground(SWTResourceManager.getColor(248, 100, 100));
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.setBackground(SWTResourceManager.getColor(248, 100, 255));

		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		final GLCanvas glcanvas = new GLCanvas(glcapabilities);

		Frame f = SWT_AWT.new_Frame(c);
		f.add(glcanvas);

		Animator a = new Animator(glcanvas);
		a.start();

		glcanvas.addGLEventListener(new GLEventListener() {

			@Override
			public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height) {
				scene.setup(glautodrawable.getGL().getGL2(), width, height);
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
				scene.render(glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(),
						glautodrawable.getSurfaceHeight());
			}
		});

	}

}
