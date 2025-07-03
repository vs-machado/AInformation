package com.phoenix.ainformation.feature_news.presentation.main_screen.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.phoenix.ainformation.R
import com.phoenix.ainformation.feature_news.domain.model.rss_feed.RssItem
import com.phoenix.ainformation.feature_news.domain.util.DateUtil
import com.phoenix.ainformation.feature_news.presentation.main_screen.AISummaryState
import com.phoenix.ainformation.feature_news.presentation.main_screen.MainScreenViewModel
import java.util.Locale

@Composable
fun FeedItem(
    item: RssItem,
    onItemClick: () -> Unit,
    viewModel: MainScreenViewModel = hiltViewModel(),
    onNavigateToAISummaryScreen: (String) -> Unit
) {
    val openAlertDialog = remember { mutableStateOf(false) }
    val aiSummaryState = viewModel.aiSummaryState.collectAsStateWithLifecycle(initialValue = AISummaryState.Initial)
    val currentSummarizedItemId = viewModel.currentSummarizedItemId.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier.clickable {
            viewModel.openWebPage(item.link, context)
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = "News image",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .fillMaxWidth(),
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = DateUtil(context).getTimeAgo(item.pubDate),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 18.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                OutlinedButton(
                    onClick = { openAlertDialog.value = true },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gemini),
                        contentDescription = stringResource(R.string.generate_ai_summary),
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.generate_ai_summary),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
        HorizontalDivider(color = Color(0xFF2C2C2C).copy(alpha = 0.7f))
    }

    when {
        openAlertDialog.value -> {
            AISummaryDialog(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    viewModel.generateAISummary(item.description, Locale.getDefault().language.toString(), item.id)
                },
                dialogTitle = stringResource(R.string.generate_ai_summary),
                dialogText = stringResource(R.string.ai_summary_dialog_text),
                currentItemId = item.id,
                currentSummarizedItemId = currentSummarizedItemId.value!!,
                icon = ImageVector.vectorResource(id = R.drawable.ic_gemini),
                aiSummaryState = aiSummaryState.value,
                onAISummaryGenerated = { summary ->
                    openAlertDialog.value = false
                    onNavigateToAISummaryScreen(summary)
                    viewModel.resetAISummaryState()
                },
                resetAISummaryState = { viewModel.resetAISummaryState() }
            )
        }
    }
}

@Composable
fun AISummaryDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    currentItemId: String,
    currentSummarizedItemId: String,
    icon: ImageVector,
    aiSummaryState: AISummaryState,
    onAISummaryGenerated: (String) -> Unit,
    resetAISummaryState: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = stringResource(R.string.ai_resource_icon), tint = Color(0xFF25B24E))
        },
        title = {
            Text(text = dialogTitle)
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        text = {
            when {
                aiSummaryState is AISummaryState.Initial -> Text(text = dialogText)
                aiSummaryState is AISummaryState.Loading -> {
                    Column {
                        Text(text = dialogText)
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color(0xFF25B24E))
                    }
                }
                aiSummaryState is AISummaryState.Success && currentItemId == currentSummarizedItemId -> {
                    onAISummaryGenerated(aiSummaryState.summary)
                    Text(stringResource(R.string.summary_successfully_generated))
                }
                aiSummaryState is AISummaryState.Error -> Text(text = stringResource(R.string.ai_summary_error, aiSummaryState.message))
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            if(aiSummaryState !is AISummaryState.Error) {
                TextButton(
                    onClick = {
                        onConfirmation()
                    }
                ) {
                    Text(stringResource(R.string.confirm), color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    resetAISummaryState()
                }
            ) {
                Text(stringResource(R.string.dismiss), color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

//@Preview
//@Composable
//fun PreviewFeedItem() {
//    FeedItem(
//        RssItem("Title", "http://google.com/", "Hello world!", "10/10/2024")
//    )
//}