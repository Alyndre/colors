package com.rawr.colors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;

public class GameScreen implements Screen {

    public MainGame game;

    private boolean isWidthExtended = false;
    private boolean isPressed = false;
    private float ratio = 0.0f;
    private float diff = 0.0f;
    private float dura = 0;

    private OrthographicCamera camera;
    private Stage stage;

    private ImageButton logo;

    private ProgressCircle timeBar;

    private Texture bigButtonTexture;
    private ImageButton.ImageButtonStyle bigButtonStyle;
    private ImageButton bigButton;

    private ImageButton reloadButton;

    private Image arrow;

    private Texture okTexture;
    private Image ok;

    private Texture koTexture;
    private Image ko;

    private Texture greybkgTexture;
    private Image greybkg;

    private Label playText;
    private Label pointsText;
    private Label pointsNumber;
    private Label highscoreText;
    private Label highscoreNumber;
    private Label readyGo;

    private int actual = 0;

    public GameScreen(MainGame gam) {

        game = gam;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT);
        ExtendViewport stagevp = new ExtendViewport(MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT, camera);

        stage = new Stage(stagevp);
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        game.state = MainGame.GameState.MENU;

        isWidthExtended = ((MainGame.VIRTUAL_WIDTH/MainGame.REAL_WIDTH)<(MainGame.VIRTUAL_HEIGHT/MainGame.REAL_HEIGHT));
        if(isWidthExtended){
            ratio = MainGame.VIRTUAL_HEIGHT/MainGame.REAL_HEIGHT;
            diff = Math.abs(MainGame.REAL_WIDTH-(MainGame.VIRTUAL_WIDTH/ratio));
        } else {
            ratio = MainGame.VIRTUAL_WIDTH/MainGame.REAL_WIDTH;
            diff = Math.abs(MainGame.REAL_HEIGHT-(MainGame.VIRTUAL_HEIGHT/ratio));
        }

        loadPlayText();
        loadLogo();
        loadPointsText();
        loadPointsNumber();
        loadHighscoreText();
        loadHighscoreNumber();
        loadArrow();
        loadSemiBackground();
        loadReadyGo();
        loadReloadButton();
        setActual();
        loadBigButton();
        loadTimeBar();
        loadOk();
        loadKo();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(253/255f, 253/255f, 253/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.pbatch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        if(game.state == MainGame.GameState.MENU) {
            //TODO: Add a fade-in for all the elements on the menu
            logo.rotateBy(65 * delta);
            if (logo.getRotation() > 360) {
                logo.setRotation(logo.getRotation() - 360);
            }
        }

        if (game.state == MainGame.GameState.TRANSITION) {
            playText.act(delta);
            logo.rotateBy((360 - logo.getRotation()) * delta);
            if (logo.getRotation() > 360) {
                logo.setRotation(logo.getRotation() - 360);
            }
            logo.clearListeners();
            addPointsText();
            addTimeBar();
            addPointsNumber();
            changeScorePosition(game.state);
            addArrow();
            addBigButton();
            pointsText.act(delta);
            addGreyBackground();
            addReadyGo();
            if (logo.getRotation() >= 359.5) {
                game.state = MainGame.GameState.COUNT;
            }
        }

        if (game.state == MainGame.GameState.COUNT) {

            if (playText.getActions().size == 0) {
                playText.remove();
            }
            logo.setRotation(0);
            removeGreyBackground();
            pointsNumber.setText(game.getPoints()+"");
            readyGo.setStyle(game.getStyleText());
            readyGo.setText("GO!");
            game.state = MainGame.GameState.GAME;
        }

        if (game.state == MainGame.GameState.GAME) {
            removeReadyGo(delta);
            if ((readyGo.getActions().size == 0) && (readyGo.getStage() != null)) {
                readyGo.remove();
            }
            pointsNumber.setText(game.getPoints()+"");
            logo.rotateBy(game.getSpeed() * delta);
            if (logo.getRotation() > 360) {
                logo.setRotation(logo.getRotation() - 360);
            }
            for (Actor a : stage.getActors()){
                if(a.getName() != null) {
                    if (a.getName().equals("ok")) {
                        a.act(delta);
                    }
                }
            }
            if ((ok.getActions().size == 0)&&(isPressed)) {
                ok.remove();
                ok.setColor(1f, 1f, 1f, 1f);
                isPressed = false;
            }

            dura+= delta;
            float percent = dura * 100 / game.getTime();
            if (dura <= game.getTime()) {

                if (percent > 66) timeBar.setColor(Color.RED);
                else if (percent > 33) timeBar.setColor(Color.ORANGE);
                else timeBar.setColor(Color.GREEN);

                timeBar.setPercentage(percent);
            } else
            {
                wrong();
            }
        }

        stage.draw();
        if (game.state == MainGame.GameState.OVER) {

            addHighscoreText();
            addHighscoreNumber();

            changeScorePosition(game.state);

            for (Actor a : stage.getActors()){
                if (a.getName() != null){
                    if(a.getName().equals("ko")){
                        a.act(delta);
                    }
                }
            }

            if ((ko.getActions().size == 0) && (ko.getStage()!=null)) {
                ko.remove();
                ko.setColor(1f, 1f, 1f, 1f);
                removeBigButton();
                addReloadButton();
                game.setHighScore(game.getPoints());
                highscoreNumber.setText(MainGame.getHighScore()+"");
            }

            logo.rotateBy(65 * delta);
            reloadButton.rotateBy(-30*delta);
        }

        game.batch.end();
    }

