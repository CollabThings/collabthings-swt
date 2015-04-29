package org.collabthings.swt.view;

import java.util.Set;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.model.LOTRuntimeObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

import com.interactivemesh.jfx.importer.Viewpoint;

public class MapView extends Composite {

	public MapView(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));

		Composite cupper = new Composite(this, SWT.BORDER);
		cupper.setLayout(new GridLayout(1, false));
		cupper.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Label lblNewLabel = new Label(cupper, SWT.NONE);
		lblNewLabel.setText("New Label");

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		Canvas c = new Canvas(composite, SWT.NONE);
		c.setSize(100, 100);

		composite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent arg0) {
				c.setSize(composite.getSize().x, composite.getSize().y);
			}
		});

		Graph g = new Graph(composite, SWT.NONE);
		GraphNode n1 = new GraphNode(g, SWT.NONE, "node1");
		GraphNode n2 = new GraphNode(g, SWT.NONE, "node2");
		GraphNode n3 = new GraphNode(g, SWT.NONE, "node3");
		GraphNode n4 = new GraphNode(g, SWT.NONE, "node4");
		new GraphConnection(g, SWT.NONE, n1, n2);
		new GraphConnection(g, SWT.NONE, n1, n3);
		new GraphConnection(g, SWT.NONE, n3, n4);

		g.setLayoutAlgorithm(new SpringLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		
	}

	public void set(LOTRunEnvironment env) {
		Set<LOTRuntimeObject> rs = env.getRunObjects();
		for (LOTRuntimeObject runo : rs) {
		}
	}

}
