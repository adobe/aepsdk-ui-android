package com.adobe.marketing.mobile.notificationbuilder.internal.builders

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils.cacheImages
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils.getCachedImage
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockAEPPushTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ZeroBezelPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class ZeroBezelNotificationBuilderTest {

    private lateinit var context: Context

    @MockK
    private lateinit var notificationManager: NotificationManager

    private lateinit var dataMap: MutableMap<String, String>
    private lateinit var expandedLayout: RemoteViews
    //    private lateinit var smallLayout: RemoteViews
    private val trackerActivityClass = DummyActivity::class.java
    private val channelIdToUse = "channel_id"

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        context = RuntimeEnvironment.getApplication()
        notificationManager = mockk()
        dataMap = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        mockkObject(PushTemplateImageUtils)
        expandedLayout = mockkClass(RemoteViews::class)
//        smallLayout = mockkClass(RemoteViews::class)
    }

    @Test
    fun `construct with image downloaded and style is IMAGE`() {
        val pushTemplate = ZeroBezelPushTemplate(MapData(dataMap))
        every { cacheImages(any()) } answers { 2 }
        val mockBitmap = mockkClass(Bitmap::class)
        every { getCachedImage(any()) } answers { mockBitmap }
//        every {
//            notificationManager.createNotificationChannelIfRequired(
//                context,
//                pushTemplate
//            )
//        } answers { channelIdToUse }


        every {
            expandedLayout.setImageViewBitmap(any(), mockBitmap)
        }

        // Call the method under test
        val builder = ZeroBezelNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass
        )

        // Verify behavior
//        assertNotNull(builder)
//        verify { PushTemplateImageUtils.cacheImages(listOf(pushTemplate.imageUrl)) }
//        verify { PushTemplateImageUtils.getCachedImage(pushTemplate.imageUrl) }
//

        verify {
            expandedLayout.setImageViewBitmap(any(), mockBitmap)
        }
    }

//    @Test
//    fun `construct with image downloaded and style is not IMAGE`() {
//        // Mocking image download
//        dataMap.replaceValueInMap(
//            Pair(
//                PushTemplateConstants.PushPayloadKeys.ZERO_BEZEL_COLLAPSED_STYLE,
//                "txt"
//            )
//        )
//        val pushTemplate = ZeroBezelPushTemplate(MapData(dataMap))
//        every { cacheImages(any()) } answers { 2 }
//
//        val mockBitmap = mockk<Bitmap>()
//        every { getCachedImage(any()) } answers { mockBitmap }
//
//        every {
//            notificationManager.createNotificationChannelIfRequired(
//                context,
//                pushTemplate
//            )
//        } returns channelIdToUse
//
//        // Call the method under test
//        val builder = ZeroBezelNotificationBuilder.construct(
//            context,
//            pushTemplate,
//            trackerActivityClass
//        )
//
//        // Verify behavior
//        assertNotNull(builder)
//        verify { PushTemplateImageUtils.cacheImages(listOf(pushTemplate.imageUrl)) }
//        verify { PushTemplateImageUtils.getCachedImage(pushTemplate.imageUrl) }
//        verify { notificationManager.createNotificationChannelIfRequired(context, pushTemplate) }
//
//        // Verify RemoteViews interactions
//        val smallLayout =
//            RemoteViews(context.packageName, R.layout.push_template_zero_bezel_collapsed)
//        val expandedLayout =
//            RemoteViews(context.packageName, R.layout.push_template_zero_bezel_expanded)
//
//        // Verify the image is set on the expanded layout
//        verify {
//            expandedLayout.setImageViewBitmap(R.id.expanded_template_image, mockBitmap)
//        }
//        // Verify the image views are hidden on the small layout
//        verify {
//            smallLayout.setViewVisibility(R.id.collapsed_template_image, View.GONE)
//            smallLayout.setViewVisibility(R.id.gradient_template_image, View.GONE)
//        }
//    }

//    @Test
//    fun `construct with no image downloaded`() {
//        // Mocking image download
//        val pushTemplate = ZeroBezelPushTemplate(MapData(dataMap))
//
//        every { PushTemplateImageUtils.cacheImages(listOf(pushTemplate.imageUrl)) } returns 0
//        every { notificationManager.createNotificationChannelIfRequired(context, pushTemplate) } returns channelIdToUse
//
//        // Call the method under test
//        val builder = ZeroBezelNotificationBuilder.construct(
//            context,
//            pushTemplate,
//            trackerActivityClass
//        )
//
//        // Verify behavior
//        assertNotNull(builder)
//        verify { PushTemplateImageUtils.cacheImages(listOf(pushTemplate.imageUrl)) }
//        verify { notificationManager.createNotificationChannelIfRequired(context, pushTemplate) }
//
//        // Verify RemoteViews interactions
//        val smallLayout = RemoteViews(context.packageName, R.layout.push_template_zero_bezel_collapsed)
//        val expandedLayout = RemoteViews(context.packageName, R.layout.push_template_zero_bezel_expanded)
//
//        // Verify the image views are hidden on the expanded layout
//        verify {
//            expandedLayout.setViewVisibility(R.id.expanded_template_image, View.GONE)
//            expandedLayout.setViewVisibility(R.id.gradient_template_image, View.GONE)
//        }
//        // Verify the image views are hidden on the small layout
//        verify {
//            smallLayout.setViewVisibility(R.id.collapsed_template_image, View.GONE)
//            smallLayout.setViewVisibility(R.id.gradient_template_image, View.GONE)
//        }
//    }

}
