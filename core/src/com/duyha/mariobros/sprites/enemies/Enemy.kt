package com.duyha.mariobros.sprites.enemies

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
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

    lateinit var velocity: Vector2

    init {
        this.setPosition(x, y)
        this.defineEnemy()
        velocity = Vector2(1f, 0f)
        body.isActive = false
    }

    protected abstract fun defineEnemy()
    abstract fun hitOnHead()
    abstract fun update(dt: Float)

    fun reverseVelocity(x: Boolean, y: Boolean) {
        if (x)
            velocity.x = - velocity.x
        if (y)
            velocity.y = -velocity.y
    }

}
