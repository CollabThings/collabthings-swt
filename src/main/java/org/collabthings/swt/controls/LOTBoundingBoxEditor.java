package org.collabthings.swt.controls;

import org.collabthings.model.CTBoundingBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class LOTBoundingBoxEditor extends CTComposite {

	private LOTVectorEditor el;
	private LOTVectorEditor en;
	private CTBoundingBox box;

	public LOTBoundingBoxEditor(final Composite c, final CTBoundingBox no,
			final ChangeListener<CTBoundingBox> listener) {
		super(c, SWT.None);
		this.box = no;
		//
		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		el = new LOTVectorEditor(this, box.getA(), v -> listener.changed(box));
		el.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		en = new LOTVectorEditor(this, box.getB(), v -> listener.changed(box));
		en.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	}

	public static interface ChangeListener<T> {
		void changed(T t);
	}
}
