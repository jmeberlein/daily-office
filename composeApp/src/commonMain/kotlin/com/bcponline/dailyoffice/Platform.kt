package com.bcponline.dailyoffice

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform