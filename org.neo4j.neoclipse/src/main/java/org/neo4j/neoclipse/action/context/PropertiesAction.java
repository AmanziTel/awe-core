package org.neo4j.neoclipse.action.context;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.neo4j.neoclipse.action.AbstractGraphAction;
import org.neo4j.neoclipse.action.Actions;
import org.neo4j.neoclipse.view.NeoGraphViewPart;

/**
 * Action to open the properties editor for a node or relationship.
 * @author Ahmed Abdelsalam
 */
public class PropertiesAction extends AbstractGraphAction
{
	public PropertiesAction( NeoGraphViewPart neoGraphViewPart )
	{
		super( Actions.PROPERTIES, neoGraphViewPart );
		setEnabled( true );
	}

	@Override
	public void run()
	{
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IPageLayout.ID_PROP_SHEET);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}