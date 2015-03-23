package com.rawr.colors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

public class SplashScreen implements Screen {


    public MainGame game;
    private OrthographicCamera camera;

    public SplashScreen (MainGame game){

        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT);

        this.game.assetManager.load("grayBackground.png", Texture.class);
        this.game.assetManager.load("white_timebar.png", Texture.class);
        this.game.assetManager.load("ok.png", Texture.class);
        this.game.assetManager.load("ko.png", Texture.class);
        this.game.assetManager.load("circle.png", Texture.class);
        this.game.assetManager.load("reload.png", Texture.class);
        this.game.assetManager.load("arrow.png", Texture.class);
        for (int i = 0; i<MainGame.NUM_OF_COLORS; i++){
            this.game.assetManager.load("btn_" + i + ".png", Texture.class);
        }

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(253/255f, 253/255f, 253/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.pbatch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        if(game.assetManager.update()) {
            game.setScreen(new GameScreen(game));
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
