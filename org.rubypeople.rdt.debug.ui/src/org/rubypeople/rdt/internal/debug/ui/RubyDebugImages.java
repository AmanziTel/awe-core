package org.rubypeople.rdt.internal.debug.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;

public class RubyDebugImages {
	private static String ICONS_PATH = "$nl$/icons/full/"; //$NON-NLS-1$

	private static ImageRegistry fgImageRegistry;

	public static final String IMG_OVR_OUT_OF_SYNCH = "IMG_OVR_OUT_OF_SYNCH"; //$NON-NLS-1$
	
	private static final String T_OVR= ICONS_PATH + "ovr16/"; 		//$NON-NLS-1$

	/**
	 * Returns the <code>ImageDescriptor</code> identified by the given key,
	 * or <code>null</code> if it does not exist.
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		return getImageRegistry().getDescriptor(key);
	}

	/*
	 * Helper method to access the image registry from the JDIDebugUIPlugin
	 * class.
	 */
	/* package */static ImageRegistry getImageRegistry() {
		if (fgImageRegistry == null) {
			initializeImageRegistry();
		}
		return fgImageRegistry;
	}
	
	private static void initializeImageRegistry() {
		fgImageRegistry= new ImageRegistry(RdtDebugUiPlugin.getStandardDisplay());
		declareImages();
	}
	
	private static void declareImages() {
		declareRegistryImage(IMG_OVR_OUT_OF_SYNCH, T_OVR + "error_co.gif");			//$NON-NLS-1$
	}
	
    /**
     * Declare an Image in the registry table.
     * @param key   The key to use when registering the image
     * @param path  The path where the image can be found. This path is relative to where
     *              this plugin class is found (i.e. typically the packages directory)
     */
    private final static void declareRegistryImage(String key, String path) {
        ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
        Bundle bundle = Platform.getBundle(RdtDebugUiPlugin.getUniqueIdentifier());
        URL url = null;
        if (bundle != null){
            url = FileLocator.find(bundle, new Path(path), null);
            desc = ImageDescriptor.createFromURL(url);
        }
        fgImageRegistry.put(key, desc);
    }
}
