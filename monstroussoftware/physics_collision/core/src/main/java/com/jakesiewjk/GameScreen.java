package com.jakesiewjk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;

public class GameScreen implements Screen {
    private GameView gameView;
    private GridView gridView;
    private PhysicsView physicsView;
    private World world;

    @Override
    public void show() {
        world = new World("models/step4a.gltf");
        Populator.populate(world);
        gameView = new GameView(world);
        gridView = new GridView();
        physicsView = new PhysicsView(world);
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

        gridView.render(gameView.getCamera());
        physicsView.render(gameView.getCamera());
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
        world.dispose();
    }
}