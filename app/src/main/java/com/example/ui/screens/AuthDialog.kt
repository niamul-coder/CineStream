package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineStreamViewModel

@Composable
fun AuthDialog(
    viewModel: CineStreamViewModel,
    onDismiss: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var isForgotPasswordMode by remember { mutableStateOf(false) }

    var userIdOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var registrationEmail by remember { mutableStateOf("") }
    var chosenUserId by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successNotice by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            isForgotPasswordMode -> "Reset Password"
                            isRegisterMode -> "Create CineStream Account"
                            else -> "Sign In to CineStream"
                        },
                        color = CineTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CineTextMuted)
                    }
                }
                Text(
                    text = "Personalized 2026 cinema streaming & offline downloads",
                    color = CineTextSecondary,
                    fontSize = 11.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CineRed.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CineRed)
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = CineRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                if (successNotice != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CineGreen.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CineGreen)
                    ) {
                        Text(
                            text = successNotice!!,
                            color = CineGreen,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                if (isForgotPasswordMode) {
                    OutlinedTextField(
                        value = userIdOrEmail,
                        onValueChange = { userIdOrEmail = it },
                        label = { Text("Your Email or User ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (userIdOrEmail.isNotBlank()) {
                                successNotice = "Password reset instructions dispatched to your verified address."
                                errorMessage = null
                            } else {
                                errorMessage = "Please enter your registered User ID or Email"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CineCyan, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Recovery Link", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "Return to Sign In",
                        color = CineCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                isForgotPasswordMode = false
                                errorMessage = null
                            }
                    )
                } else if (isRegisterMode) {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = chosenUserId,
                        onValueChange = { chosenUserId = it },
                        label = { Text("Unique User ID (e.g. neo_2026)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = registrationEmail,
                        onValueChange = { registrationEmail = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (chosenUserId.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill in all required registration fields"
                            } else {
                                val result = viewModel.register(displayName, chosenUserId, registrationEmail, password)
                                if (result.isSuccess) {
                                    onDismiss()
                                } else {
                                    errorMessage = result.exceptionOrNull()?.message ?: "Registration error"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CineCyan, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Register Account", fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Already have an account? ", color = CineTextSecondary, fontSize = 12.sp)
                        Text(
                            "Sign In",
                            color = CineCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                isRegisterMode = false
                                errorMessage = null
                            }
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = userIdOrEmail,
                        onValueChange = { userIdOrEmail = it },
                        label = { Text("User ID or Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("auth_userid_input")
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("auth_password_input")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Forgot Password?",
                            color = CineCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                isForgotPasswordMode = true
                                errorMessage = null
                            }
                        )
                    }
                    Button(
                        onClick = {
                            if (userIdOrEmail.isBlank()) {
                                errorMessage = "Please enter your User ID or email"
                            } else {
                                val res = viewModel.signIn(userIdOrEmail, password)
                                if (res.isSuccess) {
                                    onDismiss()
                                } else {
                                    errorMessage = "Invalid credentials. Try again or select a demo account below."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CineCyan, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth().testTag("auth_submit_button")
                    ) {
                        Text("Sign In", fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Need a new account? ", color = CineTextSecondary, fontSize = 12.sp)
                        Text(
                            "Register",
                            color = CineCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                isRegisterMode = true
                                errorMessage = null
                            }
                        )
                    }
                }

                HorizontalDivider(color = CineBorder, modifier = Modifier.padding(vertical = 4.dp))

                // Quick One-Click Demo Profiles
                Text(
                    text = "One-Click Instant Demo Login:",
                    color = CineTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    viewModel.repository.registeredUsers.forEach { user ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CineSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.switchAccount(user)
                                    onDismiss()
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(user.avatarEmoji, fontSize = 16.sp)
                                Text(
                                    text = user.displayName.split(" ").first(),
                                    color = CineTextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = CineSurfaceHigh
    )
}
