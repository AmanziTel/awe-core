package org.amanzi.awe.catalog.neo.actions;

import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeListener;

import org.amanzi.awe.neo.views.network.utils.TreeViewContentProvider;
import org.amanzi.awe.neo.views.network.utils.ViewLabelProvider;
import org.amanzi.awe.neo.views.network.views.NeoNetworkView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.neo4j.api.core.EmbeddedNeo;

public class NeoReaderResolveChangeReporter implements IResolveChangeListener {

	
	EmbeddedNeo neo;
	
	public NeoReaderResolveChangeReporter(EmbeddedNeo neo) 
	{// TODO Auto-generated constructor stub
		this.neo=neo;
	}


	@Override
	public void changed(IResolveChangeEvent event)
	{
		 switch( event.getType() ) 
		 {
         case POST_CHANGE:
        	 System.out.println("Resources have changed.");
             try 
             {
                 updateNetworkTreeView();
             }
             catch(Exception exc)
             {
            	 System.err.println("Error in updating NeoTreeView  :"+exc.toString());
             }
             break;
         case PRE_CLOSE:
        	 IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
             IWorkbenchPart part = window.getActivePage().findView(
                     NeoNetworkView.NETWORK_VIEW_ID);
             window.getActivePage().hideView((IViewPart) part);
             break;
         case PRE_DELETE:
         default:
             throw new IllegalStateException("Unexpected state occured!");
    
         }

	}
	
	
	 private void updateNetworkTreeView(  ) {
         final Display display = PlatformUI.getWorkbench().getDisplay();
         display.syncExec(new Runnable(){

             public void run() {

                 IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

                 try {
                     // Finding if the view is opened.
                     IWorkbenchPart part = window.getActivePage().findView(
                    		 NeoNetworkView.NETWORK_VIEW_ID);

                     if (part != null) {
                         window.getActivePage().hideView((IViewPart) part);
                     }

                     NeoNetworkView viewPart = (NeoNetworkView) window.getActivePage()
                             .showView(NeoNetworkView.NETWORK_VIEW_ID, null,
                                     IWorkbenchPage.VIEW_ACTIVATE);

                     viewPart.getViewer().setContentProvider(
                             new TreeViewContentProvider(neo));
                     viewPart.getViewer().setLabelProvider(new ViewLabelProvider());
                     viewPart.getViewer().setInput(viewPart.getViewSite());
                     viewPart.makeActions();
                     viewPart.hookDoubleClickAction();
                     viewPart.setFocus();
                     window.getActivePage().activate(viewPart);
                 } catch (PartInitException e) {
                     e.printStackTrace();
                 }
             }
         });
     }

}
