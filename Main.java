import javax.swing.JFrame;

public class Main
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        
        frame.setBounds(0, 0, 900, 928); // 507, 535, 528
        frame.setTitle("Brick Breaker! by Owen Gater");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        BrickBreakerGame game = new BrickBreakerGame();
        frame.add(game);
        
        frame.setVisible(true);
    }
}