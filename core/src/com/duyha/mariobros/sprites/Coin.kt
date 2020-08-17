package com.duyha.mariobros.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileSet
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.items.ItemDef
import com.duyha.mariobros.items.Mushroom
import com.duyha.mariobros.scenes.Hud
import com.duyha.mariobros.screens.PlayScreen

class Coin(
        private val playScreen: PlayScreen,
        private val mapObject: MapObject
) : InteractiveTileObject(playScreen, mapObject) {

    companion object {
        lateinit var tileSet: TiledMapTileSet
        const val BLANK_COIN = 28
    }

    init {
        tileSet = map.tileSets.getTileSet("tileset_gutter")
        fixture.userData = this
        setCategoryFilter(MarioBros.COIN_BIT)
    }

    override fun onHeadHit(mario: Mario) {
        if (getCell().tile.id == BLANK_COIN) {
            MarioBros.manager.get("audio/sounds/bump.wav", Sound::class.java).play()
        } else {
            if (mapObject.properties.containsKey("mushroom")) {
                playScreen.spawnItem(ItemDef(Vector2(body.position.x, body.position.y + 16/MarioBros.PPM), Mushroom::class.java))
                MarioBros.manager.get("audio/sounds/powerup_spawn.wav", Sound::class.java).play()
            } else {
                MarioBros.manager.get("audio/sounds/coin.wav", Sound::class.java).play()
            }
        }
        getCell().tile = tileSet.getTile(BLANK_COIN)
        Hud.addCore(100)
    }
}
