/*
 * Copyright (c). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



/**
 * Note: using and adapting some things from
 * http://boofcv.org/index.php?title=Tutorial_Images
 * boofcv.examples.segmentation.ExampleBinaryOps.java
 * boofcv.examples.segmentation.ExampleThresholding
 */

package exercices;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.GrayS32;
import boofcv.struct.image.GrayF32;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.image.ShowImages;
import boofcv.io.UtilIO;
//import boofcv.io.image.ConvertBufferedImage;
//import boofcv.io.image.UtilImageIO;
import boofcv.struct.ConnectRule;
//import boofcv.struct.image.GrayF32;
//import boofcv.struct.image.GrayS32;
//import boofcv.struct.image.GrayU8;


public class Task1 {

	public static void main( String args[] ) throws FileNotFoundException, UnsupportedEncodingException {
		// ========== First Exercises ========== //
		//FourCorners_Ex1a();
		
		//Threshold_Ex2a(100);
		
		//Compare_Ex2b("./sampleFiles/2b/cam_74.pgm", "./sampleFiles/2b/exercise2a_out2.png", "./sampleFiles/2b/result.txt");
		
		//SupInf_Exercise2d();
		
		//TestErosionDilation_Ex3ab();
		
		//TestOpeningEx4a();
		//TestClosingEx4b();
		
		//IdempotenceOfOpening_Ex5a(3, BuildGrayU8("./sampleFiles/5ab/cam_74.pgm"));
		//IdempotenceOfClosing_Ex5b(3, BuildGrayU8("./sampleFiles/5ab/cam_74.pgm"));
		
		//TestAlternatedClosingOpening_Ex6ab();
		
		//IdempotenceOfClosingOpening_Ex7a(3, BuildGrayU8("./sampleFiles/7ab/cam_74.pgm"));
		//IdempotenceOfOpeningClosing_Ex7b(3, BuildGrayU8("./sampleFiles/7ab/cam_74.pgm"));
		
		//NoiseRemoval_Ex8a();
		
		
		// ========== Segmentation ========== //
		
		//BoundaryExtraction_S1(1, "./sampleFiles/s1/hitchcock.png");
		//GrassFire(1, "./sampleFiles/s2/particles.png");
		
		System.out.println("GearTeeth and CoffeeGrains can both be executed apart. Run classes SegExs2GearTeeth and SegExs2CoffeeGrains.");
		
		// ========== Exercise 11 ========== //
		//FlatZone_Ex11a(0, 0, "./sampleFiles/Exercises_11a/gran01_64.pgm");
		FlatZone_Ex11a(57, 36, "./sampleFiles/Exercises_11a/immed_gray_inv_20051218_frgr4.pgm");
	}
	
	public static void FlatZone_Ex11a(int xpixel, int ypixel, String fullPath) {
		int iparameter = 1;
        int x = xpixel;
        int y = ypixel;
        int conectivity = 8;
        int label = 255;

        String file_output = BuildOutputPath(iparameter, fullPath);

        GrayU8 img_gray8_ori = BuildGrayU8(fullPath);

        GrayU8 img_gray8_aux = img_gray8_ori.clone();

        img_gray8_aux = flatzone(img_gray8_ori, x, y, label, new EightConnectivity());
        
        UtilImageIO.saveImage(img_gray8_aux, file_output);
        System.out.println("Output image file: " + file_output);
	}
	
    public static GrayU8 flatzone(GrayU8 original, int x, int y, int label, EightConnectivity connected) {
        int width = original.width;
        int height = original.height;

        GrayU8 aux = new GrayU8(width, height);

        PriorityQueue<Tuple<Integer,Integer>> queue = new PriorityQueue<Tuple<Integer,Integer>>();
        PriorityQueue<Tuple<Integer,Integer>> visited = new PriorityQueue<Tuple<Integer,Integer>>();

        Tuple<Integer,Integer> current = new Tuple<Integer,Integer>(x,y);

        queue.add(current);

        while (!queue.isEmpty()) {
            current = (Tuple<Integer,Integer>) queue.poll();
            
            visited.add(new Tuple<Integer,Integer>(current.left,current.right));
            aux.set(current.left, current.right, label);

            for (Tuple<Integer,Integer> point : connected.allNeighborPoints(original, current.left, current.right, 1)) {
                if (original.get(point.left,point.right) == original.get(current.left,current.right)
                    && !visited.contains(point))
                    { queue.add(point); }
            }
        }

        return aux;
    }
	
