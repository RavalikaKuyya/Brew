package io.github.koss.brew.ui.you.profile.model

import io.github.koss.brew.data.model.Drink

sealed class ProfileActivity

data class DrinkLog(
        val drink: Drink
)