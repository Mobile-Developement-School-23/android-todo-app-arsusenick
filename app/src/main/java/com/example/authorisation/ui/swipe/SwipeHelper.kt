package com.example.authorisation.ui.swipe

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper

class SwipeHelper(
    swipeCallback: SwipeInterface,
    context: Context
) : ItemTouchHelper(Swipe(swipeCallback, context))
