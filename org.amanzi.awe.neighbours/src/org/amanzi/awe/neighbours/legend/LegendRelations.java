package org.amanzi.awe.neighbours.legend;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.amanzi.neo.core.utils.DriveEvents;
import org.amanzi.neo.services.ui.IconManager;
import org.amanzi.neo.services.ui.NeoServicesUiPlugin;
import org.amanzi.neo.services.ui.IconManager.EventIcons;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.graphics.ViewportGraphics;

public class LegendRelations implements MapGraphic {



	private int verticalMargin; // distance between border and icons/text
	private int horizontalMargin; // distance between border and icons/text
	private int verticalSpacing; // distance between layers
	private int horizontalSpacing; // space between image and text
	private Color foregroundColour;
	private Color backgroundColour;
	private int indentSize;
	private int imageWidth;
	private int imageHeight; // size of image
	private int maxHeight;
	private int maxWidth;

	public void draw(MapGraphicContext context) {
		this.backgroundColour = Color.GRAY;// legendStyle.backgroundColour;
		this.foregroundColour = Color.BLACK;// legendStyle.foregroundColour;
		this.horizontalMargin = 5;// legendStyle.horizontalMargin;
		this.verticalMargin = 5;// legendStyle.verticalMargin;
		this.horizontalSpacing = 2;// legendStyle.horizontalSpacing;
		this.verticalSpacing = 2;// legendStyle.verticalSpacing;
		this.indentSize = 0;// legendStyle.indentSize;
		this.imageHeight = 16;// legendStyle.imageHeight;
		this.imageWidth = 16;// legendStyle.imageWidth;

		this.maxHeight = 0;// locationStyle.width;
		this.maxWidth = 0;// locationStyle.height;

		final ViewportGraphics graphics = context.getGraphics();

		int longestRow = 0; // used to calculate the width of the graphic
		final int[] numberOfEntries = new int[1]; // total number of entries to draw
		numberOfEntries[0] = 0;
		for (LegendElements element : LegendElements.values()) {

			String text = element.getDescription();
			Rectangle2D bounds = graphics.getStringBounds(text);
			int length = indentSize + imageWidth + horizontalSpacing + (int)bounds.getWidth();

			if (length > longestRow) {
				longestRow = length;
			}
			numberOfEntries[0]++;
		}

		if (numberOfEntries[0] == 0) {
			// nothing to draw!
			return;
		}

		final int rowHeight = Math.max(imageHeight, graphics.getFontHeight()); // space allocated to
		// each layer

		// total width of the graphic
		int width = longestRow + horizontalMargin * 2;
		if (maxWidth > 0) {
			width = Math.min(width, maxWidth);
		}
		// total height of the graphic
		int height = rowHeight * numberOfEntries[0] + verticalMargin * 2 + verticalSpacing * (numberOfEntries[0] - 1);
		if (maxHeight > 0) {
			height = Math.min(height, maxHeight);
		}

		// we want to grow and shrink as we desire so we'll use a different
		// rectangle than the one on the blackboard.
		Rectangle locationStyle = new Rectangle();
		Dimension displaySize = context.getMapDisplay().getDisplaySize();
		locationStyle.x = displaySize.width - width - 5;
		locationStyle.y = displaySize.height - height - 5;
		locationStyle.width = width;
		locationStyle.height = height;

		graphics.setClip(new Rectangle(locationStyle.x, locationStyle.y, locationStyle.width + 1, locationStyle.height + 1));

		/*
		 * Draw the box containing the layers/icons
		 */
		drawOutline(graphics, locationStyle);

		/*
		 * Draw the layer names/icons
		 */
		final int[] rowsDrawn = new int[1];
		rowsDrawn[0] = 0;
		final int[] x = new int[1];
		x[0] = locationStyle.x + horizontalMargin;
		final int[] y = new int[1];
		y[0] = locationStyle.y + verticalMargin;

		for (LegendElements element : LegendElements.values()) {
			final BufferedImage awtIcon = (BufferedImage)element
			.getElementIcon()
			.getImage(16);
			final String layerName = element.getDescription();

			PlatformGIS.syncInDisplayThread(new Runnable() {
				public void run() {

					drawRow(graphics, x[0], y[0], awtIcon, layerName, false);

					y[0] += rowHeight;
					if ((rowsDrawn[0] + 1) < numberOfEntries[0]) {
						y[0] += verticalSpacing;
					}
					rowsDrawn[0]++;
				}
			});
		}
	}

	private void drawRow(ViewportGraphics graphics, int x, int y, RenderedImage icon, String text, boolean indent) {

		Rectangle2D stringBounds = graphics.getStringBounds(text);

		/*
		 * Center the smaller item (text or icon) according to the taller one.
		 */
		int textVerticalOffset = 0;
		int iconVerticalOffset = 0;
		if (imageHeight == (int)stringBounds.getHeight()) {
			// items are the same height; do nothing.
		} else if (imageHeight > (int)stringBounds.getHeight()) {
			int difference = imageHeight - (int)stringBounds.getHeight();
			textVerticalOffset = difference / 2;
		} else if (imageHeight < (int)stringBounds.getHeight()) {
			int difference = (int)stringBounds.getHeight() - imageHeight;
			iconVerticalOffset = difference / 2;
		}

		if (indent) {
			x += indentSize;
		}

		if (icon != null) {
			graphics.drawImage(icon, x, y + iconVerticalOffset);

			x += imageWidth;
		}

		if (text != null && text.length() != 0) {
			graphics.drawString(text, x + horizontalMargin, y + graphics.getFontAscent() + textVerticalOffset,
					ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_LEFT);
		}
	}

	private void drawOutline(ViewportGraphics graphics, Rectangle locationStyle) {
		Rectangle outline = new Rectangle(locationStyle.x, locationStyle.y, locationStyle.width, locationStyle.height);

		graphics.setColor(backgroundColour);
		graphics.fill(outline);

		graphics.setColor(foregroundColour);
		graphics.setBackground(backgroundColour);
		graphics.draw(outline);
	}

	
}



