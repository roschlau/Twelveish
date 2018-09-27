/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish

import android.app.Notification
import android.app.PendingIntent
import android.content.*
import android.graphics.*
import android.net.Uri
import android.os.*
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationHelperActivity
import android.support.wearable.complications.rendering.ComplicationDrawable
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.util.Log
import android.util.SparseArray
import android.view.Gravity.CENTER_HORIZONTAL
import android.view.Gravity.TOP
import android.view.SurfaceHolder
import android.view.WindowInsets
import com.layoutxml.twelveish.config.ComplicationConfigActivity
import com.layoutxml.twelveish.config.DigitalWatchFaceWearableConfigActivity
import org.joda.time.LocalDate
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit

/*
            -----------------------------------------
            | Min.  | Prefix               | Suffix |
            -----------------------------------------
            | 0     | -                    | -      |
            | 1-4   | -                    | ish    |
            | 5-9   | -                    | or so  |
            | 10-14 | almost a quarter past| -      |
            | 15-19 | quarter past         | or so  |
            | 20-24 | almost half past     | -      |
            | 25-29 | around half past     | -      |
            | 30-34 | half past            | ish    |
            | 35-39 | half past            | or so  |
            | 40-44 | almost a quarter to  | -      | Hours+1
            | 45-49 | quarter to           | or so  | Hours+1
            | 50-54 | almost               | -      | Hours+1
            | 55-59 | around               | -      | Hours+1
            ----------------------------------------
 */

class MyWatchFace : CanvasWatchFaceService() {
    private lateinit var localization: ResolvedLocalization
    private var batteryLevel: Int = 100
    private var screenWidthG: Int? = null
    private var screenHeightG: Int? = null
    //SharedPreferences:
    private lateinit var prefs: Prefs
    var mActiveComplicationDataSparseArray: SparseArray<ComplicationData>? = null
        private set
    var mComplicationDrawableSparseArray: SparseArray<ComplicationDrawable>? = null
        private set

