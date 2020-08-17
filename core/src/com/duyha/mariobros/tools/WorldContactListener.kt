package com.duyha.mariobros.tools

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.sprites.Enemy
import com.duyha.mariobros.sprites.InteractiveTileObject
import kotlin.experimental.or

class WorldContactListener : ContactListener {

    override fun beginContact(contact: Contact) {
        val fixA = contact.fixtureA
        val fixB = contact.fixtureB

        val cDef = fixA.filterData.categoryBits or fixB.filterData.categoryBits

        if (fixA.userData == "head" || fixB.userData == "head") {
            val head = if (fixA.userData == "head") fixA else fixB
            val fixObj = if (head == fixA) fixB else fixA

            if (fixObj.userData is InteractiveTileObject) {
                (fixObj.userData as InteractiveTileObject).onHeadHit()
            }
        }

        when (cDef) {
            MarioBros.ENEMY_HEAD_BIT or MarioBros.MARIO_BIT -> {
                if (fixA.filterData.categoryBits == MarioBros.ENEMY_HEAD_BIT) {
                    (fixA.userData as Enemy).hitOnHead()
                } else {
                    (fixB.userData as Enemy).hitOnHead()
                }
            }
            MarioBros.ENEMY_HEAD_BIT or MarioBros.OBJECT_BIT -> {
                if (fixA.filterData.categoryBits == MarioBros.ENEMY_BIT) {
                    (fixA.userData as Enemy).reverseVelocity(x = true, y = false)
                } else {
                    (fixB.userData as Enemy).reverseVelocity(x = true, y = false)
                }
            }
            MarioBros.MARIO_BIT or MarioBros.ENEMY_BIT -> {
                Gdx.app.log("MARIO", "Mario died")
            }
        }

    }

    override fun endContact(contact: Contact) {

    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
        //Gdx.app.log("WorldContactListener", "preSolve")
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
        //Gdx.app.log("WorldContactListener", "postSolve")
    }

}
