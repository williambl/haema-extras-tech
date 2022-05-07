package com.williambl.haema.extras.tech

import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.slf4j.LoggerFactory

object HaemaExtrasTech: ModInitializer {
    val LOGGER = LoggerFactory.getLogger(HaemaExtrasTech::class.java)

    override fun onInitialize(mod: ModContainer) {
        LOGGER.info("Hi there!")
    }
}
