package com.example.lisaapp.sub

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.JSch
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.util.Properties

class SSHConnect (private val sshViewModel: SSHViewModel) {

    private val disconnectSignal = CompletableDeferred<Unit>()

    // Access credentials from ViewModel
    private var ip: String = sshViewModel.ipAddress.value.orEmpty()
    private var user: String = sshViewModel.username.value.orEmpty()
    private var pass: String = sshViewModel.password.value.orEmpty()

    suspend fun connectSSH(command: String, connectionType: String): String {
        return withContext(Dispatchers.IO) {

            if (ip.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                ip = sshViewModel.ipAddressInit
                user = sshViewModel.usernameInit
                pass = sshViewModel.passwordInit
                return@withContext "Error: Empty credentials"
            }

            try {
                // SSH connection parameters
                val username = user
                val password = pass
                val hostname = ip
                val port = 22

                // Create JSch instance
                val jsch = JSch()

                // Configure session
                val session = jsch.getSession(username, hostname, port)
                session.setPassword(password)
                val config = Properties()
                config["StrictHostKeyChecking"] = "no"
                session.setConfig(config)

                // Connect to SSH server
                session.connect(100)

                // Execute a command over SSH
                //val command = "ls -l"
                executeCommand(session, command, connectionType)

                // Disconnect SSH session
                session.disconnect()


                true.toString()
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
        }
    }

    private suspend fun executeCommand(session: com.jcraft.jsch.Session, command: String, connectionType: String) {
        withContext(Dispatchers.IO) {
            if (connectionType == "shell") {
                val channel = session.openChannel("shell") as ChannelShell
                val outputStream = channel.outputStream
                val inputStream = channel.inputStream
                val writer = outputStream.bufferedWriter()
                val reader = BufferedReader(InputStreamReader(inputStream))
                try {
                    channel.connect()

                    // Send command
                    writer.write("$command\n")
                    writer.flush()

                    // Read output
                    while (!disconnectSignal.isCompleted) {
                        val line = reader.readLine()
                        if (line != null) {
                            println("SSH Output: $line")
                        } else {
                            break // Break the loop when no more output is available
                        }
                        delay(100) // Adjust delay as needed
                    }
                } catch (e: Exception) {
                    println("Error executing command: ${e.message}")
                } finally {
                    writer.close()
                    outputStream.close()
                    inputStream.close()
                    reader.close()
                    channel.disconnect()
                }
            } else if (connectionType == "exec") {
                val outputStream = ByteArrayOutputStream()
                try {
                    val channel = session.openChannel("exec") as ChannelExec
                    channel.setCommand(command)
                    channel.outputStream = outputStream
                    channel.connect()
                    while (!channel.isClosed) {
                        Thread.sleep(100)
                    }
                    channel.disconnect()
                } catch (e: Exception) {
                    "Error executing command: ${e.message}"
                }
                outputStream.toString()
            } else {
                "Invalid connection type"
            }
        }
    }

    fun disconnectSession() {
        try {
            // SSH connection parameters
            val username = user
            val password = pass
            val hostname = ip
            val port = 22

            // Create JSch instance
            val jsch = JSch()

            // Configure session
            val session = jsch.getSession(username, hostname, port)
            session.setPassword(password)
            val config = Properties()
            config["StrictHostKeyChecking"] = "no"
            session.setConfig(config)

            // Disconnect SSH session
            session.disconnect()


            true.toString()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }


}