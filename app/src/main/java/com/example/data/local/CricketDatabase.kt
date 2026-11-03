package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PlayerEntity::class,
        MatchEntity::class,
        PostEntity::class,
        HighlightEntity::class,
        UserEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CricketDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun postDao(): PostDao
    abstract fun highlightDao(): HighlightDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: CricketDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CricketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CricketDatabase::class.java,
                    "village_cricket_v2.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance

                scope.launch(Dispatchers.IO) {
                    try {
                        checkAndPopulateData(instance)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                instance
            }
        }

        private suspend fun checkAndPopulateData(db: CricketDatabase) {
            try {
                val existing = db.playerDao().getPlayerById("p1")
                if (existing == null) {
                    populateInitialData(db)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private suspend fun populateInitialData(db: CricketDatabase) {
            val initialPlayers = listOf(
                PlayerEntity(
                    id = "p1",
                    name = "Ramesh Patel",
                    nickname = "Gully King",
                    jerseyNumber = 7,
                    role = "ALL_ROUNDER",
                    battingStyle = "Right-hand bat",
                    bowlingStyle = "Right-arm medium fast",
                    isCaptain = true,
                    isViceCaptain = false,
                    matches = 28,
                    innings = 26,
                    runs = 942,
                    highestScore = 104,
                    ballsFaced = 640,
                    fifties = 6,
                    hundreds = 2,
                    wickets = 34,
                    oversBowled = 88.0,
                    runsConceded = 510,
                    catches = 16
                ),
                PlayerEntity(
                    id = "p2",
                    name = "Suresh Kumar",
                    nickname = "Yorker King",
                    jerseyNumber = 18,
                    role = "BOWLER",
                    battingStyle = "Right-hand bat",
                    bowlingStyle = "Right-arm express fast",
                    isCaptain = false,
                    isViceCaptain = true,
                    matches = 26,
                    innings = 14,
                    runs = 210,
                    highestScore = 38,
                    ballsFaced = 150,
                    fifties = 0,
                    hundreds = 0,
                    wickets = 48,
                    oversBowled = 96.0,
                    runsConceded = 492,
                    catches = 12
                ),
                PlayerEntity(
                    id = "p3",
                    name = "Deepak Yadav",
                    nickname = "Chhotu",
                    jerseyNumber = 1,
                    role = "WICKET_KEEPER",
                    battingStyle = "Right-hand bat",
                    bowlingStyle = "None",
                    isCaptain = false,
                    isViceCaptain = false,
                    matches = 24,
                    innings = 23,
                    runs = 680,
                    highestScore = 86,
                    ballsFaced = 475,
                    fifties = 4,
                    hundreds = 0,
                    wickets = 0,
                    oversBowled = 0.0,
                    runsConceded = 0,
                    catches = 24
                ),
                PlayerEntity(
                    id = "p4",
                    name = "Munna Tiwari",
                    nickname = "Sixer Machine",
                    jerseyNumber = 45,
                    role = "BATSMAN",
                    battingStyle = "Left-hand bat",
                    bowlingStyle = "Right-arm off break",
                    isCaptain = false,
                    isViceCaptain = false,
                    matches = 22,
                    innings = 22,
                    runs = 810,
                    highestScore = 92,
                    ballsFaced = 520,
                    fifties = 7,
                    hundreds = 0,
                    wickets = 6,
                    oversBowled = 18.0,
                    runsConceded = 120,
                    catches = 9
                ),
                PlayerEntity(
                    id = "p5",
                    name = "Bablu Sharma",
                    nickname = "Doosra Master",
                    jerseyNumber = 99,
                    role = "BOWLER",
                    battingStyle = "Right-hand bat",
                    bowlingStyle = "Right-arm off spin",
                    isCaptain = false,
                    isViceCaptain = false,
                    matches = 20,
                    innings = 11,
                    runs = 95,
                    highestScore = 22,
                    ballsFaced = 80,
                    fifties = 0,
                    hundreds = 0,
                    wickets = 39,
                    oversBowled = 74.0,
                    runsConceded = 360,
                    catches = 8
                ),
                PlayerEntity(
                    id = "p6",
                    name = "Amit Verma",
                    nickname = "The Finisher",
                    jerseyNumber = 12,
                    role = "BATSMAN",
                    battingStyle = "Right-hand bat",
                    bowlingStyle = "Right-arm medium",
                    isCaptain = false,
                    isViceCaptain = false,
                    matches = 19,
                    innings = 17,
                    runs = 540,
                    highestScore = 68,
                    ballsFaced = 325,
                    fifties = 3,
                    hundreds = 0,
                    wickets = 4,
                    oversBowled = 12.0,
                    runsConceded = 95,
                    catches = 7
                ),
                PlayerEntity(
                    id = "p7",
                    name = "Vikas Singh",
                    nickname = "Cheetah",
                    jerseyNumber = 33,
                    role = "ALL_ROUNDER",
                    battingStyle = "Right-hand bat",
                    bowlingStyle = "Right-arm fast medium",
                    isCaptain = false,
                    isViceCaptain = false,
                    matches = 18,
                    innings = 15,
                    runs = 380,
                    highestScore = 49,
                    ballsFaced = 260,
                    fifties = 0,
                    hundreds = 0,
                    wickets = 21,
                    oversBowled = 52.0,
                    runsConceded = 310,
                    catches = 15
                ),
                PlayerEntity(
                    id = "p8",
                    name = "Manoj Gurjar",
                    nickname = "Speedster",
                    jerseyNumber = 24,
                    role = "BOWLER",
                    battingStyle = "Left-hand bat",
                    bowlingStyle = "Left-arm fast",
                    isCaptain = false,
                    isViceCaptain = false,
                    matches = 15,
                    innings = 8,
                    runs = 84,
                    highestScore = 26,
                    ballsFaced = 65,
                    fifties = 0,
                    hundreds = 0,
                    wickets = 28,
                    oversBowled = 58.0,
                    runsConceded = 320,
                    catches = 5
                )
            )
            db.playerDao().insertAllPlayers(initialPlayers)

            val initialMatches = listOf(
                MatchEntity(
                    id = "m1",
                    tournamentName = "Gramin Premier League 2026 (Final)",
                    opponentTeam = "Shyamnagar Strikers",
                    date = "Today",
                    time = "4:00 PM",
                    venue = "Panchayat Stadium, Rampur",
                    oversLimit = 20,
                    status = "LIVE",
                    tossResult = "Rampur Tigers won the toss & elected to bat",
                    teamBattingFirst = true,
                    teamScore = 142,
                    teamWickets = 3,
                    teamOvers = 15.4,
                    opponentScore = 0,
                    opponentWickets = 0,
                    opponentOvers = 0.0,
                    resultText = "Match in progress (1st Innings)",
                    playerOfTheMatch = ""
                ),
                MatchEntity(
                    id = "m2",
                    tournamentName = "Village Champions Trophy",
                    opponentTeam = "Belgaon Kings",
                    date = "Sun, Nov 10",
                    time = "9:30 AM",
                    venue = "Rampur Village Ground",
                    oversLimit = 20,
                    status = "UPCOMING",
                    tossResult = "",
                    teamBattingFirst = true,
                    teamScore = 0,
                    teamWickets = 0,
                    teamOvers = 0.0,
                    opponentScore = 0,
                    opponentWickets = 0,
                    opponentOvers = 0.0,
                    resultText = "Match scheduled",
                    playerOfTheMatch = ""
                ),
                MatchEntity(
                    id = "m3",
                    tournamentName = "Diwali Super 10 Blast",
                    opponentTeam = "Navi Gaon Warriors",
                    date = "Thu, Nov 14",
                    time = "2:30 PM",
                    venue = "Panchayat Maidan",
                    oversLimit = 10,
                    status = "UPCOMING",
                    tossResult = "",
                    teamBattingFirst = true,
                    teamScore = 0,
                    teamWickets = 0,
                    teamOvers = 0.0,
                    opponentScore = 0,
                    opponentWickets = 0,
                    opponentOvers = 0.0,
                    resultText = "Fixture announced",
                    playerOfTheMatch = ""
                ),
                MatchEntity(
                    id = "m4",
                    tournamentName = "GPL 2026 (Semi-Final)",
                    opponentTeam = "Kisan Nagar XI",
                    date = "Oct 28, 2026",
                    time = "10:00 AM",
                    venue = "Rampur Village Ground",
                    oversLimit = 20,
                    status = "COMPLETED",
                    tossResult = "Kisan Nagar XI won toss & elected to bowl",
                    teamBattingFirst = true,
                    teamScore = 174,
                    teamWickets = 6,
                    teamOvers = 20.0,
                    opponentScore = 136,
                    opponentWickets = 9,
                    opponentOvers = 20.0,
                    resultText = "Rampur Tigers won by 38 runs",
                    playerOfTheMatch = "Ramesh Patel (72 runs & 3 wickets)"
                ),
                MatchEntity(
                    id = "m5",
                    tournamentName = "Independence Day Shield",
                    opponentTeam = "Fatehpur Strikers",
                    date = "Aug 15, 2026",
                    time = "1:30 PM",
                    venue = "Fatehpur Stadium",
                    oversLimit = 15,
                    status = "COMPLETED",
                    tossResult = "Fatehpur won toss & elected to bat",
                    teamBattingFirst = false,
                    teamScore = 146,
                    teamWickets = 5,
                    teamOvers = 14.2,
                    opponentScore = 145,
                    opponentWickets = 8,
                    opponentOvers = 15.0,
                    resultText = "Rampur Tigers won by 5 wickets",
                    playerOfTheMatch = "Deepak Yadav (54* off 28 balls)"
                )
            )
            db.matchDao().insertAllMatches(initialMatches)

            val initialHighlights = listOf(
                HighlightEntity(
                    id = "h1",
                    title = "Ramesh Patel's 105m helicopter six into the mango orchard!",
                    description = "Last over heroics in GPL semi-final! Huge roar from the village crowd on the banyan tree.",
                    mediaUri = null,
                    mediaType = "VIDEO",
                    duration = "0:35",
                    viewsCount = 540,
                    likesCount = 86,
                    isLiked = true,
                    matchTag = "GPL Semi-Final",
                    playerTag = "Ramesh Patel",
                    timestamp = System.currentTimeMillis() - 86400000L
                ),
                HighlightEntity(
                    id = "h2",
                    title = "Suresh Kumar's lethal toe-crusher yorker cleans up middle stump",
                    description = "Clocked at raw pace! Opposition captain had no answer to this reverse-swinging yorker.",
                    mediaUri = null,
                    mediaType = "VIDEO",
                    duration = "0:24",
                    viewsCount = 412,
                    likesCount = 67,
                    isLiked = false,
                    matchTag = "GPL 2026",
                    playerTag = "Suresh Kumar",
                    timestamp = System.currentTimeMillis() - 172800000L
                ),
                HighlightEntity(
                    id = "h3",
                    title = "Deepak Yadav's gravity-defying one-handed diving catch",
                    description = "Full stretch dive behind the wickets! Village fans calling it catch of the tournament.",
                    mediaUri = null,
                    mediaType = "VIDEO",
                    duration = "0:30",
                    viewsCount = 380,
                    likesCount = 54,
                    isLiked = false,
                    matchTag = "Independence Shield",
                    playerTag = "Deepak Yadav",
                    timestamp = System.currentTimeMillis() - 259200000L
                ),
                HighlightEntity(
                    id = "h4",
                    title = "Grand Trophy Celebration with Dhol & Gulal at Village Chaupal",
                    description = "The whole village gathered to celebrate the victory. True grassroots cricket passion!",
                    mediaUri = null,
                    mediaType = "PHOTO",
                    duration = "Photo",
                    viewsCount = 720,
                    likesCount = 145,
                    isLiked = true,
                    matchTag = "Champions",
                    playerTag = "Team Rampur",
                    timestamp = System.currentTimeMillis() - 345600000L
                )
            )
            db.highlightDao().insertAllHighlights(initialHighlights)

            val initialPosts = listOf(
                PostEntity(
                    id = "post1",
                    authorName = "Ramesh Patel",
                    authorRole = "Team Captain",
                    content = "Match Day Final! 🐯 Rampur Tigers are taking on Shyamnagar Strikers in the GPL 2026 Final right now at the Panchayat Stadium. Pitch has good bounce. Keep cheering for the village team!",
                    tag = "Match Day",
                    mediaUri = null,
                    likesCount = 64,
                    isLiked = true,
                    timestamp = System.currentTimeMillis() - 3600000L,
                    serializedComments = "c1:::Raju Chaiwala:::Sending free cutting chai to the dugout! Play hard boys!:::15m ago|||c2:::Mahesh Master:::All school kids are cheering from the pavilion! Win the cup!:::30m ago"
                ),
                PostEntity(
                    id = "post2",
                    authorName = "Suresh Kumar",
                    authorRole = "Vice-Captain & Bowler",
                    content = "Trained hard at morning 5 AM nets on the turf pitch. Ball is swinging both ways today. Ready to defend any total our batsmen put up! Jai Gramin XI 🏏💪",
                    tag = "Team Spirit",
                    mediaUri = null,
                    likesCount = 48,
                    isLiked = false,
                    timestamp = System.currentTimeMillis() - 14400000L,
                    serializedComments = "c3:::Deepak Yadav:::We'll back you up behind the stumps bhai!:::2h ago"
                ),
                PostEntity(
                    id = "post3",
                    authorName = "Sarpanch Ji",
                    authorRole = "Village Patron",
                    content = "Panchayat has announced a new electric scoreboard and practice nets for our Rampur boys if they bring home the GPL trophy today! Whole village is proud of your dedication.",
                    tag = "Village Announcement",
                    mediaUri = null,
                    likesCount = 112,
                    isLiked = true,
                    timestamp = System.currentTimeMillis() - 28800000L,
                    serializedComments = "c4:::Amit Verma:::Thank you Sarpanch ji! We will give 200% on the ground.:::4h ago"
                )
            )
            db.postDao().insertAllPosts(initialPosts)

            val adminSalt = UserEntity.generateSalt()
            val playerSalt = UserEntity.generateSalt()
            val fanSalt = UserEntity.generateSalt()

            val initialUsers = listOf(
                UserEntity(
                    id = "u_krishna",
                    name = "Krishna Mohan Shukla ( Rishabh shukla )",
                    usernameOrEmail = "krishna_admin",
                    passwordHash = UserEntity.hashPassword("password123", adminSalt),
                    role = "ADMIN",
                    jerseyNumber = null,
                    playerSpecialty = "Club President & Patron",
                    phoneNumber = "+91 98765 43210",
                    villageName = "Rampur Village",
                    bio = "Village Cricket Club President and Chief Patron. Dedicated to grassroots rural sports development.",
                    avatarColorHex = "#D97706",
                    joinedAt = System.currentTimeMillis() - 31536000000L,
                    salt = adminSalt,
                    failedAttempts = 0,
                    lockedUntil = 0L,
                    securityPin = "1234"
                ),
                UserEntity(
                    id = "u_player",
                    name = "Ramesh Patel",
                    usernameOrEmail = "ramesh_player",
                    passwordHash = UserEntity.hashPassword("password123", playerSalt),
                    role = "PLAYER",
                    jerseyNumber = 7,
                    playerSpecialty = "All-Rounder (Captain)",
                    phoneNumber = "+91 98222 11111",
                    villageName = "Rampur Village",
                    bio = "Right-hand bat, medium pace bowling. Passionate about grassroots village cricket.",
                    avatarColorHex = "#1B5E20",
                    joinedAt = System.currentTimeMillis() - 15768000000L,
                    salt = playerSalt,
                    failedAttempts = 0,
                    lockedUntil = 0L
                ),
                UserEntity(
                    id = "u_fan",
                    name = "Raju Chaiwala",
                    usernameOrEmail = "raju_fan",
                    passwordHash = UserEntity.hashPassword("password123", fanSalt),
                    role = "FAN",
                    jerseyNumber = null,
                    playerSpecialty = "12th Man Army Leader",
                    phoneNumber = "+91 97333 44444",
                    villageName = "Rampur Village",
                    bio = "Lifelong Rampur Tigers supporter! Free cutting chai when the boys hit a sixer!",
                    avatarColorHex = "#B91C1C",
                    joinedAt = System.currentTimeMillis() - 7884000000L,
                    salt = fanSalt,
                    failedAttempts = 0,
                    lockedUntil = 0L
                )
            )
            db.userDao().insertAllUsers(initialUsers)
        }
    }
}
