package byow.Core;
import byow.TileEngine.TETile;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class HUD {
    private int width;
    private int height;

    private TETile[][] world;

    public int TEXT_SIZE = 20;

    public HUD (int width, int height, TETile[][] world) {
        this.width = width;
        this.height = height;
        this.world = world;

    }

    public void hoveredTile() {
        defaultLayout();
            Double mouseX = StdDraw.mouseX();
            Double mouseY = StdDraw.mouseY();

            int[] pos = findTile(mouseX, mouseY);

            if (pos[0] < 0 || pos[0] >= world.length || pos[1] < 0 || pos[1] >= world[0].length) {
                StdDraw.line(0, this.height - 5, this.width, this.height - 5);
                StdDraw.text(7, this.height - 3, "out of bounds!");
            } else {
                TETile tile = world[pos[0]][pos[1]];
                StdDraw.line(0, this.height - 5, this.width, this.height - 5);
                StdDraw.text(5, this.height - 3, tile.description());
            }
        }


    public void displayDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        StdDraw.text(width - 10, this.height - 3, dtf.format(now));
    }

    public int[] findTile(Double mouseX, Double mouseY) {
        int x = (int) Math.floor(mouseX);
        int y = (int) Math.floor(mouseY);


        return new int[]{x, y};
    }

    public void displayHoverTile() {
        defaultLayout();

        Double mouseX = StdDraw.mouseX();
        Double mouseY = StdDraw.mouseY();

        int[] pos = findTile(mouseX, mouseY);
        int xPos = pos[0];
        int yPos = pos[1];

        StdDraw.filledSquare(pos[0] + 0.5, pos[1] + 0.5, 0.5);
    }

    private void defaultLayout() {
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, TEXT_SIZE);
        StdDraw.setFont(font);
    }
}
