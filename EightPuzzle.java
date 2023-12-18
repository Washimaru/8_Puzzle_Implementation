import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import javax.naming.LimitExceededException;

public class EightPuzzle {

    private static final int[][] goalState = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };
    // ArrayList to store all of the information which alternates, before it goes to
    private static Node startNode;
    // private EightPuzzle parentState;
    private static int nodeNum;
    private static int numOfNode;

    /**
     * @param initialState
     */
    public EightPuzzle() {
        startNode = new Node(goalState, 0, 0, null);
        nodeNum = 500;
    }

    public void commandReading(String filepath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(filepath)));

        String line;

        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                if (line.substring(0, 4).equals("move")) {
                    move(line.substring(5));
                } else if (line.equals("printState")) {
                    printState();
                } else if (line.substring(0, 8).equals("setState")) {
                    int[][] myArray = new int[3][3];
                    for (int i = 0; i < myArray.length; i++) {
                        String[] rowLine = line.substring(9).trim().split(" ");
                        for (int j = 0; j < 3; j++) {
                            myArray[i][j] = Character.getNumericValue(rowLine[i].charAt(j));
                        }
                    }
                    setState(myArray);

                } else if (line.startsWith("searchAStar")) {
                    searchAStar();
                } else if (line.startsWith("searchH2AStar")) {
                    searchH2AStar();
                } else if (line.startsWith("randomizeState")) {
                    randomizeState(Integer.parseInt(line.substring(15)));
                } else if (line.startsWith("maxNodeSearched")) {
                    maxNodeSearched(Integer.parseInt(line.substring(16)));
                } else if (line.startsWith("localBeamSearch")) {
                    localBeamSearch(Integer.parseInt(line.substring(16)));
                }
            }

        }

    }

    public static Node getStartNode() {
        return startNode;
    }

    public static int getNodeLimit() {
        return nodeNum;
    }

    public static void setNodeNum(int inputNode) {
        numOfNode = inputNode;
    }

    public static int getNodeNum() {
        return numOfNode;
    }

    /**
     * inputState = input which the method receives to set the state of the puzzle
     * as
     */
    public static void setState(int[][] inputNode) {
        startNode.currentState = inputNode;
    }

    /**
     * No input is taken, and prints out the state
     */
    public static void printState() {
        StringBuilder printingState = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                printingState.append(
                        getStartNode().getCurrentState()[i][j]);
            }
        }
        System.out.println(printingState.toString());
    }

    /**
     * Calculating Heuristic for h1, which is number of misplaced tiles
     */
    private static int calculateHeuristic(int[][] inputState) {
        int num_misplaced = 0;
        if (inputState[0][1] != 1)
            num_misplaced += 1;
        if (inputState[0][2] != 2)
            num_misplaced += 1;
        if (inputState[1][0] != 3)
            num_misplaced += 1;
        if (inputState[1][1] != 4)
            num_misplaced += 1;
        if (inputState[1][2] != 5)
            num_misplaced += 1;
        if (inputState[2][0] != 6)
            num_misplaced += 1;
        if (inputState[2][1] != 7)
            num_misplaced += 1;
        if (inputState[2][2] != 8)
            num_misplaced += 1;
        return num_misplaced;
    }

    /**
     * Calculating the heuristic for h2, which is how far each tile is to the goal
     */
    private static int calculateH2Heuristic(int[][] inputState) {
        int heuristicsValue = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int[] value = correctLocation(inputState[i][j]);
                if (inputState[i][j] != 0) {
                    heuristicsValue = Math.abs(i - value[0]) + Math.abs(j - value[1]);
                }
            }
        }
        return heuristicsValue;
    }

    private static int[] correctLocation(int value) {
        int[] temp = new int[2];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (goalState[i][j] == value) {
                    temp[0] = i;
                    temp[1] = j;
                }
            }
        }
        return temp;
    }

    /**
     * @param inputState
     */
    private static int calculateBeamEval(int[][] inputState) {
        return calculateHeuristic(inputState) + calculateH2Heuristic(inputState);
    }

    /**
     * 
     * @param directionToGo gives an input of which direction the blank tile wants
     *                      to go
     * @return returns true if the direction can go into that direction, else
     *         returns false
     */
    public static int[][] move(String directionToGo) {
        int[][] currentState = getStartNode().getCurrentState();
        // stores current blank tile's location
        int blankRow = -1;
        int blankCol = -1;

        // Find the blank tile's position
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (currentState[i][j] == 0) {
                    blankRow = i;
                    blankCol = j;
                }
            }
        }

        int newRow = blankRow;
        int newCol = blankCol;

        // Determine the new position after the move
        if (directionToGo.equals("up") && blankRow > 0) {
            newRow = blankRow - 1;
        } else if (directionToGo.equals("down") && blankRow < 2) {
            newRow = blankRow + 1;
        } else if (directionToGo.equals("left") && blankCol > 0) {
            newCol = blankCol - 1;
        } else if (directionToGo.equals("right") && blankCol < 2) {
            newCol = blankCol + 1;
        }

        int[][] newState = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                newState[i][j] = currentState[i][j];
            }
        }
        newState[blankRow][blankCol] = currentState[newRow][newCol];
        newState[newRow][newCol] = 0;

        return newState;
    }

    public static int[][] randomizeState(int n) {
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            String[] directions = { "up", "down", "left", "right" };
            int setRandomDirection = rand.nextInt(4);
            int[][] tempState = move(directions[setRandomDirection]);
            if (move(directions[setRandomDirection]) != null) {
                setState(tempState);
            }
        }
        return getStartNode().getCurrentState();
    }

    public static void searchAStar() throws LimitExceededException {
        int count = 0;
        PriorityQueue<Node> openState = new PriorityQueue<>(
                Comparator.comparingInt(b -> b.getCost() + b.getHeuristic()));
        openState.add(new Node(getStartNode().getCurrentState(), 0,
                calculateHeuristic(getStartNode().getCurrentState()),
                null));
        HashSet<String> visitedState = new HashSet<>();
        while (!openState.isEmpty()) {
            count++;
            Node currentNode = openState.poll();
            if (Arrays.deepEquals(currentNode.getCurrentState(), goalState)) {
                setNodeNum(visitedState.size());
                setState(currentNode.getCurrentState());
                printPathSolution(currentNode);
                System.out.println("The total number of nodes generated are: " + visitedState.size());
                return;
            }
            visitedState.add(Arrays.deepToString(currentNode.getCurrentState()));

            List<Node> successorState = generateSuccessors(currentNode);

            for (Node successor : successorState) {
                if (!visitedState.contains(Arrays.deepToString(successor.getCurrentState()))) {
                    successor.parent = currentNode;
                    openState.add(successor);
                }
            }
            if (getNodeLimit() < visitedState.size())
                throw new LimitExceededException(
                        "Maximum Nodes searched.");
        }
        throw new LimitExceededException(
                "Maximum Nodes searched.");
    }

    public static void searchH2AStar() throws LimitExceededException {
        int count = 0;
        PriorityQueue<Node> openState = new PriorityQueue<>(
                Comparator.comparingInt(b -> b.getCost() + b.getHeuristic()));
        openState.add(new Node(getStartNode().getCurrentState(), 0,
                calculateH2Heuristic(getStartNode().getCurrentState()),
                null));
        HashSet<String> visitedState = new HashSet<>();
        while (!openState.isEmpty()) {
            count++;
            Node currentNode = openState.poll();
            if (Arrays.deepEquals(currentNode.getCurrentState(), goalState)) {
                setNodeNum(visitedState.size());
                setState(currentNode.getCurrentState());
                printPathSolution(currentNode);
                System.out.println("The total number of nodes generated are: " + visitedState.size());
                return;
            }
            visitedState.add(Arrays.deepToString(currentNode.getCurrentState()));

            List<Node> successorState = generateH2Successors(currentNode);

            for (Node successor : successorState) {
                if ((!visitedState.contains(Arrays.deepToString(successor.getCurrentState())))) {
                    successor.parent = currentNode;
                    openState.add(successor);
                }
            }
            if (getNodeLimit() < visitedState.size())
                throw new LimitExceededException(
                        "Maximum nodes searched.");
        }
        throw new LimitExceededException(
                "Maximum nodes searched.");

    }

    public static void localBeamSearch(int k) throws LimitExceededException {
        int heuristicValue = calculateBeamEval(getStartNode().getCurrentState());
        getStartNode().setHeuristic(heuristicValue);
        PriorityQueue<Node> openState = new PriorityQueue<>(
                Comparator.comparingInt(b -> b.getHeuristic()));
        HashSet<String> visitedState = new HashSet<>();
        openState.add(new Node(getStartNode().getCurrentState(), 0,
                calculateH2Heuristic(getStartNode().getCurrentState()),
                null));
        if (Arrays.deepEquals(getStartNode().getCurrentState(), goalState)) {
            printPathSolution(getStartNode());
            System.out.println("Goal reached!");
        }
        while (!openState.isEmpty()) {
            Node currentNode = openState.poll();
            if (Arrays.deepEquals(currentNode.getCurrentState(), goalState)) {
                setNodeNum(visitedState.size());
                setState(currentNode.getCurrentState());
                printPathSolution(currentNode);
                System.out.println("The total number of nodes generated are: " + visitedState.size());
                return;
            }
            visitedState.add(Arrays.deepToString(currentNode.getCurrentState()));
            List<Node> successors = generateBeamSuccessors(currentNode);
            List<Node> bestNodes = new ArrayList<>();
            for (Node successor : successors) {
                if ((!visitedState.contains(Arrays.deepToString(successor.getCurrentState())))) {
                    successor.parent = currentNode;
                    bestNodes.add(successor);
                }
            }
            bestNodes.sort(Comparator.comparingInt(Node::getHeuristic));
            if (bestNodes.size() > k) {
                bestNodes = bestNodes.subList(0, k);
            }
            openState.addAll(bestNodes);
            if (getNodeLimit() < visitedState.size())
                throw new LimitExceededException(
                        "Maximum nodes searched.");
        }
        throw new LimitExceededException("Maximum nodes reached!");

    }

    private static List<Node> generateBeamSuccessors(Node inputNode) {
        List<Node> successorNodes = new ArrayList<>();
        int blankRow = -1;
        int blankCol = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (inputNode.currentState[i][j] == 0) {
                    blankRow = i;
                    blankCol = j;
                }
            }
        }
        int[] rowVal = { -1, 1, 0, 0 };
        int[] colVal = { 0, 0, -1, 1 };

        for (int i = 0; i < 4; i++) {
            int newRow = blankRow + rowVal[i];
            int newCol = blankCol + colVal[i];
            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) {
                int[][] newState = copyState(inputNode.getCurrentState());
                newState[blankRow][blankCol] = newState[newRow][newCol];
                newState[newRow][newCol] = 0;
                boolean isDuplicate = false;
                for (Node node : successorNodes) {
                    if (Arrays.deepEquals(node.getCurrentState(), newState)) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate)
                    successorNodes
                            .add(new Node(newState, inputNode.getCost() + 1, calculateBeamEval(newState), inputNode));
            }
        }
        return successorNodes;
    }

    private static List<Node> generateH2Successors(Node inputNode) {
        List<Node> successorNodes = new ArrayList<>();
        int blankRow = -1;
        int blankCol = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (inputNode.currentState[i][j] == 0) {
                    blankRow = i;
                    blankCol = j;
                }
            }
        }
        // array which is used to move blank tile up or down
        int[] rowVal = { -1, 1, 0, 0 };
        // array which is used to move blank tile left or right
        int[] colVal = { 0, 0, -1, 1 };

        for (int i = 0; i < 4; i++) {
            int newRow = blankRow + rowVal[i];
            int newCol = blankCol + colVal[i];
            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) {
                int[][] newState = copyState(inputNode.getCurrentState());
                newState[blankRow][blankCol] = newState[newRow][newCol];
                newState[newRow][newCol] = 0;
                successorNodes
                        .add(new Node(newState, inputNode.getCost() + 1, calculateH2Heuristic(newState), inputNode));
            }
        }
        return successorNodes;
    }

    private static List<Node> generateSuccessors(Node inputNode) {
        List<Node> successorNodes = new ArrayList<>();
        int blankRow = -1;
        int blankCol = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (inputNode.currentState[i][j] == 0) {
                    blankRow = i;
                    blankCol = j;
                }
            }
        }
        // array which is used to move blank tile up or down
        int[] rowVal = { -1, 1, 0, 0 };
        // array which is used to move blank tile left or right
        int[] colVal = { 0, 0, -1, 1 };

        for (int i = 0; i < 4; i++) {
            int newRow = blankRow + rowVal[i];
            int newCol = blankCol + colVal[i];
            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) {
                int[][] newState = copyState(inputNode.getCurrentState());
                newState[blankRow][blankCol] = newState[newRow][newCol];
                newState[newRow][newCol] = 0;
                successorNodes
                        .add(new Node(newState, inputNode.getCost() + 1, calculateHeuristic(newState), inputNode));
            }
        }
        return successorNodes;
    }

    private static int[][] copyState(int[][] inputToCopy) {
        int[][] tempNode = new int[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(inputToCopy[i], 0, tempNode[i], 0, 3);
        }
        return tempNode;
    }

    private static String getMove(Node inputNode) {
        if (inputNode.parent == null)
            return "Starting State";

        int parentRow = -1;
        int parentCol = -1;
        int childRow = -1;
        int childCol = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (inputNode.currentState[i][j] == 0) {
                    childRow = i;
                    childCol = j;
                }
                if (inputNode.parent.currentState[i][j] == 0) {
                    parentRow = i;
                    parentCol = j;
                }
            }
        }
        int rowDiff = childRow - parentRow;
        int colDiff = childCol - parentCol;

        if (rowDiff == -1)
            return "Up";
        else if (rowDiff == 1)
            return "Down";
        else if (colDiff == -1)
            return "Left";
        else
            return "Right";

    }

    // try catch method of if > max node number, throw an maximum LimitExceeded
    // Exception
    public static void maxNodeSearched(int nodeLimit) throws LimitExceededException {
        if (nodeLimit < 0)
            throw new LimitExceededException("This many nodes is not possible");
        nodeNum = nodeLimit;
    }

    public static void printPathSolution(Node inputNode) {
        Stack<Node> pathPrint = new Stack<>();
        while (inputNode != null) {
            pathPrint.push(inputNode);
            inputNode = inputNode.getParent();
        }
        List<String> moveList = new ArrayList<>();
        while (!pathPrint.isEmpty()) {
            Node currentNode = pathPrint.pop();
            moveList.add(getMove(currentNode));
        }
        StringBuilder directionContainer = new StringBuilder();
        directionContainer.append("The directions to the solution is: ");
        for (int i = 0; i < moveList.size(); i++) {
            directionContainer.append(moveList.get(i));
            if (moveList.size() - 1 != i)
                directionContainer.append(", ");
        }
        System.out.println(directionContainer.toString());
    }

    static class Node implements Comparable<Node> {
        int[][] currentState;
        int cost;
        int heuristic;
        Node parent;

        public Node(int[][] currentState, int cost, int heuristic, Node parent) {
            this.currentState = currentState;
            this.cost = cost;
            this.heuristic = heuristic;
            this.parent = parent;
        }

        public int[][] getCurrentState() {
            return this.currentState;
        }

        public int getCost() {
            return this.cost;
        }

        public int getHeuristic() {
            return this.heuristic;
        }

        public Node getParent() {
            return this.parent;
        }

        public void setHeuristic(int inputVal) {
            this.heuristic = inputVal;
        }

        @Override
        public int compareTo(Node otherNode) {
            return Integer.compare(this.heuristic, otherNode.heuristic);
        }

    }

}