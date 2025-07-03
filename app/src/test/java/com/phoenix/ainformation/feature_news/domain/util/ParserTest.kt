package com.phoenix.ainformation.feature_news.domain.util

import com.phoenix.ainformation.feature_news.domain.model.rss_feed.RssItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ParserTest {
    private lateinit var parser: Parser

    @Before
    fun setup() {
        parser = Parser()
    }

    @Test
    fun `extractImageUrls should extract image URL from description`() {
        // Arrange
        val inputItems = listOf(
            RssItem(
                title = "Test Item",
                link = "https://example.com",
                pubDate = "2023-04-20",
                description = "&lt;img src=&quot;https://dims.apnews.com/dims4/default/5e63e9d/2147483647/strip/true/crop/4356x2450+0+227/resize/1440x810!/quality/90/?url=https://assets.apnews.com/64/57/9bc793265f91cc24c282fe59fe3b/b5144a80b6f3492b838e840117148247&quot; width=&quot;100%&quot;&gt;&lt;small style=&quot;color: #999;&quot;&gt;FILE - A Tesla logo is shown on Feb. 27, 2024, in Charlotte, N.C. (AP Photo/Chris Carlson, File)&lt;/small&gt;&lt;p&gt;2024-10-10T10:00:07Z&lt;/p&gt;\n" + "                                                                                                                                            &lt;p&gt;LOS ANGELES (AP) — Tesla unveiled its long-awaited robotaxi at a Hollywood studio Thursday night, though fans of the electric vehicle maker will have to wait until at least 2026 before they are available.&lt;/p&gt;&lt;p&gt;CEO Elon Musk pulled up to a stage at the Warner Bros. studio lot in one of the company’s “Cybercabs,” telling the crowd that the sleek, AI-powered vehicles don’t have steering wheels or pedals. He also expressed confidence in the progress the company has made on autonomous driving technology that makes it possible for vehicles to drive without human intervention.&lt;/p&gt;&lt;p&gt;Tesla began selling the software, which is called “Full Self-Driving,” nine years ago. &lt;span&gt;&lt;a",
            )
        )

        // Act
        val result = parser.extractImageUrls(inputItems)

        // Assert
        assertEquals("https://dims.apnews.com/dims4/default/5e63e9d/2147483647/strip/true/crop/4356x2450+0+227/resize/1440x810!/quality/90/?url=https://assets.apnews.com/64/57/9bc793265f91cc24c282fe59fe3b/b5144a80b6f3492b838e840117148247", result[0].imageUrl)
    }

    @Test
    fun `extractImageUrls should handle items without images`() {
        // Arrange
        val inputItems = listOf(
            RssItem(
                title = "Test Item",
                link = "https://example.com",
                pubDate = "2023-04-20",
                description = "Some description without image",
            )
        )

        // Act
        val result = parser.extractImageUrls(inputItems)

        // Assert
        assertNull(result[0].imageUrl)
    }
}