    private void changeScorePosition(MainGame.GameState state){
        float w = MainGame.VIRTUAL_WIDTH;
        float h = MainGame.VIRTUAL_HEIGHT;

        if (isWidthExtended){
            w += diff;
        } else {
            h += diff;
        }

        if (state == MainGame.GameState.OVER) {
            pointsNumber.setPosition(-w / 4, h - pointsNumber.getHeight());
            pointsText.setPosition(-w / 4, h - pointsText.getHeight());
        } else {
            pointsNumber.setPosition(0, h - pointsNumber.getHeight());
            pointsText.setPosition(0, h - pointsText.getHeight());
        }
    }

    private void correct(){
        ok.addAction(Actions.fadeOut(0.5f));
        addOk();
        game.addPoints(1);
        dura = 0;
        timeBar.setPercentage(0);
        isPressed = true;
        setActual();
        newButtonColor();
    }

    private void wrong(){
        game.state = MainGame.GameState.OVER;
        dura = 0;
        timeBar.setPercentage(0);
        removeArrow();
        removeTimeBar();
        addKo();
    }

    private void newButtonColor(){
        bigButtonTexture = game.assetManager.get("btn_" + actual + ".png", Texture.class);

        bigButtonStyle = new ImageButton.ImageButtonStyle();
        bigButtonStyle.up = new SpriteDrawable(new Sprite(bigButtonTexture));
        bigButtonStyle.down = new SpriteDrawable(new Sprite(bigButtonTexture));
        bigButton.setStyle(bigButtonStyle);
    }

    private void addTimeBar(){
        stage.addActor(timeBar);
    }

    private void removeTimeBar(){
        timeBar.remove();
    }

    private void loadTimeBar(){
        float x, y, w, h;

        w = 560;
        h = 560;

        timeBar = new ProgressCircle(new TextureRegion(game.assetManager.get("white_timebar.png", Texture.class)), game.pbatch, w, h);
        timeBar.setOrigin(timeBar.getWidth()/2, timeBar.getHeight()/2);

        x = (MainGame.VIRTUAL_WIDTH-timeBar.getWidth())/2;
        y = (MainGame.VIRTUAL_HEIGHT-timeBar.getHeight())/2;
        if (isWidthExtended) {
            x = x + (diff/ratio);
        } else {
            y = y + (diff/ratio);
        }
        timeBar.setPosition(x, y);
        timeBar.setPercentage(0);
        timeBar.setColor(Color.GREEN);

    }

    private void loadReadyGo(){
        float w = MainGame.VIRTUAL_WIDTH;
        float h = MainGame.VIRTUAL_HEIGHT;

        if (isWidthExtended){
            w += diff;
        } else {
            h += diff;
        }

        readyGo = new Label ("Ready?", game.getStyleWhiteText());
        readyGo.setWidth(w);
        readyGo.setHeight(h);
        readyGo.setFontScale(1f);
        readyGo.setAlignment(Align.center);
        readyGo.setPosition(0, 0);
        readyGo.setColor(1f, 1f, 1f, 1f);
        readyGo.addAction(Actions.fadeOut(0.75f));

    }

    private void addReadyGo() {
        readyGo.setStyle(game.getStyleWhiteText());
        readyGo.setColor(1f, 1f, 1f, 1f);
        readyGo.setText("Ready?");
        for (Action act : readyGo.getActions()){
            readyGo.removeAction(act);
        }
        readyGo.addAction(Actions.fadeOut(0.75f));
        stage.addActor(readyGo);
    }

    private void removeReadyGo(float d){
        readyGo.act(d);
    }

