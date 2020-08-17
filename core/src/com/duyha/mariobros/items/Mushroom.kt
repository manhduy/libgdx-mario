package com.duyha.mariobros.items

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.screens.PlayScreen
import com.duyha.mariobros.sprites.Mario
import kotlin.experimental.or

class Mushroom(
        screen: PlayScreen,
        x: Float,
        y: Float
) : Item(screen, x, y) {

    init {
        setRegion(screen.textureAtlas.findRegion("mushroom"), 0, 0, 16, 16)
        velocity = Vector2(0.7f, 0f)
    }

    override fun defineItem() {
        val bodyDef = BodyDef()
        bodyDef.position.set(x, y)
        bodyDef.type = BodyDef.BodyType.DynamicBody
        body = world.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        val circleShape = CircleShape()
        circleShape.radius = 6f / MarioBros.PPM
        fixtureDef.filter.categoryBits = MarioBros.ITEM_BIT
        fixtureDef.filter.maskBits = MarioBros.MARIO_BIT or MarioBros.OBJECT_BIT or MarioBros.GROUND_BIT or
                MarioBros.COIN_BIT or MarioBros.BRICK_BIT

        fixtureDef.shape = circleShape

        body.createFixture(fixtureDef).userData = this
    }

    override fun use(mario: Mario) {
        destroy()
        mario.grow()
    }

    override fun update(dt: Float) {
        super.update(dt)
        setPosition(body.position.x - width/2, body.position.y - height/2)
        velocity.y = body.linearVelocity.y
        body.linearVelocity = velocity
    }
}