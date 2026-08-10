package com.egobook.app.ui.square

const val TURTLE_HEAD_CENTER_X_FRACTION = 0.19f
const val TURTLE_HEAD_CENTER_Y_FRACTION = 0.66f
const val TURTLE_HEAD_WIDTH_FRACTION = 0.30f

enum class ProfileScaleMode {
    HEAD_CENTERED,
    CENTER_CROP
}

enum class ProfileImagePlacement(
    val scaleMode: ProfileScaleMode,
    val targetHeadWidthFraction: Float = 0f,
    val targetCenterXFraction: Float = 0.5f,
    val targetCenterYFraction: Float = 0.5f,
    val verticalOffsetFraction: Float = 0f
) {
    PLAZA_TURTLE(ProfileScaleMode.HEAD_CENTERED, targetHeadWidthFraction = 0.65f),
    PLAZA_BACKGROUND(ProfileScaleMode.CENTER_CROP),
    FRIEND_TURTLE(
        ProfileScaleMode.HEAD_CENTERED,
        targetHeadWidthFraction = 0.60f,
        targetCenterXFraction = 0.38f,
        targetCenterYFraction = 0.62f
    ),
    FRIEND_BACKGROUND(ProfileScaleMode.CENTER_CROP, verticalOffsetFraction = -0.131f)
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

    return when (placement.scaleMode) {
        ProfileScaleMode.HEAD_CENTERED -> {
            val scale = frameWidth * placement.targetHeadWidthFraction /
                (imageWidth * TURTLE_HEAD_WIDTH_FRACTION)
            ProfileImageTransform(
                scale = scale,
                translateX = frameWidth * placement.targetCenterXFraction -
                    imageWidth * TURTLE_HEAD_CENTER_X_FRACTION * scale,
                translateY = frameHeight * placement.targetCenterYFraction -
                    imageHeight * TURTLE_HEAD_CENTER_Y_FRACTION * scale
            )
        }

        ProfileScaleMode.CENTER_CROP -> {
            val scale = maxOf(
                frameWidth.toFloat() / imageWidth,
                frameHeight.toFloat() / imageHeight
            )
            ProfileImageTransform(
                scale = scale,
                translateX = (frameWidth - imageWidth * scale) / 2f,
                translateY = (frameHeight - imageHeight * scale) / 2f +
                    frameHeight * placement.verticalOffsetFraction
            )
        }
    }
}
