package com.duyha.mariobros.sprites

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.*
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.screens.PlayScreen

abstract class InteractiveTileObject(
        private val playScreen: PlayScreen,
        private val bounds: Rectangle
) {

    private var world = playScreen.world
    protected var map = playScreen.map

    var fixture: Fixture
    var body: Body

    init {
        val bdef = BodyDef()
        val fdef = FixtureDef()
        val shape = PolygonShape()

        bdef.type = BodyDef.BodyType.StaticBody
        bdef.position[(bounds.getX() + bounds.getWidth() / 2) / MarioBros.PPM] = (bounds.getY() + bounds.getHeight() / 2) / MarioBros.PPM

        body = world.createBody(bdef)

        shape.setAsBox(bounds.getWidth() / 2 / MarioBros.PPM, bounds.getHeight() / 2 / MarioBros.PPM)
        fdef.shape = shape
        fixture = body.createFixture(fdef)

    }

    abstract fun onHeadHit()

    fun setCategoryFilter(filterBit: Short) {

        val filter = Filter()
        filter.categoryBits = filterBit
        fixture.filterData = filter
    }

    fun getCell(): TiledMapTileLayer.Cell {
        val layer = map.layers[1] as TiledMapTileLayer
        return layer.getCell((body.position.x * MarioBros.PPM / 16).toInt(), (body.position.y * MarioBros.PPM / 16).toInt())
    }

}