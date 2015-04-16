package org.libraryofthings.swt.controls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
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
	private Map<Class, LEditorFactory> editors = new HashMap<>();
	private Label lblObject;
	private final Set<String> ignoreset;

	public ObjectViewer(Composite paranet, Object o) {
		this(paranet, o, new String[0]);
	}

	public ObjectViewer(Composite parent, Object o, String ignore[]) {
		super(parent, SWT.NONE);

		this.ignoreset = new HashSet<String>();
		for (String s : ignore) {
			ignoreset.add(s);
		}

		initOkTypes();
		parse(o);

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginTop = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginLeft = 5;
		setLayout(gridLayout);

		lblObject = new Label(this, SWT.NONE);
		lblObject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblObject.setAlignment(SWT.CENTER);
		lblObject.setText("" + objectShown);
		lblObject.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		lblObject.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));

		composite = new Composite(this, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(248, 248, 255));
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

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

		editors.put(Long.class, (key, c, o) -> {
			if (key.equals("modifytime") || key.equals("creationtime")) {
				return addDateField(key, c, (Long) o);
			} else {
				return addStringField(key, c, "WHAT IS THIS " + key + " " + o);
			}
		});

		editors.put(Set.class, (key, c, o) -> {
			return addCollectionView(key, c, o);
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
			if (!ignoreset.contains(fname)) {
				Object value;
				value = invokeGetMethod(method);
				if (isOKValueType(value)) {
					methods.put(fname, method);
				}
			}
		}
	}

	private void invokeSetMethod(String name, Object value) {
		String methodname = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
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
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error(e, "invokeGetMethod", e);
				return null;
			}
		} else {
			return null;
		}
	}

	private boolean isOKValueType(Object value) {
		if (value != null) {
			return getEditor(value) != null;
		} else {
			return false;
		}
	}

	private LEditorFactory getEditor(Object o) {
		if (o != null) {
			Class<? extends Object> class1 = o.getClass();
			LEditorFactory e = editors.get(class1);
			if (e != null) {
				return e;
			}

			Class<?>[] cs = class1.getDeclaredClasses();
			for (Class<?> class2 : cs) {
				e = editors.get(class2);
				if (e != null) {
					return e;
				}
			}

			Class<?>[] ics = class1.getInterfaces();
			for (Class<?> class2 : ics) {
				e = editors.get(class2);
				if (e != null) {
					return e;
				}
			}
		}
		return null;
	}

	private void addRows() {
		for (String key : methods.keySet()) {
			addRow(key);
		}
	}

	private void addRow(String key) {
		Composite row = new Composite(composite, SWT.NONE);
		row.setLayout(new GridLayout(2, false));
		row.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label l = new Label(row, SWT.NONE);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		l.setText(key);
		l.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));

		Method method = methods.get(key);
		Object o = invokeGetMethod(method);
		LEditorFactory e = getEditor(o);
		if (e != null) {
			Control cc = e.add(key, row, o);
			cc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		} else {
			Label la = new Label(row, getStyle());
			la.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
			la.setText("unknown type");
		}
	}

	private Control addDateField(String key, Composite c, Long d) {
		return addLabelField(key, c, "" + new Date(d));
	}

	private Composite addBoundingBoxField(String key, Composite c, LOTBoundingBox box) {
		return new LOTBoundingBoxEditor(c, box, o -> fireValueChanged(key, box));
	}

	private Composite addOrientationField(String key, Composite c, LOrientation orgo) {
		return new LOTOrientationEditor(c, orgo, o -> fireValueChanged(key, orgo));
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

	private Control addLabelField(String key, Composite c, String text) {
		Label s = new Label(c, SWT.NONE);
		s.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		s.setText(text);
		return s;
	}

	private Control addTextField(Composite c, String text, ModifyListener listener) {
		Text s = new Text(c, SWT.NONE);
		s.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		s.setEditable(true);
		s.setText(text);
		s.addModifyListener(listener);
		return s;
	}

	private Control addCollectionView(String key, Composite c, Object o) {
		Composite v = new Composite(c, SWT.None);
		v.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		v.setLayout(new RowLayout());

		Collection col = (Collection) o;
		for (Object item : col) {
			Label l = new Label(v, SWT.None);
			l.setText("" + item);
		}

		return v;
	}

	public void setObject(Object o) {
		objectShown = o;
		lblObject.setText("" + o);
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
