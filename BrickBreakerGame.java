import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.*;
import sun.audio.*;
import java.util.ArrayList;
import java.awt.Font;
import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BrickBreakerGame extends JPanel implements KeyListener, ActionListener
{
    private Ball ball;
    private Timer timer;
    private boolean play;
    private Brick[][] bricks;
    private Paddle paddle;
    private ArrayList<Ball> balls = new ArrayList<Ball>();
    private int screen = 1;
    private boolean done = true;
    private int previousScore = 0;
    private int bestScore;
    private int paddleTimer = 1000;
    private int currentX;
    private String name;
    private BufferedReader br;

    private boolean[] keys = new boolean[200];

    //images
    private Image bg;
    private Image lvl2;
    private Image lvl3;
    private Image walls;
    private Image brick;
    private Image paddleImage;
    private Image mushroom;
    private Image star;
    private Image hardBrick;
    private Image specialBrick;
    private Image title;
    private Image loss;
    private Image win;
    private Image winlvl2;
    private Image winlvl3;

    AudioStream theme; // mario theme played throughout
    AudioStream end; // mario dying noise
    AudioStream complete; // mario level complete noise

    private int ballX = (int)(3 * Math.random() + 3); //random side to side speed
    private int changer = (int)(2 * Math.random()); //flip a coin
    private int ballY = (int)(-1 *Math.random() - 1);//random vertical angle

    public void randomBall(int changer) // changes direction ball starts
    {
        if (changer == 1)//change direction ball starts
        {
            ballX *= -1;
            ballY = ballY * -1;
        }
    }

    public BrickBreakerGame()
    {
        randomBall(changer);

        balls.add(new Ball(440, 600, 30, ballX, ballY, Color.black));        
        paddle = new Paddle(450, 840, 150, 50, brick, 0, 0, 5);//130, 50, brick, 0, 0, 40

        bricks = new Brick[5][9];
        for(int i = 0; i < bricks.length; i++)
        {
            for (int j = 0; j < bricks[i].length; j++)
            {
                int random = (int)(100 * Math.random() + 1);

                if (random > 15)
                    bricks[i][j] = new Brick((50 + j * 90), (50 + i * 90), 75, 75, brick, 1, 0);
                else if (random < 10)
                    bricks[i][j] = new SpecialBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 3, 0);
                else 
                    bricks[i][j] = new HardBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 2, 0);
            }
        }

        addKeyListener(this);        
        timer = new Timer(10, this); 
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer.start();
        play = false;

        try
        {
            bg = ImageIO.read(new File("Background.jpg"));
            lvl2 = ImageIO.read(new File("Level 2.png"));
            lvl3 = ImageIO.read(new File("Level 3.jpg"));
            walls = ImageIO.read(new File("Walls.jpg")); 
            brick = ImageIO.read(new File("Brick.png")); 
            paddleImage = ImageIO.read(new File("Paddle.png")); 
            theme = new AudioStream(new FileInputStream("Mario Theme.wav"));
            end = new AudioStream(new FileInputStream("Mario Dying.wav"));
            mushroom = ImageIO.read(new File("Ball copy.png")); 
            star = ImageIO.read(new File("Star.png")); 
            hardBrick = ImageIO.read(new File("Hard Brick.png")); 
            specialBrick = ImageIO.read(new File("Special Brick.png")); 
            title = ImageIO.read(new File("Title.jpg")); 
            loss = ImageIO.read(new File("Loss.jpg")); 
            win = ImageIO.read(new File("Win.jpg")); 
            winlvl2 = ImageIO.read(new File("Winlvl2.jpg")); 
            winlvl3 = ImageIO.read(new File("Winlvl3.jpg")); 
            complete = new AudioStream(new FileInputStream("Level Complete.wav"));

            FileReader Scores = new FileReader("scores.txt");
            br = new BufferedReader(Scores);
            if (br.ready()) {
                bestScore = Integer.parseInt(br.readLine());
            }
        }
        catch (IOException e)
        {  
        }

        mushroom = mushroom.getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        paddleImage = paddleImage.getScaledInstance(150, 50, Image.SCALE_DEFAULT);
        lvl2 = lvl2.getScaledInstance(880, 890, Image.SCALE_DEFAULT);

        play(theme);

    }

    public void paint(Graphics g)
    {
        if (screen == 1) // start screen
        {
            startScreen(g);
        }
        else if (screen == 2)//lvl 1
        {
            drawGame(g);
        }
        else if (screen == 3)//loss screen
        {
            delay(250000);
            drawLoss(g);
        }
        else if (screen == 4)//win lvl1 screen
        {
            delay(250000);
            drawWin(g);
        }
        else if (screen == 5)//lvl 2
        {
            drawLevel2(g);
        }
        else if (screen == 6)//win lvl2 screen
        {
            delay(250000);
            drawWin2(g);
        }
        else if (screen == 7)//lvl 3
        {
            drawLevel3(g);
        }
        else if (screen == 8)//win lvl3 screen
        {
            delay(250000);
            drawWin3(g);
        }
    }

    public void startScreen(Graphics g)
    {
        g.drawImage(title, 0, 0, null);
        drawScoreTitle(g);

    }

    public void drawGame(Graphics g)
    {
        //draw walls and background
        g.drawImage(walls, 0, 0, null);
        g.drawImage(bg, 10, 10, null);

        //draw all balls in array
        for (Ball b : balls)
        {
            if (b == balls.get(0))//original ball
                g.drawImage(mushroom, b.getX(), b.getY(), null);
            else //special balls
                g.drawImage(star, b.getX(), b.getY(), null);
            b.move(g);
        }

        //draw 2D array of bricks
        for(int i = 0; i < bricks.length; i++)
        {
            for (int j = 0; j < bricks[i].length; j++)
            {
                if (bricks[i][j].isVisible() == true)
                {
                    if (bricks[i][j].getType() == 1) // normal brick
                    {
                        bricks[i][j].draw(g);
                        g.drawImage(brick, bricks[i][j].getX(), bricks[i][j].getY(), null);

                        for (int p = 0; p < balls.size(); p++)
                        {
                            balls.get(p).collidesWith(bricks[i][j]);
                        }
                    }
                    else if (bricks[i][j].getType() == 2) // hard brick
                    {
                        bricks[i][j].draw(g);
                        g.drawImage(hardBrick, bricks[i][j].getX(), bricks[i][j].getY(), null);

                        for (int p = 0; p < balls.size(); p++)
                        {
                            balls.get(p).collidesWith(((HardBrick)bricks[i][j]));
                        }
                    }
                    else if (bricks[i][j].getType() == 3) // special brick
                    {
                        bricks[i][j].draw(g);
                        g.drawImage(specialBrick, bricks[i][j].getX(), bricks[i][j].getY(), null);
                        for (int p = 0; p < balls.size(); p++)
                        {
                            balls.get(p).collidesWith(((SpecialBrick)bricks[i][j]));

                            if (balls.get(p).getSpecial() == true)//add new random ball
                            {
                                int coin  = (int)(4 * Math.random() + 1);
                                if (coin == 1 || coin == 4) //new ball
                                {
                                    int randomX = (int)(8 * Math.random() + 2);
                                    int randomY = (int)(-1 *Math.random() - 1);
                                    balls.add(new Ball(400, 500, 30, randomX, randomY, Color.black));
                                }
                                else if (coin == 2 || coin == 3)//bigger paddle
                                {
                                    currentX = paddle.getX();
                                    paddleImage = paddleImage.getScaledInstance(200, 50, Image.SCALE_DEFAULT);
                                    paddle = new Paddle(currentX, paddle.getY(), 200, 50, paddleImage, 0, 0, 5);
                                    paddleTimer = 1000;
                                }
                                balls.get(p).setSpecial(false);
                            }
                        }
                    }
                }
            }
        }

        //draw paddle
        paddle.draw(g);
        g.drawImage(paddleImage, paddle.getX(), paddle.getY(), null);
        for (Ball b : balls)
            b.collidesWith(paddle);

        drawScore(g);
        if (paddle.getWidth() > 150 || paddle.getWidth() < 150)
            drawTimer(g);
    }

    public void drawLevel2(Graphics g)
    {
        //draw walls and background
        g.drawImage(walls, 0, 0, null);
        g.drawImage(lvl2, 10, 10, null);

        //draw all balls in array
        for (Ball b : balls)
        {
            if (b == balls.get(0))//original ball
                g.drawImage(mushroom, b.getX(), b.getY(), null);
            else //special balls
                g.drawImage(star, b.getX(), b.getY(), null);
            b.move(g);
        }

        //draw 2D array of bricks
        for(int i = 0; i < bricks.length; i++)
        {
            for (int j = 0; j < bricks[i].length; j++)
            {
                if (bricks[i][j].isVisible() == true)
                {
                    if (bricks[i][j].getType() == 1) // normal brick
                    {
                        bricks[i][j].draw(g);
                        g.drawImage(brick, bricks[i][j].getX(), bricks[i][j].getY(), null);

                        for (int p = 0; p < balls.size(); p++)
                        {
                            balls.get(p).collidesWith(bricks[i][j]);
                        }
                    }
                    else if (bricks[i][j].getType() == 2) // hard brick
                    {
                        bricks[i][j].draw(g);
                        g.drawImage(hardBrick, bricks[i][j].getX(), bricks[i][j].getY(), null);

                        for (int p = 0; p < balls.size(); p++)
                        {
                            balls.get(p).collidesWith(((HardBrick)bricks[i][j]));
                        }
                    }
                    else if (bricks[i][j].getType() == 3) // hard brick
                    {
                        bricks[i][j].draw(g);
                        g.drawImage(specialBrick, bricks[i][j].getX(), bricks[i][j].getY(), null);
                        for (int p = 0; p < balls.size(); p++)
                        {
                            balls.get(p).collidesWith(((SpecialBrick)bricks[i][j]));

                            if (balls.get(p).getSpecial() == true)//add new random ball
                            {
                                int coin  = (int)(3 * Math.random() + 1);
                                if (coin == 1) //new ball
                                {
                                    int randomX = (int)(8 * Math.random() + 2);
                                    int randomY = (int)(-1 *Math.random() - 1);
                                    balls.add(new Ball(400, 500, 30, randomX, randomY, Color.black));
                                }
                                else if (coin == 2)//bigger paddle
                                {
                                    currentX = paddle.getX();
                                    paddleImage = paddleImage.getScaledInstance(200, 50, Image.SCALE_DEFAULT);
                                    paddle = new Paddle(currentX, paddle.getY(), 200, 50, paddleImage, 0, 0, 5);
                                    paddleTimer = 1000;
                                }
                                else//smaller paddle
                                {
                                    currentX = paddle.getX();
                                    paddleImage = paddleImage.getScaledInstance(100, 50, Image.SCALE_DEFAULT);
                                    paddle = new Paddle(currentX, paddle.getY(), 100, 50, paddleImage, 0, 0, 5);
                                    paddleTimer = 1000;
                                }
                                balls.get(p).setSpecial(false);
                            }
                        }
                    }
                }
            }
        }

        //draw paddle
        paddle.draw(g);
        g.drawImage(paddleImage, paddle.getX(), paddle.getY(), null);
        for (Ball b : balls)
            b.collidesWith(paddle);

        drawScore(g);
        if (paddle.getWidth() > 150 || paddle.getWidth() < 150)
            drawTimer(g);
    }

    public void drawLevel3(Graphics g)
    {
        //draw walls and background
        g.drawImage(walls, 0, 0, null);
        g.drawImage(lvl3, 10, 10, null);

        //draw all balls in array
        for (Ball b : balls)
        {
            if (b == balls.get(0))//original ball
                g.drawImage(mushroom, b.getX(), b.getY(), null);
            else //special balls
                g.drawImage(star, b.getX(), b.getY(), null);
            b.move(g);
        }

        //draw 2D array of bricks
        for(int i = 0; i < bricks.length; i++)
        {
            for (int j = 0; j < bricks[i].length; j++)
            {
                if (bricks[i][j].isVisible() == true)
                {
                    if (bricks[i][j].getType() == 1) // normal brick
                    {
                        bricks[i][j].draw(g);
                        g.drawImage(brick, bricks[i][j].getX(), bricks[i][j].getY(), null);

                        for (int p = 0; p < balls.size(); p++)
                        {
                            balls.get(p).collidesWith(bricks[i][j]);
                        }
                    }
                    else if (bricks[i][j].getType() == 2) // hard brick
                    {
                        bricks[i][j].draw(g);
                        g.drawImage(hardBrick, bricks[i][j].getX(), bricks[i][j].getY(), null);

                        for (int p = 0; p < balls.size(); p++)
                        {
                            balls.get(p).collidesWith(((HardBrick)bricks[i][j]));
                        }
                    }
                    else if (bricks[i][j].getType() == 3) // hard brick
                    {
                        bricks[i][j].draw(g);
                        g.drawImage(specialBrick, bricks[i][j].getX(), bricks[i][j].getY(), null);
                        for (int p = 0; p < balls.size(); p++)
                        {
                            balls.get(p).collidesWith(((SpecialBrick)bricks[i][j]));

                            if (balls.get(p).getSpecial() == true)//add new random ball
                            {
                                int coin  = (int)(3 * Math.random() + 1);
                                if (coin == 1) //new ball
                                {
                                    int randomX = (int)(8 * Math.random() + 2);
                                    int randomY = (int)(-1 *Math.random() - 1);
                                    balls.add(new Ball(400, 500, 30, randomX, randomY, Color.black));
                                }
                                /*else if (coin == 2)//bigger paddle
                                {
                                currentX = paddle.getX();
                                paddleImage = paddleImage.getScaledInstance(200, 50, Image.SCALE_DEFAULT);
                                paddle = new Paddle(currentX, paddle.getY(), 200, 50, paddleImage, 0, 0, 5);
                                }*/
                                else//smaller paddle
                                {
                                    currentX = paddle.getX();
                                    paddleImage = paddleImage.getScaledInstance(100, 50, Image.SCALE_DEFAULT);
                                    paddle = new Paddle(currentX, paddle.getY(), 100, 50, paddleImage, 0, 0, 5);
                                    paddleTimer = 1000;
                                }
                                balls.get(p).setSpecial(false);
                            }
                        }
                    }
                }
            }
        }

        //draw paddle
        paddle.draw(g);
        g.drawImage(paddleImage, paddle.getX(), paddle.getY(), null);
        for (Ball b : balls)
            b.collidesWith(paddle);

        drawScore(g);
        if (paddle.getWidth() > 150 || paddle.getWidth() < 150)
            drawTimer(g);
    }

    public void drawLoss(Graphics g)
    {
        g.drawImage(loss, 0, 0, null);
        drawScoreLoss(g);
    }

    public void drawWin(Graphics g)//after lvl1
    {
        g.drawImage(win, 0, 0, null);
        drawScoreWin(g);
    }

    public void drawWin2(Graphics g)//after lvl 2
    {
        g.drawImage(winlvl2, 0, 0, null);
        drawScoreWin(g);
    }

    public void drawWin3(Graphics g)//after lvl 3
    {
        g.drawImage(winlvl3, 0, 0, null);
        drawScoreWin(g);
    }

    public void drawScore(Graphics g) //while playing
    {
        g.setColor(Color.white);

        String score = "" + Ball.getScore();
        Font stringFont = new Font("SansSerif", Font.BOLD, 18 );
        g.setFont(stringFont);
        g.drawString("Score: " + score, 750, 35);
    }

    public void drawScoreLoss(Graphics g) //score at loss screen
    {
        g.setColor(Color.white);

        String score = "" + Ball.getScore();
        String best = "" + bestScore;

        Font stringFont = new Font("SansSerif", Font.BOLD, 25);
        g.setFont(stringFont);
        g.drawString("Score: " + score, 30, 475);

        g.drawString("High Score: " + best, 30, 525);
    }

    public void drawScoreWin(Graphics g) //score at win screen
    {
        g.setColor(Color.white);

        String score = "" + Ball.getScore();
        String best = "" + bestScore;

        Font stringFont = new Font("SansSerif", Font.BOLD, 25);
        g.setFont(stringFont);
        g.drawString("Score: " + score, 550, 70);

        g.drawString("High Score: " + best, 550, 120);
    }

    public void drawScoreTitle(Graphics g) //score at title screen
    {
        g.setColor(Color.black);

        String ps = "" + previousScore;
        String best = "" + bestScore;

        Font stringFont = new Font("SansSerif", Font.BOLD, 25);
        g.setFont(stringFont);

        g.drawString("Your previous score was: ", 50, 400);
        g.drawString(ps, 50, 450);

        g.drawString("The high score is: ", 600, 400);
        g.drawString(best, 600, 450);
    }

    public void delay(int amount) // delay for win screen
    {
        for (int i = 1; i <= amount; i++)
            System.out.println(i);
    }

    public void drawTimer(Graphics g)
    {
        g.setColor(Color.white);

        String timer = "" + (paddleTimer / 100.0);
        Font stringFont = new Font("SansSerif", Font.BOLD, 18 );
        g.setFont(stringFont);
        g.drawString("Timer: " + timer + "s", 25, 35);
    }

    public void play(AudioStream song)
    {
        AudioPlayer.player.start(song);
    }

    public void stop(AudioStream song)
    {
        AudioPlayer.player.stop(song);
    }

    public void WriteFile(String strFile, String strData)
    {
        try (BufferedWriter bufwriter = new BufferedWriter(new FileWriter(strFile, true)))
        {
            bufwriter.write(strData);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void ReadFile(String strFile)
    {
        String strBuffer;
        try (BufferedReader buffRead = new BufferedReader(new FileReader(strFile)))
        {
            while ((strBuffer = buffRead.readLine()) != null)
            {
                System.out.println(strBuffer);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void reset()
    {
        for (Ball b : balls)           
            b.setOver(false);

        //reset brick array
        bricks = new Brick[5][9];
        for(int i = 0; i < bricks.length; i++)
        {
            for (int j = 0; j < bricks[i].length; j++)
            {
                int random = (int)(100 * Math.random() + 1);

                if (random > 60)//60
                    bricks[i][j] = new Brick((50 + j * 90), (50 + i * 90), 75, 75, brick, 1, 0);
                else if (random < 5)
                    bricks[i][j] = new SpecialBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 3, 0);
                else 
                    bricks[i][j] = new HardBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 2, 0);
            }
        }
        randomBall(changer);
        //reset ball
        ballX = (int)(8 * Math.random() + 2); //random side to side speed
        ballY = (int)(-1 *Math.random() - 1);//random vertical angle

        balls = new ArrayList<Ball>();
        Ball newBall = new Ball(440, 600, 30, ballX, ballY, Color.black);
        balls.add(newBall);

        Ball.setBrickBreaks(0);

        paddleImage = paddleImage.getScaledInstance(150, 50, Image.SCALE_DEFAULT);
        paddle = new Paddle(450, 840, 150, 50, paddleImage, 0, 0, 5); //reset paddle 
    }

    public void reset2()
    {
        for (Ball b : balls)           
            b.setOver(false);

        //reset brick array
        bricks = new Brick[6][9];
        for(int i = 0; i < bricks.length; i++)
        {
            for (int j = 0; j < bricks[i].length; j++)
            {
                int random = (int)(100 * Math.random() + 1);

                if (random > 100)
                    bricks[i][j] = new Brick((50 + j * 90), (50 + i * 90), 75, 75, brick, 1, 0);
                else if (random < 5)
                    bricks[i][j] = new SpecialBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 3, 0);
                else 
                    bricks[i][j] = new HardBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 2, 0);
            }
        }
        randomBall(changer);
        //reset ball
        ballX = (int)(3 * Math.random() + 6); //random side to side speed
        ballY = (int)(-1 *Math.random() - 1);//random vertical angle

        balls = new ArrayList<Ball>();
        Ball newBall = new Ball(440, 600, 30, ballX, ballY, Color.black);
        balls.add(newBall);

        Ball.setBrickBreaks(0);

        paddleImage = paddleImage.getScaledInstance(150, 50, Image.SCALE_DEFAULT);
        paddle = new Paddle(450, 840, 150, 50, paddleImage, 0, 0, 5); //reset paddle 
    }

    public boolean Win()//checks if all bricks are gone lvl 1
    {
        if (Ball.getBrickBreaks() >= 45)
        {
            for(int i = 0; i < bricks.length; i++)
            {
                for (int j = 0; j < bricks[i].length; j++)
                {
                    bricks[i][j].setVisible(false);
                }
            }

            screen = 4; 
            reset();
            return true;
        }
        return false;
    }

    public boolean Win2()//checks if all bricks are gone lvl 2
    {
        if (Ball.getBrickBreaks() >= 45)
        {
            for(int i = 0; i < bricks.length; i++)
            {
                for (int j = 0; j < bricks[i].length; j++)
                {
                    bricks[i][j].setVisible(false);
                }
            }

            screen = 6; 
            reset();
            return true;
        }
        return false;
    }

    public boolean Win3()//checks if all bricks are gone lvl 3
    {
        if (Ball.getBrickBreaks() >= 45)
        {
            for(int i = 0; i < bricks.length; i++)
            {
                for (int j = 0; j < bricks[i].length; j++)
                {
                    bricks[i][j].setVisible(false);
                }
            }

            screen = 8; 
            reset2();
            return true;
        }
        return false;
    }

    public void update() //for paddle 
    {
        if(keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]){
            paddle.moveLeft();
            if (paddle.getX() <= 10)
            {
                paddle.setX(10);
            }
        }

        if(keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]){
            paddle.moveRight();
            if (paddle.getX() + paddle.getWidth() >= 890)
            {
                paddle.setX(890 - paddle.getWidth());
            }
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if (play)
        {
            repaint();
        }
        
        //paddle size changing when getting a powerup

        if (paddle.getWidth() > 150 && paddleTimer > 0)
        {
            paddleTimer--;
        }

        if (paddle.getWidth() > 150 && paddleTimer == 0)
        {
            currentX = paddle.getX();
            paddleImage = paddleImage.getScaledInstance(150, 50, Image.SCALE_DEFAULT);
            paddle = new Paddle(currentX, 840, 150, 50, paddleImage, 0, 0, 5);
            paddleTimer = 1000;
        }

        if (paddle.getWidth() < 130 && paddleTimer > 0)
        {
            paddleTimer--;
        }

        if (paddle.getWidth() < 130 && paddleTimer == 0)
        {
            currentX = paddle.getX();
            paddleImage = paddleImage.getScaledInstance(150, 50, Image.SCALE_DEFAULT);
            paddle = new Paddle(currentX, 840, 150, 50, paddleImage, 0, 0, 5);
            paddleTimer = 1000;
        }
        
        //if game is over
        if (screen == 2 && balls.get(0).getOver() == true || screen == 5 && balls.get(0).getOver() == true || screen == 7 && balls.get(0).getOver() == true)
        {
            try
            {
                end = new AudioStream(new FileInputStream("Mario Dying.wav"));
            }
            catch (IOException j)
            {  
            }
            stop(theme);
            play(end);
            for (int r = 1; r <= balls.size() - 1; r++)
                balls.remove(r);
        }

        if (balls.get(0).getOver() == true) // ball hits ground
        {
            stop(theme);
            play(end);
            for (int r = 1; r <= balls.size() - 1; r++)
                balls.remove(r);

            screen = 3;
            repaint();
        }

        for(int i = 0; i < bricks.length; i++)
        {
            for(int j = 0; j < bricks[i].length; j++)
            {
                if (bricks[i][j].isVisible() == true) //if brick is visible
                    done = false; //game not done
            }
        }

        if (screen == 2 && Win() == true)//lvl 1 win
        {
            stop(theme);
            Win();
            try
            {
                complete = new AudioStream(new FileInputStream("Level Complete.wav"));
            }
            catch (IOException j)
            {  
            }
            play(complete);
        }

        if (screen == 5 && Win2() == true)//lvl2 win
        {
            stop(theme);
            Win2();
            try
            {
                complete = new AudioStream(new FileInputStream("Level Complete.wav"));
            }
            catch (IOException j)
            {  
            }
            play(complete);
        }

        if (screen == 7 && Win3() == true)//lvl3 win
        {
            stop(theme);
            Win3();
            try
            {
                complete = new AudioStream(new FileInputStream("Level Complete.wav"));
            }
            catch (IOException j)
            {  
            }
            play(complete);
        }
        update();
    }

    public void keyPressed(KeyEvent e)
    {
        if (screen == 1)//title
        {
            screen = 2;
        }
        else if (screen == 2)//lvl1
        {
            play = true;
            keys[e.getKeyCode()] = true;
        }
        else if (screen == 3)//loss
        {
            screen = 1;

            for (Ball b : balls)           
                b.setOver(false);
            try
            {
                theme = new AudioStream(new FileInputStream("Mario Theme.wav"));
            }
            catch (IOException j)
            {  
            }
            play(theme);

            //reset brick array
            bricks = new Brick[5][9];
            for(int i = 0; i < bricks.length; i++)
            {
                for (int j = 0; j < bricks[i].length; j++)
                {
                    int random = (int)(100 * Math.random() + 1);

                    if (random > 25)
                        bricks[i][j] = new Brick((50 + j * 90), (50 + i * 90), 75, 75, brick, 1, 0);
                    else if (random < 10)
                        bricks[i][j] = new SpecialBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 3, 0);
                    else 
                        bricks[i][j] = new HardBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 2, 0);
                }
            }
            //reset ball
            randomBall(changer);
            ballX = (int)(3 * Math.random() + 3); //random side to side speed
            ballY = (int)(-1 *Math.random() - 1);//random vertical angle

            balls = new ArrayList<Ball>();
            Ball newBall = new Ball(440, 600, 30, ballX, ballY, Color.black);
            balls.add(newBall);

            Ball.setBrickBreaks(0);
            if (Ball.getScore() > bestScore)
                bestScore = Ball.getScore();

            previousScore = Ball.getScore();
            Ball.setScore(0);

            paddleImage = paddleImage.getScaledInstance(150, 50, Image.SCALE_DEFAULT);
            paddle = new Paddle(450, 840, 150, 50, paddleImage, 0, 0, 5); //reset paddle 

            FileWriter scores_file; //initializing FileWriter
            try
            {
                scores_file = new FileWriter("scores.txt");
                // Initializing BufferedWriter
                BufferedWriter scorewrite = new BufferedWriter(scores_file);

                // Use of write() method to write the value in file

                scorewrite.write(Integer.toString(bestScore));

                // For next line
                scorewrite.newLine();

                // flush() method : flushing the stream
                scorewrite.flush();
                // close() method : closing BufferWriter to end operation
                scorewrite.close();
                System.out.println("Written successfully");
            }
            catch (IOException except)
            {
                except.printStackTrace();
            }
        }
        else if (screen == 4)//win1
        {
            screen = 5;

            for (Ball b : balls)           
                b.setOver(false);

            try
            {
                theme = new AudioStream(new FileInputStream("Mario Theme.wav"));
            }
            catch (IOException j)
            {  
            }
            play(theme);

            //reset brick array
            bricks = new Brick[5][9];
            for(int i = 0; i < bricks.length; i++)
            {
                for (int j = 0; j < bricks[i].length; j++)
                {
                    int random = (int)(100 * Math.random() + 1);

                    if (random > 45)
                        bricks[i][j] = new Brick((50 + j * 90), (50 + i * 90), 75, 75, brick, 1, 0);
                    else if (random < 5)
                        bricks[i][j] = new SpecialBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 3, 0);
                    else 
                        bricks[i][j] = new HardBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 2, 0);
                }
            }
            randomBall(changer);
            //reset ball
            ballX = (int)(5 * Math.random() + 4); //random side to side speed
            ballY = (int)(-1 *Math.random() - 1);//random vertical angle

            balls = new ArrayList<Ball>();
            Ball newBall = new Ball(440, 600, 30, ballX, ballY, Color.black);
            balls.add(newBall);

            Ball.setBrickBreaks(0);

            paddleImage = paddleImage.getScaledInstance(150, 50, Image.SCALE_DEFAULT);
            paddle = new Paddle(450, 840, 150, 50, paddleImage, 0, 0, 5); //reset paddle 
        }
        else if (screen == 5)//level 2
        {
            play = true;
            keys[e.getKeyCode()] = true;
        }
        else if (screen == 6)//win2
        {
            screen = 7;

            for (Ball b : balls)           
                b.setOver(false);

            try
            {
                theme = new AudioStream(new FileInputStream("Mario Theme.wav"));
            }
            catch (IOException j)
            {  
            }
            play(theme);

            //reset brick array
            bricks = new Brick[5][9];
            for(int i = 0; i < bricks.length; i++)
            {
                for (int j = 0; j < bricks[i].length; j++)
                {
                    int random = (int)(100 * Math.random() + 1);

                    if (random > 80)
                        bricks[i][j] = new Brick((50 + j * 90), (50 + i * 90), 75, 75, brick, 1, 0);
                    else if (random < 5)
                        bricks[i][j] = new SpecialBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 3, 0);
                    else 
                        bricks[i][j] = new HardBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 2, 0);
                }
            }
            randomBall(changer);
            //reset ball
            ballX = (int)(6 * Math.random() + 5); //random side to side speed
            ballY = (int)(-1 *Math.random() - 1);//random vertical angle

            balls = new ArrayList<Ball>();
            Ball newBall = new Ball(440, 600, 30, ballX, ballY, Color.black);
            balls.add(newBall);

            Ball.setBrickBreaks(0);

            paddle = new Paddle(450, 840, 150, 50, paddleImage, 0, 0, 5); //reset paddle 
        }
        else if (screen == 7)//level 3
        {
            play = true;
            keys[e.getKeyCode()] = true;
        }
        else if (screen == 8)//win3
        {
            screen = 1;

            for (Ball b : balls)           
                b.setOver(false);

            try
            {
                theme = new AudioStream(new FileInputStream("Mario Theme.wav"));
            }
            catch (IOException j)
            {  
            }
            play(theme);

            //reset brick array
            bricks = new Brick[5][9];
            for(int i = 0; i < bricks.length; i++)
            {
                for (int j = 0; j < bricks[i].length; j++)
                {
                    int random = (int)(100 * Math.random() + 1);

                    if (random > 25)
                        bricks[i][j] = new Brick((50 + j * 90), (50 + i * 90), 75, 75, brick, 1, 0);
                    else if (random < 10)
                        bricks[i][j] = new SpecialBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 3, 0);
                    else 
                        bricks[i][j] = new HardBrick((50 + j * 90), (50 + i * 90), 75, 75, hardBrick, 2, 0);
                }
            }
            randomBall(changer);
            //reset ball
            ballX = (int)(3 * Math.random() + 3); //random side to side speed
            ballY = (int)(-1 *Math.random() - 1);//random vertical angle

            balls = new ArrayList<Ball>();
            Ball newBall = new Ball(440, 600, 30, ballX, ballY, Color.black);
            balls.add(newBall);

            Ball.setBrickBreaks(0);
            if (Ball.getScore() > bestScore)
                bestScore = Ball.getScore();

            previousScore = Ball.getScore();
            Ball.setScore(0);
            
            FileWriter scores_file; //initializing FileWriter
            try
            {
                scores_file = new FileWriter("scores.txt");
                // Initializing BufferedWriter
                BufferedWriter scorewrite = new BufferedWriter(scores_file);

                // Use of write() method to write the value in file

                scorewrite.write(Integer.toString(bestScore));

                // For next line
                scorewrite.newLine();

                // flush() method : flushing the stream
                scorewrite.flush();
                // close() method : closing BufferWriter to end operation
                scorewrite.close();
                System.out.println("Written successfully");
            }
            catch (IOException except)
            {
                except.printStackTrace();
            }

            paddle = new Paddle(450, 840, 150, 50, paddleImage, 0, 0, 5); //reset paddle 
        }
        repaint();
    }

    public void keyTyped(KeyEvent e)
    {

    }

    public void keyReleased(KeyEvent e)
    { 
        keys[e.getKeyCode()] = false;
    }
}