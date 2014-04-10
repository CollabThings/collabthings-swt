package org.libraryofthings.swt.controls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ObjectViewer extends Composite {
	public Object objectShown;
	public Label className;
	public Label superClassName;
	public Text toString;
	public Table table;
	private TableColumn colname;
	private TableColumn colvalue;
	private ScrolledComposite scrolledComposite;
	private TableItem tableItem;

	public ObjectViewer(Composite parent, Object o) {
		super(parent, SWT.NONE);
		this.objectShown = o;
		setLayout(new FillLayout(SWT.HORIZONTAL));

		scrolledComposite = new ScrolledComposite(this, SWT.BORDER
				| SWT.V_SCROLL);
		scrolledComposite.setAlwaysShowScrollBars(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		this.table = new Table(scrolledComposite, getStyle());
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		colname = new TableColumn(table, SWT.NONE);
		colname.setWidth(74);
		colname.setText("name");
		colvalue = new TableColumn(table, SWT.NONE);
		colvalue.setWidth(74);
		colvalue.setText("value");
		//
		scrolledComposite.setContent(table);
		scrolledComposite.setMinSize(table
				.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		//
		parse(o);
	}

	private void parse(Object o) {
		if (o == null) {
			o = new TableTestData();
		}
		//
		Method[] ms = o.getClass().getMethods();
		for (Method method : ms) {
			try {
				parseMethod(o, method);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void parseMethod(Object o, Method method)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		String mname = method.getName();
		if (mname.startsWith("get") && method.getParameterTypes().length == 0) {
			String fname = mname.substring(3).toLowerCase();

			Object value;
			value = method.invoke(o);
			if (isOKValueType(value)) {
				TableItem i = new TableItem(table, SWT.NONE);
				i.setText(0, "" + fname);
				i.setText(1, "" + value);
			}
		}
	}

	private boolean isOKValueType(Object value) {
		return true;
	}
}
