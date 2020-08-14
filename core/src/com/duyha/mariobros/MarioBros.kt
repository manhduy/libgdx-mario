package com.duyha.mariobros

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.duyha.mariobros.screens.PlayScreen

class MarioBros : Game() {

    companion object {
        const val V_WIDTH = 400f
        const val V_HEIGHT = 208f
        const val PPM = 100f //Pixel per meter

        const val GROUND_BIT = 1.toShort()
        const val MARIO_BIT = 2.toShort()
        const val BRICK_BIT = 4.toShort()
        const val COIN_BIT = 8.toShort()
        const val DESTROYED_BIT = 16.toShort()
        const val OBJECT_BIT: Short = 32
        const val ENEMY_BIT: Short = 64
        const val ENEMY_HEAD_BIT: Short = 128

        lateinit var manager: AssetManager
    }

    lateinit var batch: SpriteBatch
    override fun create() {
        batch = SpriteBatch()

        manager = AssetManager()
        manager.load("audio/music/mario_music.ogg", Music::class.java)
        manager.load("audio/sounds/coin.wav", Sound::class.java)
        manager.load("audio/sounds/bump.wav", Sound::class.java)
        manager.load("audio/sounds/breakblock.wav", Sound::class.java)
        manager.finishLoading()

        setScreen(PlayScreen(this))
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        manager.dispose()
        batch.dispose()
        super.dispose()
    }
}