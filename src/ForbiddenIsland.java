import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//Represents a single square of the game area
class Cell {
  // represents absolute height of this cell, in feet
  double height;

  // In logical coordinates, with the origin at the top-left corner of the
  // screen
  int x;
  int y;

  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // reports whether this cell is flooded or not
  boolean isFlooded;

  Cell(double height, int x, int y) {
    this.height = height;
    this.x = x;
    this.y = y;
  }

  Color getCellColor(int waterHeight) {
    if (isFlooded) {
      return new Color((int) Math.abs((this.height - waterHeight)) * 5, 
          (int) Math.abs(this.height), 100);
    }
    if (this.height < waterHeight) {
      return new Color(255 - (int) Math.abs((this.height - waterHeight)) * 5, 
          (int) Math.abs(this.height) * 3, 0);
    }

    if ((255 - ((255 * (this.height * 2)) / ForbiddenIslandWorld.ISLAND_SIZE)) > 255) {
      return Color.blue;
    }
    else if ((255 - ((255 * (this.height * 2)) / ForbiddenIslandWorld.ISLAND_SIZE)) < 0) {
      return Color.white;
    }
    return new Color(0, 155, 0,
        (int) (255 - ((255 * (this.height * 2)) / ForbiddenIslandWorld.ISLAND_SIZE)));
  }

  WorldImage makeImage(int waterHeight) {
    return new RectangleImage(ForbiddenIslandWorld.CELL_SIZE, ForbiddenIslandWorld.CELL_SIZE,
        OutlineMode.SOLID, this.getCellColor(waterHeight));
  }

  void updateFlooding(int waterHeight) {
    if (this.height <= waterHeight && this.isNextToWater()) {
      this.isFlooded = true;
    }
  }

  boolean isNextToWater() {
    return this.top.isFlooded || this.left.isFlooded || this.right.isFlooded
        || this.bottom.isFlooded;
  }
}

class OceanCell extends Cell {

  OceanCell(double height, int x, int y) {
    super(height, x, y);
    this.isFlooded = true;
  }

  @Override
  Color getCellColor(int waterHeight) {
    return Color.blue;
  }
}

class Player {
  // In logical coordinates, with the origin at the top-left corner of the
  // screen
  int x;

  int y;

  Player(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // moves this player depending on given string
  void movePlayer(String ke) {
    if (ke.equals("right") || ke.equals("d")) {
      x += 1;
    }
    else if (ke.equals("left") || ke.equals("a")) {
      x -= 1;
    }
    else if (ke.equals("up") || ke.equals("w")) {
      y -= 1;
    }
    else if (ke.equals("down") || ke.equals("s")) {
      y += 1;
    }
  }

  WorldImage makeImage(int waterHeight, String headerText) {
    WorldImage text = new TextImage(headerText, ForbiddenIslandWorld.CELL_SIZE * 2, Color.red);
    WorldImage img = new FromFileImage("player.png");
    return new OverlayImage(text, img);
  }
}

class Target {
  // In logical coordinates, with the origin at the top-left corner of the
  // screen
  int x;

  int y;

  Target(int x, int y) {
    this.x = x;
    this.y = y;
  }

  Color getTargetColor(int waterHeight) {
    return Color.magenta;
  }

  WorldImage makeImage(int waterHeight) {
    return new CircleImage(ForbiddenIslandWorld.CELL_SIZE, OutlineMode.SOLID,
        this.getTargetColor(waterHeight));
  }

  boolean playerOnTarget(Player p) {
    return p.x == this.x && p.y == this.y;
  }

  boolean isHeli() {
    return false;
  }
}

class HelicopterTarget extends Target {

  HelicopterTarget(int x, int y) {
    super(x, y);
  }

  @Override
  WorldImage makeImage(int waterHeight) {
    return new FromFileImage("helicopter.png");
  }

  @Override
  boolean isHeli() {
    return true;
  }
}

class ForbiddenIslandWorld extends World {
  // All the cells of the game, including the ocean
  IList<Cell> board;

  // All of the targets in the game
  ArrayList<Target> targets;

  // represents the player
  Player p;

  Player p2;

  // the current height of the ocean
  int waterHeight;

  // counts how many ticks have passed since waterHeight rose
  int tickCount;

  // determines the amount of ticks to wait before raising the waterHight
  final int tickWait = 10;

  // Defines an int constant
  static final int ISLAND_SIZE = 64;

  // determines the height and width of one cell (for rendering)
  static final int CELL_SIZE = 10;

  // defines the amount of targets that will be places on the island
  static final int NUM_TARGETS = 9;

  // value to be used as range for generating random terrain
  static final int RANDOM_NUDGE = 10000;

