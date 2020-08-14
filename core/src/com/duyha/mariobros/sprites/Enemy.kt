package com.duyha.mariobros.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.physics.box2d.Body
import com.duyha.mariobros.screens.PlayScreen

abstract class Enemy(
        protected val playScreen: PlayScreen,
        x: Float,
        y: Float
) : Sprite() {

    protected var world = playScreen.world
    lateinit var body: Body

    protected var setToDestroy = false
    protected var destroyed = false

    init {
        this.setPosition(x, y)
        this.defineEnemy()
    }

    protected abstract fun defineEnemy()
    abstract fun hitOnHead()

}
