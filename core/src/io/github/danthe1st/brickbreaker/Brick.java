package io.github.danthe1st.brickbreaker;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import java.util.Objects;

public class Brick {

    private static final int TILE_WIDTH=2;
    private static final int TILE_HEIGHT=1;

    private static final Color[] durabiliryColours=new Color[]{Color.GREEN,Color.YELLOW,Color.RED,Color.BLACK};

    private int durability;
    private int x;
    private int y;

    public Brick(int durability, int x, int y) {
        this.durability = durability;
        this.x = x;
        this.y = y;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return TILE_WIDTH;
    }

    public int getHeight() {
        return TILE_HEIGHT;
    }

    public Rectangle getHitBox(){
        return new Rectangle(x,y,getWidth(),getHeight());
    }

    public Color getColour() {
        return durabiliryColours[Math.min(durability-1,durabiliryColours.length-1)];
    }

    public void hit(BrickBreaker game) {
        durability--;
        if(durability<1){
            game.removeBrick(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Brick)) return false;
        Brick brick = (Brick) o;
        return getX() == brick.getX() && getY() == brick.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return "Brick{" +
                "durability=" + durability +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
