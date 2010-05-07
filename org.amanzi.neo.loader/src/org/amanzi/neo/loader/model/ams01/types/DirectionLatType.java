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
 * Class DirectionLatType.
 * 
 * @version $Revision$ $Date$
 */
public class DirectionLatType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The N type
     */
    public static final int N_TYPE = 0;

    /**
     * The instance of the N type
     */
    public static final DirectionLatType N = new DirectionLatType(N_TYPE, "N");

    /**
     * The S type
     */
    public static final int S_TYPE = 1;

    /**
     * The instance of the S type
     */
    public static final DirectionLatType S = new DirectionLatType(S_TYPE, "S");

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

    private DirectionLatType(final int type, final java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate.Returns an enumeration of all possible
     * instances of DirectionLatType
     * 
     * @return an Enumeration over all possible instances of
     * DirectionLatType
     */
    public static java.util.Enumeration enumerate(
    ) {
        return _memberTable.elements();
    }

    /**
     * Method getType.Returns the type of this DirectionLatType
     * 
     * @return the type of this DirectionLatType
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
        members.put("N", N);
        members.put("S", S);
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
     * DirectionLatType
     * 
     * @return the String representation of this DirectionLatType
     */
    public java.lang.String toString(
    ) {
        return this.stringValue;
    }

    /**
     * Method valueOf.Returns a new DirectionLatType based on the
     * given String value.
     * 
     * @param string
     * @return the DirectionLatType value of parameter 'string'
     */
    public static org.amanzi.neo.loader.model.ams01.types.DirectionLatType valueOf(
            final java.lang.String string) {
        java.lang.Object obj = null;
        if (string != null) {
            obj = _memberTable.get(string);
        }
        if (obj == null) {
            String err = "" + string + " is not a valid DirectionLatType";
            throw new IllegalArgumentException(err);
        }
        return (DirectionLatType) obj;
    }

}
