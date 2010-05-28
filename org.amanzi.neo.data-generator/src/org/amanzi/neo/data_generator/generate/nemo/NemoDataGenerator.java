package org.amanzi.neo.data_generator.generate.nemo;

import java.util.Date;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.data_generator.data.IGeneratedData;
import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.CommandRow;
import org.amanzi.neo.data_generator.data.calls.GeneratedCallsData;
import org.amanzi.neo.data_generator.data.calls.ProbeData;
import org.amanzi.neo.data_generator.data.nemo.PointData;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.data_generator.utils.RandomValueGenerator;

/**
 * Generate nemo test data
 * @author zhuhrou_a 
 * */
public class NemoDataGenerator implements IDataGenerator
{
    
    /** String TIME_FORMAT field */
    private static final String TIME_FORMAT = "HH:mm:ss.S";
    
    /** Date format field. example - "7.10.2009" */
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    
    /** Generated test asm data. */    
    private List<CallGroup> callGroups;
    
    /** Directory to which we save result  */
    private String baseDirectoryPath;
    
    /** File name to which saved generated data */
    private String fileName;
    
    /** Lines in generated test data */
    private List<PointData> generatedLines;
    
    /** Format date to time string (without date information) */
    private SimpleDateFormat timeFormat;
    
    /** Format date to date string (without time information)  */
    private SimpleDateFormat dateFormat;
    
    /** Date for all nemo data */
    private Date date;
        
    /**
     * Constructor
     * @param callGroups - generated ams data
     */
    public NemoDataGenerator(final List<CallGroup> aCallGroups,final String aDirectory, final String aFileName)
    {
        callGroups = aCallGroups;
        baseDirectoryPath = aDirectory;
        fileName = aFileName;
        generatedLines = new ArrayList<PointData>(30);
        timeFormat = new SimpleDateFormat(TIME_FORMAT);
        dateFormat = new SimpleDateFormat(DATE_FORMAT);
    }
    
    /**
    *  Generate test data
    */
    @Override
    public IGeneratedData generate() {
        generateData(callGroups);
        saveToFile();
        return new GeneratedCallsData(callGroups);
    }
    
    /**
     * Generate test data
     * @param callGroups asm data
     */
    private void generateData(final List<CallGroup> callGroups)
    {
        for(CallGroup callGroup : callGroups)
        {
            List<CallData> callData = callGroup.getData();
            for(CallData call : callData)
            {
                List<ProbeData> probesData = call.getReceiverProbes();
                for(ProbeData data : probesData)
                {
                    List<CommandRow> commands = data.getCommands();
                    for(CommandRow command : commands)
                    {
                        if(date == null)
                        {
                            date = command.getTime();
                        }
                        generateLine(command);
                    }
                }
            }
        }        
    }
    
    /**
     * Generate position data for command 
     * @param command for which generating position data
     */
    private void generateLine(CommandRow command)
    {
        //15:27:18.735 (используемый формат данных)
        Date time = command.getTime();
        String timeString = timeFormat.format(time);
        Float longitude = getRandomGenerator().getFloatValue(25f , 45f);
        Float latitude = getRandomGenerator().getFloatValue( 20f , 50f );
        PointData pointData = new PointData(timeString , longitude, latitude );
        generatedLines.add(pointData);
        command.setPointData(pointData);
    }
    
    private void saveToFile()
    {
        //first create directory for test
        File dataDirectory = new File(baseDirectoryPath);
        if(!dataDirectory.exists())
        {
            dataDirectory.mkdir();
        }
        File file = new File(dataDirectory, fileName);        
        if(file.exists()){
            throw new IllegalStateException("Dublicate file name <"+fileName+">.");
        }
        FileOutputStream fos = null;
        PrintWriter out = null;
        try
        {
            //file.createNewFile();
            fos = new FileOutputStream(file);
            out = new PrintWriter(fos);
            if(date != null)
            {
                out.println("#START,15:27:17.145,,\""+dateFormat.format(date)+"\"");
            }
            for(PointData pointData : generatedLines)
            {
                out.println(pointData.toString());
            }            
        }
        catch (FileNotFoundException e) {
            // TODO: inform logic 
            e.printStackTrace();
        }
        finally
        {
            if(out != null)
            {
                out.flush();
                out.close();
            }
        }
    }        
            
    /**
    * Getter for random generator.
    *
    * @return {@link RandomValueGenerator}
    */
    protected RandomValueGenerator getRandomGenerator() {
        return RandomValueGenerator.getGenerator();
    }    
}