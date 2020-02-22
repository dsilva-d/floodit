import java.util.Arrays;
import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;

interface ICell {

  // determines whether or not a cell should become flooded
  void checkSides(Color flood);
}

//represents a cell where there is no cell
class NoCell implements ICell {

  // a space with no cell cannot be flooded
  public void checkSides(Color flood) {
    // do nothing
  }
}

// Represents a single square of the game area
class Cell implements ICell {

  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;

  // the four adjacent cells to this one
  ICell left;
  ICell top;
  ICell right;
  ICell bottom;

  Cell(Color color) {
    this.x = 0;
    this.y = 0;
    this.color = color;
    this.flooded = false;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  Cell(Color color, Boolean flooded) {
    this.x = 0;
    this.y = 0;
    this.color = color;
    this.flooded = flooded;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  Cell(Color color, ICell left, ICell top, ICell right, ICell bottom) {
    this.color = color;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }

  Cell(int x, int y, Color color, boolean flooded) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  Cell(int x, int y, Color color, boolean flooded, ICell left, ICell top, ICell right,
      ICell bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }

  // checks the sides of this cell for flooding
  public void checkSides(Color flood) {
    if (this.color.equals(flood)) {
      this.flooded = true;
    }
  }
}

class FloodItWorld extends World {
  // gridSize by gridSize of cells
  int gridSize;
  int numColors;
  // All the cells of the game
  ArrayList<Cell> board;
  int dimension;
  Random random;
  int guessed;
  int totalguesses;
  Color floodColor;
  double time;
  int add;

  // Defines an int constant
  static final int BOARD_SIZE = 500;

  FloodItWorld(int gridSize, int numColors) {
    if (gridSize < 1) {
      throw new IllegalArgumentException("Change grid size");
    }
    else {
      this.gridSize = gridSize;
    }
    if (numColors > 9 || numColors == 0) {
      throw new IllegalArgumentException("Change color count");
    }
    else {
      this.numColors = numColors;
    }
    this.board = this.buildCellList(new Random());
    this.dimension = BOARD_SIZE / gridSize;
    this.guessed = 0;
    this.totalguesses = (gridSize * numColors / 4) + numColors;// gridSize + numColors;
    this.floodColor = board.get(0).color;
    this.add = 0;
  }

  // random constructor
  FloodItWorld(int gridSize, int numColors, Random rand) {
    if (gridSize < 1) {
      throw new IllegalArgumentException("Change grid size");
    }
    else {
      this.gridSize = gridSize;
    }
    this.numColors = numColors;
    this.board = this.buildCellList(rand);
    this.dimension = BOARD_SIZE / gridSize;
    this.random = rand;
    this.floodColor = board.get(0).color;
    this.time = 0;
  }

  // constant board constructor
  FloodItWorld(int gridSize, int numColors, ArrayList<Cell> board) {
    if (gridSize < 1) {
      throw new IllegalArgumentException("Change grid size");
    }
    else {
      this.gridSize = gridSize;
    }
    this.numColors = numColors;
    this.board = board;
    this.dimension = BOARD_SIZE / gridSize;
    this.random = new Random();
    this.floodColor = board.get(0).color;
    this.time = 0;
    this.guessed = 0;
  }

  // constants
  FloodItWorld(int gridSize, int numColors, int guesses, ArrayList<Cell> board) {
    if (gridSize < 1) {
      throw new IllegalArgumentException("Change grid size");
    }
    else {
      this.gridSize = gridSize;
    }
    this.numColors = numColors;
    this.board = board;
    this.dimension = BOARD_SIZE / gridSize;
    this.random = new Random();
    this.floodColor = board.get(0).color;
    this.time = 0;
    this.totalguesses = this.guessed = guesses;
    this.totalguesses = (gridSize * numColors / 4) + numColors;
  }

  //updates time and the colors of the cells in a waterfall pattern
  public void onTick() {
    if (!(guessed >= totalguesses || this.allTrue(board))) {
      time = this.time + .1;
    }
    if (add <= gridSize + gridSize) {
      add = add + 1;
    }
    for (Cell d : board) {
      if (d.x + d.y < add && d.flooded) {
        d.color = floodColor;
      }
    }
  }

  // resets the game
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.guessed = 0;
      this.board = buildCellList(new Random());
      this.time = 0;
    }
  }

