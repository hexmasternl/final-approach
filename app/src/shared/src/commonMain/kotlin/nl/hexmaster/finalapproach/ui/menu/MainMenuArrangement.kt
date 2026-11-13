package nl.hexmaster.finalapproach.ui.menu

import androidx.compose.ui.unit.Dp

/** How the wordmark and the menu share the main page. */
enum class MainMenuArrangement {
    /** Regular wordmark above the menu. */
    Stacked,

    /** Small wordmark to the left of the menu, for compact heights. */
    Beside,
}

/** Stacked when the regular wordmark, the gap and the menu fit the available height. */
fun chooseMainMenuArrangement(availableHeight: Dp, stackedHeight: Dp): MainMenuArrangement =
    if (stackedHeight <= availableHeight) MainMenuArrangement.Stacked else MainMenuArrangement.Beside
