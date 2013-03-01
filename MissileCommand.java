import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class MissileCommand extends Applet implements Runnable, KeyListener, MouseListener, MouseMotionListener {

  public Dimension dim;
  public int windowx;
  public int windowy;
  public int mousex;
  public int mousey;
  public boolean drawcursor;

  public Bullet[] ammunition = new Bullet[5];
  public Missile[] enemies = new Missile[20];
  public int firenum;
  public int missilesleft;
  public int level;
  public int score;
  public int health;

  public Font emulogic;
  public Font title;
  public Font subtitle;
  public Font meta;

  Graphics bufferGraphics;
  int cyclenum;
  Image offscreen;
  Thread thread;

  public void init() {
    setSize(800, 600);
    dim = this.getSize();
    windowx = dim.width;
    windowy = dim.height;

    for (int i=0; i<ammunition.length; i++) {
      ammunition[i]=new Bullet(this);
    }

    for (int i=0; i<enemies.length; i++) {
      enemies[i]=new Missile(this);
    }

    firenum = 0;
    missilesleft = enemies.length;
    level = 0;
    score = 0;
    health = 100;

    offscreen = createImage(2000,2000);
    bufferGraphics = offscreen.getGraphics();

    emulogic = new Font("Arial", Font.PLAIN, 16);
    title = new Font("Arial", Font.PLAIN, 40);
    subtitle = new Font("Arial", Font.PLAIN, 28);
    meta = new Font("Arial", Font.PLAIN, 10);

    // emulogic = new Font("Emulogic", Font.PLAIN, 16);
    // title = new Font("Emulogic", Font.PLAIN, 40);
    // subtitle = new Font("Emulogic", Font.PLAIN, 28);
    // meta = new Font("Emulogic", Font.PLAIN, 10);

    addKeyListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);
    cyclenum = 0;
    thread = new Thread(this);
    thread.start();

  }

  public void paint(Graphics g) {

    // Clear the offscreen image
    bufferGraphics.clearRect(0,0,2000,2000);
    bufferGraphics.setColor(Color.BLACK);
    bufferGraphics.fillRect(0, 0, 2000, 2000);
    bufferGraphics.setColor(Color.GREEN);

    if (level == 0) {
      bufferGraphics.setFont(title);
      bufferGraphics.drawString("Missile Command", windowx/2-305, windowy/3);
      bufferGraphics.setFont(emulogic);
      bufferGraphics.drawString("By James Little", windowx/2-120, windowy/3*2);
      bufferGraphics.drawString("Press Any Key to Play", windowx/2-168, windowy/3*2+40);

      bufferGraphics.drawString("This text will be centered with the font Emulogic.", windowx/2-168, windowy/4*3+40);
    }

    if (level > 0) {
     // Draw the Base
      bufferGraphics.fillArc(windowx/2-30, windowy-40, 60, 60, 0, 180);
      bufferGraphics.fillRect(0, windowy-10, windowx, 2);

      // Draw bullets
      for (int i=0; i<ammunition.length; i++) {
        bufferGraphics.fillOval((int)(ammunition[i].xpos), (int)(ammunition[i].ypos), 12, 12);
      }

      // Draw enemies
      for (int i=0; i<enemies.length; i++) {
        if (enemies[i].isAlive) {
          bufferGraphics.fillRect((int)(enemies[i].xpos), (int)(enemies[i].ypos), 12, 12);
        }
        if (enemies[i].asplode == true) {
          bufferGraphics.drawOval((int)(enemies[i].xpos-9), (int)(enemies[i].ypos-9), 30, 30);
        }
      }

      // Draw GUI
      bufferGraphics.setColor(Color.BLACK);
      bufferGraphics.fillRect(39, 18, 140, 21);
      bufferGraphics.fillRect(windowx-201, 18, 175, 21);
      bufferGraphics.setColor(Color.GREEN);
      bufferGraphics.setFont(emulogic);
      bufferGraphics.drawString("Level "+level, 40, 40);
      bufferGraphics.drawString("Score: "+score, windowx-200, 40);
      bufferGraphics.drawRect(windowx-200, 50, 101, 12);
      bufferGraphics.setColor(Color.RED);
      bufferGraphics.fillRect(windowx-199, 51, health, 11);
      bufferGraphics.setColor(Color.GREEN);
      bufferGraphics.setFont(meta);
      bufferGraphics.drawString(missilesleft+" Left In Wave", 40, 60);
      bufferGraphics.drawString("Health: "+health, windowx-200, 75);

      // Draw crosshairs
      if (drawcursor == true) {
        bufferGraphics.fillRect(mousex-1, mousey-14, 2, 28);
        bufferGraphics.fillRect(mousex-14, mousey-1, 28, 2);
      }
    }

    // Draw bufferGraphics image to applet
    g.drawImage(offscreen,0,0,this);

  }

  public void run() {

    while(true) {
      cyclenum++;

      dim = this.getSize();
      int windowxold = windowx;
      int windowyold = windowy;
      windowx = dim.width;
      windowy = dim.height;

      drawcursor = true;

      if (level == 0) {
        drawcursor = false;
        cyclenum = 0;
      }

      for (int i=0; i<enemies.length; i++) {
        if (cyclenum == enemies[i].gotime) {
          enemies[i].fire();
          missilesleft--;
        }

        if (enemies[i].ypos>windowy-20 && enemies[i].isAlive == true) {
          enemies[i].isAlive = false;
          health = health-(int)(10-Math.abs(enemies[i].xpos-windowx/2)/100);
          System.out.println((int)(10-Math.abs(enemies[i].xpos-windowx/2)/100));
          System.out.println((10-Math.abs(enemies[i].xpos-windowx/2)/100));
        }
      }

      if ((windowx != windowxold) || (windowy != windowyold)) {
        resetammo();
        drawcursor = false;
      }

      checkIntersection();

      moveall();
      repaint();

      try {thread.sleep(40);}
      catch (Exception e){ }
    }

  }

  public void update (Graphics g) {

    paint(g);

  }

  public void moveall() {

    for (int i=0; i<ammunition.length; i++) {
      if (ammunition[i].isAlive == true) {
        ammunition[i].move();
      }
    }

    for (int i=0; i<enemies.length; i++) {
      enemies[i].move();
    }

  }

  public void checkIntersection() {

    for (int i=0; i<ammunition.length; i++) {
      for (int j=0; j<enemies.length; j++) {
        if (ammunition[i].rec.intersects(enemies[j].rec) && ammunition[i].isAlive && enemies[j].isAlive) {
          ammunition[i].reset();
          enemies[j].asplode = true;
          score = score+windowx-(int)(enemies[j].ypos);
          enemies[j].isAlive = false;
        }
      }
    }

  }

  public void resetammo() {
    for (int i=0; i<ammunition.length; i++) {
      ammunition[i].reset();
    }
  }

  public void shoot(int x, int y) {
    double slope = (double)((windowy-20)-y)/(double)(x-windowx/2);
    double angle = Math.atan((double)(x-windowx/2)/(double)((windowy-10)-y));
    double dx = (windowy/120)*Math.sin(angle);
    double dy = (windowy/-120)*Math.cos(angle);

    if (level > 0) {
      ammunition[firenum].isAlive = true;
      ammunition[firenum].fire(dx, dy);
    }

    firenum++;
    if (firenum > ammunition.length-1) {
      firenum = 0;
    }
  }

  public boolean circleintersection(int r1, int x1, int y1, int r2, int x2, int y2){
    double distance = Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2));
    int radii = r1+r2;

    if ((double)radii >= distance) {
      return(true);
    }

    return(false);
  }

  public void keyPressed(KeyEvent event) {
    String keyin;
    keyin = ""+event.getKeyText( event.getKeyCode());

    if (level == 0 && keyin != "") {
      level = 1;
    }
  }

  public void keyReleased(KeyEvent event) {
    String keyin;
    keyin = ""+event.getKeyText( event.getKeyCode());
  }

  public void keyTyped( KeyEvent event ) {}

  public void mouseEntered( MouseEvent e ) { }
  public void mouseExited( MouseEvent e ) { }

  public void mousePressed( MouseEvent e ) {
    int x = e.getX();
    int y = e.getY();

    if (y<windowy-10) {
      shoot(x, y);
    }
  }

  public void mouseReleased( MouseEvent e ) { }
  public void mouseDragged( MouseEvent e ) { }
  public void mouseMoved( MouseEvent e ) {
    mousex = e.getX();
    mousey = e.getY();
  }

  public void mouseClicked( MouseEvent e ) {
    // int x = e.getX();
    // int y = e.getY();

    // if (y<windowy-10) {
    //   shoot(x, y);
    // }
  }
}
