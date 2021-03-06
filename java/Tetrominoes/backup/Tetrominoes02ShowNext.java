/**
 * @author Micha� Dettlaff
 * @version 1.2
 */

/*
 * <applet width=385 height=480 code="Tetrominoes.class">
 * <param name="backgroundColor" value="#FFFFFF">
 * <param name="boardColor" value="#C0C0C0">
 * </applet>
 */

import java.applet.Applet;
import java.applet.AudioClip;
import java.lang.Thread;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Date;

/**
 * Gra w stylu klasycznego Tetrisa.
 */
public class Tetrominoes extends Applet
    implements Runnable, KeyListener {

  static final int BOARD_WIDTH=10;
  static final int BOARD_HEIGHT=22;
  static final int BLOCK_SIZE=24;
  Random rand;
  Thread t;
  boolean threadSuspended;
  Graphics buffer;
  Image bufferImage;
  Color backgroundColor;
  Color boardColor;
  int width, height;
  int initSpeed, speed;
  int score;
  int lines;
  int level;
  boolean showTitle;
  boolean gameOver;
  boolean musicOn;
  AudioClip themeMusic;
  Color[][] board;
  Tetromino tetromino;
  Tetromino nextTetromino;

  public void init() {
    rand = new Random((new Date()).getTime());
    t = null;
    width=getWidth();
    height=getHeight();
    backgroundColor = Color.WHITE;
    boardColor = new Color(192, 192, 192);
    if (getParameter("backgroundColor") != null)
      backgroundColor = Color.decode(getParameter("backgroundColor"));
    if (getParameter("boardColor") != null)
      boardColor = Color.decode(getParameter("boardColor"));
    setBackground(backgroundColor);
    themeMusic = getAudioClip(getCodeBase(), "theme.au");
    musicOn=true;
    initSpeed=speed=600;

    bufferImage = createImage(width, height);
    buffer = bufferImage.getGraphics();

    addKeyListener(this);
  }

  public void destroy() {
    removeKeyListener(this);
  }

  public void run() {
    showTitle();
    newGame();
    while (true) {
      paintBuffer();
      delay(speed);
      tetromino.dropSoft();
      if (isConflict(tetromino, board)) {
	tetromino.undoDropSoft();
	stickOn(tetromino, board);
	clearFullLines(board);
	tetromino = nextTetromino;
	nextTetromino = getRandomTetromino(new TetrominoShapes(), rand);
	if (isConflict(tetromino, board)) {
	  gameOver();
       	}
      }
    }
  }

  void showTitle() {
    board = new Color[BOARD_WIDTH][BOARD_HEIGHT];
    tetromino = new Tetromino();
    nextTetromino = new Tetromino();
    nextTetromino.hide();
    showTitle=true;
    paintBuffer();
    while (showTitle);
  }

  void newGame() {
    board = new Color[BOARD_WIDTH][BOARD_HEIGHT];
    tetromino = getRandomTetromino(new TetrominoShapes(), rand);
    nextTetromino = getRandomTetromino(new TetrominoShapes(), rand);
    score=0;
    lines=0;
    level=0;
    if (musicOn) {
      themeMusic.play();
    }
  }

  void gameOver() {
    gameOver=true;
    paintBuffer();
    while (gameOver); // pauza az do wcisniecia entera lub spacji
    newGame();
  }

  /** Przykleja tetromino do planszy na stale. */
  void stickOn(Tetromino tetromino, Color[][] board) {
    int x, y;
    tetromino.immobilize();
    for (int j=0; j < 4; j++) {
      for (int i=0; i < 4; i++) {
	if (tetromino.shape[i][j] == 1) {
	  x=tetromino.position.x+i;
	  y=tetromino.position.y+j;
	  board[x][y]=tetromino.color;
	}
      }
    }
  }

  void clearFullLines(Color[][] board) {
    boolean isLineFull;
    int linesCleared=0;

    tetromino.hide();
    for (int j=BOARD_HEIGHT-1; j >= 0; j--) { // zaczynamy od dolu planszy
      isLineFull=true;
      for (int i=0; i < BOARD_WIDTH; i++) {
	if (board[i][j] == null) {
	  isLineFull=false;
	}
      }
      if (isLineFull) { // skasuj j-ta linie
	for (int l=j; l > 0; l--) {
	  for (int k=0; k < BOARD_WIDTH; k++) {
	    board[k][l] = board[k][l-1];
	  }
	}
	for (int k=0; k < BOARD_WIDTH; k++) {
	  board[k][0] = null;
	}
	linesCleared++;
	j++;
	if (linesCleared > 0) {
	  paintBuffer();
	  delay(200);
	}
      }
    }
    if (linesCleared > 0) {
      switch (linesCleared) {
	case 1:
	  score += 40*(level+1);
	  break;
	case 2:
	  score += 100*(level+1);
	  break;
	case 3:
	  score += 300*(level+1);
	  break;
	case 4:
	  score += 1200*(level+1);
	  break;
      }
      lines += linesCleared;
      speed = initSpeed - level * 20;
      level = lines / 10;
      if (level > 20) { level=20; }
    }
  }

  Tetromino getRandomTetromino(TetrominoShapes tetrominoes, Random rand) {
    Tetromino tetromino = new Tetromino();
    switch (rand.nextInt(7)) { // losujemy rodzaj tetromina
      case 0:
	tetromino = tetrominoes.I;
	tetromino.setColor(Color.CYAN);
	break;
      case 1:
	tetromino = tetrominoes.J;
	tetromino.setColor(Color.BLUE);
	break;
      case 2:
	tetromino = tetrominoes.L;
	tetromino.setColor(Color.ORANGE);
	break;
      case 3:
	tetromino = tetrominoes.O;
	tetromino.setColor(Color.YELLOW);
	tetromino.moveRight(); // zeby bylo na srodku planszy
	break;
      case 4:
	tetromino = tetrominoes.S;
	tetromino.setColor(Color.GREEN);
	break;
      case 5:
	tetromino = tetrominoes.T;
	tetromino.setColor(Color.MAGENTA);
	break;
      case 6:
	tetromino = tetrominoes.Z;
	tetromino.setColor(Color.RED);
	break;
    }
    tetromino.rotateCW(); // po inicjalizacji przekrecone, trzeba odkrecic
    return tetromino;
  }

  boolean isConflict(Tetromino tetromino, Color[][] board) {
    int x, y;
    for (int j=0; j < 4; j++) {
      for (int i=0; i < 4; i++) {
	if (tetromino.shape[i][j] == 1) {
	  x=tetromino.position.x+i;
	  y=tetromino.position.y+j;
	  if (x < 0 || x > BOARD_WIDTH-1 || y < 0 || y > BOARD_HEIGHT-1) {
	    return true;
	  } else if (board[x][y] != null) { // null oznacza puste pole
	    return true;
	  }
	}
      }
    }
    return false;
  }

  public void start() {
    if (t == null) {
      t = new Thread(this);
      threadSuspended = false;
      t.start();
    }
    else {
      paintBuffer();
      if (threadSuspended) {
	threadSuspended = false;
	synchronized(this) {
	  notify();
	}
      }
    }
  }

  public void stop() {
    threadSuspended=true;
  }

  /** Czeka zadana ilosc milisekund. */
  void delay(int interval) {
    try {
      t.sleep(interval);
      // teraz watek sprawdza czy powinien sie wstrzymac
      if (threadSuspended) {
	synchronized(this) {
	  while (threadSuspended)
	    wait();
	}
      }
    }
    catch (InterruptedException e) { }
  }

  public void paint(Graphics g) {
    update(g);
  }

  void paintBuffer() {
    drawFrameInBuffer();
    repaint();
  }

  public void update(Graphics g) {
    g.drawImage(bufferImage, 0, 0, this);
    getToolkit().sync(); // ponoc polepsza wyglad w niektorych systemach
  }

  void drawFrameInBuffer() {
    buffer.clearRect(0, 0, width, height);
    buffer.setColor(boardColor);
    // widzialna czesc planszy jest o dwie linie nizsza od rzeczywistej
    buffer.fillRect(0, 0, BOARD_WIDTH*BLOCK_SIZE, (BOARD_HEIGHT-2)*BLOCK_SIZE);

    // rysujemy biezace tetromino
    for (int j=0; j < 4; j++) {
      for (int i=0; i < 4; i++) {
	if (tetromino.shape[i][j] == 1) {
	  drawBlock(tetromino.position.x+i, tetromino.position.y+j,
			 tetromino.color);
	}
      }
    }
    // rysujemy nastepne tetromino w kolejce
    for (int j=0; j < 4; j++) {
      for (int i=0; i < 4; i++) {
	if (nextTetromino.shape[i][j] == 1) {
	  drawBlock(BOARD_WIDTH+1+i, 4+j, nextTetromino.color);
	}
      }
    }
    // rysujemy plansze
    for (int j=2; j < BOARD_HEIGHT; j++) {
      for (int i=0; i < BOARD_WIDTH; i++) {
	if (board[i][j] != null) {
	  drawBlock(i, j, board[i][j]);
	}
      }
    }
    buffer.setColor(Color.BLACK);
    buffer.drawRect(0, 0, BOARD_WIDTH*BLOCK_SIZE-1,
			    (BOARD_HEIGHT-2)*BLOCK_SIZE-1);

    buffer.setFont(new Font("Serif", Font.PLAIN, 17));
    buffer.drawString("Nast�pne:", BOARD_WIDTH*BLOCK_SIZE+25, 32);
    buffer.drawString("Punkty:", BOARD_WIDTH*BLOCK_SIZE+32, 140);
    buffer.drawString(score + "", BOARD_WIDTH*BLOCK_SIZE+32, 160);
    buffer.drawString("Linie:", BOARD_WIDTH*BLOCK_SIZE+32, 200);
    buffer.drawString(lines + "", BOARD_WIDTH*BLOCK_SIZE+32, 220);
    buffer.drawString("Poziom:", BOARD_WIDTH*BLOCK_SIZE+32, 260);
    buffer.drawString(level + "", BOARD_WIDTH*BLOCK_SIZE+32, 280);

    if (gameOver) {
      buffer.setFont(new Font("Serif", Font.PLAIN, 24));
      buffer.drawString("Koniec gry", width/2-127, height/2-30);
    }
    if (showTitle) {
      int w=BOARD_WIDTH*BLOCK_SIZE/2;
      int h=BOARD_HEIGHT*BLOCK_SIZE/2;
      buffer.setFont(new Font("Serif", Font.PLAIN, 24));
      buffer.drawString("Tetrominoes", w-65, h-160);
      buffer.setFont(new Font("Serif", Font.PLAIN, 17));
      buffer.drawString("kod:", w-16, h-70);
      buffer.drawString("Micha� Dettlaff", w-56, h-45);
      buffer.drawString("muzyka:", w-28, h-10);
      buffer.drawString("Micha� Dettlaff", w-56, h+15);
    }
  }

  void drawBlock(int x, int y, Color color) {
    buffer.setColor(color);
    // wszystko rysujemy o dwa bloki wyzej, bo pierwsze dwie linie sa ukryte
    buffer.fillRect(x*BLOCK_SIZE, (y-2)*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
  }

  public void keyReleased(KeyEvent e) {
  }

  public void keyPressed(KeyEvent e) {
    showTitle=false;
    if (!threadSuspended && !gameOver && tetromino.isMovable()) {
      switch (e.getKeyCode()) {
	case KeyEvent.VK_LEFT:
	  tetromino.moveLeft();
	  if (isConflict(tetromino, board)) {
	    tetromino.moveRight();
	  }
	  break;
	case KeyEvent.VK_RIGHT:
	  tetromino.moveRight();
	  if (isConflict(tetromino, board)) {
	    tetromino.moveLeft();
	  }
	  break;
	case KeyEvent.VK_UP:
	  tetromino.rotateCW();
	  if (isConflict(tetromino, board)) {
	    tetromino.rotateCCW();
	  }
	  break;
	case KeyEvent.VK_DOWN:
	  tetromino.dropSoft();
	  if (isConflict(tetromino, board)) {
	    tetromino.undoDropSoft();
	    tetromino.immobilize();
	  }
	  break;
	case KeyEvent.VK_CONTROL:
	  tetromino.rotateCCW();
	  if (isConflict(tetromino, board)) {
	    tetromino.rotateCW();
	  }
	  break;
      }
      paintBuffer();
    }
    e.consume();
  }

  public void keyTyped(KeyEvent e) {
    switch (e.getKeyChar()) {
      case '\n':
      case ' ':
	gameOver=false;
	break;
      case 'N':
      case 'n':
	if (threadSuspended) {
	  threadSuspended = false;
	  synchronized(this) {
	    notify();
	  }
	}
	gameOver=false;
	newGame();
	break;
      case 'p':
      case 'P':
	if (threadSuspended) {
	  threadSuspended = false;
	  synchronized(this) {
	    notify();
	  }
	}
	else
	  threadSuspended = true;
	break;
      case 'M':
      case 'm':
	if (musicOn) {
	  themeMusic.stop();
	}
	musicOn=!musicOn;
	break;
    }
  }

  public String getAppletInfo() {
    return "Gra w stylu klasycznego Tetrisa.";
  }
}

class Tetromino {
  int size;
  short[][] shape;
  Color color;
  Coords position;
  boolean movable;

  Tetromino() {
    shape = new short[4][4];
    position = new Coords(Tetrominoes.BOARD_WIDTH/2-2, 0);
    movable=true;
  }

  Tetromino(int initSize, short[][] initShape) {
    this();
    size=initSize;
    shape=initShape;
  }

  void rotateCW() {
    short[][] shapeTmp = new short[4][4];
    for (int j=0; j < size; j++) {
      for (int i=0; i < size; i++) {
	shapeTmp[size-1-j][i]=shape[i][j];
      }
    }
    shape=shapeTmp;
  }

  void rotateCCW() {
    short[][] shapeTmp = new short[4][4];
    for (int j=0; j < size; j++) {
      for (int i=0; i < size; i++) {
	shapeTmp[j][size-1-i]=shape[i][j];
      }
    }
    shape=shapeTmp;
  }

  boolean isMovable() {
    return movable;
  }

  void immobilize() {
    movable=false;
  }

  void hide() {
    shape = new short[4][4];
    size=0;
  }

  void setColor(Color newColor) {
    color=newColor;
  }

  void dropSoft() {
    position.y++;
  }

  void undoDropSoft() {
    position.y--;
  }

  void moveLeft() {
    position.x--;
  }

  void moveRight() {
    position.x++;
  }
}

/**
 * Przechowuje rodzaje tetromin. Zostaja zainicjalizowane dopiero po utworzeniu
 * instancji klasy TetrominoShapes.
 */
class TetrominoShapes {
  public final Tetromino I, J, L, O, S, T, Z;

  TetrominoShapes() {
    short[][] shapeI = {
      { 0,0,0,0 },
      { 1,1,1,1 },
      { 0,0,0,0 },
      { 0,0,0,0 },
    };
    I = new Tetromino(4, shapeI);
    short[][] shapeJ = {
      { 1,0,0,0 },
      { 1,1,1,0 },
      { 0,0,0,0 },
      { 0,0,0,0 },
    };
    J = new Tetromino(3, shapeJ);
    short[][] shapeL = {
      { 0,0,1,0 },
      { 1,1,1,0 },
      { 0,0,0,0 },
      { 0,0,0,0 },
    };
    L = new Tetromino(3, shapeL);
    short[][] shapeO = {
      { 1,1,0,0 },
      { 1,1,0,0 },
      { 0,0,0,0 },
      { 0,0,0,0 },
    };
    O = new Tetromino(2, shapeO);
    short[][] shapeS = {
      { 0,1,1,0 },
      { 1,1,0,0 },
      { 0,0,0,0 },
      { 0,0,0,0 },
    };
    S = new Tetromino(3, shapeS);
    short[][] shapeT = {
      { 0,1,0,0 },
      { 1,1,1,0 },
      { 0,0,0,0 },
      { 0,0,0,0 },
    };
    T = new Tetromino(3, shapeT);
    short[][] shapeZ = {
      { 1,1,0,0 },
      { 0,1,1,0 },
      { 0,0,0,0 },
      { 0,0,0,0 },
    };
    Z = new Tetromino(3, shapeZ);
  }
}

/**
 * Przechowuje wspolrzedne x oraz y.
 */
class Coords {
  int x, y;

  Coords(int initX, int initY) {
    x=initX;
    y=initY;
  }

  boolean equals(Coords coords) {
    return (coords.x == x && coords.y == y);
  }

  static Coords add(Coords c1, Coords c2) {
    return new Coords(c1.x+c2.x, c1.y+c2.y);
  }
}

