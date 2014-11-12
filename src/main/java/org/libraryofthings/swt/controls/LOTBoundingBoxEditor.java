package org.libraryofthings.swt.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.libraryofthings.model.LOTBoundingBox;

public class LOTBoundingBoxEditor extends Composite {

	private ChangeListener<LOTBoundingBox> listener;
	private LOTVectorEditor el;
	private LOTVectorEditor en;
	private LOTBoundingBox box;

	public LOTBoundingBoxEditor(Composite c, LOTBoundingBox no,
			ChangeListener<LOTBoundingBox> listener) {
		super(c, SWT.None);
		this.box = no;
		this.listener = listener;
		//
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 4;
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