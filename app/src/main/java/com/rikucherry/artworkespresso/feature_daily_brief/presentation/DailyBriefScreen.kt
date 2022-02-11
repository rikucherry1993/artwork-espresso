package com.rikucherry.artworkespresso.feature_daily_brief.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.insets.LocalWindowInsets
import com.rikucherry.artworkespresso.R
import com.rikucherry.artworkespresso.common.Constants
import com.rikucherry.artworkespresso.common.component.HeadingLevel
import com.rikucherry.artworkespresso.common.component.HeadingText
import com.rikucherry.artworkespresso.common.component.ShadowedImage
import com.rikucherry.artworkespresso.common.component.drawColoredShadow
import com.rikucherry.artworkespresso.common.data.remote.DeviationDto
import com.rikucherry.artworkespresso.common.theme.BackgroundPrimary
import com.rikucherry.artworkespresso.common.theme.GrayParagraph
import com.rikucherry.artworkespresso.common.theme.Purple200
import com.rikucherry.artworkespresso.common.theme.Teal200
import com.rikucherry.artworkespresso.feature_daily_brief.presentation.viewmodel.DailyBriefViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

//Collapsable AppToolBar
val CollapsedAppBarHeight = 48.dp
val ExpandedAppBarHeight = 400.dp

@Composable
fun DailyBriefScreen(
    isFreeTrail: Boolean,
    viewModel: DailyBriefViewModel = hiltViewModel()
) {
    val listState = viewModel.listState.value
    val scrollState = rememberLazyListState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val artworks = listState.data

    ModalDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            NavDrawerScreen(isFreeTrail)
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                // list of artwork items displayed under the collapsable toolbar view
                DailyArtWorkList(scrollState, isFreeTrail, artworks)
                // Collapsable toolbar contains roughly a header image and a tool bar, both can
                // adjust the position of their elements or themselves dynamically during scrolling.
                CollapsableToolBar(scrollState)

                // Press menu button to open the navigation drawer
                IconButton(
                    modifier = Modifier.size(CollapsedAppBarHeight, CollapsedAppBarHeight),
                    onClick = { openNavDrawer(drawerState, scope) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu button",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp, 4.dp)
                    )
                }
            }
        }
    )
}


@Composable
fun CollapsableToolBar(scrollState: LazyListState) {
    val imageHeight = ExpandedAppBarHeight - CollapsedAppBarHeight
    val maxOffset = with(LocalDensity.current) {
        imageHeight.roundToPx()
    } - LocalWindowInsets.current.systemBars.top

    val offset = scrollState.firstVisibleItemScrollOffset.coerceAtMost(maxOffset)
    val offsetProgress = max(0f, offset * 2f - maxOffset * 1f) / maxOffset

    TopAppBar(
        modifier = Modifier
            .height(ExpandedAppBarHeight)
            .offset { IntOffset(x = 0, y = -offset) },
        backgroundColor = Color.Transparent,
        contentColor = GrayParagraph,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .background(Color.Transparent)
            ) {
                // Top image
                ShadowedImage(
                    //TODO: Replace placeholder
                    imageData = Constants.DEFAULT_AVATAR_URL,
                    contentDescription = "Top Artwork for today",
                    imageModifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = 1f - offsetProgress },
                    shadowModifier = Modifier.fillMaxSize(),
                    imageHeight = maxOffset
                )

                // Title and author info of the top image
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.3f)
                        .fillMaxWidth(0.5f)
                        .align(Alignment.BottomEnd),
                    horizontalAlignment = Alignment.End
                ) {
                    HeadingText(
                        text = "Heading Image", //TODO: Replace with response data
                        headingLevel = HeadingLevel.SECONDARY,
                        color = GrayParagraph,
                        paddingRight = 8.dp,
                        paddingBottom = 4.dp
                    )
                    HeadingText(
                        text = "AAA BBB", //TODO: Replace with response data
                        headingLevel = HeadingLevel.THIRD,
                        color = GrayParagraph,
                        paddingRight = 8.dp
                    )
                }

            }

            // Toolbar with current date
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CollapsedAppBarHeight)
                    .background(MaterialTheme.colors.primary),
                verticalArrangement = Arrangement.Center,
            ) {
                HeadingText(
                    //TODO: Replace with current date
                    text = "Friday, Oct 29th",
                    headingLevel = HeadingLevel.SECONDARY,
                    color = GrayParagraph,
                    // Set horizontal margin to 8.dp
                    paddingLeft = 8.dp + (CollapsedAppBarHeight - 8.dp) * offsetProgress,
                )
            }
        }
    }
}

