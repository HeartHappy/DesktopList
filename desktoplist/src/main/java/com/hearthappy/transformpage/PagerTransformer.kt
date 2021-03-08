package com.hearthappy.transformpage

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * Created Date 2020/12/29.
 * @author ChenRui
 * ClassDescription:ViewPager切换动画
 */
class PagerTransformer(private var animSpecies: AnimSpecies) : ViewPager.PageTransformer {


    override fun transformPage(page: View, position: Float) {
        when (animSpecies) {
            is AnimSpecies.Windmill -> animSpecies.animation(page, position)
            is AnimSpecies.FloatUp -> animSpecies.animation(page, position)
            is AnimSpecies.Translate -> animSpecies.animation(page, position)
        }
    }

    sealed class AnimSpecies {

        abstract fun animation(page: View, position: Float)

        abstract fun type(): String


        /**
         * 风车动画
         */
        object Windmill : AnimSpecies() {
            override fun animation(page: View, position: Float) {
                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    page.rotation = 0f

                } else if (position <= 1) // a页滑动至b页 ； a页从 0.0 ~ -1 ；b页从1 ~ 0.0
                { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    if (position < 0) {

                        mRot = (ROT_MAX * position)
                        page.pivotX = page.measuredWidth * 0.5f
                        page.pivotY = page.measuredHeight.toFloat()
                        page.rotation = mRot
                    } else {

                        mRot = (ROT_MAX * position)
                        page.pivotX = page.measuredWidth * 0.5f
                        page.pivotY = page.measuredHeight.toFloat()
                        page.rotation = mRot
                    }

                    // Scale the page down (between MIN_SCALE and 1)

                    // Fade the page relative to its size.

                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    page.rotation = 0f
                }
            }

            override fun type() = "Windmill"
        }

        /**
         * 上浮动画
         */
        object FloatUp : AnimSpecies() {
            override fun animation(page: View, position: Float) {
                if (position >= -1 || position <= 1) {
                    //隐藏
                    when {
                        position < -1 -> {
                            page.alpha = 0f
                        }
                        position <= 0 -> {
                            page.alpha = 1f
                            page.translationX = 0f
                            page.scaleX = 1f
                            page.scaleY = 1f
                        }
                        position <= 1 -> {
                            //显示
                            page.alpha = 1 - position
                            // Counteract the default slide transition
                            page.translationX = page.width * -position
                            //                    Log.i(TAG, "显示transformPage: ${page.width * -position},transX:${page.translationX}")
                            // Scale the page down (between MIN_SCALE and 1)
                            val scaleFactor: Float =
                                (MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position)))
                            page.scaleX = scaleFactor
                            page.scaleY = scaleFactor
                        }
                        else -> {
                            page.alpha = 0f
                        }
                    }
                }
            }

            override fun type() = "FloatUp"
        }

        /**
         * 平移、缩放、淡入淡出
         */
        object Translate : AnimSpecies() {
            override fun animation(page: View, position: Float) {
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        page.alpha = 0f
                    }
                    //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                        val verMargin: Float = page.height * (1 - scaleFactor) / 2
                        val horMargin: Float = page.width * (1 - scaleFactor) / 2
                        if (position < 0) {
                            page.translationX = horMargin - verMargin / 2
                        } else {
                            page.translationX = -horMargin + verMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        page.scaleX = scaleFactor
                        page.scaleY = scaleFactor

                        // Fade the page relative to its size.
                        page.alpha =
                            MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        page.alpha = 0f
                    }
                }
            }

            override fun type() = "Translate"
        }
    }

    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f
        private const val ROT_MAX = 20.0f
        private var mRot = 0f
    }

}


