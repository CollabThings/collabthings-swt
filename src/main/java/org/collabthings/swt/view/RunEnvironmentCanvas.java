package org.collabthings.swt.view;

import java.awt.Stroke;

import org.collabthings.math.LVector;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.util.LOTGraphics;
import org.collabthings.view.RunEnvironmentDrawerImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class RunEnvironmentCanvas extends Composite implements PaintListener,
		LOTGraphics {

	private RunEnvironmentDrawerImpl drawer;
	private GC gc;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param drawer
	 */
	public RunEnvironmentCanvas(Composite parent, int style,
			RunEnvironmentDrawerImpl drawer) {
		super(parent, SWT.NO_BACKGROUND);
		setBackground(SWTResourceManager.getColor(248, 248, 255));
		setLayout(new GridLayout(1, false));
		addPaintListener(this);
		this.drawer = drawer;
	}

	@Override
	public void paintControl(PaintEvent e) {
		Image bufferImage = new Image(getDisplay(), getWidth(), getHeight());

		gc = new GC(bufferImage);
		;
		if (drawer != null) {
			Font font = new Font(getDisplay(), new FontData("Arial", 9,
					SWT.NONE));
			gc.setFont(font);
			drawer.draw(this);
			gc.drawText("" + drawer, 0, 0);
		} else {
			gc.drawText("drawer null", 0, 0);
		}

		e.gc.drawImage(bufferImage, 0, 0);
		bufferImage.dispose();
	}

	@Override
	public void drawLine(int asx, int asy, int bsx, int bsy) {
		gc.drawLine(asx, asy, bsx, bsy);
	}

	@Override
	public void drawLine(double ax, double ay, double az, double bx, double by,
			double bz) {
		int asx = (int) ax;
		int asy = (int) ay;
		int bsx = (int) bx;
		int bsy = (int) by;
		gc.drawLine(asx, asy, bsx, bsy);
	}

	@Override
	public void drawTriangle(LVector ta, LVector tb, LVector tc, int color) {
		drawLine(ta.x, ta.y, ta.z, tb.x, tb.y, tb.z);
		drawLine(ta.x, ta.y, ta.z, tc.x, tc.y, tc.z);
		drawLine(tc.x, tc.y, tc.z, tb.x, tb.y, tb.z);
	}

	@Override
	public void drawOval(int x, int y, int w, int h) {
		gc.drawOval(x, y, w, h);
	}

	@Override
	public void drawRect(int x, int y, int w, int h) {
		gc.drawRectangle(x, y, w, h);
	}

	@Override
	public void drawString(String string, int x, int y) {
		gc.drawString(string, x, y);
	}

	@Override
	public int getHeight() {
		return getSize().y;
	}

	@Override
	public int getWidth() {
		return getSize().x;
	}

	@Override
	public void setColor(java.awt.Color nc) {
		Color c = new Color(getDisplay(), nc.getRed(), nc.getGreen(),
				nc.getBlue());
		gc.setForeground(c);
	}

	@Override
	public void setStroke(Stroke st) {
		gc.setLineStyle(SWT.LINE_DOT);
	}

	public void callRepaint() {
		if (!isDisposed()) {
			redraw();
		}
	}

	public RunEnvironmentDrawerImpl getDrawer() {
		return this.drawer;
	}
}
