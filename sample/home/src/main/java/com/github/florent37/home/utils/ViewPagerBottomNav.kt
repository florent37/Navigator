package com.github.florent37.home.utils

import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.setupWithViewPager(viewPager: ViewPager) {
    this.setOnNavigationItemSelectedListener {
        this.findItemIndex(it.itemId).also { index ->
            if(viewPager.currentItem != index){
                viewPager.currentItem = index
            }
        }
        true
    }

    viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            this.apply {
                menu.getItem(position).itemId.also {  id ->
                    if(selectedItemId != id)
                        selectedItemId = id
                }
            }
        }
    })
}

fun BottomNavigationView.findItemIndex(id: Int): Int {
    for (i in 0 until menu.size()) {
        if (menu.getItem(i).itemId == id) {
            return i
        }
    }
    return 0
}

