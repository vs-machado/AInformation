package com.phoenix.ainformation.feature_news.domain.model.rss_feed

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import java.security.MessageDigest

/*
 * Data class used to get individual news data.
 */

@Xml(name = "item")
data class RssItem(

    @PropertyElement
    val title: String,

    @PropertyElement
    val link: String,

    @PropertyElement
    val description: String,

    @PropertyElement
    val pubDate: String,

) {
    @Transient
    val id: String = generateId(link, pubDate)

    // imageUrl is inside the description field and needs to be parsed
    @Transient
    var imageUrl: String? = null

    companion object {
        private fun generateId(link: String, pubDate: String): String {
            val input = "$link$pubDate"
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}
