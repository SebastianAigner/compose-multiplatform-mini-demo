package model

import kotlinx.serialization.Serializable

@Serializable
data class BirdImage(
    var type: String,
    var path: String,
    var author: String
)