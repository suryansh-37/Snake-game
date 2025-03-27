package SnakeGame.src;

// You might integrate food logic directly into GamePanel for simplicity
// This is an example if you wanted a separate Food class
public class Food {
    private int x;
    private int y;
    private int unitSize;

    public Food(int unitSize) {
        this.unitSize = unitSize;
    }

    public void generateNew(int screenWidth, int screenHeight, java.util.Random random) {
        x = random.nextInt((int) (screenWidth / unitSize)) * unitSize;
        y = random.nextInt((int) (screenHeight / unitSize)) * unitSize;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}