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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Panel;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.AttributedString;

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
public class ImageTools implements AutoCloseable {
    /**
     * Panel.
     */
    private Panel panel = new Panel();
    /**
     * Buffered Image.
     */
    private BufferedImage image;
    private Graphics2D graphics;
    /**
     * Output JPEG Compression Quality
     */
    private float jpegCompression = 0.75f;

    static {
        javax.imageio.ImageIO.scanForPlugins();
    }
    
    public ImageTools(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        resetGraphics();
    }
    /**
     * Image Tools.
     * @param in InputStream of an image
     * @throws IOException Read Exception
     */
    public ImageTools(final InputStream in) throws IOException {
        initInputStream(in);
    }
    /**
     * Image Tools.
     * @param in InputStream of an image
     * @throws IOException Read Exception
     */
    public ImageTools(final ImageInputStream in) throws IOException {
        image = ImageIO.read(in);
        if (image == null) {
            throw new IOException("Failed to read the image stream.");
        }
        resetGraphics();
    }
    /**
     * Image Tools.
     * @param path Path of an image
     * @throws IOException Read Exception
     */
    public ImageTools(final String path) throws IOException {
        this(new java.io.File(path));
    }
    /**
     * Image Tools.
     * @param file File of an image
     * @throws IOException Read Exception
     */
    public ImageTools(final org.netuno.psamata.io.File file) throws IOException {
        if (file.isInMemoryFile()) {
            initInputStream(file.getInputStream());
        } else {
            initFile(new java.io.File(file.toString()));
        }
    }

    /**
     * Image Tools.
     * @param path Path of an image
     * @throws IOException Read Exception
     */
    public ImageTools(final java.io.File path) throws IOException {
        initFile(path);
    }

