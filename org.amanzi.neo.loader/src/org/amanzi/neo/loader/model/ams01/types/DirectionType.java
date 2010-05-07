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
 * Class DirectionType.
 * 
 * @version $Revision$ $Date$
 */
public class DirectionType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The Get type
     */
    public static final int GET_TYPE = 0;

    /**
     * The instance of the Get type
     */
    public static final DirectionType GET = new DirectionType(GET_TYPE, "Get");

    /**
     * The Put type
     */
    public static final int PUT_TYPE = 1;

    /**
     * The instance of the Put type
     */
    public static final DirectionType PUT = new DirectionType(PUT_TYPE, "Put");

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

    private DirectionType(final int type, final java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate.Returns an enumeration of all possible
     * instances of DirectionType
     * 
     * @return an Enumeration over all possible instances of
     * DirectionType
     */
    public static java.util.Enumeration enumerate(
    ) {
        return _memberTable.elements();
    }

    /**
     * Method getType.Returns the type of this DirectionType
     * 
     * @return the type of this DirectionType
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
        members.put("Get", GET);
        members.put("Put", PUT);
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
     * DirectionType
     * 
     * @return the String representation of this DirectionType
     */
    public java.lang.String toString(
    ) {
        return this.stringValue;
    }

    /**
     * Method valueOf.Returns a new DirectionType based on the
     * given String value.
     * 
     * @param string
     * @return the DirectionType value of parameter 'string'
     */
    public static org.amanzi.neo.loader.model.ams01.types.DirectionType valueOf(
            final java.lang.String string) {
        java.lang.Object obj = null;
        if (string != null) {
            obj = _memberTable.get(string);
        }
        if (obj == null) {
            String err = "" + string + " is not a valid DirectionType";
            throw new IllegalArgumentException(err);
        }
        return (DirectionType) obj;
    }

}
