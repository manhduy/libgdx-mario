package com.duyha.mariobros.sprites

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.utils.Array
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.screens.PlayScreen
import kotlin.experimental.or

class Goomba(
        playScreen: PlayScreen,
        x: Float,
        y: Float
) : Enemy(playScreen, x, y) {

    var stateTime: Float = 0f
    lateinit var walkAnimation: Animation<TextureRegion>
    lateinit var frames: Array<TextureRegion>

    init {
        frames = Array<TextureRegion>()
        for (i in 0..2) {
            frames.add(TextureRegion(playScreen.textureAtlas.findRegion("goomba"), i * 16, 0, 16, 16))
        }
        walkAnimation = Animation(0.4f, frames)
        stateTime = 0f
        setBounds(x, y, 16f / MarioBros.PPM, 16f / MarioBros.PPM)

    }

    override fun update(dt: Float) {
        stateTime += dt
        if (setToDestroy && !destroyed) {
            world.destroyBody(body)
            destroyed = true
            setRegion(TextureRegion(playScreen.textureAtlas.findRegion("goomba"), 32, 0, 16, 16))
        } else if (!destroyed) {
            body.linearVelocity = velocity
            setPosition(body.position.x - width/2f, body.position.y - height/2f)
            setRegion(walkAnimation.getKeyFrame(stateTime, true))
        }

        setPosition(body.position.x - width/2f, body.position.y - height/2f)
        setRegion(walkAnimation.getKeyFrame(stateTime, true))
    }

    override fun defineEnemy() {
        val bodyDef = BodyDef()
        bodyDef.position.set(x, y)
        bodyDef.type = BodyDef.BodyType.DynamicBody
        body = world.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        val circleShape = CircleShape()
        circleShape.radius = 6f / MarioBros.PPM
        fixtureDef.shape = circleShape
        fixtureDef.filter.categoryBits = MarioBros.ENEMY_BIT
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT or  MarioBros.COIN_BIT or MarioBros.BRICK_BIT or
                MarioBros.ENEMY_BIT or MarioBros.OBJECT_BIT or MarioBros.MARIO_BIT

        body.createFixture(fixtureDef).userData = this

        //Create the head
        val head = PolygonShape()
        val vertices = arrayOf(
                Vector2(-5f, 8f).scl(1 / MarioBros.PPM),
                Vector2(5f, 8f).scl(1 / MarioBros.PPM),
                Vector2(-3f, 3f).scl(1 / MarioBros.PPM),
                Vector2(3f, 3f).scl(1 / MarioBros.PPM)
        )
        head.set(vertices)

        fixtureDef.shape = head
        fixtureDef.restitution = 0.5f
        fixtureDef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT
        body.createFixture(fixtureDef).userData = this

    }

    override fun draw(batch: Batch) {
        if (!destroyed || stateTime < 1) {
            super.draw(batch)
        }
    }

    override fun hitOnHead() {
        setToDestroy = true
        MarioBros.manager.get("audio/sounds/stomp.wav", Sound::class.java).play()
    }
}