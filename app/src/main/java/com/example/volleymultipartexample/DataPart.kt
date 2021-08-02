package com.example.volleymultipartexample
data class DataPart(var id: Int) {
    var fileName: String? = null
        private set
    lateinit var content: ByteArray
        private set
    val type: String? = null

    constructor() : this(0) {}
    constructor(name: String?, data: ByteArray) : this(0) {
        fileName = name
        content = data
    }

}