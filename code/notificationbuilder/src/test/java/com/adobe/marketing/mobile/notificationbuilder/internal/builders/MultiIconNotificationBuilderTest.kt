package com.adobe.marketing.mobile.notificationbuilder.internal.builders

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.MultiIconNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.createNotificationChannelIfRequired
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewClickAction
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewImage
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MultiIconPushTemplate
import com.adobe.marketing.mobile.services.Log
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MultiIconNotificationBuilderTest {

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var notificationManager: NotificationManager

    @Mock
    lateinit var remoteViews: RemoteViews

    @Mock
    internal lateinit var pushTemplate: MultiIconPushTemplate

    private val trackerActivityClass: Class<out Activity> = Activity::class.java

    @InjectMocks
    internal lateinit var multiIconNotificationBuilder: MultiIconNotificationBuilder

    @Test
    fun testConstructNotification() {
        `when`(context.packageName).thenReturn("com.adobe.test")
        `when`(context.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(notificationManager)
        `when`(notificationManager.createNotificationChannelIfRequired(context, pushTemplate)).thenReturn("test_channel_id")
        `when`(pushTemplate.templateItemList).thenReturn(mutableListOf())

        val builder = MultiIconNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)

        assertNotNull(builder)
        verify(notificationManager).createNotificationChannelIfRequired(context, pushTemplate)
    }

    /*@Test(expected = NotificationConstructionFailedException::class)
    fun testConstructNotificationWithInvalidIcons() {
        `when`(context.packageName).thenReturn("com.adobe.test")
        `when`(context.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(notificationManager)
        `when`(notificationManager.createNotificationChannelIfRequired(context, pushTemplate)).thenReturn("test_channel_id")
        `when`(pushTemplate.templateItemList).thenReturn(mutableListOf(
            MultiIconPushTemplate.MultiIconTemplateItem("invalid_icon_url", null)
        ))

        MultiIconNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
    }

    @Test
    fun testSetCancelIcon() {
        val notificationLayout = RemoteViews("com.adobe.test", R.layout.push_template_multi_icon)
        `when`(pushTemplate.cancelIcon).thenReturn("cancel_icon_url")

        MultiIconNotificationBuilder.setCancelIcon(notificationLayout, pushTemplate)

        verify(notificationLayout).setRemoteViewImage("cancel_icon_url", R.id.five_icon_close_button)
    }

    @Test
    fun testPopulateIconsForMultiIconTemplate() {
        val notificationLayout = RemoteViews("com.adobe.test", R.layout.push_template_multi_icon)
        val items = mutableListOf(
            MultiIconPushTemplate.MultiIconTemplateItem("valid_icon_url", "action_uri"),
            MultiIconPushTemplate.MultiIconTemplateItem("valid_icon_url_2", "action_uri_2"),
            MultiIconPushTemplate.MultiIconTemplateItem("valid_icon_url_3", "action_uri_3")
        )

        `when`(context.packageName).thenReturn("com.adobe.test")
        `when`(pushTemplate.actionUri).thenReturn("default_action_uri")
        `when`(pushTemplate.data.getBundle()).thenReturn(Bundle())

        MultiIconNotificationBuilder.populateIconsForMultiIconTemplate(
            context,
            trackerActivityClass,
            notificationLayout,
            pushTemplate,
            items,
            "com.adobe.test"
        )

        verify(notificationLayout, times(3)).addView(eq(R.id.icons_layout_linear), any(RemoteViews::class.java))
    }*/
}


/*
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.createNotificationChannelIfRequired
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewImage
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MultiIconPushTemplate
import org.junit.Assert
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
*/
/*@Config(sdk = [Build.VERSION_CODES.S])*//*

class MultiIconNotificationBuilderTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var trackerActivityClass: Class<out Activity>

    @Mock
    private lateinit var notificationManager: NotificationManager

    @Mock
    private lateinit var pushTemplate: MultiIconPushTemplate

    @Mock
    private lateinit var notificationLayout: RemoteViews

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        `when`(context.packageName).thenReturn("com.test.package")
        `when`(context.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(notificationManager)
        `when`(notificationManager.createNotificationChannelIfRequired(any(), any())).thenReturn("testChannel")
    }

    @Test
    fun testConstruct() {
        // Arrange
        val items = mutableListOf<MultiIconPushTemplate.MultiIconTemplateItem>()
        `when`(pushTemplate.templateItemList).thenReturn(items)
        `when`(pushTemplate.cancelIcon).thenReturn("cancelIcon")

        // Act
        val result = MultiIconNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)

        // Assert
        Assert.assertNotNull(result)
        verify(notificationLayout, times(1)).setRemoteViewImage("cancelIcon", any())
    }

    @Test
    fun testConstruct_withInvalidImages() {
        // Arrange
        val items = mutableListOf<MultiIconPushTemplate.MultiIconTemplateItem>()
        for (i in 1..2) {
            items.add(MultiIconPushTemplate.MultiIconTemplateItem("iconUrl$i", "actionUri$i"))
        }
        `when`(pushTemplate.templateItemList).thenReturn(items)
        `when`(pushTemplate.cancelIcon).thenReturn("cancelIcon")

        // Act
        try {
            MultiIconNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
            fail("Expected NotificationConstructionFailedException")
        } catch (e: NotificationConstructionFailedException) {
            // Expected exception
        }
    }

    @Test
    fun testConstruct_withValidImages() {
        // Arrange
        val items = mutableListOf<MultiIconPushTemplate.MultiIconTemplateItem>()
        for (i in 1..3) {
            items.add(MultiIconPushTemplate.MultiIconTemplateItem("iconUrl$i", "actionUri$i"))
        }
        `when`(pushTemplate.templateItemList).thenReturn(items)
        `when`(pushTemplate.cancelIcon).thenReturn("cancelIcon")

        // Act
        val result = MultiIconNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)

        // Assert
        assertNotNull(result)
        verify(notificationLayout, times(3)).setRemoteViewImage(anyString(), eq(R.id.icon_item_image_view))
    }
}*/
