package ir.danialchoopan.securediarydanial.ui.diary

import android.icu.text.DateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ir.danialchoopan.securediarydanial.domain.model.DiaryEntry
import ir.danialchoopan.securediarydanial.ui.theme.SecureDiaryDanialTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    entry: DiaryEntry?,
    onEditClick: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onBack: () -> Unit,
    language: String
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(if (language == "fa") "حذف خاطره" else "Delete Entry") },
            text = { Text(if (language == "fa") "آیا از حذف این خاطره مطمئن هستید؟" else "Are you sure you want to delete this entry?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteConfirm()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (language == "fa") "بله، حذف شود" else "Yes, Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(if (language == "fa") "لغو" else "Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    FilledIconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (entry != null) {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        entry?.let {
            val date = remember(it.timestamp, language) {
                if (language == "fa") {
                    val uLocale = ULocale("fa_IR@calendar=persian")
                    val calendar = Calendar.getInstance(uLocale)
                    calendar.timeInMillis = it.timestamp
                    val df = DateFormat.getDateInstance(DateFormat.FULL, uLocale)
                    df.format(calendar)
                } else {
                    SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(it.timestamp))
                }
            }
            val time = remember(it.timestamp, language) {
                if (language == "fa") {
                    val uLocale = ULocale("fa_IR@calendar=persian")
                    val calendar = Calendar.getInstance(uLocale)
                    calendar.timeInMillis = it.timestamp
                    val df = DateFormat.getTimeInstance(DateFormat.SHORT, uLocale)
                    df.format(calendar)
                } else {
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it.timestamp))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (!it.photoPath.isNullOrBlank()) {
                    AsyncImage(
                        model = it.photoPath,
                        contentDescription = "Entry photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Spacer(modifier = Modifier.height(64.dp))
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = it.title.ifBlank { if (language == "fa") "بدون عنوان" else "Untitled" },
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = date, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                        Text(
                            text = time,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )

                    Text(
                        text = it.content,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (it.latitude != null && it.longitude != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (language == "fa") "موقعیت ثبت شده" else "Captured Location",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${it.latitude}, ${it.longitude}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun DetailScreen(
    entryId: Long,
    viewModel: DiaryViewModel,
    onEditClick: (Long) -> Unit,
    onBack: () -> Unit,
    language: String
) {
    val entryState = viewModel.getEntryById(entryId).collectAsState(initial = null)
    val entry = entryState.value

    DetailContent(
        entry = entry,
        onEditClick = { onEditClick(entryId) },
        onDeleteConfirm = {
            viewModel.deleteEntry(entryId)
            onBack()
        },
        onBack = onBack,
        language = language
    )
}

@Preview(showBackground = true, name = "Modern Detail EN")
@Composable
fun DetailPreviewLight() {
    SecureDiaryDanialTheme(darkTheme = false, language = "en") {
        DetailContent(
            entry = DiaryEntry(1, "A Beautiful Sunset", "Today I saw the most amazing sunset at the beach. The colors were incredible.", latitude = 34.0522, longitude = -118.2437),
            onEditClick = {},
            onDeleteConfirm = {},
            onBack = {},
            language = "en"
        )
    }
}
