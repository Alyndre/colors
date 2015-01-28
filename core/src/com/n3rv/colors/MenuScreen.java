package com.n3rv.colors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

import java.awt.Color;
import java.awt.event.InputEvent;
import java.util.ArrayList;

public class MenuScreen implements Screen {

    private final static float VIRTUAL_WIDTH = 1080;
    private final static float VIRTUAL_HEIGHT = 1920;
    private final static int REAL_WIDTH = Gdx.graphics.getWidth();
    private final static int REAL_HEIGHT = Gdx.graphics.getHeight();
    private final static int NUM_OF_COLORS = 12;

    public MainGame game;

    private boolean isWidthExtended = false;
    private boolean isPressed = false;
    private float ratio = 0.0f;
    private float diff = 0.0f;

    private OrthographicCamera camera;
    private Stage stage;

    private Texture logoTexture;
    private ImageButton.ImageButtonStyle logoStyle;
    private ImageButton logo;

    private Texture bigButtonTexture;
    private ImageButton.ImageButtonStyle bigButtonStyle;
    private ImageButton bigButton;

    private Texture reloadButtonTexture;
    private ImageButton.ImageButtonStyle reloadButtonStyle;
    private ImageButton reloadButton;

    private Texture arrowTexture;
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

    public MenuScreen(MainGame gam) {

        game = gam;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1080, 1920);
        ExtendViewport stagevp = new ExtendViewport(1080, 1920, camera);

        stage = new Stage(stagevp);
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        game.state = MainGame.GameState.MENU;

