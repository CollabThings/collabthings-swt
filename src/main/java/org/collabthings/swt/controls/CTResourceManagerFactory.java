package org.collabthings.swt.controls;

public class CTResourceManagerFactory {
	private static CTResourceManager manager;
	
	public static CTResourceManager instance() {
		return manager;
	}
	
	public static void setInstance(CTResourceManager nmanager) {
		CTResourceManagerFactory.manager = nmanager;
	}
}
