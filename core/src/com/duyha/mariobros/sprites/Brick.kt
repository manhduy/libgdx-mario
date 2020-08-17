package com.duyha.mariobros.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.scenes.Hud
import com.duyha.mariobros.screens.PlayScreen

class Brick(
        private val playScreen: PlayScreen,
        private val mapObject: MapObject

) : InteractiveTileObject(playScreen, mapObject) {
    init {
        fixture.userData = this
        setCategoryFilter(MarioBros.BRICK_BIT)
    }

    override fun onHeadHit(mario: Mario) {
        if (mario.isBig()) {
            setCategoryFilter(MarioBros.DESTROYED_BIT)
            getCell().tile = null
            Hud.addCore(200)
            MarioBros.manager.get("audio/sounds/breakblock.wav", Sound::class.java).play()
        }
        MarioBros.manager.get("audio/sounds/bump.wav", Sound::class.java).play()
    }
}