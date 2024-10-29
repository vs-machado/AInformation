package com.phoenix.ainformation.feature_news.domain.model.rss_feed

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/*
 * Data class used to fetch the news feed
 */
@Xml(name = "channel")
data class Channel(
    @PropertyElement val title: String,
    @PropertyElement val description: String,
    @PropertyElement val link: String,
    @Element(name = "item") val items: List<RssItem>
)
