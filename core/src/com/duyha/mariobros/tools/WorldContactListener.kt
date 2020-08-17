package com.duyha.mariobros.tools

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.duyha.mariobros.MarioBros
import com.duyha.mariobros.items.Item
import com.duyha.mariobros.sprites.Enemy
import com.duyha.mariobros.sprites.InteractiveTileObject
import com.duyha.mariobros.sprites.Mario
import kotlin.experimental.or

class WorldContactListener : ContactListener {

    override fun beginContact(contact: Contact) {
        val fixA = contact.fixtureA
        val fixB = contact.fixtureB

        val cDef = fixA.filterData.categoryBits or fixB.filterData.categoryBits

        when (cDef) {
            MarioBros.MARIO_HEAD_BIT or MarioBros.BRICK_BIT,
            MarioBros.MARIO_HEAD_BIT or MarioBros.COIN_BIT -> {
                if (fixA.filterData.categoryBits == MarioBros.MARIO_HEAD_BIT) {
                    (fixB.userData as InteractiveTileObject).onHeadHit(fixA.userData as Mario)
                } else {
                    (fixA.userData as InteractiveTileObject).onHeadHit(fixB.userData as Mario)
                }
            }
            MarioBros.ENEMY_HEAD_BIT or MarioBros.MARIO_BIT -> {
                if (fixA.filterData.categoryBits == MarioBros.ENEMY_HEAD_BIT) {
                    (fixA.userData as Enemy).hitOnHead()
                } else {
                    (fixB.userData as Enemy).hitOnHead()
                }
            }
            MarioBros.ENEMY_BIT or MarioBros.OBJECT_BIT -> {
                if (fixA.filterData.categoryBits == MarioBros.ENEMY_BIT) {
                    (fixA.userData as Enemy).reverseVelocity(x = true, y = false)
                } else {
                    (fixB.userData as Enemy).reverseVelocity(x = true, y = false)
                }
            }
            MarioBros.MARIO_BIT or MarioBros.ENEMY_BIT -> {
                if (fixA.filterData.categoryBits == MarioBros.MARIO_BIT) {
                    (fixA.userData as Mario).hit()
                } else {
                    (fixB.userData as Mario).hit()
                }
            }
            MarioBros.ENEMY_BIT or MarioBros.ENEMY_BIT -> {
                (fixA.userData as Enemy).reverseVelocity(x = true, y = false)
                (fixB.userData as Enemy).reverseVelocity(x = true, y = false)
            }
            MarioBros.ITEM_BIT or MarioBros.OBJECT_BIT -> {
                if (fixA.filterData.categoryBits == MarioBros.ITEM_BIT) {
                    (fixA.userData as Item).reverseVelocity(x = true, y = false)
                } else {
                    (fixB.userData as Item).reverseVelocity(x = true, y = false)
                }
            }
            MarioBros.ITEM_BIT or MarioBros.MARIO_BIT -> {
                if (fixA.filterData.categoryBits == MarioBros.ITEM_BIT) {
                    (fixA.userData as Item).use(fixB.userData as Mario)
                } else {
                    (fixB.userData as Item).use(fixA.userData as Mario)
                }
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
