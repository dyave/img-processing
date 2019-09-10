package exercices;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.gui.binary.VisualizeBinaryData;
import java.awt.image.BufferedImage;

public class SegExs2GearTeeth {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(">>> Error with arguments");
            System.exit(1);
        }
        BufferedImage image = UtilImageIO.loadImage(args[0]);
        if (image == null) { 
            System.out.println(">>> Image not found.");
            System.exit(1);
        }
        GrayU8 original = ConvertBufferedImage.convertFromSingle(image, null, GrayU8.class);
        int count = Segmentation.countTeeths(original);
        System.out.println("Gear teeths: " + count);
    }
}
