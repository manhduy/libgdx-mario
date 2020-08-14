package com.duyha.mariobros.sprites

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.screens.PlayScreen
import kotlin.experimental.or

class Mario(
        screen: PlayScreen
) : Sprite() {

    lateinit var body: Body
    private var marioStand: TextureRegion
    var currentState = State.STANDING
    var previousState = State.STANDING
    lateinit var marioRun: Animation<TextureRegion>
    lateinit var marioJump: Animation<TextureRegion>

    private var stateTimer: Float = 0f
    private var runningRight: Boolean = true

    private val world = screen.world


    init {
        var frames = Array<TextureRegion>()
        for (i in 1..3) {
            frames.add(TextureRegion(screen.textureAtlas.findRegion("little_mario"), i * 16, 0, 16, 16))
        }
        marioRun = Animation<TextureRegion>(0.1f, frames)
        frames.clear()

        for (i in 4..5) {
            frames.add(TextureRegion(screen.textureAtlas.findRegion("little_mario"), i * 16, 0, 16, 16))
        }
        marioJump = Animation<TextureRegion>(0.1f, frames)

        defineMario()
        marioStand = TextureRegion(screen.textureAtlas.findRegion("little_mario"), 0, 0, 16, 16)
        setBounds(0f, 0f, 16 / MarioBros.PPM, 16 / MarioBros.PPM)
        setRegion(marioStand)
    }

    private fun defineMario() {
        val bodyDef = BodyDef()
        bodyDef.position.set(32f / MarioBros.PPM, 32f / MarioBros.PPM)
        bodyDef.type = BodyDef.BodyType.DynamicBody
        body = world.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        val circleShape = CircleShape()
        circleShape.radius = 5f / MarioBros.PPM
        fixtureDef.shape = circleShape
        fixtureDef.filter.categoryBits = MarioBros.MARIO_BIT
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT or  MarioBros.COIN_BIT or MarioBros.BRICK_BIT or
                MarioBros.ENEMY_BIT or MarioBros.OBJECT_BIT or MarioBros.ENEMY_HEAD_BIT

        body.createFixture(fixtureDef)

        val head = EdgeShape()
        head.set(Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM))
        fixtureDef.shape = head
        fixtureDef.isSensor = true

        body.createFixture(fixtureDef).userData = "head"

    }

    fun update(dt: Float) {
        setPosition(body.position.x - width/2f, body.position.y - height/2f)
        setRegion(getFrame(dt))
    }

    private fun getFrame(dt: Float): TextureRegion {
        currentState = getState()
        val region: TextureRegion = when (currentState) {
            State.JUMPING -> marioJump.getKeyFrame(stateTimer)
            State.RUNNING -> marioRun.getKeyFrame(stateTimer, true)
            State.FALLING -> marioStand
            State.STANDING -> marioStand
        }
        if ((body.linearVelocity.x < 0 || !runningRight) && !region.isFlipX ) {
            region.flip(true, false)
            runningRight = false
        } else if ((body.linearVelocity.x > 0 || runningRight) && region.isFlipX) {
            region.flip(true, false)
            runningRight = true
        }

        stateTimer = if (currentState == previousState) stateTimer + dt else 0f

        return region
    }

    private fun getState(): State {
        return when {
            body.linearVelocity.y > 0 || (body.linearVelocity.y < 0 && previousState == State.JUMPING) -> State.JUMPING
            body.linearVelocity.y < 0 -> State.FALLING
            body.linearVelocity.x != 0f -> State.RUNNING
            else -> State.STANDING
        }
    }
}