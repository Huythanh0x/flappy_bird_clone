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
    Texture play;
    int birdY;
    int birdState = 0;
    int gameState = 0;
    int countGame = 0;
    int bottomY;
    int bottomHeight;
    int score = 0;
    int maxScore = 0;
    int gap = 350;
    float gravity = 2.0f;
    float down = 0;
    Random random = new Random();
    BitmapFont bitmapScore;
    BitmapFont bitmapMaxScore;
    ArrayList<Integer> arrayBottomY = new ArrayList<>();
    ArrayList<Integer> arrayHeightBottom = new ArrayList<>();
    ArrayList<Integer> arrayPipeX = new ArrayList<>();

    ArrayList<Rectangle> bottomRectangles = new ArrayList<Rectangle>();
    Rectangle birdRectangle;
    ArrayList<Rectangle> topRectangles = new ArrayList<Rectangle>();

    Preferences preferences;

    @Override
    public void create() {
        preferences = Gdx.app.getPreferences("saveScore");
        maxScore = preferences.getInteger("score", maxScore);
        batch = new SpriteBatch();
        bird[0] = new Texture("bird1.png");
        bird[1] = new Texture("bird2.png");
        bottomTube = new Texture("bottomtube.png");
        topTube = new Texture("toptube.png");
        birdY = Gdx.graphics.getHeight() / 2 - bird[birdState].getHeight() / 2;
        background = new Texture("bg.png");
        bitmapScore = new BitmapFont();
        bitmapScore.getData().setScale(10);
        bitmapScore.setColor(Color.WHITE);
        bitmapMaxScore = new BitmapFont();
        bitmapMaxScore.getData().setScale(5);
        bitmapMaxScore.setColor(Color.WHITE);
        play = new Texture("play.png");
    }

    public void createPipe() {
        bottomHeight = (random.nextInt(50) + 15) * Gdx.graphics.getHeight() / 100;
        bottomY = bottomHeight - bottomTube.getHeight();
        arrayBottomY.add(bottomY);
        arrayPipeX.add(Gdx.graphics.getWidth());
        arrayHeightBottom.add(bottomHeight);
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(bird[birdState], Gdx.graphics.getWidth() / 3, birdY);
        birdRectangle = new Rectangle(Gdx.graphics.getWidth() / 3, birdY, bird[birdState].getWidth(), bird[birdState].getWidth());
        bitmapMaxScore.draw(batch, "Max Score " + maxScore, 50, 1000);
        bitmapScore.draw(batch, String.valueOf(score), 100, 200);
        
        if (gameState == 0) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
            batch.draw(play, 3 * Gdx.graphics.getWidth() / 5 - 125, Gdx.graphics.getHeight() / 2 - 125, 250, 250);
        }

        if (gameState == -1) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
                countGame = 0;
                birdY = Gdx.graphics.getHeight() / 2;
                down = 0;
                score = 0;
                arrayBottomY.clear();
                arrayHeightBottom.clear();
                arrayPipeX.clear();
                bottomRectangles.clear();
                topRectangles.clear();
            }

            if (maxScore < score) {
                maxScore = score;
            }

            batch.draw(play, 3 * Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() / 2 - 125, 250, 250);
            preferences.putInteger("score", maxScore);
            preferences.flush();
        }
        if (gameState == 1) {
            if (birdY <= 0) {
                gameState = -1;
            }

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
            int countPipe = 0;
            bottomRectangles.clear();
            topRectangles.clear();
            for (int i = 0; i < arrayPipeX.size(); i++) {
                arrayPipeX.set(i, arrayPipeX.get(i) - 10);
                batch.draw(bottomTube, arrayPipeX.get(i), arrayBottomY.get(i), bottomTube.getWidth(), bottomTube.getHeight());
                bottomRectangles.add(new Rectangle(arrayPipeX.get(i), arrayBottomY.get(i), bottomTube.getWidth(), bottomTube.getHeight()));
                batch.draw(topTube, arrayPipeX.get(i), arrayHeightBottom.get(i) + gap, topTube.getWidth(), topTube.getHeight());
                topRectangles.add(new Rectangle(arrayPipeX.get(i), arrayHeightBottom.get(i) + gap, topTube.getWidth(), topTube.getHeight()));

                if (topTube.getWidth() + arrayPipeX.get(i) < Gdx.graphics.getWidth() / 3) {
                    countPipe++;
                }
                if (Intersector.overlaps(birdRectangle, topRectangles.get(i))) {
                    gameState = -1;
                }
                if (Intersector.overlaps(birdRectangle, bottomRectangles.get(i))) {
                    gameState = -1;
                }
            }
            score = countPipe;
        }
        batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