  // determines the flood color and updates guesses and flood boolean
  public void onMousePressed(Posn p) {
    this.checkFlood(board);
    for (Cell c : board) {
      if (p.x >= c.x * dimension && p.x <= (c.x * dimension) + dimension && p.y >= c.y * dimension
          && p.y <= (c.y * dimension) + dimension && this.finishedFlooding()
          && (!(c.color.equals(board.get(0).color)))) {
        guessed = guessed + 1;
        floodColor = c.color;
        add = 0;
        board.get(0).color = floodColor;
        this.checkFlood(board);
      }
    }
  }

  // determines if all flooded cells are the correct color
  public boolean finishedFlooding() {
    int acc = 0;
    for (Cell c : board) {
      if (c.flooded) {
        acc = acc + 1;
      }
    }
    int acc2 = 0;
    for (Cell c : board) {
      if (c.flooded && c.color.equals(floodColor)) {
        acc2 = acc2 + 1;
      }
    }
    return acc == acc2;
  }

  // determines which of a cell's neighbors should be flooded
  public void checkFlood(ArrayList<Cell> arr) {
    for (Cell d : board) {
      if (d.flooded) {
        d.left.checkSides(floodColor);
        d.top.checkSides(floodColor);
        d.right.checkSides(floodColor);
        d.bottom.checkSides(floodColor);
      }
    }
  }

  // renders the game
  public WorldScene makeScene() {
    WorldScene w = new WorldScene(BOARD_SIZE, BOARD_SIZE); // takes in a worldScene
    this.drawCells(board, w);
    this.printGuess(w);
    return w;
  }

  // draws the cells
  void drawCells(ArrayList<Cell> arr, WorldScene w) {
    for (int i = 0; i < arr.size(); i++) {
      WorldImage c = new RectangleImage(dimension, dimension, "solid", arr.get(i).color);
      w.placeImageXY(c, arr.get(i).x * dimension + (dimension / 2),
          arr.get(i).y * dimension + (dimension / 2));
    }
  }

  // prints guesses and texts on the worldScene
  void printGuess(WorldScene w) {
    WorldImage g = new TextImage(this.guessed + "/" + this.totalguesses, 20, Color.BLACK);
    WorldImage d = new TextImage("You Lose!", 50, Color.BLACK);
    WorldImage f = new TextImage("You Win!", 50, Color.black);
    WorldImage t = new TextImage(String.format("%.2f", this.time), 20, Color.BLACK);
    WorldImage s = new TextImage("Start", 50, Color.BLACK);
    w.placeImageXY(g, BOARD_SIZE / 2, BOARD_SIZE * 10 / 11);
    w.placeImageXY(t, BOARD_SIZE * 10 / 11, BOARD_SIZE * 10 / 11);
    if (this.guessed == 0 && this.time < 1.0) {
      w.placeImageXY(s, BOARD_SIZE / 2, BOARD_SIZE / 2);
    }
    if (this.guessed >= this.totalguesses) {
      w.placeImageXY(d, BOARD_SIZE / 2, BOARD_SIZE / 2);
    }
    if (this.allTrue(board)) {
      w.placeImageXY(f, BOARD_SIZE / 2, BOARD_SIZE / 2);
    }
  }

  // determines whether the board is fully flooded
  boolean allTrue(ArrayList<Cell> arr) {
    int acc = 0;
    for (Cell c : arr) {
      if (c.flooded) {
        acc = acc + 1;
      }
    }
    return acc == arr.size() && guessed < totalguesses;
  }

