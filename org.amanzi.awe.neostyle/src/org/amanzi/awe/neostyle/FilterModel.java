/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.neostyle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * <p>
 *Model for work with filters
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class FilterModel implements IPropertyChangeListener {
    public static final String PROPERTY="FILTER_MAP";
    private static final Logger LOG =Logger.getLogger(FilterModel.class);
//    private static final FilterModel instance=new FilterModel();
//    public static FilterModel getInstance(){
//        return instance;
//    }
    private HashMap<String, IFilterWrapper>filters=new HashMap<String, IFilterWrapper>();
    public FilterModel(){
//        getPreferencesStore().addPropertyChangeListener(this);
        clear();
        String value = getPreferencesStore().getString(PROPERTY);
        if (!StringUtils.isEmpty(value)){
            restoreFromString(value);
        }
    }
    public IPreferenceStore getPreferencesStore(){
        return NeoStylePlugin.getDefault().getPreferenceStore();
    }
    public void dispose(){
        clear();
    }
    public void clear(){
        filters.clear();
    }
    public void store(){
        String value=storeToString();
        getPreferencesStore().setValue(PROPERTY, value);
    }
    public void restoreFromString(String output){
        clear();
        if (!output.isEmpty()) {
            ByteArrayInputStream bin = new ByteArrayInputStream(output.getBytes());
            ObjectInputStream in;
            try {
                in = new ObjectInputStream(new BufferedInputStream(bin));
                Object object = in.readObject();
                in.close();
                filters.putAll((Map< ? extends String, ? extends IFilterWrapper>)object);
            } catch (IOException e) {
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (ClassNotFoundException e) {
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
    }
   public String storeToString(){
       ByteArrayOutputStream bout = new ByteArrayOutputStream();

       ObjectOutputStream out;
       try {
           out = new ObjectOutputStream(new BufferedOutputStream(bout));
           out.writeObject(filters);
           out.close();
       } catch (IOException e) {
           throw (RuntimeException)new RuntimeException().initCause(e);
       }

       String value = new String(bout.toByteArray());
       LOG.debug("Store size " + value.length());
       return value;
   }
   
@Override
public void propertyChange(PropertyChangeEvent event) {
    if (PROPERTY.equals(event.getProperty())){
//        String newValue=
    }
}

public Map<String,  IFilterWrapper> formsMapByName(Collection<String> filterNames) {
    Map<String, IFilterWrapper> result=new HashMap<String, IFilterWrapper>();
    for (String name:filterNames){
        IFilterWrapper wr = filters.get(name);
        if (wr!=null){
           result.put(name, wr); 
        }
        
    }
    return result;
}

public Iterable<String> getFilterNames() {
    return Collections.unmodifiableCollection(filters.keySet());
}

public IFilterWrapper getWrapperByName(String name) {
    return filters.get(name);
}



}
