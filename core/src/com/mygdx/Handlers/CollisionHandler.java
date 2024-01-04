package com.mygdx.Handlers;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.Objects.Player;

public class CollisionHandler implements ContactListener {

    Fixture fa;
    Fixture fb;
    Player player;

    public CollisionHandler(Player player) {
        this.player = player;
    }

    @Override
    public void beginContact(Contact contact) {

        if (!handleFixtures(contact)) return;

        if (fa.getUserData().equals("leftSensor") || fb.getUserData().equals("leftSensor")) {
            player.setWallState(2);
        } else if (fa.getUserData().equals("rightSensor") || fb.getUserData().equals("rightSensor")) {
            player.setWallState(1);
        } else if (fa.getUserData().equals("bottomSensor") || fb.getUserData().equals("bottomSensor")) {
            player.setOnGround(true);
        }
    }

    @Override
    public void endContact(Contact contact) {

        if (!handleFixtures(contact)) return;

        if (fa.getUserData().equals("leftSensor") || fb.getUserData().equals("leftSensor") || fa.getUserData().equals("rightSensor") || fb.getUserData().equals("rightSensor")) {
            player.setWallState(0);
        } else if (fa.getUserData().equals("bottomSensor") || fb.getUserData().equals("bottomSensor")) {
            player.setOnGround(false);
        }
    }

    public boolean handleFixtures(Contact contact) {
        fa = contact.getFixtureA();
        fb = contact.getFixtureB();

        if (fa == null || fb == null) {
            return false;
        }
        return true;
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
