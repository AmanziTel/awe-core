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
package org.amanzi.awe.script;

import java.io.FileReader;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import net.refractions.udig.ui.operations.IOp;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class FeatureCollectionInfoOpRuby implements IOp {

    @SuppressWarnings("nls")
    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        ClassLoader remember = Thread.currentThread().getContextClassLoader();
        try{
            // This hack was needed so that the ruby code can find the same java classes as the current java code
            Thread.currentThread().setContextClassLoader(FeatureCollectionInfoOpRuby.class.getClassLoader());
    
            ScriptEngineManager m = new ScriptEngineManager();
            ScriptEngine rubyEngine = m.getEngineByName("jruby");
            ScriptContext context = rubyEngine.getContext();
    
            Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("display", display);
            bindings.put("target", target);
            bindings.put("monitor", monitor);

            //rubyEngine.eval("require '/home/craig/.m2/repository/org/opengis/geoapi/2.2-SNAPSHOT/geoapi-2.2-20080605.180517-15.jar'");
            URL scriptURL = FileLocator.toFileURL(Activator.getDefault().getBundle().getEntry("feature_collection_op.rb"));
            rubyEngine.eval(new FileReader(scriptURL.getPath()), context);
        } catch (ScriptException e) {
            System.out.println(e.toString()+": "+e.getFileName()+"["+e.getLineNumber()+":"+e.getColumnNumber()+"]: "+e.getMessage());
            e.printStackTrace();
        }finally{
            Thread.currentThread().setContextClassLoader(remember);
        }
    }

}
