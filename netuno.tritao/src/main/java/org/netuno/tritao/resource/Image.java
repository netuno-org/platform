package org.netuno.tritao.resource;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.ImageTools;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.File;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.MimeTypes;
import org.netuno.psamata.io.OutputStream;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.FileSystemPath;
import org.netuno.tritao.resource.util.ResourceException;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.TextAttribute;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.AttributedString;
import java.util.List;
import java.util.Map;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 * Image - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "image")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Image",
                introduction = "Recurso para a manipulação de imagens programaticamente.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Image",
                introduction = "Resource for manipulating images programmatically.",
                howToUse = { }
        )
})
// https://www.baeldung.com/java-add-text-to-image
public class Image extends ResourceBase implements AutoCloseable {
    public ImageTools imageTools = null;

    public Image(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    private Image(Proteu proteu, Hili hili, ImageTools imageTools) {
        super(proteu, hili);
        this.imageTools = imageTools;
    }

    public Image init(int width, int height) throws ResourceException {
        return new Image(getProteu(), getHili(), new ImageTools(width, height));
    }

    public Image init(final InputStream in) throws ResourceException {
        return init((java.io.InputStream)in);
    }

    public Image init(final java.io.InputStream in) throws ResourceException {
        try {
            return new Image(getProteu(), getHili(), new ImageTools(in));
        } catch (Exception e) {
            throw new ResourceException("_image.init(...)", e);
        }
    }
    
    public Image init(final ImageInputStream in) throws ResourceException {
        try {
            return new Image(getProteu(), getHili(), new ImageTools(in));
        } catch (Exception e) {
            throw new ResourceException("_image.init(...)", e);
        }
    }

    public Image init(final Storage storage) {
        String path = FileSystemPath.absoluteFromStorage(getProteu(), storage);
        try {
            return new Image(getProteu(), getHili(), new ImageTools(path));
        } catch (Exception e) {
            throw new ResourceException("_image.init("+ path +")", e);
        }
    }

    public Image init(final File file) {
        try {
            return new Image(getProteu(), getHili(), new ImageTools(file));
        } catch (Exception e) {
            throw new ResourceException("_image.init("+ file.getName() +")", e);
        }
    }

    public Image init(final java.io.File file) throws ResourceException {
        try {
            return new Image(getProteu(), getHili(), new ImageTools(file));
        } catch (Exception e) {
            throw new ResourceException("_image.init("+ file.getName() +")", e);
        }
    }
    
    public Image init(final java.awt.Image image) throws ResourceException {
        try {
            return new Image(getProteu(), getHili(), new ImageTools(image));
        } catch (Exception e) {
            throw new ResourceException("_image.init(...)", e);
        }
    }

    public Color color(int r, int g, int b) {
        return new Color(r, g, b);
    }

    public Color color(int r, int g, int b, int a) {
        return new Color(r, g, b, a);
    }

    public Color color(float r, float g, float b) {
        return new Color(r, g, b);
    }

    public Color color(float r, float g, float b, float a) {
        return new Color(r, g, b, a);
    }

    public Color color(String color) {
        color = color.toLowerCase().replace('-', '_');
        try {
            return (Color)Color.class.getDeclaredField(color.toUpperCase()).get(Color.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                return colorDecode(color);
            } catch (Throwable t) {
                throw new ResourceException("_image.color("+ color +")", e);
            }
        }
    }

    public Color colorDecode(String value) {
        return Color.decode(value);
    }

    public int fontField(String field) {
        try {
            field = enumValueOf(field);
            return (Integer)Font.class.getDeclaredField(field).get(null);
        } catch (Exception e) {
            throw new ResourceException("_image.fontField("+ field +")", e);
        }
    }

    public Font font(String name, int style, int fontSize) {
        return new Font(name, style, fontSize);
    }

    public Font font(Storage storage) {
        return font(Font.TRUETYPE_FONT, storage);
    }

    public Font font(int type, Storage storage) {
        try {
            return Font.createFont(type, storage.inputStream());
        } catch (Exception e) {
            throw new ResourceException("_image.font("+ type +", "+ storage.fullPath() +")", e);
        }
    }

    public Font font(Storage storage, float size) {
        return font(Font.TRUETYPE_FONT, storage, Font.PLAIN, size);
    }

    public Font font(Storage storage, int style, float size) {
        return font(Font.TRUETYPE_FONT, storage, style, size);
    }

    public Font font(int type, Storage storage, int style, float size) {
        try {
            Font font = Font.createFont(type, storage.inputStream());
            font = font.deriveFont(style, size);
            return font;
        } catch (Exception e) {
            throw new ResourceException("_image.font("+ type +", "+ storage.fullPath() +", "+ style +", "+ size +")", e);
        }
    }

    public Font font(File file) {
        return font(Font.TRUETYPE_FONT, file);
    }

    public Font font(int type, File file) {
        try {
            return Font.createFont(type, file.inputStream());
        } catch (Exception e) {
            throw new ResourceException("_image.font("+ type +", "+ file.fullPath() +")", e);
        }
    }

    public Font font(File file, float size) {
        return font(Font.TRUETYPE_FONT, file, Font.PLAIN, size);
    }

    public Font font(File file, int style, float size) {
        return font(Font.TRUETYPE_FONT, file, style, size);
    }

    public Font font(int type, File file, int style, float size) {
        try {
            Font font = Font.createFont(type, file.inputStream());
            font = font.deriveFont(style, size);
            return font;
        } catch (Exception e) {
            throw new ResourceException("_image.font("+ type +", "+ file.fullPath() +", "+ style +", "+ size +")", e);
        }
    }

    public FontMetrics fontMetrics(Font font) {
        return imageTools.fontMetrics(font);
    }

    public final java.awt.geom.Rectangle2D fontStringBounds(String text, Font font) {
        return imageTools.fontStringBounds(text, font);
    }

    private TextAttribute textAttribute(String attribute) {
        attribute = enumValueOf(attribute);
        try {
            return (TextAttribute)TextAttribute.class.getDeclaredField(attribute.toUpperCase()).get(TextAttribute.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ResourceException("_image.textAttribute("+ attribute +")", e);
        }
    }

    public AttributedString attributedString(String text, Map<String, Object> attributes) {
        return attributedString(text, new Values(attributes));
    }

    public AttributedString attributedString(String text, Values attributes) {
        AttributedString attributedText = new AttributedString(text);
        attributes.forEach((s, v) -> attributedText.addAttribute(textAttribute(s), v));
        return attributedText;
    }

    public java.awt.RenderingHints renderingHints(java.awt.RenderingHints.Key key, Object value) {
        return new java.awt.RenderingHints(key, value);
    }

    public java.awt.RenderingHints.Key renderingHintsKey(String key) {
        key = "KEY_" + enumValueOf(key);
        try {
            return (java.awt.RenderingHints.Key)java.awt.RenderingHints.Key.class.getDeclaredField(key.toUpperCase()).get(java.awt.RenderingHints.Key.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ResourceException("_image.renderingHintsKey("+ key +")", e);
        }
    }

    public int affineTransformType(String type) {
        type = "TYPE_" + enumValueOf(type);
        try {
            return (int)java.awt.geom.AffineTransform.class.getDeclaredField(type.toUpperCase()).get(java.awt.geom.AffineTransform.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ResourceException("_image.affineTransformType("+ type +")", e);
        }
    }

    public java.awt.geom.AffineTransform affineTransform() {
        return new java.awt.geom.AffineTransform();
    }

    public java.awt.geom.AffineTransform affineTransform(java.awt.geom.AffineTransform tx) {
        return new java.awt.geom.AffineTransform(tx);
    }

    public java.awt.geom.AffineTransform affineTransform(double m00, double m10, double m01, double m11, double m02, double m12) {
        return new java.awt.geom.AffineTransform(m00, m10, m01, m11, m02, m12);
    }

    public java.awt.geom.AffineTransform affineTransform(double[] tx) {
        return affineTransform(tx);
    }

    public java.awt.geom.AffineTransform affineTransform(List<?> tx) {
        return affineTransform(new Values(tx));
    }

    public java.awt.geom.AffineTransform affineTransform(Values tx) {
        return new java.awt.geom.AffineTransform(tx.toDoubleArray());
    }

    public java.awt.Image image() {
        return getImage();
    }

    public java.awt.Image getImage() {
        return imageTools.getImage();
    }

    public java.awt.image.BufferedImage bufferedImage() {
        return getBufferedImage();
    }

    public java.awt.image.BufferedImage getBufferedImage() {
        return imageTools.getBufferedImage();
    }

    public int width() {
        return getWidth();
    }

    public int getWidth() {
        return imageTools.getWidth();
    }
    
    public int height() {
        return getHeight();
    }
    
    public int getHeight() {
        return imageTools.getHeight();
    }

    public File file(String fileName, String type) {
        return getFile(fileName, type);
    }

    public File getFile(String fileName, String type) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        save(baos, type);
        return new File(fileName, MimeTypes.getMimeTypeFromExtension(type), new ByteArrayInputStream(baos.toByteArray()));
    }
    
    public float jpegCompression() {
        return getJPEGCompression();
    }
    
    public float getJPEGCompression() {
        return imageTools.getJPEGCompression();
    }

    public Image jpegCompression(float jpegCompression) {
		return setJPEGCompression(jpegCompression);
	}

    public Image setJPEGCompression(float jpegCompression) {
		imageTools.setJPEGCompression(jpegCompression);
        return this;
	}

    public Image resize(final int width, final int height) {
        imageTools.resize(width, height);
        return this;
    }

    public Image crop(final int x, final int y, final int width, final int height) {
        imageTools.crop(x, y, width, height);
        return this;
    }

    public java.awt.Graphics2D graphics() {
        return getGraphics();
    }

    public java.awt.Graphics2D getGraphics() {
        return imageTools.getGraphics();
    }

    public Image resetGraphics() {
        imageTools.resetGraphics();
        return this;
    }

    public Image drawBackground(Color color) {
        imageTools.drawBackground(color);
        return this;
    }

    public Image drawText(String text, Font font, Color color, int x, int y) {
        imageTools.drawText(text, font, color, x, y);
        return this;
    }

    public Image drawText(String text, Font font, Color color, int x, int y, double rotation) {
        imageTools.drawText(text, font, color, x, y, rotation);
        return this;
    }

    public Image drawText(String text, Font font, Color color, int x, int y, java.awt.geom.AffineTransform at) {
        imageTools.drawText(text, font, color, x, y, at);
        return this;
    }

    public Image drawText(AttributedString text, int x, int y) {
        imageTools.drawText(text, x, y);
        return this;
    }

    public Image drawText(AttributedString text, int x, int y, double rotation) {
        imageTools.drawText(text, x, y, rotation);
        return this;
    }

    public Image drawText(AttributedString text, int x, int y, java.awt.geom.AffineTransform at) {
        imageTools.drawText(text, x, y, at);
        return this;
    }
    
    public Image drawImage(Image image, int x, int y) {
        return drawImage(image.getBufferedImage(), x, y);
    }
    
    public Image drawImage(java.awt.image.BufferedImage image, int x, int y) {
        imageTools.drawImage(image, x, y);
        return this;
    }
    
    public Image drawImage(Image image, int x, int y, double rotation) {
        return drawImage(image.getBufferedImage(), x, y, rotation);
    }
    
    public Image drawImage(java.awt.image.BufferedImage image, int x, int y, double rotation) {
        imageTools.drawImage(image, x, y, rotation);
        return this;
    }
    
    public Image drawImage(Image image, java.awt.geom.AffineTransform at) {
        return drawImage(image.getImage(), at);
    }
    
    public Image drawImage(java.awt.Image image, java.awt.geom.AffineTransform at) {
        imageTools.drawImage(image, at);
        return this;
    }
    
    public Image drawImage(Image image, int dx1, int dxy1, int dx2, int dxy2, int sx1, int sy1, int sx2, int sy2, Color bgColor) {
        return drawImage(image.getImage(), dx1, dxy1, dx2, dxy2, sx1, sy1, sx2, sy2, bgColor);
    }
    
    public Image drawImage(java.awt.Image image, int dx1, int dxy1, int dx2, int dxy2, int sx1, int sy1, int sx2, int sy2, Color bgColor) {
        imageTools.drawImage(image, dx1, dxy1, dx2, dxy2, sx1, sy1, sx2, sy2, bgColor);
        return this;
    }
    
    public Image drawImage(Image image, int dx1, int dxy1, int dx2, int dxy2, int sx1, int sy1, int sx2, int sy2) {
        return drawImage(image.getImage(), dx1, dxy1, dx2, dxy2, sx1, sy1, sx2, sy2);
    }
    
    public Image drawImage(java.awt.Image image, int dx1, int dxy1, int dx2, int dxy2, int sx1, int sy1, int sx2, int sy2) {
        imageTools.drawImage(image, dx1, dxy1, dx2, dxy2, sx1, sy1, sx2, sy2);
        return this;
    }
    
    public Image drawImage(Image image, int x, int y, int width, int height, Color bgColor) {
        return drawImage(image.getImage(), x, y, width, height, bgColor);
    }
    
    public Image drawImage(java.awt.Image image, int x, int y, int width, int height, Color bgColor) {
        imageTools.drawImage(image, x, y, width, height, bgColor);
        return this;
    }
    
    public Image drawImage(Image image, int x, int y, int width, int height, Color bgColor, double rotation) {
        return drawImage(image.getImage(), x, y, width, height, bgColor, rotation);
    }
    
    public Image drawImage(java.awt.Image image, int x, int y, int width, int height, Color bgColor, double rotation) {
        imageTools.drawImage(image, x, y, width, height, bgColor, rotation);
        return this;
    }
    
    public Image drawImage(Image image, int x, int y, int width, int height) {
        return drawImage(image.getImage(), x, y, width, height);
    }
    
    public Image drawImage(java.awt.Image image, int x, int y, int width, int height) {
        imageTools.drawImage(image, x, y, width, height);
        return this;
    }
    
    public Image drawImage(Image image, int x, int y, int width, int height, double rotation) {
        return drawImage(image.getImage(), x, y, width, height, rotation);
    }
    
    public Image drawImage(java.awt.Image image, int x, int y, int width, int height, double rotation) {
        imageTools.drawImage(image, x, y, width, height, rotation);
        return this;
    }
    
    public Image drawImage(Image image, int x, int y, Color bgColor) {
        return drawImage(image.getImage(), x, y, bgColor);
    }
    
    public Image drawImage(java.awt.Image image, int x, int y, Color bgColor) {
        imageTools.drawImage(image, x, y, bgColor);
        return this;
    }
    
    public Image drawImage(Image image, int x, int y, Color bgColor, double rotation) {
        return drawImage(image.getImage(), x, y, bgColor, rotation);
    }
    
    public Image drawImage(java.awt.Image image, int x, int y, Color bgColor, double rotation) {
        imageTools.drawImage(image, x, y, bgColor, rotation);
        return this;
    }
    
    public Image drawImage(java.awt.Image image, int x, int y) {
        imageTools.drawImage(image, x, y);
        return this;
    }
    
    public Image drawImage(java.awt.Image image, int x, int y, double rotation) {
        imageTools.drawImage(image, x, y, rotation);
        return this;
    }

    public Image drawLine(Color color, int x1, int y1, int x2, int y2) {
        imageTools.drawLine(color, x1, y1, x2, y2);
        return this;
    }

    public Image drawArc(Color color, int x, int y, int width, int height, int startAngle, int arcAngle) {
        imageTools.drawArc(color, x, y, width, height, startAngle, arcAngle);
        return this;
    }

    public Image fillArc(Color color, int x, int y, int width, int height, int startAngle, int arcAngle) {
        imageTools.fillArc(color, x, y, width, height, startAngle, arcAngle);
        return this;
    }

    public Image drawOval(Color color, int x, int y, int width, int height) {
        imageTools.drawOval(color, x, y, width, height);
        return this;
    }

    public Image fillOval(Color color, int x, int y, int width, int height) {
        imageTools.fillOval(color, x, y, width, height);
        return this;
    }
    
    public Image drawPolygon(Color color, List<?> xPoints, List<?> yPoints) {
        return drawPolygon(
            color,
            new Values(xPoints),
            new Values(yPoints),
            Math.min(xPoints.size(), yPoints.size())
        );
    }

    public Image drawPolygon(Color color, Values xPoints, Values yPoints) {
        return drawPolygon(
            color,
            new Values(xPoints),
            new Values(yPoints),
            Math.min(xPoints.size(), yPoints.size())
        );
    }

    public Image drawPolygon(Color color, List<?> xPoints, List<?> yPoints, int nPoints) {
        return drawPolygon(
            color,
            new Values(xPoints),
            new Values(yPoints),
            nPoints
        );
    }

    public Image drawPolygon(Color color, Values xPoints, Values yPoints, int nPoints) {
        return drawPolygon(
            color,
            xPoints.toIntArray(),
            yPoints.toIntArray(),
            nPoints
        );
    }

    public Image drawPolygon(Color color, int[] xPoints, int[] yPoints, int nPoints) {
        imageTools.drawPolygon(color, xPoints, yPoints, nPoints);
        return this;
    }
    
    public Image fillPolygon(Color color, List<?> xPoints, List<?> yPoints) {
        return fillPolygon(
            color,
            new Values(xPoints),
            new Values(yPoints),
            Math.min(xPoints.size(), yPoints.size())
        );
    }

    public Image fillPolygon(Color color, Values xPoints, Values yPoints) {
        
        return fillPolygon(
            color,
            xPoints.toIntArray(),
            yPoints.toIntArray(),
            Math.min(xPoints.size(), yPoints.size())
        );
    }
    
    public Image fillPolygon(Color color, List<?> xPoints, List<?> yPoints, int nPoints) {
        return fillPolygon(
            color,
            new Values(xPoints),
            new Values(yPoints),
            nPoints
        );
    }

    public Image fillPolygon(Color color, Values xPoints, Values yPoints, int nPoints) {
        return fillPolygon(
            color,
            xPoints.toIntArray(),
            yPoints.toIntArray(),
            nPoints
        );
    }

    public Image fillPolygon(Color color, int[] xPoints, int[] yPoints, int nPoints) {
        imageTools.fillPolygon(color, xPoints, yPoints, nPoints);
        return this;
    }
    
    public Image drawPolyline(Color color, List<?> xPoints, List<?> yPoints) {
        return drawPolyline(
            color,
            new Values(xPoints),
            new Values(yPoints),
            Math.min(xPoints.size(), yPoints.size())
        );
    }

    public Image drawPolyline(Color color, Values xPoints, Values yPoints) {
        return drawPolyline(
            color,
            xPoints.toIntArray(),
            yPoints.toIntArray(),
            Math.min(xPoints.size(), yPoints.size())
        );
    }
    
    public Image drawPolyline(Color color, List<?> xPoints, List<?> yPoints, int nPoints) {
        return drawPolyline(
            color,
            new Values(xPoints),
            new Values(yPoints),
            nPoints
        );
    }

    public Image drawPolyline(Color color, Values xPoints, Values yPoints, int nPoints) {
        return drawPolyline(
            color,
            xPoints.toIntArray(),
            yPoints.toIntArray(),
            nPoints
        );
    }

    public Image drawPolyline(Color color, int[] xPoints, int[] yPoints, int nPoints) {
        imageTools.drawPolyline(color, xPoints, yPoints, nPoints);
        return this;
    }
    
    public Image drawRect(Color color, int x, int y, int width, int height) {
        imageTools.drawRect(color, x, y, width, height);
        return this;
    }
    
    public Image drawRect(Color color, int x, int y, int width, int height, double rotation) {
        imageTools.drawRect(color, x, y, width, height, rotation);
        return this;
    }
    
    public Image fillRect(Color color, int x, int y, int width, int height) {
        imageTools.fillRect(color, x, y, width, height);
        return this;
    }
    
    public Image fillRect(Color color, int x, int y, int width, int height, double rotation) {
        imageTools.fillRect(color, x, y, width, height, rotation);
        return this;
    }
    
    public Image drawRoundRect(Color color, int x, int y, int width, int height, int arcWidth, int arcHeight) {
        imageTools.drawRoundRect(color, x, y, width, height, arcWidth, arcHeight);
        return this;
    }
    
    public Image drawRoundRect(Color color, int x, int y, int width, int height, int arcWidth, int arcHeight, double rotation) {
        imageTools.drawRoundRect(color, x, y, width, height, arcWidth, arcHeight, rotation);
        return this;
    }
    
    public Image fillRoundRect(Color color, int x, int y, int width, int height, int arcWidth, int arcHeight) {
        imageTools.fillRoundRect(color, x, y, width, height, arcWidth, arcHeight);
        return this;
    }
    
    public Image fillRoundRect(Color color, int x, int y, int width, int height, int arcWidth, int arcHeight, double rotation) {
        imageTools.fillRoundRect(color, x, y, width, height, arcWidth, arcHeight, rotation);
        return this;
    }

    public Image output(String type) {
        save(getProteu().getOutput(), type);
        return this;
    }

    public Image save(Storage storage, String type) throws IOException {
        String path = FileSystemPath.absoluteFromStorage(getProteu(), storage);
        FileOutputStream fos = new FileOutputStream(path);
        try {
            return save(fos, type);
        } catch (Exception e) {
            throw new ResourceException("_image.save("+ path +", "+ type +")", e);
        } finally {
            fos.close();
        }
    }

    public Image save(final File file, final String type) {
        try {
            imageTools.save(file, type);
        } catch (Exception e) {
            throw new ResourceException("_image.save("+ file.getName() +", "+ type +")", e);
        }
        return this;
    }

    public Image save(final java.io.File file, final String type) {
        try {
            imageTools.save(file, type);
        } catch (Exception e) {
            throw new ResourceException("_image.save("+ file.getName() +", "+ type +")", e);
        }
        return this;
    }

    public Image save(final OutputStream out, final String type) {
        return save((java.io.OutputStream)out, type);
    }

    public Image save(final java.io.OutputStream out, final String type) {
        try {
            imageTools.save(out, type);
        } catch (Exception e) {
            throw new ResourceException("_image.save(...output, "+ type +")", e);
        }
        return this;
    }

    public Image save(final ImageOutputStream out, final String type) {
        try {
            imageTools.save(out, type);
        } catch (Exception e) {
            throw new ResourceException("_image.save(...output, "+ type +")", e);
        }
        return this;
    }

    public java.awt.image.BufferedImage convertRGBAToIndexed() {
        return ImageTools.convertRGBAToIndexed(imageTools.getBufferedImage());
    }

    public java.awt.image.BufferedImage convertRGBAToIndexed(java.awt.image.BufferedImage src) {
        return ImageTools.convertRGBAToIndexed(src);
    }

    public java.awt.image.BufferedImage makeTransparent(int x, int y) {
        return ImageTools.makeTransparent(imageTools.getBufferedImage(), x, y);
    }

    public java.awt.image.BufferedImage makeTransparent(java.awt.image.BufferedImage image, int x, int y) {
        return ImageTools.makeTransparent(image, x, y);
    }

    @Override
    public void close() throws Exception {
        if (imageTools != null) {
            imageTools.close();
            imageTools = null;
        }
    }
}
