package com.juli.terr.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;


public class Tubos
{
    public static final int TUBE_WIDTH = 52;
    private static final int FLUCTUATION = 130;
    private static final int TUBE_GAP = 140;
    private static final int LOWEST_OPENING = 120;
    private Texture topTubo, tuboabajo;
    private Vector2 posTopTube, posBotTube;
    private Random rand;
    private Rectangle boundsTop, boundsBottom;

    public Tubos(float x)
    {
        topTubo = new Texture("tubo.png");
        tuboabajo = new Texture("tubo.png");
        rand = new Random();

        posTopTube = new Vector2(x, rand.nextInt(FLUCTUATION) + TUBE_GAP + LOWEST_OPENING);
        posBotTube = new Vector2(x, posTopTube.y - TUBE_GAP - tuboabajo.getHeight());

        boundsTop = new Rectangle(posTopTube.x, posTopTube.y, topTubo.getWidth(), topTubo.getHeight());
        boundsBottom = new Rectangle(posBotTube.x, posBotTube.y, tuboabajo.getWidth(), tuboabajo.getHeight());
    }

    public Texture getTopTube()
    {
        return topTubo;
    }

    public Texture getTuboabajo()
    {
        return tuboabajo;
    }

    public Vector2 getPosTopTube()
    {
        return posTopTube;
    }

    public Vector2 getPosBotTube()
    {
        return posBotTube;
    }

    public void reposition(float x)
    {
        posTopTube.set(x, rand.nextInt(FLUCTUATION) + TUBE_GAP + LOWEST_OPENING);
        posBotTube.set(x, posTopTube.y - TUBE_GAP - tuboabajo.getHeight());
        boundsTop.setPosition(posTopTube.x, posTopTube.y);
        boundsBottom.setPosition(posBotTube.x, posBotTube.y);
    }

    public boolean collides(Rectangle player)
    {
        return player.overlaps(boundsTop) || player.overlaps(boundsBottom);
    }

    public void dispose()
    {
        topTubo.dispose();
        tuboabajo.dispose();
    }
}
