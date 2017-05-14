package org.collabthings.swt.view.parteditor;

import java.util.Stack;

import org.collabthings.app.CTApp;
import org.collabthings.model.CTObject;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTSubPart;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.CTAppControl;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.CTRunner;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import waazdoh.client.utils.ConditionWaiter;

public class PartEditor extends CTComposite implements CTAppControl {
	private static final String PREFERENCES_SHOWSOURCE = "app.editor.showsource";

	private CTPart part;

	private LLog log = LLog.getLogger(this);
	private final CTApp app;

	private Stack<CTPart> parts = new Stack<>();

	private YamlEditor csource;

	private final AppWindow window;

	private ObjectViewer viewer;

	private ObjectContextView ctree;

	private GLSceneView view;

	public PartEditor(Composite composite, CTApp app, AppWindow window, CTPart p, GLSceneView view) {
		super(composite, SWT.None);
		this.app = app;
		this.part = p;
		this.window = window;
		this.view = view;

		log.info("init " + p.toString());

		init();

		getDisplay().asyncExec(() -> {
			setPart(p);
		});
	}

	@Override
	public CTObject getObject() {
		return part;
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
		appWindow.getMainView().setViewedPart(part);
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
		window.addRunner(new CTRunner<>("PartEditor setPart").run(() -> {
			getDisplay().asyncExec(() -> {
				viewer.setObject(p);
				updateInfo();
				this.ctree.setPart(p);
			});

			log.info("setPartcwait");
			ConditionWaiter.wait(() -> view.isReady(), 60000);
			log.info("setPartcwait done");
			view.setPart(part);
		}));
	}

	private void init() {
		setBackground(SWTResourceManager.getControlBg());

		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		Composite c_toolbar = new CTComposite(this, SWT.NONE);
		c_toolbar.setBackground(SWTResourceManager.getActiontitleBackground());
		c_toolbar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		RowLayout rl_c_toolbar = new RowLayout(SWT.HORIZONTAL);
		rl_c_toolbar.spacing = 5;
		rl_c_toolbar.center = true;
		c_toolbar.setLayout(rl_c_toolbar);

		CTButton bprevious = new CTButton(c_toolbar, SWT.NONE);
		CTButton bnewsubpart = new CTButton(c_toolbar, SWT.FLAT);
		CTButton bnewscad = new CTButton(c_toolbar, SWT.FLAT);
		CTButton bnewhm = new CTButton(c_toolbar, SWT.FLAT);
		CTButton bnewbuilder = new CTButton(c_toolbar, SWT.FLAT);

		CTButton btnPublish = new CTButton(c_toolbar, SWT.NONE);
		bprevious.addSelectionListener(() -> {
			goBack();
		});
		bprevious.setText("<");
		bnewsubpart.addSelectionListener(() -> {
			part.newSubPart();
		});
		bnewsubpart.setText("A");
		bnewscad.addSelectionListener(() -> {
			part.save();
			part.newSCAD();
		});
		bnewscad.setText("SCAD");

		bnewhm.addSelectionListener(() -> {
			part.save();
			part.newHeightmap();
		});
		bnewhm.setText("HM");

		bnewbuilder.addSelectionListener(() -> {
			part.save();
			part.newBuilder();
		});
		bnewbuilder.setText("Builder");

		btnPublish.addSelectionListener(() -> {
			publish();
		});
		btnPublish.setText("Publish");

		SashForm composite_main = new SashForm(this, SWT.BORDER);
		composite_main.setBackground(SWTResourceManager.getControlBg());
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		CTTabFolder tabFolder = new CTTabFolder(composite_main, SWT.NONE);

		ctree = new ObjectContextView(app, window, tabFolder.getComposite(), SWT.NONE);

		tabFolder.addTab("Tree", ctree, null);

		viewer = new ObjectViewer(app, window, tabFolder.getComposite());
		tabFolder.addTab("properties", viewer, null, false);

		if (app.getLClient().getPreferences().getBoolean(PREFERENCES_SHOWSOURCE, false)) {
			csource = new YamlEditor(tabFolder.getComposite(), SWT.NONE, "source");
			tabFolder.addTab("Source", csource, null, false);
			csource.setObject(this.part);
		}

		tabFolder.addSelectionListener(() -> {
			// updateLayout();
		});

		viewer.addObjectChangeListener((e) -> objectChanged());

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
				view.lookAt(part.getViewingProperties().getLookAt());
			}
		});

		composite_main.setWeights(new int[] { 100 });

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

	protected void publish() {
		this.part.publish();
		this.app.getLClient().publish(part.getName(), part);
	}
}
