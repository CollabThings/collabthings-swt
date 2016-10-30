package org.collabthings.swt.controls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.collabthings.CTEvent;
import org.collabthings.CTListener;
import org.collabthings.environment.impl.CTFactoryState;
import org.collabthings.math.LOrientation;
import org.collabthings.model.CTBoundingBox;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTMaterial;
import org.collabthings.model.CTObject;
import org.collabthings.model.CTPart;
import org.collabthings.model.impl.CTOpenSCADImpl;
import org.collabthings.model.impl.CTPartBuilderImpl;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.view.ObjectSmallView;
import org.collabthings.swt.view.parteditor.CTObjectListener;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.jme3.math.Vector3f;

public class ObjectViewer extends CTComposite {
	private LLog log = LLog.getLogger(this);
	//
	public CTObject objectShown;
	public CTLabel className;
	public CTLabel superClassName;
	public Text toString;
	private Composite composite;

	//
	private Map<String, Method> methods = new HashMap<>();
	private Set<ObjectViewerListener> listeners = new HashSet<>();
	private Set<CTListener> objectchangelisteners = new HashSet<>();
	private Map<String, LEditorFactory> editors = new HashMap<>();
	private CTLabel lblObject;
	private final Set<String> ignoreset;
	private AppWindow window;
	private LOTApp app;

	/**
	 * @wbp.parser.constructor
	 */
	public ObjectViewer(LOTApp app, AppWindow window, Composite parent) {
		this(app, window, parent, new String[0]);
	}

	public ObjectViewer(LOTApp app, AppWindow window, Composite parent, String ignore[]) {
		super(parent, SWT.NONE);

		setBackground(SWTResourceManager.getControlBg());
		setFont(SWTResourceManager.getDefaultFont());

		this.app = app;
		this.window = window;

		this.ignoreset = new HashSet<String>();
		for (String s : ignore) {
			ignoreset.add(s);
		}

		init();
	}

	private void init() {
		initOkTypes();

		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);
		setLayout(gridLayout);

		lblObject = new CTLabel(this, SWT.NONE);
		lblObject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblObject.setAlignment(SWT.CENTER);
		lblObject.setText("" + objectShown);

		lblObject.setFont(10, SWT.BOLD);

		composite = new CTComposite(this, SWT.NONE);

		GridLayout gl_composite = new GridLayout(1, false);
		LOTSWT.setDefaults(gl_composite);
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	private void initOkTypes() {
		editors.put(String.class.getName(), (key, c, o) -> addStringField(key, c, (String) o));

		editors.put(Vector3f.class.getName(), (key, c, o) -> addVectorField(key, c, (Vector3f) o));

		editors.put(Double.class.getName(), (key, c, o) -> addDoubleField(key, c, (Double) o));

		editors.put(LOrientation.class.getName(), (key, c, o) -> addOrientationField(key, c, (LOrientation) o));

		editors.put(CTBoundingBox.class.getName(), (key, c, o) -> addBoundingBoxField(key, c, (CTBoundingBox) o));

		editors.put(CTFactoryState.class.getName(), (key, c, o) -> {
			CTFactoryState s = (CTFactoryState) o;
			CTFactory f = s.getFactory();
			ObjectSmallView view = new ObjectSmallView(c, app, window, f.getID().toString());
			view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		});

		editors.put(Long.class.getName(), (key, c, o) -> {
			if ("modifytime".equals(key) || "creationtime".equals(key)) {
				addDateField(key, c, (Long) o);
			} else {
				addStringField(key, c, "WHAT IS THIS " + key + " " + o);
			}
		});

		editors.put(Set.class.getName(), (key, c, o) -> addCollectionView(key, c, o));

		editors.put(CTMaterial.class.getName(), (key, c, o) -> addMaterialView(key, c, (CTMaterial) o));

		editors.put(CTOpenSCADImpl.class.getName(), (key, c, o) -> addOpenScadField(key, c, (CTOpenSCADImpl) o));

		editors.put(CTPartBuilderImpl.class.getName(),
				(key, c, o) -> addPartBuilderField(key, c, (CTPartBuilderImpl) o));
	}

	private void updateData() {
		getDisplay().asyncExec(() -> {
			refresh();
		});
	}

	private synchronized void parse(Object no) {
		this.objectShown = (CTObject) no;
		if (this.objectShown == null) {
			this.objectShown = new TableTestData();
		}

		//
		parseMethods();
	}

	private void parseMethods() {
		methods = new HashMap<>();

		Method[] ms = this.objectShown.getClass().getMethods();
		for (Method method : ms) {
			try {
				parseMethod(method);
			} catch (IllegalArgumentException e) {
				log.error(this, "parse " + this.objectShown, e);
			}
		}
	}

	private synchronized void parseMethod(Method method) {
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
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
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
		if (o != null) {
			LEditorFactory e = getEditor(o);
			if (e != null) {
				e.add(key, parent, o);
			} else {
				CTLabel la = new CTLabel(parent, getStyle());
				la.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
				la.setText("unknown type " + o.getClass().getName() + " " + key);
			}
		} else {
			CTLabel la = new CTLabel(parent, getStyle());
			la.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
			la.setText("Empty value " + key);
		}
	}

	private Control addDateField(String key, Composite c, Long d) {
		return addLabelField(key, c, "" + new Date(d)).getControl();
	}

	private Composite addBoundingBoxField(String key, Composite parent, CTBoundingBox box) {
		Composite c = getTwoRowsComposite(parent);
		addLabel(key, c);

		LOTBoundingBoxEditor bbeditor = new LOTBoundingBoxEditor(c, box, o -> fireValueChanged(key, box));
		bbeditor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

		return c;
	}

