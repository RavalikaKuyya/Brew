package io.github.koss.brew.ui.create.drink.simple

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.koss.brew.R

class SimpleAddDrinkBottomSheetDialogFragment: BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_simple_add_drink, container, false)

}