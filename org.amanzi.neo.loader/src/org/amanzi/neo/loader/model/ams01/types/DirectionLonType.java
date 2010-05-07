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
 * Class DirectionLonType.
 * 
 * @version $Revision$ $Date$
 */
public class DirectionLonType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The E type
     */
    public static final int E_TYPE = 0;

    /**
     * The instance of the E type
     */
    public static final DirectionLonType E = new DirectionLonType(E_TYPE, "E");

    /**
     * The W type
     */
    public static final int W_TYPE = 1;

    /**
     * The instance of the W type
     */
    public static final DirectionLonType W = new DirectionLonType(W_TYPE, "W");

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

    private DirectionLonType(final int type, final java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate.Returns an enumeration of all possible
     * instances of DirectionLonType
     * 
     * @return an Enumeration over all possible instances of
     * DirectionLonType
     */
    public static java.util.Enumeration enumerate(
    ) {
        return _memberTable.elements();
    }

    /**
     * Method getType.Returns the type of this DirectionLonType
     * 
     * @return the type of this DirectionLonType
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
        members.put("E", E);
        members.put("W", W);
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
     * DirectionLonType
     * 
     * @return the String representation of this DirectionLonType
     */
    public java.lang.String toString(
    ) {
        return this.stringValue;
    }

    /**
     * Method valueOf.Returns a new DirectionLonType based on the
     * given String value.
     * 
     * @param string
     * @return the DirectionLonType value of parameter 'string'
     */
    public static org.amanzi.neo.loader.model.ams01.types.DirectionLonType valueOf(
            final java.lang.String string) {
        java.lang.Object obj = null;
        if (string != null) {
            obj = _memberTable.get(string);
        }
        if (obj == null) {
            String err = "" + string + " is not a valid DirectionLonType";
            throw new IllegalArgumentException(err);
        }
        return (DirectionLonType) obj;
    }

}
