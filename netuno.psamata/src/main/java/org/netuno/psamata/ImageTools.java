/*
 * Licensed to the Netuno.org under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Netuno.org licenses this file to You under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.netuno.psamata;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

/**
 * Image Tools.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ImageTools {
    /**
     * Panel.
     */
    private Panel panel = new Panel();
    /**
     * Buffered Image.
     */
    private BufferedImage img;
    /**
     * Output JPEG Compression Quality
     */
    private float ouputJpegCompressionQuality = 0.75f;
    /**
     * Image Tools.
     * @param in InputStream of a image
     * @throws IOException Read Exception
     */
    public ImageTools(final InputStream in) throws IOException {
        img = ImageIO.read(ImageIO.createImageInputStream(in));
    }
    /**
     * Image Tools.
     * @param in InputStream of a image
     * @throws IOException Read Exception
     */
    public ImageTools(final ImageInputStream in) throws IOException {
        img = ImageIO.read(in);
    }
    /**
     * Image Tools.
     * @param path Path of a image
     * @throws IOException Read Exception
     */
    public ImageTools(final String path) throws IOException {
    	FileImageInputStream in = null;
        try {
            in = new FileImageInputStream(new File(path));
            img = ImageIO.read(in);
        } catch (IOException ioe) {
            throw ioe;
        } finally {
        	if (in != null && img == null) {
                in.close();
            }
        }
    }
    /**
     * Image Tools.
     * @param imgobj Image
     */
    public ImageTools(final Image imgobj) {
        img = new BufferedImage(imgobj.getWidth(panel),
        imgobj.getHeight(panel), BufferedImage.TYPE_INT_RGB);
        Graphics2D grapImg = img.createGraphics();
        grapImg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
        grapImg.drawImage(imgobj, 0, 0, panel);
    }
    /**
     * Get Width.
     * @return width
     */
    public final int getWidth() {
        return img.getWidth();
    }
    /**
     * Get Height.
     * @return height
     */
    public final int getHeight() {
        return img.getHeight();
    }
    /**
     * Get ouput JPEG compression quality
     * @return JPEG compression quality
     */
    public float getOuputJpegCompressionQuality() {
		return ouputJpegCompressionQuality;
	}
    /**
     * Set ouput JPEG compression quality
     * @param ouputJpegCompressionQuality JPEG compression quality
     */
	public void setOuputJpegCompressionQuality(float ouputJpegCompressionQuality) {
		this.ouputJpegCompressionQuality = ouputJpegCompressionQuality;
	}
	/**
     * Resize.
     * @param w Width
     * @param h Height
     */
    public final void resize(final int w, final int h) {
        int width = w;
        int height = h;
        if ((width > 0 && height <= 0) || (width <= 0 && height > 0)) {
            float fRatio = (float)img.getWidth() / (float)img.getHeight();
            if (width > 0) {
                height = (int)((float)width / fRatio);
            } else {
                width = (int)((float)height * fRatio);
            }
        }
        BufferedImage imgNew = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_RGB);
        Graphics2D grapImg = imgNew.createGraphics();
        grapImg.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        AffineTransform xform = AffineTransform.getScaleInstance(
        (double) width / img.getWidth(), (double) height / img.getHeight());
        grapImg.drawRenderedImage(img, xform);
        grapImg.dispose();
        img = imgNew;
    }
    /**
     * Crop.
     * @param x X
     * @param y Y
     * @param w Width
     * @param h Height
     */
    public final void crop(final int x, final int y, final int w, final int h) {
        BufferedImage imgNew = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D grapImg = imgNew.createGraphics();
        grapImg.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        grapImg.drawImage(img, 0, 0, w, h, x, y, x + w, y + h, null);
        grapImg.dispose();
        img = imgNew;
    }
    /**
     * Get Image.
     * @return Image
     */
    public final Image getImage() {
        return img.getScaledInstance(img.getWidth(), img.getHeight(),
        Image.SCALE_DEFAULT);
    }

    /**
     * Set Output Image.
     * @param out Output
     * @param type Output type: "png", "jpg", "gif"
     * @throws IOException Write file exception.
     */
    public final void setOutputImage(final OutputStream out, final String type)
    	    throws IOException {
    	setOutputImage(ImageIO.createImageOutputStream(out), type);
    }
    
    /**
     * Set Output Image.
     * @param out Output
     * @param type Output type: "png", "jpg", "gif"
     * @throws IOException Write file exception.
     */
    public final void setOutputImage(final ImageOutputStream out, final String type)
    throws IOException {
        try {
        	if (type.equalsIgnoreCase("gif")) {
                ImageIO.write(convertRGBAToIndexed(img), "gif", out);
        	} else if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg")) {
                ImageWriter writer = ImageIO.getImageWritersByFormatName(type).next();
                ImageWriteParam writerParam = writer.getDefaultWriteParam();
                writerParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writerParam.setCompressionQuality(getOuputJpegCompressionQuality());
                writer.setOutput(out);
                writer.write(null, new IIOImage(img, null, null), writerParam);
                writer.dispose();
        	} else {
        		ImageIO.write(img, type, out);
        	}
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    /**
     * Save.
     * @param path Path to save
     * @param type Output type: "png", "jpg", "gif"
     * @throws IOException Write file exception.
     */
    public final void save(final String path, final String type) throws IOException {
    	FileImageOutputStream out = null;
        try {
            out = new FileImageOutputStream(new File(path));
            setOutputImage(out, type);
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    public static BufferedImage convertRGBAToIndexed(BufferedImage src) {
    	BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
    	java.awt.Graphics g = dest.getGraphics();
    	g.setColor(new java.awt.Color(231,20,189));
    	g.fillRect(0, 0, dest.getWidth(), dest.getHeight()); //fill with a hideous color and make it transparent
    	dest = makeTransparent(dest,0,0);
    	dest.createGraphics().drawImage(src,0,0, null);
    	return dest;
    }

    public static BufferedImage makeTransparent(BufferedImage image, int x, int y) {
    	java.awt.image.ColorModel cm = image.getColorModel();
    	if (!(cm instanceof java.awt.image.IndexColorModel)) {
    		return image; //sorry...
    	}
    	java.awt.image.IndexColorModel icm = (java.awt.image.IndexColorModel) cm;
    	java.awt.image.WritableRaster raster = image.getRaster();
    	int pixel = raster.getSample(x, y, 0); //pixel is offset in ICM's palette
    	int size = icm.getMapSize();
    	byte[] reds = new byte[size];
    	byte[] greens = new byte[size];
    	byte[] blues = new byte[size];
    	icm.getReds(reds);
    	icm.getGreens(greens);
    	icm.getBlues(blues);
    	java.awt.image.IndexColorModel icm2 = new java.awt.image.IndexColorModel(8, size, reds, greens, blues, pixel);
    	return new BufferedImage(icm2, raster, image.isAlphaPremultiplied(), null);
    }
}
