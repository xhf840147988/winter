package com.xhf.winter

object FloatWindowManager {
    private var switchState: Boolean = false

    fun setSwitchState(state: Boolean) {
        switchState = state
    }

    fun getSwitchState(): Boolean = switchState
}