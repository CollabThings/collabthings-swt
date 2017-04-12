/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/
package org.collabthings.swt.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.impl.CTFactoryState;
import org.collabthings.environment.impl.CTToolUser;
import org.collabthings.model.CTAttachedFactory;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTRuntimeObject;
import org.collabthings.model.CTTool;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

public class MapView extends CTComposite {

	private Graph graph;
	private Map<Object, GraphNode> nodes = new HashMap<Object, GraphNode>();

	private List<Integer> rows = new LinkedList<Integer>();

	public MapView(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));

		Composite cupper = new CTComposite(this, SWT.BORDER);
		cupper.setLayout(new GridLayout(1, false));
		cupper.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		new CTLabel(cupper, SWT.NONE);

		Composite composite = new CTComposite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		Canvas c = new Canvas(composite, SWT.NONE);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		composite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent arg0) {
				c.setSize(composite.getSize().x, composite.getSize().y);
			}
		});
		c.setLayout(new GridLayout(1, false));

		graph = new Graph(c, SWT.NONE);
		graph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		c.addPaintListener((e) -> {
			GC gc = e.gc;
			graph.getContents().paint(new SWTGraphics(gc));
		});
	}

	public void set(CTRunEnvironment env) {
		clear();

		if (env != null) {
			GraphNode envnode = new GraphNode(graph, SWT.NONE, "Env " + env);

			Set<CTRuntimeObject> rs = env.getRunObjects();
			for (CTRuntimeObject runo : rs) {
				if (runo instanceof CTFactoryState) {
					CTFactoryState state = (CTFactoryState) runo;
					addFactoryState(1, envnode, state);
				}
			}

			SpringLayoutAlgorithm springLayoutAlgorithm = new MapLayout();
			// springLayoutAlgorithm.setSpringGravitation(10);

			graph.setLayoutAlgorithm(springLayoutAlgorithm, true);
		}
	}

	private void addFactoryState(int depth, GraphNode envnode,
			CTFactoryState state) {
		GraphNode factorystatenode = getNode(state, "" + state);
		factorystatenode.setBackgroundColor(graph.LIGHT_BLUE);
		setNodeLocation(depth, factorystatenode);

		new GraphConnection(graph, SWT.NONE, envnode, factorystatenode);

		CTFactory factory = state.getFactory();
		addFactory(depth + 3, factorystatenode, factory);

		List<CTFactoryState> fs = state.getFactories();
		for (CTFactoryState childstate : fs) {
			addFactoryState(depth + 3, factorystatenode, childstate);
		}

		List<CTToolUser> toolusers = state.getToolUsers();
		for (CTToolUser tooluser : toolusers) {
			String toolname = tooluser.getName();
			if (toolname == null) {
				toolname = "" + tooluser;
			}

			GraphNode toolusernode = getNode(tooluser, toolname);
			setNodeLocation(depth, toolusernode);
			new GraphConnection(graph, SWT.NONE, factorystatenode, toolusernode);
		}
	}

	private void setNodeLocation(int depth, GraphNode node) {
		while (rows.size() <= depth) {
			rows.add(0);
		}

		int x = rows.get(depth);
		rows.set(depth, x + 1);

		node.setLocation(x * 100, depth * 50);
	}

	private void addFactory(int depth, GraphNode parent, CTFactory factory) {
		GraphNode factorynode = getNode(factory, "" + factory);
		setNodeLocation(depth, factorynode);

		new GraphConnection(graph, SWT.NONE, parent, factorynode);

		Set<String> fs = factory.getFactories();
		for (String string : fs) {
			CTAttachedFactory childfactory = factory.getFactory(string);
			addFactory(depth + 3, factorynode, childfactory.getFactory());
		}

		addEnvironment(depth + 1, factorynode, factory.getEnvironment());
	}

	private void addEnvironment(int depth, GraphNode parent,
			CTEnvironment environment) {
		Set<String> tools = environment.getTools();

		for (String string : tools) {
			CTTool tool = environment.getTool(string);
			GraphNode toolnode = getNode(tool, string);
			setNodeLocation(depth, toolnode);
			new GraphConnection(graph, SWT.NONE, parent, toolnode);
		}
	}

	private GraphNode getNode(Object o, String text) {
		GraphNode node = nodes.get(o);
		if (node == null) {
			node = new GraphNode(graph, SWT.NONE, text, o);
			nodes.put(o, node);
		}

		return node;
	}

	private void clear() {
		List<GraphConnection> cs = new LinkedList<GraphConnection>(
				graph.getConnections());
		for (GraphConnection c : cs) {
			c.dispose();
		}

		List<GraphNode> ns = new LinkedList<GraphNode>(graph.getNodes());
		for (GraphNode graphNode : ns) {
			graphNode.dispose();
		}

		nodes.clear();
	}

	private class MapLayout extends SpringLayoutAlgorithm {
		MapLayout() {
			super(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
			setRandom(false);
		}

	}
}
