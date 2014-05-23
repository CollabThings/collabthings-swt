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
import org.eclipse.swt.widgets.FileDialog;
import org.libraryofthings.LLog;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.swt.SWTResourceManager;
import org.libraryofthings.swt.app.LOTApp;
import org.libraryofthings.swt.controls.ObjectViewer;
import org.libraryofthings.swt.dialog.LOTMessageDialog;
import org.xml.sax.SAXException;

public class PartView extends Composite {
	private static final String DEFAULT_X3D_IMPORTPATH = "lot.gui.default.import_path";
	private LOTPart part;
	private ObjectViewer table_properties;
	private Model3DView partcanvas;
	private LLog log = LLog.getLogger(this);
	private LOTApp app;

	public PartView(LOTApp app, LOTPart p, Composite composite) {
		super(composite, SWT.None);
		this.app = app;
		this.part = p;
		init();
	}

	private PartView(Composite c, int i) {
		super(c, i);
		init();
	}

	private void init() {
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);

		Composite c_toolbar = new Composite(this, SWT.NONE);
		c_toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1));
		c_toolbar.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button btnImport = new Button(c_toolbar, SWT.FLAT);
		btnImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				importSelected();
			}
		});
		btnImport
				.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));
		btnImport.setText("+");

		Button button = new Button(c_toolbar, SWT.FLAT);
		button.setText("A");
		button.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));

		SashForm composite_main = new SashForm(this, SWT.NONE);
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));

		Composite c_partproperties = new Composite(composite_main, SWT.NONE);
		c_partproperties.setLayout(new FillLayout(SWT.HORIZONTAL));
		c_partproperties.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false,
				true, 1, 1));
		c_partproperties.setBounds(0, 0, 64, 64);

		table_properties = new ObjectViewer(c_partproperties, part);

		Composite c_view = new Composite(composite_main, SWT.NONE);
		c_view.setLayout(new FillLayout(SWT.HORIZONTAL));
		c_view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c_view.setBounds(0, 0, 64, 64);
		//

		partcanvas = new Model3DView(c_view, SWT.NONE);
		composite_main.setWeights(new int[] { 91, 356 });

	}

	protected void importSelected() {
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.x3d" });
		String path = app.getEnvironment().getPreferences()
				.get(DEFAULT_X3D_IMPORTPATH, "");
		dialog.setFilterPath(path);
		String result = dialog.open();

		try {
			File file = new File(result);
			app.getEnvironment().getPreferences()
					.set(DEFAULT_X3D_IMPORTPATH, file.getParent());
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
	}
}
