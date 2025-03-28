package com.jakesiewjk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    public final Color BACKGROUND_COLOR = new Color(153f / 255f, 255f / 255f, 236f / 255f, 1.0f);
    private PerspectiveCamera camera;
    private CamController cameraController;
    private Environment environment;
    private Model model;
    private Texture textureGround;
    private Array<ModelInstance> instances;
    private ModelBatch modelBatch;

    @Override
    public void show() {
        // Prepare your screen here.
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
        camera.position.set(10f, 1.5f, 5.f);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        cameraController = new CamController(camera);
        Gdx.input.setInputProcessor(cameraController);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .6f, .6f, .6f, 1f));
        environment.add(new DirectionalLight().set(.8f, .8f, .8f, -1f, -.8f, -.2f));

        textureGround = new Texture(Gdx.files.internal("textures/Stylized_Stone_Floor_005_basecolor.jpg"), true);
        textureGround.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
        textureGround.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        TextureRegion textureRegion = new TextureRegion(textureGround);
        int repeats = 10;
        textureRegion.setRegion(0, 0, textureGround.getWidth() * repeats, textureGround.getHeight() * repeats);

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(100f, 1f, 100f, new Material(TextureAttribute.createDiffuse(textureRegion)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
                        | VertexAttributes.Usage.TextureCoordinates);

        instances = new Array<>();
        instances.add(new ModelInstance(model, 0, -1, 0));
        modelBatch = new ModelBatch();

        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        camera.position.set(10f, Settings.eyeHeight, 5f);
        camera.lookAt(0, Settings.eyeHeight, 0);
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        cameraController.update(delta);

        // render
        ScreenUtils.clear(BACKGROUND_COLOR, true);
        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        camera.update();
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
        // Destroy screen's assets here.
        modelBatch.dispose();
        model.dispose();
        textureGround.dispose();
    }
}