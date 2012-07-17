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

package org.amanzi.awe.statistics.template;

/**
 * <p>
 * Condition
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public enum Condition {
    LT("<"){

        @Override
        public Condition getInverseCondition() {
            return GE;
        }
        
    }, 
    LE("<="){

        @Override
        public Condition getInverseCondition() {
            return GT;
        }
        
    }, 
    GT(">"){

        @Override
        public Condition getInverseCondition() {
            return LE;
        }
        
    }, 
    GE(">="){

        @Override
        public Condition getInverseCondition() {
            return GT;
        }
        
    }, 
    NE("!="){

        @Override
        public Condition getInverseCondition() {
            return EQ;
        }
        
    }, 
    EQ("=="){

        @Override
        public Condition getInverseCondition() {
            return NE;
        }
        
    };
    private String text;

    /**
     * @param text
     */
    private Condition(String text) {
        this.text = text;
    }

    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
    
    public abstract Condition getInverseCondition();

    public static Condition findConditionByText(String text) {
        for (Condition condition : values()) {
            if (condition.getText().equals(text)) {
                return condition;
            }
        }
        return null;
    }
}
