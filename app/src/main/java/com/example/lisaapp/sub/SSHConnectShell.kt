package com.example.lisaapp.sub

import com.example.lisaapp.ConnectionCredentialsSSH
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

class SSHConnectShell {

    private val disconnectSignal = CompletableDeferred<Unit>()
    suspend fun connectSSH(command: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // SSH connection parameters
                val username = ConnectionCredentialsSSH().usernameSSH
                val password = ConnectionCredentialsSSH().passwordSSH
                val hostname = ConnectionCredentialsSSH().ipAddressSSH
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
                session.connect(3000)

                // Execute a command over SSH
                //val command = "ls -l"
                executeCommand(session, command)

                // Disconnect SSH session
                session.disconnect()


                true.toString()
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
        }
    }

    private suspend fun executeCommand(session: com.jcraft.jsch.Session, command: String) {
        withContext(Dispatchers.IO) {
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
                    delay(10) // Adjust delay as needed
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
        }
    }


}