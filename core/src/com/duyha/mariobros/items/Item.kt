package com.duyha.mariobros.items

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.screens.PlayScreen
import com.duyha.mariobros.sprites.Mario

abstract class Item(
        private val screen: PlayScreen,
        x: Float,
        y: Float
) : Sprite() {

    protected var world: World = screen.world
    protected lateinit var velocity: Vector2
    protected var toDestroy: Boolean = false
    protected var destroyed: Boolean = false
    protected lateinit var body: Body

    init {
        this.setPosition(x, y)
        this.setBounds(x, y, 16 / MarioBros.PPM, 16 / MarioBros.PPM)
        defineItem()
    }

    abstract fun defineItem()
    abstract fun use(mario: Mario)

    override fun draw(batch: Batch) {
        if (!destroyed) {
            super.draw(batch)
        }
    }

    open fun update(dt: Float) {
        if (toDestroy && !destroyed) {
            world.destroyBody(body)
            destroyed = true
        }
    }

    fun destroy() {
        toDestroy = true
    }

    fun reverseVelocity(x: Boolean, y: Boolean) {
        if (x)
            velocity.x = - velocity.x
        if (y)
            velocity.y = -velocity.y
    }

}