  // builds a list of the given positive number long
  Color buildColorList(int n) {
    ArrayList<Color> allColors = new ArrayList<Color>(
        Arrays.asList(Color.blue, Color.red, Color.yellow, Color.pink, Color.green, Color.orange,
            Color.cyan, Color.magenta, Color.LIGHT_GRAY));
    return allColors.get(n);
  }

  // put in a number and makes a list with that many cells
  ArrayList<Cell> buildCellList(Random rand) {
    ArrayList<Cell> ref = new ArrayList<Cell>();
    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        ref.add(new Cell(i, j, this.buildColorList(rand.nextInt(numColors)), false));
      }
    }
    this.connect(ref);
    ref.get(0).flooded = true;
    return ref;
  }

  // makes the connections between cells in the given arrayList
  void connect(ArrayList<Cell> arr) {
    for (int ind = 0; ind < arr.size(); ind++) {
      Cell c = arr.get(ind);
      int x = c.x;
      int y = c.y;
      ICell no = new NoCell();

      if (x == 0) {
        c.left = no;
      }
      else {
        c.left = arr.get(ind - gridSize);
      }

      if (x == gridSize - 1) {
        c.right = no;
      }
      else {
        c.right = arr.get(ind + gridSize);
      }

      if (y == 0) {
        c.top = no;
      }
      else {
        c.top = arr.get(ind - 1);
      }

      if (y == gridSize - 1) {
        c.bottom = no;
      }
      else {
        c.bottom = arr.get(ind + 1);
      }
    }
  }
}

class Examples {

  // test big bang
  void testGame(Tester t) {
    FloodItWorld fw = new FloodItWorld(14, 6);
    fw.bigBang(fw.gridSize * fw.dimension, fw.gridSize * fw.dimension, .1);
  }

  Cell first;
  Cell red;
  Cell pink;
  Cell blue;
  Cell orange;
  Cell yellow;
  Cell cyan;
  Cell black;
  Cell magenta;
  Cell first2;
  Cell red2;
  Cell pink2;
  Cell blue2;
  ArrayList<Cell> test;
  ArrayList<Cell> test2;
  ICell none = new NoCell();
  FloodItWorld f = new FloodItWorld(3, 3, new Random(2));
  Cell sblue;
  ArrayList<Cell> single;
  FloodItWorld f1;
  WorldScene worldscene;
  Cell ctest;
  ICell c1;
  ICell c2;
  ICell c3;
  FloodItWorld f2;
  FloodItWorld f3;
  FloodItWorld fguesses;
  FloodItWorld fguesses2;
  FloodItWorld fguesses3;
  FloodItWorld fguesses4;

  void initial() {
    first = new Cell(0, 0, Color.GREEN, true);
    pink = new Cell(0, 1, Color.pink, false);
    red = new Cell(0, 2, Color.RED, false);
    blue = new Cell(1, 0, Color.BLUE, false);
    orange = new Cell(1, 1, Color.ORANGE, false);
    yellow = new Cell(1, 2, Color.YELLOW, false);
    cyan = new Cell(2, 0, Color.CYAN, false);
    black = new Cell(2, 1, Color.BLACK, false);
    magenta = new Cell(2, 2, Color.MAGENTA, false);
    first2 = new Cell(0, 0, Color.green, true);
    red2 = new Cell(0, 1, Color.red, false);
    pink2 = new Cell(1, 0, Color.pink, false);
    blue2 = new Cell(1, 1, Color.blue, false);
    test = new ArrayList<Cell>(
        Arrays.asList(first, red, pink, blue, orange, yellow, cyan, black, magenta));
    test2 = new ArrayList<Cell>(Arrays.asList(first2, red2, pink2, blue2));
    single = new ArrayList<Cell>();
    sblue = new Cell(Color.BLUE);
    single.add(sblue);
    f1 = new FloodItWorld(1, 1);
    worldscene = new WorldScene(500, 500);
    ctest = new Cell(Color.red);
    c1 = new Cell(Color.red);
    c2 = new Cell(Color.BLUE);
    c3 = new NoCell();
    f3 = new FloodItWorld(3, 3, test);
    f2 = new FloodItWorld(2, 3, test2);
    fguesses = new FloodItWorld(3, 3, 3, test);
    fguesses = new FloodItWorld(3, 3, 3, test);
    fguesses2 = new FloodItWorld(2, 2, 0, test);
    fguesses3 = new FloodItWorld(2, 2, 3, test);
    fguesses4 = new FloodItWorld(1, 1, 0, test);
  }

