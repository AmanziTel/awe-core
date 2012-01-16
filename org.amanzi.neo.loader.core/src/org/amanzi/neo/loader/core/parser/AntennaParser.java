package org.amanzi.neo.loader.core.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.loader.core.CountingFileInputStream;
import org.amanzi.neo.loader.core.config.IConfiguration;
import org.amanzi.neo.loader.core.ProgressEventImpl;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.model.IModel;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * Parser for antenna data
 * </p>
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 * @param <T1> saver
 * @param <T2> configuration
 */
public class AntennaParser<T1 extends ISaver<IModel, MappedData, T2>, T2 extends IConfiguration>
        extends
            AbstractParser<T1, T2, MappedData> {

    private static final Logger LOGGER = Logger.getLogger(AntennaParser.class);

    // separator
    private static final String SEPARATOR = " ";

    // constants
    private static final String HORIZONTAL = "HORIZONTAL";
    private static final String VERTICAL = "VERTICAL";

    private String charSetName = Charset.defaultCharset().name();

    private CountingFileInputStream is;
    protected BufferedReader reader;
    private double persentageOld = 0;

    public AntennaParser() {
        super();
        try {
            if (currentFile != null) {
                is = new CountingFileInputStream(currentFile);
                reader = new BufferedReader(new InputStreamReader(is, charSetName));
            }
        } catch (FileNotFoundException e) {
            // TODO Handle FileNotFoundException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (UnsupportedEncodingException e) {
            // TODO Handle UnsupportedEncodingException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    protected MappedData parseElement() {
        if (tempFile == null || tempFile != currentFile) {
            try {
                is = new CountingFileInputStream(currentFile);
                reader = new BufferedReader(new InputStreamReader(is, charSetName));
                tempFile = currentFile;
                persentageOld = 0;
            } catch (FileNotFoundException e) {
                // TODO Handle FileNotFoundException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (UnsupportedEncodingException e) {
                // TODO Handle UnsupportedEncodingException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
        try {
            String lineStr;
            MappedData element = new MappedData();
            while ((lineStr = reader.readLine()) != null) {
                if (lineStr != null) {
                    String[] line = lineStr.split(SEPARATOR);
                    if (line[0].equals(HORIZONTAL)) {
                        Integer count = Integer.parseInt(line[1]);
                        element.put(NetworkService.HORIZONTAL_PATTERNS_COUNT, line[1]);
                        for (int i = 1; i < count + 1; i++) {
                            String parameters = reader.readLine();
                            String[] param = parameters.split(SEPARATOR);
                            element.put(NetworkService.HORIZONTAL_ANGLE + i, param[0]);
                            element.put(NetworkService.HORIZONTAL_LOSS + i, param[1]);
                        }
                    } else if (line[0].equals(VERTICAL)) {
                        Integer count = Integer.parseInt(line[1]);
                        element.put(NetworkService.VERTICAL_PATTERNS_COUNT, line[1]);
                        for (int i = 1; i < count + 1; i++) {
                            String parameters = reader.readLine();
                            String[] param = parameters.split(SEPARATOR);
                            element.put(NetworkService.VERTICAL_ANGLE + i, param[0]);
                            element.put(NetworkService.VERTICAL_LOSS + i, param[1]);
                        }
                    } else {
                        element.put(line[0].toLowerCase(), line[1]);
                    }
                }
            }
            return element;
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        } finally {
            double percentage = is.percentage();
            if (percentage - persentageOld >= PERCENTAGE_FIRE) {
                persentageOld = percentage;
                fireSubProgressEvent(currentFile, new ProgressEventImpl(String.format(currentFile.getName()), percentage));
            }
        }
    }

    @Override
    protected void finishUpParse() {
        try {
            is.close();
            reader.close();
        } catch (IOException e) {
            AweConsolePlugin.error("Cannt't close stream");
            LOGGER.error("cannt't close stream", e);
        }
    }
}