    /**
     * Image Tools.
     * @param imgobj Image
     */
    public ImageTools(final Image imgobj) {
        image = new BufferedImage(
            imgobj.getWidth(panel),
            imgobj.getHeight(panel),
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D grapImg = createGraphics(image);
        grapImg.drawImage(imgobj, 0, 0, null);
        grapImg.dispose();
    }

    private void initInputStream(InputStream in) throws IOException {
        image = ImageIO.read(ImageIO.createImageInputStream(in));
        if (image == null) {
            throw new IOException("Failed to read the image stream.");
        }
        resetGraphics();
    }

    private void initFile(java.io.File path) throws IOException {
        FileImageInputStream in = null;
        try {
            in = new FileImageInputStream(path);
            image = ImageIO.read(in);
            if (image == null) {
                throw new IOException("Failed to read the image file: "+ path);
            }
            resetGraphics();
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            if (in != null && image == null) {
                in.close();
            }
        }
    }

    /**
     * Get Width.
     * @return width
     */
    public final int getWidth() {
        return image.getWidth();
    }
    /**
     * Get Height.
     * @return height
     */
    public final int getHeight() {
        return image.getHeight();
    }
    /**
     * Get output JPEG compression quality
     * @return JPEG compression
     */
    public float getJPEGCompression() {
		return jpegCompression;
	}
    /**
     * Set output JPEG compression quality
     * @param jpegCompression JPEG compression quality
     */
	public ImageTools setJPEGCompression(float jpegCompression) {
		this.jpegCompression = jpegCompression;
        return this;
	}
	/**
     * Resize.
     * @param w Width
     * @param h Height
     */
    public final ImageTools resize(final int w, final int h) {
        int width = w;
        int height = h;
        if ((width > 0 && height <= 0) || (width <= 0 && height > 0)) {
            float fRatio = (float)getWidth() / (float)getHeight();
            if (width > 0) {
                height = (int)((float)width / fRatio);
            } else {
                width = (int)((float)height * fRatio);
            }
        }
        BufferedImage imgNew = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D grapImg = createGraphics(imgNew);
        grapImg.setComposite(AlphaComposite.Clear);
        grapImg.fillRect(0, 0, width, height);
        grapImg.setComposite(AlphaComposite.Src);
        AffineTransform xform = AffineTransform.getScaleInstance(
            (double)width / getWidth(),
            (double)height / getHeight()
        );
        grapImg.drawRenderedImage(image, xform);
        grapImg.dispose();
        image = imgNew;
        resetGraphics();
        return this;
    }
    /**
     * Crop.
     * @param x X
     * @param y Y
     * @param width Width
     * @param height Height
     */
    public final ImageTools crop(final int x, final int y, final int width, final int height) {
        BufferedImage imgNew = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D grapImg = createGraphics(imgNew);
        grapImg.setComposite(AlphaComposite.Clear);
        grapImg.fillRect(0, 0, width, height);
        grapImg.setComposite(AlphaComposite.Src);
        grapImg.drawImage(image, 0, 0, width, height, x, y, x + width, y + height, null);
        grapImg.dispose();
        image = imgNew;
        resetGraphics();
        return this;
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    public ImageTools resetGraphics() {
        if (graphics != null) {
            graphics.dispose();
        }
        graphics = createGraphics(image);
        return this;
    }

    public final ImageTools drawBackground(Color color) {
        BufferedImage imgNew = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createGraphics(imgNew);
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(image, 0, 0, null);
        g.dispose();
        image = imgNew;
        resetGraphics();
        return this;
    }

    public final FontMetrics fontMetrics(Font font) {
        Graphics g = createGraphics(image);
        try {
            return g.getFontMetrics(font);
        } finally {
            g.dispose();
        }
    }

    public final java.awt.geom.Rectangle2D fontStringBounds(String text, Font font) {
        Graphics2D g = createGraphics(image);
        try {
            return font.getStringBounds(text, g.getFontRenderContext());
        } finally {
            g.dispose();
        }
    }

    public ImageTools drawText(String text, Font font, Color color, int x, int y) {
        getGraphics().setColor(color);
        getGraphics().setFont(font);
        getGraphics().drawString(text, x, y);
        return this;
    }

    public ImageTools drawText(String text, Font font, Color color, int x, int y, double rotation) {
        AffineTransform transform = getGraphics().getTransform();
        getGraphics().rotate(-Math.toRadians(rotation), x, y);
        getGraphics().setColor(color);
        getGraphics().setFont(font);
        getGraphics().drawString(text, x, y);
        getGraphics().setTransform(transform);
        return this;
    }

    public ImageTools drawText(String text, Font font, Color color, int x, int y, AffineTransform at) {
        AffineTransform transform = getGraphics().getTransform();
        getGraphics().setColor(color);
        getGraphics().setFont(font);
        getGraphics().setTransform(at);
        getGraphics().drawString(text, x, y);
        getGraphics().setTransform(transform);
        return this;
    }

    public ImageTools drawText(AttributedString text, int x, int y) {
        getGraphics().drawString(text.getIterator(), x, y);
        return this;
    }

    public ImageTools drawText(AttributedString text, int x, int y, double rotation) {
        AffineTransform transform = getGraphics().getTransform();
        getGraphics().rotate(-Math.toRadians(rotation), x, y);
        getGraphics().drawString(text.getIterator(), x, y);
        getGraphics().setTransform(transform);
        return this;
    }

    public ImageTools drawText(AttributedString text, int x, int y, AffineTransform at) {
        AffineTransform transform = getGraphics().getTransform();
        getGraphics().setTransform(at);
        getGraphics().drawString(text.getIterator(), x, y);
        getGraphics().setTransform(transform);
        return this;
    }

    public ImageTools drawImage(BufferedImage image, int x, int y) {
        getGraphics().drawImage(image, null, x, y);
        return this;
    }

    public ImageTools drawImage(BufferedImage image, int x, int y, double rotation) {
        AffineTransform transform = getGraphics().getTransform();
        getGraphics().rotate(-Math.toRadians(rotation), x, y);
        getGraphics().drawImage(image, null, x, y);
        getGraphics().setTransform(transform);
        return this;
    }

    public ImageTools drawImage(java.awt.Image image, AffineTransform at) {
        getGraphics().drawImage(image, at, null);
        return this;
    }

    public ImageTools drawImage(java.awt.Image image, int dx1, int dxy1, int dx2, int dxy2, int sx1, int sy1, int sx2, int sy2, Color bgColor) {
        getGraphics().drawImage(image, dx1, dxy1, dx2, dxy2, sx1, sy1, sx2, sy2, bgColor, null);
        return this;
    }

    public ImageTools drawImage(java.awt.Image image, int dx1, int dxy1, int dx2, int dxy2, int sx1, int sy1, int sx2, int sy2) {
        getGraphics().drawImage(image, dx1, dxy1, dx2, dxy2, sx1, sy1, sx2, sy2, null);
        return this;
    }

    public ImageTools drawImage(java.awt.Image image, int x, int y, int width, int height, Color bgColor) {
        getGraphics().drawImage(image, x, y, width, height, bgColor, null);
        return this;
    }

    public ImageTools drawImage(java.awt.Image image, int x, int y, int width, int height, Color bgColor, double rotation) {
        AffineTransform transform = getGraphics().getTransform();
        getGraphics().rotate(-Math.toRadians(rotation), x, y);
        getGraphics().drawImage(image, x, y, width, height, bgColor, null);
        getGraphics().setTransform(transform);
        return this;
    }

    public ImageTools drawImage(java.awt.Image image, int x, int y, int width, int height) {
        getGraphics().drawImage(image, x, y, width, height, null);
        return this;
    }

    public ImageTools drawImage(java.awt.Image image, int x, int y, int width, int height, double rotation) {
        AffineTransform transform = getGraphics().getTransform();
        getGraphics().rotate(-Math.toRadians(rotation), x, y);
        getGraphics().drawImage(image, x, y, width, height, null);
        getGraphics().setTransform(transform);
        return this;
    }

    public ImageTools drawImage(java.awt.Image image, int x, int y, Color bgColor) {
        getGraphics().drawImage(image, x, y, bgColor, null);
        return this;
    }

    public ImageTools drawImage(java.awt.Image image, int x, int y, Color bgColor, double rotation) {
        AffineTransform transform = getGraphics().getTransform();
        getGraphics().rotate(-Math.toRadians(rotation), x, y);
        getGraphics().drawImage(image, x, y, bgColor, null);
        getGraphics().setTransform(transform);
        return this;
    }

    public ImageTools drawImage(java.awt.Image image, int x, int y) {
        getGraphics().drawImage(image, x, y, null);
        return this;
    }

    public ImageTools drawImage(java.awt.Image image, int x, int y, double rotation) {
        AffineTransform transform = getGraphics().getTransform();
        getGraphics().rotate(-Math.toRadians(rotation), x, y);
        getGraphics().drawImage(image, x, y, null);
        getGraphics().setTransform(transform);
        return this;
    }

    public ImageTools drawLine(Color color, int x1, int y1, int x2, int y2) {
        getGraphics().setColor(color);
        getGraphics().drawLine(x1, y1, x2, y2);
        return this;
    }

    public ImageTools drawArc(Color color, int x, int y, int width, int height, int startAngle, int arcAngle) {
        getGraphics().setColor(color);
        getGraphics().drawArc(x, y, width, height, startAngle, arcAngle);
        return this;
    }

    public ImageTools fillArc(Color color, int x, int y, int width, int height, int startAngle, int arcAngle) {
        getGraphics().setColor(color);
        getGraphics().fillArc(x, y, width, height, startAngle, arcAngle);
        return this;
    }

    public ImageTools drawOval(Color color, int x, int y, int width, int height) {
        getGraphics().setColor(color);
        getGraphics().drawOval(x, y, width, height);
        return this;
    }

    public ImageTools fillOval(Color color, int x, int y, int width, int height) {
        getGraphics().setColor(color);
        getGraphics().fillOval(x, y, width, height);
        return this;
    }

    public ImageTools drawPolygon(Color color, int[] xPoints, int[] yPoints, int nPoints) {
        getGraphics().setColor(color);
        getGraphics().drawPolygon(xPoints, yPoints, nPoints);
        return this;
    }

    public ImageTools fillPolygon(Color color, int[] xPoints, int[] yPoints, int nPoints) {
        getGraphics().setColor(color);
        getGraphics().fillPolygon(xPoints, yPoints, nPoints);
        return this;
    }

    public ImageTools drawPolyline(Color color, int[] xPoints, int[] yPoints, int nPoints) {
        getGraphics().setColor(color);
        getGraphics().drawPolyline(xPoints, yPoints, nPoints);
        return this;
    }

    public ImageTools drawRect(Color color, int x, int y, int width, int height) {
        getGraphics().setColor(color);
        getGraphics().drawRect(x, y, width, height);
        return this;
    }

    public ImageTools drawRect(Color color, int x, int y, int width, int height, double rotation) {
        AffineTransform transform = getGraphics().getTransform();
        getGraphics().rotate(-Math.toRadians(rotation), x, y);
        getGraphics().setColor(color);
        getGraphics().drawRect(x, y, width, height);
        getGraphics().setTransform(transform);
        return this;
    }

    public ImageTools fillRect(Color color, int x, int y, int width, int height) {
        getGraphics().setColor(color);
        getGraphics().fillRect(x, y, width, height);
        return this;
    }

    public ImageTools fillRect(Color color, int x, int y, int width, int height, double rotation) {
        AffineTransform transform = getGraphics().getTransform();
        getGraphics().rotate(-Math.toRadians(rotation), x, y);
        getGraphics().setColor(color);
        getGraphics().fillRect(x, y, width, height);
        getGraphics().setTransform(transform);
        return this;
    }

    public ImageTools drawRoundRect(Color color, int x, int y, int width, int height, int arcWidth, int arcHeight) {
        getGraphics().setColor(color);
        getGraphics().drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        return this;
    }

    public ImageTools drawRoundRect(Color color, int x, int y, int width, int height, int arcWidth, int arcHeight, double rotation) {
        AffineTransform transform = getGraphics().getTransform();
        getGraphics().rotate(-Math.toRadians(rotation), x, y);
        getGraphics().setColor(color);
        getGraphics().drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        getGraphics().setTransform(transform);
        return this;
    }

    public ImageTools fillRoundRect(Color color, int x, int y, int width, int height, int arcWidth, int arcHeight) {
        getGraphics().setColor(color);
        getGraphics().fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        return this;
    }

    public ImageTools fillRoundRect(Color color, int x, int y, int width, int height, int arcWidth, int arcHeight, double rotation) {
        AffineTransform transform = getGraphics().getTransform();
        getGraphics().rotate(-Math.toRadians(rotation), x, y);
        getGraphics().setColor(color);
        getGraphics().fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        getGraphics().setTransform(transform);
        return this;
    }

    /**
     * Get Image.
     * @return Image
     */
    public final Image getImage() {
        return image.getScaledInstance(
            getWidth(),
            getHeight(),
            Image.SCALE_DEFAULT
        );
    }
    
    /**
     * Get Image.
     * @return Image
     */
    public final BufferedImage getBufferedImage() {
        return image;
    }

    /**
     * Set Output Image.
     * @param out Output
     * @param type Output type: "png", "jpg", "gif"
     * @throws IOException Write file exception.
     */
    public final ImageTools save(final OutputStream out, final String type) throws IOException {
    	save(ImageIO.createImageOutputStream(out), type);
        return this;
    }
    
    /**
     * Set Output Image.
     * @param out Output
     * @param type Output type: "png", "jpg", "gif"
     * @throws IOException Write file exception.
     */
    public final ImageTools save(final ImageOutputStream out, final String type) throws IOException {
        try {
        	if (type.equalsIgnoreCase("gif")) {
                ImageIO.write(convertRGBAToIndexed(image), "gif", out);
        	} else if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg")) {
                BufferedImage target = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);;
                Graphics2D g = createGraphics(target);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.drawImage(image, 0, 0, null);
                g.dispose();
                ImageWriter writer = ImageIO.getImageWritersByFormatName(type).next();
                ImageWriteParam writerParam = writer.getDefaultWriteParam();
                writerParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writerParam.setCompressionQuality(getJPEGCompression());
                writer.setOutput(out);
                writer.write(null, new IIOImage(target, null, null), writerParam);
                writer.dispose();
        	} else {
        		if (!ImageIO.write(image, type, out)) {
                    throw new IOException("Failed to write image file of type "+ type + ".");
                }
        	}
        } catch (IOException ioe) {
            throw ioe;
        }
        return this;
    }

