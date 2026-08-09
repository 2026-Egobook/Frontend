package com.egobook.app.ui.square

enum class ProfileAlignment {
    START,
    CENTER,
    END
}

enum class ProfileImagePlacement(
    val scale: Float,
    val horizontal: ProfileAlignment,
    val vertical: ProfileAlignment,
    val verticalOffsetFraction: Float = 0f
) {
    PLAZA_TURTLE(0.3f, ProfileAlignment.START, ProfileAlignment.END),
    PLAZA_BACKGROUND(0.1f, ProfileAlignment.CENTER, ProfileAlignment.CENTER),
    FRIEND_TURTLE(1f, ProfileAlignment.START, ProfileAlignment.END),
    FRIEND_BACKGROUND(0.5f, ProfileAlignment.CENTER, ProfileAlignment.CENTER, -0.131f)
}

data class ProfileImageTransform(
    val scale: Float,
    val translateX: Float,
    val translateY: Float
)

fun calculateProfileImageTransform(
    frameWidth: Int,
    frameHeight: Int,
    imageWidth: Int,
    imageHeight: Int,
    placement: ProfileImagePlacement
): ProfileImageTransform {
    require(frameWidth > 0 && frameHeight > 0)
    require(imageWidth > 0 && imageHeight > 0)

    val scale = placement.scale
    val scaledWidth = imageWidth * scale
    val scaledHeight = imageHeight * scale
    return ProfileImageTransform(
        scale = scale,
        translateX = alignedOffset(frameWidth.toFloat(), scaledWidth, placement.horizontal),
        translateY = alignedOffset(frameHeight.toFloat(), scaledHeight, placement.vertical) +
            frameHeight * placement.verticalOffsetFraction
    )
}

private fun alignedOffset(container: Float, content: Float, alignment: ProfileAlignment): Float =
    when (alignment) {
        ProfileAlignment.START -> 0f
        ProfileAlignment.CENTER -> (container - content) / 2f
        ProfileAlignment.END -> container - content
    }
