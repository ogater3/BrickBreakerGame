import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.*;
public class Brick
{
    //instance vars
    private int x;
    private int y; 
    private int width;
    private int height;
    private Image image;
    private boolean visible;
    private int type;
    private int hits; 
    
    //constructors
    public Brick(int x, int y, int width, int height, Image image, int t, int h)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.visible = true;
        type = t;
        hits = h;
    }
    
    //accessors
    public int getX(){return x;}
    public int getY(){return y;}
    public int getWidth(){return width;}
    public int getHeight(){return height;}
    public Image getImage(){return image;}
    public boolean isVisible(){return visible;}
    public int getType(){return type;}
    public int getHits(){return hits;}
    
    
    //mutators
    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}
    public void setWidth(int width) {this.width = width;}
    public void setHeight(int height) {this.height = height;}
    public void setImage(Image image) {this.image = image;}
    public void setVisible(boolean v) {visible = v;}
    public void setType(int t) {type = t;}
    public void setHits(int h) {hits = h;}
    
    
    public void draw(Graphics g)
    {
        g.drawImage(image, x, y, null);
    }
    
    public void hit()
    {
        visible = false;
    }
}