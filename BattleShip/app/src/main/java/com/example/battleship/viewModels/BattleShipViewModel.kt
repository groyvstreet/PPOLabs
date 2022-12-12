package com.example.battleship.viewModels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.battleship.models.Game
import com.example.battleship.models.Ship
import com.example.battleship.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.math.BigInteger
import java.security.MessageDigest

class BattleShipViewModel : ViewModel() {

    var email by mutableStateOf("")

    var password by mutableStateOf("")

    var isPasswordVisible by mutableStateOf(false)

    var isProfileLoading by mutableStateOf(false)

    var signupNickname by mutableStateOf("")

    var signupEmail by mutableStateOf("")

    var signupPassword by mutableStateOf("")

    var selectedPoint by mutableStateOf<List<Int>>(listOf())

    lateinit var signInWithGoogle: () -> Unit

    lateinit var signOut: () -> Unit

    lateinit var selectImg: () -> Unit

    lateinit var updateProfile: () -> Unit

    lateinit var signUpWithEmailAndPassword: (String, String, String) -> Unit

    lateinit var signInWithEmailAndPassword: (String, String) -> Unit

    lateinit var createRoom: () -> Unit

    lateinit var enterRoom: suspend () -> Unit

    lateinit var attackEnemy: (Int, Int) -> Unit

    lateinit var leaveRoom: () -> Unit

    lateinit var loadGame: (String, String) -> Unit

    lateinit var deleteRoom: () -> Unit

    lateinit var addShip: (Ship, Int) -> Unit

    lateinit var dropShip: (Int, Int) -> Unit

    lateinit var setReady: () -> Unit

    lateinit var load: (BattleShipViewModel) -> Unit

    companion object {
        var currentUser by mutableStateOf(Firebase.auth.currentUser)
        var isAuth by mutableStateOf(false)
        var isProfileUpdating by mutableStateOf(false)
        var email by mutableStateOf("email")
        var oldSelectedImg by mutableStateOf<Uri?>(null)
        var oldNickname by mutableStateOf("player")
        var oldIsGravatar by mutableStateOf(false)
        var selectedImg by mutableStateOf<Uri?>(null)
        var nickname by mutableStateOf("player")
        var isGravatar by mutableStateOf(false)
        var games by mutableStateOf<List<Game>>(listOf())
        var isGameLoading by mutableStateOf(false)
        var isLoading by mutableStateOf(false)
        var isAppLoaded by mutableStateOf(false)
        var isPlayerWaiting by mutableStateOf(false)
        var isConnected by mutableStateOf(true)
        var isRoomCreating by mutableStateOf(false)
        var isExiting by mutableStateOf(false)

        var isRoomCreated by mutableStateOf(false)
        var isPlayerConnected by mutableStateOf(false)
        var roomId by mutableStateOf("")
        var firstPlayerId by mutableStateOf<String?>(null)
        var secondPlayerId by mutableStateOf<String?>(null)
        var enemy by mutableStateOf<User?>(null)
        var firstPlayerMap by mutableStateOf<List<MutableList<Int>>?>(null)
        var secondPlayerMap by mutableStateOf<List<MutableList<Int>>?>(null)
        var firstPlayerShips by mutableStateOf<MutableList<Ship>?>(null)
        var secondPlayerShips by mutableStateOf<MutableList<Ship>?>(null)
        var firstPlayerScore by mutableStateOf(0L)
        var secondPlayerScore by mutableStateOf(0L)
        var coefficient by mutableStateOf(10L)
        var isFirstPlayerMoving by mutableStateOf(false)
        var isSecondPlayerMoving by mutableStateOf(false)
        var isFirstPlayerReady by mutableStateOf(false)
        var isSecondPlayerReady by mutableStateOf(false)
        var isAttackButtonEnabled by mutableStateOf(false)
        var isReadyButtonEnabled by mutableStateOf(true)
        var winnerId by mutableStateOf("null")
        var startTime by mutableStateOf("null")

        fun updateAttackButton() {
            isAttackButtonEnabled = (currentUser!!.uid == firstPlayerId && isFirstPlayerMoving) ||
                    (currentUser!!.uid == secondPlayerId && isSecondPlayerMoving)
        }

        var counter by mutableStateOf(1)
        var isEnemyMoving by mutableStateOf(false)

        fun updateEnemyMoving() {
            isEnemyMoving = !((currentUser!!.uid == firstPlayerId && isFirstPlayerMoving) ||
                    (currentUser!!.uid == secondPlayerId && isSecondPlayerMoving))
        }

        var isGameOver by mutableStateOf(false)

        var winner by mutableStateOf<User?>(null)
        var loser by mutableStateOf<User?>(null)

        var ships by mutableStateOf(mutableListOf(
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false)
        ))

