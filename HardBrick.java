import java.awt.*;

public class HardBrick extends Brick
{
    //private int hits;
    
    public HardBrick(int x, int y, int width, int height, Image image, int type, int hits)
    {
        super(x, y, width, height, image, type, hits);
        //hits = h;
    }
    
    public void hit()
    {
        setVisible(false);
    }
    
    public void draw(Graphics g)
    {
        g.drawImage(getImage(), getX(), getY(), null);
    }
}