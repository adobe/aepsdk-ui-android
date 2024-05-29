package com.adobe.marketing.mobile.notificationbuilder.internal.builders

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertNotNull
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import junit.framework.TestCase.assertEquals
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import android.content.Intent
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import org.junit.Assert.*
import org.robolectric.Shadows.shadowOf
import kotlin.properties.Delegates

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class BasicNotificationBuilderTest {

    @Mock
    private lateinit var basicPushTemplate: BasicPushTemplate
    private lateinit var context: Context
    private lateinit var trackerActivityClass: Class<out Activity>


    private lateinit var dataMap: MutableMap<String, String>
    private lateinit var broadcastReceiverClass: Class<out BroadcastReceiver>
    private lateinit var channelId: String

    private lateinit var remindLaterText : String
    private lateinit var actionButtonsString: String
    private var remindLaterTimestamp by Delegates.notNull<Long>()
    private var remindLaterDuration by Delegates.notNull<Int>()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        context = RuntimeEnvironment.getApplication()
        trackerActivityClass = DummyActivity::class.java
        broadcastReceiverClass = DummyBroadcastReceiver::class.java

        dataMap = mutableMapOf(
            "adb_version" to "1.0",
            "adb_title" to "Test Title",
            "adb_body" to "Test Body"
        )

        channelId = "testChannelId"
        remindLaterText = "Remind Later"
        actionButtonsString = "Action Button String"
        remindLaterTimestamp = 1715271471L
        remindLaterDuration = 3600
    }

    @Test
    fun `construct should not return null`() {
        val notificationBuilder = BasicNotificationBuilder.construct(context, basicPushTemplate, trackerActivityClass, broadcastReceiverClass)
        assertNotNull(notificationBuilder)
    }

    @Test
    fun `construct should return a NotificationCompat Builder`() {
        val notificationBuilder = BasicNotificationBuilder.construct(context, basicPushTemplate, trackerActivityClass, broadcastReceiverClass)
        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder.javaClass)
    }

    @Test
    fun `fallbackToBasicNotification should return a NotificationCompat Builder`() {
        val notificationBuilder = BasicNotificationBuilder.fallbackToBasicNotification(
            context,
            trackerActivityClass,
            broadcastReceiverClass,
            dataMap
        )
        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder.javaClass)
    }

    @Test
    fun `createRemindPendingIntent should return PendingIntent with correct extended data`() {

        val pushTemplate = BasicPushTemplate(MapData(dataMap))

        val pendingIntent = BasicNotificationBuilder.createRemindPendingIntent(
            context,
            broadcastReceiverClass,
            channelId,
            pushTemplate
        )

        assertNotNull(pendingIntent)
        val intent = shadowOf(pendingIntent).savedIntent

        intent.putExtra(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TEXT, remindLaterText)
        assertEquals(remindLaterText, intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TEXT))

        intent.putExtra(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TIMESTAMP, remindLaterTimestamp)
        assertEquals(remindLaterTimestamp, intent.getLongExtra(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TIMESTAMP, -1))

        intent.putExtra(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_DURATION, remindLaterDuration)
        assertEquals(remindLaterDuration, intent.getIntExtra(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_DURATION, -1))

        intent.putExtra(PushTemplateConstants.PushPayloadKeys.ACTION_BUTTONS, actionButtonsString)
        assertEquals(actionButtonsString, intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.ACTION_BUTTONS))
        assertEquals(channelId, intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.CHANNEL_ID))
    }

    @Test
    fun `createRemindPendingIntent should return null when broadcastReceiverClass is null`() {

        val pushTemplate = BasicPushTemplate(MapData(dataMap))

        val pendingIntent = BasicNotificationBuilder.createRemindPendingIntent(
            context,
            null,
            channelId,
            pushTemplate
        )

        assertNull(pendingIntent)
    }
}

class DummyActivity : Activity() {
    // empty class for testing
}

class DummyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // empty class for testing
    }
}
