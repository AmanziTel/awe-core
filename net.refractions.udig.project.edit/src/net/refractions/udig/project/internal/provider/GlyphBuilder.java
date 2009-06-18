/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.internal.provider;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.ui.Drawing;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wms.request.GetLegendGraphicRequest;
import org.geotools.feature.Feature;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.styling.Style;

/**
 * Builds SWT images for to represent layers.
 * 
 * @author jeichar
 * @since 0.6.0
 */
public class GlyphBuilder {

    public static ImageDescriptor createImageDescriptor( final RenderedImage image ) {
        return new ImageDescriptor(){
            public ImageData getImageData() {
                return createImageData(image);
            }
        };
    }
    /** Create a buffered image that can be be coverted to SWTland later */
    public static BufferedImage createBufferedImage( int w, int h ) {
        return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
    }
    public static Image createSWTImage( RenderedImage image ) {
        // Rectangle size = new Rectangle(0, 0, image.getWidth(), image.getHeight());
        ImageData data = createImageData(image);

        return new org.eclipse.swt.graphics.Image(Display.getDefault(), data);
    }

    public final static int TRANSPARENT = 0x220000 | 0x2200 | 0x22;

    public static ImageData createImageData( RenderedImage image ) {
        ImageData swtdata = null;
        int width = image.getWidth();
        int height = image.getHeight();
        PaletteData palette;
        int depth;

        depth = 24;
        palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
        swtdata = new ImageData(width, height, depth, palette);
        swtdata.transparentPixel = TRANSPARENT;

        byte blueT = (byte) ((TRANSPARENT) & 0xFF);
        byte greenT = (byte) ((TRANSPARENT >> 8) & 0xFF);
        byte redT = (byte) ((TRANSPARENT >> 16) & 0xFF);
        // System.out.println("red="+redT+"blue"+blueT+"green"+greenT);
        // System.out.println("Transparent"+TRANSPARENT);

        // awtImage2.getRGB();
        Raster raster = image.getData();
        int[] awtdata = raster.getPixels(0, 0, width, height, new int[width * height * 3]);// raster.getNumBands()]);
        int step = swtdata.depth / 8;

        byte[] data = swtdata.data;
        int baseindex = 0;
        // System.out.println( "AWT size:" + awtdata.length );
        for( int y = 0; y < height; y++ ) {
            int idx = ((0 + y) * swtdata.bytesPerLine) + (0 * step);

            for( int x = 0; x < width; x++ ) {
                baseindex = (x + (y * width)) * 4;

                if (awtdata[baseindex + 3] == 0) {
                    data[idx++] = blueT;
                    data[idx++] = greenT;
                    data[idx++] = redT;
                } else {
                    data[idx++] = (byte) awtdata[baseindex];
                    data[idx++] = (byte) awtdata[baseindex + 1];
                    data[idx++] = (byte) awtdata[baseindex + 2];
                }
            }
        }
        return swtdata;
    }
    public ImageDescriptor createWMSGylph( Layer target ) {
        if (target.isType(WebMapServer.class))
            return null;
        try {
            WebMapServer wms = target.getResource(WebMapServer.class, null);
            org.geotools.data.ows.Layer layer = target.getResource(
                    org.geotools.data.ows.Layer.class, null);

            if (wms.getCapabilities().getRequest().getGetLegendGraphic() != null) {

                GetLegendGraphicRequest request = wms.createGetLegendGraphicRequest();
                request.setLayer(layer.getName());

                String desiredFormat = null;
                List formats = Arrays.asList(wms.getCapabilities().getRequest()
                        .getGetLegendGraphic().getFormatStrings());
                if (formats.contains("image/png")) { //$NON-NLS-1$
                    desiredFormat = "image/png"; //$NON-NLS-1$
                }
                if (desiredFormat == null && formats.contains("image/gif")) { //$NON-NLS-1$
                    desiredFormat = "image/gif"; //$NON-NLS-1$
                }
                if (desiredFormat == null) {
                    return null;
                }
                request.setFormat(desiredFormat);

                return ImageDescriptor.createFromURL(request.getFinalURL());
            }
        } catch (Exception e) {
            // darn
        }
        return null;
        /*
         * BufferedImage image = createBufferedImage( target, 16, 16); Graphics2D g2 = (Graphics2D)
         * image.getGraphics(); g2.setColor(Color.GREEN); g2.fillRect(1, 1, 14, 14);
         * g2.setColor(Color.BLACK); g2.drawRect(0, 0, 15, 15); return createImageDescriptor(image);
         */
    }
    private Feature sampleFeature( Layer layer ) {
        FeatureReader reader = null;
        try {
            reader = layer.getResource(FeatureSource.class, null).getFeatures().reader();
        } catch (Throwable ignore) {
            return null;
        }
        try {
            return reader.next();
        } catch (NoSuchElementException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (IllegalAttributeException e) {
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e1) {
                return null;
            }
        }
    }

