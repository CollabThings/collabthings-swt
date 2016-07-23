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
import org.collabthings.swt.view.ObjectContextView;
import org.collabthings.swt.view.ObjectContextView.PartListener;
import org.collabthings.swt.view.ObjectContextView.SubpartListener;
import org.collabthings.swt.view.YamlEditor;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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

	private ObjectContextView ctree;

	private int scrolledareaw;

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

		SashForm composite_main = new SashForm(this, SWT.BORDER);
		composite_main.setBackground(SWTResourceManager.getControlBg());
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		CTTabFolder tabFolder = new CTTabFolder(composite_main, SWT.NONE);

		ctree = new ObjectContextView(app, window, tabFolder.getComposite(), SWT.NONE);

		tabFolder.addTab("Tree", ctree, null);

		viewer = new ObjectViewer(app, window, tabFolder.getComposite());
		tabFolder.addTab("properties", viewer, null, false);

		csource = new YamlEditor(tabFolder.getComposite(), SWT.NONE, "source");
		tabFolder.addTab("Source", csource, null, false);

		csource.setObject(this.part);

		tabFolder.addSelectionListener(() -> {
			// updateLayout();
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

		composite_main.setWeights(new int[] { 100, 200 });

		objectChanged();
	}

	private void objectChanged() {
		updateInfo();
		ctree.updatePart();
	}

	private void updateInfo() {
		// updateLayout();
	}

	private void addCopy(CTSubPart subpart) {
		CTSubPart nsub = this.part.newSubPart();
		nsub.setOrientation(subpart.getLocation(), subpart.getNormal(), subpart.getAngle());
		nsub.setPart(subpart.getPart());
	}

	private void view(CTSubPart p) {
		pushPart(p.getPart());
	}

	protected void publish() {
		this.part.publish();
		this.app.getLClient().publish(part.getName(), part);
	}
}