  ArrayList<ArrayList<Double>> getHeights() {
    ArrayList<ArrayList<Double>> heights = new ArrayList<ArrayList<Double>>();
    ArrayList<Double> row;

    for (int y = ISLAND_SIZE / 2; y >= ISLAND_SIZE / -2; y--) {
      row = new ArrayList<Double>();
      heights.add(row);

      for (int x = ISLAND_SIZE / 2; x >= ISLAND_SIZE / -2; x--) {
        row.add((ISLAND_SIZE * 1.0) / 2 - Math.abs(x) - Math.abs(y));
      }
    }
    return heights;
  }

  ArrayList<ArrayList<Double>> getRandomHeights() {
    Random rand = new Random();
    ArrayList<ArrayList<Double>> heights = getHeights();

    ArrayList<ArrayList<Double>> newHeights = new ArrayList<ArrayList<Double>>();
    ArrayList<Double> row;
    for (ArrayList<Double> list : heights) {
      row = new ArrayList<Double>();
      newHeights.add(row);
      for (Double height : list) {
        if (height > 0) {
          row.add((double) rand.nextInt(ISLAND_SIZE / 2) + 1);
        }
        else {
          row.add(height);
        }
      }
    }
    return newHeights;
  }

  ArrayList<ArrayList<Double>> getSubdivisHeights() {
    ArrayList<ArrayList<Double>> heights = new ArrayList<ArrayList<Double>>();
    ArrayList<Double> row;
    for (int i = 0; i < ISLAND_SIZE + 1; i++) {
      row = new ArrayList<Double>();
      heights.add(row);
      for (int j = 0; j < ISLAND_SIZE + 1; j++) {
        row.add(0.0);
      }
    }
    heights.get(ISLAND_SIZE / 2).set(ISLAND_SIZE / 2, ISLAND_SIZE / 2.0); // middle
    heights.get(0).set(ISLAND_SIZE / 2, 1.0); //top mid
    heights.get(ISLAND_SIZE / 2).set(ISLAND_SIZE, 1.0); // right mid
    heights.get(ISLAND_SIZE / 2).set(0, 1.0); //left mid
    heights.get(ISLAND_SIZE).set(ISLAND_SIZE / 2, 1.0); //bottom mid



    return heights;
  }

  ArrayList<ArrayList<Double>> calcValuesGivenCorners(ArrayList<ArrayList<Double>> quadrant) {
    // divide array into four quadrants
    // calculate the values
    // call again onto each of the four quadrants
    if (quadrant.size() == 3) {
      this.calcQuadrant(quadrant);
      return quadrant;
    }

    //split the array into the top two quadrants
    ArrayList<ArrayList<Double>> topHalf = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i <= quadrant.size() / 2; i++) {
      topHalf.add(quadrant.get(i));
    }

    ArrayList<ArrayList<Double>> topLeft = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<Double>> topRight = new ArrayList<ArrayList<Double>>();
    ArrayList<Double> leftRow;
    ArrayList<Double> rightRow;
    for (int i = 0; i < topHalf.size(); i++) {
      leftRow = new ArrayList<Double>();
      rightRow = new ArrayList<Double>();
      topLeft.add(leftRow);
      topRight.add(rightRow);
      for (int j = 0; j < topHalf.get(i).size(); j++) {
        if (j > topHalf.get(i).size() / 2) {
          rightRow.add(topHalf.get(i).get(j));
        }
        else if (j == topHalf.get(i).size() / 2) {
          rightRow.add(topHalf.get(i).get(j));
          leftRow.add(topHalf.get(i).get(j));
        }
        else {
          leftRow.add(topHalf.get(i).get(j));
        }
      }
    }

    // split the array into the bottom two quadrants
    ArrayList<ArrayList<Double>> bottomHalf = new ArrayList<ArrayList<Double>>();
    for (int i = quadrant.size() / 2; i < quadrant.size(); i++) {
      bottomHalf.add(quadrant.get(i));
    }
    ArrayList<ArrayList<Double>> bottomLeft = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<Double>> bottomRight = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i < bottomHalf.size(); i++) {
      leftRow = new ArrayList<Double>();
      rightRow = new ArrayList<Double>();
      bottomLeft.add(leftRow);
      bottomRight.add(rightRow);
      for (int j = 0; j < bottomHalf.get(i).size(); j++) {
        if (j > bottomHalf.get(i).size() / 2) {
          rightRow.add(bottomHalf.get(i).get(j));
        }
        else if (j == bottomHalf.get(i).size() / 2) {
          rightRow.add(bottomHalf.get(i).get(j));
          leftRow.add(bottomHalf.get(i).get(j));
        }
        else {
          leftRow.add(bottomHalf.get(i).get(j));
        }
      }
    }

