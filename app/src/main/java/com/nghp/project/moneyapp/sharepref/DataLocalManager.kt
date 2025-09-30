package com.nghp.project.moneyapp.sharepref

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nghp.project.moneyapp.db.model.LanguageModel
import com.nghp.project.moneyapp.db.model.TransactionModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DataLocalManager {
    private var mySharedPreferences: MySharePreferences? = null

    companion object {
        private var instance: DataLocalManager? = null
        fun init(context: Context) {
            instance = DataLocalManager()
            instance!!.mySharedPreferences = MySharePreferences(context)
        }

        private fun getInstance(): DataLocalManager? {
            if (instance == null) instance = DataLocalManager()
            return instance
        }

        fun setFirstInstall(key: String?, isFirst: Boolean) {
            getInstance()!!.mySharedPreferences!!.putBooleanValue(key, isFirst)
        }

        fun getFirstInstall(key: String?): Boolean {
            return getInstance()!!.mySharedPreferences!!.getBooleanValue(key, true)
        }

        fun setBoolean(key: String, isFirst: Boolean) {
            getInstance()!!.mySharedPreferences!!.putBooleanValue(key, isFirst)
        }

        fun getBoolean(key: String, default: Boolean): Boolean {
            return getInstance()!!.mySharedPreferences!!.getBooleanValue(key, default)
        }

        fun setOption(option: String?, key: String?) {
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, option)
        }

        fun getOption(key: String?): String? {
            return getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")
        }

        fun setInt(count: Int, key: String?) {
            getInstance()!!.mySharedPreferences!!.putIntWithKey(key, count)
        }

        fun getInt(key: String?): Int {
            return getInstance()!!.mySharedPreferences!!.getIntWithKey(key, -1)
        }

        fun setLong(count: Long, key: String?) {
            getInstance()!!.mySharedPreferences!!.putLongWithKey(key, count)
        }

        fun getLong(key: String?): Long {
            return getInstance()!!.mySharedPreferences!!.getLongWithKey(key, -1L)
        }

        fun setLanguage(key: String, lang: LanguageModel) {
            getInstance()!!.mySharedPreferences?.putStringWithKey(
                key,
                Gson().toJsonTree(lang).asJsonObject.toString()
            )
        }

        fun getLanguage(key: String): LanguageModel? {
            val strJson = getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")!!
            var lang: LanguageModel? = null
            try {
                lang = Gson().fromJson(JSONObject(strJson).toString(), LanguageModel::class.java)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return lang
        }

        fun setListTimeStamp(key: String?, lstTimeStamp: ArrayList<String?>?) {
            val gson = Gson()
            val jsonArray = gson.toJsonTree(lstTimeStamp).asJsonArray
            val json = jsonArray.toString()
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, json)
        }

        fun getListTimeStamp(key: String?): ArrayList<String> {
            val lstTimeStamp = ArrayList<String>()
            val strJson = getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")
            try {
                val jsonArray = JSONArray(strJson)
                for (i in 0 until jsonArray.length()) {
                    lstTimeStamp.add(jsonArray[i].toString())
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return lstTimeStamp
        }

//        fun setListTransactionModel(lstTransaction: MutableList<TransactionModel>, key: String) {
//            getInstance()!!.mySharedPreferences!!.putStringWithKey(
//                key,
//                Gson().toJsonTree(lstTransaction).asJsonArray.toString()
//            )
//        }
//
//        fun getListTransactionModel(key: String): MutableList<TransactionModel> {
//            val lstTransaction = mutableListOf<TransactionModel>()
//            try {
//                val jsonArray =
//                    JSONArray(getInstance()!!.mySharedPreferences!!.getStringWithKey(key, ""))
//                if (jsonArray.length() != 0)
//                    for (i in 0 until jsonArray.length())
//                        lstTransaction.add(
//                            Gson().fromJson(
//                                jsonArray.getJSONObject(i).toString(),
//                                object : TypeToken<TransactionModel>() {}.type
//                            )
//                        )
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//            return lstTransaction
//        }

//        fun setNotifyFake(key: String, notifyModel: NotifyModel) {
//            getInstance()!!.mySharedPreferences?.putStringWithKey(key, Gson().toJsonTree(notifyModel).asJsonObject.toString())
//        }
//
//        fun getNotifyFake(key: String): NotifyModel {
//            val strJson = getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")!!
//            var notifyModel = NotifyModel()
//            try {
//                notifyModel = Gson().fromJson(JSONObject(strJson).toString(), NotifyModel::class.java)
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//            return notifyModel
//        }


//        fun setListBucket(lstBucket: ArrayList<BucketPicModel>, key: String) {
//            val gson = Gson()
//            val jsonArray = gson.toJsonTree(lstBucket).asJsonArray
//            val json = jsonArray.toString()
//            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, json)
//        }
//
//        fun getListBucket(key: String): ArrayList<BucketPicModel> {
//            val gson = Gson()
//            var jsonObject: JSONObject
//            val lstBucket: ArrayList<BucketPicModel> = ArrayList()
//            val strJson = getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")
//            try {
//                val jsonArray = JSONArray(strJson)
//                for (i in 0 until jsonArray.length()) {
//                    jsonObject = jsonArray.getJSONObject(i)
//                    lstBucket.add(gson.fromJson(jsonObject.toString(), BucketPicModel::class.java))
//                }
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//            return lstBucket
//        }
    }
}