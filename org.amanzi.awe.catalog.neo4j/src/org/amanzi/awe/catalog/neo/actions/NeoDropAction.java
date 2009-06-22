package org.amanzi.awe.catalog.neo.actions;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.IDropAction;
import net.refractions.udig.ui.ViewerDropLocation;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.neo4j.api.core.Node;


public class NeoDropAction extends IDropAction {

    /** the sld url * */
	
    URL url;
    
	List<IGeoResource> resourceList;
	
	Node networkNode;

    public boolean accept( Object source, Object destination ) {
        // make sure we can turn the object into an sld
    	if(destination instanceof Layer || destination instanceof Map)
    	{
    		try
    		{
    			if(source instanceof Node)
    			{
    				 networkNode=(Node)source;
    			
					ApplicationGIS.addLayersToMap(ApplicationGIS.getActiveProject(), resourceList);
    				
    			}
    		}
    		catch(Exception ex)
    		{
    			
    		}
    	
    		
    	}
//        try {
//            if (source instanceof URL) {
//                url = (URL) source;
//            } else if (source instanceof File) {
//                url = ((File) source).toURL();
//            } else if (source instanceof String) {
//                try {
//                    url = new URL((String) source);
//                } catch (MalformedURLException e) {
//                    // try attaching a file protocol
//                    url = new URL("file:///" + (String) source); //$NON-NLS-1$
//                }
//
//            }
//        } catch (MalformedURLException e) {
//          String msg="";
//            ProjectUIPlugin.log(msg, e);
//        }

        return url != null;
    }

   

	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return super.getData();
	}

	@Override
	public Object getDestination() {
		// TODO Auto-generated method stub
		return super.getDestination();
	}

	@Override
	public IConfigurationElement getElement() {
		// TODO Auto-generated method stub
		return super.getElement();
	}

	@Override
	public DropTargetEvent getEvent() {
		// TODO Auto-generated method stub
		return super.getEvent();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}

	@Override
	public void init(IConfigurationElement element2, DropTargetEvent event2,
			ViewerDropLocation location2, Object destination2, Object data2) {
		// TODO Auto-generated method stub
		super.init(element2, event2, location2, destination2, data2);
	}

	@Override
	public boolean accept() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void perform(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

}