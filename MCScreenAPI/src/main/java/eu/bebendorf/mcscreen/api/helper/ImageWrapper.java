package eu.bebendorf.mcscreen.api.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ImageWrapper {

    private ColorModel colorModel;
    private WritableRaster raster;

    public ImageWrapper(BufferedImage image){
        this.colorModel = image.getColorModel();
        this.raster = image.getRaster();
    }

    public ImageWrapper(int width, int height){
        this(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
    }

    public int getWidth(){
        return raster.getWidth();
    }

    public int getHeight(){
        return raster.getHeight();
    }

    public void setPixelColor(int x,int y,int r,int g,int b,int a){
        raster.setPixel(x,y,new int[]{r,g,b,a});
    }

    private int[] getPixelColor(int x,int y){
        return raster.getPixel(x,y,new int[4]);
    }

    public WrappedPixel getPixel(int x,int y){
        return new WrappedPixel(x,y);
    }

    public Image getImage(){
        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
    }

    public void write(File file){
        try {
            ImageIO.write((RenderedImage) getImage(),"PNG",file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ImageWrapper read(File file) {
        try {
            return new ImageWrapper(ImageIO.read(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void forEach(PixelIterator iterator){
        for(int x=0;x<getWidth();x++)
            for(int y=0;y<getHeight();y++)
                iterator.accept(getPixel(x,y));
    }

    public interface PixelIterator {
        void accept(WrappedPixel pixel);
    }

    @AllArgsConstructor
    @Getter
    public class WrappedPixel {
        private int x, y;
        public int[] getRGBA(){
            return getPixelColor(x,y);
        }
        public int[] getRGB(){
            return Arrays.copyOfRange(getRGBA(),0,3);
        }
        public int getAlpha(){
            return getRGBA()[3];
        }
        public int getRed(){
            return getRGBA()[0];
        }
        public int getGreen(){
            return getRGBA()[1];
        }
        public int getBlue(){
            return getRGBA()[2];
        }
        public void setRGBA(int r,int g,int b,int a){
            setPixelColor(x,y,r,g,b,a);
        }
        public void setRGBA(int[] rgba){
            setRGBA(rgba[0],rgba[1],rgba[2],rgba[3]);
        }
        public void setRGB(int r,int g,int b){
            setRGBA(r,g,b,getAlpha());
        }
        public void setRGB(int[] rgb){
            setRGB(rgb[0],rgb[1],rgb[2]);
        }
        public void setAlpha(int a){
            int[] rgba = getRGBA();
            rgba[3] = a;
            setRGBA(rgba);
        }
        public void setRed(int r){
            int[] rgba = getRGBA();
            rgba[0] = r;
            setRGBA(rgba);
        }
        public void setGreen(int g){
            int[] rgba = getRGBA();
            rgba[1] = g;
            setRGBA(rgba);
        }
        public void setBlue(int b){
            int[] rgba = getRGBA();
            rgba[2] = b;
            setRGBA(rgba);
        }
    }

}