@Composable
fun DailyArtWorkList(scrollState: LazyListState, isFreeTrail: Boolean
                     , artworks: List<DeviationDto>?) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = ExpandedAppBarHeight),
        state = scrollState
    ) {
        val marginHorizontal = 12.dp
        item {
            Column(
                modifier = Modifier.padding(marginHorizontal, 0.dp)
            ) {
                val itemWidth = LocalConfiguration.current.screenWidthDp.dp - marginHorizontal * 2f
                artworks?.forEach { artwork ->
                    Spacer(modifier = Modifier.height(12.dp))
                    ListItemCard(
                        imageUrl = artwork.content!!.src,
                        authorIconUrl = artwork.author?.userIconUrl ?: Constants.DEFAULT_AVATAR_URL,
                        authorName = artwork.author?.username ?: "Unknown",
                        createDate = artwork.publishedTime ?: "Unknown", //TODO: Convert type
                        title = artwork.title ?: "Untitled",
                        isFreeTrail = isFreeTrail,
                        isFavourite = artwork.isFavourited ?: false,
                        isDownloadable = artwork.isDownloadable ?: false,
                        itemWidth = itemWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemWidth * 0.9f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ListItemCard(
    imageUrl: String,
    authorIconUrl: String,
    authorName: String,
    createDate: String,
    title: String,
    isFreeTrail: Boolean,
    isFavourite: Boolean, //invisible if it's a free trail.
    isDownloadable: Boolean, //invisible if it's a free trail.
    itemWidth: Dp,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .drawColoredShadow(
                color = GrayParagraph
            ),
        shape = RoundedCornerShape(16.dp),
    ) {
        //set aspect ratio to 3:4
        val imageHeight = itemWidth * 0.75f
        val imageHeightPx = with(LocalDensity.current) {
            imageHeight.toPx()
        }

        //states
        val favouriteState = remember { mutableStateOf(isFavourite) }

        Column {
            Box {
                ShadowedImage(
                    imageData = imageUrl,
                    contentDescription = "Artwork Item",
                    imageModifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight),
                    shadowModifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight),
                    imageHeight = imageHeightPx
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundPrimary)
                    .padding(8.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = authorIconUrl,
                        builder = {
                            transformations(CircleCropTransformation())
                            placeholder(R.drawable.placeholder_person_foreground)
                        }
                    ),
                    contentDescription = "author icon",
                    modifier = Modifier.size(itemWidth * 0.1f),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.width(itemWidth * 0.5f)
                ) {
                    HeadingText(
                        text = "$authorName $createDate",
                        headingLevel = HeadingLevel.PARAGRAPH,
                        color = GrayParagraph
                    )

                    HeadingText(
                        text = title,
                        headingLevel = HeadingLevel.THIRD,
                        color = GrayParagraph
                    )
                }
                if (!isFreeTrail) {
                    //show favorite & download button only if the user has logged in
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            modifier = Modifier.fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //Favourite button
                            IconButton(
                                onClick = {
                                    favouriteState.value = !favouriteState.value
                                },
                                modifier = Modifier
                                    .size(itemWidth * 0.1f)
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = if (favouriteState.value) painterResource(R.drawable.ic_baseline_favorited_24)
                                    else painterResource(R.drawable.ic_baseline_not_favorited_24),
                                    contentDescription = "Download button",
                                    tint = Purple200
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            //Download button
                            IconButton(
                                onClick = { /*TODO*/ },
                                modifier = Modifier
                                    .size(itemWidth * 0.1f)
                                    .padding(4.dp),
                                enabled = isDownloadable,
                            ) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = painterResource(R.drawable.ic_baseline_download_24),
                                    contentDescription = "Download button",
                                    tint = if (isDownloadable) Teal200 else GrayParagraph
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun openNavDrawer(drawerState: DrawerState, scope: CoroutineScope) {
    scope.launch {
        drawerState.open()
    }
}

fun closeNavDrawer(drawerState: DrawerState, scope: CoroutineScope) {
    scope.launch {
        drawerState.close()
    }
}


