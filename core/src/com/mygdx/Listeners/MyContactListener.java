package com.mygdx.Listeners;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.Handlers.EntityHandler;
import com.mygdx.Handlers.ControlHandler;
import com.mygdx.Handlers.VisionHandler;
import com.mygdx.RoleCast.Mage;
import com.mygdx.RoleCast.PlayableCharacter;
import com.mygdx.Helpers.Constants.*;

public class MyContactListener implements ContactListener {

    private Fixture fa;
    private Fixture fb;
    private final EntityHandler entityHandler;
    private final VisionHandler visionHandler;
    private final ControlHandler controlHandler;

    public MyContactListener(EntityHandler entityHandler, VisionHandler visionHandler, ControlHandler controlHandler) {
        this.entityHandler = entityHandler;
        this.visionHandler = visionHandler;
        this.controlHandler = controlHandler;
    }

    @Override
    public void beginContact(Contact contact) {

        if (handleFixtures(contact)) return;

        PlayableCharacter character;

        if (fa.getUserData().equals("leftSensor") || fb.getUserData().equals("leftSensor")) {
            character = (PlayableCharacter) entityHandler.getEntity(fa.getUserData().equals("leftSensor") ? fa.getBody() : fb.getBody());
            character.setWallState(-1);
        } else if (fa.getUserData().equals("rightSensor") || fb.getUserData().equals("rightSensor")) {
            character = (PlayableCharacter) entityHandler.getEntity(fa.getUserData().equals("rightSensor") ? fa.getBody() : fb.getBody());
            character.setWallState(1);
        } else if (fa.getUserData().equals("bottomSensor") || fb.getUserData().equals("bottomSensor")) {
            character = (PlayableCharacter) entityHandler.getEntity(fa.getUserData().equals("bottomSensor") ? fa.getBody() : fb.getBody());
            character.land();
            if (character.getMovementState() == MSTATE.PREV) character.setMovementState(MSTATE.HSTILL);
        } else if (fa.getUserData().equals("player_hb") || fb.getUserData().equals("player_hb")) {
            character = (PlayableCharacter) entityHandler.getEntity(fa.getUserData().equals("player_hb") ? fa.getBody() : fb.getBody());
            entityHandler.addEntityOperation(character, "die");
        } else if (fa.getUserData().equals("checkpoint") || fb.getUserData().equals("checkpoint")) {
            character = (PlayableCharacter) entityHandler.getEntity(fa.getUserData().equals("checkpoint") ? fb.getBody() : fa.getBody());
            ((Mage) character).setCheckPoint(fa.getUserData().equals("checkpoint") ? fb.getBody().getPosition() : fa.getBody().getPosition());
        } else if (fa.getUserData().equals("vision") || fb.getUserData().equals("vision")) {

            PlayableCharacter source = (PlayableCharacter) entityHandler.getEntity(fa.getUserData().equals("vision") ? fa.getBody() : fb.getBody());
            PlayableCharacter target = (PlayableCharacter) entityHandler.getEntity((Integer) (fa.getUserData().equals("vision") ? fb : fa).getUserData());

            if (visionHandler.getNextNeighbour(source) == null) source.setTarget(target);
            visionHandler.addNeighbour(source, target);
//            System.out.println("add");
//            entityHandler.getUnivEyesight().printROV();
        }
    }

    @Override
    public void endContact(Contact contact) {

        if (handleFixtures(contact)) return;

        PlayableCharacter character;

        if (fa.getUserData().equals("leftSensor") || fb.getUserData().equals("leftSensor")) {
            character = (PlayableCharacter) entityHandler.getEntity(fa.getUserData().equals("leftSensor") ? fa.getBody() : fb.getBody());
            character.setWallState(0);
        } else if (fa.getUserData().equals("rightSensor") || fb.getUserData().equals("rightSensor")) {
            character = (PlayableCharacter) entityHandler.getEntity(fa.getUserData().equals("rightSensor") ? fa.getBody() : fb.getBody());
            character.setWallState(0);
        } else if (fa.getUserData().equals("bottomSensor") || fb.getUserData().equals("bottomSensor")) {
            character = (PlayableCharacter) entityHandler.getEntity(fa.getUserData().equals("bottomSensor") ? fa.getBody() : fb.getBody());
            character.removePlayerState(PSTATE.ON_GROUND);
        } else if (fa.getUserData().equals("vision") || fb.getUserData().equals("vision")) {

            PlayableCharacter source = (PlayableCharacter) entityHandler.getEntity(fa.getUserData().equals("vision") ? fa.getBody() : fb.getBody());
            PlayableCharacter target = (PlayableCharacter) entityHandler.getEntity((Integer) (fa.getUserData().equals("vision") ? fb : fa).getUserData());

            visionHandler.removeNeighbour(target);
//            System.out.println("remove");
//            entityHandler.getUnivEyesight().printROV();
            source.removeTarget();
            character = controlHandler.getCurrCharacter();
            if (controlHandler.characterRollback()) character.looseControl();
            PlayableCharacter possible = visionHandler.getNextNeighbour(source);
            if (possible != null) source.setTarget(possible);

        }
    }

    public boolean handleFixtures(Contact contact) {
        fa = contact.getFixtureA();
        fb = contact.getFixtureB();

        return fa == null || fb == null;
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
