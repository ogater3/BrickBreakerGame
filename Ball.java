import java.awt.*;
import javax.swing.*;
import sun.audio.*;
public class Ball
{
    //instance variables
    private int x; //top left x value
    private int y; // top left y value
    private int size; // diameter
    private int dx;
    private int dy;
    private Color color;
    private boolean over;
    private int hit = 0;
    public static int brickBreaks = 0; //number of broken bricks
    public static int score = 0; // score
    private boolean special; //if special brick is hit

    public Ball(int xValue, int yValue, int s, int dxValue, int dyValue, Color c)
    {
        x = xValue;
        y = yValue;
        size = s;
        dx = dxValue;
        dy = dyValue;
        color = c;
        this.over = false;
        this.special = false;
    }

    public Ball()
    {
        x = 250;
        y = 250;
        size = 10;
        dx = 1;
        dy = 1;
        //color = Color.white;
    }

    //accessors and mutators
    public int getX(){return x;}

    public int getY(){return y;}

    public int getSize(){return size;}

    public int getDx(){return dx;}

    public int getDy(){return dy;}

    public Color getColor(){return color;}

    public boolean getOver(){return over;}
    
    public int getHit(){return hit;}
    
    public static int getBrickBreaks(){return brickBreaks;}
    
    public static int getScore(){return score;}
    
    public boolean getSpecial(){return special;}
    

    public void setX(int xValue){x = xValue;}

    public void setY(int yValue){y = yValue;}

    public void setSize(int s){size = s;}

    public void setDx(int dxValue){dx = dxValue;}

    public void setDy(int dyValue){dy = dyValue;}

    public void setColor(Color c){color = c;}

    public void setHit(int h){hit = h;}

    public void setOver(boolean o){over = o;}
    
    public static void setBrickBreaks(int b){brickBreaks = b;}
    
    public static void setScore(int s){score = s;}
    
    public void setSpecial(boolean s){special = s;}

    public void move(Graphics g)
    {
        x += dx;

        //check collison with left and right walls
        if (x < 5)
        {
            x = 5;
            dx = -dx;
        }
        else if (x + size > 895)
        {
            x = 895 - size;
            dx = -dx;
        }

        y += dy;
        //check colison with top wall

        if (y < 5)
        {
            y = 5;
            dy = -dy;
        }

        //check if game is over
        if(y > 900 - size)
        {
            y = 900 - size;
            dy = 0;
            dx = 0;
            over = true;
        }

        draw(g);
    }

    public void draw(Graphics g)
    {
        //g.setColor(color);
        //g.drawOval(x, y, size, size);
        //g.drawImage(image, x, y, size, size, null);
    }

    public void collidesWith(Brick b)
    {
        Rectangle ball = new Rectangle(x, y, size, size);
        Rectangle brick = new Rectangle(b.getX(), b.getY(), b.getWidth(), b.getHeight());
        
        if (b.getType() == 0) // if paddle
        {
            if (ball.intersects(brick))
            {
                b.hit();

                if (x < b.getX() || x + size > b.getX() + b.getWidth())//side collisions
                    dx = -dx;
                else 
                    dy = -dy;
            }
        }
        else if (b.getType() == 1) // if normal brick
        {
            if (ball.intersects(brick))
            {
                b.hit();
                brickBreaks++;
                score += 100;

                if (x < b.getX() || x + size > b.getX() + b.getWidth())//side collisions
                    dx = -dx;
                else 
                    dy = -dy;
            }
        }
        else if (b.getType() == 2) // if hard brick
        {
            if (ball.intersects(brick))
            {
                b.setHits(b.getHits() + 1);
                if (b.getHits() >= 3) 
                {
                    b.hit();
                    setHit(0);
                    brickBreaks++;
                    score += 300;
                }

                if (x < b.getX() || x + size > b.getX() + b.getWidth())//side collisions
                    dx = -dx;
                else 
                    dy = -dy;
            }
        }
        else if (b.getType() == 3) // if special brick
        {
            if (ball.intersects(brick))
            {
                b.hit();
                brickBreaks++;
                score += 500;
                special = true;
                if (x < b.getX() || x + size > b.getX() + b.getWidth())//side collisions
                    dx = -dx;
                else 
                    dy = -dy;
            }
        }
    }
}