  // tests for finishedFlooding
  boolean testfinishedFlooding(Tester t) {
    initial();
    return t.checkExpect(f3.finishedFlooding(), true) && t.checkExpect(f1.finishedFlooding(), true);
  }

  // tests for IllegalArgumentException
  boolean testExceptions(Tester t) {
    initial();
    return t.checkConstructorException(new IllegalArgumentException("Change grid size"),
        "FloodItWorld", 0, 3)
        && t.checkConstructorException(new IllegalArgumentException("Change color count"),
            "FloodItWorld", 3, 10)
        && t.checkConstructorException(new IllegalArgumentException("Change color count"),
            "FloodItWorld", 3, 0);
  }

  // tests for checkSides
  void testcheckSides(Tester t) {
    initial();
    t.checkExpect(f3.board.get(1).flooded, false);
    t.checkExpect(f3.board.get(0).flooded, true);
    t.checkExpect(f3.board.get(3).flooded, false);
    f3.board.get(3).checkSides(Color.blue);
    t.checkExpect(f3.board.get(1).flooded, false);
    t.checkExpect(f3.board.get(3).color, Color.blue);
    t.checkExpect(f3.board.get(3).flooded, true);
    f3.board.get(1).checkSides(Color.red);
    t.checkExpect(f3.board.get(1).flooded, true);
  }

  // tests for connect
  void testConnect(Tester t) {
    initial();
    t.checkExpect(first.left, null);
    t.checkExpect(first.top, null);
    t.checkExpect(first.right, null);
    t.checkExpect(first.bottom, null);

    t.checkExpect(red.left, null);
    t.checkExpect(red.top, null);
    t.checkExpect(red.right, null);
    t.checkExpect(red.bottom, null);

    t.checkExpect(pink.left, null);
    t.checkExpect(pink.top, null);
    t.checkExpect(pink.right, null);
    t.checkExpect(pink.bottom, null);

    t.checkExpect(blue.left, null);
    t.checkExpect(blue.top, null);
    t.checkExpect(blue.right, null);
    t.checkExpect(blue.bottom, null);

    t.checkExpect(orange.left, null);
    t.checkExpect(orange.top, null);
    t.checkExpect(orange.right, null);
    t.checkExpect(orange.bottom, null);

    t.checkExpect(yellow.left, null);
    t.checkExpect(yellow.top, null);
    t.checkExpect(yellow.right, null);
    t.checkExpect(yellow.bottom, null);

    t.checkExpect(cyan.left, null);
    t.checkExpect(cyan.top, null);
    t.checkExpect(cyan.right, null);
    t.checkExpect(cyan.bottom, null);

    t.checkExpect(black.left, null);
    t.checkExpect(black.top, null);
    t.checkExpect(black.right, null);
    t.checkExpect(black.bottom, null);

    t.checkExpect(magenta.left, null);
    t.checkExpect(magenta.top, null);
    t.checkExpect(magenta.right, null);
    t.checkExpect(magenta.bottom, null);

    f3.connect(test);

    t.checkExpect(first.left, none);
    t.checkExpect(first.top, none);
    t.checkExpect(first.right, blue);
    t.checkExpect(first.bottom, red);
    t.checkExpect(red.left, none);
    t.checkExpect(red.top, first);
    t.checkExpect(red.right, orange);
    t.checkExpect(pink.left, none);
    t.checkExpect(pink.top, red);
    t.checkExpect(pink.right, yellow);
    t.checkExpect(blue.left, first);
    t.checkExpect(blue.top, none);
    t.checkExpect(blue.right, cyan);
    t.checkExpect(blue.bottom, orange);
    t.checkExpect(orange.left, red);
    t.checkExpect(orange.top, blue);
    t.checkExpect(orange.right, black);
    t.checkExpect(orange.bottom, yellow);
    t.checkExpect(yellow.left, pink);
    t.checkExpect(yellow.top, orange);
    t.checkExpect(yellow.right, magenta);
    t.checkExpect(yellow.bottom, none);
    t.checkExpect(cyan.left, blue);
    t.checkExpect(cyan.top, none);
    t.checkExpect(cyan.right, none);
    t.checkExpect(cyan.bottom, black);
    t.checkExpect(magenta.left, yellow);
    t.checkExpect(magenta.top, black);
    t.checkExpect(magenta.right, none);
    t.checkExpect(magenta.bottom, none);
  }