    private void loadSemiBackground() {

        if (greybkgTexture == null){
            greybkgTexture = game.assetManager.get("grayBackground.png", Texture.class);
        }
        greybkg = new Image(greybkgTexture);
        greybkg.setWidth(1920);
        greybkg.setHeight(1920);
        greybkg.setPosition(0, 0);
        greybkg.setOrigin(greybkg.getWidth()/2, greybkg.getHeight()/2);
    }

    private void addGreyBackground(){
        stage.addActor(greybkg);
    }

    private void removeGreyBackground(){
        greybkg.remove();
    }

    @SuppressWarnings("unchecked")
    private void setActual() {
        ArrayList<Integer> presol = new ArrayList();
        for (int i=0; i<MainGame.NUM_OF_COLORS; i++) {
            if (i != actual){
                presol.add(i);
            }
        }
        actual = presol.get((int)(Math.random()*presol.size()));
    }

    private void loadOk() {
        float x, y;

        if (okTexture == null){
            okTexture = game.assetManager.get("ok.png");
        }
        ok = new Image(okTexture);
        ok.setWidth(540);
        ok.setHeight(540);

        x = (MainGame.VIRTUAL_WIDTH-ok.getWidth())/2;
        y = (MainGame.VIRTUAL_HEIGHT-ok.getHeight())/2;
        if (isWidthExtended) {
            x = (MainGame.VIRTUAL_WIDTH-ok.getWidth())/2;
            x = x + (diff/ratio);
        } else {
            y = (MainGame.VIRTUAL_HEIGHT-ok.getHeight())/2;
            y = y + (diff/ratio);
        }
        ok.setPosition(x, y);
        ok.addAction(Actions.fadeOut(0.5f));
        ok.setName("ok");
        ok.setOrigin(ok.getWidth()/2, ok.getHeight()/2);
    }

    private void loadKo() {
        float x, y;

        if (koTexture == null){
            koTexture = game.assetManager.get("ko.png");
        }
        ko = new Image(koTexture);
        ko.setWidth(540);
        ko.setHeight(540);

        x = (MainGame.VIRTUAL_WIDTH-ko.getWidth())/2;
        y = (MainGame.VIRTUAL_HEIGHT-ko.getHeight())/2;
        if (isWidthExtended) {
            x = (MainGame.VIRTUAL_WIDTH-ko.getWidth())/2;
            x = x + (diff/ratio);
        } else {
            y = (MainGame.VIRTUAL_HEIGHT-ko.getHeight())/2;
            y = y + (diff/ratio);
        }
        ko.setPosition(x, y);
        ko.addAction(Actions.fadeOut(1));
        ko.setName("ko");
        ko.setOrigin(ko.getWidth()/2, ko.getHeight()/2);
    }

    private void addOk(){
        stage.addActor(ok);
    }

    private void addKo(){
        ko.setColor(1f, 1f, 1f, 1f);
        for (Action act : ko.getActions()){
            ko.removeAction(act);
        }
        ko.addAction(Actions.fadeOut(1));
        stage.addActor(ko);
    }

