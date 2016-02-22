package org.collabthings.swt.view.parteditor;

import java.util.List;
import java.util.Stack;

import javafx.embed.swt.FXCanvas;
import javafx.geometry.Point2D;
import javafx.scene.Scene;

import org.collabthings.environment.impl.LOTRunEnvironmentImpl;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTSubPart;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.LOTDoubleEditor;
import org.collabthings.swt.controls.LOTVectorEditor;
import org.collabthings.util.LLog;
import org.collabthings.view.JFXPartView;
import org.collabthings.view.JFXPartView.ViewCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.FlowLayout;

public class PartEditor extends Composite implements LOTAppControl {
	private LOTPart part;

	private LLog log = LLog.getLogger(this);
	private final LOTApp app;
	private final AppWindow window;

	private JFXPartView view;

	private LOTRunEnvironmentImpl rune;

	private Composite c_view;

	private Composite csubparts;
	private Text tpartname;

	private Stack<LOTPart> parts = new Stack<LOTPart>();

	public PartEditor(Composite composite, LOTApp app, AppWindow window,
			LOTPart p) {
		super(composite, SWT.None);
		this.app = app;
		this.window = window;
		this.part = p;
		init();
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
			LOTPart p = parts.pop();
			setPart(p);
		}
	}

	private void pushPart(LOTPart p) {
		if (this.part != null) {
			parts.push(part);
		}
		setPart(p);
	}

	public void setPart(LOTPart p) {
		this.part = p;
		view.setPart(part);
		updatePartInfo();
		updateSubpartList();
	}

	private void init() {
		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		Composite c_toolbar = new Composite(this, SWT.NONE);
		c_toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1));
		RowLayout rl_c_toolbar = new RowLayout(SWT.HORIZONTAL);
		rl_c_toolbar.center = true;
		c_toolbar.setLayout(rl_c_toolbar);

		Button button_1 = new Button(c_toolbar, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				goBack();
			}
		});
		button_1.setText("<");

		Button button = new Button(c_toolbar, SWT.FLAT);
		button.setText("A");
		button.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));

		Button btnPublish = new Button(c_toolbar, SWT.NONE);
		btnPublish.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				publish();
			}
		});
		btnPublish.setText("Publish");

		SashForm composite_main = new SashForm(this, SWT.NONE);
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));

		ScrolledComposite scrolledComposite = new ScrolledComposite(
				composite_main, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite cinfo = new Composite(scrolledComposite, SWT.NONE);
		cinfo.setLayout(new GridLayout(2, false));

		Label lpartname = new Label(cinfo, SWT.NONE);
		lpartname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lpartname.setText("Name");

		tpartname = new Text(cinfo, SWT.BORDER);
		tpartname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		scrolledComposite.setContent(cinfo);
		scrolledComposite.setMinSize(cinfo
				.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		c_view = new Composite(composite_main, SWT.NONE);
		c_view.setLayout(new FillLayout(SWT.HORIZONTAL));
		c_view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c_view.setBounds(0, 0, 64, 64);

		view = new JFXPartView();
		view.setPart(part);

		composite_main.setWeights(new int[] { 1, 1 });

		Composite cbottom = new Composite(this, SWT.NONE);
		GridLayout gl_cbottom = new GridLayout(3, false);
		gl_cbottom.marginHeight = 1;
		gl_cbottom.verticalSpacing = 1;
		gl_cbottom.marginWidth = 1;
		gl_cbottom.horizontalSpacing = 1;
		cbottom.setLayout(gl_cbottom);
		cbottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));

		Button bleft = new Button(cbottom, SWT.NONE);
		bleft.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		bleft.setText("<");

		csubparts = new Composite(cbottom, SWT.NONE);
		csubparts.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		csubparts.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		Button bright = new Button(cbottom, SWT.NONE);
		bright.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1,
				1));
		bright.setText(">");

		startView();
		updatePartInfo();
		updateSubpartList();
	}

	private void updatePartInfo() {
		tpartname.setText("" + part.getName());
	}

	private void updateSubpartList() {
		for (Control c : csubparts.getChildren()) {
			c.dispose();
		}

		List<LOTSubPart> sps = part.getSubParts();
		for (LOTSubPart subpart : sps) {
			addSubpartToList(subpart);
		}

		csubparts.layout();
		// addSubpartToList(null);
	}

	private void addSubpartToList(LOTSubPart subpart) {

		Composite composite = new Composite(csubparts, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.verticalSpacing = 1;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		composite.setLayout(gl_composite);

		Label lsubname = new Label(composite, SWT.NONE);
		lsubname.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1,
				1));
		lsubname.setText("" + subpart);

		LOTVectorEditor elocation = new LOTVectorEditor(composite,
				subpart.getLocation(), (e) -> {
					subpart.setOrientation(e, subpart.getNormal(),
							subpart.getAngle());
				});
		elocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		LOTVectorEditor enormal = new LOTVectorEditor(composite,
				subpart.getNormal(), (e) -> {
					subpart.setOrientation(subpart.getLocation(), e,
							subpart.getAngle());
				});
		enormal.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		LOTDoubleEditor eangle = new LOTDoubleEditor(composite,
				subpart.getAngle(), d -> {
					subpart.setAngle(d);
				});
		FillLayout fl_eangle = (FillLayout) eangle.getLayout();
		eangle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1,
				1));

		Button b = new Button(composite, SWT.NONE);
		b.setText("view");
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				view(subpart);
			}
		});
	}

	private void view(LOTSubPart p) {
		pushPart(p.getPart());
	}

	private void startView() {
		FXCanvas canvas = new FXCanvas(c_view, SWT.BORDER);
		canvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent m) {
				view.mouseScrolled(m.count);
			}
		});
		canvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent m) {
				view.mouseUp(m.x, m.y, m.button);
			}

			@Override
			public void mouseDown(MouseEvent m) {
				view.mouseDown(m.x, m.y, m.button);
			}

			@Override
			public void mouseDoubleClick(MouseEvent m) {

			}
		});
		canvas.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent m) {
				view.mouseMove(m.x, m.y, m.button);
			}
		});

		app.addTask(() -> {
			view.setCanvas(new ViewCanvas() {
				@Override
				public boolean isVisible() {
					return !canvas.isDisposed() && canvas.isVisible();
				}

				@Override
				public void refresh() {
					canvas.redraw();
				}

				@Override
				public Point2D getUpperLeft() {
					Point d = canvas.toDisplay(1, 1);
					return new Point2D(d.x, d.y);
				}

				@Override
				public void setScene(Scene scene) {
					canvas.setScene(scene);
				}

				@Override
				public double getWidth() {
					return canvas.getSize().x;
				}

				@Override
				public double getHeight() {
					return canvas.getSize().y;
				}
			});
		});
	}

	protected void publish() {
		this.part.publish();
	}
}
