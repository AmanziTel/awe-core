package org.amanzi.awe.render.sites.tool;

import java.net.URL;
import java.util.Iterator;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.geotools.feature.Feature;
import org.geotools.filter.Filter;


public class Selector 
{
    private ISelectionListener selectionListener;
    Object object;
    ISelectionService selectionService;
    IAdaptable adaptable;
	  
    private final class WorkbenchSelectionListener implements ISelectionListener 
    {
        public void selectionChanged( IWorkbenchPart part, ISelection selection ) 
        {
            if( selection instanceof IStructuredSelection )
            {
                updateSelection( (IStructuredSelection) selection );
            }
            else 
            {
                updateSelection( null );
            }
        }
    }
    
    public Selector() 
    {
    	selectionListener = new WorkbenchSelectionListener();
        selectionService =PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService(); 
        selectionService.addPostSelectionListener(selectionListener);
    }

    protected void updateSelection( IStructuredSelection selection ) 
    {
        if( selection == null || selection.isEmpty() )
        {
            return;
        }
            object = selection.getFirstElement();
        if( object == null )
        {
            return;
        }
        else 
        {
         
        }
       
        for( Iterator<?> iterator=selection.iterator(); iterator.hasNext(); )
        {            
            object = iterator.next();
          
            if( object instanceof IMap )
            {
                        
            }
            if( object instanceof ILayer )
            {
                       
            }
      
            if( object instanceof IService )
            {
                         
            }
            if( object instanceof IGeoResource)
            {
                         
            }

            if( object instanceof Filter )
            {
                        
            }
            if( object instanceof Feature )
            {
                         
            }
            // IADATABLE
           
            if( object instanceof IAdaptable)
            {
                adaptable = (IAdaptable) object;
                if( adaptable.getAdapter(IMap.class) != null )
                {
                   
                }
                if( adaptable.getAdapter(ILayer.class) != null )
                {
                   
                }

                if( adaptable.getAdapter(IService.class) != null )
                {
                   
                }
                if( adaptable.getAdapter(IGeoResource.class) != null)
                {
                   
                }

                if( adaptable.getAdapter(Filter.class) != null )
                {
                  
                }
                if( adaptable.getAdapter(Feature.class) != null )
                {
                   
                }
                if( adaptable.getAdapter(URL.class) != null )
                {
                    
                }
            }
        }  
    }
}
