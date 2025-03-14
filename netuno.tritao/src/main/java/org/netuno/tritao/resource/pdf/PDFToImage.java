package org.netuno.tritao.resource.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.netuno.psamata.io.File;
import org.netuno.tritao.resource.Storage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface PDFToImage {

    default void toImage(Storage source, Storage destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(source, -1, destinationPath, filePrefixName, fileExtension);
    }

    default void toImage(Storage source, Storage destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(source, -1, -1, destinationPath, filePrefixName, fileExtension, dpi);
    }

    default void toImage(Storage source, int pageNumber, Storage destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(source, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, 300);
    }

    default void toImage(Storage source, int pageNumber, Storage destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(source, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, dpi);
    }

    default void toImage(Storage source, int startPage, int endPage, Storage destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(source.file(), startPage, endPage, destinationPath.folder(), filePrefixName, fileExtension, dpi);
    }

    default void toImage(Storage source, File destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(source, -1, destinationPath, filePrefixName, fileExtension);
    }

    default void toImage(Storage source, File destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(source, -1, -1, destinationPath, filePrefixName, fileExtension, dpi);
    }

    default void toImage(Storage source, int pageNumber, File destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(source, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, 300);
    }

    default void toImage(Storage source, int pageNumber, File destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(source, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, dpi);
    }

    default void toImage(Storage source, int startPage, int endPage, File destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(source.file(), startPage, endPage, destinationPath, filePrefixName, fileExtension, dpi);
    }

    default void toImage(File source, Storage destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(source, -1, destinationPath, filePrefixName, fileExtension);
    }

    default void toImage(File source, Storage destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(source, -1, -1, destinationPath, filePrefixName, fileExtension, dpi);
    }

    default void toImage(File source, int pageNumber, Storage destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(source, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, 300);
    }

    default void toImage(File source, int pageNumber, Storage destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(source, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, dpi);
    }

    default void toImage(File source, int startPage, int endPage, Storage destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(source, startPage, endPage, destinationPath.folder(), filePrefixName, fileExtension, dpi);
    }

    default void toImage(File source, File destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(source, -1, destinationPath, filePrefixName, fileExtension);
    }

    default void toImage(File source, File destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(source, -1, -1, destinationPath, filePrefixName, fileExtension, dpi);
    }

    default void toImage(File source, int pageNumber, File destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(source, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, 300);
    }

    default void toImage(File source, int pageNumber, File destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(source, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, dpi);
    }

    default void toImage(File source, int startPage, int endPage, File destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(source.getFullPath()))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int numberOfPages = document.getNumberOfPages();
            for (int i = (startPage >= 0 ? startPage : 0); i < (endPage >= 0 ? endPage : numberOfPages); ++i) {
                java.io.File outFile = new java.io.File(destinationPath.fullPath(), filePrefixName + "-" + (i + 1) + "." + fileExtension);
                BufferedImage bufImage = pdfRenderer.renderImageWithDPI(i, dpi, fileExtension.equalsIgnoreCase("png") ? ImageType.ARGB : ImageType.RGB);
                ImageIO.write(bufImage, fileExtension, outFile);
            }
        }
    }

    default void toImage(java.io.InputStream in, Storage destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(in, -1, destinationPath, filePrefixName, fileExtension);
    }

    default void toImage(java.io.InputStream in, Storage destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(in, -1, -1, destinationPath, filePrefixName, fileExtension, dpi);
    }

    default void toImage(java.io.InputStream in, int pageNumber, Storage destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(in, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, 300);
    }

    default void toImage(java.io.InputStream in, int pageNumber, Storage destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(in, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, dpi);
    }

    default void toImage(java.io.InputStream in, int startPage, int endPage, Storage destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(in, startPage, endPage, destinationPath.folder(), filePrefixName, fileExtension, dpi);
    }

    default void toImage(java.io.InputStream in, File destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(in, -1, destinationPath, filePrefixName, fileExtension);
    }

    default void toImage(java.io.InputStream in, File destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(in, -1, -1, destinationPath, filePrefixName, fileExtension, dpi);
    }

    default void toImage(java.io.InputStream in, int pageNumber, File destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(in, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, 300);
    }

    default void toImage(java.io.InputStream in, int pageNumber, File destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(in, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, dpi);
    }

    default void toImage(java.io.InputStream in, int startPage, int endPage, File destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        try (PDDocument document = Loader.loadPDF(RandomAccessReadBuffer.createBufferFromStream(in))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int numberOfPages = document.getNumberOfPages();
            for (int i = (startPage >= 0 ? startPage : 0); i < (endPage >= 0 ? endPage : numberOfPages); ++i) {
                java.io.File outFile = new java.io.File(destinationPath.fullPath(), filePrefixName + "-" + (i + 1) + "." + fileExtension);
                BufferedImage bufImage = pdfRenderer.renderImageWithDPI(i, dpi, fileExtension.equalsIgnoreCase("png") ? ImageType.ARGB : ImageType.RGB);
                ImageIO.write(bufImage, fileExtension, outFile);
            }
        }
    }

    default byte[][] toImage(java.io.InputStream in, String fileExtension) throws IOException {
        return toImage(in, -1, fileExtension);
    }

    default byte[][] toImage(java.io.InputStream in, String fileExtension, int dpi) throws IOException {
        return toImage(in, -1, -1, fileExtension, dpi);
    }

    default byte[][] toImage(java.io.InputStream in, int pageNumber, String fileExtension) throws IOException {
        return toImage(in, pageNumber, pageNumber, fileExtension, 300);
    }

    default byte[][] toImage(java.io.InputStream in, int pageNumber, String fileExtension, int dpi) throws IOException {
        return toImage(in, pageNumber, pageNumber, fileExtension, dpi);
    }

    default byte[][] toImage(java.io.InputStream in, int startPage, int endPage, String fileExtension, int dpi) throws IOException {
        try (PDDocument document = Loader.loadPDF(RandomAccessReadBuffer.createBufferFromStream(in))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int numberOfPages = document.getNumberOfPages();
            byte[][] images = new byte[(endPage >= 0 ? endPage : numberOfPages) - (startPage >= 0 ? startPage : 0)][];
            int idx = 0;
            for (int i = (startPage >= 0 ? startPage : 0); i < (endPage >= 0 ? endPage : numberOfPages); ++i) {
                BufferedImage bufImage = pdfRenderer.renderImageWithDPI(i, dpi, fileExtension.equalsIgnoreCase("png") ? ImageType.ARGB : ImageType.RGB);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufImage, fileExtension, baos);
                images[idx] = baos.toByteArray();
                idx++;
            }
            return images;
        }
    }

}
