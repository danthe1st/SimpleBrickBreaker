package io.github.danthe1st.brickbreaker;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BrickBreaker extends ApplicationAdapter {

    private static final int HEIGHT = 15;
    private static final int WIDTH = 20;

    private static final int PLAYER_POSITION_Y = 2;

    private static final int PLAYER_WIDTH = 2;
    private static final float PLAYER_HEIGHT = 0.2f;


    private SpriteBatch batch;
    private Texture brickImg;
    private Texture gameOverImage;
    private Texture winImage;
    private OrthographicCamera cam;
    private ShapeRenderer shapeRenderer;

    private float playerPos;
    private Bullet bullet;

    private Set <Brick> bricks = new HashSet <>();
    private Brick[][] elementsInGameField;

    private boolean playing = true;

    private long startTime;
    private int lives;

    @Override
    public void create() {
        batch = new SpriteBatch();
        brickImg = new Texture("brick.png");
        gameOverImage = new Texture("gameOver.png");
        winImage = new Texture("finished.png");
        cam = new OrthographicCamera();
        cam.setToOrtho(false, WIDTH, HEIGHT);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        reset();
    }

    private void reset() {
        playerPos = WIDTH / 2 - 1 + 0.01f;
        resetBullet();
        bricks.clear();
        elementsInGameField = new Brick[WIDTH][HEIGHT];
        for(int i = 0; i < WIDTH; i += 2) {
            for(int j = HEIGHT - 4; j < HEIGHT - 1; j++) {
                addBrick(new Brick(3, i, j));
            }
        }
        lives = 3;
        startTime=0;
    }

    private void resetBullet() {
        bullet = new Bullet(playerPos + PLAYER_WIDTH / 2, PLAYER_POSITION_Y + PLAYER_HEIGHT);
    }

    private void addBrick(Brick brick) {
        setInBrickPosition(brick, null, brick);
        bricks.add(brick);
    }

    public void removeBrick(Brick brick) {
        bricks.remove(brick);
        setInBrickPosition(brick, brick, null);
    }

    private void setInBrickPosition(Brick positionBrick, Brick expected, Brick toSet) {
        for(int x = 0; x < positionBrick.getWidth(); x++) {
            for(int y = 0; y < positionBrick.getHeight(); y++) {
                int actualX = positionBrick.getX() + x;
                int actualY = positionBrick.getY() + y;
                if(!Objects.equals(expected, elementsInGameField[actualX][actualY])) {
                    throw new IllegalStateException("Brick does not match expected brick at " + actualX + "|" + actualY + "\n"
                            + "expected: " + expected + "\n"
                            + "actual: " + elementsInGameField[actualX][actualY]);
                }
                elementsInGameField[actualX][actualY] = toSet;
            }
        }
    }

    public Brick getBrick(int x, int y) {
        if(isOutOfBounds(x,y)){
            return null;
        }
        return elementsInGameField[x][y];
    }

    public boolean isOutOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= elementsInGameField.length || y >= elementsInGameField[x].length;
    }

    public int getPlayerPositionY() {
        return PLAYER_POSITION_Y;
    }

    public float getPlayerPositionX() {
        return playerPos;
    }

    public int getPlayerWidth() {
        return PLAYER_WIDTH;
    }

    public float getBaseSpeed() {
        return 2 * (1 + ((System.currentTimeMillis() - startTime)) / (float) 120_000);//2min-->double speed, 4min-->3x speec, etc
    }

    @Override
    public void render() {
        checkImportantKeys();
        if(lives<1){
            justDrawScreen(gameOverImage);
            return;
        }else if(bricks.isEmpty()){
            justDrawScreen(winImage);
            return;
        }

        ScreenUtils.clear(1, 1, 1, 1);
        if(playing) {
            loop();
        }//TODO else pause info
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer.setProjectionMatrix(cam.combined);

        drawAll();
    }

    private void justDrawScreen(Texture texture){
        ScreenUtils.clear(1, 1, 1, 1);
        batch.begin();
        batch.draw(texture,0,0,WIDTH,HEIGHT);
        batch.end();
    }

    private void checkImportantKeys() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            playing = !playing;
            if(playing){
                startTime=System.currentTimeMillis()-startTime;
            }else{
                startTime=System.currentTimeMillis()-startTime;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.R)) {
            reset();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void loop() {

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerPos = Math.max(playerPos - 10 * Gdx.graphics.getDeltaTime(), 0);
            moveBulletIfNotYetShot();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerPos = Math.min(playerPos + 10 * Gdx.graphics.getDeltaTime(), WIDTH - PLAYER_WIDTH);
            moveBulletIfNotYetShot();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if(!bullet.isActive()) {
                bullet.shoot();
                startTime += System.currentTimeMillis();
            }
        }


        bullet.next(this);
    }

    private void moveBulletIfNotYetShot() {
        if(!bullet.isActive()) {
            bullet.setX(playerPos + PLAYER_WIDTH / 2);
        }
    }

    private void drawAll() {
        batch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for(Brick brick : bricks) {
            drawBrick(brick);
        }

        drawBullet();
        drawPlayer();

        shapeRenderer.end();
        batch.end();
    }

    private void drawBullet() {
        shapeRenderer.setColor(Color.GRAY);

        shapeRenderer.circle(bullet.getX(), bullet.getY(), 0.2f,20);
    }

    private void drawBrick(Brick brick) {
        shapeRenderer.setColor(brick.getColour());
        shapeRenderer.rect(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
        batch.draw(brickImg, brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
    }

    private void drawPlayer() {
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(playerPos, PLAYER_POSITION_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    @Override
    public void dispose() {
        batch.dispose();
        brickImg.dispose();
        gameOverImage.dispose();
        winImage.dispose();
        shapeRenderer.dispose();
    }


    public void bulletLost() {
        lives--;
        resetBullet();
        startTime-=System.currentTimeMillis();
    }

    public float getFieldWidth() {
        return WIDTH;
    }

    public float getFieldHeight(){
        return HEIGHT;
    }
}
