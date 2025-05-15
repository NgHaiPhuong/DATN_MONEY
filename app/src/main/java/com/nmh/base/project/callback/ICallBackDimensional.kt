package com.nmh.base_lib.callback

import com.nmh.base.project.callback.ICallBackCheck
import com.nmh.base.project.callback.ICallBackItem

interface ICallBackDimensional {
    fun callBackItem(objects: Any, callBackItem: ICallBackItem)

    fun callBackCheck(objects: Any, check: ICallBackCheck)
}