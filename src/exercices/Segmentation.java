package exercices;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.GrayU16;
import boofcv.struct.ConnectRule;
import boofcv.core.image.ConvertImage;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.image.ShowImages;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.misc.PixelMath;
import java.awt.image.BufferedImage;
import java.util.*;

public class Segmentation {
    public static GrayU8 closing(GrayU8 image, int size) {
        return BinaryImageOps.erode8(BinaryImageOps.dilate8(image,size,null),size,null);
    }

    public static GrayU8 opening(GrayU8 image, int size) {
        return BinaryImageOps.dilate8(BinaryImageOps.erode8(image,size,null),size,null);
    }

    public static int countGrains(GrayU8 original) {
        int width = original.width;
        int height = original.height;

        GrayU8 thresholded = new GrayU8(width, height);
        GrayU8 aux = new GrayU8(width, height);
        
        ThresholdImageOps.threshold(original,thresholded,145,false);

        GrayU8 aux1 = closing(opening(thresholded,1),1);
        GrayU8 aux2 = BinaryImageOps.dilate4(aux1,5,null);
        GrayU8 aux3 = opening(aux2,1);
        GrayU8 aux4 = BinaryImageOps.dilate4(aux3,6,null);
        aux = BinaryImageOps.invert(aux4,null); //Para coffee_markers.png, pasar aqui thresholded como parametro directamente.

        List<Contour> contours = BinaryImageOps.contour(aux, ConnectRule.EIGHT, null); 

        ////////
		// colors of contours
		int colorExternal = 0xFFFFFF;
		int colorInternal = 0xFF2020;
		
		BufferedImage visualOriginal = VisualizeBinaryData.renderBinary(original, false, null);
		BufferedImage visualThresholded = VisualizeBinaryData.renderBinary(thresholded, false, null);
		BufferedImage visualAux1 = VisualizeBinaryData.renderBinary(aux1, false, null);
		BufferedImage visualAux2 = VisualizeBinaryData.renderBinary(aux2, false, null);
		BufferedImage visualAux3 = VisualizeBinaryData.renderBinary(aux3, false, null);
		BufferedImage visualAux4 = VisualizeBinaryData.renderBinary(aux4, false, null);
		BufferedImage visualAux = VisualizeBinaryData.renderBinary(aux, false, null);
		BufferedImage visualContour = VisualizeBinaryData.renderContours(contours, colorExternal, colorInternal,
				width, height, null);

		ListDisplayPanel panel = new ListDisplayPanel();
		panel.addImage(visualOriginal, "Original");
		panel.addImage(visualThresholded, "Thresholded");
		panel.addImage(visualAux1, "Aux1");
		panel.addImage(visualAux2, "Aux2");
		panel.addImage(visualAux3, "Aux3");
		panel.addImage(visualAux4, "Aux4");
		panel.addImage(visualAux, "Aux");
		panel.addImage(visualContour, "Contours");
		ShowImages.showWindow(panel,"Binary Operations",true);
		//////////
        
        return contours.size();
    }

    public static int countTeeths(GrayU8 original) {
        int width = original.width;
        int height = original.height;

        GrayU8 thresholded = new GrayU8(width, height);
        GrayU8 aux = new GrayU8(width, height);
        GrayU16 subtracted = new GrayU16(width, height);
        
        ThresholdImageOps.threshold(original,thresholded,100,false);

        aux = thresholded;

        aux = closing(aux,5);
        aux = BinaryImageOps.erode4(aux,1,null);
        aux = BinaryImageOps.dilate4(aux,2,null);

        PixelMath.subtract(aux, thresholded, subtracted);

        ConvertImage.convert(subtracted,aux);

        aux = BinaryImageOps.erode4(aux,1,null);

        aux = opening(aux,1);

        List<Contour> contours = BinaryImageOps.contour(aux, ConnectRule.EIGHT, null); 
        
        return contours.size();
    }
}