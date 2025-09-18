import java.util.Random;

public class Dice{
    private int value;

    public Dice() {
        this.value = 0;
    }

    public int roll() {
        Random rand = new Random();
        this.value = rand.nextInt(6) + 1;
        return value;
    }

    public int getValue() {
        return value;
    }
}


public class Yahtzee {
    public static void main(String[] args) {
        System.out.println("Hello, Yahtzee!");
    }
}

