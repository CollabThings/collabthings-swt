package org.collabthings.swt.app;

import org.collabthings.app.CTApp;
import org.collabthings.swt.AppWindow;

public class CTRunner<T> {

	private String name;
	private long interval;
	private long lastrun;
	private CTRunnerBoolean runwhile;
	private CTRunnerReturn<T> action;
	private CTRunnerAction<T> gui;
	private Runnable run;

	public CTRunner(String name, int interval) {
		this.name = name;
		this.interval = interval;
	}

	public CTRunner(String string) {
		this.name = string;
		interval = -1;
	}

	public CTRunner<T> runWhile(CTRunnerBoolean b) {
		runwhile = b;
		return this;
	}

	public CTRunner<T> action(CTRunnerReturn<T> r) {
		this.action = r;
		return this;
	}

	public CTRunner<T> gui(CTRunnerAction<T> value) {
		this.gui = value;
		return this;
	}

	public boolean check(AppWindow window, CTApp app) {
		if (runwhile != null || action != null || run != null) {
			// run once if runwhile is null
			boolean shouldContinue = runwhile != null ? runwhile.shouldContinue() : true;
			if (shouldContinue) {
				long current = System.currentTimeMillis();
				if (current - lastrun > interval) {
					lastrun = current;

					T t = null;

					if (this.run != null) {
						run.run();
					}

					if (action != null) {
						t = action.action();
					}

					if (gui != null) {
						final T ft = t;
						window.launch((e) -> gui.action(ft));
					}
				}

				// run once if runwhile is null
				return runwhile != null;
			} else {
				return false;
			}
		} else {
			// not initialized yet
			return true;
		}
	}

	public CTRunner<T> run(Runnable r) {
		this.run = r;
		return this;
	}

	public static interface CTRunnerBoolean {
		boolean shouldContinue();
	}

	public static interface CTRunnerReturn<T> {
		T action();
	}

	public static interface CTRunnerAction<T> {
		void action(T t);
	}

	public String getName() {
		return name;
	}

}
