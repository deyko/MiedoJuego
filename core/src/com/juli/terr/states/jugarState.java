package com.juli.terr.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.juli.terr.MyTestGame;
import com.juli.terr.sprites.moned;
import com.juli.terr.helpers.Size;
import com.juli.terr.sprites.Bichos;
import com.juli.terr.sprites.Drag;
import com.juli.terr.sprites.Tubos;
import com.juli.terr.sprites.bom;
import com.juli.terr.sprites.nube;


public class jugarState extends State
{
    // Final
    private static final int TUBE_SPACING = 125;
    private static final int TUBE_COUNT = 40;
    // Objectos
    private Drag bird;
    private Array<Tubos> tubes;
    private Array<moned> coins;

    private int tubeCounter;

    // Texturas
    private Texture bg;
    private Texture bgfg;
    private Texture ground;

    private Texture retryButton;
    private Vector2 retryButtonPos;
    private Size retryButtonSize;

    // Logica
    private Vector2 groundPos1, groundPos2, bgfgPos1, bgfgPos2;
    private Boolean isPlayerDead;
    private int score;
    private int highscore;

    private Array<Bichos> minions;
    private Array<bom> explosions;
    private Array<nube> clouds;

    private Sound gameOverSound;
    private Sound gameOverSound2;

    private BitmapFont scoreText;
    private BitmapFont highscoreText;
    private GlyphLayout scoreTextLayout;
    private GlyphLayout highscoreTextLayout;
    private SpriteBatch hudBatch;

    protected jugarState(GameStateManager gsm)
    {
        super(gsm);
        cam.setToOrtho(false, MyTestGame.WIDTH / 2, MyTestGame.HEIGHT / 2);
        // Objects
        bird = new Drag(50, 200);
        tubes = new Array<Tubos>();
        coins = new Array<moned>();
        minions = new Array<Bichos>();
        explosions = new Array<bom>();
        // Textures

        int rand = (int) Math.floor(Math.random()*4);
        String bgPath = "bg_dark.png";
        switch (rand)
        {
            case 0:
                bgPath = "bg_dark.png";
                break;
            case 1:
                bgPath = "bg_dark_black.png";
                break;
            case 2:
                bgPath = "bg_dark_green.png";
                break;
            case 3:
                bgPath = "bg_dark_red.png";
                break;
        }

        bg = new Texture(bgPath);

        bgfg = new Texture("fondo.png");
        ground = new Texture("suelo.png");

        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("muere.ogg"));
        gameOverSound2 = Gdx.audio.newSound(Gdx.files.internal("glass2.mp3"));


        retryButton = new Texture("goatras.png");
        retryButtonSize = new Size(50, 50);

        tubeCounter = 0;

        // hace las nubes
        clouds = new Array<nube>();
        float randomY = (float) Math.floor(Math.random() * (cam.viewportHeight - 50) + 50);
        clouds.add(new nube( cam.viewportWidth, randomY));
        randomY = (float) Math.floor(Math.random() * (cam.viewportHeight - 50) + 50);
        clouds.add(new nube( cam.viewportWidth + 70, randomY));
        randomY = (float) Math.floor(Math.random() * (cam.viewportHeight - 50) + 50);
        clouds.add(new nube( cam.viewportWidth + 140, randomY));
        randomY = (float) Math.floor(Math.random() * (cam.viewportHeight - 50) + 50);
        clouds.add(new nube( cam.viewportWidth + 210, randomY));
        randomY = (float) Math.floor(Math.random() * (cam.viewportHeight - 50) + 50);
        clouds.add(new nube( cam.viewportWidth + 280, randomY));

        //Logica
        groundPos1 = new Vector2(cam.position.x - cam.viewportWidth / 2, MyTestGame.GROUND_Y_OFFSET);
        groundPos2 = new Vector2((cam.position.x - cam.viewportWidth / 2) + ground.getWidth(), MyTestGame.GROUND_Y_OFFSET);

