package org.collabthings.swt.controls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.collabthings.environment.impl.LOTFactoryState;
import org.collabthings.math.LOrientation;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTBoundingBox;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.LOTMaterial;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.view.ObjectSmallView;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

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
	private Map<String, LEditorFactory> editors = new HashMap<>();
	private Label lblObject;
	private final Set<String> ignoreset;
	private AppWindow window;
	private LOTApp app;

	/**
	 * @wbp.parser.constructor
	 */
	public ObjectViewer(LOTApp app, AppWindow window, Composite parent, Object o) {
		this(app, window, parent, o, new String[0]);
	}

	public ObjectViewer(LOTApp app, AppWindow window, Composite parent,
			Object o, String ignore[]) {
		super(parent, SWT.NONE);

		this.app = app;
		this.window = window;

		this.ignoreset = new HashSet<String>();
		for (String s : ignore) {
			ignoreset.add(s);
		}

		initOkTypes();
		parse(o);

		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);
		setLayout(gridLayout);

		lblObject = new Label(this, SWT.NONE);
		lblObject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		lblObject.setAlignment(SWT.CENTER);
		lblObject.setText("" + objectShown);
		lblObject.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		lblObject.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));

		composite = new Composite(this, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(248, 248, 255));
		GridLayout gl_composite = new GridLayout(1, false);
		LOTSWT.setDefaults(gl_composite);
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		addRows();
	}

	private void initOkTypes() {
		editors.put(String.class.getName(),
				(key, c, o) -> addStringField(key, c, (String) o));

		editors.put(LVector.class.getName(),
				(key, c, o) -> addVectorField(key, c, (LVector) o));

		editors.put(Double.class.getName(),
				(key, c, o) -> addDoubleField(key, c, (Double) o));

		editors.put(LOrientation.class.getName(),
				(key, c, o) -> addOrientationField(key, c, (LOrientation) o));

		editors.put(LOTBoundingBox.class.getName(),
				(key, c, o) -> addBoundingBoxField(key, c, (LOTBoundingBox) o));

		editors.put(LOTFactoryState.class.getName(), (key, c, o) -> {
			LOTFactoryState s = (LOTFactoryState) o;
			LOTFactory f = s.getFactory();
			ObjectSmallView view = new ObjectSmallView(c, app, window, f
					.getID().toString());
			view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
					1));
		});

		editors.put(Long.class.getName(), (key, c, o) -> {
			if ("modifytime".equals(key) || "creationtime".equals(key)) {
				addDateField(key, c, (Long) o);
			} else {
				addStringField(key, c, "WHAT IS THIS " + key + " " + o);
			}
		});

		editors.put(Set.class.getName(),
				(key, c, o) -> addCollectionView(key, c, o));

		editors.put(LOTMaterial.class.getName(),
				(key, c, o) -> addMaterialView(key, c, (LOTMaterial) o));
	}

	private void parse(Object no) {
		this.objectShown = no;
		if (this.objectShown == null) {
			this.objectShown = new TableTestData();
		}

		//
		Method[] ms = this.objectShown.getClass().getMethods();
		for (Method method : ms) {
			try {
				parseMethod(method);
			} catch (IllegalArgumentException e) {
				log.error(this, "parse " + this.objectShown, e);
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
			return getEditor(value) != null;
		} else {
			return false;
		}
	}

	private LEditorFactory getEditor(Object o) {
		if (o != null) {
			Class<? extends Object> class1 = o.getClass();
			LEditorFactory e = editors.get(class1.getName());
			if (e != null) {
				return e;
			}

			Class<?>[] cs = class1.getDeclaredClasses();
			for (Class<?> class2 : cs) {
				e = editors.get(class2.getName());
				if (e != null) {
					return e;
				}
			}

			Class<?>[] ics = class1.getInterfaces();
			for (Class<?> class2 : ics) {
				e = editors.get(class2.getName());
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
		Method method = methods.get(key);
		Object o = invokeGetMethod(method);

		addValue(composite, key, o);
	}

	private void addValue(Composite parent, String key, Object o) {
		LEditorFactory e = getEditor(o);
		if (e != null) {
			e.add(key, parent, o);
		} else {
			Label la = new Label(parent, getStyle());
			la.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
			la.setText("unknown type " + o.getClass().getName() + " " + key);
		}
	}

	private Control addDateField(String key, Composite c, Long d) {
		return addLabelField(key, c, "" + new Date(d));
	}

	private Composite addBoundingBoxField(String key, Composite parent,
			LOTBoundingBox box) {
		Composite c = getTwoRowsComposite(parent);
		addLabel(key, c);

		LOTBoundingBoxEditor bbeditor = new LOTBoundingBoxEditor(c, box,
				o -> fireValueChanged(key, box));
		bbeditor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				2, 1));

		return c;
	}

	private Composite addOrientationField(String key, Composite c,
			LOrientation orgo) {
		return new LOTOrientationEditor(c, orgo, o -> fireValueChanged(key,
				orgo));
	}

	private LOTMaterialEditor addMaterialView(String key, Composite c,
			LOTMaterial o) {
		return new LOTMaterialEditor(c, o);
	}

	private Control addDoubleField(String key, Composite c, Double d) {
		return addTextField(c, key, "" + d, new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				String sdata = getString(arg0);
				invokeSetMethod(key, Double.parseDouble(sdata));
			}
		});
	}

	private Composite addVectorField(String key, Composite parent, LVector orgv) {
		Composite c = getRowComposite(parent);
		addLabel(key, c);
		LOTVectorEditor e = new LOTVectorEditor(c, orgv, v -> invokeSetMethod(
				key, v));
		return e;
	}

	private Composite getRowComposite(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		return c;
	}

	private Composite getTwoRowsComposite(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);

		c.setLayout(gridLayout);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		return c;
	}

	private Control addStringField(String key, Composite c, String s) {
		return addTextField(c, key, s, new ModifyListener() {

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

	private Control addLabelField(String key, Composite parent, String text) {
		Composite c = getRowComposite(parent);

		addLabel(key, c);

		Label s = new Label(c, SWT.NONE);
		s.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		s.setText(text);
		return s;
	}

	private void addLabel(String key, Composite c) {
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		l.setText(key);
		l.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
	}

	private Control addTextField(Composite parent, String name, String text,
			ModifyListener listener) {
		Composite c = getRowComposite(parent);

		addLabel(name, c);

		Text s = new Text(c, SWT.NONE);
		s.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		s.setEditable(true);
		s.setText(text);
		s.addModifyListener(listener);
		return s;
	}

	@SuppressWarnings("unchecked")
	private Control addCollectionView(String key, Composite parent, Object o) {
		log.info("addng " + key + " o:" + o);
		Composite v = getTwoRowsComposite(parent);
		addLabel("Collection " + key, v);

		Composite c = new Composite(v, SWT.None);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl = new GridLayout();
		LOTSWT.setDefaults(gl);

		c.setLayout(gl);

		for (Object item : (Collection<Object>) o) {
			Label l = new Label(c, SWT.None);
			l.setText("" + item);

			addValue(c, "" + item, item);
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
		public void add(String key, Composite c, Object o);
	}
}
