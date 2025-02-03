package com.example.rickandmorty.features.characterDetails

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.Transformation
import com.example.rickandmorty.components.topbar.TopBarConfig
import com.example.rickandmorty.features.theme.LocalTopBarManager

@Composable
fun CharacterDetailsRoute(
    modifier: Modifier = Modifier,
    characterId: Int = 0,
    characterName: String = "",
    onBackPressed: () -> Unit = {},
    viewModel: CharacterDetailsViewModel = hiltViewModel<CharacterDetailsViewModel, CharacterDetailsViewModel.Factory> { factory ->
        factory.create(characterId)
    }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CharacterDetailsScreen(
        modifier = modifier,
        state = state,
        characterName = characterName,
        onBackPressed = onBackPressed
    )
}

@Composable
fun CharacterDetailsScreen(
    modifier: Modifier = Modifier,
    characterName: String = "",
    state: CharacterDetailsState = CharacterDetailsState.IDLE,
    onBackPressed: () -> Unit = {},
) {
    val topBarManager = LocalTopBarManager.current

    LaunchedEffect(Unit) {
        topBarManager.showTopBar()
        topBarManager.setTopBarConfig(
            config = TopBarConfig(
                showTitle = true,
                title = characterName,
                showBackButton = true,
                onBackClicked = onBackPressed
            )
        )
    }

    CharacterDetailsContent(
        state = state,
        modifier = modifier
    )
}

@Composable
fun CharacterDetailsContent(
    modifier: Modifier = Modifier,
    state: CharacterDetailsState = CharacterDetailsState.IDLE,
) {

    val context = LocalContext.current
    var backgroundColor by remember { mutableStateOf(Color.DarkGray) }

    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(state.character.image)
            .crossfade(true)
            .transformations(object : Transformation {
                override val cacheKey: String
                    get() = state.character.image ?: ""

                override suspend fun transform(input: Bitmap, size: coil.size.Size): Bitmap {
                    Palette.from(input).generate { palette ->
                        palette?.dominantSwatch?.rgb?.let { colorValue ->
                            backgroundColor = Color(colorValue)
                        }
                    }
                    return input
                }
            })
            .build()
    )

    Column(
        modifier = modifier
            .background(Color.DarkGray)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(150.dp)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier,
                painter = painter,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = if (state.character.status == "Alive") {
                            Color.Green
                        } else if (state.character.status == "Dead") {
                            Color.Red
                        } else {
                            Color.Black
                        },
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = state.character.status,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 24.dp)
        ) {
            Text(
                text = "Species:",
                color = Color.Gray,
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = state.character.species,
                color = Color.White,
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Gender:",
                color = Color.Gray,
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = state.character.gender,
                color = Color.White,
                fontSize = 16.sp,
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Last known location:",
                color = Color.Gray,
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = state.character.location.name,
                color = Color.White,
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "First seen in:",
                color = Color.Gray,
                fontSize = 16.sp,
            )

            Text(
                text = state.character.origin.name,
                color = Color.White,
                fontSize = 16.sp,
            )
        }


    }
}