        isWidthExtended = ((VIRTUAL_WIDTH/REAL_WIDTH)<(VIRTUAL_HEIGHT/REAL_HEIGHT));
        if(isWidthExtended){
            ratio = VIRTUAL_HEIGHT/REAL_HEIGHT;
            diff = Math.abs(REAL_WIDTH-(VIRTUAL_WIDTH/ratio));
        } else {
            ratio = VIRTUAL_WIDTH/REAL_WIDTH;
            diff = Math.abs(REAL_HEIGHT-(VIRTUAL_HEIGHT/ratio));
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
        loadOk();
        loadKo();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(253/255f, 253/255f, 253/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        if(game.state == MainGame.GameState.MENU) {
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

        stage.draw();

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
                if(a.getName() == "ok"){
                    a.act(delta);
                }
            }
            if ((ok.getActions().size == 0)&&(isPressed)) {
                ok.remove();
                ok.setColor(1f, 1f, 1f, 1f);
                isPressed = false;
            }

        }

        if (game.state == MainGame.GameState.OVER) {

            addHighscoreText();
            addHighscoreNumber();

            changeScorePosition(game.state);

            for (Actor a : stage.getActors()){
                if(a.getName() == "ko"){
                    a.act(delta);
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
        float w = VIRTUAL_WIDTH;
        float h = VIRTUAL_HEIGHT;

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
        isPressed = true;
        setActual();
        newButtonColor();
    }

    private void wrong(){
        game.state = MainGame.GameState.OVER;
        removeArrow();
        addKo();
    }

    private void newButtonColor(){
        bigButtonTexture = new Texture(Gdx.files.internal("btn_"+ actual +".png"));

        bigButtonStyle = new ImageButton.ImageButtonStyle();
        bigButtonStyle.up = new SpriteDrawable(new Sprite(bigButtonTexture));
        bigButtonStyle.down = new SpriteDrawable(new Sprite(bigButtonTexture));
        bigButton.setStyle(bigButtonStyle);
    }

    private void loadReadyGo(){
        float w = VIRTUAL_WIDTH;
        float h = VIRTUAL_HEIGHT;

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
            greybkgTexture = new Texture(Gdx.files.internal("grayBackground.png"));
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

    private void setActual() {
        ArrayList<Integer> presol = new ArrayList();
        for (int i=0; i<NUM_OF_COLORS; i++) {
            if (i != actual){
                presol.add(i);
            }
        }
        actual = presol.get((int)(Math.random()*presol.size()));
    }

    private void loadOk() {
        float x, y;

        if (okTexture == null){
            okTexture = new Texture(Gdx.files.internal("ok.png"));
        }
        ok = new Image(okTexture);
        ok.setWidth(540);
        ok.setHeight(540);

        x = (VIRTUAL_WIDTH-ok.getWidth())/2;
        y = (VIRTUAL_HEIGHT-ok.getHeight())/2;
        if (isWidthExtended) {
            x = (VIRTUAL_WIDTH-ok.getWidth())/2;
            x = x + (diff/ratio);
        } else {
            y = (VIRTUAL_HEIGHT-ok.getHeight())/2;
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
            koTexture = new Texture(Gdx.files.internal("ko.png"));
        }
        ko = new Image(koTexture);
        ko.setWidth(540);
        ko.setHeight(540);

        x = (VIRTUAL_WIDTH-ko.getWidth())/2;
        y = (VIRTUAL_HEIGHT-ko.getHeight())/2;
        if (isWidthExtended) {
            x = (VIRTUAL_WIDTH-ko.getWidth())/2;
            x = x + (diff/ratio);
        } else {
            y = (VIRTUAL_HEIGHT-ko.getHeight())/2;
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
        float x, y;

        logoTexture = new Texture(Gdx.files.internal("circle.png"));
        logoStyle = new ImageButton.ImageButtonStyle();
        logoStyle.up = new SpriteDrawable(new Sprite(logoTexture));
        logoStyle.down = new SpriteDrawable(new Sprite(logoTexture));
        logo = new ImageButton(logoStyle);
        logo.setWidth(900);
        logo.setHeight(900);

        x = (VIRTUAL_WIDTH-logo.getWidth())/2;
        y = (VIRTUAL_HEIGHT-logo.getHeight())/2;
        if (isWidthExtended) {
            x = (VIRTUAL_WIDTH-logo.getWidth())/2;
            x = x + (diff/ratio);
        } else {
            y = (VIRTUAL_HEIGHT-logo.getHeight())/2;
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
        float w = VIRTUAL_WIDTH;
        float h = VIRTUAL_HEIGHT;

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
        if (arrow == null){

            float x, y;

            arrowTexture = new Texture(Gdx.files.internal("arrow.png"));
            arrow = new Image(arrowTexture);
            arrow.setWidth(50);
            arrow.setHeight(50);

            x = (VIRTUAL_WIDTH-arrow.getWidth())/2;
            y = (VIRTUAL_HEIGHT-arrow.getHeight())/1.45f;
            if (isWidthExtended) {
                x = (VIRTUAL_WIDTH-arrow.getWidth())/2;
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
        float w = VIRTUAL_WIDTH;
        float h = VIRTUAL_HEIGHT;

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
        float w = VIRTUAL_WIDTH;
        float h = VIRTUAL_HEIGHT;

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
        float w = VIRTUAL_WIDTH;
        float h = VIRTUAL_HEIGHT;

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
        float w = VIRTUAL_WIDTH;
        float h = VIRTUAL_HEIGHT;

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

            bigButtonTexture = new Texture(Gdx.files.internal("btn_" + actual + ".png"));

            bigButtonStyle = new ImageButton.ImageButtonStyle();
            bigButtonStyle.up = new SpriteDrawable(new Sprite(bigButtonTexture));
            bigButtonStyle.down = new SpriteDrawable(new Sprite(bigButtonTexture));
            bigButton = new ImageButton(bigButtonStyle);
            bigButton.setWidth(540);
            bigButton.setHeight(540);

            x = (VIRTUAL_WIDTH-bigButton.getWidth())/2;
            y = (VIRTUAL_HEIGHT-bigButton.getHeight())/2;
            if (isWidthExtended) {
                x = (VIRTUAL_WIDTH-bigButton.getWidth())/2;
                x = x + (diff/ratio);
            } else {
                y = (VIRTUAL_HEIGHT-bigButton.getHeight())/2;
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
        if (reloadButton == null) {

            float x, y;

            reloadButtonTexture = new Texture(Gdx.files.internal("reload.png"));

            reloadButtonStyle = new ImageButton.ImageButtonStyle();
            reloadButtonStyle.up = new SpriteDrawable(new Sprite(reloadButtonTexture));
            reloadButtonStyle.down = new SpriteDrawable(new Sprite(reloadButtonTexture));
            reloadButton = new ImageButton(reloadButtonStyle);
            reloadButton.setWidth(540);
            reloadButton.setHeight(540);

            x = (VIRTUAL_WIDTH - reloadButton.getWidth()) / 2;
            y = (VIRTUAL_HEIGHT - reloadButton.getHeight()) / 2;
            if (isWidthExtended) {
                x = (VIRTUAL_WIDTH - reloadButton.getWidth()) / 2;
                x = x + (diff / ratio);
            } else {
                y = (VIRTUAL_HEIGHT - reloadButton.getHeight()) / 2;
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