	public static void GrassFire(int iparameter, String fullPath) {
		GrayU8 img = BuildGrayU8(fullPath);
		GrayU8 imgx = new GrayU8(img.width, img.height);
		int color = 10;
		
		for (int j = 0; j < img.height; j++) {
			for (int i = 0; i < img.width; i++) {
				if (img.get(i, j) == 255 && imgx.get(i, j) == 0) {
					color += 7;
					imgx.set(i, j, color);
					CheckNeighbors(iparameter, img, imgx, i, j, color);
				}
			}
		}
		UtilImageIO.saveImage(imgx, BuildOutputPath(iparameter, fullPath));
	}
	
	public static void CheckNeighbors(int iparameter, GrayU8 img, GrayU8 imgx, int i, int j, int color) {
		//UtilImageIO.saveImage(imgx, BuildOutputPath(++iparameter, "./sampleFiles/s2/out1.png"));
		for (int y = j - iparameter; y <= j + iparameter; y++) {
			for (int x = i - iparameter; x <= i + iparameter; x++) {
				if ((y >= 0 && y < img.height) && (x >= 0 && x < img.width) && !(x == i && y == j)) {
					//System.out.format("x,y =%d,%d", x, y);
					if (img.get(x, y) == 255 && imgx.get(x, y) == 0) { //Cuando el pixel es foreground. Aqui se asume que es imagen binaria.
						imgx.set(x, y, color);
						CheckNeighbors(iparameter, img, imgx, x, y, color);
					}
				}
			}
		}
	}
	
	public static void BoundaryExtraction_S1(int iparameter, String fullPath) {
		GrayU8 img = BuildGrayU8(fullPath);
		GrayU8 erosioned = Erosion(1, img);
		
		GrayU8 bundary = Subtraction(img, erosioned);
		UtilImageIO.saveImage(bundary, BuildOutputPath(iparameter, fullPath));
	}
	
	public static GrayU8 Subtraction(GrayU8 img1, GrayU8 img2) {
		GrayU8 imgx = new GrayU8(img1.width, img1.height);
		
		for (int j = 0; j < img1.height; j++) {
			for (int i = 0; i < img1.width; i++) {
				int subtraction = img1.get(i, j) - img2.get(i, j);
				imgx.set(i, j, subtraction < 0? 0 : subtraction);
			}
		}
		return imgx;
	}
	
	// {{{ Task 01 - Exercises 0X }}}
	
	public static void NoiseRemoval_Ex8a() {
		String fullPath = "./sampleFiles/8a/isn_256.pgm";
		GrayU8 img = BuildGrayU8(fullPath);
		int iparameter = 1;
		
		GrayU8 filter1 = Opening(iparameter, img);
		GrayU8 filter2 = Closing(iparameter, img);
		GrayU8 filter3 = ClosingOpening(iparameter, img);
		GrayU8 filter4 = OpeningClosing(iparameter, img);
		
		UtilImageIO.saveImage(filter1, BuildOutputPath(iparameter, fullPath, "opening"));
		UtilImageIO.saveImage(filter2, BuildOutputPath(iparameter, fullPath, "closing"));
		UtilImageIO.saveImage(filter3, BuildOutputPath(iparameter, fullPath, "closingOpening"));
		UtilImageIO.saveImage(filter4, BuildOutputPath(iparameter, fullPath, "openingClosing"));
		System.out.println("filter3(ClosingOpening) and filter4(OpeningClosing) are the best filters.");
	}
	
	public static void IdempotenceOfClosingOpening_Ex7a(int iparameter, GrayU8 img) {
		GrayU8 co1 = ClosingOpening(iparameter, img);
		GrayU8 co2 = ClosingOpening(iparameter, co1);
		boolean areIdentical = Compare(co1, co2);
		System.out.println("Idempotence of ClosingOpening");
		System.out.println("(ClosingOpening1 == ClosingOpening2)? " + areIdentical);
	}
	
	public static void IdempotenceOfOpeningClosing_Ex7b(int iparameter, GrayU8 img) {
		GrayU8 oc1 = OpeningClosing(iparameter, img);
		GrayU8 oc2 = OpeningClosing(iparameter, oc1);
		boolean areIdentical = Compare(oc1, oc2);
		System.out.println("Idempotence of OpeningClosing");
		System.out.println("(OpeningClosing1 == OpeningClosing2)? " + areIdentical);
	}
	
