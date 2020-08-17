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
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.items.Item
import com.duyha.mariobros.items.ItemDef
import com.duyha.mariobros.items.Mushroom
import com.duyha.mariobros.scenes.GameOverScreen
import com.duyha.mariobros.scenes.Hud
import com.duyha.mariobros.sprites.Goomba
import com.duyha.mariobros.sprites.Mario
import com.duyha.mariobros.sprites.State
import com.duyha.mariobros.tools.B2WorldCreator
import com.duyha.mariobros.tools.WorldContactListener
import java.util.*
import java.util.concurrent.LinkedBlockingDeque

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
    private lateinit var creator: B2WorldCreator

    private var player: Mario

    val textureAtlas = TextureAtlas("Mario_and_Enemies.pack")

    var music: Music

    lateinit var items: Array<Item>
    lateinit var itemToSpawn: LinkedBlockingDeque<ItemDef>

    init {
        map = mapLoader.load("level1.tmx")
        mapRenderer = OrthogonalTiledMapRenderer(map,  1f/ MarioBros.PPM)
        gameCamera.position.set(gameViewPort.worldWidth/2f, gameViewPort.worldHeight/2f, 0f)

        world = World(Vector2(0f, -10f), true)
        box2DRenderer = Box2DDebugRenderer()

        creator = B2WorldCreator(this)

        player = Mario(this)

        world.setContactListener(WorldContactListener())

        music = MarioBros.manager.get("audio/music/mario_music.ogg")
        music.isLooping = true
        //music.play()

        items = Array()
        itemToSpawn = LinkedBlockingDeque()
    }

    fun spawnItem(itemDef: ItemDef) {
        itemToSpawn.add(itemDef)
    }

    fun handleSpawningItems() {
        if (!itemToSpawn.isEmpty()) {
            val idef = itemToSpawn.poll()
            if (idef.type == Mushroom::class.java) {
                items.add(Mushroom(this, idef.position.x, idef.position.y))
            }
        }
    }

    private fun update(dt: Float) {
        //handle user input first
        handleInput(dt)
        handleSpawningItems()

        world.step(1/60f, 6, 2)

        player.update(dt)
        for (enemy in creator.goombas) {
            enemy.update(dt)
            if (enemy.x < player.x + 224 / MarioBros.PPM) {
                enemy.body.isActive = true
            }
        }

        for (item in  items) {
            item.update(dt)
        }

        hud.update(dt)

        if (player.currentState != State.DEAD) {
            gameCamera.position.x = player.body.position.x
        }

        gameCamera.update()
        mapRenderer.setView(gameCamera)
    }

    private fun handleInput(dt: Float) {
        if (player.currentState != State.DEAD) {
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
        for (enemy in creator.goombas) {
            enemy.draw(game.batch)
        }
        for (item in items) {
            item.draw(game.batch)
        }
        game.batch.end()

        //Set our batch to now draw what the Hud camera sees
        game.batch.projectionMatrix = hud.stage.camera.combined
        hud.stage.draw()

        if (gameOver()) {
            Gdx.app.log("Mario", "Game over")
            game.screen = GameOverScreen(game)
            dispose()
        }
    }

    fun gameOver(): Boolean {
        return (player.currentState == State.DEAD && player.getStateTimer() > 3f)
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