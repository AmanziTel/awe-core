package org.amanzi.neo.services.utils;

/**
 * Interface for Task that must return a Result
 * 
 * @author Lagutko_N
 */
public interface RunnableWithResult<T> extends Runnable {
    
    /**
     * Computed result
     *
     * @return result of this task
     */
    public T getValue();
    
}