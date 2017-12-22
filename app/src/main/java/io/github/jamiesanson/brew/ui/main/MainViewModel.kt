package io.github.jamiesanson.brew.ui.main

import android.arch.lifecycle.ViewModel
import io.github.jamiesanson.brew.ui.main.navigator.Screens
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class MainViewModel @Inject constructor(): ViewModel() {
    private lateinit var router: Router

    fun init(router: Router) {
        this.router = router
        router.replaceScreen(Screens.MAIN_SCREEN)
    }


}