package org.amanzi.awe.afp.wizards;

import java.awt.Color;

import org.amanzi.awe.afp.executors.AfpProcessProgress;
import org.amanzi.awe.afp.models.AfpModel;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.neo4j.graphdb.GraphDatabaseService;

public class AfpProgressPage extends AfpWizardPage implements AfpProcessProgress{
	
	private AfpModel model;
	JFreeChart chart;
    XYSeries series[];
    XYSeriesCollection dataset = new XYSeriesCollection();
    XYLineAndShapeRenderer renderer;
    //private JFreeChart chart;
    private Button[] colorButtons = new Button[8];
	private String[] graphParams = new String[]{
		"Total",
		"Sector Separations",
		"Interference",
		"Site Separations",
		"Neighbour",
		"Frequency Constraints",
		"Triangulation",
		"Shadowing"
	};
	private Color[] seriesColors = new Color[] {
			Color.RED,
			Color.ORANGE,
			Color.YELLOW,
			Color.GREEN,
			Color.BLUE,
			Color.LIGHT_GRAY,
			Color.GRAY,
			Color.CYAN			
	};
	private boolean[] seriesVisible = new boolean[]{ true,true,true,true,true,true,true,true};
	private Button[] paramButtons = new Button[8];
	
	public AfpProgressPage(String pageName, AfpModel model, String desc) {
		super(pageName);
        this.model = model;
        setTitle(AfpImportWizard.title);
        setDescription(desc);
        setPageComplete (false);
        //model.getExecutor()
	}

	
	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
   	 	main.setLayout(new GridLayout(2, false));
   	 	main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 2));
   	 	
   	 	Group summaryGroup = new Group(main, SWT.NONE);
   	 	summaryGroup.setLayout(new GridLayout(2, false));
   	 	summaryGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
   	 	summaryGroup.setText("Summary");
   	 	
   	 	new Label(summaryGroup, SWT.LEFT).setText("Selected Sectors:");
   	 	new Label(summaryGroup, SWT.LEFT).setText("0");
	   	new Label(summaryGroup, SWT.LEFT).setText("Selected TRXs:");
	   	new Label(summaryGroup, SWT.LEFT).setText("0");
	   	new Label(summaryGroup, SWT.LEFT).setText("BCCH TRXs:");
	   	new Label(summaryGroup, SWT.LEFT).setText("0");
	   	new Label(summaryGroup, SWT.LEFT).setText("TCH Non/BB Hopping TRXs");
	   	new Label(summaryGroup, SWT.LEFT).setText("0");
	   	new Label(summaryGroup, SWT.LEFT).setText("TCH SY Hopping TRXs");
	   	new Label(summaryGroup, SWT.LEFT).setText("0");
   	 	
   	 	
   	 	
	   	TabFolder tabFolder =new TabFolder(main, SWT.NONE | SWT.BORDER);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		
		TabItem item1 =new TabItem(tabFolder,SWT.NONE);
		item1.setText("Graph");
		
        //chart = createChart();
		Group graphGroup = new Group(tabFolder, SWT.NONE);
		graphGroup.setLayout(new GridLayout(6, false));
		graphGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		
		chart = createChart(createDataset());
		GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true, 6 ,1);
		gridData.heightHint = 200;
		gridData.minimumWidth = 100;
	
		final ChartComposite frame = new ChartComposite(graphGroup, SWT.NONE, chart, true);
		frame.setLayoutData(gridData);
		
		
		Display display = parent.getShell().getDisplay();
		for (int i = 0; i < graphParams.length; i++){
			colorButtons[i] = new Button(graphGroup, SWT.PUSH);
			colorButtons[i].setBackground(
					new org.eclipse.swt.graphics.Color(display, 
							this.seriesColors[i].getRed(),
							this.seriesColors[i].getGreen(),this.seriesColors[i].getBlue())); 
					
			paramButtons[i] = new Button(graphGroup, SWT.CHECK);
			paramButtons[i].setSelection(true);
			paramButtons[i].setText(graphParams[i]);
			paramButtons[i].setData(new Integer(i));
			
			paramButtons[i].addSelectionListener(new SelectionAdapter(){
	    		@Override
				public void widgetSelected(SelectionEvent e) {
    				Button button = ((Button)e.getSource());
	    			
	    			if(button.getData() != null) {
	    				int i = ((Integer)button.getData()).intValue();
	    				
	    				if(button.getSelection()) {
	    					seriesVisible[i] = true;
	    					dataset.addSeries(series[i]);
	    				} else {
	    					seriesVisible[i] = false;
	    					dataset.removeSeries(series[i]);
	    				}
	    		        for(int k=0, j=0;k<series.length;k++) {
	    		        	if(seriesVisible[k]) {
	    		        		renderer.setSeriesPaint(j, seriesColors[k]);
	    		        		j++;
	    		        	}
	    		        }
	    			}
				}
	    	});

		}
		colorButtons[0].setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
		paramButtons[0].setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
		colorButtons[5].setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, true, 1, 2));
		paramButtons[5].setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, true, 1, 2));
		
		item1.setControl(graphGroup);
		
		TabItem item2 =new TabItem(tabFolder,SWT.NONE);
		item2.setText("Table");
		
		Group tableGroup = new Group(tabFolder, SWT.NONE);
		tableGroup.setLayout(new GridLayout(6, false));
		tableGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		