    // calculate values
    this.calcQuadrant(topLeft);
    this.calcQuadrant(topRight);
    this.calcQuadrant(bottomLeft);
    this.calcQuadrant(bottomRight);

    return this.combineQuadrants(this.calcValuesGivenCorners(topLeft),
        this.calcValuesGivenCorners(topRight), this.calcValuesGivenCorners(bottomLeft), 
        this.calcValuesGivenCorners(bottomRight));
  }

  void calcQuadrant(ArrayList<ArrayList<Double>> quadrant) {
    Random rand = new Random();

    double topLeft = quadrant.get(0).get(0);
    double topRight = quadrant.get(0).get(quadrant.size() - 1);
    double bottomLeft = quadrant.get(quadrant.size() - 1).get(0);
    double bottomRight = quadrant.get(quadrant.size() - 1).get(quadrant.size() - 1);
    // mid top
    quadrant.get(0).set(quadrant.size() / 2, 
        (topRight + topLeft) / 2 + (rand.nextInt(RANDOM_NUDGE) - (RANDOM_NUDGE / 2)));
    // mid left
    quadrant.get(quadrant.size() / 2).set(0, 
        (topLeft + bottomLeft) / 2 + (rand.nextInt(RANDOM_NUDGE) - (RANDOM_NUDGE / 2)));
    // bottom mid
    quadrant.get(quadrant.size() - 1).set(quadrant.size() / 2, 
        (bottomLeft + bottomRight) / 2 + (rand.nextInt(RANDOM_NUDGE) - (RANDOM_NUDGE / 2)));
    // mid right
    quadrant.get(quadrant.size() / 2).set(quadrant.size() - 1, 
        (bottomRight + topRight) / 2 + (rand.nextInt(RANDOM_NUDGE) - (RANDOM_NUDGE / 2)));
    // middle value
    quadrant.get(quadrant.size() / 2).set(quadrant.size() / 2, 
        (topLeft + topRight + bottomLeft + bottomRight) / 4 
        + (rand.nextInt(RANDOM_NUDGE) - (RANDOM_NUDGE / 2)));
  }

  ArrayList<ArrayList<Double>> combineQuadrants(ArrayList<ArrayList<Double>> topLeft,
      ArrayList<ArrayList<Double>> topRight, ArrayList<ArrayList<Double>> bottomLeft,
      ArrayList<ArrayList<Double>> bottomRight) {

    ArrayList<ArrayList<Double>> combinedTop = topRight;
    ArrayList<ArrayList<Double>> combinedBottom = bottomRight;

    //make the top half of quadrant(ignore first coloum topLeft)
    for (int i = 0; i < topLeft.size(); i++) {
      for (int j = 1; j < topLeft.size(); j++) {
        combinedTop.get(i).add(topLeft.get(i).get(j));
      }
    }

    //make the bottom half of quadrant (ignore first colum bottom Left)
    for (int i = 0; i < bottomLeft.size(); i++) {
      for (int j = 1; j < bottomLeft.get(i).size(); j++) {
        combinedBottom.get(i).add(bottomLeft.get(i).get(j));
      }
    }

    //combine top and bottom (ignore first row of bottom)
    for (int i = 1; i < combinedBottom.size(); i++) {
      combinedTop.add(combinedBottom.get(i));
    }
    return combinedTop;
  }

  ArrayList<ArrayList<Cell>> setCellHeights(ArrayList<ArrayList<Double>> heights) {
    ArrayList<ArrayList<Cell>> cells = new ArrayList<ArrayList<Cell>>();
    ArrayList<Cell> row;

    for (int y = 0; y < heights.size(); y++) {
      row = new ArrayList<Cell>();
      cells.add(row);

      for (int x = 0; x < heights.get(y).size(); x++) {
        if (heights.get(y).get(x) <= 0) {
          row.add(new OceanCell(heights.get(y).get(x), x, y));
        }
        else {
          row.add(new Cell(heights.get(y).get(x), x, y));
        }
      }
    }
    return cells;
  }

  ArrayList<ArrayList<Cell>> setCellLinks(ArrayList<ArrayList<Cell>> cells) {
    for (int y = 0; y < cells.size(); y++) {
      for (int x = 0; x < cells.size(); x++) {
        // start with top, left, right, and bottom set to itself
        cells.get(y).get(x).top = cells.get(y).get(x);
        cells.get(y).get(x).left = cells.get(y).get(x);
        cells.get(y).get(x).right = cells.get(y).get(x);
        cells.get(y).get(x).bottom = cells.get(y).get(x);

        if (y > 0) { // if not on the top, set top
          cells.get(y).get(x).top = cells.get(y - 1).get(x);
        }
        if (y < cells.size() - 1) { // if not on bottom set bottom
          cells.get(y).get(x).bottom = cells.get(y + 1).get(x);
        }
        if (x > 0) { // if not on left set left
          cells.get(y).get(x).left = cells.get(y).get(x - 1);
        }
        if (x < cells.get(y).size() - 1) { // if not on right set right
          cells.get(y).get(x).right = cells.get(y).get(x + 1);
        }
      }
    }
    return cells;
  }

  public void makeStandardMountain() {
    this.board = Utils.twoDArrayToIList(setCellLinks(setCellHeights(getHeights())));
    this.setPlayer();
    this.setTargets();
    this.waterHeight = 0;
    this.tickCount = 0;
  }

  public void makeRandomMountain() {
    this.board = Utils.twoDArrayToIList(setCellLinks(setCellHeights(getRandomHeights())));
    this.setPlayer();
    this.setTargets();
    this.waterHeight = 0;
    this.tickCount = 0;
  }

  public void makeRandomTerrain() {
    this.board = Utils.twoDArrayToIList(setCellLinks(setCellHeights(
        calcValuesGivenCorners(getSubdivisHeights()))));
    this.setPlayer();
    this.setTargets();
    this.waterHeight = 0;
    this.tickCount = 0;
  }

  void handleFlooding() {
    for (Cell c : board) {
      c.updateFlooding(waterHeight);
    }
  }

  boolean isFloodedXY(int x, int y) {
    for (Cell c : board) {
      if (c.x == x && c.y == y) {
        return c.isFlooded;
      }
    }
    return false;
  }

  void setPlayer() {
    Random rand = new Random();
    int px = rand.nextInt(ISLAND_SIZE);
    int py = rand.nextInt(ISLAND_SIZE);
    if (isFloodedXY(px, py)) {
      this.setPlayer();
    }
    else {
      p = new Player(px, py);
      p2 = new Player(px, py);
    }
  }

  void setTargets() {
    Random rand = new Random();
    targets = new ArrayList<Target>();
    for (int i = 0; i < NUM_TARGETS; i++) {
      int px = rand.nextInt(ISLAND_SIZE);
      int py = rand.nextInt(ISLAND_SIZE);
      if (isFloodedXY(px, py)) {
        i--;
      }
      else {
        targets.add(new Target(px, py));
      }
    }
  }

  void checkTargets() {
    for (int i = 0; i < targets.size(); i++) {
      Target t = targets.get(i);
      if ((t.playerOnTarget(p) || t.playerOnTarget(p2)) && !t.isHeli()) {
        targets.remove(t);
      }
    }

    if (targets.size() == 0) {
      targets.add(this.makeHeli());
    }
  }

  HelicopterTarget makeHeli() {
    return new HelicopterTarget(this.highestCell().x, this.highestCell().y);
  }

  Cell highestCell() {
    Cell max = new Cell(Double.MIN_NORMAL, 0, 0);
    for (Cell c : board) {
      if (c.height > max.height) {
        max = c;
      }
    }
    return max;
  }

  @Override
  public void onTick() {
    this.checkTargets();
    if (this.tickCount >= this.tickWait) {
      this.waterHeight++;
      handleFlooding();
      this.tickCount = 0;
    }
    else {
      this.tickCount++;
    }
  }

  @Override
  public void onKeyEvent(String ke) {
    // TODO reset, new game
    if (ke.equals("right") && !isFloodedXY(p.x + 1, p.y)) {
      p.movePlayer(ke);
    }
    else if (ke.equals("left") && !isFloodedXY(p.x - 1, p.y)) {
      p.movePlayer(ke);
    }
    else if (ke.equals("up") && !isFloodedXY(p.x, p.y - 1)) {
      p.movePlayer(ke);
    }
    else if (ke.equals("down") && !isFloodedXY(p.x, p.y + 1)) {
      p.movePlayer(ke);
    }
    // p2 movement
    else if (ke.equals("d") && !isFloodedXY(p2.x + 1, p2.y)) {
      p2.movePlayer(ke);
    }
    else if (ke.equals("a") && !isFloodedXY(p2.x - 1, p2.y)) {
      p2.movePlayer(ke);
    }
    else if (ke.equals("w") && !isFloodedXY(p2.x, p2.y - 1)) {
      p2.movePlayer(ke);
    }
    else if (ke.equals("s") && !isFloodedXY(p2.x, p2.y + 1)) {
      p2.movePlayer(ke);
    }
    else if (ke.equals("m")) {
      this.makeStandardMountain();
    }
    else if (ke.equals("r")) {
      this.makeRandomMountain();
    }
    else if (ke.equals("t")) {
      this.makeRandomTerrain();
    }
  }

  @Override
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(ISLAND_SIZE * CELL_SIZE, ISLAND_SIZE * CELL_SIZE);
    for (Cell c : board) {
      scene.placeImageXY(c.makeImage(waterHeight), c.x * CELL_SIZE, c.y * CELL_SIZE);
    }
    for (Target t : targets) {
      scene.placeImageXY(t.makeImage(waterHeight), t.x * CELL_SIZE, t.y * CELL_SIZE);
    }
    scene.placeImageXY(p.makeImage(waterHeight, "P1"), p.x * CELL_SIZE, p.y * CELL_SIZE);
    scene.placeImageXY(p2.makeImage(waterHeight, "P2"), p2.x * CELL_SIZE, p2.y * CELL_SIZE);
    return scene;
  }

  // produce the last image of this world by adding text to the image
  @Override
  public WorldScene lastScene(String s) {
    WorldScene scene = this.makeScene();
    scene.placeImageXY(new TextImage(s, CELL_SIZE * 4, Color.red), (ISLAND_SIZE / 2) * CELL_SIZE,
        (ISLAND_SIZE / 2) * CELL_SIZE);
    return scene;
  }

  @Override
  public WorldEnd worldEnds() {
    if (this.isFloodedXY(p.x, p.y) || this.isFloodedXY(p2.x, p2.y)) {
      return new WorldEnd(true, this.lastScene("you drowned"));
    }
    else if (targets.size() == 1 && targets.get(0).isHeli() 
        && targets.get(0).playerOnTarget(p) && targets.get(0).playerOnTarget(p2)) {
      return new WorldEnd(true, this.lastScene("you escaped"));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }
}

