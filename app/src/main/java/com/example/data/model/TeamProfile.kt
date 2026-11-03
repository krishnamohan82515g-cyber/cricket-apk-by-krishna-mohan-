package com.example.data.model

data class Trophy(
    val title: String,
    val year: String,
    val description: String,
    val opponentBeaten: String
)

data class TeamProfile(
    val name: String = "Rampur Tigers Cricket Club",
    val motto: String = "Gramin Josh, Champion Spirit!",
    val villageName: String = "Rampur Village",
    val establishedYear: String = "2014",
    val homeGround: String = "Gram Panchayat Sports Ground, Rampur",
    val captain: String = "Ramesh Patel",
    val viceCaptain: String = "Suresh Kumar",
    val coach: String = "Guruji Masterji",
    val totalMatches: Int = 34,
    val wins: Int = 26,
    val losses: Int = 8,
    val trophiesWon: Int = 5,
    val fanCount: Int = 482,
    val isUserFanMember: Boolean = false,
    val trophies: List<Trophy> = listOf(
        Trophy("Gramin Premier League (GPL)", "2025", "T20 Champions against Shyamnagar", "Won by 24 runs"),
        Trophy("Diwali Super Cup", "2024", "Annual Inter-Village 10-Over Tournament", "Won by 6 wickets"),
        Trophy("Independence Day Trophy", "2024", "15-Over Knockout Championship", "Won by 43 runs"),
        Trophy("Panchayat Champions Shield", "2023", "District Village Sports Meet", "Won by 5 wickets"),
        Trophy("Monsoon Leather Ball Cup", "2022", "Regional Village Invitational", "Won by 18 runs")
    )
) {
    val winPercentage: Double
        get() = if (totalMatches > 0) (wins.toDouble() / totalMatches) * 100.0 else 0.0
}
