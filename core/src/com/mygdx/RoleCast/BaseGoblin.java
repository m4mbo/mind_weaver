package com.mygdx.RoleCast;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.Handlers.CharacterCycle;
import com.mygdx.Handlers.VisionMap;
import com.mygdx.Tools.MyResourceManager;
import com.mygdx.Tools.MyTimer;
import com.mygdx.Helpers.Constants;

public class BaseGoblin extends PlayableCharacter{

    public BaseGoblin(int x, int y, World world, int id, MyTimer timer, MyResourceManager myResourceManager, CharacterCycle characterCycle, VisionMap visionMap) {
        super(world, id, timer, myResourceManager, characterCycle, visionMap);

        loadSprites();

        // Initializing sprite
        setAnimation(TextureRegion.split(resourceManager.getTexture("goblin_idle"), 20, 14)[0], 1/5f, false, 1f);

        BodyDef bdef = new BodyDef();
        bdef.position.set(x / Constants.PPM, y / Constants.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape circleShape = new CircleShape();
        PolygonShape polygonShape = new PolygonShape();

        //Create body fixture
        polygonShape.setAsBox(8 / Constants.PPM, 6 / Constants.PPM, new Vector2(0, 0), 0);
        fdef.shape = polygonShape;
        fdef.friction = 0;
        fdef.filter.categoryBits = Constants.BIT_GOBLIN;
        fdef.filter.maskBits = Constants.BIT_GROUND | Constants.BIT_MAGE | Constants.BIT_GOBLIN | Constants.BIT_ROV;
        b2body.createFixture(fdef).setUserData(id);

        fdef = new FixtureDef();

        //Create player hitbox
        polygonShape.setAsBox(9 / Constants.PPM, 7 / Constants.PPM, new Vector2(0, 0), 0);
        fdef.shape = polygonShape;
        fdef.filter.maskBits = Constants.BIT_HAZARD;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData("goblin_hb");

        //Create mage range of vision
        circleShape.setRadius(140 / Constants.PPM);
        fdef.shape = circleShape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = Constants.BIT_ROV;
        fdef.filter.maskBits = Constants.BIT_GOBLIN;
        b2body.createFixture(fdef).setUserData("vision");

        //Create right sensor
        polygonShape.setAsBox(1 / Constants.PPM, 3 / Constants.PPM, new Vector2(8.2f / Constants.PPM, 0), 0);
        fdef.shape = polygonShape;
        fdef.isSensor = true;
        fdef.filter.maskBits = Constants.BIT_GROUND;
        b2body.createFixture(fdef).setUserData("rightSensor");

        //Create left sensor
        polygonShape.setAsBox(1 / Constants.PPM, 3 / Constants.PPM, new Vector2(-8.2f / Constants.PPM, 0), 0);
        fdef.shape = polygonShape;
        fdef.isSensor = true;
        fdef.filter.maskBits = Constants.BIT_GROUND;
        b2body.createFixture(fdef).setUserData("leftSensor");

        //Create bottom sensor
        polygonShape.setAsBox(6.6f / Constants.PPM, 1 / Constants.PPM, new Vector2(0, -6 / Constants.PPM), 0);
        fdef.shape = polygonShape;
        fdef.isSensor = true;
        fdef.filter.maskBits = Constants.BIT_GROUND | Constants.BIT_GOBLIN | Constants.BIT_MAGE;
        b2body.createFixture(fdef).setUserData("bottomSensor");
    }

    @Override
    public void loadSprites() {
        // Loading all textures
        resourceManager.loadTexture("Goblins/basegoblin_run.png", "goblin_run");
        resourceManager.loadTexture("Goblins/basegoblin_idle.png", "goblin_idle");
        resourceManager.loadTexture("Goblins/basegoblin_jump.png", "goblin_jump");
        resourceManager.loadTexture("Goblins/basegoblin_land.png", "goblin_land");
        resourceManager.loadTexture("Goblins/basegoblin_fall.png", "goblin_fall");
    }

    @Override
    public void handleAnimation() {
        switch (currAState) {
            case RUN:
                setAnimation(TextureRegion.split(resourceManager.getTexture("goblin_run"), 20, 14)[0], 1/14f, false, 1f);
                break;
            case IDLE:
                setAnimation(TextureRegion.split(resourceManager.getTexture("goblin_idle"), 20, 14)[0], 1/5f, false, 1f);
                break;
            case JUMP:
                setAnimation(TextureRegion.split(resourceManager.getTexture("goblin_jump"), 20, 14)[0], 1/17f, true, 1f);
                break;
            case FALL:
                setAnimation(TextureRegion.split(resourceManager.getTexture("goblin_fall"), 20, 14)[0], 1/5f, true, 1f);
                break;
            case LAND:
                setAnimation(TextureRegion.split(resourceManager.getTexture("goblin_land"), 20, 14)[0], 1/14f, false, 1f);
                break;
        }
    }

    @Override
    public void die() { }
}
