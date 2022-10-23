package com.droidsmith.tireguide.data

enum class TireWidth constructor(val displayName: String) {
    TWENTY("20"),
    TWENTY_ONE("21"),
    TWENTY_TWO("22"),
    TWENTY_THREE("23"),
    TWENTY_FOUR("24"),
    TWENTY_FIVE("25"),
    TWENTY_SIX("26"),
    TWENTY_SEVEN("27"),
    TWENTY_EIGHT("28");

    companion object {
        fun getWidthFromDisplayName(displayName: String?): TireWidth? =
            values().firstOrNull { it.displayName.equals(displayName, ignoreCase = true) }

        fun getWidthsAsStrings(): Array<String> = values().map { it.displayName }.toTypedArray()
    }
}