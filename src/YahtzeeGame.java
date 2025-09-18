import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class YahtzeeGame {
        private final Dice[] dice = new Dice[5]; 
        private final ArrayList<Integer> keep = new ArrayList<>(); 
        private int turn = 1; 
        private final Scanner scanner;
        private String diceDisplay;
        private ScoreCard ScoreCard; 

        public YahtzeeGame() {
            for (int i = 0; i < 5; i++) {
                dice[i] = new Dice();
            }
            scanner = new Scanner(System.in);
            ScoreCard = new ScoreCard(); 
        }

        public void rollDice() {
            for (int i = 0; i < 5; i++) {
                if (!keep.contains(i)) { 
                    dice[i].roll();
                }
            }
        }

        public void displayDice() {
            System.out.println("Current dice values:");
            for (int i = 0; i < 5; i++) {
                diceDisplay = switch (dice[i].getValue()) {
                    case 1 ->
                        "+-----+\n" +
                        "|     |\n" +
                        "|     |\n" +
                        "|  *  |\n" +
                     "|     |\n" +
                        "+-----+";
                    case 2 ->
                         "+-----+\n" +
                            "| *   |\n" +
                            "|     |\n" +
                            "|   * |\n" +
                            "+-----+";
                    case 3 ->
                         "+-----+\n" +
                            "| *   |\n" +
                            "|  *  |\n" +
                            "|   * |\n" +
                            "+-----+";
                    case 4 ->
                         "+-----+\n" +
                            "| * * |\n" +
                            "|     |\n" +
                            "| * * |\n" +
                            "+-----+";
                    case 5 ->
                         "+-----+\n" +
                            "| * * |\n" +
                            "|  *  |\n" +
                            "| * * |\n" +
                            "+-----+";
                    case 6 ->
                         "+-----+\n" +
                            "| * * |\n" +
                            "| * * |\n" +
                            "| * * |\n" +
                            "+-----+";
                    default -> "/";
                        };
                System.out.println(diceDisplay); 
            }
            System.out.println();
        }

        public void keepDice() {
            keep.clear(); 
            System.out.println("Which dice do you want to keep (1-5)? Enter 0 when done");
            String input = scanner.nextLine();
            while (!input.equals("0")) {
                try {
                    int dieIndex = Integer.parseInt(input) - 1;
                    if (dieIndex >= 0 && dieIndex < 5) {
                            keep.add(dieIndex);
                    } else {
                        System.out.println("Invalid die index.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
                input = scanner.nextLine();
            }
        }

        public boolean hasMoreRolls() {
            return turn < 3;
        }

    public void playTurn() {
        while (!ScoreCard.isFull()) {
            keep.clear(); 
            turn = 1; 
            while (turn <= 3) {
                System.out.println("Turn " + turn);
                rollDice();
                displayDice();
                if (turn == 3 || keep.size() == 5) {
                    break;
                }
                keepDice();
                turn++;
            }
            int[] diceValues = new int[5];
            for (int i = 0; i < 5; i++) {
                diceValues[i] = dice[i].getValue();
            }

            Map<Category, Integer> possible = ScoreCard.calculatePossibleScores(diceValues);
            ScoreCard.printScoreCard(possible);

            System.out.println("Pick a category number");
            String input = scanner.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);   
                if (choice >= 1 && choice < Category.values().length) {
                    ScoreCard.assignScore(choice, possible);
                } else {
                    System.out.println("Invalid category index. Please pick between 1 and " 
                                    + Category.values().length + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
         ScoreCard.FinalScoreCard();
    }
}

