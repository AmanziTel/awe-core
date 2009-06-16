package org.amanzi.neo.loader.tools;


import net.refractions.udig.project.ui.tool.ActionTool;
import net.refractions.udig.project.ui.tool.SimpleTool;

import org.neo4j.api.core.EmbeddedNeo;

public class LoadNetwork extends SimpleTool implements ActionTool 
{
	EmbeddedNeo neo ;
	ShowFileDialog SFD;

	public void run() 
	{
		SFD=new ShowFileDialog();
		SFD.run();
	}
}
