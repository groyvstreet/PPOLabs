package com.example.battleship.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.battleship.models.Ship
import com.example.battleship.viewModels.BattleShipViewModel

@Composable
fun Horizontal4(index: Int) {
    DragTarget(
        modifier = Modifier,
        dataToDrop = Ship(
            listOf(
                listOf(),
                listOf(),
                listOf(),
                listOf()
            )
        ),
        index = index
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
        }
    }
}

@Composable
fun Vertical4(index: Int) {
    DragTarget(
        modifier = Modifier,
        dataToDrop = Ship(
            listOf(
                listOf(),
                listOf(),
                listOf(),
                listOf()
            )
        ),
        index = index
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = false },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = false },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = false },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = false },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
        }
    }
}

@Composable
fun Horizontal3(index: Int) {
    DragTarget(
        modifier = Modifier,
        dataToDrop = Ship(
            listOf(
                listOf(),
                listOf(),
                listOf()
            )
        ),
        index = index
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
        }
    }
}

@Composable
fun Vertical3(index: Int) {
    DragTarget(
        modifier = Modifier,
        dataToDrop = Ship(
            listOf(
                listOf(),
                listOf(),
                listOf()
            )
        ),
        index = index
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = false },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = false },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = false },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
        }
    }
}

@Composable
fun Horizontal2(index: Int) {
    DragTarget(
        modifier = Modifier,
        dataToDrop = Ship(
            listOf(
                listOf(),
                listOf()
            )
        ),
        index = index
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
        }
    }
}

@Composable
fun Vertical2(index: Int) {
    DragTarget(
        modifier = Modifier,
        dataToDrop = Ship(
            listOf(
                listOf(),
                listOf()
            )
        ),
        index = index
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = false },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
            Button(
                onClick = { BattleShipViewModel.isVerticalShips[index].value = false },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
        }
    }
}

@Composable
fun Horizontal1(index: Int) {
    DragTarget(
        modifier = Modifier,
        dataToDrop = Ship(
            listOf(
                listOf()
            )
        ),
        index = index
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                ),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(30.dp)
            ) {}
        }
    }
}