    private void loadLogo() {
        Texture logoTexture;
        ImageButton.ImageButtonStyle logoStyle;
        float x, y;

        logoTexture = game.assetManager.get("circle.png", Texture.class);
        logoTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        logoStyle = new ImageButton.ImageButtonStyle();
        logoStyle.up = new SpriteDrawable(new Sprite(logoTexture));
        logoStyle.down = new SpriteDrawable(new Sprite(logoTexture));
        logo = new ImageButton(logoStyle);
        logo.setWidth(900);
        logo.setHeight(900);

        x = (MainGame.VIRTUAL_WIDTH-logo.getWidth())/2;
        y = (MainGame.VIRTUAL_HEIGHT-logo.getHeight())/2;
        if (isWidthExtended) {
            x = (MainGame.VIRTUAL_WIDTH-logo.getWidth())/2;
            x = x + (diff/ratio);
        } else {
            y = (MainGame.VIRTUAL_HEIGHT-logo.getHeight())/2;
            y = y + (diff/ratio);
        }
        logo.setPosition(x, y);

        logo.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.state = MainGame.GameState.TRANSITION;
            }
        });
        logo.setTransform(true);
        stage.addActor(logo);
        logo.setOrigin(logo.getWidth()/2, logo.getHeight()/2);
    }

    private void loadPlayText() {
        float w = MainGame.VIRTUAL_WIDTH;
        float h = MainGame.VIRTUAL_HEIGHT;

        if (isWidthExtended){
            w += diff;
        } else {
            h += diff;
        }

        playText = new Label ("Play", game.getStyleText());
        playText.setWidth(w);
        playText.setHeight(h);
        playText.setFontScale(1f);
        playText.setAlignment(Align.center);
        playText.setPosition(0, 0);
        playText.addAction(Actions.fadeOut(1));
        stage.addActor(playText);
    }

    private void loadArrow(){
        Texture arrowTexture;

        if (arrow == null){

            float x, y;

            arrowTexture = game.assetManager.get("arrow.png", Texture.class);
            arrowTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            arrow = new Image(arrowTexture);
            arrow.setWidth(50);
            arrow.setHeight(50);

            x = (MainGame.VIRTUAL_WIDTH-arrow.getWidth())/2;
            y = (MainGame.VIRTUAL_HEIGHT-arrow.getHeight())/1.45f;
            if (isWidthExtended) {
                x = (MainGame.VIRTUAL_WIDTH-arrow.getWidth())/2;
                x = x + (diff/ratio);
            } else {
                y = y + (diff/ratio);
            }
            arrow.setPosition(x, y);

            arrow.setOrigin(arrow.getWidth()/2, arrow.getHeight()/2);

        }
    }

    private void addHighscoreNumber(){ stage.addActor(highscoreNumber); }

    private void addHighscoreText(){ stage.addActor(highscoreText); }

    private void removeHighscoreNumber(){ highscoreNumber.remove(); }

    private void removeHighscoreText(){ highscoreText.remove(); }

    private void loadHighscoreNumber(){
        float w = MainGame.VIRTUAL_WIDTH;
        float h = MainGame.VIRTUAL_HEIGHT;

        if (isWidthExtended){
            w += diff;
        } else {
            h += diff;
        }

        highscoreNumber = new Label (MainGame.getHighScore()+"", game.getStyleNumber());
        highscoreNumber.setWidth(w);
        highscoreNumber.setHeight(575);
        highscoreNumber.setFontScale(1f);
        highscoreNumber.setAlignment(Align.center);
        highscoreNumber.setPosition(w/4, h-highscoreNumber.getHeight());
    }

    private void loadHighscoreText(){
        float w = MainGame.VIRTUAL_WIDTH;
        float h = MainGame.VIRTUAL_HEIGHT;

        if (isWidthExtended){
            w += diff;
        } else {
            h += diff;
        }

        highscoreText = new Label ("HIGHSCORE", game.getStyleText());
        highscoreText.setWidth(w);
        highscoreText.setHeight(250);
        highscoreText.setFontScale(0.35f);
        highscoreText.setAlignment(Align.center);
        highscoreText.setPosition(w/4, h-highscoreText.getHeight());
    }

    private void loadPointsNumber() {
        float w = MainGame.VIRTUAL_WIDTH;
        float h = MainGame.VIRTUAL_HEIGHT;

        if (isWidthExtended){
            w += diff;
        } else {
            h += diff;
        }

        pointsNumber = new Label (game.getPoints()+"", game.getStyleNumber());
        pointsNumber.setWidth(w);
        pointsNumber.setHeight(575);
        pointsNumber.setFontScale(1f);
        pointsNumber.setAlignment(Align.center);
        pointsNumber.setPosition(0, h-pointsNumber.getHeight());
    }

    private void loadPointsText() {
        float w = MainGame.VIRTUAL_WIDTH;
        float h = MainGame.VIRTUAL_HEIGHT;

        if (isWidthExtended){
            w += diff;
        } else {
            h += diff;
        }

        pointsText = new Label ("SCORE", game.getStyleText());
        pointsText.setWidth(w);
        pointsText.setHeight(250);
        pointsText.setFontScale(0.35f);
        pointsText.setAlignment(Align.center);
        pointsText.setPosition(0, h-pointsText.getHeight());

    }

    private void loadBigButton() {
        if (bigButton == null){

            float x, y;

            bigButtonTexture = game.assetManager.get("btn_" + actual + ".png");
            bigButtonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            bigButtonStyle = new ImageButton.ImageButtonStyle();
            bigButtonStyle.up = new SpriteDrawable(new Sprite(bigButtonTexture));
            bigButtonStyle.down = new SpriteDrawable(new Sprite(bigButtonTexture));
            bigButton = new ImageButton(bigButtonStyle);
            bigButton.setWidth(540);
            bigButton.setHeight(540);

            x = (MainGame.VIRTUAL_WIDTH-bigButton.getWidth())/2;
            y = (MainGame.VIRTUAL_HEIGHT-bigButton.getHeight())/2;
            if (isWidthExtended) {
                x = (MainGame.VIRTUAL_WIDTH-bigButton.getWidth())/2;
                x = x + (diff/ratio);
            } else {
                y = (MainGame.VIRTUAL_HEIGHT-bigButton.getHeight())/2;
                y = y + (diff/ratio);
            }
            bigButton.setPosition(x, y);

            bigButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    switch (actual)
                    {
                        case 0:
                            if (logo.getRotation()>345||logo.getRotation()<15){
                                correct();
                            } else {
                                wrong();
                            } break;
                        case 1:
                            if (logo.getRotation()>15&&logo.getRotation()<45){
                                correct();
                            } else {
                                wrong();
                            } break;
                        case 2:
                            if (logo.getRotation()>45&&logo.getRotation()<75){
                                correct();
                            } else {
                                wrong();
                            } break;
                        case 3:
                            if (logo.getRotation()>75&&logo.getRotation()<105){
                                correct();
                            } else {
                                wrong();
                            } break;
                        case 4:
                            if (logo.getRotation()>105&&logo.getRotation()<135){
                                correct();
                            } else {
                                wrong();
                            } break;
                        case 5:
                            if (logo.getRotation()>135&&logo.getRotation()<165){
                                correct();
                            } else {
                                wrong();
                            } break;
                        case 6:
                            if (logo.getRotation()>165&&logo.getRotation()<195){
                                correct();
                            } else {
                                wrong();
                            } break;
                        case 7:
                            if (logo.getRotation()>195&&logo.getRotation()<225){
                                correct();
                            } else {
                                wrong();
                            } break;
                        case 8:
                            if (logo.getRotation()>225&&logo.getRotation()<255){
                                correct();
                            } else {
                                wrong();
                            } break;
                        case 9:
                            if (logo.getRotation()>255&&logo.getRotation()<285){
                                correct();
                            } else {
                                wrong();
                            } break;
                        case 10:
                            if (logo.getRotation()>285&&logo.getRotation()<315){
                                correct();
                            } else {
                                wrong();
                            } break;
                        case 11:
                            if (logo.getRotation()>315&&logo.getRotation()<345){
                                correct();
                            } else {
                                wrong();
                            } break;
                    }
                }
            });
            bigButton.setTransform(true);
            bigButton.setOrigin(bigButton.getWidth()/2, bigButton.getHeight()/2);
        }
    }

    private void loadReloadButton() {
        Texture reloadButtonTexture;
        ImageButton.ImageButtonStyle reloadButtonStyle;

        if (reloadButton == null) {

            float x, y;

            reloadButtonTexture = game.assetManager.get("reload.png");

            reloadButtonStyle = new ImageButton.ImageButtonStyle();
            reloadButtonStyle.up = new SpriteDrawable(new Sprite(reloadButtonTexture));
            reloadButtonStyle.down = new SpriteDrawable(new Sprite(reloadButtonTexture));
            reloadButton = new ImageButton(reloadButtonStyle);
            reloadButton.setWidth(540);
            reloadButton.setHeight(540);

            x = (MainGame.VIRTUAL_WIDTH - reloadButton.getWidth()) / 2;
            y = (MainGame.VIRTUAL_HEIGHT - reloadButton.getHeight()) / 2;
            if (isWidthExtended) {
                x = (MainGame.VIRTUAL_WIDTH - reloadButton.getWidth()) / 2;
                x = x + (diff / ratio);
            } else {
                y = (MainGame.VIRTUAL_HEIGHT - reloadButton.getHeight()) / 2;
                y = y + (diff / ratio);
            }
            reloadButton.setPosition(x, y);
            reloadButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.state = MainGame.GameState.TRANSITION;
                    setActual();
                    newButtonColor();
                    game.setPoints(0);
                    pointsNumber.setText(game.getPoints()+"");
                    removeHighscoreNumber();
                    removeHighscoreText();
                    removeReloadButton();
                    changeScorePosition(game.state);
                }
            });
            reloadButton.setTransform(true);
            reloadButton.setOrigin(reloadButton.getWidth() / 2, reloadButton.getHeight() / 2);
        }
    }

    private void addReloadButton() { stage.addActor(reloadButton); }

    private void removeReloadButton() { reloadButton.remove(); }

    private void addArrow() {
        stage.addActor(arrow);
    }

    private void removeArrow() { arrow.remove(); }

    private void addBigButton() {
        stage.addActor(bigButton);
    }

    private void removeBigButton() { bigButton.remove(); }

    private void addPointsText() {
        stage.addActor(pointsText);
    }

    private void addPointsNumber(){
        stage.addActor(pointsNumber);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
        //game.state = MainGame.GameState.MENU;
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
        game.state = MainGame.GameState.MENU;
    }

    @Override
    public void dispose() {
        game.state = MainGame.GameState.MENU;
    }
}