	private Composite addOrientationField(String key, Composite c, LOrientation orgo) {
		return new LOTOrientationEditor(c, orgo, o -> fireValueChanged(key, orgo));
	}

	private CTMaterialEditor addMaterialView(String key, Composite c, CTMaterial o) {
		CTMaterialEditor e = new CTMaterialEditor(c, o);
		e.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		return e;
	}

	private Control addDoubleField(String key, Composite c, Double d) {
		return addTextField(c, key, "" + d, (t) -> {
			String sdata = t.getText();
			invokeSetMethod(key, Double.parseDouble(sdata));
		});
	}

	private Composite addVectorField(String key, Composite parent, Vector3f orgv) {
		Composite c = getRowComposite(parent);
		addLabel(key, c);
		LOTVectorEditor e = new LOTVectorEditor(c, orgv, v -> invokeSetMethod(key, v));
		return e;
	}

	private Composite getRowComposite(Composite parent) {
		Composite c = new CTComposite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		return c;
	}

	private Composite getTwoRowsComposite(Composite parent) {
		Composite c = new CTComposite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);

		c.setLayout(gridLayout);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		return c;
	}

	private Control addStringField(String key, Composite c, String s) {
		return addTextField(c, key, s, (t) -> {
			String sdata = "" + t.getText();
			invokeSetMethod(key, sdata);
		});
	}

	private CTLabel addLabelField(String key, Composite parent, String text) {
		Composite c = getRowComposite(parent);

		addLabel(key, c);

		CTLabel s = new CTLabel(c, SWT.NONE);
		s.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		s.setText(text);
		return s;
	}

	private void addLabel(String key, Composite c) {
		CTLabel l = new CTLabel(c, SWT.NONE);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		l.setText(key);
	}

	private Control addTextField(Composite parent, String name, String text, TextListener listener) {
		Composite c = getRowComposite(parent);

		addLabel(name, c);

		CTText s = new CTText(c, SWT.NONE);
		s.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		s.setEditable(true);
		s.setText(text);

		s.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				listener.changed(s);
			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});

		return s;
	}

	@SuppressWarnings("unchecked")
	private Control addCollectionView(String key, Composite parent, Object o) {
		log.info("addng " + key + " o:" + o);
		Composite v = getTwoRowsComposite(parent);
		addLabel("Collection " + key, v);

		Composite c = new CTComposite(v, SWT.None);

		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl = new GridLayout();
		LOTSWT.setDefaults(gl);

		c.setLayout(gl);

		for (Object item : (Collection<Object>) o) {
			CTLabel l = new CTLabel(c, SWT.None);
			l.setText("" + item);

			addValue(c, "" + item, item);
		}

		return v;
	}

	private Control addPartBuilderField(String key, Composite parent, CTPartBuilderImpl o) {
		Composite c = new CTComposite(parent, SWT.BORDER);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);

		gridLayout.numColumns = 4;
		c.setLayout(gridLayout);

		CTLabel l = new CTLabel(c, SWT.NONE);
		l.setText("PartBuilder");
		l.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		CTButton s = new CTButton(c, SWT.NONE);
		s.setText("Save");

		CTButton brun = new CTButton(c, SWT.NONE);
		brun.setText("Run");
		brun.addSelectionListener(() -> o.run((CTPart) this.objectShown));

		CTButton bopen = new CTButton(c, SWT.NONE);
		bopen.setText("Open");
		bopen.addSelectionListener(() -> {
			if (objectShown instanceof CTPart) {
				CTPart p = (CTPart) objectShown;
				window.getMainView().viewBuilder(objectShown.getName() + "/" + o.getName(), p, o);
			}
		});

		return c;
	}

	private Control addOpenScadField(String key, Composite parent, CTOpenSCADImpl o) {
		Composite c = new CTComposite(parent, SWT.BORDER);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);

		gridLayout.numColumns = 4;
		c.setLayout(gridLayout);

		CTLabel l = new CTLabel(c, SWT.NONE);
		l.setText("Openscad model");
		l.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		CTButton b = new CTButton(c, SWT.NONE);
		b.setText("Open");
		b.addSelectionListener(() -> {
			window.getMainView().viewSCAD(o);
		});

		CTButton r = new CTButton(c, SWT.NONE);
		r.setText("Remove");
		r.addSelectionListener(() -> {
			CTPart p = (CTPart) this.objectShown;
			p.resetModel();
		});

		CTButton s = new CTButton(c, SWT.NONE);
		s.setText("Save");

		return c;
	}

	public void setObject(CTObject o) {
		objectShown = o;
		new CTObjectListener(o, () -> {
			return !isDisposed() && o == objectShown;
		}, () -> updateData());

		lblObject.setText("" + o);
		refresh();
	}

	private void refresh() {
		log.info("refreshdata " + this);

		for (Control control : composite.getChildren()) {
			control.dispose();
		}

		parseMethods();

		addRows();

		layout();

		Set<CTListener> ls = objectchangelisteners;
		for (CTListener ctListener : ls) {
			ctListener.event(new CTEvent("refresh"));
		}
	}

	public void addListener(ObjectViewerListener objectViewerListener) {
		this.listeners.add(objectViewerListener);
	}

	public void addObjectChangeListener(CTListener l) {
		this.objectchangelisteners.add(l);
	}

	private interface LEditorFactory {
		public void add(String key, Composite c, Object o);
	}

	private interface TextListener {
		void changed(CTText t);
	}
}
