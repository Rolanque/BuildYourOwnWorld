package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;

import edu.princeton.cs.algs4.StdDraw;


import java.util.ArrayList;
import java.util.Arrays;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;

    private String saveData = "SaveData.txt";
    private String moveTracker;
    private String seed;
    private static final String N = "n";
    private static final String Q = "q";
    private static final String L = "l";
    private static final String W = "w";
    private static final String A = "a";
    private static final String S = "s";
    private static final String D = "d";
    private static final String COLON = ":";
    private static boolean lightsOn = true;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        Menu menu = new Menu(HEIGHT, WIDTH);
        String strVal = null;
        boolean keyPress = false;


        while (!keyPress) {
            if (StdDraw.hasNextKeyTyped()) {
                char val = StdDraw.nextKeyTyped();
                strVal = Character.toString(val).toLowerCase();
                if (validMenuInput(strVal)) {
                    keyPress = true;
                }
            }
        }

        switch (strVal) {
            case N:
                menu.drawStringInput();
                seed = menu.seedCreator();
                Long thisSeed = Long.parseLong(seed);
                WorldGenerator worldGen = new WorldGenerator(thisSeed);
                TETile[][] world = worldGen.returnWorld();
                HUD hud = new HUD(WIDTH, HEIGHT, world);
                ter.initialize(WIDTH, HEIGHT);
                keyPressToMove(worldGen, hud);
                break;
            case L:
                String[] data = loadFile();
                if (data != null) {
                    seed = data[0];
                    String playerMoves = data[1];
                    ter.initialize(WIDTH, HEIGHT);
                    worldGen = new WorldGenerator(Long.parseLong(seed));
                    world = worldGen.returnWorld();
                    hud = new HUD(WIDTH, HEIGHT, world);
                    String[] movesStrList = playerMoves.split("");
                    ArrayList<String> playerMovesArray = new
                        ArrayList<String>(Arrays.asList(movesStrList));
                    for (String s : playerMovesArray) {
                        playerMoves(s, worldGen, hud);
                    }
                    keyPressToMove(worldGen, hud, playerMoves);
                }
                break;
            case Q:
                System.exit(0);
                break;
            default:
                break;
        }
    }


    public boolean validMenuInput(String s) {
        return (s.equals(N) || s.equals(L) || s.equals(Q));
    }

    public boolean validGameInput(String s) {
        return (s.equals("w") || s.equals("a") || s.equals("s") || s.equals("d") || s.equals("c"));
    }

    private void keyPressToMove(WorldGenerator world, HUD hud) {

        String strVal = null;
        boolean previousMoveColon = false;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char val = StdDraw.nextKeyTyped();
                strVal = Character.toString(val).toLowerCase();
                if (validGameInput(strVal)) {
                    playerMoves(strVal, world, hud);
                    moveTracker += strVal;
                }
                if (strVal.equals(Q) && previousMoveColon) {
                    saveFile(seed, moveTracker);
                    System.exit(0);
                }
                previousMoveColon = strVal.equals(COLON);
            }
            if (lightsOn) {
                ter.renderFrame(world.returnWorld(), hud);
            }

        }

    }

    private void keyPressToMove(WorldGenerator world, HUD hud, String oldMoves) {

        moveTracker += oldMoves;
        String strVal = null;
        boolean previousMoveColon = false;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char val = StdDraw.nextKeyTyped();
                strVal = Character.toString(val).toLowerCase();
                if (validGameInput(strVal)) {
                    playerMoves(strVal, world, hud);
                    moveTracker += strVal;
                }
                if (strVal.equals(Q) && previousMoveColon) {
                    saveFile(seed, moveTracker);

                    System.exit(0);
                }
                previousMoveColon = strVal.equals(COLON);
            }
            if (lightsOn) {
                ter.renderFrame(world.returnWorld(), hud);
            }

        }
    }

    private void playerMoves(String input, WorldGenerator world, HUD hud) {
        if (validGameInput(input)) {
            if (input.equals("w")) {
                world.moveCharacter(0);
            }
            if (input.equals("s")) {
                world.moveCharacter(1);
            }
            if (input.equals("a")) {
                world.moveCharacter(2);
            }
            if (input.equals("d")) {
                world.moveCharacter(3);
            }
            if (input.equals("c")) {
                if (lightsOn) {
                    lightsOn = false;
                } else {
                    lightsOn = true;
                }
            }
        }
        if (!lightsOn) {
            int[] playerPos = world.returnPlayerPos();
            ter.renderFrame(world.returnWorld(), hud, playerPos[0] - 3, playerPos[1] - 3,
                    playerPos[0] + 3, playerPos[1] + 3);
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, running both of these:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        StringBuilder inputSeed = new StringBuilder();
        if (input.charAt(0) == 'N' || input.charAt(0) == 'n') {
            for (int i = 1; i < input.length(); i++) {
                if (input.charAt(i) == 'S' || input.charAt(i) == 's') {
                    break;
                }
                inputSeed.append(input.charAt(i));
            }
        }

        String mySeed = inputSeed.toString();
        StringBuilder strMoves = new StringBuilder();

        if (input.charAt(0) == 'L' || input.charAt(0) == 'l') {
            String[] info = loadFile();
            if (info == null) {
                System.exit(0);
            }
            mySeed = info[0];
            String pastMoves = info[1];
            for (int idx = 0; idx < pastMoves.length(); idx++) {
                strMoves.append(pastMoves.charAt(idx));
            }
        }

        if (input.charAt(0) == 'C' || input.charAt(0) == 'c') {
            if (!lightsOn) {
                lightsOn = true;
            } else {
                lightsOn = false;
            }
        }


        for (int j = inputSeed.length(); j < input.length(); j++) {
            String move = Character.toString(input.charAt(j)).toLowerCase();
            if (move.equals(":") && j < input.length() - 1) {
                String nextMove = Character.toString(input.charAt(j + 1)).toLowerCase();
                if (nextMove.equals("q")) {
                    String moves = strMoves.toString();
                    saveFile(mySeed, moves);
                }
            } else if (validGameInput(move)) {
                strMoves.append(move);
            }
        }

        Long thisSeed = Long.parseLong(mySeed);
        ter.initialize(WIDTH, HEIGHT);
        WorldGenerator worldGen = new WorldGenerator(thisSeed);
        TETile[][] world = worldGen.returnWorld();
        HUD hud = new HUD(WIDTH, HEIGHT, world);
        ArrayList<String> playerMovesArray = new
                ArrayList<String>(Arrays.asList(strMoves.toString()));
        System.out.println(playerMovesArray);
        for (String s : playerMovesArray) {
            playerMoves(s, worldGen, hud);
        }

        hudRender(worldGen, hud);
        return world;
    }


    public void hudRender(WorldGenerator world, HUD hud) {
        ter.renderFrame(world.returnWorld(), hud);
    }

    private void saveFile(String worldSeed, String moves) {
        Out out = new Out(saveData);
        In in = new In(saveData);
        while (in.hasNextLine()) {
            if (in.isEmpty()) {
                break;
            }
            in.readLine();
        }
        out.println(worldSeed + "+" + moves);
    }

    private String[] loadFile() {
        In in = new In(saveData);
        String line = null;
        while (in.hasNextLine()) {
            if (in.isEmpty()) {
                break;
            }
            line = in.readLine();
        }

        if (line == null) {
            return null;
        }
        return line.split("[+]+");
    }
}