    override fun onCreateEngine(): Engine {
        applicationContext.registerReceiver(Intent.ACTION_BATTERY_CHANGED) { _, intent ->
            batteryLevel = (100 * intent.getIntExtra(
                BatteryManager.EXTRA_LEVEL,
                -1
            ) / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1).toFloat()).toInt()
        }
        return Engine()
    }

    private fun drawComplications(canvas: Canvas, currentTimeMillis: Long) {
        for (complicationId in complicationIds) {
            with(mComplicationDrawableSparseArray!!.get(complicationId)) {
                setBackgroundColorActive(prefs.backgroundColor)
                setHighlightColorActive(prefs.secondaryColor)
                setHighlightColorAmbient(prefs.secondaryColorAmbient)
                setIconColorActive(prefs.secondaryColor)
                setIconColorAmbient(prefs.secondaryColorAmbient)
                setTextColorActive(prefs.secondaryColor)
                setTextColorAmbient(prefs.secondaryColorAmbient)
                setRangedValuePrimaryColorActive(prefs.secondaryColor)
                setRangedValuePrimaryColorAmbient(prefs.secondaryColorAmbient)
                setRangedValueSecondaryColorActive(prefs.secondaryColor)
                setRangedValueSecondaryColorAmbient(prefs.secondaryColorAmbient)
                setRangedValueRingWidthActive(prefs.secondaryColor)
                setRangedValueRingWidthAmbient(prefs.secondaryColorAmbient)
                setTitleColorActive(prefs.secondaryColor)
                setTitleColorAmbient(prefs.secondaryColorAmbient)
                draw(canvas, currentTimeMillis)
            }
        }
    }

    private class EngineHandler internal constructor(engine: MyWatchFace.Engine) : Handler() {
        private val mWeakReference = WeakReference(engine)

        override fun handleMessage(msg: Message) {
            val engine = mWeakReference.get()
            if (engine != null) {
                when (msg.what) {
                    MSG_UPDATE_TIME -> engine.handleUpdateTimeMessage()
                }
            }
        }
    }

    inner class Engine : CanvasWatchFaceService.Engine() {

        private val mUpdateTimeHandler = EngineHandler(this)
        private val mCalendar: Calendar by lazy { Calendar.getInstance() }
        private val mTimeZoneReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                mCalendar.timeZone = TimeZone.getDefault()
                invalidate()
            }
        }
        private var mRegisteredTimeZoneReceiver = false
        private var mChinSize: Float = 0.toFloat()
        private lateinit var mBackgroundPaint: Paint
        private lateinit var mTextPaint: Paint
        private lateinit var mTextPaint2: Paint
        private var mLowBitAmbient: Boolean = false
        private var mAmbient: Boolean = false

        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)

            setWatchFaceStyle(
                WatchFaceStyle.Builder(this@MyWatchFace)
                    .setStatusBarGravity(CENTER_HORIZONTAL or TOP)
                    .setShowUnreadCountIndicator(true)
                    .setAcceptsTapEvents(true)
                    .build()
            )

            mBackgroundPaint = Paint()
            mBackgroundPaint.color =
                    ContextCompat.getColor(applicationContext, R.color.background)

            mTextPaint = Paint().apply {
                typeface = NORMAL_TYPEFACE
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }

            mTextPaint2 = Paint().apply {
                typeface = NORMAL_TYPEFACE
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }

            loadPreferences()

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                loadComplications()
        }

        private fun loadComplications() {
            mActiveComplicationDataSparseArray = SparseArray(complicationIds.size)
            val bottomComplicationDrawable =
                getDrawable(R.drawable.custom_complication_styles) as ComplicationDrawable
            bottomComplicationDrawable.setContext(applicationContext)
            mComplicationDrawableSparseArray = SparseArray(complicationIds.size)
            mComplicationDrawableSparseArray!!.put(
                BOTTOM_COMPLICATION_ID,
                bottomComplicationDrawable
            )
            setActiveComplications(*MyWatchFace.complicationIds)
        }

        private fun showRateNotification() {
            Log.d(TAG, "showRateNotification: start")
            val notificationId = 1
            // The channel ID of the notification.
            val id = "Main"
            // Build intent for notification content
            val viewIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.layoutxml.twelveish"))
            viewIntent.putExtra(
                "Rate Twelveish",
                "Would you like to rate Twelveish? I won't ask again :)"
            )
            val viewPendingIntent = PendingIntent.getActivity(applicationContext, 0, viewIntent, 0)

            // Notification channel ID is ignored for Android 7.1.1
            // (API level 25) and lower.
            val notificationBuilder = NotificationCompat.Builder(applicationContext, id)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Rate Twelveish")
                .setContentText("Would you like to rate Twelveish? Tap to go to Google Play store.")
                .setContentIntent(viewPendingIntent)

            // Get an instance of the NotificationManager service
            val notificationManager = NotificationManagerCompat.from(applicationContext)

            // Issue the notification with notification manager.
            notificationManager.notify(notificationId, notificationBuilder.build())
        }

        private fun showTutorialNotification() {
            val notificationId = 2
            // The channel ID of the notification.
            val id = "Main"
            // Build intent for notification content
            val viewIntent =
                Intent(applicationContext, DigitalWatchFaceWearableConfigActivity::class.java)
            viewIntent.putExtra("Open settings", "Don't forget to customize the watch")
            val viewPendingIntent = PendingIntent.getActivity(applicationContext, 0, viewIntent, 0)

            // Notification channel ID is ignored for Android 7.1.1
            // (API level 25) and lower.
            val notificationBuilder = NotificationCompat.Builder(applicationContext, id)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Open Twelveish Settings")
                .setContentText("Don't forget to customize Twelveish directly on your watch")
                .setContentIntent(viewPendingIntent)

            // Get an instance of the NotificationManager service
            val notificationManager = NotificationManagerCompat.from(applicationContext)

            // Issue the notification with notification manager.
            notificationManager.notify(notificationId, notificationBuilder.build())
        }

        override fun onComplicationDataUpdate(
            complicationId: Int, complicationData: ComplicationData?
        ) {
            mActiveComplicationDataSparseArray!!.put(complicationId, complicationData)
            val complicationDrawable = mComplicationDrawableSparseArray!!.get(complicationId)
            complicationDrawable.setComplicationData(complicationData)
            invalidate()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            screenHeightG = height
            screenWidthG = width
        }

        private fun loadPreferences() {
            prefs = Prefs(
                this@MyWatchFace,
                applicationContext.getSharedPreferences(
                    getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
            )
            localization = ResolvedLocalization.from(prefs.language, this@MyWatchFace)

            MyWatchFace.NORMAL_TYPEFACE = when (prefs.font) {
                "robotolight" -> Typeface.create("sans-serif-light", Typeface.NORMAL)
                "alegreya" -> ResourcesCompat.getFont(applicationContext, R.font.alegreya)
                "cabin" -> ResourcesCompat.getFont(applicationContext, R.font.cabin)
                "ibmplexsans" -> ResourcesCompat.getFont(applicationContext, R.font.ibmplexsans)
                "inconsolata" -> ResourcesCompat.getFont(applicationContext, R.font.inconsolata)
                "merriweather" -> ResourcesCompat.getFont(applicationContext, R.font.merriweather)
                "nunito" -> ResourcesCompat.getFont(applicationContext, R.font.nunito)
                "pacifico" -> ResourcesCompat.getFont(applicationContext, R.font.pacifico)
                "quattrocento" -> ResourcesCompat.getFont(applicationContext, R.font.quattrocento)
                "quicksand" -> ResourcesCompat.getFont(applicationContext, R.font.quicksand)
                "rubik" -> ResourcesCompat.getFont(applicationContext, R.font.rubik)
                else -> Typeface.create("sans-serif-light", Typeface.NORMAL)
            }
            mTextPaint2.typeface = MyWatchFace.NORMAL_TYPEFACE

            if (prefs.counter >= 50 && (!prefs.showedRateAlready)) {
                prefs.showedRateAlready = true
                showRateNotification()
            }
            prefs.counter++
            if (!prefs.showedTutorialAlready) {
                prefs.showedTutorialAlready = true
                showTutorialNotification()
            }
        }

        override fun onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            super.onDestroy()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                registerReceiver()
                mCalendar.timeZone = TimeZone.getDefault()
                invalidate()
            } else {
                unregisterReceiver()
            }
            loadPreferences()
            updateTimer()
        }

        private fun registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = true
            val filter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
            this@MyWatchFace.registerReceiver(mTimeZoneReceiver, filter)
        }

        private fun unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = false
            this@MyWatchFace.unregisterReceiver(mTimeZoneReceiver)
        }

        override fun onApplyWindowInsets(insets: WindowInsets) {
            super.onApplyWindowInsets(insets)
            val resources = this@MyWatchFace.resources
            val isRound = insets.isRound
            mChinSize = insets.systemWindowInsetBottom.toFloat()
            val textSize =
                resources.getDimension(if (isRound) R.dimen.digital_text_size_round else R.dimen.digital_text_size)
            val textSizeSmall =
                resources.getDimension(if (isRound) R.dimen.digital_text_size_round else R.dimen.digital_text_size) / 2.5f
            mTextPaint.textSize = textSizeSmall
            mTextPaint2.textSize = textSize
            val bottomBounds = Rect(
                screenWidthG!! / 2 - screenHeightG!! / 4,
                (screenHeightG!! * 3 / 4 - mChinSize).toInt(),
                screenWidthG!! / 2 + screenHeightG!! / 4,
                (screenHeightG!! - mChinSize).toInt()
            )
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val bottomComplicationDrawable =
                    mComplicationDrawableSparseArray!!.get(BOTTOM_COMPLICATION_ID)
                bottomComplicationDrawable.bounds = bottomBounds
            }
        }

        override fun onPropertiesChanged(properties: Bundle?) {
            super.onPropertiesChanged(properties)
            mLowBitAmbient =
                    properties!!.getBoolean(WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false)
        }

        override fun onTimeTick() {
            super.onTimeTick()
            invalidate()
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            mAmbient = inAmbientMode
            if (mLowBitAmbient) {
                mTextPaint.isAntiAlias = !inAmbientMode
                mTextPaint2.isAntiAlias = !inAmbientMode
            }
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                var complicationDrawable: ComplicationDrawable
                for (COMPLICATION_ID in complicationIds) {
                    complicationDrawable = mComplicationDrawableSparseArray!!.get(COMPLICATION_ID)
                    complicationDrawable.setInAmbientMode(mAmbient)
                }
            }
            updateTimer()
        }

        override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            when (tapType) {
                WatchFaceService.TAP_TYPE_TOUCH -> {
                    // The user has started touching the screen.
                }
                WatchFaceService.TAP_TYPE_TOUCH_CANCEL -> {
                    // The user has started a different gesture or otherwise cancelled the tap.
                }
                WatchFaceService.TAP_TYPE_TAP ->
                    // The user has completed the tap gesture.
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val tappedComplicationId = getTappedComplicationId(x, y)
                        if (tappedComplicationId != -1) {
                            onComplicationTap(tappedComplicationId)
                        }
                    }
            }
            invalidate()
        }

        private fun getTappedComplicationId(x: Int, y: Int): Int {
            var complicationId: Int
            var complicationData: ComplicationData?
            var complicationDrawable: ComplicationDrawable
            val currentTimeMillis = System.currentTimeMillis()
            for (COMPLICATION_ID in complicationIds) {
                complicationId = COMPLICATION_ID
                complicationData = mActiveComplicationDataSparseArray!!.get(complicationId)
                if (complicationData != null
                    && complicationData.isActive(currentTimeMillis)
                    && complicationData.type != ComplicationData.TYPE_NOT_CONFIGURED
                    && complicationData.type != ComplicationData.TYPE_EMPTY
                ) {
                    complicationDrawable = mComplicationDrawableSparseArray!!.get(complicationId)
                    val complicationBoundingRect = complicationDrawable.bounds
                    if (complicationBoundingRect.width() > 0) {
                        if (complicationBoundingRect.contains(x, y)) {
                            return complicationId
                        }
                    } else {
                        Log.e(TAG, "Not a recognized complication id.")
                    }
                }
            }
            return -1
        }

        private fun onComplicationTap(complicationId: Int) {
            val complicationData = mActiveComplicationDataSparseArray!!.get(complicationId)
            if (complicationData == null) {
                Log.d(TAG, "No PendingIntent for complication $complicationId.")
                return
            }
            if (complicationData.tapAction != null) {
                try {
                    complicationData.tapAction.send()
                } catch (e: PendingIntent.CanceledException) {
                    Log.e(TAG, "onComplicationTap() tap action error: $e")
                }

            } else if (complicationData.type == ComplicationData.TYPE_NO_PERMISSION) {
                val componentName = ComponentName(
                    applicationContext, MyWatchFace::class.java
                )
                val permissionRequestIntent =
                    ComplicationHelperActivity.createPermissionRequestHelperIntent(
                        applicationContext, componentName
                    )
                startActivity(permissionRequestIntent)
            }
        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {
            //Set colors
            if (isInAmbientMode) {
                canvas.drawColor(Color.BLACK)
                mTextPaint.color = prefs.secondaryColorAmbient
                mTextPaint2.color = prefs.mainColorAmbient
            } else {
                canvas.drawColor(prefs.backgroundColor)
                mTextPaint.color = prefs.secondaryColor
                mTextPaint2.color = prefs.mainColor
            }

            //Get time
            val now = System.currentTimeMillis()
            mCalendar.timeInMillis = now
            val seconds = mCalendar.get(Calendar.SECOND)
            val minutes = mCalendar.get(Calendar.MINUTE)
            var hourDigital: Int? = mCalendar.get(if (prefs.militaryTime) Calendar.HOUR_OF_DAY else Calendar.HOUR)
            if (hourDigital == 0 && (!prefs.militaryTime))
                hourDigital = 12
            val index = minutes / 5
            var hourText: Int = if (prefs.militaryTextTime)
                mCalendar.get(Calendar.HOUR_OF_DAY) + localization.timeshift[index]
            else
                mCalendar.get(Calendar.HOUR) + localization.timeshift[index]
            if (prefs.militaryTextTime && hourText >= 24) {
                hourText -= 24
            } else if (!prefs.militaryTextTime && hourText > 12) {
                hourText -= 12
            }
            if (!prefs.militaryTextTime && hourText == 0) {
                hourText = 12
            }

            //Get digital clock
            val ampmSymbols =
                when {
                    !prefs.ampm -> ""
                    mCalendar.get(Calendar.HOUR_OF_DAY) >= 12 -> " pm"
                    else -> " am"
                }
            val shouldDisplayDate = (isInAmbientMode && prefs.showSecondaryCalendarInactive)
                    || (!isInAmbientMode && prefs.showSecondaryCalendarActive)
            val dateDisplay = when {
                !shouldDisplayDate -> ""
                else -> prefs.dateFormat.print(LocalDate.fromCalendarFields(mCalendar))
            }

            //Get battery percentage
            val shouldDisplayBattery = (isInAmbientMode && prefs.showBatteryAmbient)
                    || (!isInAmbientMode && prefs.showBattery)
            val batteryDisplay =
                if (!shouldDisplayBattery) {
                    ""
                } else {
                    batteryLevel.toString() + "%"
                }

            //Get day of the week
            val shouldDisplayDayOfWeek = (isInAmbientMode && prefs.showDayAmbient)
                    || (!isInAmbientMode && prefs.showDay)
            val dayOfTheWeek =
                if (shouldDisplayDayOfWeek) {
                    localization.weekDays[mCalendar.get(Calendar.DAY_OF_WEEK) - 1]
                } else {
                    ""
                }

            //Draw digital clock, date, battery percentage and day of the week
            var firstSeparator = 40.0f

            var timeDisplay = if (mAmbient || !prefs.showSeconds) {
                String.format(Locale.UK, "%d:%02d$ampmSymbols", hourDigital, minutes)
            } else {
                String.format(Locale.UK, "%d:%02d:%02d$ampmSymbols", hourDigital, minutes, seconds)
            }
            if (isInAmbientMode && (!prefs.showSecondary) || !isInAmbientMode && (!prefs.showSecondaryActive)) {
                timeDisplay = ""
            }
            if (timeDisplay != "" || dayOfTheWeek != "") {
                if (timeDisplay != "" && dayOfTheWeek != "") {
                    canvas.drawText(
                        "$timeDisplay • $dayOfTheWeek",
                        (bounds.width() / 2).toFloat(),
                        firstSeparator - mTextPaint.ascent(),
                        mTextPaint
                    )
                    firstSeparator = 40 - mTextPaint.ascent() + mTextPaint.descent()
                } else if (timeDisplay != "") {
                    canvas.drawText(
                        timeDisplay,
                        (bounds.width() / 2).toFloat(),
                        firstSeparator - mTextPaint.ascent(),
                        mTextPaint
                    )
                    firstSeparator = 40 - mTextPaint.ascent() + mTextPaint.descent()
                } else {
                    canvas.drawText(
                        dayOfTheWeek,
                        (bounds.width() / 2).toFloat(),
                        firstSeparator - mTextPaint.ascent(),
                        mTextPaint
                    )
                    firstSeparator = 40 - mTextPaint.ascent() + mTextPaint.descent()
                }
            }
            if (dateDisplay != "" || batteryDisplay != "") {
                if (dateDisplay != "" && batteryDisplay != "") {
                    canvas.drawText(
                        "$dateDisplay • $batteryDisplay",
                        (bounds.width() / 2).toFloat(),
                        firstSeparator - mTextPaint.ascent(),
                        mTextPaint
                    )
                    firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent()
                } else if (dateDisplay != "") {
                    canvas.drawText(
                        dateDisplay,
                        (bounds.width() / 2).toFloat(),
                        firstSeparator - mTextPaint.ascent(),
                        mTextPaint
                    )
                    firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent()
                } else {
                    canvas.drawText(
                        batteryDisplay,
                        (bounds.width() / 2).toFloat(),
                        firstSeparator - mTextPaint.ascent(),
                        mTextPaint
                    )
                    firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent()
                }
            }
            if (firstSeparator < bounds.height() / 4)
                firstSeparator = bounds.height().toFloat() / 4

            //Draw text clock
            if (isInAmbientMode && prefs.showWordsAmbient || !isInAmbientMode && prefs.showWords) {
                val text2 = when (prefs.capitalisation) {
                    0 -> capitaliseWordsTitleCase(hourText, minutes, index)
                    1 -> capitaliseCaps(hourText, minutes, index)
                    2 -> capitaliseLowercase(hourText, minutes, index)
                    3 -> capitaliseFirstWordTitleCase(hourText, minutes, index)
                    4 -> capitaliseLinesTitleCase(hourText, minutes, index)
                    else -> capitaliseWordsTitleCase(hourText, minutes, index)
                }
                mTextPaint2.textSize = getTextSizeForWidth(
                    (bounds.width() - 32).toFloat(),
                    (bounds.height() * 3 / 4).toFloat() - mChinSize - firstSeparator - 32f,
                    text2
                )
                val x = (bounds.width() / 2).toFloat()
                var y = (bounds.height() * 3 / 4 - mChinSize + firstSeparator) / 2
                for (line in text2.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    y += mTextPaint2.descent() - mTextPaint2.ascent()
                }
                val difference = y - (bounds.height() * 3 / 4 - mChinSize + firstSeparator) / 2
                y = (bounds.height() * 3 / 4 - mChinSize + firstSeparator) / 2 - difference / 2 -
                        mTextPaint2.ascent()
                for (line in text2.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    canvas.drawText(line, x, y, mTextPaint2)
                    y += mTextPaint2.descent() - mTextPaint2.ascent()
                }
            }

            //Draw complication
            if ((isInAmbientMode && prefs.showComplicationAmbient || !isInAmbientMode && prefs.showComplication) && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                drawComplications(canvas, now)
        }

        val exactTimes: Array<String> by lazy {
            resources.getStringArray(
                when (prefs.language) {
                    "en" -> R.array.ExactTimes
                    "de" -> R.array.ExactTimesDE
                    "lt" -> R.array.ExactTimesLT
                    "fi" -> R.array.ExactTimesFI
                    "ru" -> R.array.ExactTimesRU
                    "hu" -> R.array.ExactTimesHU
                    else -> R.array.ExactTimes
                }
            )
        }

        private fun capitaliseWordsTitleCase(hours: Int, minutes: Int, index: Int): String {
            //Prefix
            var mainPrefix = ""
            val prefix: StringBuilder
            if (minutes > 0 && localization.prefixes[index] != "") {
                val prefixArray =
                    localization.prefixes[index].split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                prefix = StringBuilder()
                for (word in prefixArray) {
                    if (prefix.length != 0)
                        prefix.append(" ")
                    val capitalised = word.substring(0, 1).toUpperCase() + word.substring(1)
                    prefix.append(capitalised)
                }
                mainPrefix = prefix.toString()
            }

            //Time
            val hoursInWords = StringBuilder()
            val mainText: String
            val mainArray = exactTimes[hours]
                .split(" ".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
            for (word in mainArray) {
                if (hoursInWords.isNotEmpty()) {
                    hoursInWords.append(" ")
                    hoursInWords.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1))
                } else {
                    if (!(mainPrefix == "" || localization.prefixNewLine[index]))
                        hoursInWords.append(word)
                    else
                        hoursInWords.append(
                            word.substring(
                                0,
                                1
                            ).toUpperCase()
                        ).append(word.substring(1))
                }
            }
            mainText = hoursInWords.toString()

            //Suffix
            var mainSuffix = ""
            if (prefs.showSuffixes) {
                val suffix: StringBuilder
                if (minutes > 0 && localization.suffixes[index] != "") {
                    if (localization.suffixNewLine[index]) {
                        val suffixArray =
                            localization.suffixes[index].split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        suffix = StringBuilder()
                        for (word in suffixArray) {
                            if (suffix.length != 0)
                                suffix.append(" ")
                            val capitalised = word.substring(0, 1).toUpperCase() + word.substring(1)
                            suffix.append(capitalised)
                        }
                        mainSuffix = suffix.toString()
                    } else {
                        mainSuffix = localization.suffixes[index].toLowerCase()
                    }
                }
            }
            return mainPrefix + (if (minutes > 0) if (localization.prefixNewLine[index]) "\n" else "" else "") + mainText + (if (minutes > 0) if (localization.suffixNewLine[index]) "\n" else "" else "") + mainSuffix
        }

        private fun capitaliseCaps(hours: Int, minutes: Int, index: Int): String {
            val middle = exactTimes[hours]
            val text = ((if (minutes > 0) localization.prefixes[index] else "")
                    + (if (minutes > 0) if (localization.prefixNewLine[index]) "\n" else "" else "")
                    + middle
                    + (if (minutes > 0) if (localization.suffixNewLine[index]) "\n" else "" else "")
                    + if (prefs.showSuffixes) if (minutes > 0) localization.suffixes[index] else "" else "")
            return text.toUpperCase()
        }

        private fun capitaliseLowercase(hours: Int, minutes: Int, index: Int): String {
            val middle = exactTimes[hours]
            val text = ((if (minutes > 0) localization.prefixes[index] else "")
                    + (if (minutes > 0) if (localization.prefixNewLine[index]) "\n" else "" else "")
                    + middle
                    + (if (minutes > 0) if (localization.suffixNewLine[index]) "\n" else "" else "")
                    + if (prefs.showSuffixes) if (minutes > 0) localization.suffixes[index] else "" else "")

            return text.toLowerCase()
        }

        private fun capitaliseFirstWordTitleCase(hours: Int, minutes: Int, index: Int): String {
            val middle = exactTimes[hours]
            val text20 = ((if (minutes > 0) localization.prefixes[index] else "")
                    + (if (minutes > 0) if (localization.prefixNewLine[index]) "\n" else "" else "")
                    + middle
                    + (if (minutes > 0) if (localization.suffixNewLine[index]) "\n" else "" else "")
                    + if (prefs.showSuffixes) if (minutes > 0) localization.suffixes[index] else "" else "")
            return text20.substring(0, 1).toUpperCase() + text20.substring(1).toLowerCase()
        }

        private fun capitaliseLinesTitleCase(hours: Int, minutes: Int, index: Int): String {
            //Prefix
            var mainPrefix = ""
            if (minutes > 0 && localization.prefixes[index] != "") {
                mainPrefix = localization.prefixes[index].substring(0, 1).toUpperCase() +
                        localization.prefixes[index].substring(1).toLowerCase()
            }

            //Time
            val middle = exactTimes[hours]
            val mainText: String
            if (mainPrefix == "" || localization.prefixNewLine[index])
                mainText = middle.substring(0, 1).toUpperCase() + middle.substring(1)
            else
                mainText = middle.toLowerCase()

            //Suffix
            var mainSuffix = ""
            if (prefs.showSuffixes) {
                if (minutes > 0 && localization.suffixes[index] != "") {
                    if (localization.suffixNewLine[index]) {
                        mainSuffix = localization.suffixes[index].substring(0, 1).toUpperCase() +
                                localization.suffixes[index].substring(1).toLowerCase()
                    } else {
                        mainSuffix = localization.suffixes[index].toLowerCase()
                    }
                }
            }
            return mainPrefix + (if (mCalendar.get(Calendar.MINUTE) > 0) if (localization.prefixNewLine[index]) "\n" else "" else "") + mainText + (if (mCalendar.get(
                    Calendar.MINUTE
                ) > 0
            ) if (localization.suffixNewLine[index]) "\n" else "" else "") + mainSuffix

        }

        private fun updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            }
        }

        private fun shouldTimerBeRunning(): Boolean {
            return isVisible && !isInAmbientMode
        }

        fun handleUpdateTimeMessage() {
            invalidate()
            if (shouldTimerBeRunning()) {
                val timeMs = System.currentTimeMillis()
                val delayMs = INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }

        private fun getTextSizeForWidth(
            desiredWidth: Float,
            desiredHeight: Float,
            text: String
        ): Float {
            var min = Integer.MAX_VALUE.toFloat()
            var linecount = 0f
            for (line in text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                if (line != "")
                    linecount++
                val testTextSize = 100.00f
                mTextPaint2.textSize = testTextSize
                val bounds = Rect()
                val lineBounds = "O" + line + "O"
                mTextPaint2.getTextBounds(lineBounds, 0, lineBounds.length, bounds)
                val desiredTextSize = testTextSize * desiredWidth / bounds.width()
                val desiredTextSize2 =
                    testTextSize * desiredHeight / (bounds.height() + mTextPaint2.descent()) / linecount
                if (desiredTextSize < min)
                    min = desiredTextSize
                if (desiredTextSize2 < min)
                    min = desiredTextSize2
            }
            val newPaint = mTextPaint2
            newPaint.textSize = min
            while (newPaint.measureText(
                    "|",
                    0,
                    "|".length
                ) / 5 > 6
            ) { //6 is the burn in protection shifting limit in pixels
                min -= 2f
                newPaint.textSize = min
            }
            return min
        }
    }

    companion object {
        private var NORMAL_TYPEFACE: Typeface? =
            Typeface.create("sans-serif-light", Typeface.NORMAL)

        private val INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1)
        private val MSG_UPDATE_TIME = 0
        private val TAG = "MyWatchFace"
        //Complications and their data
        val BOTTOM_COMPLICATION_ID = 0
        val complicationIds = intArrayOf(BOTTOM_COMPLICATION_ID)
        private val COMPLICATION_SUPPORTED_TYPES = arrayOf(
            intArrayOf(
                ComplicationData.TYPE_RANGED_VALUE,
                ComplicationData.TYPE_ICON,
                ComplicationData.TYPE_LONG_TEXT,
                ComplicationData.TYPE_SHORT_TEXT,
                ComplicationData.TYPE_SMALL_IMAGE,
                ComplicationData.TYPE_LARGE_IMAGE
            )
        )

        fun getComplicationId(complicationLocation: ComplicationConfigActivity.ComplicationLocation): Int {
            return when (complicationLocation) {
                ComplicationConfigActivity.ComplicationLocation.BOTTOM -> BOTTOM_COMPLICATION_ID
            }
        }

        fun getSupportedComplicationTypes(complicationLocation: ComplicationConfigActivity.ComplicationLocation): IntArray {
            return when (complicationLocation) {
                ComplicationConfigActivity.ComplicationLocation.BOTTOM -> COMPLICATION_SUPPORTED_TYPES[0]
            }
        }
    }
}
