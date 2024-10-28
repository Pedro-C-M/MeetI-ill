package com.example.meet_ill

enum class Status{
    Received,
    Read,
    Unreceived
}
class Message(val content:String,val status:Status) {
}