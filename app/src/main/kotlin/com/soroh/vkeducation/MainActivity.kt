package com.soroh.vkeducation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.soroh.vkeducation.ui.theme.VKEducationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VKEducationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var textError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    fun validateText(): Boolean {
        return if (text.isBlank()) {
            textError = "Введите текст"
            false
        } else {
            textError = null
            true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Явные, неявные и системные интенты",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                textError = null
                phoneError = null
            },
            label = { Text("Введите текст или номер телефона") },
            isError = textError != null || phoneError != null,
            supportingText = {
                when {
                    textError != null -> Text(textError!!)
                    phoneError != null -> Text(phoneError!!)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (validateText()) {
                    context.startActivity(SecondActivity.newIntent(context, text))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Открыть вторую Activity")
        }

        Button(
            onClick = {
                val phone = text.trim()
                if (phone.isBlank()) {
                    phoneError = "Введите номер телефона"
                    return@Button
                }
                if (!phone.matches(Regex("^\\+?[0-9\\s\\-()]{5,20}$"))) {
                    phoneError = "Некорректный номер телефона"
                    return@Button
                }
                phoneError = null
                val dialIntent = Intent(Intent.ACTION_DIAL, "tel:$phone".toUri())
                try {
                    context.startActivity(dialIntent)
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(context, "Нет приложения для звонков", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Позвонить другу")
        }

        Button(
            onClick = {
                if (validateText()) {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    try {
                        context.startActivity(
                            Intent.createChooser(sendIntent, "Поделиться через…")
                        )
                    } catch (_: ActivityNotFoundException) {
                        Toast.makeText(context, "Нет приложения для отправки", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Поделиться текстом")
        }
    }
}
