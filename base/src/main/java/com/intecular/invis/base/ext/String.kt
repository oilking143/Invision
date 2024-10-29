package com.intecular.invis.base.ext

import com.intecular.invis.base.ApiHeader

fun String.formatApiHeader(): String {
    return "${ApiHeader.COMMON_HEADER_PREFIX.content}${this}"
}