        var isVerticalShips by mutableStateOf(mutableListOf(
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false)
        ))
    }

    fun clearLoginFields() {
        email = ""
        password = ""
    }

    fun clearSignupFields() {
        signupNickname = ""
        signupEmail = ""
        signupPassword = ""
    }

    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    fun getEnemyShipPointColor(x: Int, y: Int): Color {
        val map: List<List<Int>>
        val ships: List<Ship>

        if (currentUser!!.uid == firstPlayerId) {
            map = secondPlayerMap!!
            ships = secondPlayerShips!!
        } else {
            map = firstPlayerMap!!
            ships = firstPlayerShips!!
        }

        return if (map[x][y] == -1) {
            Color.Gray
        } else if (map[x][y] == 0) {
            if (selectedPoint.isNotEmpty() && selectedPoint[0] == x && selectedPoint[1] == y) {
                Color.White
            } else {
                Color.Cyan
            }
        } else {
            var ship = Ship(listOf())

            for (elem in ships) {
                for (coordinate in elem.coordinates) {
                    if (coordinate[0] == x && coordinate[1] == y) {
                        ship = elem
                        break
                    }
                }
            }

            if (ship.isDestroyed) {
                Color.Red
            } else {
                Color.Yellow
            }
        }
    }

    fun getShipPointColor(x: Int, y: Int): Color {
        val map: List<List<Int>>
        val ships: List<Ship>

        if (currentUser!!.uid == firstPlayerId) {
            map = firstPlayerMap!!
            ships = firstPlayerShips!!
        } else {
            map = secondPlayerMap!!
            ships = secondPlayerShips!!
        }

        return if (map[x][y] == -1) {
            Color.Gray
        } else if (map[x][y] == 0) {
            var ship = Ship(listOf())

            for (elem in ships) {
                for (coordinate in elem.coordinates) {
                    if (coordinate[0] == x && coordinate[1] == y) {
                        ship = elem
                        break
                    }
                }
            }

            if (ship.coordinates.isEmpty()) {
                Color.Cyan
            } else {
                Color.Green
            }
        } else {
            var ship = Ship(listOf())

            for (elem in ships) {
                for (coordinate in elem.coordinates) {
                    if (coordinate[0] == x && coordinate[1] == y) {
                        ship = elem
                        break
                    }
                }
            }

            if (ship.isDestroyed) {
                Color.Red
            } else {
                Color.Yellow
            }
        }
    }

    fun getShipPointColor(x: Int, y: Int, map: List<MutableList<Int>>, ships: List<Ship>): Color {
        return if (map[x][y] == -1) {
            Color.Gray
        } else if (map[x][y] == 0) {
            var ship = Ship(listOf())

            for (elem in ships) {
                for (coordinate in elem.coordinates) {
                    if (coordinate[0] == x && coordinate[1] == y) {
                        ship = elem
                        break
                    }
                }
            }

            if (ship.coordinates.isEmpty()) {
                Color.Cyan
            } else {
                Color.Green
            }
        } else {
            var ship = Ship(listOf())

            for (elem in ships) {
                for (coordinate in elem.coordinates) {
                    if (coordinate[0] == x && coordinate[1] == y) {
                        ship = elem
                        break
                    }
                }
            }

            if (ship.isDestroyed) {
                Color.Red
            } else {
                Color.Yellow
            }
        }
    }
}
