package net.refractions.udig.project;

import java.util.List;

public interface IRubyProject extends IProjectElement {
    
    /**
     * Returns all Children of RubyProject by given Type
     *
     * @param type type of Children
     * @return children of given type
     */
    
    public List<IRubyProjectElement> getElements(Class type );

}
