package com.example.battleship.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.battleship.R
import com.example.battleship.ui.theme.BottomBoxShape
import com.example.battleship.ui.theme.LightTextColor
import com.example.battleship.ui.theme.PrimaryColor
import com.example.battleship.viewModels.BattleShipViewModel

@Composable
fun LoginScreen(
    viewModel: BattleShipViewModel,
    navController: NavController
) {
    if (BattleShipViewModel.isLoading) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(text = "Info")
            },
            text = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Loading...")
                }
            },
            buttons = {
            }
        )
    }

    Box(contentAlignment = Alignment.TopCenter) {
        Image(
            painter = painterResource(id = R.drawable.login_background), contentDescription = "",
            modifier = Modifier.fillMaxWidth()
        )
    }
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "WELCOME TO BATTLE SHIP",
                fontSize = 28.sp,
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { viewModel.signInWithGoogle() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.background
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(text = "Continue with Google", color = PrimaryColor, fontSize = 16.sp)
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp,
                shape = BottomBoxShape.medium
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Log In with Email",
                        color = LightTextColor,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    OutlinedTextField(
                        value = viewModel.email, onValueChange = {
                            viewModel.email = it
                        },
                        label = {
                            Text(text = "Email Address", color = PrimaryColor)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = PrimaryColor,
                            textColor = PrimaryColor

                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType =
                            KeyboardType.Email
                        ),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_mail_24),
                                contentDescription = "",
                                tint = PrimaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                    OutlinedTextField(
                        value = viewModel.password, onValueChange = {
                            viewModel.password = it
                        },
                        label = {
                            Text(text = "Password", color = PrimaryColor)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = PrimaryColor,
                            textColor = PrimaryColor
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        visualTransformation = if (!viewModel.isPasswordVisible) {
                            PasswordVisualTransformation()
                        } else {
                            VisualTransformation.None
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_lock_24),
                                contentDescription = "",
                                tint = PrimaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                viewModel.isPasswordVisible = !viewModel.isPasswordVisible
                            }) {
                                if (!viewModel.isPasswordVisible) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_baseline_visibility_off_24),
                                        contentDescription = "",
                                        tint = PrimaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_baseline_visibility_24),
                                        contentDescription = "",
                                        tint = PrimaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    )
                    Button(
                        onClick = {
                            BattleShipViewModel.isLoading = true
                            viewModel.signInWithEmailAndPassword(
                                viewModel.email,
                                viewModel.password
                            )
                            viewModel.clearLoginFields()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 20.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = PrimaryColor,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        enabled = viewModel.email.isNotBlank() && viewModel.password.isNotBlank()
                    ) {
                        Text(text = "Log In")
                    }
                    TextButton(
                        onClick = { navController.navigate("signup") },
                        contentPadding = PaddingValues(vertical = 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Don't have an Account? Sign Up",
                            color = LightTextColor,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
