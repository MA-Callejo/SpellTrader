package com.kiwistudio.spelltrader.conexion

import org.json.JSONObject

data class Response(val code: Int, val msg: String?, val body: JSONObject?)
