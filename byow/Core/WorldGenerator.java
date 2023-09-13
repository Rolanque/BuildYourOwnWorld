package byow.Core;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.util.*;


public class WorldGenerator {

    static class Room {
        int x, y, width, height;

        public Room(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    public class Player {
        private int x, y;

        public Player(int xPos, int yPos) {
            this.x = xPos;
            this.y = yPos;
        }
    }

    private int worldWidth;
    private int worldHeight;

    private Random random;
    private int numRooms;
    private Player player;
    private HashMap<ArrayList<Integer>, Room> hashRooms;

    TETile[][] world;

    ArrayList<Room> myRooms;

    public static final int WORLD_HEIGHT = 45; // height of world
    public static final int WORLD_WIDTH = 80; // width of world
    public static final int NUMROOMSMIN = 100; // minimum number of rooms
    public static final int NUMROOMSMAX = 200; // maximum number of rooms
    public static final int MINMEASURE = 4; // minimum height/width of rooms
    public static final int MAXMEASURE = 15; // maximum height/width of rooms

    public static final int CANVASHEIGHT = 70;

    public WorldGenerator(Long seed) {
        this.random = new Random(seed);
        worldHeight = WORLD_HEIGHT;
        worldWidth = WORLD_WIDTH;
        hashRooms = new HashMap<>();

        world = new TETile[worldWidth][worldHeight];
        new HUD(worldWidth, CANVASHEIGHT, world);
        initializeWorld();
        numRooms = this.random.nextInt(NUMROOMSMIN, NUMROOMSMAX);
        myRooms = new ArrayList<>();
        generateRooms();
        connectRooms();
        generateCharacter();
    }

    public void initializeWorld() {
        for (int y = 0; y < worldHeight; y++) {
            for (int x = 0; x < worldWidth; x++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private void generateRooms() {
        for (int i = 0; i < numRooms; i++) {
            // GENERATE RANDOM VALUES
            int posX = this.random.nextInt(worldWidth);
            int posY = this.random.nextInt(worldHeight);
            int myWidth = this.random.nextInt(MINMEASURE, MAXMEASURE);
            int myHeight = this.random.nextInt(MINMEASURE, MAXMEASURE);

            Room newRoom = new Room(posX, posY, myWidth, myHeight);
            if (validSpace(newRoom)) {
                placeRooms(posX, posY, myWidth, myHeight);
                myRooms.add(newRoom);
                int y = mid(newRoom)[1];
                int x = mid(newRoom)[0];
                        ArrayList<Integer> currPos = new ArrayList<>(Arrays.asList(x, y));
                        hashRooms.put(currPos, newRoom);

            }
        }
    }


    private void connectRooms() {
        int startRoomNum = this.random.nextInt(myRooms.size());
        Room startRoom = myRooms.get(startRoomNum);

        ArrayList<Room> globalQueue = new ArrayList<>();
        globalQueue.add(startRoom); // add the starting room to the queue

        ArrayList<Room> children;
        HashSet<Room> visited = new HashSet<>();
        visited.add(startRoom);

        while (!globalQueue.isEmpty()) {
            Room currRoom = globalQueue.remove(0); // remove the currRoom from the queue
            children = bfs(currRoom, 1, visited);
            for (Room newRoom : children) {
                createHallway(currRoom, newRoom);
            }

            visited.add(currRoom);
            if (visited.size() == myRooms.size() - 1) {
                break;
            }

            globalQueue = children;
        }

    }
    private void createHallway(Room currRoom, Room newRoom) {
        int[] currRoomPos = mid(currRoom);
        int[] newRoomPos = mid(newRoom);

        int genRand = this.random.nextInt(2);

        int currX = currRoomPos[0];
        int currY = currRoomPos[1];

        int lengthX = newRoomPos[0] - currX;
        int lengthY = newRoomPos[1] - currY;

        int dirrX = 1;
        int dirrY = 1;

        if (lengthX < 0) {
            dirrX *= -1;
        }

        if (lengthY < 0) {
            dirrY *= -1;
        }

        switch (genRand) {
            case 0:
                horizontal(currX, currY, lengthX, dirrX);
                corners(newRoomPos[0], currY);
                vertical(currX + lengthX, currY + dirrY, lengthY, dirrY);
            case 1:
                vertical(currX, currY, lengthY, dirrY);
                corners(currX, newRoomPos[1]);
                horizontal(currX + dirrX, currY + lengthY, lengthX, dirrX);
        }
    }



    private int[] mid(Room room) {
        return new int[]{room.x + (room.width / 2), room.y + (room.height / 3)};
    }


    private void vertical(int x, int y, int length, int dirr) {
        // up = 1
        // down = -1

        length = Math.abs(length); // take the absolute value of the length

        for (int i = 0; i < length; i++) {
            if (world[x][y + (i * dirr)] == Tileset.FLOOR_ALT) {
                continue;
            }

            world[x][y + (i * dirr)] = Tileset.FLOOR_ALT;
            world[x + 1][y + (i * dirr)] = Tileset.WALL_ALT;
            world[x - 1][y + (i * dirr)] = Tileset.WALL_ALT;
        }
    }


    private void horizontal(int x, int y, int length, int dirr) {
        // right = 1
        // left = -1

        length = Math.abs(length); // take the absolute value of the length

        for (int i = 0; i < length; i++) {
            if (world[x + (i * dirr)][y] == Tileset.FLOOR_ALT) {
                continue;
            }

            world[x + (i * dirr)][y] = Tileset.FLOOR_ALT;
            world[x + (i * dirr)][y + 1] = Tileset.WALL_ALT;
            world[x + (i * dirr)][y - 1] = Tileset.WALL_ALT;
        }
    }

    private void corners(int x, int y) {
        world[x][y] = Tileset.FLOOR_ALT;

        int[][] dirr = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {-1, 1}, {1, -1}};

        for (int[] cord : dirr) {
            int xPos = x + cord[0];
            int yPos = y + cord[1];

            if (xPos < worldWidth && xPos >= 0 && yPos < worldHeight
                    && yPos >= 0 && world[xPos][yPos] == Tileset.NOTHING) {
                world[xPos][yPos] = Tileset.WALL_ALT;
            }
        }
    }

    private ArrayList<Room> bfs(Room currRoom, int num, HashSet<Room> visitedRooms) {
        ArrayList<Integer> startingPos = new ArrayList<>(Arrays.asList(currRoom.x, currRoom.y));
        ArrayList<ArrayList<Integer>> myQueue = new ArrayList<>(List.of(startingPos));
        HashSet<ArrayList<Integer>> visited = new HashSet<>();
        visited.add(startingPos);

        ArrayList<Room> children = new ArrayList<>();
        ArrayList<ArrayList<Integer>> curr;
        int[][] dirr = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        HashSet<Room> localVisited = new HashSet<>();
        localVisited.add(currRoom);

        while (!myQueue.isEmpty()) {
            curr = new ArrayList<>();

            for (ArrayList<Integer> singles : myQueue) {
                for (int[] i : dirr) {
                    int newX = singles.get(0) + i[0];
                    int newY = singles.get(1) + i[1];
                    if (newX >= 0 && newX < worldWidth && newY >= 0 && newY < worldHeight) {
                        ArrayList<Integer> currPos = new ArrayList<>(Arrays.asList(newX, newY));
                        if (!visited.contains(currPos)) {
                            if (hashRooms.containsKey(currPos)) {
                                Room thisRoom = hashRooms.get(currPos);
                                if (!thisRoom.equals(currRoom) && !localVisited.contains(thisRoom)
                                        && !visitedRooms.contains(thisRoom)) {
                                    children.add(thisRoom);
                                    localVisited.add(thisRoom);
                                    num -= 1;
                                }
                            }

                            visited.add(currPos);
                            curr.add(currPos);
                        }

                        if (num == 0) {
                            return children;
                        }
                    }
                }
            }
            myQueue = curr;
        }

        return children;

    }

    private void placeRooms(int posX, int posY, int width, int height) {
        for (int y = posY; y <= posY + height; y++) {
            for (int x = posX; x <= posX + width; x++) {
                if (isEdge(x, y, posX, posY, width, height)) {
                    world[x][y] = Tileset.WALL_ALT;
                } else {
                    world[x][y] = Tileset.FLOOR_ALT;

                }

            }
        }
    }

    private boolean isEdge(int x, int y, int posX, int posY, int width, int height) {
        // Bottom Edge
        if (x >= posX && x <= posX + width && y == posY) {
            return true;
        }
        // Top Edge
        if (x >= posX && x <= posX + width && y == posY + height) {
            return true;
        }
        // Left Edge
        if (y >= posY && y <= posY + height && x == posX) {
            return true;
        }
        // Right Edge
        if (y >= posY && y <= posY + height && x == posX + width) {
            return true;
        }

        return false;
    }

    private boolean validSpace(Room myRoom) {
        int altX = myRoom.x + myRoom.width; // upper bound x
        int altY = myRoom.y + myRoom.height; // upper bound y

        if (altX >= worldWidth || altY >= worldHeight || myRoom.x < 0 || myRoom.y < 0) {
            return false;
        }

        for (int x = myRoom.x; x < altX; x++) {
            for (int y = myRoom.y; y < altY; y++) {
                if (world[x][y] != Tileset.NOTHING) {
                    return false;
                }
            }
        }

        return true;
    }

    // CHARACTER INTERACTIVITY - BEGIN
    private void generateCharacter() {
        for (int y = worldHeight - 1; y > 0; y--) {
            for (int x = 1; x < worldWidth; x++) {
                TETile pos = world[x][y];
                if (pos == Tileset.FLOOR_ALT) {
                    player = new Player(x, y);
                    world[x][y] = Tileset.AVATAR;
                    return;
                }
            }
        }
    }

    public void moveCharacter(Integer direction) {
        if (direction == 0) {
            moveHelper(0, 1);
        }
        if (direction == 1) {
            moveHelper(0, -1);
        }
        if (direction == 2) {
            moveHelper(-1, 0);
        }
        if (direction == 3) {
            moveHelper(1, 0);
        }
        }

    private void moveHelper(int x, int y) {
        if (world[player.x + x][player.y + y] != Tileset.FLOOR_ALT) {
            return;
        }
        world[player.x][player.y] = Tileset.FLOOR_ALT;
        world[player.x + x][player.y + y] = Tileset.AVATAR;
        player.x += x;
        player.y += y;

    }

    public int[] returnPlayerPos() {
        return new int[]{player.x, player.y};
    }


    // CHARACTER INTERACTIVITY - END

    public TETile[][] returnWorld() {
        return world;
    }

}