	public static void TestAlternatedClosingOpening_Ex6ab() {
		//iparameter = 2 | 4;
		GrayU8 img = BuildGrayU8("./sampleFiles/6ab/immed_gray_inv.pgm");

		System.out.println("=== ClosingOpening Test ===");
		
		GrayU8 clo2ope2 = ClosingOpening(2, img);
		GrayU8 expectedCO2 = BuildGrayU8("./sampleFiles/6ab/immed_gray_inv_20051123_clo2ope2.pgm");
		boolean areCO2Identical = Compare(clo2ope2, expectedCO2);
		System.out.println("Closing2 Opening2");
		System.out.println("(obtainedResult == expectedResult)? " + areCO2Identical);
		
		GrayU8 clo4ope4 = ClosingOpening(4, img);
		GrayU8 expectedCO4 = BuildGrayU8("./sampleFiles/6ab/immed_gray_inv_20051123_clo4ope4.pgm");
		UtilImageIO.saveImage(expectedCO4, "./sampleFiles/asdf.pgm");
		boolean areCO4Identical = Compare(clo4ope4, expectedCO4);
		System.out.println("Closing4 Opening4");
		System.out.println("(obtainedResult == expectedResult)? " + areCO4Identical);
		
		System.out.println("=== OpeningClosing Test ===");

		GrayU8 ope2clo2 = OpeningClosing(2, img);
		GrayU8 expectedOC2 = BuildGrayU8("./sampleFiles/6ab/immed_gray_inv_20051123_ope2clo2.pgm");
		boolean areOC2Identical = Compare(ope2clo2, expectedOC2);
		System.out.println("Opening2 Closing2");
		System.out.println("(obtainedResult == expectedResult)? " + areOC2Identical);
		
		GrayU8 ope4clo4 = OpeningClosing(4, img);
		GrayU8 expectedOC4 = BuildGrayU8("./sampleFiles/6ab/immed_gray_inv_20051123_ope4clo4.pgm");
		boolean areOC4Identical = Compare(ope4clo4, expectedOC4);
		System.out.println("Opening4 Closing4");
		System.out.println("(obtainedResult == expectedResult)? " + areOC4Identical);
	}
	
	public static GrayU8 ClosingOpening(int iparameter, GrayU8 img) {
		return Closing(iparameter, Opening(iparameter, img));
	}
	
	public static GrayU8 OpeningClosing(int iparameter, GrayU8 img) {
		return Opening(iparameter, Closing(iparameter, img));
	}
	
	public static void IdempotenceOfOpening_Ex5a(int iparameter, GrayU8 img) {
		GrayU8 ope1 = Opening(iparameter, img);
		GrayU8 ope2 = Opening(iparameter, ope1);
		boolean areIdentical = Compare(ope1, ope2);
		System.out.println("Idempotence of Opening");
		System.out.println("(opening1 == opening2)? " + areIdentical);
	}
	
	public static void IdempotenceOfClosing_Ex5b(int iparameter, GrayU8 img) {
		GrayU8 clo1 = Closing(iparameter, img);
		GrayU8 clo2 = Closing(iparameter, clo1);
		boolean areIdentical = Compare(clo1, clo2);
		System.out.println("Idempotence of Closing");
		System.out.println("(closing1 == closing2)? " + areIdentical);
	}
	
	public static void TestOpeningEx4a() {
		int iparameter = 2;
		String fullPath = "./sampleFiles/4ab/immed_gray_inv.pgm";
		String openingOutput = BuildOutputPath(iparameter, fullPath, "opening");
		GrayU8 openingResult = Opening(iparameter, BuildGrayU8(fullPath));
		UtilImageIO.saveImage(openingResult, openingOutput);
		String expectedOpening = "./sampleFiles/4ab/immed_gray_inv_20051123_ope2.pgm";
		System.out.println("Opening comparison:" + Compare(openingResult, BuildGrayU8(expectedOpening)));
	}
	
	public static void TestClosingEx4b() {
		int iparameter = 2;
		String fullPath = "./sampleFiles/4ab/immed_gray_inv.pgm";
		String closingOutput = BuildOutputPath(iparameter, fullPath, "closing");
		GrayU8 closingResult = Closing(iparameter, BuildGrayU8(fullPath));
		UtilImageIO.saveImage(closingResult, closingOutput);
		String expectedClosing = "./sampleFiles/4ab/immed_gray_inv_20051123_clo2.pgm";
		System.out.println("Opening comparison:" + Compare(closingResult, BuildGrayU8(expectedClosing)));
	}
	
	public static GrayU8 Opening(int iparameter, GrayU8 img) {
		return Dilation(iparameter, Erosion(iparameter, img));
	}
	
