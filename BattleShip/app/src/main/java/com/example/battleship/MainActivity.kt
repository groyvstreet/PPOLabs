package com.example.battleship

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.example.battleship.components.BattleShipScaffold
import com.example.battleship.models.Game
import com.example.battleship.models.Room
import com.example.battleship.models.Ship
import com.example.battleship.models.User
import com.example.battleship.ui.theme.BattleShipTheme
import com.example.battleship.ui.theme.PrimaryColor
import com.example.battleship.viewModels.BattleShipViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.fixedRateTimer

class MainActivity : ComponentActivity() {

    private var database = FirebaseDatabase.getInstance()
    private var storage = FirebaseStorage.getInstance()
    private var auth = FirebaseAuth.getInstance()
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var roomRef: DatabaseReference
    private lateinit var onlineRef: DatabaseReference
    private lateinit var writeTimer: Timer
    private lateinit var readTimer: Timer
    private lateinit var connectionTimer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    BattleShipViewModel.isLoading = true
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.i("ApiException: ", "${e.message}")
            }
        }

        super.onCreate(savedInstanceState)
        setContent {
            BattleShipTheme {
                val viewModel: BattleShipViewModel by viewModels()

                load(viewModel)

                viewModel.signInWithGoogle = ::signInWithGoogle
                viewModel.signOut = ::signOut
                viewModel.selectImg = ::selectImg
                viewModel.updateProfile = ::updateProfile
                viewModel.signUpWithEmailAndPassword = ::signUpWithEmailAndPassword
                viewModel.signInWithEmailAndPassword = ::signInWithEmailAndPassword
                viewModel.createRoom = ::createRoom
                viewModel.enterRoom = ::enterRoom
                viewModel.attackEnemy = ::attackEnemy
                viewModel.leaveRoom = ::leaveRoom
                viewModel.loadGame = ::loadGame
                viewModel.deleteRoom = ::deleteRoom
                viewModel.addShip = ::addShip
                viewModel.dropShip = ::dropShip
                viewModel.setReady = ::setReady
                viewModel.load = ::load

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PrimaryColor
                ) {
                    BattleShipScaffold(viewModel)
                }
            }
        }
    }

    private fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        if (wifiInfo != null && wifiInfo.isConnected) {
            return true
        }

        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        if (wifiInfo != null && wifiInfo.isConnected) {
            return true
        }

        wifiInfo = cm.activeNetworkInfo
        return wifiInfo != null && wifiInfo.isConnected
    }

    private fun load(viewModel: BattleShipViewModel) {
        if (BattleShipViewModel.currentUser != null) {
            viewModel.isProfileLoading = true
            BattleShipViewModel.isAuth = true
            BattleShipViewModel.email = auth.currentUser!!.email!!

            if (!hasConnection(this)) {
                viewModel.isProfileLoading = false
                Toast.makeText(this, "A network error", Toast.LENGTH_SHORT).show()
                return
            }

            database.reference
                .child("Users")
                .child(auth.uid.toString()).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result.getValue<Map<String, Any>>()
                        BattleShipViewModel.selectedImg =
                            (value!!["imageUrl"] as String).toUri()
                        BattleShipViewModel.nickname = value["nickname"] as String
                        BattleShipViewModel.isGravatar = value["gravatar"] as Boolean
                        BattleShipViewModel.oldSelectedImg = BattleShipViewModel.selectedImg
                        BattleShipViewModel.oldNickname = BattleShipViewModel.nickname
                        BattleShipViewModel.oldIsGravatar = BattleShipViewModel.isGravatar

                        if (value["games"] != null) {
                            val games = value["games"] as List<Map<String, Any>>

                            for (game in games) {
                                var winnerShips = listOf<Ship>()
                                var loserShips = listOf<Ship>()

                                if (game["winnerShips"] != null) {
                                    for (ship in game["winnerShips"] as MutableList<Map<String, Any>>) {
                                        winnerShips = winnerShips.plus(
                                            Ship(
                                                ship["coordinates"] as List<List<Int>>,
                                                ship["destroyed"] as Boolean
                                            )
                                        )
                                    }
                                }

                                for (ship in game["loserShips"] as MutableList<Map<String, Any>>) {
                                    loserShips = loserShips.plus(
                                        Ship(
                                            ship["coordinates"] as List<List<Int>>,
                                            ship["destroyed"] as Boolean
                                        )
                                    )
                                }

                                BattleShipViewModel.games = BattleShipViewModel.games.plus(
                                    Game(
                                        game["id"] as String,
                                        game["winnerId"] as String,
                                        game["loserId"] as String,
                                        game["startTime"] as String,
                                        game["durationTime"] as String,
                                        game["winnerScore"] as Long,
                                        game["loserScore"] as Long,
                                        game["winnerMap"] as List<MutableList<Int>>,
                                        game["loserMap"] as List<MutableList<Int>>,
                                        winnerShips.toMutableList(),
                                        loserShips.toMutableList()
                                    )
                                )
                            }
                        }

                        viewModel.isProfileLoading = false
                        BattleShipViewModel.isAppLoaded = true
                    } else {
                        if (it.exception is FirebaseNetworkException) {
                            viewModel.isProfileLoading = false
                            Toast.makeText(this, "A network error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    private fun selectImg() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (data.data != null) {
                BattleShipViewModel.selectedImg = data.data!!

            }
        }
    }

    private fun updateProfile() {
        BattleShipViewModel.isProfileUpdating = true
        if (BattleShipViewModel.selectedImg != BattleShipViewModel.oldSelectedImg) {
            val reference = storage.reference.child("Users").child(auth.uid.toString())
            reference.putFile(BattleShipViewModel.selectedImg!!).addOnCompleteListener {
                if (it.isSuccessful) {
                    reference.downloadUrl.addOnSuccessListener { task ->
                        val user =
                            User(
                                auth.uid.toString(),
                                auth.currentUser!!.email!!,
                                BattleShipViewModel.nickname,
                                task.toString(),
                                BattleShipViewModel.isGravatar,
                                BattleShipViewModel.games
                            )
                        database.reference
                            .child("Users")
                            .child(auth.uid.toString())
                            .setValue(user)
                            .addOnSuccessListener {
                                BattleShipViewModel.oldSelectedImg = BattleShipViewModel.selectedImg
                                BattleShipViewModel.oldNickname = BattleShipViewModel.nickname
                                BattleShipViewModel.oldIsGravatar = BattleShipViewModel.isGravatar
                                BattleShipViewModel.isProfileUpdating = false
                                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    if (it.exception is FirebaseNetworkException) {
                        BattleShipViewModel.isProfileUpdating = false
                        Toast.makeText(this, "A network error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            val user = User(
                auth.uid.toString(),
                auth.currentUser!!.email!!,
                BattleShipViewModel.nickname,
                BattleShipViewModel.selectedImg.toString(),
                BattleShipViewModel.isGravatar,
                BattleShipViewModel.games
            )
            database.reference
                .child("Users")
                .child(auth.uid.toString())
                .setValue(user)
                .addOnSuccessListener {
                    BattleShipViewModel.oldNickname = BattleShipViewModel.nickname
                    BattleShipViewModel.oldIsGravatar = BattleShipViewModel.isGravatar
                    BattleShipViewModel.isProfileUpdating = false
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                database.reference.child("Users").child(auth.uid.toString()).get()
                    .addOnSuccessListener { data ->
                        if (data.getValue<Map<String, Any>>() == null) {
                            val user = User(
                                auth.uid.toString(),
                                auth.currentUser!!.email!!,
                                "player",
                                "null",
                                false,
                                listOf()
                            )
                            database.reference
                                .child("Users")
                                .child(auth.uid.toString())
                                .setValue(user)
                                .addOnSuccessListener {
                                    BattleShipViewModel.currentUser = auth.currentUser
                                    BattleShipViewModel.isAuth = true
                                    BattleShipViewModel.isLoading = false
                                }
                        } else {
                            BattleShipViewModel.currentUser = auth.currentUser
                            BattleShipViewModel.isAuth = true
                            BattleShipViewModel.isLoading = false
                        }
                    }
            } else {
                if (it.exception is FirebaseNetworkException) {
                    BattleShipViewModel.isLoading = false
                    Toast.makeText(this, "A network error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signOut() {
        Firebase.auth.signOut()
        BattleShipViewModel.isAuth = false
    }

    private fun signUpWithEmailAndPassword(nickname: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = User(
                        auth.uid.toString(),
                        auth.currentUser!!.email!!,
                        nickname,
                        "null",
                        false,
                        listOf()
                    )
                    database.reference
                        .child("Users")
                        .child(auth.uid.toString())
                        .setValue(user)
                        .addOnSuccessListener {
                            BattleShipViewModel.currentUser = auth.currentUser
                            BattleShipViewModel.isLoading = false
                        }
                } else {
                    Log.i("Error ", "${task.exception}: ${task.exception!!.message}")

                    when (task.exception) {
                        is FirebaseAuthUserCollisionException -> Toast.makeText(
                            this,
                            "The email address is already in use by another account",
                            Toast.LENGTH_SHORT
                        ).show()
                        is FirebaseAuthWeakPasswordException -> Toast.makeText(
                            this,
                            "Password should be at least 6 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                        is FirebaseNetworkException -> {
                            BattleShipViewModel.isLoading = false
                            Toast.makeText(
                                this,
                                "A network error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    BattleShipViewModel.currentUser = auth.currentUser
                    BattleShipViewModel.isLoading = false
                } else {
                    Log.i("Error ", "${task.exception}: ${task.exception!!.message}")

                    when (task.exception) {
                        is FirebaseAuthInvalidUserException -> Toast.makeText(
                            this,
                            "The email address is not registered",
                            Toast.LENGTH_SHORT
                        ).show()
                        is FirebaseAuthInvalidCredentialsException -> Toast.makeText(
                            this,
                            "Incorrect password",
                            Toast.LENGTH_SHORT
                        ).show()
                        is FirebaseNetworkException -> {
                            BattleShipViewModel.isLoading = false
                            Toast.makeText(
                                this,
                                "A network error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
    }

    private fun createRoom() {
        BattleShipViewModel.firstPlayerMap = null
        BattleShipViewModel.secondPlayerMap = null
        BattleShipViewModel.firstPlayerShips = null
        BattleShipViewModel.secondPlayerShips = null
        BattleShipViewModel.firstPlayerId = null
        BattleShipViewModel.secondPlayerId = null
        BattleShipViewModel.enemy = null
        BattleShipViewModel.firstPlayerScore = 0L
        BattleShipViewModel.secondPlayerScore = 0L

        if (!hasConnection(this)) {
            Toast.makeText(this, "A network error", Toast.LENGTH_SHORT).show()
            return
        }

        BattleShipViewModel.isRoomCreating = true
        BattleShipViewModel.coefficient = 10
        BattleShipViewModel.isReadyButtonEnabled = true

        val roomId = UUID.randomUUID().toString()

        val map = listOf(
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        )

        val playerShips = mutableListOf<Ship>()

        val room = Room(
            roomId,
            auth.uid.toString(),
            "null",
            map,
            map,
            playerShips,
            playerShips
        )

        roomRef = database.reference.child("Rooms").child(roomId)

        roomRef.setValue(room).addOnSuccessListener {
            BattleShipViewModel.roomId = roomId
            BattleShipViewModel.firstPlayerId = auth.uid
            BattleShipViewModel.isRoomCreated = true
            BattleShipViewModel.isRoomCreating = false
            BattleShipViewModel.isGameLoading = true

            val ref = database.reference.child("Connections")
                .child(BattleShipViewModel.roomId).child(auth.uid.toString())

            writeTimer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
                ref.setValue(LocalDateTime.now().toString())
            }
        }

        roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue<Map<String, Any>>() ?: return

                if (BattleShipViewModel.isRoomCreated && value["secondPlayerId"] != "null") {
                    BattleShipViewModel.secondPlayerId = value["secondPlayerId"] as String
                    database.reference.child("Users")
                        .child(BattleShipViewModel.secondPlayerId!!).get().addOnSuccessListener {
                            val enemy = it.getValue<Map<String, Any>>()
                            BattleShipViewModel.enemy = User(
                                "null",
                                enemy!!["email"] as String,
                                enemy["nickname"] as String,
                                enemy["imageUrl"] as String,
                                enemy["gravatar"] as Boolean,
                                listOf()
                            )
                        }

                    onlineRef = database.reference.child("Connections")
                        .child(BattleShipViewModel.roomId)
                        .child(BattleShipViewModel.secondPlayerId!!)

                    readTimer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
                        onlineRef.get().addOnSuccessListener { data ->
                            val string = data.getValue<String>() ?: return@addOnSuccessListener

                            val lastTime = LocalDateTime.parse(string)
                            val nowTime = LocalDateTime.now()
                            val res = nowTime.minusYears(lastTime.year.toLong())
                                .minusMonths(lastTime.monthValue.toLong())
                                .minusDays(lastTime.dayOfMonth.toLong())
                                .minusHours(lastTime.hour.toLong())
                                .minusMinutes(lastTime.minute.toLong())
                                .minusSeconds(lastTime.second.toLong())

                            BattleShipViewModel.isPlayerWaiting = res.second >= 6L
                        }
                    }

                    BattleShipViewModel.isPlayerConnected = true
                }

                if (BattleShipViewModel.isPlayerConnected) {
                    BattleShipViewModel.firstPlayerMap =
                        value["firstPlayerMap"] as List<MutableList<Int>>
                    BattleShipViewModel.secondPlayerMap =
                        value["secondPlayerMap"] as List<MutableList<Int>>

                    if (BattleShipViewModel.firstPlayerShips == null ||
                        (BattleShipViewModel.isFirstPlayerReady && BattleShipViewModel.isSecondPlayerReady)
                    ) {
                        BattleShipViewModel.firstPlayerShips = mutableListOf()
                    }

                    if (value["firstPlayerShips"] != null) {
                        for (ship in value["firstPlayerShips"] as List<Map<String, Any>>) {
                            BattleShipViewModel.firstPlayerShips =
                                BattleShipViewModel.firstPlayerShips!!.plus(
                                    Ship(
                                        ship["coordinates"] as List<List<Int>>,
                                        ship["destroyed"] as Boolean
                                    )
                                ).toMutableList()
                        }
                    }

                    if (BattleShipViewModel.secondPlayerShips == null ||
                        (BattleShipViewModel.isFirstPlayerReady && BattleShipViewModel.isSecondPlayerReady)
                    ) {
                        BattleShipViewModel.secondPlayerShips = mutableListOf()
                    }

                    if (value["secondPlayerShips"] != null) {
                        for (ship in value["secondPlayerShips"] as List<Map<String, Any>>) {
                            BattleShipViewModel.secondPlayerShips =
                                BattleShipViewModel.secondPlayerShips!!.plus(
                                    Ship(
                                        ship["coordinates"] as List<List<Int>>,
                                        ship["destroyed"] as Boolean
                                    )
                                ).toMutableList()
                        }
                    }

                    BattleShipViewModel.firstPlayerScore = value["firstPlayerScore"] as Long
                    BattleShipViewModel.secondPlayerScore = value["secondPlayerScore"] as Long

                    var isGameOver = false
                    var winnerId = BattleShipViewModel.secondPlayerId

                    for (ship in BattleShipViewModel.firstPlayerShips!!) {
                        isGameOver = ship.isDestroyed

                        if (!ship.isDestroyed) {
                            isGameOver = false
                            break
                        }
                    }

                    if (!isGameOver) {
                        winnerId = BattleShipViewModel.firstPlayerId

                        for (ship in BattleShipViewModel.secondPlayerShips!!) {
                            isGameOver = ship.isDestroyed

                            if (!ship.isDestroyed) {
                                isGameOver = false
                                break
                            }
                        }
                    }

                    if (isGameOver) {
                        var startTime: LocalDateTime = LocalDateTime.now()
                        val duration: LocalDateTime

                        if (value["startTime"] as String == "null") {
                            val endTime = LocalDateTime.now()
                            duration = endTime.minusYears(endTime.year.toLong())
                                .minusMonths(endTime.monthValue.toLong())
                                .minusDays(endTime.dayOfMonth.toLong())
                                .minusHours(endTime.hour.toLong())
                                .minusMinutes(endTime.minute.toLong())
                        } else {
                            startTime =
                                LocalDateTime.parse(
                                    (value["startTime"] as String).replace(
                                        ' ',
                                        'T'
                                    )
                                )
                            val endTime = LocalDateTime.now()
                            duration = endTime.minusYears(startTime.year.toLong())
                                .minusMonths(startTime.monthValue.toLong())
                                .minusDays(startTime.dayOfMonth.toLong())
                                .minusHours(startTime.hour.toLong())
                                .minusMinutes(startTime.minute.toLong())
                        }

                        if (BattleShipViewModel.firstPlayerId == winnerId) {
                            BattleShipViewModel.games = BattleShipViewModel.games.plus(
                                Game(
                                    BattleShipViewModel.roomId,
                                    BattleShipViewModel.firstPlayerId!!,
                                    BattleShipViewModel.secondPlayerId!!,
                                    startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    duration.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    BattleShipViewModel.firstPlayerScore,
                                    BattleShipViewModel.secondPlayerScore,
                                    BattleShipViewModel.firstPlayerMap!!,
                                    BattleShipViewModel.secondPlayerMap!!,
                                    BattleShipViewModel.firstPlayerShips!!,
                                    BattleShipViewModel.secondPlayerShips!!
                                )
                            )
                        }

                        if (BattleShipViewModel.secondPlayerId == winnerId) {
                            BattleShipViewModel.games = BattleShipViewModel.games.plus(
                                Game(
                                    BattleShipViewModel.roomId,
                                    BattleShipViewModel.secondPlayerId!!,
                                    BattleShipViewModel.firstPlayerId!!,
                                    startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    duration.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    BattleShipViewModel.secondPlayerScore,
                                    BattleShipViewModel.firstPlayerScore,
                                    BattleShipViewModel.secondPlayerMap!!,
                                    BattleShipViewModel.firstPlayerMap!!,
                                    BattleShipViewModel.secondPlayerShips!!,
                                    BattleShipViewModel.firstPlayerShips!!
                                )
                            )
                        }

                        database.reference.child("Users")
                            .child(auth.uid!!).child("games")
                            .setValue(BattleShipViewModel.games).addOnSuccessListener {
                                roomRef.removeValue().addOnSuccessListener {
                                    stopCheckConnection()

                                    BattleShipViewModel.isRoomCreating = false
                                    BattleShipViewModel.isExiting = false
                                    BattleShipViewModel.isPlayerWaiting = false
                                    BattleShipViewModel.roomId = ""
                                    BattleShipViewModel.isAttackButtonEnabled = false
                                    BattleShipViewModel.isReadyButtonEnabled = true
                                    BattleShipViewModel.startTime = "null"
                                    BattleShipViewModel.isEnemyMoving = false
                                    BattleShipViewModel.winner = null
                                    BattleShipViewModel.loser = null

                                    for (i in BattleShipViewModel.ships.indices) {
                                        BattleShipViewModel.ships[i].value = false
                                    }

                                    for (i in BattleShipViewModel.isVerticalShips.indices) {
                                        BattleShipViewModel.isVerticalShips[i].value = false
                                    }

                                    BattleShipViewModel.isGameOver = true
                                    BattleShipViewModel.isPlayerConnected = false
                                    BattleShipViewModel.winnerId = winnerId!!
                                }
                            }
                    }

                    BattleShipViewModel.isFirstPlayerReady = value["firstPlayerReady"] as Boolean
                    BattleShipViewModel.isSecondPlayerReady = value["secondPlayerReady"] as Boolean

                    if (value["startTime"] as String == "null") {
                        if (BattleShipViewModel.isFirstPlayerReady && BattleShipViewModel.isSecondPlayerReady) {
                            val startTime = LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            )
                            BattleShipViewModel.startTime = startTime
                            roomRef.child("startTime").setValue(startTime)
                        }
                    } else {
                        BattleShipViewModel.startTime = value["startTime"] as String
                    }

                    BattleShipViewModel.isFirstPlayerMoving = value["firstPlayerMoving"] as Boolean
                    BattleShipViewModel.isSecondPlayerMoving =
                        value["secondPlayerMoving"] as Boolean

                    if (BattleShipViewModel.isFirstPlayerMoving != BattleShipViewModel.isSecondPlayerMoving) {
                        BattleShipViewModel.updateAttackButton()
                        BattleShipViewModel.updateEnemyMoving()
                    }

                    BattleShipViewModel.winnerId = value["winnerId"] as String
                    BattleShipViewModel.isGameLoading = false
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        BattleShipViewModel.isAttackButtonEnabled = true

        startCheckConnection()
    }

    private suspend fun enterRoom() {
        BattleShipViewModel.firstPlayerMap = null
        BattleShipViewModel.secondPlayerMap = null
        BattleShipViewModel.firstPlayerShips = null
        BattleShipViewModel.secondPlayerShips = null
        BattleShipViewModel.firstPlayerId = null
        BattleShipViewModel.secondPlayerId = null
        BattleShipViewModel.enemy = null
        BattleShipViewModel.firstPlayerScore = 0L
        BattleShipViewModel.secondPlayerScore = 0L

        if (!hasConnection(this)) {
            BattleShipViewModel.isGameLoading = false
            Toast.makeText(this, "A network error", Toast.LENGTH_SHORT).show()
            return
        }

        BattleShipViewModel.coefficient = 10
        BattleShipViewModel.isReadyButtonEnabled = true

        val value = database.reference.child("Rooms").child(BattleShipViewModel.roomId)
            .child("id").get().await()

        if (value.getValue<String>() == null) {
            Toast.makeText(this, "Room not found", Toast.LENGTH_SHORT).show()
            BattleShipViewModel.roomId = ""
            return
        } else {
            roomRef = database.reference.child("Rooms").child(BattleShipViewModel.roomId)
        }

        roomRef.child("secondPlayerId").setValue(auth.uid).addOnSuccessListener {
            val secondPlayerShips = mutableListOf<Ship>()

            roomRef.child("secondPlayerShips").setValue(secondPlayerShips).addOnSuccessListener {
                roomRef.get().addOnSuccessListener {
                    val value = it.getValue<Map<String, Any>>()

                    BattleShipViewModel.firstPlayerId = value!!["firstPlayerId"] as String
                    BattleShipViewModel.secondPlayerId = value["secondPlayerId"] as String

                    BattleShipViewModel.isFirstPlayerMoving = value["firstPlayerMoving"] as Boolean
                    BattleShipViewModel.isSecondPlayerMoving =
                        value["secondPlayerMoving"] as Boolean

                    BattleShipViewModel.updateEnemyMoving()

                    BattleShipViewModel.firstPlayerMap =
                        value["firstPlayerMap"] as List<MutableList<Int>>
                    BattleShipViewModel.secondPlayerMap =
                        value["secondPlayerMap"] as List<MutableList<Int>>

                    BattleShipViewModel.firstPlayerShips = mutableListOf()

                    if (value["firstPlayerShips"] != null) {
                        for (ship in value["firstPlayerShips"] as List<Map<String, Any>>) {
                            BattleShipViewModel.firstPlayerShips =
                                BattleShipViewModel.firstPlayerShips!!.plus(
                                    Ship(
                                        ship["coordinates"] as List<List<Int>>,
                                        ship["destroyed"] as Boolean
                                    )
                                ).toMutableList()
                        }
                    }

                    BattleShipViewModel.secondPlayerShips = mutableListOf()

                    if (value["secondPlayerShips"] != null) {
                        for (ship in value["secondPlayerShips"] as List<Map<String, Any>>) {
                            BattleShipViewModel.secondPlayerShips =
                                BattleShipViewModel.secondPlayerShips!!.plus(
                                    Ship(
                                        ship["coordinates"] as List<List<Int>>,
                                        ship["destroyed"] as Boolean
                                    )
                                ).toMutableList()
                        }
                    }

                    BattleShipViewModel.firstPlayerScore = value["firstPlayerScore"] as Long
                    BattleShipViewModel.secondPlayerScore = value["secondPlayerScore"] as Long

                    database.reference.child("Users")
                        .child(BattleShipViewModel.firstPlayerId!!).get()
                        .addOnSuccessListener { user ->
                            val enemy = user.getValue<Map<String, Any>>()
                            BattleShipViewModel.enemy = User(
                                "null",
                                enemy!!["email"] as String,
                                enemy["nickname"] as String,
                                enemy["imageUrl"] as String,
                                enemy["gravatar"] as Boolean,
                                listOf()
                            )

                            BattleShipViewModel.isRoomCreated = true
                            BattleShipViewModel.isPlayerConnected = true
                        }

                    val ref = database.reference.child("Connections")
                        .child(BattleShipViewModel.roomId).child(auth.uid!!)

                    writeTimer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
                        ref.setValue(LocalDateTime.now().toString())
                    }

                    onlineRef = database.reference.child("Connections")
                        .child(BattleShipViewModel.roomId)
                        .child(BattleShipViewModel.firstPlayerId!!)

                    readTimer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
                        onlineRef.get().addOnSuccessListener { data ->
                            val string = data.getValue<String>() ?: return@addOnSuccessListener

                            val lastTime = LocalDateTime.parse(string)
                            val nowTime = LocalDateTime.now()
                            val res = nowTime.minusYears(lastTime.year.toLong())
                                .minusMonths(lastTime.monthValue.toLong())
                                .minusDays(lastTime.dayOfMonth.toLong())
                                .minusHours(lastTime.hour.toLong())
                                .minusMinutes(lastTime.minute.toLong())
                                .minusSeconds(lastTime.second.toLong())

                            BattleShipViewModel.isPlayerWaiting = res.second >= 6L
                        }
                    }

                    BattleShipViewModel.isGameLoading = false
                }
            }
        }

        roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue<Map<String, Any>>() ?: return

                if (BattleShipViewModel.isPlayerConnected) {
                    BattleShipViewModel.firstPlayerMap =
                        value["firstPlayerMap"] as List<MutableList<Int>>
                    BattleShipViewModel.secondPlayerMap =
                        value["secondPlayerMap"] as List<MutableList<Int>>

                    if (BattleShipViewModel.firstPlayerShips == null ||
                        (BattleShipViewModel.isFirstPlayerReady && BattleShipViewModel.isSecondPlayerReady)
                    ) {
                        BattleShipViewModel.firstPlayerShips = mutableListOf()
                    }

                    if (value["firstPlayerShips"] != null) {
                        for (ship in value["firstPlayerShips"] as List<Map<String, Any>>) {
                            BattleShipViewModel.firstPlayerShips =
                                BattleShipViewModel.firstPlayerShips!!.plus(
                                    Ship(
                                        ship["coordinates"] as List<List<Int>>,
                                        ship["destroyed"] as Boolean
                                    )
                                ).toMutableList()
                        }
                    }

                    if (BattleShipViewModel.secondPlayerShips == null ||
                        (BattleShipViewModel.isFirstPlayerReady && BattleShipViewModel.isSecondPlayerReady)
                    ) {
                        BattleShipViewModel.secondPlayerShips = mutableListOf()
                    }

                    if (value["secondPlayerShips"] != null) {
                        for (ship in value["secondPlayerShips"] as List<Map<String, Any>>) {
                            BattleShipViewModel.secondPlayerShips =
                                BattleShipViewModel.secondPlayerShips!!.plus(
                                    Ship(
                                        ship["coordinates"] as List<List<Int>>,
                                        ship["destroyed"] as Boolean
                                    )
                                ).toMutableList()
                        }
                    }

                    BattleShipViewModel.firstPlayerScore = value["firstPlayerScore"] as Long
                    BattleShipViewModel.secondPlayerScore = value["secondPlayerScore"] as Long

                    var isGameOver = false
                    var winnerId = BattleShipViewModel.secondPlayerId

                    for (ship in BattleShipViewModel.firstPlayerShips!!) {
                        isGameOver = ship.isDestroyed

                        if (!ship.isDestroyed) {
                            isGameOver = false
                            break
                        }
                    }

                    if (!isGameOver) {
                        winnerId = BattleShipViewModel.firstPlayerId

                        for (ship in BattleShipViewModel.secondPlayerShips!!) {
                            isGameOver = ship.isDestroyed

                            if (!ship.isDestroyed) {
                                isGameOver = false
                                break
                            }
                        }
                    }

                    if (isGameOver) {
                        var startTime: LocalDateTime = LocalDateTime.now()
                        val duration: LocalDateTime

                        if (value["startTime"] as String == "null") {
                            val endTime = LocalDateTime.now()
                            duration = endTime.minusYears(endTime.year.toLong())
                                .minusMonths(endTime.monthValue.toLong())
                                .minusDays(endTime.dayOfMonth.toLong())
                                .minusHours(endTime.hour.toLong())
                                .minusMinutes(endTime.minute.toLong())
                        } else {
                            startTime =
                                LocalDateTime.parse(
                                    (value["startTime"] as String).replace(
                                        ' ',
                                        'T'
                                    )
                                )
                            val endTime = LocalDateTime.now()
                            duration = endTime.minusYears(startTime.year.toLong())
                                .minusMonths(startTime.monthValue.toLong())
                                .minusDays(startTime.dayOfMonth.toLong())
                                .minusHours(startTime.hour.toLong())
                                .minusMinutes(startTime.minute.toLong())
                        }

                        if (BattleShipViewModel.firstPlayerId == winnerId) {
                            BattleShipViewModel.games = BattleShipViewModel.games.plus(
                                Game(
                                    BattleShipViewModel.roomId,
                                    BattleShipViewModel.firstPlayerId!!,
                                    BattleShipViewModel.secondPlayerId!!,
                                    startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    duration.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    BattleShipViewModel.firstPlayerScore,
                                    BattleShipViewModel.secondPlayerScore,
                                    BattleShipViewModel.firstPlayerMap!!,
                                    BattleShipViewModel.secondPlayerMap!!,
                                    BattleShipViewModel.firstPlayerShips!!,
                                    BattleShipViewModel.secondPlayerShips!!
                                )
                            )
                        }

                        if (BattleShipViewModel.secondPlayerId == winnerId) {
                            BattleShipViewModel.games = BattleShipViewModel.games.plus(
                                Game(
                                    BattleShipViewModel.roomId,
                                    BattleShipViewModel.secondPlayerId!!,
                                    BattleShipViewModel.firstPlayerId!!,
                                    startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    duration.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    BattleShipViewModel.secondPlayerScore,
                                    BattleShipViewModel.firstPlayerScore,
                                    BattleShipViewModel.secondPlayerMap!!,
                                    BattleShipViewModel.firstPlayerMap!!,
                                    BattleShipViewModel.secondPlayerShips!!,
                                    BattleShipViewModel.firstPlayerShips!!
                                )
                            )
                        }

                        database.reference.child("Users")
                            .child(auth.uid!!).child("games")
                            .setValue(BattleShipViewModel.games).addOnSuccessListener {
                                roomRef.removeValue().addOnSuccessListener {
                                    stopCheckConnection()

                                    BattleShipViewModel.isRoomCreating = false
                                    BattleShipViewModel.isExiting = false
                                    BattleShipViewModel.isPlayerWaiting = false
                                    BattleShipViewModel.roomId = ""
                                    BattleShipViewModel.isAttackButtonEnabled = false
                                    BattleShipViewModel.isReadyButtonEnabled = true
                                    BattleShipViewModel.startTime = "null"
                                    BattleShipViewModel.isEnemyMoving = false
                                    BattleShipViewModel.winner = null
                                    BattleShipViewModel.loser = null

                                    for (i in BattleShipViewModel.ships.indices) {
                                        BattleShipViewModel.ships[i].value = false
                                    }

                                    for (i in BattleShipViewModel.isVerticalShips.indices) {
                                        BattleShipViewModel.isVerticalShips[i].value = false
                                    }

                                    BattleShipViewModel.isGameOver = true
                                    BattleShipViewModel.isPlayerConnected = false
                                    BattleShipViewModel.winnerId = winnerId!!
                                }
                            }
                    }

                    BattleShipViewModel.isFirstPlayerReady = value["firstPlayerReady"] as Boolean
                    BattleShipViewModel.isSecondPlayerReady = value["secondPlayerReady"] as Boolean

                    if (value["startTime"] as String == "null") {
                        if (BattleShipViewModel.isFirstPlayerReady && BattleShipViewModel.isSecondPlayerReady) {
                            val startTime = LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            )
                            BattleShipViewModel.startTime = startTime
                            roomRef.child("startTime").setValue(startTime)
                        }
                    } else {
                        BattleShipViewModel.startTime = value["startTime"] as String
                    }

                    BattleShipViewModel.isFirstPlayerMoving = value["firstPlayerMoving"] as Boolean
                    BattleShipViewModel.isSecondPlayerMoving =
                        value["secondPlayerMoving"] as Boolean

                    if (BattleShipViewModel.isFirstPlayerMoving != BattleShipViewModel.isSecondPlayerMoving) {
                        BattleShipViewModel.updateAttackButton()
                        BattleShipViewModel.updateEnemyMoving()
                    }

                    BattleShipViewModel.winnerId = value["winnerId"] as String
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        startCheckConnection()
    }

    private fun attackEnemy(x: Int, y: Int) {
        if (!hasConnection(this)) {
            Toast.makeText(this, "A network error", Toast.LENGTH_SHORT).show()
            return
        }

        val map: List<MutableList<Int>>
        val ships: MutableList<Ship>

        if (auth.uid == BattleShipViewModel.firstPlayerId) {
            map = BattleShipViewModel.secondPlayerMap!!
            ships = BattleShipViewModel.secondPlayerShips!!
        } else {
            map = BattleShipViewModel.firstPlayerMap!!
            ships = BattleShipViewModel.firstPlayerShips!!
        }

        var ship = Ship(listOf())
        var index = 0

        for (elem in ships) {
            for (coordinate in elem.coordinates) {
                if (coordinate[0] == x && coordinate[1] == y) {
                    ship = elem
                    break
                }
            }

            if (ship.coordinates.isNotEmpty()) {
                break
            }

            index += 1
        }

        if (ship.coordinates.isEmpty()) {
            map[x][y] = -1
        } else {
            map[x][y] = 1
            var isDestroyed = true

            for (point in ship.coordinates) {
                if (map[point[0]][point[1]] == 0) {
                    isDestroyed = false
                    break
                }
            }

            ships[index] = Ship(ships[index].coordinates, isDestroyed)
        }

        if (BattleShipViewModel.counter == 1) {
            if (auth.uid == BattleShipViewModel.firstPlayerId && BattleShipViewModel.isFirstPlayerMoving) {
                if (index <= 9) {
                    BattleShipViewModel.firstPlayerScore += BattleShipViewModel.coefficient
                    BattleShipViewModel.coefficient += 10

                    roomRef.setValue(
                        Room(
                            BattleShipViewModel.roomId,
                            BattleShipViewModel.firstPlayerId!!,
                            BattleShipViewModel.secondPlayerId!!,
                            BattleShipViewModel.firstPlayerMap!!,
                            map,
                            BattleShipViewModel.firstPlayerShips!!,
                            ships,
                            BattleShipViewModel.firstPlayerScore,
                            BattleShipViewModel.secondPlayerScore,
                            isFirstPlayerMoving = true,
                            isSecondPlayerMoving = false,
                            BattleShipViewModel.isFirstPlayerReady,
                            BattleShipViewModel.isSecondPlayerReady,
                            BattleShipViewModel.startTime
                        )
                    )
                } else {
                    BattleShipViewModel.coefficient = 10

                    roomRef.setValue(
                        Room(
                            BattleShipViewModel.roomId,
                            BattleShipViewModel.firstPlayerId!!,
                            BattleShipViewModel.secondPlayerId!!,
                            BattleShipViewModel.firstPlayerMap!!,
                            map,
                            BattleShipViewModel.firstPlayerShips!!,
                            ships,
                            BattleShipViewModel.firstPlayerScore,
                            BattleShipViewModel.secondPlayerScore,
                            isFirstPlayerMoving = false,
                            isSecondPlayerMoving = true,
                            BattleShipViewModel.isFirstPlayerReady,
                            BattleShipViewModel.isSecondPlayerReady,
                            BattleShipViewModel.startTime
                        )
                    )
                }
            } else if (auth.uid == BattleShipViewModel.secondPlayerId && BattleShipViewModel.isSecondPlayerMoving) {
                if (index <= 9) {
                    BattleShipViewModel.secondPlayerScore += BattleShipViewModel.coefficient
                    BattleShipViewModel.coefficient += 10

                    roomRef.setValue(
                        Room(
                            BattleShipViewModel.roomId,
                            BattleShipViewModel.firstPlayerId!!,
                            BattleShipViewModel.secondPlayerId!!,
                            map,
                            BattleShipViewModel.secondPlayerMap!!,
                            ships,
                            BattleShipViewModel.secondPlayerShips!!,
                            BattleShipViewModel.firstPlayerScore,
                            BattleShipViewModel.secondPlayerScore,
                            isFirstPlayerMoving = false,
                            isSecondPlayerMoving = true,
                            BattleShipViewModel.isFirstPlayerReady,
                            BattleShipViewModel.isSecondPlayerReady,
                            BattleShipViewModel.startTime
                        )
                    )
                } else {
                    BattleShipViewModel.coefficient = 10

                    roomRef.setValue(
                        Room(
                            BattleShipViewModel.roomId,
                            BattleShipViewModel.firstPlayerId!!,
                            BattleShipViewModel.secondPlayerId!!,
                            map,
                            BattleShipViewModel.secondPlayerMap!!,
                            ships,
                            BattleShipViewModel.secondPlayerShips!!,
                            BattleShipViewModel.firstPlayerScore,
                            BattleShipViewModel.secondPlayerScore,
                            isFirstPlayerMoving = true,
                            isSecondPlayerMoving = false,
                            BattleShipViewModel.isFirstPlayerReady,
                            BattleShipViewModel.isSecondPlayerReady,
                            BattleShipViewModel.startTime
                        )
                    )
                }
            }
        }
    }

    private fun leaveRoom() {
        if (!hasConnection(this)) {
            Toast.makeText(this, "A network error", Toast.LENGTH_SHORT).show()
            return
        }

        BattleShipViewModel.isExiting = true

        if (auth.uid == BattleShipViewModel.firstPlayerId) {
            for (i in BattleShipViewModel.firstPlayerShips!!.indices) {
                BattleShipViewModel.firstPlayerShips!![i].isDestroyed = true
            }

            roomRef.child("firstPlayerShips").setValue(
                BattleShipViewModel.firstPlayerShips!!
                    .plus(Ship(listOf(listOf(-1, -1)), true))
            )
        }

        if (auth.uid == BattleShipViewModel.secondPlayerId) {
            for (i in BattleShipViewModel.secondPlayerShips!!.indices) {
                BattleShipViewModel.secondPlayerShips!![i].isDestroyed = true
            }

            roomRef.child("secondPlayerShips").setValue(
                BattleShipViewModel.secondPlayerShips!!
                    .plus(Ship(listOf(listOf(-1, -1)), true))
            )
        }
    }

    private fun loadGame(winnerId: String, loserId: String) {
        database.reference.child("Users").child(winnerId).get().addOnSuccessListener {
            val value = it.getValue<Map<String, Any>>() ?: return@addOnSuccessListener

            BattleShipViewModel.winner = User(
                "",
                value["email"] as String,
                value["nickname"] as String,
                value["imageUrl"] as String,
                value["gravatar"] as Boolean,
                listOf()
            )
        }

        database.reference.child("Users").child(loserId).get().addOnSuccessListener {
            val value = it.getValue<Map<String, Any>>() ?: return@addOnSuccessListener

            BattleShipViewModel.loser = User(
                "",
                value["email"] as String,
                value["nickname"] as String,
                value["imageUrl"] as String,
                value["gravatar"] as Boolean,
                listOf()
            )
        }
    }

    private fun deleteRoom() {
        if (!hasConnection(this)) {
            Toast.makeText(this, "A network error", Toast.LENGTH_SHORT).show()
            BattleShipViewModel.isRoomCreated = false
            BattleShipViewModel.isGameLoading = false
            BattleShipViewModel.roomId = ""
        }

        roomRef.removeValue().addOnSuccessListener {
            Toast.makeText(this, "Room deleted successfully", Toast.LENGTH_SHORT).show()
            BattleShipViewModel.isRoomCreated = false
            BattleShipViewModel.isGameLoading = false
            BattleShipViewModel.roomId = ""
        }
    }

    private fun addShip(ship: Ship, index: Int) {
        var isPossible = true

        if (auth.uid == BattleShipViewModel.firstPlayerId) {
            for (coordinate in ship.coordinates) {
                for (playerShip in BattleShipViewModel.firstPlayerShips!!) {
                    for (i in playerShip.coordinates) {
                        for (j in -1..1) {
                            for (k in -1..1) {
                                if (coordinate[0] + j == i[0] && coordinate[1] + k == i[1]) {
                                    isPossible = false
                                }
                            }
                        }
                    }
                }
            }

            if (isPossible) {
                BattleShipViewModel.ships[index].value = true
                BattleShipViewModel.firstPlayerShips =
                    BattleShipViewModel.firstPlayerShips!!.plus(ship).toMutableList()
            }
        }

        if (auth.uid == BattleShipViewModel.secondPlayerId) {
            for (coordinate in ship.coordinates) {
                for (playerShip in BattleShipViewModel.secondPlayerShips!!) {
                    for (i in playerShip.coordinates) {
                        for (j in -1..1) {
                            for (k in -1..1) {
                                if (coordinate[0] + j == i[0] && coordinate[1] + k == i[1]) {
                                    isPossible = false
                                }
                            }
                        }
                    }
                }
            }

            if (isPossible) {
                BattleShipViewModel.ships[index].value = true
                BattleShipViewModel.secondPlayerShips =
                    BattleShipViewModel.secondPlayerShips!!.plus(ship).toMutableList()
            }
        }
    }

    private fun dropShip(x: Int, y: Int) {
        if (auth.uid == BattleShipViewModel.firstPlayerId) {
            var index = -1

            for (i in BattleShipViewModel.firstPlayerShips!!.indices) {
                for (j in BattleShipViewModel.firstPlayerShips!![i].coordinates.indices) {
                    if (BattleShipViewModel.firstPlayerShips!![i].coordinates[j][0] == x &&
                        BattleShipViewModel.firstPlayerShips!![i].coordinates[j][1] == y
                    ) {
                        index = i
                    }
                }
            }

            if (index != -1) {
                val ships = BattleShipViewModel.firstPlayerShips
                val ship = ships!!.removeAt(index)
                BattleShipViewModel.firstPlayerShips = null
                BattleShipViewModel.firstPlayerShips = ships

                if (ship.coordinates.size == 4) {
                    BattleShipViewModel.ships[0].value = false
                    BattleShipViewModel.isVerticalShips[0].value = false
                }

                if (ship.coordinates.size == 3) {
                    if (BattleShipViewModel.ships[1].value) {
                        BattleShipViewModel.ships[1].value = false
                        BattleShipViewModel.isVerticalShips[1].value = false
                    } else if (BattleShipViewModel.ships[2].value) {
                        BattleShipViewModel.ships[2].value = false
                        BattleShipViewModel.isVerticalShips[2].value = false
                    }
                }

                if (ship.coordinates.size == 2) {
                    if (BattleShipViewModel.ships[3].value) {
                        BattleShipViewModel.ships[3].value = false
                        BattleShipViewModel.isVerticalShips[3].value = false
                    } else if (BattleShipViewModel.ships[4].value) {
                        BattleShipViewModel.ships[4].value = false
                        BattleShipViewModel.isVerticalShips[4].value = false
                    } else if (BattleShipViewModel.ships[5].value) {
                        BattleShipViewModel.ships[5].value = false
                        BattleShipViewModel.isVerticalShips[5].value = false
                    }
                }

                if (ship.coordinates.size == 1) {
                    if (BattleShipViewModel.ships[6].value) {
                        BattleShipViewModel.ships[6].value = false
                    } else if (BattleShipViewModel.ships[7].value) {
                        BattleShipViewModel.ships[7].value = false
                    } else if (BattleShipViewModel.ships[8].value) {
                        BattleShipViewModel.ships[8].value = false
                    } else if (BattleShipViewModel.ships[9].value) {
                        BattleShipViewModel.ships[9].value = false
                    }
                }
            }
        }

        if (auth.uid == BattleShipViewModel.secondPlayerId) {
            var index = -1

            for (i in BattleShipViewModel.secondPlayerShips!!.indices) {
                for (j in BattleShipViewModel.secondPlayerShips!![i].coordinates.indices) {
                    if (BattleShipViewModel.secondPlayerShips!![i].coordinates[j][0] == x &&
                        BattleShipViewModel.secondPlayerShips!![i].coordinates[j][1] == y
                    ) {
                        index = i
                    }
                }
            }

            if (index != -1) {
                val ships = BattleShipViewModel.secondPlayerShips
                val ship = ships!!.removeAt(index)
                BattleShipViewModel.secondPlayerShips = null
                BattleShipViewModel.secondPlayerShips = ships

                if (ship.coordinates.size == 4) {
                    BattleShipViewModel.ships[0].value = false
                }

                if (ship.coordinates.size == 3) {
                    if (BattleShipViewModel.ships[1].value) {
                        BattleShipViewModel.ships[1].value = false
                    } else if (BattleShipViewModel.ships[2].value) {
                        BattleShipViewModel.ships[2].value = false
                    }
                }

                if (ship.coordinates.size == 2) {
                    if (BattleShipViewModel.ships[3].value) {
                        BattleShipViewModel.ships[3].value = false
                    } else if (BattleShipViewModel.ships[4].value) {
                        BattleShipViewModel.ships[4].value = false
                    } else if (BattleShipViewModel.ships[5].value) {
                        BattleShipViewModel.ships[5].value = false
                    }
                }

                if (ship.coordinates.size == 1) {
                    if (BattleShipViewModel.ships[6].value) {
                        BattleShipViewModel.ships[6].value = false
                    } else if (BattleShipViewModel.ships[7].value) {
                        BattleShipViewModel.ships[7].value = false
                    } else if (BattleShipViewModel.ships[8].value) {
                        BattleShipViewModel.ships[8].value = false
                    } else if (BattleShipViewModel.ships[9].value) {
                        BattleShipViewModel.ships[9].value = false
                    }
                }

            }
        }
    }

    private fun setReady() {
        if (auth.uid == BattleShipViewModel.firstPlayerId) {
            roomRef.child("firstPlayerShips").setValue(BattleShipViewModel.firstPlayerShips)
                .addOnSuccessListener {
                    roomRef.child("firstPlayerReady").setValue(true)
                }
        }

        if (auth.uid == BattleShipViewModel.secondPlayerId) {
            roomRef.child("secondPlayerShips").setValue(BattleShipViewModel.secondPlayerShips)
                .addOnSuccessListener {
                    roomRef.child("secondPlayerReady").setValue(true)
                }
        }
    }

    private fun startCheckConnection() {
        var time = 0
        var time2 = 0

        connectionTimer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            BattleShipViewModel.isConnected = hasConnection(this@MainActivity)

            if (!BattleShipViewModel.isConnected) {
                time += 1

                if (time >= 5) {
                    stopCheckConnection()

                    BattleShipViewModel.isPlayerConnected = false
                    BattleShipViewModel.isPlayerWaiting = false
                    BattleShipViewModel.roomId = ""
                    BattleShipViewModel.firstPlayerId = null
                    BattleShipViewModel.secondPlayerId = null
                    BattleShipViewModel.enemy = null
                    BattleShipViewModel.firstPlayerMap = null
                    BattleShipViewModel.secondPlayerMap = null
                    BattleShipViewModel.firstPlayerShips = null
                    BattleShipViewModel.secondPlayerShips = null
                    BattleShipViewModel.firstPlayerScore = 0L
                    BattleShipViewModel.secondPlayerScore = 0L
                    BattleShipViewModel.isAttackButtonEnabled = false
                    BattleShipViewModel.isReadyButtonEnabled = true
                    BattleShipViewModel.winnerId = "null"
                    BattleShipViewModel.startTime = "null"
                    BattleShipViewModel.isEnemyMoving = false
                    BattleShipViewModel.isGameOver = false
                    BattleShipViewModel.winner = null
                    BattleShipViewModel.loser = null
                    BattleShipViewModel.isRoomCreating = false
                    BattleShipViewModel.isExiting = false
                    BattleShipViewModel.isGameLoading = false
                    BattleShipViewModel.isRoomCreated = false

                    for (i in BattleShipViewModel.ships.indices) {
                        BattleShipViewModel.ships[i].value = false
                    }

                    for (i in BattleShipViewModel.isVerticalShips.indices) {
                        BattleShipViewModel.isVerticalShips[i].value = false
                    }
                }
            } else {
                time = 0
            }

            if (BattleShipViewModel.isPlayerWaiting) {
                time2 += 1

                if (time2 >= 5) {
                    stopCheckConnection()

                    BattleShipViewModel.isPlayerConnected = false
                    BattleShipViewModel.isPlayerWaiting = false
                    BattleShipViewModel.roomId = ""
                    BattleShipViewModel.firstPlayerId = null
                    BattleShipViewModel.secondPlayerId = null
                    BattleShipViewModel.enemy = null
                    BattleShipViewModel.firstPlayerScore = 0L
                    BattleShipViewModel.secondPlayerScore = 0L
                    BattleShipViewModel.isAttackButtonEnabled = false
                    BattleShipViewModel.isReadyButtonEnabled = true
                    BattleShipViewModel.winnerId = "null"
                    BattleShipViewModel.startTime = "null"
                    BattleShipViewModel.isEnemyMoving = false
                    BattleShipViewModel.isGameOver = false
                    BattleShipViewModel.winner = null
                    BattleShipViewModel.loser = null
                    BattleShipViewModel.isRoomCreating = false
                    BattleShipViewModel.isExiting = false
                    BattleShipViewModel.isGameLoading = false
                    BattleShipViewModel.isRoomCreated = false

                    for (i in BattleShipViewModel.ships.indices) {
                        BattleShipViewModel.ships[i].value = false
                    }

                    for (i in BattleShipViewModel.isVerticalShips.indices) {
                        BattleShipViewModel.isVerticalShips[i].value = false
                    }
                }
            } else {
                time2 = 0
            }
        }
    }

    private fun stopCheckConnection() {
        if (this@MainActivity::connectionTimer.isInitialized) {
            connectionTimer.cancel()
        }

        if (this@MainActivity::writeTimer.isInitialized) {
            writeTimer.cancel()
        }

        if (this@MainActivity::readTimer.isInitialized) {
            readTimer.cancel()
        }
    }
}

