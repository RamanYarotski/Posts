package com.homeassignment.posts.presentation.uiutils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions

fun NavController.navigateSafe(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
) {
    currentDestination?.getAction(resId)?.let {
        navigate(resId, args, navOptions)
    }
}