	public static GrayU8 Closing(int iparamater, GrayU8 img) {
		return Erosion(iparamater, Dilation(iparamater, img));
	}

	public static void TestErosionDilation_Ex3ab() {
		ErosionDilation_Ex3ab(2, "./sampleFiles/3ab/immed_gray_inv.pgm");
		
		int iparameter = 2;
		String fullPath = "./sampleFiles/3ab/immed_gray_inv.pgm";
		String erosionOutput = BuildOutputPath(iparameter, fullPath, "erosion");
		String dilationOutput = BuildOutputPath(iparameter, fullPath, "dilation");
		GrayU8 img1 = BuildGrayU8(fullPath);
		UtilImageIO.saveImage(Erosion(iparameter, BuildGrayU8(fullPath)), erosionOutput);
		UtilImageIO.saveImage(Dilation(iparameter, img1), dilationOutput);
		
		String basicDilation = "./sampleFiles/3ab/cam_74_dilx2.pgm";
		String recursiveDilation = "./sampleFiles/3ab/cam_74_dil2.pgm";
		System.out.println("(Dilation == RecursiveDilation)?");
		System.out.println(Compare(BuildGrayU8(basicDilation), BuildGrayU8(recursiveDilation)));
	}
	
	public static String BuildOutputPath(int iparameter, String fullPath) {
		//Return the output path on the same current folder.
		return BuildOutputPath(iparameter, fullPath, "");
	}
	
	public static String BuildOutputPath(int iparameter, String fullPath, String purpose) {
		String[] pathParts = fullPath.split("/");
		String pathOnly = "";
		for (int s = 0; s <= pathParts.length-2; s++) {
			pathOnly += pathParts[s] + "/";
		}
		String imgFile = pathParts[pathParts.length-1];
		String[] imgNameAndFormat = imgFile.split("\\.");
		String imgName = imgNameAndFormat[0];
		String imgFormat = "." + imgNameAndFormat[1];
		
		if (purpose == "erosion") {
			return pathOnly + imgName + "_ero" + iparameter + imgFormat;
		} else if (purpose == "dilation") {
			return pathOnly + imgName + "_dil" + iparameter + imgFormat;
		} else if (purpose == "opening") {
			return pathOnly + imgName + "_ope" + iparameter + imgFormat;
		} else if (purpose == "closing") {
			return pathOnly + imgName + "_clo" + iparameter + imgFormat;
		} else if (purpose == "closingOpening") {
			return pathOnly + imgName + "_cloope" + iparameter + imgFormat;
		} else if (purpose == "openingClosing") {
			return pathOnly + imgName + "_opeclo" + iparameter + imgFormat;
		} else {
			return pathOnly + imgName + "_out" + iparameter + imgFormat;
		}
	}
	
	public static GrayU8 BuildGrayU8(String filein1) {
		BufferedImage buffimage1 = UtilImageIO.loadImage(filein1);
		GrayU8 img1 = ConvertBufferedImage.convertFromSingle(buffimage1, null, GrayU8.class);
		return img1;
	}
	
	public static GrayU8 Dilation(int iparameter, GrayU8 img1) {
		GrayU8 img3 = new GrayU8(img1.width, img1.height);
		if (iparameter == 1) {
			for (int j = 0; j < img1.height; j++) {
				for (int i = 0; i < img1.width; i++) {
					int maxval = img1.get(i, j);
					for (int y = j - iparameter; y <= j + iparameter; y++) {
						for (int x = i - iparameter; x <= i + iparameter; x++) {
							if ((y >= 0 && y < img1.height) && (x >= 0 && x < img1.width)) {
								maxval = img1.get(x, y) > maxval? img1.get(x, y) : maxval;
							}
						}
					}
					img3.set(i, j, maxval);
				}
			}
			return img3;
		} else {
			return Dilation(1, Dilation(iparameter - 1, img1));
		}
	}
	
	public static GrayU8 Erosion(int iparameter, GrayU8 img1) {
		GrayU8 img2 = new GrayU8(img1.width, img1.height);
		if (iparameter == 1) {
			for (int j = 0; j < img1.height; j++) {
				for (int i = 0; i < img1.width; i++) {
					int minval = img1.get(i, j);
					for (int y = j - iparameter; y <= j + iparameter; y++) {
						for (int x = i - iparameter; x <= i + iparameter; x++) {
							if ((y >= 0 && y < img1.height) && (x >= 0 && x < img1.width)) {
								minval = img1.get(x, y) < minval? img1.get(x, y) : minval;
							}
						}
					}
					img2.set(i, j, minval);
				}
			}
			return img2;
		} else {
			return Erosion(1, Erosion(iparameter - 1, img1));
		}
	}
	
