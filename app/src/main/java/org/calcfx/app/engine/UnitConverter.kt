package org.calcfx.app.engine

/**
 * 40 Standard Scientific Metric & Unit Conversions (matching Casio CONV mode).
 */
data class ConversionPair(
    val code: Int,
    val fromUnit: String,
    val toUnit: String,
    val factor: Double,
    val category: String,
    val offset: Double = 0.0 // for temperature conversions
)

object UnitConverter {
    val ALL = listOf(
        ConversionPair(1, "in", "cm", 2.54, "Length"),
        ConversionPair(2, "cm", "in", 1.0 / 2.54, "Length"),
        ConversionPair(3, "ft", "m", 0.3048, "Length"),
        ConversionPair(4, "m", "ft", 1.0 / 0.3048, "Length"),
        ConversionPair(5, "yd", "m", 0.9144, "Length"),
        ConversionPair(6, "m", "yd", 1.0 / 0.9144, "Length"),
        ConversionPair(7, "mile", "km", 1.609344, "Length"),
        ConversionPair(8, "km", "mile", 1.0 / 1.609344, "Length"),
        ConversionPair(9, "n mile", "m", 1852.0, "Length"),
        ConversionPair(10, "m", "n mile", 1.0 / 1852.0, "Length"),
        ConversionPair(11, "acre", "m²", 4046.8564224, "Area"),
        ConversionPair(12, "m²", "acre", 1.0 / 4046.8564224, "Area"),
        ConversionPair(13, "gal (US)", "L", 3.785411784, "Volume"),
        ConversionPair(14, "L", "gal (US)", 1.0 / 3.785411784, "Volume"),
        ConversionPair(15, "gal (UK)", "L", 4.54609, "Volume"),
        ConversionPair(16, "L", "gal (UK)", 1.0 / 4.54609, "Volume"),
        ConversionPair(17, "pc", "km", 3.08567758149e13, "Length"),
        ConversionPair(18, "km", "pc", 1.0 / 3.08567758149e13, "Length"),
        ConversionPair(19, "km/h", "m/s", 1.0 / 3.6, "Speed"),
        ConversionPair(20, "m/s", "km/h", 3.6, "Speed"),
        ConversionPair(21, "oz", "g", 28.349523125, "Mass"),
        ConversionPair(22, "g", "oz", 1.0 / 28.349523125, "Mass"),
        ConversionPair(23, "lb", "kg", 0.45359237, "Mass"),
        ConversionPair(24, "kg", "lb", 1.0 / 0.45359237, "Mass"),
        ConversionPair(25, "atm", "Pa", 101325.0, "Pressure"),
        ConversionPair(26, "Pa", "atm", 1.0 / 101325.0, "Pressure"),
        ConversionPair(27, "bar", "Pa", 100000.0, "Pressure"),
        ConversionPair(28, "Pa", "bar", 1.0 / 100000.0, "Pressure"),
        ConversionPair(29, "mmHg", "Pa", 133.322387415, "Pressure"),
        ConversionPair(30, "Pa", "mmHg", 1.0 / 133.322387415, "Pressure"),
        ConversionPair(31, "hp", "kW", 0.74569987158227022, "Power"),
        ConversionPair(32, "kW", "hp", 1.0 / 0.74569987158227022, "Power"),
        ConversionPair(33, "kgf/cm²", "Pa", 98066.5, "Pressure"),
        ConversionPair(34, "Pa", "kgf/cm²", 1.0 / 98066.5, "Pressure"),
        ConversionPair(35, "kgf m", "J", 9.80665, "Energy"),
        ConversionPair(36, "J", "kgf m", 1.0 / 9.80665, "Energy"),
        ConversionPair(37, "lbf/in²", "kPa", 6.894757293168, "Pressure"),
        ConversionPair(38, "kPa", "lbf/in²", 1.0 / 6.894757293168, "Pressure"),
        ConversionPair(39, "°F", "°C", 5.0 / 9.0, "Temperature", -32.0),
        ConversionPair(40, "°C", "°F", 9.0 / 5.0, "Temperature", 32.0)
    )

    fun convert(value: Double, code: Int): Double {
        val pair = ALL.find { it.code == code } ?: return value
        return if (pair.category == "Temperature") {
            if (code == 39) {
                (value + pair.offset) * pair.factor
            } else {
                value * pair.factor + pair.offset
            }
        } else {
            value * pair.factor
        }
    }
}
