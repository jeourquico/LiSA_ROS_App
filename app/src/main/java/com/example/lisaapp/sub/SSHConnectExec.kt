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

class SSHConnectExec {


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


    private suspend fun executeCommand(session: com.jcraft.jsch.Session, command: String): String {
        return withContext(Dispatchers.IO) {
            val outputStream = ByteArrayOutputStream()
            try {
                val channel = session.openChannel("exec") as ChannelExec
                channel.setCommand(command)
                channel.outputStream = outputStream
                channel.connect()
                while (!channel.isClosed) {
                    Thread.sleep(1000)
                }
                channel.disconnect()
            } catch (e: Exception) {
                "Error executing command: ${e.message}"
            }
            outputStream.toString()
        }
    }

}
