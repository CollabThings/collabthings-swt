package org.libraryofthings.swt.controls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.libraryofthings.LLog;
import org.libraryofthings.math.LOrientation;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTBoundingBox;

public class ObjectViewer extends Composite {
	private LLog log = LLog.getLogger(this);
	//
	public Object objectShown;
	public Label className;
	public Label superClassName;
	public Text toString;
	private Composite composite;

	//
	private Map<String, Method> methods = new HashMap<>();
	private Set<ObjectViewerListener> listeners = new HashSet<>();
	private Text testext;
	private Map<Class, LEditorFactory> editors = new HashMap<>();

	public ObjectViewer(Composite parent, Object o) {
		super(parent, SWT.NONE);

		initOkTypes();
		parse(o);
		addRows();
	}

	private void initOkTypes() {
		editors.put(String.class, (key, c, o) -> {
			return addStringField(key, c, (String) o);
		});
		editors.put(LVector.class, (key, c, o) -> {
			return addVectorField(key, c, (LVector) o);
		});

		editors.put(Double.class, (key, c, o) -> {
			return addDoubleField(key, c, (Double) o);
		});

		editors.put(LOrientation.class, (key, c, o) -> {
			return addOrientationField(key, c, (LOrientation) o);
		});
		editors.put(LOTBoundingBox.class, (key, c, o) -> {
			return addBoundingBoxField(key, c, (LOTBoundingBox) o);
		});
	}

	private String getObjectValue(String name) {
		return "" + invokeGetMethod(methods.get(name));
	}

	private void parse(Object o) {
		if (o == null) {
			o = new TableTestData();
		}

		this.objectShown = o;
		//
		Method[] ms = o.getClass().getMethods();
		for (Method method : ms) {
			try {
				parseMethod(method);
			} catch (IllegalArgumentException e) {
				log.error(this, "parse " + o, e);
			}
		}
	}

	private void parseMethod(Method method) {
		String mname = method.getName();
		if (mname.startsWith("get") && method.getParameterTypes().length == 0) {
			String fname = mname.substring(3).toLowerCase();

			Object value;
			value = invokeGetMethod(method);
			if (isOKValueType(value)) {
				methods.put(fname, method);
			}
		}
	}

	private void invokeSetMethod(String name, Object value) {
		String methodname = "set" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		Method[] ms = objectShown.getClass().getMethods();
		for (Method method : ms) {
			if (method.getName().equals(methodname)) {
				try {
					log.info("invoking method " + methodname + " with " + value);
					method.invoke(objectShown, value);
					fireValueChanged(name, value);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					log.error(this, "invokeSetMethod", e);
				}
			}
		}
	}

	private void fireValueChanged(String name, Object value) {
		Set<ObjectViewerListener> ls = this.listeners;
		for (ObjectViewerListener objectViewerListener : ls) {
			objectViewerListener.valueChanged(name, value);
		}
	}

	private Object invokeGetMethod(Method method) {
		if (method != null) {
			try {
				return method.invoke(objectShown);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				log.error(e, "invokeGetMethod", e);
				return null;
			}
		} else {
			return null;
		}
	}

	private boolean isOKValueType(Object value) {
		if (value != null) {
			return editors.get(value.getClass()) != null;
		} else {
			return false;
		}
	}

	private void addRows() {
		setLayout(new GridLayout(1, false));

		Label lblObject = new Label(this, SWT.NONE);
		lblObject.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true,
				false, 1, 1));
		lblObject.setAlignment(SWT.CENTER);
		lblObject.setText("" + objectShown);

		composite = new Composite(this, SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1,
				1));
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		for (String key : methods.keySet()) {
			addRow(key);
		}

	}

	private void addRow(String key) {
		Composite c = new Composite(composite, SWT.NONE);
		GridLayout gl_c = new GridLayout(2, false);
		gl_c.verticalSpacing = 0;
		gl_c.marginWidth = 0;
		gl_c.marginHeight = 0;
		gl_c.horizontalSpacing = 0;
		c.setLayout(gl_c);
		Label l = new Label(c, getStyle());
		GridData gd_l = new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1);
		gd_l.widthHint = 101;
		l.setLayoutData(gd_l);
		l.setText(key);
		//
		Method method = methods.get(key);
		Object o = invokeGetMethod(method);
		LEditorFactory e = editors.get(o.getClass());
		if (e != null) {
			Control cc = e.add(key, c, o);
			cc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		} else {
			Label la = new Label(c, getStyle());
			la.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			la.setText("unknown type");
		}
	}

	private Composite addBoundingBoxField(String key, Composite c,
			LOTBoundingBox box) {
		return new LOTBoundingBoxEditor(c, box, o -> fireValueChanged(key, box));
	}

	private Composite addOrientationField(String key, Composite c,
			LOrientation orgo) {
		return new LOTOrientationEditor(c, orgo, o -> fireValueChanged(key,
				orgo));
	}

	private Control addDoubleField(String key, Composite c, Double d) {
		return addTextField(c, "" + d, new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				String sdata = getString(arg0);
				invokeSetMethod(key, Double.parseDouble(sdata));
			}
		});
	}

	private Composite addVectorField(String key, Composite c, LVector orgv) {
		return new LOTVectorEditor(c, orgv, v -> invokeSetMethod(key, v));
	}

	private Control addStringField(String key, Composite c, String s) {
		return addTextField(c, s, new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				String sdata = "" + getString(arg0);
				invokeSetMethod(key, sdata);
			}
		});
	}

	private String getString(ModifyEvent arg0) {
		Text t = (Text) arg0.widget;
		return "" + t.getText();
	}

	private Control addTextField(Composite c, String text,
			ModifyListener listener) {
		Text s = new Text(c, SWT.NONE);
		s.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		s.setEditable(true);
		s.setText(text);
		s.addModifyListener(listener);
		return s;
	}

	public void setObject(Object o) {
		objectShown = o;
		refresh();
	}

	private void refresh() {
		for (Control control : getChildren()) {
			control.dispose();
		}
		addRows();
		layout();
	}

	public void addListener(ObjectViewerListener objectViewerListener) {
		this.listeners.add(objectViewerListener);
	}

	private interface LEditorFactory {
		public Control add(String key, Composite c, Object o);
	}
}
