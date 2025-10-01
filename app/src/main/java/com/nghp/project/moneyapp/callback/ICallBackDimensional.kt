package com.nmh.base_lib.callback

import com.nghp.project.moneyapp.callback.ICallBackCheck
import com.nghp.project.moneyapp.callback.ICallBackItem

interface ICallBackDimensional {
    fun callBackItem(objects: Any, callBackItem: ICallBackItem)

    fun callBackCheck(objects: Any, check: ICallBackCheck)
}