    public Image createGlyph( Layer layer, Style styleObject ) {
        int width = 16;
        int height = 16;

        Image image = new Image(Display.getDefault(), width, height);
        Feature feature = sampleFeature(layer);
        ViewportGraphics graphics = Drawing.createGraphics(new GC(image), Display.getDefault(),
                new Dimension(width - 1, width - 1));
        graphics.clearRect(0, 0, width, height);
        // graphics.clearRect(0,0,16,16);
        AffineTransform transform = Drawing.worldToScreenTransform(feature.getBounds(),
                new Rectangle(1, 0, width - 1, width - 1));
        // Drawing.createGraphics(image.createGraphics());
        Drawing.create().drawFeature(graphics, feature, transform, styleObject);
        // return createSWTImage(image);
        return image;
        // StyleImpl imp = (StyleImpl) styleObject;
        // FeatureTypeStyle style = imp.getFeatureTypeStyles()[0];
        // Rule rule = style.getRules()[0];
        // Symbolizer symbolizer = rule.getSymbolizers()[0];
        // Feature feature = sampleFeature( layer );
        //
        // if (symbolizer instanceof LineSymbolizer) {
        // try {
        // LineSymbolizer line = (LineSymbolizer) symbolizer;
        // Stroke stroke = line.getStroke();
        // Color color = stroke.getColor(feature);
        //
        // BufferedImage image = createBufferedImage( layer, 16, 16);
        // Graphics2D g2 = (Graphics2D) image.getGraphics();
        // g2.setColor(color);
        // g2.drawArc(4, 4, 24, 24, 90, 90);
        // g2.drawLine(0, 2, 11, 11);
        // g2.drawLine(11, 11, 15, 8);
        //
        // g2.setColor(Color.BLACK);
        // g2.drawRect(0, 0, 15, 15);
        // return createImageDescriptor(image);
        // } catch (Exception e) {
        // BufferedImage image = createBufferedImage(16, 16);
        // Graphics2D g2 = (Graphics2D) image.getGraphics();
        // g2.setColor(Color.BLACK);
        // g2.drawArc(4, 4, 24, 24, 90, 90);
        // g2.drawLine(0, 2, 11, 11);
        // g2.drawLine(11, 11, 15, 8);
        //
        // g2.setColor(Color.BLACK);
        // g2.drawRect(0, 0, 15, 15);
        // return createImageDescriptor(image);
        // }
        // } else if (symbolizer instanceof PolygonSymbolizer) {
        // try {
        // PolygonSymbolizer poly = (PolygonSymbolizer) symbolizer;
        // Stroke stroke = poly.getStroke();
        // Color color = stroke.getColor(feature);
        // Fill fill = poly.getFill();
        // Paint fillColor = (Paint) fill.getColor().getValue(feature);
        //
        // BufferedImage image = createBufferedImage( layer, 16, 16);
        // Graphics2D g2 = (Graphics2D) image.getGraphics();
        // g2.setPaint(fillColor);
        // g2.fillArc(4, 4, 24, 24, 90, 90);
        // g2.setPaint(color);
        // g2.drawArc(4, 4, 24, 24, 90, 90);
        //
        // g2.setColor(Color.BLACK);
        // g2.drawRect(0, 0, 16, 16);
        // return createImageDescriptor(image);
        // } catch (Exception e) {
        // BufferedImage image = createBufferedImage( layer, 16, 16);
        // Graphics2D g2 = (Graphics2D) image.getGraphics();
        //
        // g2.setColor(Color.BLACK);
        // g2.drawRect(2, 2, 11, 11);
        // g2.setColor(Color.BLUE);
        // g2.fillRect(3, 3, 10, 10);
        //
        // g2.setColor(Color.BLACK);
        // g2.drawRect(0, 0, 15, 15);
        // return createImageDescriptor(image);
        // }
        // }
        // return null;
    }
    public Object createGlyph( Layer layer ) {
        try {
            ImageDescriptor glyph;
            if (layer.isType(WebMapServer.class)) {
                glyph = createWMSGylph(layer);
                if (glyph != null)
                    return glyph;
            }
            /*
             * // This so does not work right now if (layer.getStyle() != null) { glyph =
             * createGylph( layer, layer.getStyle() ); if( glyph != null ) return glyph; }
             */
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return ProjectEditPlugin.INSTANCE.getImage("full/obj16/Layer"); //$NON-NLS-1$
    }

}
