/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.rubypeople.rdt.internal.core;

import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISpreadsheet;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.core.util.MementoTokenizer;

/**
 * Class that implements ISpreadsheet interface
 * 
 * This class implements methods for providing access to Spreadsheet from Ruby Project Tree
 * 
 * @author Lagutko_N
 */
public class Spreadsheet extends RubyElement implements ISpreadsheet {
    
    protected String name;

    /**
     * @param parent
     */
    public Spreadsheet(RubyElement parent, String spreadsheetName) {
        super(parent);
        name = spreadsheetName;
    }
    
    @Override
    public String getElementName() {
        return name;
    }

    @Override
    protected void closing(Object info) throws RubyModelException {
    }

    @Override
    protected Object createElementInfo() {
        return null;
    }

    @Override
    protected void generateInfos(Object info, HashMap newElements, IProgressMonitor monitor) throws RubyModelException {
    }

    @Override
    public int getElementType() {
        return 0;
    }

    @Override
    public IRubyElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner owner) {
        return null;
    }

    @Override
    protected char getHandleMementoDelimiter() {
        return 0;
    }

    public IResource getCorrespondingResource() throws RubyModelException {
        return null;
    }

    public IPath getPath() {
        return null;
    }

    public IResource getResource() {
        return null;
    }

    public IResource getUnderlyingResource() throws RubyModelException {
        return null;
    }

    public boolean isStructureKnown() throws RubyModelException {
        return false;
    }
    
    /**
     * Returns true if this handle represents the same Spreadsheet element as the given
     * handle.
     * 
     * @see Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Spreadsheet)) return false;
        Spreadsheet other = (Spreadsheet) obj;
        return this.parent.equals(other.parent) && super.equals(obj);
    }

}
