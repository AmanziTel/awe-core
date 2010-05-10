/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * Class StatusType.
 * 
 * @version $Revision$ $Date$
 */
public class StatusType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The 2 type
     */
    public static final int VALUE_2_TYPE = 0;

    /**
     * The instance of the 2 type
     */
    public static final StatusType VALUE_2 = new StatusType(VALUE_2_TYPE, "2");

    /**
     * The 3 type
     */
    public static final int VALUE_3_TYPE = 1;

    /**
     * The instance of the 3 type
     */
    public static final StatusType VALUE_3 = new StatusType(VALUE_3_TYPE, "3");

    /**
     * The 8 type
     */
    public static final int VALUE_8_TYPE = 2;

    /**
     * The instance of the 8 type
     */
    public static final StatusType VALUE_8 = new StatusType(VALUE_8_TYPE, "8");

    /**
     * The 9 type
     */
    public static final int VALUE_9_TYPE = 3;

    /**
     * The instance of the 9 type
     */
    public static final StatusType VALUE_9 = new StatusType(VALUE_9_TYPE, "9");

    /**
     * Field _memberTable.
     */
    private static java.util.Hashtable _memberTable = init();

    /**
     * Field type.
     */
    private final int type;

    /**
     * Field stringValue.
     */
    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private StatusType(final int type, final java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate.Returns an enumeration of all possible
     * instances of StatusType
     * 
     * @return an Enumeration over all possible instances of
     * StatusType
     */
    public static java.util.Enumeration enumerate(
    ) {
        return _memberTable.elements();
    }

    /**
     * Method getType.Returns the type of this StatusType
     * 
     * @return the type of this StatusType
     */
    public int getType(
    ) {
        return this.type;
    }

    /**
     * Method init.
     * 
     * @return the initialized Hashtable for the member table
     */
    private static java.util.Hashtable init(
    ) {
        Hashtable members = new Hashtable();
        members.put("2", VALUE_2);
        members.put("3", VALUE_3);
        members.put("8", VALUE_8);
        members.put("9", VALUE_9);
        members.put("0", new StatusType(4, "0"));
        return members;
    }

    /**
     * Method readResolve. will be called during deserialization to
     * replace the deserialized object with the correct constant
     * instance.
     * 
     * @return this deserialized object
     */
    private java.lang.Object readResolve(
    ) {
        return valueOf(this.stringValue);
    }

    /**
     * Method toString.Returns the String representation of this
     * StatusType
     * 
     * @return the String representation of this StatusType
     */
    @Override
    public java.lang.String toString(
    ) {
        return this.stringValue;
    }

    /**
     * Method valueOf.Returns a new StatusType based on the given
     * String value.
     * 
     * @param string
     * @return the StatusType value of parameter 'string'
     */
    public static org.amanzi.neo.loader.model.ams01.types.StatusType valueOf(
            final java.lang.String string) {
        java.lang.Object obj = null;
        if (string != null) {
            obj = _memberTable.get(string);
        }
        if (obj == null) {
            String err = "" + string + " is not a valid StatusType";
            throw new IllegalArgumentException(err);
        }
        return (StatusType) obj;
    }

}
