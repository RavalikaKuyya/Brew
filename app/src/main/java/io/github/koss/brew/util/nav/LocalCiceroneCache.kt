package io.github.koss.brew.util.nav

import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

/**
 * Cicerone cache for components using localised routing, like sub-fragments of the MainFragment.
 */
class LocalCiceroneCache {
    private val containers: HashMap<String, Cicerone<Router>> = HashMap()

    fun getCicerone(containerTag: String): Cicerone<Router> {
        if (!containers.containsKey(containerTag)) {
            containers.put(containerTag, Cicerone.create())
        }
        return containers[containerTag]!!
    }
}