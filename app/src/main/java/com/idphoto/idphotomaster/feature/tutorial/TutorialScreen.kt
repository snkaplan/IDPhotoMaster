package com.idphoto.idphotomaster.feature.tutorial

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.systemdesign.components.DotIndicator
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Blue
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.LightGrey
import de.palm.composestateevents.NavigationEventEffect
import kotlinx.coroutines.launch

@Composable
fun TutorialScreen(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TutorialViewModel = hiltViewModel()
) {
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    NavigationEventEffect(
        event = viewState.navigateToHome,
        onConsumed = viewModel::onNavigateToHomeConsumed,
        action = navigateToHome
    )
    ScreenContent(
        viewState = viewState,
        onSkipClicked = viewModel::onSkipClicked,
        modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScreenContent(
    viewState: TutorialViewState,
    onSkipClicked: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val pagerState = rememberPagerState(pageCount = { viewState.tutorialItems.size })
        val isLastPage = pagerState.currentPage == pagerState.pageCount - 1
        Column(modifier = Modifier.weight(1f)) {
            HorizontalPager(
                modifier = modifier.weight(1f), state = pagerState
            ) { page ->
                PageItem(viewState.tutorialItems[page])
            }
            DotIndicator(count = viewState.tutorialItems.size, pagerState = pagerState)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isLastPage) Arrangement.SpaceEvenly else Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val coroutineScope = rememberCoroutineScope()
            if (isLastPage.not()) {
                ClickableText(
                    modifier = Modifier.padding(20.dp),
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = LightGrey,
                                fontSize = 14.sp
                            )
                        ) {
                            append(stringResource(id = R.string.skip))
                        }
                    }) {
                    onSkipClicked.invoke()
                }
            }
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
                onClick = {
                    if (isLastPage) {
                        onSkipClicked.invoke()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }, modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    fontWeight = FontWeight.Bold,
                    text = if (isLastPage) stringResource(id = R.string.start) else stringResource(
                        id = R.string.next
                    )
                )
            }
        }
    }
}

@Composable
fun PageItem(pageItem: TutorialPageItem) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            modifier = Modifier.size(300.dp),
            painter = painterResource(id = pageItem.imageId),
            contentDescription = "Page image"
        )
        Text(
            text = stringResource(id = pageItem.titleId),
            style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.padding(horizontal = 40.dp),
            text = stringResource(pageItem.descriptionId),
            style = TextStyle(fontWeight = FontWeight.Normal),
            textAlign = TextAlign.Center
        )
    }
}