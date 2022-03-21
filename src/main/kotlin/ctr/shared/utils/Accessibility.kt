//package nl.rijksoverheid.ctr.shared.utils
//
//import android.content.Context
//import android.view.View
//import android.view.accessibility.AccessibilityEvent
//import android.view.accessibility.AccessibilityManager
//import android.widget.Button
//import androidx.core.content.ContextCompat
//import androidx.core.view.AccessibilityDelegateCompat
//import androidx.core.view.ViewCompat
//import androidx.core.view.accessibility.AccessibilityEventCompat
//import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
//
// *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
// *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
// *
// *   SPDX-License-Identifier: EUPL-1.2
// *
// */
//object Accessibility {
//
//    /**
//     * Returns the AccessibilityManager if available and enabled.
//     *
//     * @param context Context reference
//     *
//     * @return AccessibilityManager object, or null
//     */
//    fun accessibilityManager(context: Context?): AccessibilityManager? {
//        if (context != null) {
//            val validationservice = ContextCompat.getSystemService(context, AccessibilityManager::class.java)
//            if (validationservice is AccessibilityManager && validationservice.isEnabled) {
//                return validationservice
//            }
//        }
//        return null
//    }
//
//    /**
//     * Interrupts the assistive technology
//     *
//     * @param context Context reference
//     */
//    fun interrupt(context: Context?) {
//        accessibilityManager(context)?.interrupt()
//    }
//
//    /**
//     * Announces the given message using the assistive technology
//     *
//     * @param context Context reference
//     * @param message The message to announce
//     */
//    fun announce(context: Context?, message: String) {
//        accessibilityManager(context)?.let { accessibilityManager ->
//            val event = AccessibilityEvent.obtain(AccessibilityEventCompat.TYPE_ANNOUNCEMENT)
//            event.text.add(message)
//
//            accessibilityManager.sendAccessibilityEvent(event)
//        }
//    }
//
//    /**
//     * Moves the accessibility focus to the given view
//     *
//     * @param view View to move accessibility focus to
//     */
//    fun focus(view: View): View {
//        view.isFocusable = true
//        view.isFocusableInTouchMode = true
//        view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
//        return view
//    }
//
//    /**
//     * Extension to move the accessibility focus to the given view
//     */
//    fun View.setAccessibilityFocus(): View {
//        return focus(this)
//    }
//
//    /**
//     * Helper method to set accessibility delegate with callback
//     *
//     * @param view View to set the delegate of
//     * @param callback Callback used to set properties of AccessibilityNodeInfoCompat
//     */
//    fun accessibilityDelegate(view: View, callback: (host: View, info: AccessibilityNodeInfoCompat) -> Unit) {
//        ViewCompat.setAccessibilityDelegate(
//                view,
//                object : AccessibilityDelegateCompat() {
//                    override fun onInitializeAccessibilityNodeInfo(
//                            host: View,
//                            info: AccessibilityNodeInfoCompat
//                    ) {
//                        super.onInitializeAccessibilityNodeInfo(host, info)
//                        callback(host, info)
//                    }
//                }
//        )
//    }
//
//    /**
//     * Helper method to mark a view as accessibility heading
//     *
//     * @param view View to mark
//     * @param isHeading Value to apply
//     */
//    fun heading(view: View, isHeading: Boolean = true): View {
//        accessibilityDelegate(view) { _, info ->
//            info.isHeading = isHeading
//        }
//        return view
//    }
//
//    /**
//     * Extension to mark the given view as accessibility heading
//     *
//     * @param isHeading Value to apply
//     */
//    fun View.setAsAccessibilityHeading(isHeading: Boolean = true): View {
//        return heading(this, isHeading)
//    }
//
//    /**
//     * Helper method to mark a view as accessibility button
//     *
//     * @param view View to mark
//     * @param isButton Value to apply
//     */
//    fun button(view: View, isButton: Boolean = true): View {
//        accessibilityDelegate(view) { _, info ->
//            info.className = if (isButton) {
//                Button::class.java.name
//            } else {
//                this::class.java.name
//            }
//        }
//        return view
//    }
//
//    /**
//     * Extension to mark the given view as accessibility button
//     *
//     * @param isButton Value to apply
//     */
//    fun View.setAsAccessibilityButton(isButton: Boolean = true): View {
//        return button(this, isButton)
//    }
//}