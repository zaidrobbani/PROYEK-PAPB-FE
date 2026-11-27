package com.example.proyek_papb_fe.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import com.example.proyek_papb_fe.core.di.AppContainer

@Composable
fun rememberServerImageUri(profileImageUrl: String?): String? {
    var localUri by remember(profileImageUrl) { mutableStateOf<String?>(null) }
    LaunchedEffect(profileImageUrl) {
        localUri = AppContainer.profilePhotoRepository.localUriOf(profileImageUrl)
    }
    return localUri
}

@Composable
fun ProfilePhoto(
    photoUrl: String?,
    name: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.foundation.shape.CircleShape,
    fontSize: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified
) {
    val localUri = rememberServerImageUri(photoUrl)
    if (localUri == null) {
        if (fontSize == androidx.compose.ui.unit.TextUnit.Unspecified) InitialsAvatar(name, modifier, shape)
        else InitialsAvatar(name, modifier, shape, fontSize)
    } else {
        LocalImage(
            uri = localUri,
            fallbackRes = null,
            contentDescription = "Foto $name",
            modifier = modifier.clip(shape),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            placeholder = { InitialsAvatar(name, androidx.compose.ui.Modifier, shape) }
        )
    }
}
