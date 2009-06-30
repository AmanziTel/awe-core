package org.amanzi.splash.neo4j.ui;

/*
 * "The Java Developer's Guide to Eclipse"
 *   by D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003, 2004. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Convenience class for the UI plug-in's image descriptors.
 */
public class SplashImages {
	public static final ImageDescriptor IMAGE_ALIGN_LEFT;
	public static final ImageDescriptor IMAGE_ALIGN_CENTER;
	public static final ImageDescriptor IMAGE_ALIGN_RIGHT;
	public static final ImageDescriptor IMAGE_SPREADSHEET;	
	public static final ImageDescriptor IMAGE_CLEAR_ALL;		

	static {
		IMAGE_ALIGN_LEFT = createImageDescriptor("icons/align_left.gif");
		IMAGE_ALIGN_CENTER = createImageDescriptor("icons/align_center.gif");
		IMAGE_ALIGN_RIGHT = createImageDescriptor("icons/align_right.gif");
		IMAGE_SPREADSHEET = createImageDescriptor("icons/spreadsheet.gif");		
		IMAGE_CLEAR_ALL = createImageDescriptor("icons/clear_all.gif");		
	}

	private static ImageDescriptor createImageDescriptor(String path) {
		try {
			URL url = new URL(SplashPlugin.getDefault().getBundle().getEntry("/"), path); //$NON-NLS-1$
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
		}
		return ImageDescriptor.getMissingImageDescriptor();
	}
}
