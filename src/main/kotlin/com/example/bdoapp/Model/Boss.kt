package com.example.bdoapp.Model
import java.time.DayOfWeek
import java.time.LocalTime

data class BossSpawn(val day: DayOfWeek,
                val time: LocalTime,
                val bosses: List<String>)
val bossSchedule = listOf(
    // Monday
    BossSpawn(DayOfWeek.MONDAY, LocalTime.of(0, 15), listOf("Kutum", "Karanda", "Uturi")),
    BossSpawn(DayOfWeek.MONDAY, LocalTime.of(2, 0), listOf("Karanda", "Bulgasal")),
    BossSpawn(DayOfWeek.MONDAY, LocalTime.of(5, 0), listOf("Kzarka")),
    BossSpawn(DayOfWeek.MONDAY, LocalTime.of(8, 0), listOf("Kzarka")),
    BossSpawn(DayOfWeek.MONDAY, LocalTime.of(12, 0), listOf("Offin")),
    BossSpawn(DayOfWeek.MONDAY, LocalTime.of(14, 0), listOf("Garmoth")),
    BossSpawn(DayOfWeek.MONDAY, LocalTime.of(16, 0), listOf("Kutum", "Uturi")),
    BossSpawn(DayOfWeek.MONDAY, LocalTime.of(19, 0), listOf("Nouver", "Golden Pig King", "Bulgasal")),
    BossSpawn(DayOfWeek.MONDAY, LocalTime.of(22, 15), listOf("Kzarka", "Sangoon", "Uturi")),
    BossSpawn(DayOfWeek.MONDAY, LocalTime.of(23, 15), listOf("Garmoth")),

    // Tuesday
    BossSpawn(DayOfWeek.TUESDAY, LocalTime.of(0, 15), listOf("Karanda", "Golden Pig King")),
    BossSpawn(DayOfWeek.TUESDAY, LocalTime.of(2, 0), listOf("Kutum")),
    BossSpawn(DayOfWeek.TUESDAY, LocalTime.of(5, 0), listOf("Kzarka")),
    BossSpawn(DayOfWeek.TUESDAY, LocalTime.of(8, 0), listOf("Nouver")),
    BossSpawn(DayOfWeek.TUESDAY, LocalTime.of(12, 0), listOf("Kutum")),
    BossSpawn(DayOfWeek.TUESDAY, LocalTime.of(14, 0), listOf("Garmoth")),
    BossSpawn(DayOfWeek.TUESDAY, LocalTime.of(16, 0), listOf("Nouver", "Golden Pig King")),
    BossSpawn(DayOfWeek.TUESDAY, LocalTime.of(19, 0), listOf("Karanda", "Bulgasal", "Uturi")),
    BossSpawn(DayOfWeek.TUESDAY, LocalTime.of(22, 15), listOf("Quint", "Muraka")),
    BossSpawn(DayOfWeek.TUESDAY, LocalTime.of(23, 15), listOf("Garmoth")),

    // Wednesday
    BossSpawn(DayOfWeek.WEDNESDAY, LocalTime.of(0, 15), listOf("Kzarka", "Kutum", "Bulgasal")),
    BossSpawn(DayOfWeek.WEDNESDAY, LocalTime.of(2, 0), listOf("Karanda", "Golden Pig King")),
    BossSpawn(DayOfWeek.WEDNESDAY, LocalTime.of(5, 0), listOf("Kzarka")),
    BossSpawn(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), listOf("Karanda")),
    BossSpawn(DayOfWeek.WEDNESDAY, LocalTime.of(12, 0), listOf("Nouver")),
    BossSpawn(DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), listOf("Garmoth")),
    BossSpawn(DayOfWeek.WEDNESDAY, LocalTime.of(16, 0), listOf("Kutum", "Offin", "Bulgasal")),
    BossSpawn(DayOfWeek.WEDNESDAY, LocalTime.of(19, 0), listOf("Vell")),
    BossSpawn(DayOfWeek.WEDNESDAY, LocalTime.of(22, 15), listOf("Kzarka", "Sangoon")),
    BossSpawn(DayOfWeek.WEDNESDAY, LocalTime.of(23, 15), listOf("Garmoth")),

    // Thursday
    BossSpawn(DayOfWeek.THURSDAY, LocalTime.of(0, 15), listOf("Nouver", "Bulgasal")),
    BossSpawn(DayOfWeek.THURSDAY, LocalTime.of(2, 0), listOf("Kutum")),
    BossSpawn(DayOfWeek.THURSDAY, LocalTime.of(5, 0), listOf("Nouver")),
    BossSpawn(DayOfWeek.THURSDAY, LocalTime.of(8, 0), listOf("Kutum")),
    BossSpawn(DayOfWeek.THURSDAY, LocalTime.of(14, 0), listOf("Garmoth")),
    BossSpawn(DayOfWeek.THURSDAY, LocalTime.of(16, 0), listOf("Kzarka", "Uturi")),
    BossSpawn(DayOfWeek.THURSDAY, LocalTime.of(19, 0), listOf("Kutum", "Sangoon", "Bulgasal")),
    BossSpawn(DayOfWeek.THURSDAY, LocalTime.of(22, 15), listOf("Quint", "Muraka")),
    BossSpawn(DayOfWeek.THURSDAY, LocalTime.of(23, 15), listOf("Garmoth")),

    // Friday
    BossSpawn(DayOfWeek.FRIDAY, LocalTime.of(0, 15), listOf("Karanda", "Sangoon")),
    BossSpawn(DayOfWeek.FRIDAY, LocalTime.of(2, 0), listOf("Nouver", "Bulgasal")),
    BossSpawn(DayOfWeek.FRIDAY, LocalTime.of(5, 0), listOf("Karanda")),
    BossSpawn(DayOfWeek.FRIDAY, LocalTime.of(8, 0), listOf("Kutum")),
    BossSpawn(DayOfWeek.FRIDAY, LocalTime.of(12, 0), listOf("Karanda")),
    BossSpawn(DayOfWeek.FRIDAY, LocalTime.of(14, 0), listOf("Garmoth")),
    BossSpawn(DayOfWeek.FRIDAY, LocalTime.of(16, 0), listOf("Nouver", "Uturi")),
    BossSpawn(DayOfWeek.FRIDAY, LocalTime.of(19, 0), listOf("Kzarka", "Golden Pig King")),
    BossSpawn(DayOfWeek.FRIDAY, LocalTime.of(22, 15), listOf("Kzarka", "Kutum", "Bulgasal")),
    BossSpawn(DayOfWeek.FRIDAY, LocalTime.of(23, 15), listOf("Garmoth")),

    // Saturday
    BossSpawn(DayOfWeek.SATURDAY, LocalTime.of(0, 15), listOf("Karanda", "Golden Pig King", "Sangoon")),
    BossSpawn(DayOfWeek.SATURDAY, LocalTime.of(2, 0), listOf("Offin", "Golden Pig King", "Bulgasal")),
    BossSpawn(DayOfWeek.SATURDAY, LocalTime.of(5, 0), listOf("Nouver")),
    BossSpawn(DayOfWeek.SATURDAY, LocalTime.of(8, 0), listOf("Kutum")),
    BossSpawn(DayOfWeek.SATURDAY, LocalTime.of(12, 0), listOf("Nouver")),
    BossSpawn(DayOfWeek.SATURDAY, LocalTime.of(14, 0), listOf("Garmoth")),
    BossSpawn(DayOfWeek.SATURDAY, LocalTime.of(16, 0), listOf("Black Shadow")),
    BossSpawn(DayOfWeek.SATURDAY, LocalTime.of(19, 0), listOf("Karanda", "Kzarka", "Bulgasal", "Sangoon")),
    BossSpawn(DayOfWeek.SATURDAY, LocalTime.of(23, 15), listOf("Garmoth")),

    // Sunday
    BossSpawn(DayOfWeek.SUNDAY, LocalTime.of(0, 15), listOf("Nouver", "Kutum", "Golden Pig King", "Uturi")),
    BossSpawn(DayOfWeek.SUNDAY, LocalTime.of(2, 0), listOf("Kzarka", "Sangoon")),
    BossSpawn(DayOfWeek.SUNDAY, LocalTime.of(5, 0), listOf("Kutum")),
    BossSpawn(DayOfWeek.SUNDAY, LocalTime.of(8, 0), listOf("Nouver")),
    BossSpawn(DayOfWeek.SUNDAY, LocalTime.of(12, 0), listOf("Kzarka")),
    BossSpawn(DayOfWeek.SUNDAY, LocalTime.of(14, 0), listOf("Garmoth")),
    BossSpawn(DayOfWeek.SUNDAY, LocalTime.of(16, 0), listOf("Vell")),
    BossSpawn(DayOfWeek.SUNDAY, LocalTime.of(19, 15), listOf("Garmoth")),
    BossSpawn(DayOfWeek.SUNDAY, LocalTime.of(22, 15), listOf("Kzarka", "Nouver", "Golden Pig King", "Sangoon"))
)

