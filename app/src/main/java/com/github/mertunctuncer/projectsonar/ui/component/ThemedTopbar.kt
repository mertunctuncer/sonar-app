package com.github.mertunctuncer.projectsonar.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector


@ExperimentalMaterial3Api
@Composable

fun ThemedTopBar(
    modifier: Modifier,
    title: @Composable () -> Unit,
    navIcon: ImageVector,
    navContent: String,
    onNavClick: () -> Unit,
    actions: @Composable() (RowScope.() -> Unit) = {},
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onNavClick) {
                Icon(
                    imageVector = navIcon,
                    contentDescription = navContent
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}