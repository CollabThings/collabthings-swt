package org.collabthings.swt.view.parteditor;

import java.util.Stack;

import org.collabthings.environment.impl.CTRunEnvironmentImpl;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTSubPart;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTTabFolder;
import org.collabthings.swt.controls.ObjectViewer;
import org.collabthings.swt.view.GLSceneView;
import org.collabthings.swt.view.ObjectTreeView;
import org.collabthings.swt.view.ObjectTreeView.PartListener;
import org.collabthings.swt.view.ObjectTreeView.SubpartListener;
import org.collabthings.swt.view.YamlEditor;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class PartEditor extends CTComposite implements LOTAppControl {
	private CTPart part;

	private LLog log = LLog.getLogger(this);
	private final LOTApp app;

	private GLSceneView view;

	private CTRunEnvironmentImpl rune;

	private Stack<CTPart> parts = new Stack<CTPart>();

	private YamlEditor csource;

	private final AppWindow window;

	private ObjectViewer viewer;

	private ScrolledComposite scrolledComposite;

	private ObjectTreeView ctree;

	public PartEditor(Composite composite, LOTApp app, AppWindow window, CTPart p) {
		super(composite, SWT.None);
		this.app = app;
		this.part = p;
		this.window = window;

		log.info("init " + p.toString());

		init();

		getDisplay().asyncExec(() -> {
			setPart(p);
		});
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public MenuItem createMenu(Menu menu) {
		return null;
	}

	@Override
	public String getControlName() {
		return "part " + part.getName();
	}

	@Override
	public void selected(AppWindow appWindow) {

	}

	private void goBack() {
		if (!parts.isEmpty()) {
			CTPart p = parts.pop();
			setPart(p);
		}
	}

	private void pushPart(CTPart p) {
		if (this.part != null) {
			parts.push(part);
		}
		setPart(p);
	}

	public void setPart(CTPart p) {
		this.part = p;
		view.setPart(part);
		viewer.setObject(p);
		updateInfo();

		this.ctree.setPart(p);
	}

	private void init() {
		setBackground(SWTResourceManager.getControlBg());

		GridLayout gridLayout = new GridLayout(2, false);
		setLayout(gridLayout);
		new Label(this, SWT.NONE);

		Composite c_toolbar = new CTComposite(this, SWT.NONE);
		c_toolbar.setBackground(SWTResourceManager.getActiontitleBackground());
		c_toolbar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		RowLayout rl_c_toolbar = new RowLayout(SWT.HORIZONTAL);
		rl_c_toolbar.spacing = 5;
		rl_c_toolbar.center = true;
		c_toolbar.setLayout(rl_c_toolbar);

		CTButton button_1 = new CTButton(c_toolbar, SWT.NONE);
		button_1.addSelectionListener(() -> {
			goBack();
		});
		button_1.setText("<");

		CTButton button = new CTButton(c_toolbar, SWT.FLAT);
		button.addSelectionListener(() -> {
			part.newSubPart();
		});
		button.setText("A");

		CTButton bnewscad = new CTButton(c_toolbar, SWT.FLAT);
		bnewscad.addSelectionListener(() -> {
			part.save();
			part.newSCAD();
		});
		bnewscad.setText("set SCAD");

		CTButton btnPublish = new CTButton(c_toolbar, SWT.NONE);
		btnPublish.addSelectionListener(() -> {
			publish();
		});
		btnPublish.setText("Publish");
		new Label(this, SWT.NONE);

		SashForm composite_main = new SashForm(this, SWT.NONE);
		composite_main.setBackground(SWTResourceManager.getControlBg());
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		CTTabFolder tabFolder = new CTTabFolder(composite_main, SWT.NONE);

		scrolledComposite = new ScrolledComposite(tabFolder.getComposite(), SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

		ctree = new ObjectTreeView(tabFolder.getComposite(), SWT.NONE);

		tabFolder.addTab("Tree", ctree, null);
		tabFolder.addTab("properties", scrolledComposite, null, false);

		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		viewer = new ObjectViewer(app, window, scrolledComposite);

		csource = new YamlEditor(tabFolder.getComposite(), SWT.NONE, "source");
		tabFolder.addTab("Source", csource, null, false);

		csource.setObject(this.part);

		tabFolder.addSelectionListener(() -> {
			updateLayout();
		});

		scrolledComposite.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				updateLayout();
			}
		});

		viewer.addObjectChangeListener(() -> objectChanged());

		ctree.addSubpartListener(new SubpartListener() {

			@Override
			public void hoverOver(CTSubPart subpart) {
				view.setHighlight(subpart);
			}
		});

		ctree.addPartListener(new PartListener() {
			@Override
			public void view(CTPart part) {
				pushPart(part);
			}
		});

		view = new GLSceneView(composite_main);
		view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		view.setBounds(0, 0, 64, 64);
		new Label(this, SWT.NONE);

		composite_main.setWeights(new int[] { 100, 200 });
		new Label(this, SWT.NONE);

		objectChanged();
	}

	private void objectChanged() {
		updateInfo();
		ctree.updatePart();
		scrolledComposite.setContent(viewer);
	}

	private void updateInfo() {
		updateLayout();
	}

	private void addCopy(CTSubPart subpart) {
		CTSubPart nsub = this.part.newSubPart();
		nsub.setOrientation(subpart.getLocation(), subpart.getNormal(), subpart.getAngle());
		nsub.setPart(subpart.getPart());
	}

	private void view(CTSubPart p) {
		pushPart(p.getPart());
	}

	private void updateLayout() {
		getDisplay().asyncExec(() -> {
			if (scrolledComposite != null && viewer != null) {
				Rectangle clientArea = scrolledComposite.getClientArea();
				int w = clientArea.width;

				viewer.layout();
				viewer.redraw();
				viewer.pack();

				int height = viewer.computeSize(w, SWT.DEFAULT).y;
				if (height < clientArea.height) {
					height = clientArea.height;
				}

				scrolledComposite.setMinSize(w, height);
			}
		});
	}

	protected void publish() {
		this.part.publish();
	}
}
