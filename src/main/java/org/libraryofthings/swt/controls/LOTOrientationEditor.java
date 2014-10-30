package org.libraryofthings.swt.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.libraryofthings.math.LOrientation;

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

		el = new LOTVectorEditor(this, o.location, v -> listener.changed(o));
		en = new LOTVectorEditor(this, o.normal, v -> {
			if (en != null) {
				o.normal.normalize();
				en.updateValues();
				listener.changed(o);
			}
		});
		ea = createField((Double) o.angle);
	}

	private LOTDoubleEditor createField(Double value) {
		LOTDoubleEditor ley = new LOTDoubleEditor(this, value,
				e -> angleChanged(e));
		ley.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		return ley;
	}

	private void angleChanged(double na) {
		o.angle = na;
		listener.changed(o);
	}

	public static interface ChangeListener<T> {
		void changed(T t);
	}
}
