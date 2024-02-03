package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.Game.MindWeaver;
import com.mygdx.Handlers.*;
import com.mygdx.Listeners.MyContactListener;
import com.mygdx.Listeners.GameInputProcessor;
import com.mygdx.Objects.Door;
import com.mygdx.Objects.PressurePlate;
import com.mygdx.Tools.MyTimer;
import com.mygdx.RoleCast.BaseGoblin;
import com.mygdx.RoleCast.Mage;
import com.mygdx.Handlers.B2WorldHandler;
import com.mygdx.Helpers.Constants;
import com.mygdx.Tools.MyResourceManager;
import com.mygdx.Tools.ShapeDrawer;

import java.util.concurrent.atomic.AtomicInteger;

public class GameScreen implements Screen {
    private final MyTimer timer;
    private final MindWeaver game;
    private final OrthographicCamera gameCam;
    private final Viewport gamePort;
    private final OrthogonalTiledMapRenderer renderer;
    private final World world;    // World holding all the physical objects
    private final Box2DDebugRenderer b2dr;
    private final GameInputProcessor inputProcessor;
    private final EntityHandler entityHandler;
    private final ObjectHandler objectHandler;
    private final VisionMap visionMap;
    private final CharacterCycle characterCycle;
    private final ShapeDrawer shapeDrawer;
    private final ShaderHandler shaderHandler;
    public GameScreen(MindWeaver game, int level, MyResourceManager resourceManager, GameInputProcessor inputProcessor) {

        this.game = game;
        this.inputProcessor = inputProcessor;

        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());      // Full-screen

        // Creating tile map
        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap map = null;

        switch (level) {
            case 1:
                map = mapLoader.load("Tilemaps/level1.tmx");
                break;
            case 2:
                map = mapLoader.load("Tilemaps/level2.tmx");
                break;
            default:
                break;
        }

        renderer = new OrthogonalTiledMapRenderer(map, 1 / Constants.PPM);
        world = new World(new Vector2(0, -Constants.G), true);
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(Constants.TILE_SIZE * 30 / Constants.PPM, Constants.TILE_SIZE * 17 / Constants.PPM, gameCam);
        gameCam.position.set(2, 77, 0);

        AtomicInteger eidAllocator = new AtomicInteger();
        shaderHandler = new ShaderHandler();
        shapeDrawer = new ShapeDrawer(shaderHandler, game.batch);
        timer = new MyTimer();

        objectHandler = new ObjectHandler();
        Door door = new Door(world, resourceManager, 330, 138);
        objectHandler.addObject(door);
        PressurePlate pressurePlate = new PressurePlate(world, resourceManager, 200, 131, 1);
        pressurePlate.addReactable(door);
        objectHandler.addObject(pressurePlate);
        entityHandler = new EntityHandler();
        visionMap =  new VisionMap(world, shapeDrawer);
        characterCycle = new CharacterCycle(visionMap);

        Mage mage = new Mage(250, 200, world, eidAllocator.getAndIncrement(), timer, resourceManager, 3, characterCycle, visionMap);
        entityHandler.addEntity(mage);
        characterCycle.initialize(mage);

        entityHandler.addEntity(new BaseGoblin(180, 150, world, eidAllocator.getAndIncrement(), timer, resourceManager, characterCycle,visionMap));
        entityHandler.addEntity(new BaseGoblin(350, 150, world, eidAllocator.getAndIncrement(), timer, resourceManager, characterCycle,visionMap));

        visionMap.initialize(entityHandler);
        inputProcessor.setGameVariables(characterCycle);

        world.setContactListener(new MyContactListener(entityHandler, visionMap, characterCycle));
        b2dr = new Box2DDebugRenderer();
        new B2WorldHandler(world, map, resourceManager, timer, eidAllocator);     //Creating world
    }

    @Override
    public void show() {  }

    public void update(float delta) {

        shaderHandler.update(delta);
        entityHandler.update(delta);
        visionMap.update(delta);
        timer.update(delta);
        inputProcessor.update();
        objectHandler.update(delta);

        world.step(1/60f, 6, 2);

        gameCam.position.set(characterCycle.getCurrentCharacter().getPosition().x, characterCycle.getCurrentCharacter().getPosition().y + 20 / Constants.PPM, 0);

        entityHandler.handleEntities();
    }

    @Override
    public void render(float delta) {

        update(delta);

        // Clearing the screen
        Gdx.gl.glClearColor( 0, 0, 0, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(gameCam.combined);

        renderer.setView(gameCam);
        renderer.render();
        entityHandler.render(game.batch);
        objectHandler.render(game.batch);
        shapeDrawer.render(game.batch);

        gameCam.update();

        //b2dr.render(world, gameCam.combined);
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() { }

}
