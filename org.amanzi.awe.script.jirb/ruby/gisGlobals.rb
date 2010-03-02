$catalog = Java::net.refractions.udig.catalog.CatalogPlugin.getDefault
$catalogs = Java::net.refractions.udig.catalog.CatalogPlugin.getDefault.getCatalogs
$projects = Java::net.refractions.udig.project.ui.ApplicationGIS.getProjects
$active_project = Java::net.refractions.udig.project.ui.ApplicationGIS.getActiveProject
$feature_source_class = Java::JavaClass.for_name("org.geotools.data.FeatureSource")
