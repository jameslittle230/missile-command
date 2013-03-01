import java.awt.*;

public class Bullet {
  public double dx;
  public double dy;
  public double xpos;
  public double ypos;
  public boolean isAlive;

  public Rectangle rec;
  public MissileCommand m;

  public Bullet(MissileCommand world) {
    dx = 0;
    dy = 0;
    xpos = -10;
    ypos = -10;
    rec = new Rectangle((int)(xpos), (int)(ypos), 12, 12);
    m = world;
  }

  public void fire(double dxin, double dyin) {
    isAlive = true;
    xpos = m.windowx/2;
    ypos = m.windowy-20;
    dx = dxin;
    dy = dyin;
  }

  public void move() {
    xpos = xpos+dx;
    ypos = ypos+dy;
    rec = new Rectangle((int)(xpos), (int)(ypos), 12, 12);
  }

  public void reset() {
    isAlive = false;
    dx = 0;
    dy = 0;
    xpos = -10;
    ypos = -10;
  }
}