class Utils {
  public static <T> IList<T> twoDArrayToIList(ArrayList<ArrayList<T>> twoDArray) {
    IList<T> newList = new MtList<T>();
    for (ArrayList<T> list : twoDArray) {
      for (T t : list) {
        newList = new ConsList<T>(t, newList);
      }
    }
    return newList;
  }
}

// a generic list: could hold anything!
interface IList<T> extends Iterable<T> {
  boolean isCons();

  ConsList<T> asCons();
}

class MtList<T> implements IList<T> {
  @Override
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }

  @Override
  public boolean isCons() {
    return false;
  }

  @Override
  public ConsList<T> asCons() {
    throw new ClassCastException("MtList cannot be cast to Cons");
  }
}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  @Override
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }

  @Override
  public boolean isCons() {
    return true;
  }

  @Override
  public ConsList<T> asCons() {
    return this;
  }
}

class IListIterator<T> implements Iterator<T> {
  IList<T> items;

  IListIterator(IList<T> items) {
    this.items = items;
  }

  @Override
  public boolean hasNext() {
    return this.items.isCons();
  }

  @Override
  public T next() {
    ConsList<T> asList = this.items.asCons();
    T item = asList.first;
    this.items = asList.rest;
    return item;
  }
}

class RunForbiddenIsland {
  public static void main(String[] args) {    
    ForbiddenIslandWorld randomTerrain = new ForbiddenIslandWorld();
    randomTerrain.makeRandomTerrain();
    randomTerrain.bigBang(ForbiddenIslandWorld.ISLAND_SIZE * ForbiddenIslandWorld.CELL_SIZE,
        ForbiddenIslandWorld.ISLAND_SIZE * ForbiddenIslandWorld.CELL_SIZE, 0.3);
  }
}

