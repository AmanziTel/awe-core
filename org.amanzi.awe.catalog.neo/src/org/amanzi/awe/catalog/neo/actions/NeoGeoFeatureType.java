package org.amanzi.awe.catalog.neo.actions;


	public enum NeoGeoFeatureType 
	{
	    /**
	     * Feature type point.
	     */
	    POINT("Point"),
	    /**
	     * Feature type Line.
	     */
	    LINE("LineString"),
	    /**
	     * Feature type polygon.
	     */
	    POLYGON("Polygon"),
	    /**
	     * Feature type MultiPoint.
	     */
	    MULTI_POINT("MultiPoint"),
	    /**
	     * Feature type MultiLineString.
	     */
	    MULTI_LINE_STRING("MultiLineString"),
	    /**
	     * Feature type MultiPolygon.
	     */
	    MULTI_POLYGON("MultiPolygon");
	
	    private String code;

	    /**
	     * GeoJsonFeatureType constructor.
	     * 
	     * @param code NeoGeoFeatureType code as String
	     */
	    private NeoGeoFeatureType( final String code ) {
	        this.code = code;
	    }

	    /**
	     * Returns GeoJsonStructure code as String.
	     * 
	     * @return GeoJsonStructure code as String
	     */
	    public String getCode() {
	        return code;
	    }

	    /**
	     * Converts NeoGeoFeatureType string code to {@link JSONGeoFeatureType} object.
	     * 
	     * @param code NeoGeoFeatureType code as String
	     * @return {@link NeoGeoFeatureType} object
	     */

	    public static NeoGeoFeatureType fromCode( final String code ) {
	        for( NeoGeoFeatureType type : NeoGeoFeatureType.class.getEnumConstants() ) {
	            if (type.getCode().equals(code)) {
	                return type;
	            }
	        }
	        throw new Error(NeoGeoFeatureType.class.getSimpleName() + " cannot convert " + code);
	    }
	}

	


