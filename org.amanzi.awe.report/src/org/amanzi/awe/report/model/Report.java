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

package org.amanzi.awe.report.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.report.model.events.IReportModelListener;
import org.amanzi.awe.report.model.events.ReportEventType;
import org.amanzi.awe.report.model.events.ReportModelEvent;
import org.amanzi.neo.core.utils.Pair;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class Report {
    public static final String FIRST_ARGUMENT="first_argument";
    private List<String> errors=new ArrayList<String>(0);
    private String name;
    private String date;
    private String author;
    private String file;
    private List<IReportPart> parts = new ArrayList<IReportPart>(0);
    private List<IReportModelListener> listeners = new ArrayList<IReportModelListener>(0);


    /**
     * @param name
     * @param date
     * @param author
     */
    public Report(String name, String date, String author) {
        super();
        this.name = name;
        this.date = date;
        this.author = author;
    }

    /**
     * @param name
     */
    public Report(String name) {
        super();
        this.name = name;
    }

    public Report() {

    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the date.
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date The date to set.
     */
    public void setDate(String date) {
        System.out.println("java setDate: " + date);
        this.date = date;
    }

    /**
     * @return Returns the author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author The author to set.
     */
    public void setAuthor(String author) {
        System.out.println("java setAuthor: " + author);
        this.author = author;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString());
        sb.append(" name: ").append(name).append("; author: ").append(author).append("; date: ").append(date).append(
                partsToString());
        return sb.toString();
    }

    private String partsToString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\nParts:");
        for (IReportPart part : parts) {
            sb.append("\n").append(part.getClass().getName()).append(" ");
            if (part instanceof ReportText) {
                sb.append(((ReportText)part).getText());
            } else if (part instanceof Chart) {
                sb.append(((Chart)part).getName());

            }
        }
        return sb.toString();
    }

    public void addText(ReportText text) {
        parts.add(text);
    }

    /**
     * @return Returns the parts.
     */
    public List<IReportPart> getParts() {
        return parts;
    }


    /**
     * @return Returns the file.
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file The file to set.
     */
    public void setFile(String file) {
        this.file = file;
    }

    public String getScript() {
        StringBuffer sb=  new StringBuffer("report '").append(name).append("' do\n").append("author '").append(author).append("'\n").append("date '")
                .append(date).append("'");
        for (IReportPart part:parts){
            sb.append("\n").append(part.getScript());
        }
        return sb.append("\nend").toString();

    }

    public void addImage(ReportImage imagePart) {
    }
    /**
     * Adds part. Event will not be generated. This method is intended to be called from Ruby Model
     * Builder.
     * 
     * @param part - the part has been added
     */
    public void addPart(IReportPart part) {
        System.out.println("Part was added: "+parts.size());
        part.setIndex(parts.size());
        parts.add(part);
    }

    /**
     * Removes the part
     * 
     * @param part - the part to be removed
     */
    public void removePart(IReportPart part) {
        Iterator<IReportPart> iterator = parts.iterator();
        while (iterator.hasNext()) {
            IReportPart next = iterator.next();
            if (next.equals(part)) {
                iterator.remove();
                while (iterator.hasNext()) {
                    next = iterator.next();
                    next.setIndex(next.getIndex() - 1);
                }
                firePartRemoved(part);
            }
        }
    }

    /**
     * Moves the part up
     * 
     * @param part - the part to be moved up
     */
    public void movePartUp(IReportPart part) {
        int index = part.getIndex();
        if (index > 0) {
            IReportPart previous = parts.get(index - 1);
            previous.setIndex(index);
            part.setIndex(index - 1);
            parts.set(index, previous);
            parts.set(index - 1, part);
            firePartMovedUp(part);
        }
    }

    /**
     * Moves the part down
     * 
     * @param part - the part to be moved down
     */
    public void movePartDown(IReportPart part) {
        int index = part.getIndex();
        if (index < parts.size() - 1) {
            IReportPart next = parts.get(index + 1);
            next.setIndex(index);
            part.setIndex(index + 1);
            parts.set(index, next);
            parts.set(index + 1, part);
            firePartMovedDown(part);
        }
    }

    /**
     * Fires part added event
     * 
     * @param part - the part has been added
     * @param script - the script text from which this part has been generated
     */
    public void firePartAdded(IReportPart part, String script) {
        firePartEvent(ReportEventType.PART_ADDED, part, script);
    }
    /**
     * Fires part added event
     * 
     * @param part - the part has been added
     * @param script - the script text from which this part has been generated
     */
    public void firePartPropertyChanged(IReportPart part, String property, String newValue) {
        firePartEvent(ReportEventType.PROPERTY_CHANGED, part, new Pair<String,String>(property,newValue));
    }

    /**
     * Fires part removed event
     * 
     * @param part - the part has been removed
     */
    private void firePartRemoved(IReportPart part) {
        firePartEvent(ReportEventType.PART_REMOVED, part, null);
    }

    /**
     * Fires part moved up event
     * 
     * @param part - the part has been moved up
     */
    private void firePartMovedUp(IReportPart part) {
        firePartEvent(ReportEventType.PART_MOVED_UP, part, null);// new index

    }

    /**
     * Fires part moved down event
     * 
     * @param part - the part has been moved down
     */
    private void firePartMovedDown(IReportPart part) {
        firePartEvent(ReportEventType.PART_MOVED_DOWN, part, null);// new index
    }

    /**
     * Fires the part event according to its type
     * 
     * @param eventType - the event type to be fired
     * @param part - the source part for the event
     * @param script - the script text for the part, may be null
     */

    private void firePartEvent(ReportEventType eventType, IReportPart part, Object data) {
        ReportModelEvent event = new ReportModelEvent(eventType, part, data);
        for (IReportModelListener l : listeners) {
            l.reportChanged(event);
        }
    }

    /**
     * @param listener
     */
    public void addReportListener(IReportModelListener listener) {
        listeners.add(listener);
    }

    /**
     * @param listener
     */

    public void removeReportListener(IReportModelListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Removes all listeners
     */
     
    public void removeAllReportListeners() {
        Iterator<IReportModelListener> iterator = listeners.iterator();
        while (iterator.hasNext()){
            iterator.next();
            iterator.remove();
        }
    }

    /**
     * Adds an error to the report error list
     * 
     * @param error error to be added
     */
    public void addError(String error) {
        errors.add(error);
    }

    /**
     * @return Returns the errors.
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Checks if report has errors or not
     * @return true if report has errors, false otherwise
     */
    public boolean hasErrors() {
        return errors.size() != 0;
    }
}
