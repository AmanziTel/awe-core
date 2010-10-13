/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C), 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.neo.loader.nemo_enum;

import java.util.Random;

/**
 * <p>
 * FTPcause enum for nemo data
 * </p>.
 *
 * @author Saelenchits_N
 * @since 1.0.0
 */
@Deprecated
public enum FTPcause {
    
    /** The TIMEOUT. */
    TIMEOUT(1 ,"Timeout"),
    
    /** The INVALI d_ remot e_ address. */
    INVALID_REMOTE_ADDRESS(2 ,"Invalid remote address"),
    
    /** The INVALI d_ usernam e_ password. */
    INVALID_USERNAME_PASSWORD(3 ,"Invalid username/password"),
    
    /** The INVALI d_ remot e_ file. */
    INVALID_REMOTE_FILE(4 ,"Invalid remote file"),
    
    /** The INVALI d_ loca l_ file. */
    INVALID_LOCAL_FILE(5 ,"Invalid local file"),
    
    /** The SERVIC e_ no t_ available. */
    SERVICE_NOT_AVAILABLE(421 ,"Service not available, closing control connection."),
    
    /** The CANNO t_ ope n_ connection. */
    CANNOT_OPEN_CONNECTION(425 ,"Cannot open data connection."),
    
    /** The CONNECTIO n_ closed. */
    CONNECTION_CLOSED(426 ,"Connection closed, transfer aborted."),
    
    /** The FIL e_ busy. */
    FILE_BUSY(450 ,"Requested file action not taken. File unavailable (e.g., file busy),."),
    
    /** The LOCA l_ error. */
    LOCAL_ERROR(451 ,"Requested action aborted, local error in processing."),
    
    /** The REQUES t_ no t_ taken. */
    REQUEST_NOT_TAKEN(452 ,"Requested action not taken. Insufficient storage space in system."),
    
    /** The COMMAN d_ synta x_ error. */
    COMMAND_SYNTAX_ERROR (500 ,"Syntax error, command unrecognized. This may include errors such as command line too long."),
    
    /** The PARAMETER s_ synta x_ error. */
    PARAMETERS_SYNTAX_ERROR (501 ,"Syntax error in parameters or arguments."),
    
    /** The COMMAN d_ no t_ implemented. */
    COMMAND_NOT_IMPLEMENTED(502 ,"Command not implemented."),
    
    /** The BA d_ sequence. */
    BAD_SEQUENCE(503 ,"Bad sequence of commands."),
    
    /** The COMMAN d_ fo r_ paramete r_ no t_ implemented. */
    COMMAND_FOR_PARAMETER_NOT_IMPLEMENTED(504 ,"Command not implemented for that parameter."),
    
    /** The USE r_ no t_ logge d_ in. */
    USER_NOT_LOGGED_IN(530 ,"User not logged in."),
    
    /** The NEE d_ account. */
    NEED_ACCOUNT(532 ,"Need account for storing files."),
    
    /** The FIL e_ no t_ found. */
    FILE_NOT_FOUND(550 ,"Requested action not taken. File unavailable (e.g., file not found, no access),."),
    
    /** The STORAG e_ allocatio n_ exceeded. */
    STORAGE_ALLOCATION_EXCEEDED(552 ,"Requested file action aborted, storage allocation exceeded."),
    
    /** The ILLEGA l_ fil e_ name. */
    ILLEGAL_FILE_NAME(553 ,"Requested action not taken. Illegal file name.");
    
    /** The id. */
    private final int id;
    
    /** The description. */
    private final String description;

    /**
     * Instantiates a new FTP cause.
     *
     * @param id the id
     * @param description the description
     */
    private FTPcause(int id, String description) {
        this.id = id;
        this.description = description;
    }
    
    /**
     * Gets the random cause.
     *
     * @return the random cause
     */
    public static FTPcause getRandomCause(){
        Random r= new Random();
        return FTPcause.values()[r.nextInt(FTPcause.values().length)];
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

}