class ExamplesForbiddenIsland {
  ArrayList<Double> doubles;
  ArrayList<ArrayList<Double>> twoDDoubles;
  ArrayList<ArrayList<Double>> doublesMt;
  IList<Double> twoDDoublesIlist;

  ForbiddenIslandWorld standardMountain;
  ForbiddenIslandWorld randomMountain;
  ForbiddenIslandWorld randomTerrainM;
  Cell mid;
  Cell top;
  Cell left;
  Cell right;
  Cell bottom;
  Cell midNoL;
  Cell topNoL;
  Cell leftNoL;
  Cell rightNoL;
  Cell bottomNoL;
  OceanCell oceanTopLeftNoL;

  OceanCell oceanTopLeft;
  ArrayList<ArrayList<Double>> heights;
  ArrayList<ArrayList<Double>> heights2;
  ArrayList<ArrayList<Double>> randHeights;
  ArrayList<ArrayList<Cell>> cellHeights;
  ArrayList<ArrayList<Cell>> cellHeights2;
  ArrayList<ArrayList<Cell>> cellHeightsLinked;

  ArrayList<ArrayList<Double>> randomTerrainStarterHeights;
  ArrayList<ArrayList<Double>> randomTerrain;
  ArrayList<ArrayList<Double>> testQuadrant;
  ArrayList<ArrayList<Double>> testQuadrant2;
  ArrayList<Double> row1;
  ArrayList<Double> row2;
  ArrayList<Double> row3;
  ArrayList<Double> row1Big;
  ArrayList<Double> row2Big;
  ArrayList<Double> row3Big;
  ArrayList<Double> row4Big;
  ArrayList<Double> row5Big;

