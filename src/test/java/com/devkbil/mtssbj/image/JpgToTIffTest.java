package com.devkbil.mtssbj.image;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class JpgToTIffTest {
    public static void main(String[] args) throws IOException {

        String compressionType = "JPEG";
        String dirName = "/Users/kbil/IdeaProjects/module/jpg";
        String outputFileName = "result";
        boolean generated = false;
        ImageOutputStream ios = null;
        ImageWriter writer = null;
        try {
            // verify the directory for the image files
            File dir = new File(dirName);
            if (null != dir && !dir.isDirectory()) {
                throw new FileNotFoundException("No directory exists with the given name" + dirName);
            }
            // verify the images files exist

            File[] files = dir.listFiles();
            if (null == files || files.length == 0) {
                throw new FileNotFoundException("No image files to process");

            } else {
                // Create the output file on the disk
                File file = new File(dirName + outputFileName + ".tif");
                ios = ImageIO.createImageOutputStream(file);

                // Get the appropriate Tiff Image Writer
                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("tiff");
                if (null == writers || !writers.hasNext()) {
                    throw new Exception("Appropriate Tiff writer not found");
                }

                writer = ImageIO.getImageWritersByFormatName("tiff").next();
                writer.setOutput(ios);
                // Set the compression parameters for Tiff image
                ImageWriteParam param = writer.getDefaultWriteParam();
                // param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                // param.setCompressionType(compressionType);
                // param.setCompressionQuality(0.9F);

                // Loop through all image files and write them to output tiff image
                for (int i = 0; i < files.length; i++) {
                    InputStream fis = null;
                    int dotIndex = files[i].getName().lastIndexOf('.');
                    dotIndex = dotIndex > 0 ? dotIndex : files[i].getName().length();
                    String fileName = files[i].getName().substring(0, dotIndex);
                    if (!fileName.equalsIgnoreCase(outputFileName)) {
                        try {

                            fis = new BufferedInputStream(new FileInputStream(files[i]));
                            BufferedImage image = ImageIO.read(fis);
                            IIOImage img = new IIOImage(image, null, null);
                            if (i == 0) {
                                writer.write(null, img, param);
                            } else {
                                writer.writeInsert(-1, img, param);
                            }
                            image.flush();
                        } finally {
                            if (null != fis) {
                                fis.close();
                            }
                        }
                    }
                }
                ios.flush();
                generated = true;
            }

        } catch (FileNotFoundException ex) {
            generated = false;
        } catch (IOException ex) {
            generated = false;
        } catch (Exception ex) {
            generated = false;
        } finally {
            if (null != ios)
                ios.close();
            if (null != writer)
                writer.dispose();
        }
    }
}
