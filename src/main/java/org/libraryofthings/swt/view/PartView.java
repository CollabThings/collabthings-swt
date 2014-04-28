package org.libraryofthings.swt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.swt.SWTResourceManager;
import org.libraryofthings.swt.controls.ObjectViewer;

public class PartView extends Composite {

	private LOTPart part;
	private ObjectViewer table_properties;

	public PartView(LOTPart p, Composite composite) {
		super(composite, SWT.None);
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

		Button btnNewButton = new Button(c_toolbar, SWT.FLAT);
		btnNewButton.setFont(SWTResourceManager.getFont("Segoe UI", 8,
				SWT.NORMAL));
		btnNewButton.setText("A");

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

		Part3DView partcanvas = new Part3DView(c_view, SWT.NONE);
		composite_main.setWeights(new int[] {91, 356});

	}
}
