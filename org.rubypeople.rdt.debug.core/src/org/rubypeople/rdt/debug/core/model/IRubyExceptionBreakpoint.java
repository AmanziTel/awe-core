package org.rubypeople.rdt.debug.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;

public interface IRubyExceptionBreakpoint extends IBreakpoint
{
	/**
	 * Returns exception class name.
	 * 
	 * @return exception class name
	 * @throws CoreException 
	 */
	String getException();
}
