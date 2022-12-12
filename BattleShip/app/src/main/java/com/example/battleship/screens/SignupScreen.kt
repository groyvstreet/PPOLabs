package com.example.battleship.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.battleship.R
import com.example.battleship.ui.theme.*
import com.example.battleship.viewModels.BattleShipViewModel

@Composable
fun SignupScreen(
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 20.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "",
            modifier = Modifier.size(160.dp)
        )
        Text(
            text = "CREATE YOUR ACCOUNT",
            textAlign = TextAlign.Center,
            color = SecondaryColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
        )
        TextField(
            value = viewModel.signupNickname, onValueChange = { viewModel.signupNickname = it },
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),
            colors = TextFieldDefaults.textFieldColors(
                textColor = PrimaryColor,
                backgroundColor = MaterialTheme.colors.background,
                cursorColor = PrimaryColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = InputBoxShape.medium,
            singleLine = true,
            leadingIcon = {
                Row(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_person_24),
                        contentDescription = "",
                        tint = PrimaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .width(6.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(BackgroundColor)
                    )
                }
            },
            placeholder = {
                Text(text = "Nickname", color = PlaceholderColor)
            },
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        TextField(
            value = viewModel.signupEmail, onValueChange = { viewModel.signupEmail = it },
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),
            colors = TextFieldDefaults.textFieldColors(
                textColor = PrimaryColor,
                backgroundColor = MaterialTheme.colors.background,
                cursorColor = PrimaryColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = InputBoxShape.medium,
            singleLine = true,
            leadingIcon = {
                Row(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_mail_24),
                        contentDescription = "",
                        tint = PrimaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .width(6.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(BackgroundColor)
                    )
                }
            },
            placeholder = {
                Text(text = "Email Address", color = PlaceholderColor)
            },
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        TextField(
            value = viewModel.signupPassword, onValueChange = { viewModel.signupPassword = it },
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),
            colors = TextFieldDefaults.textFieldColors(
                textColor = PrimaryColor,
                backgroundColor = MaterialTheme.colors.background,
                cursorColor = PrimaryColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = InputBoxShape.medium,
            singleLine = true,
            leadingIcon = {
                Row(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_lock_24),
                        contentDescription = "",
                        tint = PrimaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .width(6.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(BackgroundColor)
                    )
                }
            },
            placeholder = {
                Text(text = "Password", color = PlaceholderColor)
            },
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Button(
            onClick = {
                viewModel.signUpWithEmailAndPassword(
                    viewModel.signupNickname,
                    viewModel.signupEmail,
                    viewModel.signupPassword
                )
                viewModel.clearSignupFields()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = PrimaryColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 2.dp
            ),
            shape = Shapes.medium,
            enabled = viewModel.signupNickname.isNotBlank() && viewModel.signupEmail.isNotBlank() &&
                    viewModel.signupPassword.isNotBlank()
        ) {
            Text(
                text = "Sign Up",
                color = SecondaryColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        SocialMediaSignInButtons(viewModel = viewModel)
        TextButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Text(
                text = "Already have an account? Sign In",
                color = SecondaryColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun SocialMediaSignInButtons(viewModel: BattleShipViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                BattleShipViewModel.isLoading = true
                viewModel.signInWithGoogle()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.background,
            ),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
            modifier = Modifier.clip(shape = Shapes.large),
            contentPadding = PaddingValues(horizontal = 26.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Google",
                    color = SecondaryColor
                )
            }
        }
    }
}
