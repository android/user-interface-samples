package com.example.jetnews.glance.ui.layout

import androidx.compose.runtime.Composable
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import com.example.jetnews.R
import com.example.jetnews.ui.MainActivity


/**
 * Content to be displayed when there are no items in the list. To be displayed below the
 * app-specific title bar in the [androidx.glance.appwidget.components.Scaffold] .
 */
@Composable
internal fun EmptyListContent() {
  val context = LocalContext.current

  NoDataContent(
    noDataText = context.getString(R.string.no_data),
    noDataIconRes = R.drawable.ic_jetnews_logo,
    actionButtonText = context.getString(R.string.fetch_news),
    actionButtonIcon = R.drawable.ic_refresh,
    actionButtonOnClick = actionStartActivity<MainActivity>()
  )
}