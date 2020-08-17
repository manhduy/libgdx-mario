package com.duyha.mariobros.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.viewport.FitViewport
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.scenes.Hud
import com.duyha.mariobros.sprites.Goomba
import com.duyha.mariobros.sprites.Mario
import com.duyha.mariobros.tools.B2WorldCreator
import com.duyha.mariobros.tools.WorldContactListener

class PlayScreen(private val game: MarioBros) : Screen {
    private var texture: Texture = Texture("badlogic.jpg")
    private val gameCamera = OrthographicCamera()
    private val gameViewPort = FitViewport(MarioBros.V_WIDTH / MarioBros.PPM,
            MarioBros.V_HEIGHT / MarioBros.PPM, gameCamera)

    private val hud = Hud(game.batch)

    private var mapLoader: TmxMapLoader = TmxMapLoader()
    var map: TiledMap
    private var mapRenderer: OrthogonalTiledMapRenderer

    var world: World
    private var box2DRenderer: Box2DDebugRenderer

    private var player: Mario

    private lateinit var goomba: Goomba

    val textureAtlas = TextureAtlas("Mario_and_Enemies.pack")

    lateinit var music: Music

    init {
        map = mapLoader.load("level1.tmx")
        mapRenderer = OrthogonalTiledMapRenderer(map,  1f/ MarioBros.PPM)
        gameCamera.position.set(gameViewPort.worldWidth/2f, gameViewPort.worldHeight/2f, 0f)

        world = World(Vector2(0f, -10f), true)
        box2DRenderer = Box2DDebugRenderer()

        B2WorldCreator(this)

        player = Mario(this)

        world.setContactListener(WorldContactListener())

        music = MarioBros.manager.get("audio/music/mario_music.ogg")
        music.isLooping = true
        //music.play()

        goomba = Goomba(this, 5.64f, .16f)
    }

    private fun update(dt: Float) {
        //handle user input first
        handleInput(dt)

        world.step(1/60f, 6, 2)

        player.update(dt)

        goomba.update(dt)

        hud.update(dt)

        gameCamera.position.x = player.body.position.x

        gameCamera.update()
        mapRenderer.setView(gameCamera)
    }

    private fun handleInput(dt: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            player.body.applyLinearImpulse(Vector2(0f, 4f), player.body.worldCenter, true)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.body.linearVelocity.x <= 2) {
            player.body.applyLinearImpulse(Vector2(0.1f, 0f), player.body.worldCenter, true)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.body.linearVelocity.x <= 2) {
            player.body.applyLinearImpulse(Vector2(-0.1f, 0f), player.body.worldCenter, true)
        }
    }

    override fun show() {}

    override fun render(delta: Float) {
        //separate our update logic from render
        update(delta)

        //Clear the game screen with black
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        //Render our game map
        mapRenderer.render()

        //Renderer our Bod2DDebugLines
        box2DRenderer.render(world, gameCamera.combined)

        game.batch.projectionMatrix = gameCamera.combined
        game.batch.begin()
        player.draw(game.batch)
        goomba.draw(game.batch)
        game.batch.end()

        //Set our batch to now draw what the Hud camera sees
        game.batch.projectionMatrix = hud.stage.camera.combined
        hud.stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        gameViewPort.update(width, height)
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        map.dispose()
        mapRenderer.dispose()
        world.dispose()
        box2DRenderer.dispose()
        hud.dispose()
    }

}