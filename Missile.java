import java.awt.*;

public class Missile {
  public double dx;
  public double dy;
  public double xpos;
  public double ypos;
  public int gotime;
  public boolean isAlive;

  public boolean asplode;

  public Rectangle rec;
  public MissileCommand m;

  public Missile(MissileCommand world) {
    m = world;
    dx = 0;
    dy = 0;
    xpos = (Math.random()*m.windowx);
    ypos = -20;
    isAlive = false;
    rec = new Rectangle((int)(xpos), (int)(ypos), 12, 12);
    gotime = (int)(Math.random()*2000+20);
  }

  public void fire() {
    isAlive = true;
    if (xpos<m.windowx/2) {
      dx = (Math.random()+0.2);
    } else {
      dx = -1*(Math.random()+0.2);
    }
    dy = 2;
    dy = (int)(Math.sqrt(Math.pow((m.windowy/180), 2)-Math.pow(dx, 2)));
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
