package com.tori.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;


class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture bird[] = new Texture[2];
    Texture bottomTube;
    Texture topTube;
    int birdY;
    int birdState = 0;
    int countGame = 0;
    float gravity = 2.0f;
    float down = 0;
    int gap = 350;
    Random random = new Random();
    int bottomY;
    int heightBottom;
    int score = 0;
    int maxScore = 999;
    BitmapFont bitmapScore;
    BitmapFont bitmapMaxScore;
    ArrayList<Integer> arrayBottomY = new ArrayList<>();
    ArrayList<Integer> arrayHeightBottom = new ArrayList<>();
    ArrayList<Integer> arrayX = new ArrayList<>();
    ArrayList<Rectangle> bottomRectangles = new ArrayList<Rectangle>();
    Rectangle birdRectangle;
    ArrayList<Rectangle> topRectangles = new ArrayList<Rectangle>();
    int gameState = 0;
    Preferences preferences;
    @Override
    public void create() {
        preferences = Gdx.app.getPreferences("saveScore");
        maxScore = preferences.getInteger("score", maxScore);
        batch = new SpriteBatch();
        bird[0] = new Texture("bird.png");
        bird[1] = new Texture("bird2.png");
        bottomTube = new Texture("bottomtube.png");
        topTube = new Texture("toptube.png");
        birdY = Gdx.graphics.getHeight() / 2;
        background = new Texture("bg.png");
        bitmapScore = new BitmapFont();
        bitmapScore.getData().setScale(10);
        bitmapScore.setColor(Color.WHITE);
        bitmapMaxScore = new BitmapFont();
        bitmapMaxScore.getData().setScale(5);
        bitmapMaxScore.setColor(Color.WHITE);
    }

    public void createPipe() {
        heightBottom = (random.nextInt(50) + 15) * Gdx.graphics.getHeight() / 100;
        bottomY = heightBottom - bottomTube.getHeight();
        arrayBottomY.add(bottomY);
        arrayX.add(Gdx.graphics.getWidth());
        arrayHeightBottom.add(heightBottom);
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(bird[birdState], Gdx.graphics.getWidth() / 3, birdY);
        bitmapMaxScore.draw(batch, "Max Score " + String.valueOf(maxScore), 50, 1000);
        bitmapScore.draw(batch, String.valueOf(score), 100, 200);
        if(birdY <= 0 || birdY >= Gdx.graphics.getHeight() - bird[birdState].getHeight()){
            gameState = -1;
        }
        if (gameState == 0) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 1) {
            if (Gdx.input.isTouched()) {
                down = 0;
                birdY += 20;
            }
            if (countGame % 100 == 0) {
                createPipe();
            }
            down += gravity;
            countGame++;
            if (countGame % 3 == 0) {
                birdY -= down;
                if (birdState == 0) {
                    birdState = 1;
                } else birdState = 0;
            }


            birdRectangle = new Rectangle(Gdx.graphics.getWidth() / 3, birdY, bird[birdState].getWidth(), bird[birdState].getWidth());
            int count = 0;
            bottomRectangles.clear();
            topRectangles.clear();
            for (int i = 0; i < arrayBottomY.size(); i++) {
                batch.draw(bottomTube, arrayX.get(i), arrayBottomY.get(i), bottomTube.getWidth(), bottomTube.getHeight());
                bottomRectangles.add(new Rectangle(arrayX.get(i), arrayBottomY.get(i), bottomTube.getWidth(), bottomTube.getHeight()));
                batch.draw(topTube, arrayX.get(i), arrayHeightBottom.get(i) + gap, topTube.getWidth(), topTube.getHeight());
                topRectangles.add(new Rectangle(arrayX.get(i), arrayHeightBottom.get(i) + gap, topTube.getWidth(), topTube.getHeight()));
                arrayX.set(i, arrayX.get(i) - 10);

                if (topTube.getWidth() + arrayX.get(i) < bird[birdState].getWidth() + Gdx.graphics.getWidth() / 3) {
                    count++;
                }

            }
            for (int i = 0; i < topRectangles.size(); i++) {
                if (Intersector.overlaps(birdRectangle, topRectangles.get(i))) {
                    gameState = -1;
                }
                if (Intersector.overlaps(birdRectangle, bottomRectangles.get(i))) {
                    gameState = -1;
                }
            }
            score = count;
            count = 0;


        } else if (gameState == -1) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
                countGame = 0;
                birdY = Gdx.graphics.getHeight() / 2;
                down = 0;
                score = 0;
                arrayBottomY.clear();
                arrayHeightBottom.clear();
                arrayX.clear();
                bottomRectangles.clear();
                topRectangles.clear();
            }

            if(maxScore < score){
                maxScore = score;
            }
            preferences.putInteger("score", maxScore);
        }
        batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
