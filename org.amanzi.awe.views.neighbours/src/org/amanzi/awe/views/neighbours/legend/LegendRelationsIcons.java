package org.amanzi.awe.views.neighbours.legend;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;


import org.amanzi.awe.views.neighbours.NeighboursPlugin;


public enum LegendRelationsIcons {
	OTHERS("others"),
	MORE_0_2("more_0_2"),
	MORE_1("more_1"),
	MORE_5("more_5"),
	MORE_15("more_15"),
	MORE_30("more_30"),
	MORE_50("more_50");
	
	
	
    private String fileName;
    private HashMap<Integer,BufferedImage> images = new HashMap<Integer,BufferedImage>();

    LegendRelationsIcons(String fileName) {
        this.fileName = fileName;
        for(int size: new Integer[]{16}){
            loadImage(size);
        }
    }
    private void loadImage(int size) {
        InputStream stream = NeighboursPlugin.getDefault().getClass().getClassLoader().getResourceAsStream("images/icons/" + fileName + "_" + size + ".png");
       
        try {
            images.put(size,ImageIO.read(stream));
        } catch (Exception e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * @return Returns the image.
     */
    public java.awt.Image getImage(int size) {
        if (!images.containsKey(size))
            loadImage(size);
        return images.get(size);
    }

}



