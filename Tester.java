import java.util.HashMap;

public class Tester {

    private static HashMap<String, Integer> holder;

    public static HashMap<String, Integer> getHashMap() {
        return holder;
    }

    public static void main(String[] args) {
        int repetitions = 100;
        int[] nodeLimits = { 1, 500, 1000, 1250, 1750, 2500, 3250, 5000, 5250, 6000, 7500, 10000, 15000, 20000, 22500 };
        HashMap<Integer, Integer> numOfNodeAH1 = new HashMap<>();
        HashMap<Integer, Integer> numOfNodeAH2 = new HashMap<>();
        HashMap<Integer, Integer> numOfNodeBeam = new HashMap<>();
        HashMap<Integer, Integer> numSuccessNodeAH1 = new HashMap<>();
        HashMap<Integer, Integer> numSuccessNodeAH2 = new HashMap<>();
        HashMap<Integer, Integer> numSuccessNodeBeam = new HashMap<>();

        for (int nodeLimit : nodeLimits) {
            int totalNodesAH1 = 0;
            int totalNodesAH2 = 0;
            int totalNodesBeam = 0;
            int totalSuccessAH1 = 0;
            int totalSuccessAH2 = 0;
            int totalSuccessBeam = 0;

            for (int i = 0; i < repetitions; i++) {
                new EightPuzzle();
                EightPuzzle.randomizeState(100);
                try {
                    EightPuzzle.maxNodeSearched(nodeLimit);

                    EightPuzzle.searchAStar();
                    totalNodesAH1 += EightPuzzle.getNodeNum();
                    totalSuccessAH1++;
                } catch (Exception e) {
                }
            }
            for (int i = 0; i < repetitions; i++) {
                new EightPuzzle();
                EightPuzzle.randomizeState(100);
                try {
                    EightPuzzle.maxNodeSearched(nodeLimit);

                    EightPuzzle.searchH2AStar();
                    totalNodesAH2 += EightPuzzle.getNodeNum();
                    totalSuccessAH2++;
                } catch (Exception e) {
                }
            }
            for (int i = 0; i < repetitions; i++) {
                new EightPuzzle();
                EightPuzzle.randomizeState(100);
                try {
                    EightPuzzle.maxNodeSearched(nodeLimit);
                    EightPuzzle.localBeamSearch(4);
                    totalNodesBeam += EightPuzzle.getNodeNum();
                    totalSuccessBeam++;
                } catch (Exception e) {
                }
            }

            numOfNodeAH1.put(nodeLimit, totalNodesAH1);
            numOfNodeAH2.put(nodeLimit, totalNodesAH2);
            numOfNodeBeam.put(nodeLimit, totalNodesBeam);
            numSuccessNodeAH1.put(nodeLimit, totalSuccessAH1);
            numSuccessNodeAH2.put(nodeLimit, totalSuccessAH2);
            numSuccessNodeBeam.put(nodeLimit, totalSuccessBeam);
        }

        System.out.println("Number of Nodes for A*H1: " + numOfNodeAH1);
        System.out.println("Number of Nodes for A*H2: " + numOfNodeAH2);
        System.out.println("Number of Nodes for Beam Search: " + numOfNodeBeam);
        System.out.println("Number of successful for A*H1: " + numSuccessNodeAH1);
        System.out.println("Number of successful for A*H2: " + numSuccessNodeAH2);
        System.out.println("Number of successful for Beam Search: " + numSuccessNodeBeam);
    }

}