    /**
     * Save.
     * @param path Path to save
     * @param type Output type: "png", "jpg", "gif"
     * @throws IOException Write file exception.
     */
    public final ImageTools save(final String path, final String type) throws IOException {
        return save(new java.io.File(path), type);
    }
    /**
     * Save.
     * @param path Path to save
     * @param type Output type: "png", "jpg", "gif"
     * @throws IOException Write file exception.
     */
    public final ImageTools save(final org.netuno.psamata.io.File path, final String type) throws IOException {
        return save(path.toString(), type);
    }
    /**
     * Save.
     * @param path Path to save
     * @param type Output type: "png", "jpg", "gif"
     * @throws IOException Write file exception.
     */
    public final ImageTools save(final java.io.File path, final String type) throws IOException {
    	FileImageOutputStream out = null;
        try {
            out = new FileImageOutputStream(path);
            save(out, type);
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return this;
    }

    @Override
    public void close() throws Exception {
        if (graphics != null) {
            graphics.dispose();
            graphics = null;
        }
        image = null;
    }

    public static Graphics2D createGraphics(BufferedImage bi) {
        Graphics2D g = bi.createGraphics();
        RenderingHints hints =new RenderingHints(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY
        );
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.addRenderingHints(hints);
        return g;
    }
    
    public static BufferedImage convertRGBAToIndexed(BufferedImage src) {
    	BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
    	Graphics2D g = createGraphics(dest);
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
