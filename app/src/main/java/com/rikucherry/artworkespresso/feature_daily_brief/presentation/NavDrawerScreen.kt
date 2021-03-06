package com.rikucherry.artworkespresso.feature_daily_brief.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.rikucherry.artworkespresso.R
import com.rikucherry.artworkespresso.common.Constants
import com.rikucherry.artworkespresso.common.component.HeadingLevel
import com.rikucherry.artworkespresso.common.component.HeadingText
import com.rikucherry.artworkespresso.common.theme.*
import com.rikucherry.artworkespresso.common.tool.DataFormatHelper
import com.rikucherry.artworkespresso.common.tool.DataFormatHelper.weeklyDates
import com.rikucherry.artworkespresso.feature_daily_brief.presentation.viewmodel.DailyBriefViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavDrawerScreen(
    isFreeTrail: Boolean,
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: DailyBriefViewModel?
    ) {

    val userName: String
    val iconUrl: String

    val defaultWeekday = DataFormatHelper.getWeekdayOfToday()
    // The drawer activate the weekday of current date by default
    val indexOfToday = weeklyDates.indexOf(defaultWeekday)
    val selectedIdx = remember { mutableStateOf(indexOfToday)}

    if (isFreeTrail) {
        userName = "Client"
        iconUrl = Constants.DEFAULT_AVATAR_URL
    } else {
        userName = viewModel?.loginInfoState?.value?.data?.userName ?: "Not Available"
        iconUrl = viewModel?.loginInfoState?.value?.data?.userIconUrl ?: Constants.DEFAULT_AVATAR_URL
    }

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(0.7f),
                verticalAlignment = Alignment.Bottom
            ) {
                Image(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            //TODO: Move to user screen
                        },
                    painter = rememberImagePainter(
                        data = iconUrl,
                        builder = {
                            transformations(CircleCropTransformation())
                            placeholder(R.drawable.placeholder_person_foreground)
                        }
                    ),
                    contentScale = ContentScale.Fit,
                    contentDescription = "Author Icon",
                )
            }

            Row (
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.Top){
                HeadingText(
                    text = userName,
                    color = GrayParagraph,
                    headingLevel = HeadingLevel.THIRD,
                    paddingTop = 8.dp
                )
            }
        }

        // The list of weekdays
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                for (i in weeklyDates.indices) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BackgroundPrimary, RectangleShape),
                        colors = ButtonDefaults.buttonColors(
                            // Highlight the selected item background
                            backgroundColor = if (i == selectedIdx.value) {
                                GrayMedium
                            } else {
                                GrayDark
                            }
                        ),
                        enabled = i <= indexOfToday, // Cannot check future dates
                        onClick = {
                            selectedIdx.value = i
                            val weekday = weeklyDates[i]
                            viewModel?.selectedWeekday = weekday
                            viewModel?.getArtworks()
                            closeNavDrawer(drawerState, scope)
                        }
                    ) {
                        HeadingText(
                            text = weeklyDates[i],
                            // Highlighted the selected item label
                            color =
                            when {
                                i > indexOfToday -> {
                                    GrayDark
                                }
                                i == selectedIdx.value -> {
                                    Teal200
                                }
                                else -> {
                                    GrayParagraph
                                }
                            },
                            headingLevel = HeadingLevel.SECONDARY,
                            paddingTop = 4.dp,
                            paddingBottom = 4.dp
                        )
                    }
                }
            }
        }
    }
}


fun openNavDrawer(drawerState: DrawerState, scope: CoroutineScope) {
    scope.launch {
        if (drawerState.isClosed) {
            drawerState.open()
        }
    }
}

fun closeNavDrawer(drawerState: DrawerState, scope: CoroutineScope) {
    scope.launch {
        if (drawerState.isOpen) {
            drawerState.close()
        }
    }
}

@Preview
@Composable
fun Preview(){
    NavDrawerScreen(false
        , DrawerState(DrawerValue.Open)
        , rememberCoroutineScope(),
    null)
}