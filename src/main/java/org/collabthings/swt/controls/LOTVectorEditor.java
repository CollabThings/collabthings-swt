package org.collabthings.swt.controls;

import org.collabthings.math.LVector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LOTVectorEditor extends Composite {

	private ChangeListener<LVector> listener;
	private LOTDoubleEditor ey;
	private LOTDoubleEditor ez;
	private LOTDoubleEditor ex;
	private LVector v;
	private Label lname;

	public LOTVectorEditor(Composite c, LVector nv, ChangeListener<LVector> listener) {
		super(c, SWT.None);
		this.v = nv;
		this.listener = listener;
		//
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 4;
		setLayout(gridLayout);

		if (v == null) {
			v = new LVector();
		}

		lname = new Label(this, SWT.None);
		
		ex = new LOTDoubleEditor(this, (Double) v.x, d -> {
			v.x = d;
			changed();
		});
		ex.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		ey = new LOTDoubleEditor(this, (Double) v.x, d -> {
			v.y = d;
			changed();
		});
		ey.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		ez = new LOTDoubleEditor(this, (Double) v.x, d -> {
			v.z = d;
			changed();
		});
		ez.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		updateValues();
	}

	public void setName(String name) {
		lname.setText(name);
	}
	
	private void changed() {
		listener.changed(getV());
	}

	private LVector getV() {
		return new LVector(ex.getValue(), ey.getValue(), ez.getValue());
	}

	public static interface ChangeListener<T> {
		void changed(T t);
	}

	public void updateValues() {
		ex.setValue(v.x);
		ey.setValue(v.y);
		ez.setValue(v.z);
	}
}
