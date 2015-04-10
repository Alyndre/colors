package com.rawr.colors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SplashScreen implements Screen {


    public MainGame game;
    private OrthographicCamera camera;

    private Animation animation;
    private Float elapsed;

    public SplashScreen (MainGame game){

        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT);

        elapsed = 0f;

        loadGif();

        this.game.assetManager.load("sfx/correct.ogg", Sound.class);
        this.game.assetManager.load("sfx/wrong.ogg", Sound.class);
        this.game.assetManager.load("sfx/play.ogg", Sound.class);

        this.game.assetManager.load("img/grayBackground.png", Texture.class);
        this.game.assetManager.load("img/white_timebar.png", Texture.class);
        this.game.assetManager.load("img/ok.png", Texture.class);
        this.game.assetManager.load("img/ko.png", Texture.class);
        this.game.assetManager.load("img/circle.png", Texture.class);
        this.game.assetManager.load("img/reload.png", Texture.class);
        this.game.assetManager.load("img/arrow.png", Texture.class);
        for (int i = 0; i<MainGame.NUM_OF_COLORS; i++){
            this.game.assetManager.load("img/btn_" + i + ".png", Texture.class);
        }

    }

    private void loadGif(){
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("img/loadSprite.pack"));
        animation = new Animation(1/30f, atlas.getRegions());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.pbatch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        if(game.assetManager.update()) {
            game.setScreen(new GameScreen(game));
        } else {
            elapsed += delta;
            game.batch.draw(animation.getKeyFrame(elapsed, true), MainGame.VIRTUAL_WIDTH-256, MainGame.VIRTUAL_HEIGHT-256);
        }

        game.batch.end();

    }

    @Override
    public void show() { }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() { }
}
