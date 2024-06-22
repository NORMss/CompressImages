package com.norm.mycompressimages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import com.norm.mycompressimages.ui.theme.MyCompressImagesTheme
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyCompressImagesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = this

                    var currentImage by remember {
                        mutableStateOf(Uri.EMPTY)
                    }

                    var compressedImage by remember {
                        mutableStateOf<Bitmap?>(null)
                    }

                    val galleryLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri ->
                        currentImage = uri
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Button(
                            onClick = {
                                galleryLauncher.launch("image/*")
                            }
                        ) {
                            Text(
                                text = "Open gallery"
                            )
                        }
                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                        AsyncImage(
                            model = currentImage,
                            contentDescription = null,
                            modifier = Modifier
                                .size(256.dp),
                        )
                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                        if (currentImage != Uri.EMPTY) {
                            Button(onClick = {
                                val drawable = currentImage.toDrawable(context)
                                compressedImage = context.reduceImageSize(drawable)
                            }
                            ) {
                                Text(
                                    text = "Compress"
                                )
                            }
                            AsyncImage(
                                model = compressedImage,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
    }
}

fun Uri.toDrawable(context: Context): Drawable? {
    val content = context.contentResolver
    val inputStream = content.openInputStream(this)
    return Drawable.createFromStream(inputStream, this.toString())
}

fun Context.reduceImageSize(drawable: Drawable?): Bitmap? {
    if (drawable == null) {
        return null
    }

    val baos = ByteArrayOutputStream()
    val bitmap = drawable.toBitmap(256, 256, Bitmap.Config.ARGB_8888)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val imageBitmap = baos.toByteArray()

    return BitmapFactory.decodeByteArray(imageBitmap, 0, imageBitmap.size)
}