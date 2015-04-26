package org.libraryofthings.swt.view;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.libraryofthings.LLog;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.LOTAppControl;
import org.libraryofthings.swt.SWTResourceManager;
import org.libraryofthings.swt.app.LOTApp;
import org.libraryofthings.swt.controls.ObjectViewer;
import org.libraryofthings.swt.controls.ObjectViewerListener;
import org.libraryofthings.swt.dialog.LOTMessageDialog;
import org.xml.sax.SAXException;

public class PartView extends Composite implements LOTAppControl {
	private static final String DEFAULT_X3D_IMPORTPATH = "lot.gui.default.import_path";
	private LOTPart part;
	private ObjectViewer partobjectviewer;
	private Model3DView partcanvas;
	private LLog log = LLog.getLogger(this);
	private final LOTApp app;
	private final AppWindow window;
	private ObjectViewer modelobjectviewer;

	public PartView(Composite composite, LOTApp app, AppWindow window, LOTPart p) {
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

	private void init() {
		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		Composite c_toolbar = new Composite(this, SWT.NONE);
		c_toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		c_toolbar.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button btnImport = new Button(c_toolbar, SWT.FLAT);
		btnImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				importSelected();
			}
		});
		btnImport.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));
		btnImport.setText("+");

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
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite c_partproperties = new Composite(composite_main, SWT.NONE);
		c_partproperties.setLayout(new FillLayout(SWT.VERTICAL));
		c_partproperties.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
		c_partproperties.setBounds(0, 0, 64, 64);

		createPartDataViewer(c_partproperties);
		createModelDataViewer(c_partproperties);

		Composite c_view = new Composite(composite_main, SWT.NONE);
		c_view.setLayout(new FillLayout(SWT.HORIZONTAL));
		c_view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c_view.setBounds(0, 0, 64, 64);
		//

		partcanvas = new Model3DView(c_view, SWT.NONE);
		composite_main.setWeights(new int[] { 136, 311 });

	}

	protected void publish() {
		this.part.publish();
	}

	private void createModelDataViewer(Composite c_partproperties) {
		this.modelobjectviewer = new ObjectViewer(app, window, c_partproperties, part.getModel());
		this.modelobjectviewer.addListener(new ObjectViewerListener() {
			@Override
			public void valueChanged(String name, Object o) {
				modelObjectChanged(name, o);
			}
		});
	}

	private void createPartDataViewer(Composite c_partproperties) {
		this.partobjectviewer = new ObjectViewer(app, window, c_partproperties, part);
		partobjectviewer.addListener(new ObjectViewerListener() {
			@Override
			public void valueChanged(String name, Object o) {
				partObjectChanged(name, o);
			}
		});
	}

	private void modelObjectChanged(String name, Object value) {
		this.partcanvas.refresh(part.getModel());
	}

	protected void partObjectChanged(String name, Object o) {
		// TODO Auto-generated method stub
	}

	protected void importSelected() {
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.x3d" });
		String path = app.getLClient().getPreferences().get(DEFAULT_X3D_IMPORTPATH, "");
		dialog.setFilterPath(path);
		String result = dialog.open();

		try {
			File file = new File(result);
			app.getLClient().getPreferences().set(DEFAULT_X3D_IMPORTPATH, file.getParent());
			importFile(file);
		} catch (SAXException | IOException e) {
			LOTMessageDialog d = new LOTMessageDialog(getShell());
			d.show(e);
		}
	}

	public void importFile(File file) throws SAXException, IOException {
		log.info("loading model " + file);
		part.importModel(file);
		this.partcanvas.addModel(part.getModel());
		modelobjectviewer.setObject(part.getModel());
	}
}
