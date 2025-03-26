package com.jakesiewjk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.jakesiewjk.physics.PhysicsView;
import com.jakesiewjk.views.GameView;
import com.jakesiewjk.views.GridView;

public class GameScreen implements Screen {
    private GameView gameView;
    private GridView gridView;
    private PhysicsView physicsView;
    private int windowWidth;
    private int windowHeight;
    private World world;
    private boolean debugRender = false;

    @Override
    public void show() {
        world = new World(Settings.GLTF_FILE);
        Populator.populate(world);
        gameView = new GameView(world);
        gridView = new GridView();
        physicsView = new PhysicsView(world);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
        inputMultiplexer.addProcessor(gameView.getCameraController());
        inputMultiplexer.addProcessor(world.getPlayerController());
    }

    @Override
    public void render(float delta) {

        world.update(delta);
        gameView.render(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Populator.populate(world);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            world.shootBall();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            debugRender = !debugRender;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            toggleFullScreen();
        }

        if (debugRender) {
            gridView.render(gameView.getCamera());
            physicsView.render(gameView.getCamera());
        }
    }

    private void toggleFullScreen() {
        if (!Gdx.graphics.isFullscreen()) {
            windowWidth = Gdx.graphics.getWidth();
            windowHeight = Gdx.graphics.getHeight();

            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            return;
        }

        Gdx.graphics.setWindowedMode(windowWidth, windowHeight);
        resize(windowWidth, windowHeight);
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
        dispose();
    }

    @Override
    public void dispose() {
        gridView.dispose();
        gameView.dispose();
        physicsView.dispose();
        world.dispose();
    }
}