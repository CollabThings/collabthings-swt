package org.libraryofthings.swt.view;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jgraph.JGraph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.model.LOTRuntimeObject;

public class MapView extends Composite {

	private MapObject root;

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

		root = new MapObject("root");
		root.add("test1");
		MapObject test2 = root.add("test2");
		test2.add("test21");
		test2.add("test22");

		JGraph jgraph = new JGraph();
		c.add(jgraph);
	}

	@Override
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		layout();
		paint(gc, root);
	}

	private void layoutMapObject(MapObject o) {
		if (o.parent != null) {
			o.y = o.parent.y + 20;
		}

		if (o.cs != null) {
			for (MapObject c : o.cs) {
				layoutMapObject(c);
			}
		}

	}

	private void paint(GC gc, MapObject o) {
		gc.drawText("" + o, o.x, o.y);

		ListenableGraph g = new ListenableDirectedGraph(DefaultEdge.class);

		g.addVertex("test");

		if (o.cs != null) {
			for (MapObject c : o.cs) {
				paint(gc, c);
			}
		}
	}

	public void set(LOTRunEnvironment env) {
		root = new MapObject("" + env);

		Set<LOTRuntimeObject> rs = env.getRunObjects();
		for (LOTRuntimeObject runo : rs) {
			MapObject cmo = root.add("" + runo);
		}

		layoutMapObject(root);
	}

	class MapObject {
		int x, y;
		String text;
		private LinkedList<MapObject> cs;
		private MapObject parent;

		private final String name;

		public MapObject(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name.toUpperCase();
		}

		public void add(MapObject c) {
			getChildren().add(c);
			c.setParent(this);
		}

		private void setParent(MapObject lotObject) {
			this.parent = lotObject;
		}

		public List<MapObject> getChildren() {
			if (cs == null) {
				cs = new LinkedList<MapObject>();
			}

			return cs;
		}

		public MapObject add(String name) {
			MapObject m = new MapObject(name);
			add(m);
			return m;
		}

		public String getName() {
			return name;
		}
	}
}
