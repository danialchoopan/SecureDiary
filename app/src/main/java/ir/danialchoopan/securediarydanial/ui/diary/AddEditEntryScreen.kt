package ir.danialchoopan.securediarydanial.ui.diary

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.core.net.toUri
import coil.compose.AsyncImage
import ir.danialchoopan.securediarydanial.domain.model.DiaryEntry
import ir.danialchoopan.securediarydanial.ui.theme.SecureDiaryDanialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEntryContent(
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    photoUri: Uri?,
    onAddPhotoClick: () -> Unit,
    onRemovePhotoClick: () -> Unit,
    latitude: Double?,
    longitude: Double?,
    onAddLocationClick: () -> Unit,
    onSaveClick: () -> Unit,
    onBack: () -> Unit,
    language: String,
    isEdit: Boolean
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isEdit) {
                            if (language == "fa") "ویرایش خاطره" else "Edit Entry"
                        } else {
                            if (language == "fa") "خاطره جدید" else "New Entry"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (language == "fa") "ذخیره" else "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Photo Section
            if (photoUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Selected photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    FilledIconButton(
                        onClick = onRemovePhotoClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove photo")
                    }
                }
            } else {
                OutlinedCard(
                    onClick = onAddPhotoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            if (language == "fa") "افزودن عکس به این روز" else "Add a photo for today",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Title Field
            TextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { 
                    Text(
                        if (language == "fa") "عنوان خاطره..." else "Entry Title...",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))

            // Content Field
            TextField(
                value = content,
                onValueChange = onContentChange,
                placeholder = { Text(if (language == "fa") "داستان امروز چیست؟" else "What's the story today?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .heightIn(min = 200.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // Location Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (latitude != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == "fa") "موقعیت ثبت شد" else "Location Captured",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                TextButton(
                    onClick = onAddLocationClick,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (language == "fa") "ثبت موقعیت" else "Add Location")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AddEditEntryScreen(
    entryId: Long,
    viewModel: DiaryViewModel,
    onBack: () -> Unit,
    language: String
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(entryId) {
        if (entryId != -1L && !isInitialized) {
            viewModel.getEntryById(entryId).collect { entry ->
                entry?.let {
                    title = it.title
                    content = it.content
                    photoUri = it.photoPath?.toUri()
                    latitude = it.latitude
                    longitude = it.longitude
                    isInitialized = true
                }
            }
        }
    }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) photoUri = uri }
    )

    AddEditEntryContent(
        title = title,
        onTitleChange = { title = it },
        content = content,
        onContentChange = { content = it },
        photoUri = photoUri,
        onAddPhotoClick = { photoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onRemovePhotoClick = { photoUri = null },
        latitude = latitude,
        longitude = longitude,
        onAddLocationClick = {
            latitude = 35.6892
            longitude = 51.3890
        },
        onSaveClick = {
            viewModel.saveEntry(
                DiaryEntry(
                    id = if (entryId == -1L) 0L else entryId,
                    title = title,
                    content = content,
                    photoPath = photoUri?.toString(),
                    latitude = latitude,
                    longitude = longitude,
                    timestamp = if (entryId == -1L) System.currentTimeMillis() else 0L
                )
            )
            onBack()
        },
        onBack = onBack,
        language = language,
        isEdit = entryId != -1L
    )
}

@Preview(showBackground = true, name = "Modern Add Entry EN")
@Composable
fun AddEditPreviewLight() {
    SecureDiaryDanialTheme(darkTheme = false, language = "en") {
        AddEditEntryContent(
            title = "",
            onTitleChange = {},
            content = "",
            onContentChange = {},
            photoUri = null,
            onAddPhotoClick = {},
            onRemovePhotoClick = {},
            latitude = null,
            longitude = null,
            onAddLocationClick = {},
            onSaveClick = {},
            onBack = {},
            language = "en",
            isEdit = false
        )
    }
}
