package com.juli.terr.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.juli.terr.helpers.Size;


public class nube
{
    private Texture texture;
    private Vector2 position;
    private Size size;

    public nube(float posX, float posY)
    {
        position = new Vector2(posX, posY);
        int random = (int) Math.floor(Math.random()*3);
        String texturePath;
        switch (random)
        {
            case 1:
                texturePath = "nube1.png";
                break;
            case 2:
                texturePath = "nube2.png";
                break;
            case 3:
                texturePath = "nube3.png";
                break;
            default:
                texturePath = "nube1.png";
        }

        texture = new Texture(texturePath);

        size = new Size(80, 40);
    }

    public Texture getTexture() { return texture; }

    public Vector2 getPosition() { return position; }

    public void setPosition(float x, float y)
    {
        position.x = x;
        position.y = y;
    }

    public Size getSize() { return size; }

    public void update(float dt)
    {
        position.add(65 * dt, 0);


    }

    public void dispose()
    {
        texture.dispose();
    }
}