  // test buildColorList
  boolean testbuildColorList(Tester t) {
    return t.checkExpect(f.buildColorList(0), Color.BLUE)
        && t.checkExpect(f.buildColorList(1), Color.RED)
        && t.checkExpect(f.buildColorList(2), Color.YELLOW)
        && t.checkExpect(f.buildColorList(3), Color.PINK)
        && t.checkExpect(f.buildColorList(4), Color.GREEN)
        && t.checkExpect(f.buildColorList(5), Color.orange)
        && t.checkExpect(f.buildColorList(6), Color.CYAN)
        && t.checkExpect(f.buildColorList(7), Color.magenta)
        && t.checkExpect(f.buildColorList(8), Color.LIGHT_GRAY);
  }

  // allTrue
  boolean testallTrue(Tester t) {
    initial();
    return t.checkExpect(f.allTrue(f3.board), false) && t.checkExpect(f1.allTrue(f1.board), true);
  }

  // test drawing
  void testDrawing(Tester t) {
    initial();
    f1.drawCells(single, worldscene);
    WorldScene expected = new WorldScene(500, 500);
    expected.placeImageXY(new RectangleImage(500, 500, "solid", Color.BLUE), 250, 250);
    t.checkExpect(worldscene, expected);
  }

  // tests for build cell list
  void testbuildCellList(Tester t) {
    Random r = new Random(2);
    FloodItWorld f1 = new FloodItWorld(1, 2, r);
    f1.buildCellList(r);
    t.checkExpect(f1.board.get(0),
        new Cell(0, 0, Color.red, true, new NoCell(), new NoCell(), new NoCell(), new NoCell()));
    Random r2 = new Random(5);
    FloodItWorld f2 = new FloodItWorld(1, 8, r2);
    t.checkExpect(f2.board.get(0), new Cell(0, 0, new Color(255, 200, 0), true, new NoCell(),
        new NoCell(), new NoCell(), new NoCell()));
  }

  // check flood tests
  void testCheckFlood(Tester t) {
    initial();
    t.checkExpect(f2.board.get(0).flooded, true);
    t.checkExpect(f2.board.get(1).flooded, false);
    t.checkExpect(f2.board.get(2).flooded, false);
    t.checkExpect(f2.board.get(3).flooded, false);
    f2.board.set(0, new Cell(0, 0, Color.red, true));
    f2.connect(f2.board);
    f2.checkFlood(f2.board);
    t.checkExpect(f2.board.get(0).flooded, true);
    t.checkExpect(f2.board.get(1).flooded, false);
    t.checkExpect(f2.board.get(2).flooded, false);
    t.checkExpect(f2.board.get(3).flooded, false);
  }

  // onMouse tests
  void testMouse(Tester t) {
    initial();
    t.checkExpect(f.board.get(0).color, new Color(255, 0, 0));
    t.checkExpect(f.board.get(1).color, new Color(0, 0, 255));
    f.onMousePressed(new Posn(10, 250));
    t.checkExpect(f.board.get(1).flooded, true);
    t.checkExpect(f.board.get(0).color, new Color(0, 0, 255));
    f.onMousePressed(new Posn(10, 250));
    t.checkExpect(f.board.get(0).color, new Color(0, 0, 255));
    t.checkExpect(f.board.get(2).color, new Color(255, 255, 0));
    f.onMousePressed(new Posn(10, 450));
    t.checkExpect(f.board.get(2).flooded, false);
    t.checkExpect(f.board.get(0).color, new Color(0, 0, 255));
  }

