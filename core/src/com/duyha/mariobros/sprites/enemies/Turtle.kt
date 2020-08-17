package com.duyha.mariobros.sprites.enemies

import com.badlogic.gdx.graphics.g2d.Animation
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

class Turtle(
        playScreen: PlayScreen,
        x: Float,
        y: Float
) : Enemy(playScreen, x, y) {

    var stateTime: Float = 0f
    lateinit var walkAnimation: Animation<TextureRegion>
    lateinit var frames: Array<TextureRegion>

    private var currentState: State
    private var previousState: State
    var shell: TextureRegion


    init {
        frames = Array()
        frames.add(TextureRegion(playScreen.textureAtlas.findRegion("turtle"), 0, 0, 16, 24))
        frames.add(TextureRegion(playScreen.textureAtlas.findRegion("turtle"), 16, 0, 16, 24))
        shell = TextureRegion(playScreen.textureAtlas.findRegion("turtle"), 64, 0, 16, 24)
        walkAnimation = Animation(0.2f, frames)
        currentState  = State.WALKING
        previousState = State.WALKING

        setBounds(getX(), getY(), 16 / MarioBros.PPM, 24 / MarioBros.PPM)
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

    override fun hitOnHead() {
        if (currentState != State.SHELL) {
            currentState = State.SHELL
            velocity.x = 0f;
        }
    }

    override fun update(dt: Float) {
        setRegion(getFrame(dt))
        if (currentState == State.SHELL && stateTime > 5) {
            currentState = State.WALKING
            velocity.x = 1f
        }

        setPosition(body.position.x - width/2, body.position.y - 8 / MarioBros.PPM)
        body.linearVelocity = velocity
    }

    private fun getFrame(dt: Float): TextureRegion {
        val region: TextureRegion = when (currentState) {
            State.SHELL -> {
                shell
            }
            State.WALKING -> {
                walkAnimation.getKeyFrame(stateTime, true)
            }
        }

        if (velocity.x > 0 && !region.isFlipX) {
            region.flip(true, false)
        }
        if (velocity.x < 0 && region.isFlipX) {
            region.flip(true, false)
        }
        stateTime = if (currentState == previousState) stateTime + dt else 0f

        previousState = currentState

        return region
    }

    enum class State {
        WALKING, SHELL
    }
}