//// Assignment 5
//// Scruggs Malcolm
//// scruggs
//// Paul Scherlek
//// scherpa
//
//import java.awt.Color;
//import java.util.Random;
//
//import tester.*;
//import javalib.funworld.*;
//import javalib.worldimages.*;
//
////to represent a player
//class Player {
//  Posn center;
//  int size;
//  Color col;
//  boolean isDead;
//
//  // The constructor 
//  Player(Posn center, int size, Color col) {
//    this.center = center; 
//    this.size = size; 
//    this.col = col;
//    this.isDead = false;
//  }
//
//  Player(Posn center, int size, Color col, boolean isDead) {
//    this.center = center; 
//    this.size = size; 
//    this.col = col;
//    this.isDead = isDead;
//  }
//
//
//  /* TEMPLATE
//   *
//   * FIELDS:
//   * ... this.center ...    -- Posn
//   * ... this.size ...      -- int
//   * ... this.col ...       -- Color
//   * ... this.isDead ...    -- Boolean
//   *
//   * METHODS:
//   * ... this.movePlayer(String) ...              -- Player
//   * ... this.makeImage() ...                     -- WorldImage
//   * ... this.exitLeft(int, int) ...              -- boolean
//   * ... this.exitRight(int, int) ...             -- boolean
//   * ... this.exitBottom(int, int) ...            -- boolean
//   * ... this.exitTop(int, int) ...               -- boolean
//   * ... this.canEat(BackgroundFish) ...          -- boolean
//   * ... this.collisionsHelp(BackgroundFish) ...  -- boolean
//   * ... this.collisions(BackgroundFish) ...      -- Player
//   * ... this.biggestFish() ...                   -- boolean
//   *
//   */
//
//  // moves this player depending on given string
//  public Player movePlayer(String ke) {
//    if (ke.equals("right")) {
//      return new Player(new Posn(this.center.x + 12, this.center.y),
//          this.size, this.col);
//    } else if (ke.equals("left")) {
//      return new Player(new Posn(this.center.x - 12, this.center.y),
//          this.size, this.col);
//    } else if (ke.equals("up")) {
//      return new Player(new Posn(this.center.x, this.center.y - 12),
//          this.size, this.col);
//    } else if (ke.equals("down")) {
//      return new Player(new Posn(this.center.x, this.center.y + 12),
//          this.size, this.col);
//    } else {
//      return this;
//    }
//  }
//
//  //produce the image of this player . 
//  public WorldImage makeImage() {
//    return new RectangleImage(this.size, this.size,  OutlineMode.SOLID, col);
//  }
//
//  // returns true if a fish has exited the game from the left
//  public boolean exitLeft(int width, int height) {
//    return this.center.x < 0;
//  }
//
//  // returns true if a fish has exited the game from the right
//  public boolean exitRight(int width, int height) {
//    return this.center.x > width;
//  }
//
//  // returns true if a fish has exited the game from the bottom
//  public boolean exitBottom(int width, int height) {
//    return this.center.y < 0;
//  }
//
//  // returns true if a fish has exited the game from the top
//  public boolean exitTop(int width, int height) {
//    return this.center.y > height;
//  }
//
//  // determine if this player can eat given background fish
//  public boolean canEat(BackgroundFish other) {
//    return this.size > other.size;
//  }
//
//  // did player collide with a given background fish
//  public boolean collisionsHelp(BackgroundFish other) {
//    return (other.center.x - (other.size / 2) <= this.center.x + (this.size / 2)
//        && other.center.x + (other.size / 2) >= this.center.x - (this.size / 2))
//        && (other.center.y - (other.size / 2) <= this.center.y + (this.size / 2)
//        && other.center.y + (other.size / 2) >= this.center.y - (this.size / 2));
//  }
//
//  // handle actions upon collision with another fish 
//  public Player collisions(BackgroundFish other) {
//    if (this.isDead) {
//      return this;
//    }
//    else if (this.canEat(other) && this.collisionsHelp(other)) { 
//      return new Player(this.center, this.size + (int) Math.sqrt(other.size), this.col);
//    }
//    else if (this.collisionsHelp(other)) {
//      return new Player(this.center, this.size, this.col, true);
//    }
//    else { 
//      return this; 
//    }
//  }
//
//  // determine whether player is the biggest fish in the sea
//  public boolean biggestFish(int maxSize) {
//    return this.size > maxSize;
//  }
//}
//
////to represent a background fish (the enemy fish)
//class BackgroundFish {
//  Posn center;
//  int size;
//  Color col;
//  boolean movingLeft;
//
//  BackgroundFish(Posn center, int size, Color col, boolean movingLeft) {
//    this.center = center; 
//    this.size = size; 
//    this.col = col;
//    this.movingLeft = movingLeft;
//  }
//
//  /* TEMPLATE
//   *
//   * FIELDS:
//   * ... this.center ...                          -- Posn
//   * ... this.size ...                            -- int
//   * ... this.col ...                             -- Color
//   * ... this.movingLeft ...                      -- Boolean
//   *
//   * METHODS:
//   * ... this.makeImage() ...                     -- WorldImage
//   * ... this.moveBGFish() ...                    -- BackgroundFish
//   * ... this.collisionsHelp(BackgroundFish) ...  -- boolean
//   *
//   *
//   */
//
//  // generate random integer given a minimum and maximum integer
//  int randomInt(int min, int max) {
//    return min + (new Random().nextInt(max - min));
//  }
//
//  // returns a new background fish with given attributes
//  public WorldImage makeImage() {
//    return new RectangleImage(this.size, this.size,  OutlineMode.SOLID, col);
//  }
//
//  //is the background fish outside the bounds given by the width and height 
//  boolean outsideBounds(int width, int height) {
//    return this.center.x < 0 || this.center.x > width || this.center.y < 0
//        || this.center.y > height;
//  }
//
//  // moves the fish acording to the direction it should be going in. Bigger fish move slower
//  public BackgroundFish moveBGFish() {
//    if (movingLeft) {
//      return new BackgroundFish(new Posn((int) (this.center.x - (12 - Math.sqrt(size))), 
//          this.center.y), this.size, this.col, this.movingLeft);
//    }
//    else {
//      return new BackgroundFish(new Posn((int) (this.center.x + (12 - Math.sqrt(size))), 
//          this.center.y), this.size, this.col, this.movingLeft);
//    }
//  }
//
//  // determine whether given player has collided with this background fish
//  public boolean collisionsHelp(Player other) {
//    return other.collisionsHelp(this);
//  }
//}
//
////to represent the fishgame
//class FishGame extends World {  
//  int width = 600;
//  int height = 600;
//  int maxSize = 75;
//  Player player;
//  BackgroundFish b1;
//  BackgroundFish b2;
//  BackgroundFish b3;
//  BackgroundFish b4;
//  BackgroundFish b5;
//
//  // The constructor 
//  public FishGame(Player player) {
//    super();
//    this.player = player;
//    this.b1 = new BackgroundFish(new Posn(0 - width, 0), 10, Color.GREEN, false);
//    this.b2 = new BackgroundFish(new Posn(0 - width, 0), 10, Color.magenta, false);
//    this.b3 = new BackgroundFish(new Posn(0 - width, 0), 10, Color.orange, false);
//    this.b4 = new BackgroundFish(new Posn(0 - width, 0), 10, Color.YELLOW, false);
//    this.b5 = new BackgroundFish(new Posn(0 - width, 0), 10, Color.WHITE, false);
//  }
//
//  public FishGame(Player player, BackgroundFish b1, BackgroundFish b2, BackgroundFish b3,
//      BackgroundFish b4, BackgroundFish b5) {
//    super();
//    this.player = player;
//    this.b1 = b1;
//    this.b2 = b2;
//    this.b3 = b3;
//    this.b4 = b4;
//    this.b5 = b5;
//  }
//
//  /* TEMPLATE
//   *
//   * FIELDS:
//   * ... this.center ...                          -- Posn
//   * ... this.size ...                            -- int
//   * ... this.col ...                             -- Color
//   * ... this.movingLeft ...                      -- Boolean
//   *
//   * METHODS:
//   * ... this.onKeyEvent(String) ...              -- World
//   * ... this.onTick() ...                        -- World
//   * ... this.playerBounds()...                   -- Player
//   * ... this.handleCollisions(player) ...        -- PLayer
//   * ... this.handleBounds(BackgroundFish)...     -- BackgroundFish
//   * ... this.background()...                     -- WorldImage
//   * ... this.makeScene() ...                     -- WorldScene
//   * ... this.lastScene() ...                     -- WorldScene
//   * ... this.worldEnds() ...                     -- WorldEnd
//   * 
//   *
//   */
//
//  // Move the Player when the player presses a key 
//  public World onKeyEvent(String ke) {
//    if (ke.equals("x")) {
//      return this.endOfWorld("Goodbye");
//    }
//    else {
//      return new FishGame(this.player.movePlayer(ke), this.b1, this.b2, this.b3, this.b4, this.b5);
//    }
//  }
//
//  //Handle player collisions, reset player if out of bounds, and generate new background fish
//  // if they have gone off screen
//
//  public World onTick() {
//    //return new BlobWorldFun(this.blob.randomMove(5));
//    return new FishGame(handleCollisions(this.playerBounds()), this.handleBounds(b1),
//        this.handleBounds(b2), this.handleBounds(b3), this.handleBounds(b4), this.handleBounds(b5));
//  }
//
//  // handle the players location, moiving it to the other side of screen if it goes off
//  public Player playerBounds() {
//    if (this.player.exitLeft(width, height)) {
//      return new Player(new Posn(width, this.player.center.y),
//          this.player.size, this.player.col);
//    }
//    else if (this.player.exitRight(width, height)) {
//      return new Player(new Posn(0, this.player.center.y),
//          this.player.size, this.player.col);
//    }
//    else if (this.player.exitBottom(width, height)) {
//      return new Player(new Posn(this.player.center.x, height),
//          this.player.size, this.player.col);
//    }
//    else if (this.player.exitTop(width, height)) {
//      return new Player(new Posn(this.player.center.x, 0),
//          this.player.size, this.player.col);
//    }
//    else {
//      return this.player;
//    }
//  }
//
//  // determine whether player had hit background fish, and adjust size acordingly
//  public Player handleCollisions(Player p) {
//    return p.collisions(b1).collisions(b2).collisions(b3).collisions(b4).collisions(b5);
//  }
//
//  // makes a new random backgroundfish if the given one is outside of bounds
//  public BackgroundFish handleBounds(BackgroundFish bgFish) {
//    if (bgFish.outsideBounds(width, height) || this.player.collisionsHelp(bgFish)) {
//      int randSize = bgFish.randomInt(5, maxSize);
//      int randY = bgFish.randomInt(0, height);
//      int randLeft = bgFish.randomInt(0, 2);
//      boolean newToLeft;
//      int startX;
//      if (randLeft > 0) {
//        newToLeft = false;
//      }
//      else {
//        newToLeft = true;
//      }
//      if (newToLeft) {
//        startX = width;
//      }
//      else {
//        startX = 0;
//      }
//      return new BackgroundFish(new Posn(startX, randY), randSize, bgFish.col, newToLeft);
//    }
//    else {
//      return bgFish.moveBGFish();
//    }
//  }
//
//
//  // The entire background image for this world 
//  public WorldImage background = new RectangleImage(this.width,
//      this.height, OutlineMode.SOLID, Color.BLUE);
//
//  // produce the image of this world by adding the moving blob to the
//  // background image
//  public WorldScene makeScene() {
//    return this
//        .getEmptyScene()
//        .placeImageXY(this.background, this.width / 2, this.height / 2)
//        .placeImageXY(this.player.makeImage(), this.player.center.x,
//            this.player.center.y)
//        .placeImageXY(this.b1.makeImage(), this.b1.center.x, this.b1.center.y)
//        .placeImageXY(this.b2.makeImage(), this.b2.center.x, this.b2.center.y)
//        .placeImageXY(this.b3.makeImage(), this.b3.center.x, this.b3.center.y)
//        .placeImageXY(this.b4.makeImage(), this.b4.center.x, this.b4.center.y)
//        .placeImageXY(this.b5.makeImage(), this.b5.center.x, this.b5.center.y);
//  }
//
//  // produce the last image of this world by adding text to the image 
//  public WorldScene lastScene(String s) {
//    return this.makeScene().placeImageXY(new TextImage(s, Color.red), 100,
//        40);
//  }
//
//  // end the world if player is biggest fish, of if player has been eaten
//  public WorldEnd worldEnds() {
//    if (this.player.biggestFish(maxSize)) {
//      return new WorldEnd(true, this.makeScene().placeImageXY(
//          new TextImage("You are the biggest fish", 13, FontStyle.BOLD_ITALIC, Color.red),
//          100, 40));
//    }
//    else if (this.player.isDead) {
//      return new WorldEnd(true, this.makeScene().placeImageXY(
//          new TextImage("You have been eaten", 20, FontStyle.BOLD_ITALIC, Color.red),
//          100, 40)); 
//    }
//    else {
//      return new WorldEnd(false, this.makeScene());
//    }
//  }
//}
//
//class RunFishGame {
//  public static void main(String[] args) {
//    FishGame p = new FishGame(new Player(new Posn(300, 300), 40,
//        Color.RED));
//    p.bigBang(600, 600, 0.01);
//  }
//}
//
//class ExamplesFishGame {
//  // player examples
//  Player p1 = new Player(new Posn(300, 300), 40, Color.RED);
//  Player p2 = new Player(new Posn(0,0), 25, Color.RED);
//  Player p3 = new Player(new Posn(-10, 300), 77, Color.RED);
//  Player p4 = new Player(new Posn(-10, 300), 77, Color.RED, true);
//  Player p5 = new Player(new Posn(700, 300), 40, Color.RED);
//  Player p6 = new Player(new Posn(300, -10), 40, Color.RED);
//  Player p7 = new Player(new Posn(300, 700), 40, Color.RED);
//  Player p8 = new Player(new Posn(295, 300), 40, Color.RED);
//  Player p9 = new Player(new Posn(305, 300), 40, Color.RED);
//  Player p10 = new Player(new Posn(300, 295), 40, Color.RED);
//  Player p11 = new Player(new Posn(300, 305), 40, Color.RED);
//
//  // BackgroundFish examples
//  BackgroundFish back1 = new BackgroundFish(new Posn(300,300), 40, Color.RED, true);
//  BackgroundFish back2 = new BackgroundFish(new Posn(300,300), 35, Color.RED, false);
//  BackgroundFish back3 = new BackgroundFish(new Posn(0, 0), 25, Color.RED, true);
//  BackgroundFish back4 = new BackgroundFish(new Posn(0, 0), 25, Color.RED, false);
//  BackgroundFish back5 = new BackgroundFish(new Posn(-10, 300), 75, Color.RED, true);
//  BackgroundFish back6 = new BackgroundFish(new Posn(294,300), 40, Color.RED, true);
//  BackgroundFish back7 = new BackgroundFish(new Posn(25,25), 40, Color.RED, true);
//  BackgroundFish back8 = new BackgroundFish(new Posn(200,300), 40, Color.RED, true);
//  BackgroundFish back9 = new BackgroundFish(new Posn(300,100), 40, Color.RED, true);
//  BackgroundFish back10 = new BackgroundFish(new Posn(150,150), 20, Color.RED, true);
//
//  // FishGame examples
//  FishGame g1 = new FishGame(p1);
//  FishGame g2 = new FishGame(p2);
//  FishGame g3 = new FishGame(p3);
//  FishGame g4 = new FishGame(p1, back1, back2, back3, back4, back5);
//  FishGame g5 = new FishGame(p2, back1, back2, back3, back4, back5);
//  FishGame g6 = new FishGame(p2, back1, back1, back1, back1, back1);
//  FishGame g7 = new FishGame(p2, back6, back6, back6, back6, back6);
//  FishGame g8 = new FishGame(p4);
//
//  // test the method makeImage()
//  boolean testMakeImage(Tester t) {
//    return t.checkExpect(p1.makeImage(),
//        new RectangleImage(40, 40,  OutlineMode.SOLID, Color.RED))
//        && t.checkExpect(back1.makeImage(),
//            new RectangleImage(40, 40,  OutlineMode.SOLID, Color.RED));
//  }
//
//  //test the method movePlayer()
//  boolean testMovePlayer(Tester t) {
//    return t.checkExpect(p1.movePlayer("right"), new Player(new Posn(305, 300), 40, Color.RED))
//        && t.checkExpect(p1.movePlayer("left"), new Player(new Posn(295, 300), 40, Color.RED))
//        && t.checkExpect(p1.movePlayer("up"), new Player(new Posn(300, 295), 40, Color.RED))
//        && t.checkExpect(p1.movePlayer("down"), new Player(new Posn(300, 305), 40, Color.RED));
//  }
//
//  //test the method exitLeft()
//  boolean testExitLeft(Tester t) {
//    return t.checkExpect(p3.exitLeft(600, 600), true)
//        && t.checkExpect(p5.exitLeft(600, 600), false) 
//        && t.checkExpect(p6.exitLeft(600, 600), false)
//        && t.checkExpect(p7.exitLeft(600, 600), false);
//  }
//
//  // test the method exitRight()
//  boolean testExitRight(Tester t) {
//    return t.checkExpect(p3.exitRight(600, 600), false)
//        && t.checkExpect(p5.exitRight(600, 600), true) 
//        && t.checkExpect(p6.exitRight(600, 600), false)
//        && t.checkExpect(p7.exitRight(600, 600), false);
//  }
//
//  // test the method exitBottom()
//  boolean testExitBottom(Tester t) {
//    return t.checkExpect(p3.exitBottom(600, 600), false)
//        && t.checkExpect(p5.exitBottom(600, 600), false) 
//        && t.checkExpect(p6.exitBottom(600, 600), true)
//        && t.checkExpect(p7.exitBottom(600, 600), false);
//  }
//
//  // test the method exitTop()
//  boolean testExitTop(Tester t) {
//    return t.checkExpect(p3.exitTop(600, 600), false)
//        && t.checkExpect(p5.exitTop(600, 600), false) 
//        && t.checkExpect(p6.exitTop(600, 600), false)
//        && t.checkExpect(p7.exitTop(600, 600), true);
//  }
//
//  // test the method canEat()
//  boolean testCanEat(Tester t) {
//    return t.checkExpect(p1.canEat(back1), false)
//        && t.checkExpect(p1.canEat(back3), true)
//        && t.checkExpect(p3.canEat(back5), true);
//  }
//
//  //test the method collisionHelp()
//  boolean testCollisionsHelp(Tester t) {
//    return t.checkExpect(p1.collisionsHelp(back1), true)
//        && t.checkExpect(p1.collisionsHelp(back7), false)
//        && t.checkExpect(p1.collisionsHelp(back8), false)
//        && t.checkExpect(p1.collisionsHelp(back9), false)
//        && t.checkExpect(p1.collisionsHelp(back7), false)
//        && t.checkExpect(back1.collisionsHelp(p1), true)
//        && t.checkExpect(back7.collisionsHelp(p1), false)
//        && t.checkExpect(back8.collisionsHelp(p1), false)
//        && t.checkExpect(back9.collisionsHelp(p1), false)
//        && t.checkExpect(back7.collisionsHelp(p1), false);
//  }
//
//  // test the method collisions(BackgroundFish other)
//  boolean testCollisions(Tester t) {
//    return t.checkExpect(p1.collisions(back1), new Player(new Posn(300, 300), 40, Color.RED, true))
//        && t.checkExpect(p1.collisions(back3), p1)
//        && t.checkExpect(p1.collisions(back2), new Player(new Posn(300, 300), 45, Color.RED))
//        && t.checkExpect(p3.collisions(back4), p3)
//        && t.checkExpect(p3.collisions(back3), p3);
//  }
//
//  // test the method biggestFish(int maxSize)
//  boolean testBiggestFish(Tester t) {
//    return t.checkExpect(p1.biggestFish(75), false)
//        && t.checkExpect(p2.biggestFish(75), false)
//        && t.checkExpect(p3.biggestFish(75), true)
//        && t.checkExpect(p4.biggestFish(75), true);
//  }
//
//  // test the method randomInt(int min, int max)
//  boolean testRandomInt(Tester t) {
//    return t.checkNumRange(back1.randomInt(0, 75), 0, 75)
//        && t.checkOneOf("test randomInt", this.back1.randomInt(0,5), 
//            0, 1, 2, 3, 4);
//  }
//
//  // test the method(outsideBounds(int width, int height)
//  boolean testOutsideBounds(Tester t) {
//    return t.checkExpect(back1.outsideBounds(600, 600), false)
//        && t.checkExpect(back1.outsideBounds(200, 200), true)
//        && t.checkExpect(back1.outsideBounds(300, 300), false);
//  }
//
//  // test the method moveBGFish()
//  boolean testMoveBGFish(Tester t) {
//    return t.checkExpect(back1.moveBGFish(), 
//        new BackgroundFish(new Posn(294,300), 40, Color.RED, true))
//        && t.checkExpect(back2.moveBGFish(), 
//            new BackgroundFish(new Posn(306,300), 35, Color.RED, false));
//  }
//
//  // test the method onKeyEvent(String ke)
//  boolean testOnKeyEvent(Tester t) {
//    return t.checkExpect(g1.onKeyEvent("a"), g1)
//        && t.checkExpect(g1.onKeyEvent("left"), new FishGame(p8))
//        && t.checkExpect(g1.onKeyEvent("right"), new FishGame(p9))
//        && t.checkExpect(g1.onKeyEvent("up"), new FishGame(p10))
//        && t.checkExpect(g1.onKeyEvent("down"), new FishGame(p11));      
//  }
//
//  // test the method onTick()
//  boolean testOnTick(Tester t) {
//    return t.checkExpect(g6.onTick(), g7);
//  }
//
//  // test the method woeldEnds()
//  boolean testWorldEnds(Tester t) {
//    return t.checkExpect(g3.worldEnds(), new WorldEnd(true, g3.makeScene().placeImageXY(
//        new TextImage("You are the biggest fish", 13,
//            FontStyle.BOLD_ITALIC, Color.red),
//        100, 40)))
//        && t.checkExpect(g8.worldEnds(), new WorldEnd(true, g8.makeScene().placeImageXY(
//            new TextImage("You have been eaten", 20, 
//                FontStyle.BOLD_ITALIC, Color.red),
//            100, 40)))
//        && t.checkExpect(g1.worldEnds(), new WorldEnd(false, g1.makeScene()));
//  }
//}