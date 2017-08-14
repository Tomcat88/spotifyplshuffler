package runtime.wrappers

external fun require(module: String): dynamic

inline fun <T> jsObject(builder: T.() -> Unit): T {
    val obj: T = js("({})")
    return obj.apply {
        builder()
    }
}

inline fun js(builder: dynamic.() -> Unit): dynamic = jsObject(builder)

fun Any.getOwnPropertyNames(): Array<String> {
    val me = this
    return js("Object.getOwnPropertyNames(me)")
}

fun toPlainObjectStripNull(me: Any): dynamic {
    val obj = js("({})")
    for (p in me.getOwnPropertyNames().filterNot { it == "__proto__" || it == "constructor" }) {
        js("if (me[p] != null) { obj[p]=me[p] }")
    }
    return obj
}

fun jsstyle(builder: dynamic.() -> Unit): String = js(builder)

fun msToHMS(ms: Int): String {
    // 1- Convert to seconds:
    var seconds = ms / 1000
    // 2- Extract hours:
    val hours = seconds / 3600 // 3,600 seconds in 1 hour
    seconds %= 3600            // seconds remaining after extracting hours
    // 3- Extract minutes:
    val minutes = seconds / 60 // 60 seconds in 1 minute
    // 4- Keep only seconds not extracted to minutes:
    seconds %= 60
    return "$minutes:$seconds"
}
