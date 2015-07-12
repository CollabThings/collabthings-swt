package org.collabthings.swt.controls;

import org.collabthings.math.LOrientation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class LOTOrientationEditor extends Composite {

	private ChangeListener<LOrientation> listener;
	private LOTVectorEditor el;
	private LOTVectorEditor en;
	private LOTDoubleEditor ea;
	private LOrientation o;

	public LOTOrientationEditor(Composite c, LOrientation no,
			ChangeListener<LOrientation> listener) {
		super(c, SWT.None);
		this.o = no;
		this.listener = listener;
		//
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 4;
		setLayout(gridLayout);

		el = new LOTVectorEditor(this, o.getLocation(),
				v -> listener.changed(o));
		el.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		en = new LOTVectorEditor(this, o.getNormal(), v -> {
			if (en != null) {
				o.getNormal().normalize();
				en.updateValues();
				listener.changed(o);
			}
		});

		en.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		ea = new LOTDoubleEditor(this, o.getAngle(), e -> angleChanged(e));
		ea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
	}

	private void angleChanged(double na) {
		o.setAngle(na);
		listener.changed(o);
	}

	public static interface ChangeListener<T> {
		void changed(T t);
	}
}
