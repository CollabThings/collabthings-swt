package org.libraryofthings.swt.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.libraryofthings.math.LVector;

public class LOTVectorEditor extends Composite {

	private ChangeListener<LVector> listener;
	private LOTDoubleEditor ey;
	private LOTDoubleEditor ez;
	private LOTDoubleEditor ex;

	public LOTVectorEditor(Composite c, LVector v,
			ChangeListener<LVector> listener) {
		super(c, SWT.None);
		this.listener = listener;
		//
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 4;
		setLayout(gridLayout);

		ex = createField((Double) v.x);
		ey = createField((Double) v.y);
		ez = createField((Double) v.z);
	}

	private LOTDoubleEditor createField(Double value) {
		LOTDoubleEditor ley = new LOTDoubleEditor(this, value, e -> changed());
		ley.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		return ley;
	}

	private void changed() {
		listener.changed(getV());
	}

	private LVector getV() {
		return new LVector(ex.getValue(), ey.getValue(), ez.getValue()
				);
	}

	public static interface ChangeListener<T> {
		void changed(T t);
	}
}