  void initData() {
    doublesMt = new ArrayList<ArrayList<Double>>();
    doubles = new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0, 4.1));
    twoDDoubles = new ArrayList<ArrayList<Double>>();
    twoDDoubles.add(doubles);
    twoDDoubles.add(doubles);
    twoDDoublesIlist = new ConsList<Double>(4.1,
        new ConsList<Double>(3.0, new ConsList<Double>(2.0,
            new ConsList<Double>(1.0, new ConsList<Double>(4.1, new ConsList<Double>(3.0,
                new ConsList<Double>(2.0, new ConsList<Double>(1.0, new MtList<Double>()))))))));

    standardMountain = new ForbiddenIslandWorld();
    standardMountain.makeStandardMountain();
    randomMountain = new ForbiddenIslandWorld();
    randomMountain.makeRandomMountain();
    randomTerrainM = new ForbiddenIslandWorld();
    randomTerrainM.makeRandomTerrain();
    mid = new Cell(32, 32, 32);
    mid.top = top;
    mid.bottom = bottom;
    mid.left = left;
    mid.right = right;
    midNoL = new Cell(32, 32, 32);

    top = new Cell(31, 32, 31);
    top.top = top;
    top.bottom = mid;
    top.left = top;
    top.right = top;
    topNoL = new Cell(31, 32, 31);

    left = new Cell(31, 31, 32);
    left.top = left;
    left.bottom = left;
    left.left = left;
    left.right = mid;
    leftNoL = new Cell(31, 31, 32);

    right = new Cell(31, 33, 32);
    right.top = right;
    right.bottom = right;
    right.left = mid;
    right.right = right;
    rightNoL = new Cell(31, 33, 32);

    bottom = new Cell(31, 32, 33);
    bottom.top = mid;
    bottom.bottom = bottom;
    bottom.left = bottom;
    bottom.right = bottom;
    bottomNoL = new Cell(31, 32, 33);

    oceanTopLeft = new OceanCell(-32, 0, 0);
    oceanTopLeft.top = oceanTopLeft;
    oceanTopLeft.bottom = oceanTopLeft;
    oceanTopLeft.left = oceanTopLeft;
    oceanTopLeft.right = oceanTopLeft;
    oceanTopLeftNoL = new OceanCell(-32, 0, 0);

    heights = standardMountain.getHeights();
    heights2 = standardMountain.getHeights();
    randHeights = randomMountain.getRandomHeights();
    cellHeights = standardMountain.setCellHeights(heights);
    cellHeights2 = standardMountain.setCellHeights(heights2);
    cellHeightsLinked = standardMountain.setCellLinks(cellHeights2);

    randomTerrainStarterHeights = randomTerrainM.getSubdivisHeights();
    randomTerrain = randomTerrainM.calcValuesGivenCorners(randomTerrainStarterHeights);
    row1 = new ArrayList<Double>(Arrays.asList(1.0, 2.0, 1.0));
    row2 = new ArrayList<Double>(Arrays.asList(3.0, 2.0, 3.0));
    row3 = new ArrayList<Double>(Arrays.asList(1.0, 2.0, 1.0));
    row1Big = new ArrayList<Double>(Arrays.asList(1.0, 2.0, 1.0, 2.0 , 1.0));
    row2Big = new ArrayList<Double>(Arrays.asList(3.0, 2.0, 3.0, 3.0 , 2.0));
    row3Big = new ArrayList<Double>(Arrays.asList(1.0, 2.0, 1.0, 2.0 , 1.0));
    row4Big = new ArrayList<Double>(Arrays.asList(3.0, 2.0, 3.0, 3.0 , 2.0));
    row5Big = new ArrayList<Double>(Arrays.asList(1.0, 2.0, 1.0, 2.0 , 1.0));
    testQuadrant = new ArrayList<ArrayList<Double>>();
    testQuadrant.add(row1);
    testQuadrant.add(row2);
    testQuadrant.add(row3);
    testQuadrant2 = new ArrayList<ArrayList<Double>>();
    testQuadrant2.add(row1Big);
    testQuadrant2.add(row2Big);
    testQuadrant2.add(row3Big);
    testQuadrant2.add(row4Big);
    testQuadrant2.add(row5Big);
  }

  void testTwoDArrayToIList(Tester t) {
    initData();
    t.checkExpect(Utils.twoDArrayToIList(twoDDoubles), twoDDoublesIlist);
    t.checkExpect(Utils.twoDArrayToIList(doublesMt), new MtList<Double>());
  }

  void testMakeScene(Tester t) {
    initData();
    standardMountain.makeScene();
    t.checkExpect(standardMountain.board.equals(new MtList<Cell>()), false);
  }

  void testGetCellColor(Tester t) {
    initData();
    t.checkExpect(mid.getCellColor(0), new Color(0, 155, 0, 0));
    t.checkExpect(mid.getCellColor(12), new Color(0, 155, 0, 0));
    t.checkExpect(top.getCellColor(0), new Color(0, 155, 0, 7));
    t.checkExpect(top.getCellColor(12), new Color(0, 155, 0, 7));

    t.checkExpect(left.getCellColor(0), new Color(0, 155, 0, 7));
    t.checkExpect(left.getCellColor(12), new Color(0, 155, 0, 7));

    t.checkExpect(right.getCellColor(0), new Color(0, 155, 0, 7));
    t.checkExpect(right.getCellColor(12), new Color(0, 155, 0, 7));

    t.checkExpect(bottom.getCellColor(0), new Color(0, 155, 0, 7));
    t.checkExpect(bottom.getCellColor(12), new Color(0, 155, 0, 7));

  }

  void testGetHeights(Tester t) {
    initData();
    t.checkExpect(heights.get(32).get(32), 32.0);
    t.checkExpect(heights.get(31).get(32), 31.0);
    t.checkExpect(heights.get(30).get(32), 30.0);
    t.checkExpect(heights.get(32).get(31), 31.0);
    t.checkExpect(heights.get(31).get(31), 30.0);
    t.checkExpect(heights.get(0).get(0), -32.0);
    t.checkExpect(heights.get(0).get(64), -32.0);
    t.checkExpect(heights.get(64).get(0), -32.0);
    t.checkExpect(heights.get(64).get(64), -32.0);
  }

  void testGetRandHeights(Tester t) {
    initData();
    t.checkExpect(randHeights.get(0).get(0), -32.0);
    t.checkExpect(randHeights.get(0).get(64), -32.0);
    t.checkExpect(randHeights.get(64).get(0), -32.0);
    t.checkExpect(randHeights.get(64).get(64), -32.0);
    t.checkNumRange(randHeights.get(32).get(32), 0, 33);
    t.checkNumRange(randHeights.get(30).get(32), 0, 33);
    t.checkNumRange(randHeights.get(44).get(32), 0, 33);
    t.checkNumRange(randHeights.get(32).get(26), 0, 33);
    t.checkNumRange(randHeights.get(38).get(26), 0, 33);
    t.checkNumRange(randHeights.get(34).get(34), 0, 33);
    t.checkNumRange(randHeights.get(25).get(32), 0, 33);
    t.checkNumRange(randHeights.get(37).get(32), 0, 33);
    t.checkNumRange(randHeights.get(32).get(38), 0, 33);
    t.checkNumRange(randHeights.get(32).get(37), 0, 33);
    t.checkNumRange(randHeights.get(32).get(36), 0, 33);
    t.checkNumRange(randHeights.get(32).get(35), 0, 33);
    t.checkNumRange(randHeights.get(32).get(32), 0, 33);
    t.checkNumRange(randHeights.get(44).get(32), 0, 33);
    t.checkNumRange(randHeights.get(22).get(32), 0, 33);
    t.checkNumRange(randHeights.get(19).get(32), 0, 33);
    t.checkNumRange(randHeights.get(18).get(32), 0, 33);
  }

  void testSetCellHeights(Tester t) {
    initData();
    t.checkExpect(cellHeights.get(32).get(32), midNoL);
    t.checkExpect(cellHeights.get(31).get(32), topNoL);
    t.checkExpect(cellHeights.get(33).get(32), bottomNoL);
    t.checkExpect(cellHeights.get(32).get(31), leftNoL);
    t.checkExpect(cellHeights.get(32).get(33), rightNoL);
    t.checkExpect(cellHeights.get(0).get(0), oceanTopLeftNoL);
  }

  void testSetCellLinks(Tester t) {
    initData();
    t.checkExpect(cellHeightsLinked.get(32).get(32).top.height, 31.0);
    t.checkExpect(cellHeightsLinked.get(32).get(32).bottom.height, 31.0);
    t.checkExpect(cellHeightsLinked.get(32).get(32).left.height, 31.0);
    t.checkExpect(cellHeightsLinked.get(32).get(32).right.height, 31.0);
    t.checkExpect(cellHeightsLinked.get(32).get(32).top.top.height, 30.0);
    t.checkExpect(cellHeightsLinked.get(32).get(32).bottom.bottom.height, 30.0);
    t.checkExpect(cellHeightsLinked.get(32).get(32).bottom.bottom.bottom.height, 29.0);
  }

  void testIterator(Tester t) {

    ArrayList<String> strings = new ArrayList<String>(Arrays.asList("a", "b", "c"));

    ArrayList<Integer> ints = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

    ArrayList<Boolean> bool = new ArrayList<Boolean>();

    Iterator<String> stringIter = strings.iterator();

    Iterator<Integer> intIter = ints.iterator();

    Iterator<Boolean> boolIter = bool.iterator();

    t.checkExpect(stringIter.hasNext(), true);

    t.checkExpect(stringIter.next(), "a");

    t.checkExpect(intIter.hasNext(), true);

    t.checkExpect(intIter.next(), 1);

    t.checkExpect(boolIter.hasNext(), false);
  }

  void testMakeImage(Tester t) {
    initData();
    t.checkExpect(mid.makeImage(0),
        new RectangleImage(10, 10, OutlineMode.SOLID, new Color(0, 155, 0, 0)));
    t.checkExpect(top.makeImage(0),
        new RectangleImage(10, 10, OutlineMode.SOLID, new Color(0, 155, 0, 7)));
    t.checkExpect(left.makeImage(0),
        new RectangleImage(10, 10, OutlineMode.SOLID, new Color(0, 155, 0, 7)));
    t.checkExpect(right.makeImage(0),
        new RectangleImage(10, 10, OutlineMode.SOLID, new Color(0, 155, 0, 7)));
    t.checkExpect(bottom.makeImage(0),
        new RectangleImage(10, 10, OutlineMode.SOLID, new Color(0, 155, 0, 7)));
    t.checkExpect(cellHeightsLinked.get(32).get(32).top.makeImage(0),
        new RectangleImage(10, 10, OutlineMode.SOLID, new Color(0, 155, 0, 7)));
  }

  void testGetSubdivisionHeights(Tester t) {
    initData();
    t.checkExpect(randomTerrainStarterHeights.get(0).get(0), 0.0);
    t.checkExpect(randomTerrainStarterHeights.get(0).get(32), 1.0);
    t.checkExpect(randomTerrainStarterHeights.get(32).get(0), 1.0);
    t.checkExpect(randomTerrainStarterHeights.get(64).get(32), 1.0);
    t.checkExpect(randomTerrainStarterHeights.get(32).get(32), 32.0);
    t.checkExpect(randomTerrainStarterHeights.get(32).get(64), 1.0);
    t.checkExpect(randomTerrainStarterHeights.get(17).get(4), 0.0);
  }

  void testCalcValuesGivenCorner(Tester t) {
    initData();
    t.checkNumRange(randomTerrain.get(4).get(17), -64, 64);
    t.checkNumRange(randomTerrain.get(5).get(17), -64, 64);
    t.checkNumRange(randomTerrain.get(17).get(17), -64, 64);
    t.checkNumRange(randomTerrain.get(4).get(44), -64, 64);
    t.checkNumRange(randomTerrain.get(5).get(19), -64, 64);
    t.checkNumRange(randomTerrain.get(17).get(60), -64, 64);
  }

  void testCalcQuadrant(Tester t) {
    initData();
    randomTerrainM.calcQuadrant(testQuadrant);
    t.checkExpect(testQuadrant.size(), 3);
    t.checkExpect(testQuadrant.get(0).get(0), 1.0);
    t.checkExpect(testQuadrant.get(2).get(0), 1.0);
    t.checkExpect(testQuadrant.get(2).get(0), 1.0);
    t.checkExpect(testQuadrant.get(0).get(2), 1.0);
    t.checkNumRange(testQuadrant.get(0).get(1), -18, 18);
    t.checkNumRange(testQuadrant.get(1).get(1), -18, 18);
    t.checkNumRange(testQuadrant.get(2).get(1), -18, 18);
  }

  // this is commented out because depending on your java settings you may 
  // get the following error: java.lang.OutOfMemoryError: Java heap space
  // we didn't want to cause any problem on a graders machine so we commented it out
  //  void testCombineQuadrant(Tester t) {
  //    t.checkExpect(testCombineQuadrantsHelp(t), true);
  //  }
  //  
  //  boolean testCombineQuadrantsHelp(Tester t) {
  //    initData();
  //    ArrayList<ArrayList<Double>> combinded = randomTerrainM.combineQuadrants(testQuadrant, 
  //        testQuadrant, testQuadrant, testQuadrant);  
  //    for (int i = 0; i < combinded.size(); i++) {
  //      for (int j = 0; j < combinded.get(i).size(); j++) {
  //        if (!(combinded.get(i).get(j) == testQuadrant2.get(i).get(j))) {
  //          return false;
  //        }
  //      }
  //    }
  //    return true;
  //  }
}