package org.libraryofthings.swt.view;

import java.awt.Stroke;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.libraryofthings.view.LOTGraphics;
import org.libraryofthings.view.RunEnviromentDrawer;
import org.eclipse.wb.swt.SWTResourceManager;

public class RunEnvironmentCanvas extends Composite implements PaintListener,
		LOTGraphics {

	private RunEnviromentDrawer drawer;
	private GC gc;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param drawer
	 */
	public RunEnvironmentCanvas(Composite parent, int style,
			RunEnviromentDrawer drawer) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		addPaintListener(this);
		this.drawer = drawer;
	}

	@Override
	public void paintControl(PaintEvent e) {
		gc = e.gc;
		if (drawer != null) {
			drawer.draw(this);
			gc.drawText("" + drawer, 5, 20);
		} else {
			gc.drawText("drawer null", 5, 20);
		}
	}

	@Override
	public void drawLine(int asx, int asy, int bsx, int bsy) {
		gc.drawLine(asx, asy, bsx, bsy);
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
		gc.setLineStyle(SWT.LINE_DASH);
	}

	public void callRepaint() {
		redraw();
	}

	public RunEnviromentDrawer getDrawer() {
		return this.drawer;
	}
}
