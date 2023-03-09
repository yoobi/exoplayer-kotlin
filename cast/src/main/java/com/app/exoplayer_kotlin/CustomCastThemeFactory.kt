package com.app.exoplayer_kotlin

import android.content.Context
import android.os.Bundle
import androidx.mediarouter.app.*


class CustomCastProvider(context: Context) : MediaRouteActionProvider(context) {
    init {
        dialogFactory = CustomCastThemeFactory()
    }
}

class CustomCastThemeFactory : MediaRouteDialogFactory() {
    override fun onCreateChooserDialogFragment(): MediaRouteChooserDialogFragment {
        return CustomMediaRouterChooserDialogFragment()
    }

    override fun onCreateControllerDialogFragment(): MediaRouteControllerDialogFragment {
        return CustomMediaRouteControllerDialogFragment()
    }
}

class CustomMediaRouterChooserDialogFragment: MediaRouteChooserDialogFragment() {
    override fun onCreateChooserDialog(
        context: Context,
        savedInstanceState: Bundle?
    ): MediaRouteChooserDialog =
        MediaRouteChooserDialog(context)
}

class CustomMediaRouteControllerDialogFragment: MediaRouteControllerDialogFragment() {
    override fun onCreateControllerDialog(
        context: Context,
        savedInstanceState: Bundle?
    ): MediaRouteControllerDialog =
//        MediaRouteControllerDialog(context)
    MediaRouteControllerDialog(context, R.style.CustomControllerTheme)
}
