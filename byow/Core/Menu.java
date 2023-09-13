package byow.Core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;

public class Menu {
    private int width;
    private int height;

    private HashSet<String> validSeedNumbers = new HashSet<>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));

    public void drawMenu() {
        drawFrame("CS 61B: The World");
        StdDraw.pause(1000);
        drawMenu(new String[]{"New World (N)", "Load (L)", "Quit (Q)"}, width, height);
        StdDraw.pause(1000);
    }


    public Menu(int height, int width) {
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        drawMenu();
        StdDraw.show();
    }
    public void draw(String s, int posX, int posY) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(posX, posY, s);
    }

    public void drawFrame(String s, int posX, int posY) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(posX, posY, s);
    }
    public void drawFrame(String s) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(width / 2, height / 2, s);
    }
        public void drawMenu(String[] s, int posX, int posY) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        for (int i = 0; i < s.length; i++) {
            StdDraw.text(posX / 2, posY - (i + 1) * posY / 4, s[i]);
        }
    }
    public void drawMenu(String[] s, int entries) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        for (int i = 0; i < s.length; i++) {
            StdDraw.text(width / 2, height - (i + 1) * height / (entries + 1), s[i]);
        }
        StdDraw.show();
    }

    public void drawStringInput() {
        drawMenu(new String[]{"Please enter your seed below:", "Press S to generate the world"}, 2);
        StdDraw.show();
    }

    public String seedCreator() {
        String wall = "s";
        String strVal;
        int seedLength = 0;

        StringBuilder seedBuilder = new StringBuilder();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char val = StdDraw.nextKeyTyped();
                strVal = Character.toString(val).toLowerCase();
                if (strVal.equals(wall) && seedLength != 0) {
                    break;
                }
                if (validSeedNumbers.contains(strVal)) {
                    seedBuilder.append(strVal);
                    StdDraw.clear();
                    drawStringInput();
                    this.draw(String.valueOf(seedBuilder), width / 2, height / 2);
                    seedLength += 1;
                }
            }
        }
        return seedBuilder.toString();
    }


}
