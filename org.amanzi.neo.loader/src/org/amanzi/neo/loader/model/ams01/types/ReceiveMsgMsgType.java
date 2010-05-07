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
 * Class ReceiveMsgMsgType.
 * 
 * @version $Revision$ $Date$
 */
public class ReceiveMsgMsgType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The 12 type
     */
    public static final int VALUE_12_TYPE = 0;

    /**
     * The instance of the 12 type
     */
    public static final ReceiveMsgMsgType VALUE_12 = new ReceiveMsgMsgType(VALUE_12_TYPE, "12");

    /**
     * The 13 type
     */
    public static final int VALUE_13_TYPE = 1;

    /**
     * The instance of the 13 type
     */
    public static final ReceiveMsgMsgType VALUE_13 = new ReceiveMsgMsgType(VALUE_13_TYPE, "13");

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

    private ReceiveMsgMsgType(final int type, final java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate.Returns an enumeration of all possible
     * instances of ReceiveMsgMsgType
     * 
     * @return an Enumeration over all possible instances of
     * ReceiveMsgMsgType
     */
    public static java.util.Enumeration enumerate(
    ) {
        return _memberTable.elements();
    }

    /**
     * Method getType.Returns the type of this ReceiveMsgMsgType
     * 
     * @return the type of this ReceiveMsgMsgType
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
        members.put("12", VALUE_12);
        members.put("13", VALUE_13);
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
     * ReceiveMsgMsgType
     * 
     * @return the String representation of this ReceiveMsgMsgType
     */
    public java.lang.String toString(
    ) {
        return this.stringValue;
    }

    /**
     * Method valueOf.Returns a new ReceiveMsgMsgType based on the
     * given String value.
     * 
     * @param string
     * @return the ReceiveMsgMsgType value of parameter 'string'
     */
    public static org.amanzi.neo.loader.model.ams01.types.ReceiveMsgMsgType valueOf(
            final java.lang.String string) {
        java.lang.Object obj = null;
        if (string != null) {
            obj = _memberTable.get(string);
        }
        if (obj == null) {
            String err = "" + string + " is not a valid ReceiveMsgMsgType";
            throw new IllegalArgumentException(err);
        }
        return (ReceiveMsgMsgType) obj;
    }

}
