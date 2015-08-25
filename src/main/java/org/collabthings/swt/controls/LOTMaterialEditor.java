package org.collabthings.swt.controls;

import org.collabthings.model.LOTMaterial;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.controls.LOTDoubleEditor.ChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LOTMaterialEditor extends Composite {
	private LOTMaterial material;
	private LOTDoubleEditor ered;
	private LOTDoubleEditor egreen;
	private LOTDoubleEditor eblue;
	private Composite composite;
	private Label lmaterial;
	private Label lcolor;

	public LOTMaterialEditor(Composite c, LOTMaterial m) {
		super(c, SWT.None);
		this.material = m;

		ChangeListener<Double> listener = (e) -> {
			colorChanged();
		};
		setLayout(new GridLayout(1, false));

		lmaterial = new Label(this, SWT.NONE);
		lmaterial.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		lmaterial.setText("Material");
		lmaterial.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		lmaterial.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));

		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		composite.setLayout(new GridLayout(4, false));

		lcolor = new Label(composite, SWT.NONE);
		lcolor.setText("Color");

		ered = new LOTDoubleEditor(composite, m.getColor()[0], listener);
		ered.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		egreen = new LOTDoubleEditor(composite, m.getColor()[1], listener);
		egreen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		eblue = new LOTDoubleEditor(composite, m.getColor()[2], listener);
		eblue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
	}

	private void colorChanged() {
		material.getColor()[0] = checkValue(ered);
		material.getColor()[1] = checkValue(egreen);
		material.getColor()[2] = checkValue(eblue);
	}

	private double checkValue(LOTDoubleEditor e) {
		double d = e.getValue();
		if (d < 0) {
			d = 0;
		} else if (d > 1) {
			d = 1;
		}
		return d;

	}

}