//		Text progressText = new Text (graphGroup, SWT.BORDER | SWT.MULTI);
//		GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true, 6 ,1);
//		gridData.heightHint = 200;
//		gridData.minimumWidth = 100;
//		progressText.setLayoutData(gridData);
//		progressText.setText("Summary Report Content");
		
		item2.setControl(tableGroup);
		
		
		Group controlGroup = new Group(main, SWT.NONE);
		controlGroup.setLayout(new GridLayout(1, false));
		controlGroup.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 1, 1));
		controlGroup.setText("Control");
		
		Button pauseButton = new Button(controlGroup, SWT.PUSH);
		pauseButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		pauseButton.setText("Pause");
		pauseButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		Button resumeButton = new Button(controlGroup, SWT.PUSH);
		resumeButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		resumeButton.setText("Resume");
		resumeButton.setEnabled(false);
		resumeButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		Button stopButton = new Button(controlGroup, SWT.PUSH);
		stopButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, true, true));
		stopButton.setText("Stop");
		stopButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		setPageComplete (true);
		setControl(main);
	}
	
	@Override
	public IWizardPage getPreviousPage(){
		return null;
	}

	private XYDataset createDataset() {

		series = new XYSeries[graphParams.length];
        dataset = new XYSeriesCollection();

        
		for(int i=0; i< graphParams.length;i++) {
			series[i] = new XYSeries(graphParams[i]);
			//for(int j=0;j<8;i++)
				//series[i].add(1.0,i);
				//series[i].add(2.0,i);
			
	        dataset.addSeries(series[i]);
		}
        return dataset;
        
    }

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            dataset.
	 * 
	 * @return A chart.
	 */
	 private JFreeChart createChart(final XYDataset dataset) {
	        
	        // create the chart...
	        final JFreeChart chart = ChartFactory.createXYLineChart(
	            null,      // chart title
	            null,                      // x axis label
	            "Sectors",                      // y axis label
	            dataset,                  // data
	            PlotOrientation.VERTICAL,
	            false,                     // include legend
	            true,                     // tooltips
	            false                     // urls
	        );

	        chart.setBackgroundPaint(Color.white);

	        // get a reference to the plot for further customisation...
	        final XYPlot plot = chart.getXYPlot();
	        plot.setBackgroundPaint(Color.lightGray);
	        plot.setDomainGridlinePaint(Color.white);
	        plot.setRangeGridlinePaint(Color.white);
	        
	        renderer = new XYLineAndShapeRenderer();
	        renderer.setSeriesLinesVisible(0, false);
	        renderer.setSeriesShapesVisible(1, false);
	        plot.setRenderer(renderer);
	        for(int i=0;i<series.length;i++) {
	        	renderer.setSeriesPaint(i, this.seriesColors[i]);
	        }

	        // change the auto tick unit selection to integer units only...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        // OPTIONAL CUSTOMISATION COMPLETED.
	                
	        return chart;
	    }


	@Override
	public void onProgressUpdate(int result,long time, int remaingtotal,
			int sectorSeperations, int siteSeperation, int freqConstraints,
			int interference, int neighbor, int tringulation, int shadowing) {

		series[0].add(time,remaingtotal);
		series[1].add(time,sectorSeperations);
		series[2].add(time,siteSeperation);
		series[3].add(time,freqConstraints);
		series[4].add(time,interference);
		series[5].add(time,neighbor);
		series[6].add(time,tringulation);
		series[7].add(time,shadowing);
		
	}
}
