package com.duyha.mariobros.scenes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.duyha.mariobros.MarioBros

class Hud(
        private val sb: SpriteBatch
) : Disposable {

    companion object {
        var score: Int = 0
        var scoreLabel = Label(String.format("%06d", score), Label.LabelStyle(BitmapFont(), Color.WHITE))

        fun addCore(value: Int) {
            score += value
            scoreLabel.setText(String.format("%06d", score))
        }

    }

    private var viewport =  FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, OrthographicCamera())
    private var worldTimer: Int = 300
    private var timeCount: Float = 0f

    var countDownLabel: Label
    var timeLabel: Label
    var levelLabel: Label
    var worldLabel: Label
    var marioLabel: Label
    var stage: Stage = Stage(viewport, sb)

    init {
        val table = Table()
        table.top()
        table.setFillParent(true)

        countDownLabel = Label(String.format("%03d", worldTimer), Label.LabelStyle(BitmapFont(), Color.WHITE))
        timeLabel = Label("TIME", Label.LabelStyle(BitmapFont(), Color.WHITE))
        levelLabel = Label("1-1", Label.LabelStyle(BitmapFont(), Color.WHITE))
        worldLabel = Label("WORLD", Label.LabelStyle(BitmapFont(), Color.WHITE))
        marioLabel = Label("MARIO", Label.LabelStyle(BitmapFont(), Color.WHITE))

        table.add(marioLabel).expandX().padTop(10f)
        table.add(worldLabel).expandX().padTop(10f)
        table.add(timeLabel).expandX().padTop(10f)
        table.row()
        table.add(scoreLabel).expandX()
        table.add(levelLabel).expandX()
        table.add(countDownLabel).expandX()

        stage.addActor(table)
    }

    fun update(dt: Float) {
        timeCount += dt
        if (timeCount >= 1) {
            worldTimer --
            countDownLabel.setText(String.format("%03d", worldTimer))
            timeCount = 0f
        }
    }

    override fun dispose() {
        stage.dispose()
    }

}