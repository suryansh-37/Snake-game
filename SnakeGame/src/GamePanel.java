package SnakeGame.src;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    int DELAY; // MODIFIED - No longer final, will be set by difficulty
    final int INITIAL_DELAY = 75; // NEW - Store the initial delay
    final int MIN_DELAY = 30; // NEW - Minimum possible delay
    final int MAX_DELAY = 150; // NEW - Maximum possible delay

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    JButton startButton;
    JButton retryButton; // MODIFIED - Declared as instance variable
    JPanel startPanel;
    JPanel gameOverPanel;
    JLabel finalScoreLabel;
    JComboBox<String> difficultyComboBox;
    Color fruitColor;

    GamePanel() {
        random = new Random();
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        setLayout(null);
        createStartPanel();
        createGameOverPanel();
        DELAY = INITIAL_DELAY;
    }

    public void createStartPanel() {
        startPanel = new JPanel();
        startPanel.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        startPanel.setBackground(new Color(0, 0, 0));
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        startPanel.setBorder(BorderFactory.createEmptyBorder(SCREEN_HEIGHT / 4, 0, 0, 0));

        JLabel gameTitleLabel = new JLabel("Snake Game");
        gameTitleLabel.setForeground(new Color(255, 215, 0));
        gameTitleLabel.setFont(new Font("Arial Black", Font.BOLD, 80));
        gameTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel difficultyLabel = new JLabel("Select Difficulty:");
        difficultyLabel.setForeground(Color.white);
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        difficultyLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        String[] difficulties = {"Easy", "Medium", "Hard"};
        difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setSelectedIndex(1);
        difficultyComboBox.setFont(new Font("Arial", Font.PLAIN, 20));
        difficultyComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton = new JButton("Start Game");
        startButton.setForeground(Color.black);
        startButton.setBackground(new Color(124, 252, 0));
        startButton.setFont(new Font("Arial Black", Font.BOLD, 36));
        startButton.setFocusable(false);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> startGame());
        startButton.setBorder(BorderFactory.createRaisedBevelBorder());

        startPanel.add(gameTitleLabel);
        startPanel.add(Box.createVerticalStrut(40));
        startPanel.add(difficultyLabel);
        startPanel.add(difficultyComboBox);
        startPanel.add(Box.createVerticalStrut(30));
        startPanel.add(startButton);
        add(startPanel);
    }

    public void createGameOverPanel() {
        gameOverPanel = new JPanel();
        gameOverPanel.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        gameOverPanel.setBackground(new Color(0, 0, 0, 150));
        gameOverPanel.setLayout(new BoxLayout(gameOverPanel, BoxLayout.Y_AXIS));
        gameOverPanel.setBorder(BorderFactory.createEmptyBorder(SCREEN_HEIGHT / 4, 0, 0, 0));

        JLabel gameOverLabel = new JLabel("Game Over");
        gameOverLabel.setForeground(Color.red);
        gameOverLabel.setFont(new Font("Ink Free", Font.BOLD, 75));
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        finalScoreLabel = new JLabel("Score: 0");
        finalScoreLabel.setForeground(Color.white);
        finalScoreLabel.setFont(new Font("Arial", Font.BOLD, 30));
        finalScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        retryButton = new JButton("Retry");
        retryButton.setForeground(Color.white);
        retryButton.setBackground(new Color(70, 130, 180));
        retryButton.setFont(new Font("Arial", Font.BOLD, 30));
        retryButton.setFocusable(false);
        retryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        retryButton.addActionListener(e -> startGame());

        JButton exitButton = new JButton("Exit");
        exitButton.setForeground(Color.white);
        exitButton.setBackground(new Color(178, 34, 34));
        exitButton.setFont(new Font("Arial", Font.BOLD, 30));
        exitButton.setFocusable(false);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> System.exit(0));

        gameOverPanel.add(gameOverLabel);
        gameOverPanel.add(Box.createVerticalStrut(20));
        gameOverPanel.add(finalScoreLabel);
        gameOverPanel.add(Box.createVerticalStrut(20));
        gameOverPanel.add(retryButton);
        gameOverPanel.add(Box.createVerticalStrut(10));
        gameOverPanel.add(exitButton);
        gameOverPanel.setVisible(false);
        add(gameOverPanel);
    }

    public void startGame() {
        if (startPanel != null && startPanel.isVisible()) {
            startPanel.setVisible(false);
            setGameDifficulty();
            resetGameState();
            running = true;
            timer = new Timer(DELAY, this);
            timer.start();
            requestFocusInWindow();
        } else if (gameOverPanel != null && gameOverPanel.isVisible()) {
            gameOverPanel.setVisible(false);
            resetGameState();
            running = true;
            timer = new Timer(DELAY, this);
            timer.start();
            finalScoreLabel.setText("Score: 0");
            requestFocusInWindow();
            selectNewFruitColor();
        } else if (!running) {
            resetGameState();
            running = true;
            timer = new Timer(DELAY, this);
            timer.start();
            requestFocusInWindow();
            selectNewFruitColor();
        }
    }

    public void resetGameState() {
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        direction = 'R';
        bodyParts = 6;
        applesEaten = 0;
        newApple();
        selectNewFruitColor();
    }

    public void setGameDifficulty() {
        String difficulty = (String) difficultyComboBox.getSelectedItem();
        switch (difficulty) {
            case "Easy":
                DELAY = MAX_DELAY;
                break;
            case "Medium":
                DELAY = INITIAL_DELAY;
                break;
            case "Hard":
                DELAY = MIN_DELAY;
                break;
        }
        if (timer != null) {
            timer.setDelay(DELAY);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            draw(g);
            drawScore(g);
        }
    }

    public void drawScore(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
    }

    public void selectNewFruitColor() {
        Random randomColor = new Random();
        switch (randomColor.nextInt(4)) {
            case 0:
                fruitColor = Color.red;
                break;
            case 1:
                fruitColor = Color.magenta;
                break;
            case 2:
                fruitColor = Color.yellow;
                break;
            case 3:
                fruitColor = Color.orange;
                break;
        }
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(fruitColor);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                    drawSnakeFace(g, x[i], y[i]);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
        }
    }

    public void drawSnakeFace(Graphics g, int headX, int headY) {
        g.setColor(Color.white);

        int eyeSize = UNIT_SIZE / 5;
        int eyeOffset = UNIT_SIZE / 4;
        int mouthSize = UNIT_SIZE / 6;
        int mouthOffset = UNIT_SIZE / 3;

        switch (direction) {
            case 'R':
                g.fillOval(headX + eyeOffset, headY + eyeOffset, eyeSize, eyeSize);
                g.fillOval(headX + eyeOffset, headY + UNIT_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                g.setColor(Color.red);
                g.fillRect(headX + mouthOffset, headY + UNIT_SIZE / 2 - mouthSize / 2, mouthSize, mouthSize);
                break;
            case 'L':
                g.fillOval(headX + UNIT_SIZE - eyeOffset - eyeSize, headY + eyeOffset, eyeSize, eyeSize);
                g.fillOval(headX + UNIT_SIZE - eyeOffset - eyeSize, headY + UNIT_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                g.setColor(Color.red);
                g.fillRect(headX + UNIT_SIZE - mouthOffset - mouthSize, headY + UNIT_SIZE / 2 - mouthSize / 2, mouthSize, mouthSize);
                break;
            case 'U':
                g.fillOval(headX + eyeOffset, headY + eyeOffset, eyeSize, eyeSize);
                g.fillOval(headX + UNIT_SIZE - eyeOffset - eyeSize, headY + eyeOffset, eyeSize, eyeSize);
                g.setColor(Color.red);
                g.fillRect(headX + UNIT_SIZE / 2 - mouthSize / 2, headY + mouthOffset, mouthSize, mouthSize);
                break;
            case 'D':
                g.fillOval(headX + eyeOffset, headY + UNIT_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                g.fillOval(headX + UNIT_SIZE - eyeOffset - eyeSize, headY + UNIT_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                g.setColor(Color.red);
                g.fillRect(headX + UNIT_SIZE / 2 - mouthSize / 2, headY + UNIT_SIZE - mouthOffset - mouthSize, mouthSize, mouthSize);
                break;
        }
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
            selectNewFruitColor();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        if (x[0] < 0) {
            running = false;
        }
        if (x[0] >= SCREEN_WIDTH) {
            running = false;
        }
        if (y[0] < 0) {
            running = false;
        }
        if (y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
            gameOverPanel.setVisible(true);
            finalScoreLabel.setText("Score: " + applesEaten);
            requestFocusInWindow();
        }
    }

    public void gameOver(Graphics g) {
        // We are now using the gameOverPanel, so this method can be empty
    }

    public void resetGame() {
        startGame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (running) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') {
                            direction = 'U';
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') {
                            direction = 'D';
                        }
                        break;
                }
            }
        }
    }
}