	public static void ErosionDilation_Ex3ab(int iparameter, String fullPath) {
		String[] pathParts = fullPath.split("/");
		String pathOnly = "";
		for (int s = 0; s <= pathParts.length-2; s++) {
			pathOnly += pathParts[s] + "/";
		}
		String imgFile = pathParts[pathParts.length-1];
		String[] imgNameAndFormat = imgFile.split("\\.");
		String imgName = imgNameAndFormat[0];
		String imgFormat = "." + imgNameAndFormat[1];
						
		String filein1 = pathOnly + imgName + imgFormat;
		String fileout1 = pathOnly + imgName + "_erox" + iparameter + imgFormat;
		String fileout2 = pathOnly + imgName + "_dilx" + iparameter + imgFormat;
		
		BufferedImage image1 = UtilImageIO.loadImage(filein1);
		
		GrayU8 img1 = ConvertBufferedImage.convertFromSingle(image1, null, GrayU8.class);
		GrayU8 img2 = new GrayU8(img1.width, img1.height);
		GrayU8 img3 = new GrayU8(img1.width, img1.height);
		System.out.println("img1: " + img1.width + " x " + img1.height);
		
		for (int j = 0; j < img1.height; j++) {
			for (int i = 0; i < img1.width; i++) {
				int minval = img1.get(i, j);
				int maxval = img1.get(i, j);
				for (int y = j - iparameter; y <= j + iparameter; y++) {
					for (int x = i - iparameter; x <= i + iparameter; x++) {
						if ((y >= 0 && y < img1.height) && (x >= 0 && x < img1.width)) {
							minval = img1.get(x, y) < minval? img1.get(x, y) : minval;
							maxval = img1.get(x, y) > maxval? img1.get(x, y) : maxval;
						}
					}
				}
				img2.set(i, j, minval);
				img3.set(i, j, maxval);
			}
		}
		UtilImageIO.saveImage(img2, fileout1);
		UtilImageIO.saveImage(img3, fileout2);
	}
	
	public static void SupInf_Exercise2d() {
		String filein1 = "./sampleFiles/2d/cam_74.pgm";
		String filein2 = "./sampleFiles/2d/cam_74_threshold100.pgm";
		String fileout1 = "./sampleFiles/2d/sup.pgm";
		String fileout2 = "./sampleFiles/2d/inf.pgm";
		
		BufferedImage image1 = UtilImageIO.loadImage(filein1);
		BufferedImage image2 = UtilImageIO.loadImage(filein2);
		
		GrayU8 img1 = ConvertBufferedImage.convertFromSingle(image1, null, GrayU8.class);
		System.out.println("img1: " + img1.width + " x " + img1.height);
		GrayU8 img2 = ConvertBufferedImage.convertFromSingle(image2, null, GrayU8.class);
		System.out.println("img2: " + img2.width + " x " + img2.height);
		
		for (int j = 0; j < img1.height; j++) {
			for (int i = 0; i < img1.width; i++) {
				if (img1.get(i, j) >= img2.get(i, j)) {
					img1.set(i, j, img1.get(i, j));
					img2.set(i, j, img2.get(i, j));//inf
				} else {
					int infVal = img1.get(i, j);
					img1.set(i, j, img2.get(i, j));
					img2.set(i, j, infVal);//inf
				}
			}
		}
		UtilImageIO.saveImage(img1, fileout1);
		UtilImageIO.saveImage(img2, fileout2);
	}
	
	public static boolean Compare(GrayU8 img1, GrayU8 img2) {
		if (img1.width != img2.width || img1.height != img2.height) {
			System.out.println("Images with different sizes.");
			return false;
		}
		
		boolean isIdentical = true;
		outerloop:
		for (int j = 0; j < img1.height; j++) {
			for (int i = 0; i < img1.width; i++) {
				if (img1.get(i, j) != img2.get(i, j)) {
					isIdentical = false;
					break outerloop;
				}
			}
		}
		
		//System.out.println(isIdentical? "=" : "!=");
		return isIdentical;
	}
	
