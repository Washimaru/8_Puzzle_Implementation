public class Main {
    public static void main(String[] args) {
        EightPuzzle testing = new EightPuzzle();
        EightPuzzle testing2 = new EightPuzzle();
        EightPuzzle testing3 = new EightPuzzle();
        EightPuzzle testing4 = new EightPuzzle();

        String filePath1 = "/Users/neverland/Documents/AIProject/untitled/src/P1CommandBeam.txt";
        String filePath2 = "/Users/neverland/Documents/AIProject/untitled/src/P1CommandH2AStar.txt";
        String filePath3 = "/Users/neverland/Documents/AIProject/untitled/src/P1CommandH1AStar.txt";
        String filePath4 = "/Users/neverland/Documents/AIProject/untitled/src/P1CommandTestFail.txt";

        try {
            testing4.commandReading(filePath4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            testing2.commandReading(filePath1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            testing.commandReading(filePath2);
        } catch (Exception e) {
        }

        try {
            testing3.commandReading(filePath3);
        } catch (Exception e) {
        }

    }

}
