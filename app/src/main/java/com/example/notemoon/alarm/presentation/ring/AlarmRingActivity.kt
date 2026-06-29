package com.example.notemoon.alarm.presentation.ring

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notemoon.alarm.domain.util.MathChallenge
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.notemoon.alarm.domain.repository.AlarmRepository
import com.example.notemoon.alarm.domain.util.AlarmSchedule
import com.example.notemoon.alarm.receiver.AlarmContract
import com.example.notemoon.alarm.receiver.AlarmReceiver
import com.example.notemoon.ui.theme.NoteMoonTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

// Fixed, high-contrast colors for the alarm/challenge screen so it reads clearly
// on every device, independent of the (wallpaper-derived) dynamic theme.
private val RingBg = Color(0xFF1B2440)        // deep navy background
private val RingOn = Color(0xFFFFFFFF)        // text/icons on the background
private val FieldBg = Color(0xFFFFFFFF)       // input + button surface
private val FieldText = Color(0xFF121826)     // near-black text on the field
private val FieldHint = Color(0xFF5B6473)     // label/placeholder on the field
private val ErrorRed = Color(0xFFFFD2D2)      // "wrong" message on the dark bg

/**
 * Full-screen alarm screen, launched by the firing notification's full-screen
 * intent. It shows over the lock screen and turns the screen on, displays the
 * time and label, and offers Snooze / Dismiss — both of which route through
 * [AlarmReceiver] so the ringer has one controller. It closes itself when the
 * alarm is dismissed from anywhere (button, notification action, or auto-stop).
 */
@AndroidEntryPoint
class AlarmRingActivity : ComponentActivity() {

    @Inject lateinit var repository: AlarmRepository

    private var alarmId: Long = -1L
    private var timeText by mutableStateOf("")
    private var labelText by mutableStateOf("Alarm")
    private var mathEnabled by mutableStateOf(false)
    private var mathTotal by mutableStateOf(2)

    private val finishedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) = finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showWhenLockedAndTurnScreenOn()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // The alarm must be dealt with via the buttons; ignore Back.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = Unit
        })

        ContextCompat.registerReceiver(
            this,
            finishedReceiver,
            IntentFilter(AlarmContract.ACTION_FINISHED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        handleIntent(intent)

        setContent {
            NoteMoonTheme {
                AlarmRingScreen(
                    time = timeText,
                    label = labelText,
                    mathEnabled = mathEnabled,
                    mathTotal = mathTotal,
                    onSnooze = { sendAction(AlarmContract.ACTION_SNOOZE) },
                    onDismiss = { sendAction(AlarmContract.ACTION_DISMISS) }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        alarmId = intent?.getLongExtra(AlarmContract.EXTRA_ALARM_ID, -1L) ?: -1L
        if (alarmId == -1L) {
            finish()
            return
        }
        lifecycleScope.launch {
            repository.getAlarmById(alarmId)?.let { alarm ->
                timeText = AlarmSchedule.formatTime(alarm.hour, alarm.minute)
                labelText = alarm.label.ifBlank { "Alarm" }
                mathEnabled = alarm.mathToDismiss
                mathTotal = alarm.mathQuestions.coerceAtLeast(1)
            }
        }
    }

    private fun sendAction(action: String) {
        sendBroadcast(
            Intent(this, AlarmReceiver::class.java).apply {
                this.action = action
                putExtra(AlarmContract.EXTRA_ALARM_ID, alarmId)
            }
        )
        finish()
    }

    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        runCatching { unregisterReceiver(finishedReceiver) }
    }
}

@androidx.compose.runtime.Composable
private fun AlarmRingScreen(
    time: String,
    label: String,
    mathEnabled: Boolean,
    mathTotal: Int,
    onSnooze: () -> Unit,
    onDismiss: () -> Unit
) {
    var inChallenge by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = RingBg
    ) {
        if (inChallenge) {
            MathChallengeContent(
                total = mathTotal,
                onSolved = onDismiss,
                onCancel = { inChallenge = false }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(PaddingValues(horizontal = 24.dp, vertical = 48.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Alarm,
                    contentDescription = null,
                    tint = RingOn,
                    modifier = Modifier.height(56.dp)
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = time,
                    color = RingOn,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = label,
                    color = RingOn,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(64.dp))

                OutlinedButton(
                    onClick = onSnooze,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RingOn)
                ) {
                    Icon(Icons.Filled.Snooze, contentDescription = null)
                    Text("  Snooze", fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { if (mathEnabled) inChallenge = true else onDismiss() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FieldBg,
                        contentColor = RingBg
                    )
                ) {
                    Text(if (mathEnabled) "Dismiss (solve maths)" else "Dismiss", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun MathChallengeContent(
    total: Int,
    onSolved: () -> Unit,
    onCancel: () -> Unit
) {
    var solved by remember { mutableIntStateOf(0) }
    var question by remember { mutableStateOf(MathChallenge.generate()) }
    var input by remember { mutableStateOf("") }
    var wrong by remember { mutableStateOf(false) }

    fun submit() {
        val value = input.trim().toIntOrNull()
        if (value == question.answer) {
            val next = solved + 1
            if (next >= total) {
                onSolved()
                return
            }
            solved = next
            wrong = false
        } else {
            wrong = true
        }
        // Always move to a fresh random question (so a wrong answer is re-asked anew).
        question = MathChallenge.generate()
        input = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(horizontal = 24.dp, vertical = 48.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Solved $solved / $total",
            color = RingOn,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "${question.prompt} = ?",
            color = RingOn,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = input,
            onValueChange = { new -> input = new.filter { it.isDigit() || it == '-' }.take(4) },
            isError = wrong,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Type the answer here") },
            textStyle = TextStyle(
                color = FieldText,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth(),
            // Solid white field with near-black text — fixed colors so the box and
            // the typed answer are clearly visible on any device/theme.
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FieldBg,
                unfocusedContainerColor = FieldBg,
                errorContainerColor = FieldBg,
                focusedTextColor = FieldText,
                unfocusedTextColor = FieldText,
                errorTextColor = FieldText,
                cursorColor = FieldText,
                focusedBorderColor = FieldText,
                unfocusedBorderColor = FieldHint,
                errorBorderColor = Color(0xFFD23B3B),
                focusedLabelColor = FieldText,
                unfocusedLabelColor = FieldHint,
                errorLabelColor = FieldHint
            )
        )
        if (wrong) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Wrong — try this one.",
                color = ErrorRed,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { submit() },
            enabled = input.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FieldBg,
                contentColor = RingBg,
                disabledContainerColor = Color(0xFFC9CDD6),
                disabledContentColor = Color(0xFF5B6473)
            )
        ) {
            Text("Check", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onCancel) {
            Text("Cancel", color = RingOn)
        }
    }
}
