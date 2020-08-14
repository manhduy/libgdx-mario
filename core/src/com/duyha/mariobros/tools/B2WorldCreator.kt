package com.duyha.mariobros.tools

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.physics.box2d.*
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.screens.PlayScreen
import com.duyha.mariobros.sprites.Brick
import com.duyha.mariobros.sprites.Coin

class B2WorldCreator(
        private val playScreen: PlayScreen
) {

    private val world = playScreen.world
    private val map = playScreen.map

    init {
        val bodyDef = BodyDef()
        val shape = PolygonShape()
        val fixtureDef = FixtureDef()
        var body: Body

        //Create ground bodies/fixtures
        for (mapObj in map.layers[2].objects.getByType(RectangleMapObject::class.java)) {
            val rectangle = mapObj.rectangle
            bodyDef.type = BodyDef.BodyType.StaticBody
            bodyDef.position.set((rectangle.x + rectangle.width/2f) / MarioBros.PPM, (rectangle.y + rectangle.height/2f) / MarioBros.PPM)

            body = world.createBody(bodyDef)

            shape.setAsBox(rectangle.width/2f / MarioBros.PPM, rectangle.height/2f / MarioBros.PPM)
            fixtureDef.shape = shape
            body.createFixture(fixtureDef)
        }

        //Create pipe bodies/fixtures
        for (mapObj in map.layers[3].objects.getByType(RectangleMapObject::class.java)) {
            val rectangle = mapObj.rectangle
            bodyDef.type = BodyDef.BodyType.StaticBody
            bodyDef.position.set((rectangle.x + rectangle.width/2f) / MarioBros.PPM, (rectangle.y + rectangle.height/2f) / MarioBros.PPM)

            body = world.createBody(bodyDef)

            shape.setAsBox(rectangle.width/2f / MarioBros.PPM, rectangle.height/2f / MarioBros.PPM)
            fixtureDef.shape = shape
            fixtureDef.filter.categoryBits = MarioBros.OBJECT_BIT
            body.createFixture(fixtureDef)
        }

        //Create coins bodies/fixtures
        for (mapObj in map.layers[4].objects.getByType(RectangleMapObject::class.java)) {
            val rectangle = mapObj.rectangle

            Coin(playScreen, rectangle)
        }

        //Create bricks bodies/fixtures
        for (mapObj in map.layers[5].objects.getByType(RectangleMapObject::class.java)) {
            val rectangle = mapObj.rectangle
            Brick(playScreen, rectangle)
        }
    }

}