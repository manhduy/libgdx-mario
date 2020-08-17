package com.duyha.mariobros.sprites

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.screens.PlayScreen
import javax.management.StandardEmitterMBean
import kotlin.experimental.or

class Mario(
        screen: PlayScreen
) : Sprite() {

    lateinit var body: Body
    var currentState = State.STANDING
    var previousState = State.STANDING

    private var marioStand: TextureRegion
    lateinit var marioRun: Animation<TextureRegion>
    lateinit var marioJump: TextureRegion
    private val bigMarioStand: TextureRegion
    private val bigMarioJump: TextureRegion
    private var marioDead: TextureRegion
    private val bigMarioRun: Animation<TextureRegion>
    private val growMario: Animation<TextureRegion>

    private var stateTimer: Float = 0f
    private var runningRight: Boolean = true
    private var marioIsBig = false
    private var marioIsDead = false
    private var runGrowAnimation = false
    private var timeToDefineBigMario = false
    private var timeToRedefineMario = false


    private val world = screen.world

    init {
        var frames = Array<TextureRegion>()
        for (i in 1..3) {
            frames.add(TextureRegion(screen.textureAtlas.findRegion("little_mario"), i * 16, 0, 16, 16))
        }
        marioRun = Animation<TextureRegion>(0.1f, frames)

        frames.clear()

        for (i in 1..3) {
            frames.add(TextureRegion(screen.textureAtlas.findRegion("big_mario"), i * 16, 0, 16, 32))
        }
        bigMarioRun = Animation<TextureRegion>(0.1f, frames)

        frames.clear()

        frames.add((TextureRegion(screen.textureAtlas.findRegion("big_mario"), 240, 0, 16, 32)))
        frames.add((TextureRegion(screen.textureAtlas.findRegion("big_mario"), 0, 0, 16, 32)))
        frames.add((TextureRegion(screen.textureAtlas.findRegion("big_mario"), 240, 0, 16, 32)))
        frames.add((TextureRegion(screen.textureAtlas.findRegion("big_mario"), 0, 0, 16, 32)))
        growMario = Animation(0.2f, frames)

        marioJump = TextureRegion(screen.textureAtlas.findRegion("little_mario"), 80, 0, 16, 16)
        bigMarioJump = TextureRegion(screen.textureAtlas.findRegion("big_mario"), 80, 0, 16, 32)

        //Define texture region for mario standing
        marioStand = TextureRegion(screen.textureAtlas.findRegion("little_mario"), 0, 0, 16, 16)
        bigMarioStand = TextureRegion(screen.textureAtlas.findRegion("big_mario"), 0, 0, 16, 32)

        //Create mario dead texture region
        marioDead = TextureRegion(screen.textureAtlas.findRegion("little_mario"), 96, 0, 16, 16)

        defineMario()
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
        circleShape.radius = 6f / MarioBros.PPM
        fixtureDef.shape = circleShape
        fixtureDef.filter.categoryBits = MarioBros.MARIO_BIT
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT or  MarioBros.COIN_BIT or MarioBros.BRICK_BIT or
                MarioBros.ENEMY_BIT or MarioBros.OBJECT_BIT or MarioBros.ENEMY_HEAD_BIT or MarioBros.ITEM_BIT

        body.createFixture(fixtureDef).userData = this

        val head = EdgeShape()
        head.set(Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM))
        fixtureDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT
        fixtureDef.shape = head
        fixtureDef.isSensor = true

        body.createFixture(fixtureDef).userData = this

    }

    private fun defineBigMario() {
        val currentPosition = body.position
        world.destroyBody(body)

        val bodyDef = BodyDef()
        bodyDef.position.set(currentPosition.add(0f, 10 / MarioBros.PPM))
        bodyDef.type = BodyDef.BodyType.DynamicBody
        body = world.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        val circleShape = CircleShape()
        circleShape.radius = 5f / MarioBros.PPM
        fixtureDef.shape = circleShape
        fixtureDef.filter.categoryBits = MarioBros.MARIO_BIT
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT or  MarioBros.COIN_BIT or MarioBros.BRICK_BIT or
                MarioBros.ENEMY_BIT or MarioBros.OBJECT_BIT or MarioBros.ENEMY_HEAD_BIT or MarioBros.ITEM_BIT

        body.createFixture(fixtureDef).userData = this
        circleShape.position = Vector2(0f, -14 / MarioBros.PPM)
        body.createFixture(fixtureDef).userData = this

        val head = EdgeShape()
        head.set(Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM))
        fixtureDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT
        fixtureDef.shape = head
        fixtureDef.isSensor = true

        body.createFixture(fixtureDef).userData = this
        timeToDefineBigMario = false
    }

    fun redefineMario() {
        val position = body.position
        world.destroyBody(body)

        val bodyDef = BodyDef()
        bodyDef.position.set(position)
        bodyDef.type = BodyDef.BodyType.DynamicBody
        body = world.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        val circleShape = CircleShape()
        circleShape.radius = 6f / MarioBros.PPM
        fixtureDef.shape = circleShape
        fixtureDef.filter.categoryBits = MarioBros.MARIO_BIT
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT or  MarioBros.COIN_BIT or MarioBros.BRICK_BIT or
                MarioBros.ENEMY_BIT or MarioBros.OBJECT_BIT or MarioBros.ENEMY_HEAD_BIT or MarioBros.ITEM_BIT

        body.createFixture(fixtureDef).userData = this

        val head = EdgeShape()
        head.set(Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM))
        fixtureDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT
        fixtureDef.shape = head
        fixtureDef.isSensor = true

        body.createFixture(fixtureDef).userData = this

        timeToRedefineMario = false
    }

    fun update(dt: Float) {
        if (marioIsBig) {
            setPosition(body.position.x - width/2f, body.position.y - height/2f - 6/MarioBros.PPM)
        } else {
            setPosition(body.position.x - width/2f, body.position.y - height/2f)
        }
        setRegion(getFrame(dt))
        if (timeToDefineBigMario) {
            defineBigMario()
        }
        if (timeToRedefineMario) {
            redefineMario()
        }
    }

    private fun getFrame(dt: Float): TextureRegion {
        currentState = getState()
        val region: TextureRegion = when (currentState) {
            State.DEAD -> marioDead
            State.GROWING -> {
                growMario.getKeyFrame(stateTimer).also {
                    if (growMario.isAnimationFinished(stateTimer)) {
                        runGrowAnimation = false
                    }
                }

            }
            State.JUMPING -> if (marioIsBig) bigMarioJump else marioJump
            State.RUNNING -> if (marioIsBig) bigMarioRun.getKeyFrame(stateTimer, true) else
                marioRun.getKeyFrame(stateTimer, true)
            State.FALLING -> marioStand
            State.STANDING -> if (marioIsBig) bigMarioStand else marioStand
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
            marioIsDead -> State.DEAD
            runGrowAnimation -> State.GROWING
            body.linearVelocity.y > 0 || (body.linearVelocity.y < 0 && previousState == State.JUMPING) -> State.JUMPING
            body.linearVelocity.y < 0 -> State.FALLING
            body.linearVelocity.x != 0f -> State.RUNNING
            else -> State.STANDING
        }
    }

    fun grow() {
        runGrowAnimation = true
        marioIsBig = true
        timeToDefineBigMario = true
        setBounds(x, y, width, height*2)
        MarioBros.manager.get("audio/sounds/powerup.wav", Sound::class.java).play()
    }

    fun isBig() = marioIsBig

    fun hit() {
        if (marioIsBig) {
            marioIsBig = false
            timeToRedefineMario = true
            setBounds(x, y, width, height / 2)
            MarioBros.manager.get("audio/sounds/powerdown.wav", Sound::class.java).play()
        } else {
            MarioBros.manager.get("audio/music/mario_music.ogg", Music::class.java).stop()
            MarioBros.manager.get("audio/sounds/mariodie.wav", Sound::class.java).play()
            marioIsDead = true
            val filter = Filter()
            filter.maskBits = MarioBros.NOTHING_BIT
            for (fixture in body.fixtureList) {
                fixture.filterData = filter
            }
            body.applyLinearImpulse(Vector2(0f, 4f), body.worldCenter, true)

        }
    }
}