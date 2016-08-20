package org.collabthings.swt.controls;

import org.collabthings.model.CTMaterial;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.controls.dialogs.CTMaterialColorPopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CTMaterialEditor extends CTComposite {
	private CTMaterial material;
	private Composite composite;
	private CTLabel lmaterial;
	private CTLabel lcolor;
	private Composite ccolorandclick;
	private CTMaterialColorPopupDialog dialog;
	private Label lC;

	public CTMaterialEditor(Composite c, CTMaterial m) {
		super(c, SWT.None);
		this.material = m;

		setLayout(new GridLayout(1, false));

		lmaterial = new CTLabel(this, SWT.NONE);
		lmaterial.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lmaterial.setText("Material");

		composite = new CTComposite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(5, false));

		lcolor = new CTLabel(composite, SWT.NONE);
		lcolor.setText("Color");

		ccolorandclick = new Composite(composite, SWT.NONE);
		ccolorandclick.setLayout(new GridLayout(1, false));

		lC = new Label(ccolorandclick, SWT.NONE);
		lC.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				clicked();
			}
		});
		lC.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lC.setAlignment(SWT.CENTER);
		lC.setText("C");

		ccolorandclick.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				clicked();
			}

		});

		updateColor();

	}

	private void clicked() {
		if (dialog == null) {
			dialog = new CTMaterialColorPopupDialog(getShell(), material, this);
			dialog.setListener(this);
			dialog.open();
			dialog = null;
		}
	}

	public void colorChanged(double ered, double egreen, double eblue) {
		material.getColor()[0] = checkValue(ered);
		material.getColor()[1] = checkValue(egreen);
		material.getColor()[2] = checkValue(eblue);

		updateColor();
	}

	private void updateColor() {
		Color color = SWTResourceManager.getColor(SWTResourceManager.getRGBWithDoubled(material.getColor()[0],
				material.getColor()[1], material.getColor()[2]));
		ccolorandclick.setBackground(color);
		ccolorandclick.setForeground(color);
		lC.setBackground(color);
		lC.setForeground(color);
	}

	private double checkValue(double d) {
		if (d < 0) {
			d = 0;
		} else if (d > 1) {
			d = 1;
		}
		return d;

	}
}
