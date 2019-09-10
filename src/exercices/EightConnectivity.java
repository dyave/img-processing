package exercices;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageAccessException;
import java.util.*;

public class EightConnectivity {
    public ArrayList<Tuple<Integer,Integer>> allNeighborPoints(GrayU8 image, int i, int j, int size) {
        ArrayList<Tuple<Integer,Integer>> list = new ArrayList<Tuple<Integer,Integer>>(8);

        for (int neighbor = 1; neighbor <= size; neighbor++) {
            for (int n = i - neighbor; n <= i + neighbor; n++) {
                if (image.isInBounds(n,j + neighbor))
                    { list.add(new Tuple<Integer,Integer>(n,j + neighbor)); }
            }

            for (int n = i - neighbor; n <= i + neighbor; n++) {
                if (image.isInBounds(n,j - neighbor))
                    { list.add(new Tuple<Integer,Integer>(n,j - neighbor)); }
            }

            for (int n = j - neighbor + 1; n < j + neighbor; n++) {
                if (image.isInBounds(i - neighbor,n))
                    { list.add(new Tuple<Integer,Integer>(i - neighbor,n)); }
            }

            for (int n = j - neighbor + 1; n < j + neighbor; n++) {
                if (image.isInBounds(i + neighbor,n))
                    { list.add(new Tuple<Integer,Integer>(i + neighbor,n)); }
            }
        }
        return list;
    }
}