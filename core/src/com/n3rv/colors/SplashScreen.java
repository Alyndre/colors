package com.n3rv.colors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;


public class SplashScreen implements Screen {

    /*private Texture probaTexture;
    private Image probaImage;
    ProgressCircle progressCircle;
    float dura = 0;*/
    PolygonSpriteBatch pbatch;


    public MainGame game;
    private OrthographicCamera camera;
    private Stage stage;

    public SplashScreen (MainGame game){

        this.game = game;

        pbatch = new PolygonSpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT);
        ExtendViewport stagevp = new ExtendViewport(MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT, camera);

        stage = new Stage(stagevp);

        this.game.assetManager.load("grayBackground.png", Texture.class);
        this.game.assetManager.load("ok.png", Texture.class);
        this.game.assetManager.load("ko.png", Texture.class);
        this.game.assetManager.load("circle.png", Texture.class);
        this.game.assetManager.load("reload.png", Texture.class);
        this.game.assetManager.load("arrow.png", Texture.class);
        for (int i = 0; i<MainGame.NUM_OF_COLORS; i++){
            this.game.assetManager.load("btn_" + i + ".png", Texture.class);
        }

/*
        probaTexture = new Texture(Gdx.files.internal("btn_1.png"));
        probaImage = new Image(probaTexture);
        probaImage.setWidth(1080);
        probaImage.setHeight(1080);
        probaImage.setPosition(0, MainGame.REAL_WIDTH/2);
        s.addActor(probaImage);

        progressCircle = new ProgressCircle(new TextureRegion(new Texture(Gdx.files.internal("btn_10.png"))), pbatch);
        progressCircle.setPosition(0, MainGame.REAL_WIDTH/2);
        progressCircle.setWidth(1080);
        progressCircle.setHeight(1080);
        s.addActor(progressCircle);*/
    }

    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(253/255f, 253/255f, 253/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(game.assetManager.update()) {
            game.setScreen(new GameScreen(game));

            /*
            dura+= delta;
            if (dura <= 20) {
                float percent = dura * 100 / 20;

                progressCircle.setPercentage(percent);
            } else
            {
                dura = 0; //loop
            }

            progressCircle.draw(game.batch, 1f);
            */

        }

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        stage.draw();

        game.batch.end();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
