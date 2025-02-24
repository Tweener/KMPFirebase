package com.tweener.firebase

class JsPlatform : Platform {
    override val name: String = "Js"
}

actual fun getPlatform(): Platform = JsPlatform()
