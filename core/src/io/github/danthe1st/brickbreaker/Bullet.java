package io.github.danthe1st.brickbreaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {

    private final float HEIGHT = 0.1f;
    private final float WIDTH = 0.1f;



    private float x;
    private float y;
    private float xSpeed;
    private float ySpeed;

    public Bullet(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean isActive() {
        return !(isZero(xSpeed) && isZero(ySpeed));
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void shoot() {
        if(isActive()) {
            throw new IllegalStateException("Cannot shoot multiple times");
        }
        ySpeed = 3;
    }

    public void next(BrickBreaker game) {
        float xDistance = xSpeed * game.getBaseSpeed() * Gdx.graphics.getDeltaTime();
        float yDistance = ySpeed * game.getBaseSpeed() * Gdx.graphics.getDeltaTime();
        do {

            Vector2 outsideIntersection = new Vector2();

            if(intersectsRectangleLine(new Vector2(x, y), new Vector2(x + xDistance, y + yDistance), new Rectangle((int) x, (int) y, 1, 1), outsideIntersection)
                    && !(outsideIntersection.x == x && outsideIntersection.y == y)) {
                float newX = outsideIntersection.x;
                float newY = outsideIntersection.y;
                float successXDistance = newX - x;
                float successYDistance = newY - y;
                if(successXDistance == 0 && successYDistance == 0) {
                    return;
                }

                x = newX;
                y = newY;
                xDistance -= successXDistance;
                yDistance -= successYDistance;

                int otherTileX = (int) (x + 0.5 * Math.signum(xSpeed));
                int otherTileY = (int) (y + 0.5 * Math.signum(ySpeed));
                if(isZero(x - Math.round(x))) {

                    if(isZero(x)||game.isOutOfBounds(otherTileX, (int) (game.getFieldWidth()/2))) {
                        xSpeed = Math.abs(xSpeed);
                        if(x>game.getFieldWidth()/2){
                            xSpeed=-xSpeed;
                        }
                    } else if(game.getBrick(otherTileX, (int) y) != null) {
                        game.getBrick(otherTileX, (int) y).hit(game);
                        xSpeed *= -1;
                    }
                }
                if(isZero(y - Math.round(y))) {
                    if(isZero(y)||game.isOutOfBounds((int) (game.getFieldWidth()/2), otherTileY)) {
                        if(y < game.getFieldHeight()/2) {
                            game.bulletLost();
                        } else {
                            ySpeed = -Math.abs(ySpeed);
                        }
                    } else if(game.getBrick((int) x, otherTileY) != null) {
                        game.getBrick((int) x, otherTileY).hit(game);
                        ySpeed *= -1;
                    } else if(game.getPlayerPositionY() == (int) y && x > game.getPlayerPositionX() && x < game.getPlayerPositionX() + game.getPlayerWidth() && ySpeed < 0) {
                        ySpeed = Math.abs(ySpeed);
                        xSpeed = x - game.getPlayerPositionX() - (float) game.getPlayerWidth() / 2;
                        return;
                    }
                }
            } else {
                x += xDistance;
                y += yDistance;
                xDistance = 0;
                yDistance = 0;
                if(x<0){
                    x=0;
                    xSpeed=Math.abs(xSpeed);
                }
                if(x>game.getFieldWidth()){
                    x=game.getFieldWidth();
                    xSpeed=-Math.abs(xSpeed);
                }
                if(y<0){
                    y=0;
                    ySpeed=Math.abs(ySpeed);
                }
                if(y>game.getFieldHeight()){
                    y=game.getFieldHeight();
                    ySpeed=-Math.abs(ySpeed);
                }
            }
        } while(!(isZero(xDistance) && isZero(yDistance)));
    }



    private static boolean intersectsRectangleLine(Vector2 lineStart, Vector2 lineEnd, Rectangle rect, Vector2 intersection) {
        //kinda imitates Intersector.intersectSegmentRectangle
        Vector2 a = new Vector2(rect.x, rect.y);
        Vector2 b = new Vector2(rect.x + rect.width, rect.y);
        Vector2 c = new Vector2(rect.x, rect.y + rect.height);
        Vector2 d = new Vector2(rect.x + rect.width, rect.y + rect.height);

        return Intersector.intersectSegments(lineStart, lineEnd, a, b, intersection) ||
                Intersector.intersectSegments(lineStart, lineEnd, c, d, intersection) ||
                Intersector.intersectSegments(lineStart, lineEnd, a, c, intersection) ||
                Intersector.intersectSegments(lineStart, lineEnd, b, d, intersection);
    }

    private boolean isZero(float toTest) {
        return toTest > -0.001 && toTest < 0.001;
    }

    @Override
    public String toString() {
        return "Bullet{" +
                "HEIGHT=" + HEIGHT +
                ", WIDTH=" + WIDTH +
                ", x=" + x +
                ", y=" + y +
                ", xSpeed=" + xSpeed +
                ", ySpeed=" + ySpeed +
                '}';
    }
}