	public static boolean Compare_Ex2b(String filein1, String filein2, String fileout) 
			throws FileNotFoundException, UnsupportedEncodingException {
		// Compare_Ex2b writes the result to an out file.
		BufferedImage buffimage1 = UtilImageIO.loadImage(filein1);
		BufferedImage buffimage2 = UtilImageIO.loadImage(filein2);

		GrayU8 img1 = ConvertBufferedImage.convertFromSingle(buffimage1, null, GrayU8.class);
		System.out.println("img1: " + img1.width + " x " + img1.height);
		
		GrayU8 img2 = ConvertBufferedImage.convertFromSingle(buffimage2, null, GrayU8.class);
		System.out.println("img2: " + img2.width + " x " + img2.height);
		
		if (img1.width != img2.width || img1.height != img2.height) {
			System.out.println("Images are of different size.");
			PrintWriter writer = new PrintWriter(fileout, "UTF-8");
			writer.println("!=");
			writer.close();
			return false;
		}
		
		boolean isIdentical = true;
		outerloop:
		for (int j = 0; j < img1.height; j++) {
			for (int i = 0; i < img1.width; i++) {
				if (img1.get(i, j) != img2.get(i, j)) {
					isIdentical = false;
					break outerloop;
				}
			}
		}
		
		PrintWriter writer = new PrintWriter(fileout, "UTF-8");
		if (isIdentical) {
			writer.println("=");
		} else {
			writer.println("!=");
		}
		writer.close();
		System.out.println(isIdentical? "=" : "!=");
		return isIdentical;
	}
	
	public static void Threshold_Ex2a(int threshold) {
		/* Threshold until parameter (100)
		 */
		
		String filein1 = "./sampleFiles/2a/cam_74.pgm";
		String fileout1 = "./sampleFiles/2a/out1.png";
		
		BufferedImage image = UtilImageIO.loadImage(filein1);
		GrayU8 sourceimg = ConvertBufferedImage.convertFromSingle(image, null, GrayU8.class);
		GrayU8 outimg = new GrayU8(sourceimg.width, sourceimg.height);
				
		for (int j = 0; j < sourceimg.height; j++) {
			for (int i = 0; i < sourceimg.width; i++) {
				if (sourceimg.get(i, j) >= threshold)
					outimg.set(i, j, 255);//
				else
					outimg.set(i, j, 0);//
			}
		}
		
		UtilImageIO.saveImage(outimg, fileout1);//
	}
	
	public static void FourCorners_Ex1a() {
		/* It loads an specified image and converts to GrayU8 so that it can be handled.
		 * The imgray8 is going to be modified: its 4 corner pixels will have values 201, 202, 203, 204.
		 * Because of GrayU8, only gray colors are supported 0...255.
		 * Copy of an GrayU8.
		 */
		
		String filein1 = "./sampleFiles/1a/particles01__rows480__cols638.jpg";
		String fileout1 = "./sampleFiles/1a/out1.png";
		String fileout2 = "./sampleFiles/1a/out2.png";
		System.out.println("filein1: " + filein1);
		
		BufferedImage image = UtilImageIO.loadImage(filein1);

		// Converting from BufferedImage input image to BoofCV imgray8
		GrayU8 imgray8 = ConvertBufferedImage.convertFromSingle(image, null, GrayU8.class);
		System.out.println("imgray8.width: " + imgray8.width);
		System.out.println("imgray8.height: " + imgray8.height);

		// Copying the data array from imgray8 to a new im2gray8.
		GrayU8 im2gray8 = new GrayU8(imgray8.width, imgray8.height); // se crea una imagen
		im2gray8.setData(Arrays.copyOf(imgray8.getData(), imgray8.width * imgray8.height));

		imgray8.set(0, 0, 201); // setting the pixel at col 0, row 0 to value 201
		imgray8.set((imgray8.width - 1), 0, 202);
		imgray8.set(0, (imgray8.height - 1), 203);
		imgray8.set((imgray8.width - 1), (imgray8.height - 1), 204);
		System.out.println("imgray8.get(0,0): " + imgray8.get(0, 0));
		System.out.println("imgray8.get((imgray8.width - 1), 0): " + imgray8.get((imgray8.width - 1), 0));
		System.out.println("imgray8.get(0, (imgray8.height - 1)): " + imgray8.get(0, (imgray8.height - 1)));
		System.out.println("imgray8.get((imgray8.width - 1),(imgray8.height - 1)): "
				+ imgray8.get((imgray8.width - 1), (imgray8.height - 1)));

		UtilImageIO.saveImage(imgray8, fileout1);
		System.out.println("fileout1: " + fileout1);
		UtilImageIO.saveImage(im2gray8, fileout2);
		System.out.println("fileout2: " + fileout2);
	}

}