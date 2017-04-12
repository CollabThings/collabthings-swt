/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/
package org.collabthings.swt.app;

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

	public boolean check(AppWindow window) {
		if (runwhile != null || action != null || run != null) {
			// run once if runwhile is null
			boolean shouldContinue = runwhile != null ? runwhile.shouldContinue() : true;
			if (shouldContinue) {
				long current = System.currentTimeMillis();
				if (current - lastrun > interval) {
					lastrun = current;

					checkRun(window);
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

	private void checkRun(AppWindow window) {
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

	public CTRunner<T> run(Runnable r) {
		this.run = r;
		return this;
	}

	@FunctionalInterface
	public static interface CTRunnerBoolean {
		boolean shouldContinue();
	}

	@FunctionalInterface
	public static interface CTRunnerReturn<T> {
		T action();
	}

	@FunctionalInterface
	public static interface CTRunnerAction<T> {
		void action(T t);
	}

	public String getName() {
		return name;
	}

}
