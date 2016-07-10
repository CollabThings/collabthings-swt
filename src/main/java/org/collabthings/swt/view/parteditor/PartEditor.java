package org.collabthings.swt.view.parteditor;

import java.util.List;
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
import org.collabthings.swt.controls.CTLabel;
import org.collabthings.swt.controls.CTTabFolder;
import org.collabthings.swt.controls.LOTDoubleEditor;
import org.collabthings.swt.controls.LOTVectorEditor;
import org.collabthings.swt.controls.ObjectViewer;
import org.collabthings.swt.view.GLSceneView;
import org.collabthings.swt.view.YamlEditor;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
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

	private Composite csubparts;

	private Stack<CTPart> parts = new Stack<CTPart>();

	private YamlEditor csource;

	private final AppWindow window;

	private ObjectViewer viewer;

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
		updateInfo();
	}

	private void init() {
		setBackground(SWTResourceManager.getControlBg());

		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		Composite c_toolbar = new CTComposite(this, SWT.NONE);
		c_toolbar.setBackground(SWTResourceManager.getActiontitleBackground());
		c_toolbar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		RowLayout rl_c_toolbar = new RowLayout(SWT.HORIZONTAL);
		rl_c_toolbar.spacing = 15;
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

		SashForm composite_main = new SashForm(this, SWT.NONE);
		composite_main.setBackground(SWTResourceManager.getControlBg());
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		CTTabFolder tabFolder = new CTTabFolder(composite_main, SWT.NONE);

		ScrolledComposite scrolledComposite = new ScrolledComposite(tabFolder.getComposite(),
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tabFolder.addTab("properties", scrolledComposite, null);

		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		viewer = new ObjectViewer(app, window, scrolledComposite, part);

		scrolledComposite.setContent(viewer);
		scrolledComposite.setMinSize(viewer.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		csource = new YamlEditor(tabFolder.getComposite(), SWT.NONE, "source");
		tabFolder.addTab("Source", csource, null);

		csource.setObject(this.part);

		view = new GLSceneView(composite_main);
		view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		view.setBounds(0, 0, 64, 64);
		composite_main.setWeights(new int[] { 1, 1 });

		// composite_main.setWeights(new int[] { 1, 1 });

		Composite cbottom = new CTComposite(this, SWT.NONE);
		GridLayout gl_cbottom = new GridLayout(3, false);
		gl_cbottom.marginHeight = 1;
		gl_cbottom.verticalSpacing = 1;
		gl_cbottom.marginWidth = 1;
		gl_cbottom.horizontalSpacing = 1;
		cbottom.setLayout(gl_cbottom);
		cbottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		CTButton bleft = new CTButton(cbottom, SWT.NONE);
		bleft.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		bleft.setText("<");

		csubparts = new CTComposite(cbottom, SWT.NONE);
		csubparts.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridData gd_csubparts = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_csubparts.heightHint = 95;
		csubparts.setLayoutData(gd_csubparts);

		CTButton bright = new CTButton(cbottom, SWT.NONE);
		new Label(cbottom, SWT.NONE);
		new Label(cbottom, SWT.NONE);
		bright.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		bright.setText(">");

		updateInfo();
	}

	private void updateInfo() {
		getDisplay().asyncExec(() -> {
			updatePartInfo();
			updateSubpartList();
		});
	}

	private void updatePartInfo() {
		if (part != null) {
			viewer.setObject(part);
		}
	}

	private void updateSubpartList() {
		for (Control c : csubparts.getChildren()) {
			c.dispose();
		}

		if (part != null) {
			List<CTSubPart> sps = part.getSubParts();
			for (CTSubPart subpart : sps) {
				addSubpartToList(subpart);
			}
		}

		csubparts.layout();
		// addSubpartToList(null);
	}

	private void addSubpartToList(CTSubPart subpart) {

		Composite composite = new CTComposite(csubparts, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.verticalSpacing = 1;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		composite.setLayout(gl_composite);

		CTLabel lsubname = new CTLabel(composite, SWT.NONE);
		lsubname.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));
		lsubname.setText("" + subpart);

		LOTVectorEditor elocation = new LOTVectorEditor(composite, subpart.getLocation(), (e) -> {
			subpart.setOrientation(e, subpart.getNormal(), subpart.getAngle());
		});
		elocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		LOTVectorEditor enormal = new LOTVectorEditor(composite, subpart.getNormal(), (e) -> {
			subpart.setOrientation(subpart.getLocation(), e, subpart.getAngle());
		});
		enormal.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		LOTDoubleEditor eangle = new LOTDoubleEditor(composite, subpart.getAngle(), d -> {
			subpart.setAngle(d);
		});
		FillLayout fl_eangle = (FillLayout) eangle.getLayout();
		eangle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		CTButton b = new CTButton(composite, SWT.NONE);
		b.setText("view");
		b.addSelectionListener(() -> {
			view(subpart);
		});
	}

	private void view(CTSubPart p) {
		pushPart(p.getPart());
	}

	protected void publish() {
		this.part.publish();
	}
}
