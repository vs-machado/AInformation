package com.phoenix.ainformation.feature_news.data.repository

import com.phoenix.ainformation.feature_news.domain.model.rss_feed.RssItem
import com.phoenix.ainformation.feature_news.domain.model.rss_feed.repository.RssNewsRepository

class FakeRssNewsRepository: RssNewsRepository {

    override suspend fun getItems(page: Int, pageSize: Int): Result<List<RssItem>> {
        return Result.success(listOf(
            RssItem(
                title = "Yahya Sinwar, Hamas’ top leader and a mastermind of the Oct. 7 attack on Israel, is dead at 61",
                link = "https://apnews.com/article/yahya-sinwar-hamas-leader-killed-obituary-gaza-6965bea4270313c1f1501335c705ac9a",
                description = "<img src=\"https://dims.apnews.com/dims4/default/3051e12/...\" width=\"100%\"><small>...</small><p>...</p>",
                pubDate = "Fri, 18 Oct 2024 23:53:02 +0800"
            ).apply {
                imageUrl = extractImageUrl(description)
            },
            RssItem(
                title = "Liam Payne’s 1D bandmates, James Corden and more friends mourn singer",
                link = "https://apnews.com/article/liam-payne-death-reactions-one-direction-band-tribute-f08045886ebff903fbc3addc62dcb836",
                description = "<img src=\"https://dims.apnews.com/dims4/default/7931e4/...\" width=\"100%\"><small>...</small><p>...</p>",
                pubDate = "Fri, 18 Oct 2024 12:27:54 +0800"
            ).apply {
                imageUrl = extractImageUrl(description)
            },
            RssItem(
                title = "US to probe Tesla’s ‘Full Self-Driving’ system after pedestrian killed",
                link = "https://apnews.com/article/tesla-full-self-driving-investigation-pedestrian-killed-f2121166d60d85bd173a734c91049e73",
                description = "<img src=\"https://dims.apnews.com/dims4/default/ec63584/...\" width=\"100%\"><small>...</small><p>...</p>",
                pubDate = "Fri, 18 Oct 2024 11:06:15 +0800"
            ).apply {
                imageUrl = extractImageUrl(description)
            }
        ))
    }

    // Function to extract the image URL from the description field
    private fun extractImageUrl(description: String): String? {
        val regex = Regex("""<img src="([^"]+)""")
        return regex.find(description)?.groups?.get(1)?.value
    }

}