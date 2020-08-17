package com.duyha.mariobros.scenes

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.screens.PlayScreen

class GameOverScreen(
        private val game: Game
) : Screen {

    private val viewport = FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, OrthographicCamera())
    private val stage = Stage(viewport, (game as MarioBros).batch)

    init {
        val font = Label.LabelStyle(BitmapFont(), Color.WHITE)
        val table = Table()
        table.center()
        table.setFillParent(true)

        val gameOverLabel = Label("GAME OVER", font)
        val playAgainLabel = Label("Click to Play Again", font)

        table.add(gameOverLabel).expandX()
        table.row()
        table.add(playAgainLabel).expandX().padTop(10f)

        stage.addActor(table)

    }

    override fun hide() {
    }

    override fun show() {
    }

    override fun render(delta: Float) {
        if (Gdx.input.justTouched()) {
            game.screen = PlayScreen(game as MarioBros)
            dispose()
        }
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.draw()
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun dispose() {
        stage.dispose()
    }

}