  // reset tests
  void testreset(Tester t) {
    initial();
    t.checkExpect(fguesses.guessed, 3);
    fguesses.onKeyEvent("f");
    t.checkExpect(fguesses.guessed, 3);
    fguesses.onKeyEvent("r");
    t.checkExpect(fguesses.guessed, 0);
  }

  // guesses tests
  void testGuesses(Tester t) {
    initial();
    WorldScene ws = new WorldScene(500, 500);
    ws.placeImageXY(new RectangleImage(250, 250, "solid", fguesses2.board.get(0).color), 125, 125);
    ws.placeImageXY(new RectangleImage(250, 250, "solid", fguesses2.board.get(1).color), 125, 375);
    ws.placeImageXY(new RectangleImage(250, 250, "solid", fguesses2.board.get(2).color), 375, 125);
    ws.placeImageXY(new RectangleImage(250, 250, "solid", fguesses2.board.get(3).color), 375, 375);

    WorldScene ws2 = new WorldScene(500, 500);
    ws2.placeImageXY(new RectangleImage(250, 250, "solid", fguesses2.board.get(0).color), 125, 125);
    ws2.placeImageXY(new RectangleImage(250, 250, "solid", fguesses2.board.get(1).color), 125, 375);
    ws2.placeImageXY(new RectangleImage(250, 250, "solid", fguesses2.board.get(2).color), 375, 125);
    ws2.placeImageXY(new RectangleImage(250, 250, "solid", fguesses2.board.get(3).color), 375, 375);

    t.checkExpect(ws, ws2);

    WorldImage t1 = new TextImage("0/3", 20, Color.BLACK);
    ws2.placeImageXY(t1, 250, 454);
    ws2.placeImageXY(new TextImage(String.format("%.2f", fguesses2.time), 20, Color.BLACK), 454,
        454);
    ws2.placeImageXY(new TextImage("Start", 50, Color.BLACK), 250, 250);
    fguesses2.printGuess(ws);
    t.checkExpect(ws, ws2);
    initial();

    WorldScene ws3 = new WorldScene(500, 500);
    ws3.placeImageXY(new RectangleImage(250, 250, "solid", fguesses3.board.get(0).color), 125, 125);
    ws3.placeImageXY(new RectangleImage(250, 250, "solid", fguesses3.board.get(1).color), 125, 375);
    ws3.placeImageXY(new RectangleImage(250, 250, "solid", fguesses3.board.get(2).color), 375, 125);
    ws3.placeImageXY(new RectangleImage(250, 250, "solid", fguesses3.board.get(3).color), 375, 375);

    WorldScene ws4 = new WorldScene(500, 500);
    ws4.placeImageXY(new RectangleImage(250, 250, "solid", fguesses3.board.get(0).color), 125, 125);
    ws4.placeImageXY(new RectangleImage(250, 250, "solid", fguesses3.board.get(1).color), 125, 375);
    ws4.placeImageXY(new RectangleImage(250, 250, "solid", fguesses3.board.get(2).color), 375, 125);
    ws4.placeImageXY(new RectangleImage(250, 250, "solid", fguesses3.board.get(3).color), 375, 375);

    t.checkExpect(ws3, ws4);

    WorldImage t2 = new TextImage("3/3", 20, Color.BLACK);
    ws4.placeImageXY(t2, 250, 454);
    ws4.placeImageXY(new TextImage(String.format("%.2f", fguesses3.time), 20, Color.BLACK), 454,
        454);
    ws4.placeImageXY(new TextImage("You Lose!", 50, FontStyle.REGULAR, Color.BLACK), 250, 250);
    fguesses3.printGuess(ws3);
    t.checkExpect(ws3, ws4);
  }
}
