package org.amanzi.awe.views.drive.views;

import org.amanzi.awe.awe.views.view.provider.NetworkTreeContentProvider;
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.core.service.NeoServiceProvider;

/**
 * <p>
 * Content provider for Drive tree
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class DriveTreeContentProvider extends NetworkTreeContentProvider {
    /**
     * Constructor
     * 
     * @param neoProvider neoServiceProvider for this ContentProvider
     */
    public DriveTreeContentProvider(NeoServiceProvider neoProvider) {
        super(neoProvider);
    }

    @Override
    protected Root getRoot() {
        return new DriveRoot(neoServiceProvider);
    }
}