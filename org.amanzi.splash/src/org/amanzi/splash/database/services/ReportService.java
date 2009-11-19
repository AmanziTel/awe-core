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

package org.amanzi.splash.database.services;

import java.util.ArrayList;
import java.util.Iterator;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.CellID;
import org.amanzi.neo.core.database.nodes.CellNode;
import org.amanzi.neo.core.database.nodes.ColumnNode;
import org.amanzi.neo.core.database.nodes.ReportNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.database.nodes.TextNode;
import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.splash.report.model.Chart;
import org.amanzi.splash.report.model.Report;
import org.amanzi.splash.report.model.ReportText;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

/**
 * Service for reports
 * 
 * @author Pechko E.
 * @since 1.0.0
 */
public class ReportService {
    /*
     * NeoService Provider
     */
    private NeoServiceProvider provider;

    /*
     * NeoService
     */
    protected NeoService neoService;

    /*
     * Project Service
     */
    protected AweProjectService projectService;

    /**
     * Constructor
     */
    public ReportService() {
        provider = NeoServiceProvider.getProvider();
        neoService = provider.getService();
        projectService = NeoCorePlugin.getDefault().getProjectService();
    }

    /**
     * Updates report
     * 
     * @param root ruby project node
     * @param name old report name
     * @param report modified report
     */
    public void updateReport(RubyProjectNode root, String name, Report report) {
        Transaction tx = neoService.beginTx();
        try {
            ReportNode reportNode = null;
            if (name == null) {
                reportNode = projectService.createReport(root, report.getName());
            } else {
                reportNode = projectService.findOrCreateReport(root, name);

            }
            reportNode.setReportName(report.getName());
            reportNode.setReportDate(report.getDate().toString());
            reportNode.setReportAuthor(report.getAuthor());
            tx.success();
        }  catch (Exception e) {
            e.printStackTrace();
            tx.failure();
        }finally {
            tx.finish();
        }
    }

    /**
     * Method to obtain cell range
     * 
     * @param root ruby project node
     * @param sheet sheet name
     * @param range cell range represented as pair of its first and last cellID
     * @return list of nodes
     */
    public ArrayList<CellNode> getCellRange(RubyProjectNode root, String sheet, Pair<CellID, CellID> range) {
        Transaction tx = neoService.beginTx();
        ArrayList<CellNode> result = new ArrayList<CellNode>();
//        System.out.println("Range: " + range.l() + ".." + range.r());
        try {
            SpreadsheetNode spreadsheetNode = projectService.findSpreadsheet(root, sheet);
            Iterator<ColumnNode> columns = spreadsheetNode.getColumns(range.l().getColumnIndex(), range.r().getColumnIndex());
            while (columns.hasNext()) {
                ColumnNode column = columns.next();
                Iterator<CellNode> cells = column.getCells(range.l().getRowName(), range.r().getRowName());
                while (cells.hasNext()) {
                    result.add(cells.next());
                }
            }
            tx.success();
        } finally {
            tx.finish();
        }
        return result;
    }

    public ArrayList<Node> getNodes(RubyProjectNode rootNode, Long[] ids) {
        final ArrayList<Node> nodes = new ArrayList<Node>();
        //TODO handle NotFoundException 
        for (Long id:ids){
            nodes.add(neoService.getNodeById(id));
        }
        return nodes;
    }
}
