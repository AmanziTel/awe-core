package org.amanzi.awe.catalog.neo.actions;

import java.io.IOException;


import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeListener;

public class NeoReaderResolveChangeReporter implements IResolveChangeListener {

	@Override
	public void changed(IResolveChangeEvent event)
	{
		 switch( event.getType() ) 
		 {
         case POST_CHANGE:
           
             break;
         case PRE_CLOSE:
            
             break;
         case PRE_DELETE:
         default:
    
         }

	}

}
