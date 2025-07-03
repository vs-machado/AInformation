package com.phoenix.ainformation.feature_news.domain.util

import com.phoenix.ainformation.feature_news.domain.model.rss_feed.RssItem
import org.jsoup.Jsoup

class Parser {

    // Update the imageUrl if a matcher exists in the description field
    fun extractImageUrls(items: List<RssItem>): List<RssItem> {
        return items.map { item ->
            item.description.let { description ->
                // Decode HTML entities
                val decodedHtml = description
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("&quot;", "\"")

                val document = Jsoup.parse(decodedHtml)
                val imgTag = document.select("img").first()

                item.imageUrl = imgTag?.attr("src")
            }
            item
        }
    }

    fun cleanDescription(description: String): String {
        // Replace common HTML entities with their actual characters
        var cleanedDescription = description
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&amp;", "&")
            .replace("&apos;", "'")

        // Preserve paragraphs by converting <p> and <br> tags to newlines
        cleanedDescription = cleanedDescription
            .replace("<br>", "\n")
            .replace("<br/>", "\n")
            .replace("<p>", "\n")
            .replace("</p>", "\n")

        // Remove all remaining HTML tags (anything between < and >)
        cleanedDescription = cleanedDescription.replace(Regex("<[^>]*>"), "")

        // Clean up multiple spaces and newlines
        cleanedDescription = cleanedDescription
            .replace(Regex("[ \\t]+"), " ") // Replace multiple spaces/tabs with a single space
            .replace(Regex("\n{2,}"), "\n\n") // Replace multiple newlines with two newlines
            .trim()

        return cleanedDescription
    }
}