        bgfgPos1 = new Vector2(cam.position.x - cam.viewportWidth / 2, 0);
        bgfgPos2 = new Vector2((cam.position.x - cam.viewportWidth / 2) + cam.viewportWidth, 0);

        isPlayerDead = false;
        score = 0;
        highscore = Gdx.app.getPreferences("gamePreferences").getInteger("highScore");

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("FiraSans-Regular.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 24;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.borderWidth = 2;


        scoreText = fontGenerator.generateFont(fontParameter);
        highscoreText = fontGenerator.generateFont(fontParameter);

        scoreTextLayout = new GlyphLayout(scoreText, "Tupuntuación: " + score);

        highscoreTextLayout = new GlyphLayout(highscoreText, "MaxPuntuación: " + highscore);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);





        hudBatch = new SpriteBatch();

        for (int i = 1; i <= TUBE_COUNT; i++)
        {
            tubes.add(new Tubos(i * (TUBE_SPACING + Tubos.TUBE_WIDTH) + 175));

        }
    }

    @Override
    protected void handleInput()
    {
        if (Gdx.input.justTouched())
        {
            if (!isPlayerDead)
            {
                Vector3 tp = new Vector3();
                cam.unproject(tp.set(Gdx.input.getX(), Gdx.input.getY(), 0));

                Boolean interacted = false;
                for (moned coin: coins)
                {
                    Rectangle bounds = new Rectangle(coin.getPosition().x, coin.getPosition().y, coin.getTexture().getWidth(), coin.getTexture().getHeight());
                    if (bounds.contains(tp.x, tp.y))
                    {
                        explosions.add(new bom(coin.getPosition().x, coin.getPosition().y));
                        coin.explode();
                        coins.removeIndex(coins.indexOf(coin, true));
                        score++;
                        interacted = true;
                        coin.playPickupSound();

                    }
                }

                for (Bichos minion: minions)
                {
                    if (minion.getBounds().contains(tp.x, tp.y))
                    {
                        explosions.add(new bom(minion.getPosition().x, minion.getPosition().y));
                        coins.add(new moned(minion.getPosition().x, minion.getPosition().y, minion.getPosition().y > cam.viewportHeight / 2 ));
                        minion.die();

                        minions.removeIndex(minions.indexOf(minion, true));
                        interacted = true;

                    }
                }

                if (!interacted)
                {
                    bird.jump();
                    score++;
                }
            }
            else
            {
                Vector3 tp = new Vector3();
                cam.unproject(tp.set(Gdx.input.getX(), Gdx.input.getY(), 0));
                Rectangle bounds = new Rectangle(retryButtonPos.x, retryButtonPos.y, retryButtonSize.getWidth(), retryButtonSize.getHeight());

                if (bounds.contains(tp.x, tp.y))
                {
                    gsm.set(new jugarState(gsm));
                }
            }

        }
    }

    @Override
    public void update(float dt)
    {
        handleInput();

        if (!isPlayerDead)
        {
            updateBackgroundMovement();
            bird.update(dt, cam);
            cam.position.x = bird.getPosition().x + 80;

            bgfgPos1.add(50 * dt, 0);
            bgfgPos2.add(50 * dt, 0);

            for (int i = 0; i < tubes.size; i++)
            {
                Tubos tube = tubes.get(i);
                double camPos = cam.position.x - (cam.viewportWidth / 2);
                double tubePos = tube.getPosTopTube().x + tube.getTopTube().getWidth();

                if (camPos > tubePos)
                {
                    tubeCounter++;
                    repositionTube(tube);
                    handleSpawnMinion(tubeCounter);
                    handleSpawnCoin(tubeCounter);
                    bird.increaseMovement();
                }

                if (tube.collides(bird.getBounds()))
                {
                    handleDeath();
                }
            }

            for (Bichos minion: minions)
            {
                minion.update(dt, bird);
                if (minion.collides(bird.getBounds()))
                {
                    handleDeath();
                }
            }

            for (moned coin: coins)
            {
                coin.update(bird);
            }

            for (bom explosion: explosions)
            {
                explosion.update(dt);
                if (explosion.isAnimationFinished())
                {
                    explosions.removeIndex(explosions.indexOf(explosion, true));
                }
            }

            for (nube cloud: clouds)
            {
                cloud.update(dt);
                double camPos = cam.position.x - (cam.viewportWidth / 2);
                double cloudPos = cloud.getPosition().x + cloud.getSize().getWidth();

                if (camPos > cloudPos)
                {
                    float randomY = (float) Math.floor(Math.random() * (cam.viewportHeight - 50) + 50);
                    cloud.setPosition(cam.position.x + cam.viewportWidth + 20, randomY);
                }
            }

            if (bird.getPosition().y <= ground.getHeight() + MyTestGame.GROUND_Y_OFFSET)
            {
                handleDeath();
            }
        }

        cam.update();
    }

    private void repositionTube(Tubos tube)
    {
        tube.reposition(tube.getPosTopTube().x + ((Tubos.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
    }

    private void handleSpawnMinion(int tubeCounter)
    {
        int number = (int) Math.floor(Math.random() * (cam.viewportHeight - 50) + 50);

        if (tubeCounter < 8 && tubeCounter % 2 == 0)
        {
            minions.add(new Bichos(cam.position.x + cam.viewportWidth + 30, number));
        }
        else if (4 <= tubeCounter && tubeCounter < 12 && tubeCounter % 2 == 0)
        {
            minions.add(new Bichos(cam.position.x + cam.viewportWidth + 30, number));
            minions.add(new Bichos(cam.position.x + cam.viewportWidth + 30, number + 50));
        }
        else if (8 <= tubeCounter && tubeCounter < 12)
        {
            minions.add(new Bichos(cam.position.x + cam.viewportWidth + 30, number));
        }
        else if (8 <= tubeCounter && tubeCounter < 12 && tubeCounter % 2 == 0)
        {
            minions.add(new Bichos(cam.position.x + cam.viewportWidth + 30, number));
            minions.add(new Bichos(cam.position.x + cam.viewportWidth + 30, number + 50));
        }
        else
        {
            int numOfMinions =  (int) Math.round(Math.floor(tubeCounter / 4));
            for (int i = 0; i < numOfMinions; i++)
            {
                minions.add(new Bichos(cam.position.x + cam.viewportWidth + 30, number + (i * 70)));
            }
        }
    }

    private void handleSpawnCoin(int tubeCounter)
    {

        if (tubeCounter  % 2 == 1)
        {
            boolean isSinking = Math.floor(Math.random() * 2) == 0;
            coins.add(new moned(cam.position.x + cam.viewportWidth + 30, cam.viewportHeight / 2, isSinking));
        }
    }

    @Override
    public void render(SpriteBatch sb)
    {
        renderGameplay(sb);
        if (isPlayerDead)
        {
            renderGameover(sb);
        }
    }

    public void renderGameplay(SpriteBatch sb)
    {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();

        sb.draw(bg, cam.position.x - (cam.viewportWidth / 2), - MyTestGame.GROUND_Y_OFFSET / 2);

        for (nube cloud: clouds)
        {
            sb.draw(cloud.getTexture(), cloud.getPosition().x, cloud.getPosition().y, 80, 40);
        }

        sb.draw(bgfg, bgfgPos1.x, 0, cam.viewportWidth, cam.viewportHeight / 2);
        sb.draw(bgfg, bgfgPos2.x, 0, cam.viewportWidth, cam.viewportHeight / 2);



        for (Tubos tube : tubes) {
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
            sb.draw(tube.getTuboabajo(), tube.getPosBotTube().x, tube.getPosBotTube().y);
        }

        for (bom explosion: explosions)
        {
            sb.draw(explosion.getCurrentFrame(), explosion.getPosition().x, explosion.getPosition().y );
        }

        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);

        for (moned coin: coins)
        {
            sb.draw(coin.getTexture(), coin.getPosition().x, coin.getPosition().y);
        }

        for (Bichos minion: minions)
        {
            sb.draw(minion.getTexture(), minion.getPosition().x, minion.getPosition().y, minion.getWidth(), minion.getHeight());
        }

        sb.draw(bird.getTexture(), bird.getPosition().x, bird.getPosition().y, bird.getTextureWidth(), bird.getTextureHeight());
        sb.end();

        if (!isPlayerDead)
        {
            hudBatch.setProjectionMatrix(cam.projection);
            hudBatch.begin();
            scoreText.draw(hudBatch,  String.valueOf("Puntuación :"+score), -cam.viewportWidth/2 + 10, cam.viewportHeight / 2 - 10);
            hudBatch.end();
        }
    }

    public void renderGameover(SpriteBatch sb)
    {

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.projection);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 0.5f);
        shapeRenderer.rect( - cam.viewportWidth / 2, -cam.viewportHeight / 2, cam.viewportWidth, cam.viewportHeight);
        shapeRenderer.end();


        sb.begin();

        scoreText.draw(sb, scoreTextLayout, cam.position.x - scoreTextLayout.width / 2, cam.viewportHeight / 10 * 8);
        highscoreText.draw(sb, highscoreTextLayout, cam.position.x - highscoreTextLayout.width / 2, cam.viewportHeight / 10 * 7);
        sb.draw(retryButton, retryButtonPos.x, retryButtonPos.y, retryButtonSize.getWidth(), retryButtonSize.getHeight());
        sb.end();
    }

    @Override
    public void dispose()
    {

        ground.dispose();
        bg.dispose();
        bgfg.dispose();
        bird.dispose();
        scoreText.dispose();
        retryButton.dispose();
        for (Tubos tube: tubes)
        {
            tube.dispose();
        }
        for (bom explosion: explosions)
        {
            explosion.dispose();
        }
        for (moned coin: coins)
        {
            coin.dispose();
        }
        for (Bichos minion: minions)
        {
            minion.dispose();
        }
        System.out.println("Entro en el dispose");
    }

    private void handleDeath()
    {
        int flippedCoin = (int) Math.floor(Math.random()*2);
        if (flippedCoin == 0)
        {
            gameOverSound.play(.3f);
        }
        else
        {
            gameOverSound2.play(.3f);
        }

        isPlayerDead = true;
        scoreTextLayout = new GlyphLayout(scoreText, "Score: " + score);
        retryButtonPos = new Vector2(cam.position.x - retryButtonSize.getWidth() / 2, cam.viewportHeight / 10 * 4);
        Preferences preferences = Gdx.app.getPreferences("gamePreferences");
        if (score > highscore)
        {
            preferences.putInteger("Maximo score: ", score);
            preferences.flush();
        }
    }

    private void updateBackgroundMovement()
    {
        if (cam.position.x - (cam.viewportWidth / 2) > groundPos1.x + ground.getWidth())
        {
            groundPos1.add(ground.getWidth() * 2, 0);
        }
        if (cam.position.x - (cam.viewportWidth / 2) > groundPos2.x + ground.getWidth())
        {
            groundPos2.add(ground.getWidth() * 2, 0);
        }
        if (cam.position.x - (cam.viewportWidth / 2) > bgfgPos1.x + cam.viewportWidth)
        {
            bgfgPos1.add(cam.viewportWidth * 2, 0);
        }
        if (cam.position.x - (cam.viewportWidth / 2) > bgfgPos2.x + cam.viewportWidth)
        {
            bgfgPos2.add(cam.viewportWidth * 2, 0);
        }
    }
}
