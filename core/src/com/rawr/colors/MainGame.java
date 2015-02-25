package com.rawr.colors;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.assets.AssetManager;

public class MainGame extends Game {

    private static final int INITIAL_SPEED = 65;
    public final static float VIRTUAL_WIDTH = 1080;
    public final static float VIRTUAL_HEIGHT = 1920;
    public static int REAL_WIDTH;
    public static int REAL_HEIGHT;
    public final static int NUM_OF_COLORS = 12;

    public AssetManager assetManager;
    public SpriteBatch batch;
    public BitmapFont font_text;
    public BitmapFont font_number;
    private Label.LabelStyle styleText;
    private Label.LabelStyle styleNumber;
    private Label.LabelStyle styleWhiteText;
    private Integer points = 0;
    public static Preferences prefs;

    public GameState state;
    public enum GameState {
        MENU, TRANSITION, COUNT, GAME, OVER
    }
    private float speed = 0;

    public void create() {
        assetManager = new AssetManager();
        batch = new SpriteBatch();

        setSpeed(INITIAL_SPEED);
        setPoints(0);

        loadFonts();
        setLabelStyles();

        REAL_WIDTH = Gdx.graphics.getWidth();
        REAL_HEIGHT = Gdx.graphics.getHeight();

        // Create (or retrieve existing) preferences file
        prefs = Gdx.app.getPreferences("Colors");

        // Provide default high score of 0
        if (!prefs.contains("HighScore")) {
            prefs.putInteger("HighScore", 0);
        }

        this.setScreen(new SplashScreen(this));

    }

    public float getSpeed(){
        return speed;
    }

    public void setSpeed(Integer x){
        speed = x;
    }

    private void increaseSpeed(){

        speed = speed + (float)(Math.log(points*1.75)/Math.log(2));
    }

    private void setLabelStyles() {
        styleText = new Label.LabelStyle(font_text, Color.DARK_GRAY);
        styleWhiteText = new Label.LabelStyle(font_text, Color.WHITE);
        styleNumber = new Label.LabelStyle(font_number, Color.DARK_GRAY);
    }

    public Label.LabelStyle getStyleText(){
        return styleText;
    }

    public Label.LabelStyle getStyleNumber() {
        return styleNumber;
    }

    public Label.LabelStyle getStyleWhiteText() { return styleWhiteText; }

    private void loadFonts () {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ProximaNova-Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.genMipMaps = true;
        parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.size = 200;
        font_text = generator.generateFont(parameter);
        generator.dispose();

        FreeTypeFontGenerator generator_2 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ProximaNova-Semibold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter_2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter_2.genMipMaps = true;
        parameter_2.minFilter = Texture.TextureFilter.MipMapLinearNearest;
        parameter_2.magFilter = Texture.TextureFilter.Linear;
        parameter_2.size = 200;
        font_number = generator_2.generateFont(parameter_2);
        generator_2.dispose();
    }

    // Receives an integer and maps it to the String highScore in prefs
    public void setHighScore(int val) {
        if(getHighScore() < val) {
            prefs.putInteger("HighScore", val);
            prefs.flush();
        }
    }

    // Retrieves the current high score
    public static int getHighScore() {
        return prefs.getInteger("HighScore");
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        batch.dispose();
        font_text.dispose();
        font_number.dispose();
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer l) {
        points = l;
        setSpeed(INITIAL_SPEED);
    }

    public void addPoints(Integer x) {
        points = points + x;
        increaseSpeed();
    }



}
